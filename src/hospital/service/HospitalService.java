package hospital.service;

import hospital.dao.HospitalDataStore;
import hospital.exception.ResourceNotFoundException;
import hospital.exception.SlotAlreadyBookedException;
import hospital.exception.ValidationException;
import hospital.model.Appointment;
import hospital.model.AppointmentStatus;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.TimeSlot;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service orchestrating appointment bookings, cancellations, and doctor schedules.
 */
public class HospitalService {
    private final HospitalDataStore dataStore;
    private final AtomicInteger appointmentCounter = new AtomicInteger(1000);

    public HospitalService(HospitalDataStore dataStore) {
        this.dataStore = dataStore;
        // align counter above existing count
        int maxExisting = 1000;
        for (Appointment a : dataStore.getAllAppointments()) {
            try {
                String id = a.getAppointmentId().replace("APT-", "");
                int num = Integer.parseInt(id);
                if (num > maxExisting) maxExisting = num;
            } catch (Exception ignored) {}
        }
        appointmentCounter.set(maxExisting);
    }

    public List<Doctor> getAllDoctors() {
        return dataStore.getAllDoctors();
    }

    public List<Doctor> getDoctorsByDepartment(Department department) {
        return dataStore.getDoctorsByDepartment(department);
    }

    public Doctor getDoctorById(String doctorId) throws ResourceNotFoundException {
        Doctor doc = dataStore.getDoctorById(doctorId);
        if (doc == null) {
            throw new ResourceNotFoundException("Doctor with ID " + doctorId + " not found.");
        }
        return doc;
    }

    public List<TimeSlot> getAvailableSlots(String doctorId) throws ResourceNotFoundException {
        Doctor doc = getDoctorById(doctorId);
        return doc.getAvailableSlots();
    }

    /**
     * Books an appointment with thread-safety and validation.
     */
    public synchronized Appointment bookAppointment(String patientName, String phone, String email,
                                                    int age, String gender, String doctorId, String slotId)
            throws ValidationException, ResourceNotFoundException, SlotAlreadyBookedException {

        // 1. Validation
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new ValidationException("Patient name cannot be empty.");
        }
        if (phone == null || phone.trim().length() < 7) {
            throw new ValidationException("A valid contact phone number is required.");
        }
        if (age <= 0 || age > 125) {
            throw new ValidationException("Please enter a valid age between 1 and 125.");
        }

        // 2. Doctor & Slot checks
        Doctor doctor = getDoctorById(doctorId);
        TimeSlot slot = doctor.findSlotById(slotId);
        if (slot == null) {
            throw new ResourceNotFoundException("Slot ID " + slotId + " was not found for Doctor " + doctor.getName());
        }

        if (slot.isBooked()) {
            throw new SlotAlreadyBookedException("Slot at " + slot.getFormattedTime() + " is already booked! Please pick another slot.");
        }

        // 3. Find or register patient
        Patient patient = dataStore.findPatientByPhone(phone.trim());
        if (patient == null) {
            String patientId = "PAT-" + (System.currentTimeMillis() % 10000);
            patient = new Patient(patientId, patientName.trim(), phone.trim(),
                    (email != null && !email.trim().isEmpty()) ? email.trim() : "N/A", age, gender);
        } else {
            // Update details if provided
            patient.setName(patientName.trim());
            patient.setAge(age);
            patient.setGender(gender);
            if (email != null && !email.trim().isEmpty()) {
                patient.setEmail(email.trim());
            }
        }

        // 4. Create and persist appointment
        String appointmentId = "APT-" + appointmentCounter.incrementAndGet();
        slot.setBooked(true);
        slot.setBookedAppointmentId(appointmentId);

        Appointment appointment = new Appointment(appointmentId, patient, doctor, slot);
        dataStore.saveAppointment(appointment);

        return appointment;
    }

    /**
     * Cancels an appointment and restores the doctor's time slot for other patients.
     */
    public synchronized boolean cancelAppointment(String appointmentId) throws ResourceNotFoundException {
        Appointment apt = dataStore.getAppointmentById(appointmentId);
        if (apt == null) {
            throw new ResourceNotFoundException("Appointment " + appointmentId + " not found.");
        }

        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            return false;
        }

        apt.setStatus(AppointmentStatus.CANCELLED);
        TimeSlot slot = apt.getSlot();
        if (slot != null) {
            slot.setBooked(false);
            slot.setBookedAppointmentId(null);
        }

        dataStore.updateAppointment(apt);
        return true;
    }

    /**
     * Marks an appointment as completed following consultation.
     */
    public synchronized boolean completeAppointment(String appointmentId) throws ResourceNotFoundException {
        Appointment apt = dataStore.getAppointmentById(appointmentId);
        if (apt == null) {
            throw new ResourceNotFoundException("Appointment " + appointmentId + " not found.");
        }

        apt.setStatus(AppointmentStatus.COMPLETED);
        dataStore.updateAppointment(apt);
        return true;
    }

    public List<Appointment> getAllAppointments() {
        return dataStore.getAllAppointments();
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return dataStore.getAppointmentsByDoctor(doctorId);
    }

    public Appointment getAppointmentById(String appointmentId) {
        return dataStore.getAppointmentById(appointmentId);
    }
}
