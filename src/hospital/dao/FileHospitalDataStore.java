package hospital.dao;

import hospital.model.Appointment;
import hospital.model.AppointmentStatus;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.TimeSlot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File-backed persistence implementation with automatic seed data from PPTX slides.
 */
public class FileHospitalDataStore implements HospitalDataStore {
    private final Map<String, Doctor> doctors = new LinkedHashMap<>();
    private final Map<String, Patient> patients = new LinkedHashMap<>();
    private final Map<String, Appointment> appointments = new LinkedHashMap<>();
    private final File dataFile;

    public FileHospitalDataStore() {
        this(new File("data", "appointments.csv"));
    }

    public FileHospitalDataStore(File dataFile) {
        this.dataFile = dataFile;
        seedDoctorsAndSlots();
        loadAppointmentsFromFile();
    }

    private void seedDoctorsAndSlots() {
        LocalDate today = LocalDate.now();

        // 1. Cardiology (Matching Slide 1: Available today 10:30)
        Doctor docCardio = new Doctor("DOC-101", "Dr. Rajesh Sharma", "9876543210", "r.sharma@hospital.org",
                Department.CARDIOLOGY, "MD, DM (Cardiology)", 800.0, "Room 201 (Block A)");
        docCardio.addSlot(new TimeSlot("SLOT-C1", today, LocalTime.of(10, 30)));
        docCardio.addSlot(new TimeSlot("SLOT-C2", today, LocalTime.of(11, 30)));
        docCardio.addSlot(new TimeSlot("SLOT-C3", today, LocalTime.of(16, 0)));
        docCardio.addSlot(new TimeSlot("SLOT-C4", today.plusDays(1), LocalTime.of(10, 30)));
        doctors.put(docCardio.getId(), docCardio);

        // 2. Orthopedics (Matching Slide 1: Available today 11:15)
        Doctor docOrtho = new Doctor("DOC-102", "Dr. Priya Sundaram", "9876543211", "p.sundaram@hospital.org",
                Department.ORTHOPEDICS, "MS (Ortho), DNB, MCh", 750.0, "Room 105 (Block B)");
        docOrtho.addSlot(new TimeSlot("SLOT-O1", today, LocalTime.of(11, 15)));
        docOrtho.addSlot(new TimeSlot("SLOT-O2", today, LocalTime.of(14, 0)));
        docOrtho.addSlot(new TimeSlot("SLOT-O3", today, LocalTime.of(17, 30)));
        docOrtho.addSlot(new TimeSlot("SLOT-O4", today.plusDays(1), LocalTime.of(11, 15)));
        doctors.put(docOrtho.getId(), docOrtho);

        // 3. Pediatrics (Matching Slide 1: Available today 12:00)
        Doctor docPedia = new Doctor("DOC-103", "Dr. Arvind Menon", "9876543212", "a.menon@hospital.org",
                Department.PEDIATRICS, "MD (Pediatrics), DCH", 600.0, "Room 110 (Child Care)");
        docPedia.addSlot(new TimeSlot("SLOT-P1", today, LocalTime.of(12, 0)));
        docPedia.addSlot(new TimeSlot("SLOT-P2", today, LocalTime.of(15, 30)));
        docPedia.addSlot(new TimeSlot("SLOT-P3", today, LocalTime.of(18, 0)));
        docPedia.addSlot(new TimeSlot("SLOT-P4", today.plusDays(1), LocalTime.of(12, 0)));
        doctors.put(docPedia.getId(), docPedia);

        // 4. General Medicine
        Doctor docGen = new Doctor("DOC-104", "Dr. Ananya Rao", "9876543213", "a.rao@hospital.org",
                Department.GENERAL_MEDICINE, "MBBS, MD (Internal Med)", 500.0, "Room 101 (OPD)");
        docGen.addSlot(new TimeSlot("SLOT-G1", today, LocalTime.of(9, 30)));
        docGen.addSlot(new TimeSlot("SLOT-G2", today, LocalTime.of(10, 0)));
        docGen.addSlot(new TimeSlot("SLOT-G3", today, LocalTime.of(16, 30)));
        doctors.put(docGen.getId(), docGen);

        // 5. Neurology
        Doctor docNeuro = new Doctor("DOC-105", "Dr. Vikram Seth", "9876543214", "v.seth@hospital.org",
                Department.NEUROLOGY, "DM (Neurology), Fellowship AIIMS", 900.0, "Room 304 (Block C)");
        docNeuro.addSlot(new TimeSlot("SLOT-N1", today, LocalTime.of(11, 0)));
        docNeuro.addSlot(new TimeSlot("SLOT-N2", today, LocalTime.of(15, 0)));
        doctors.put(docNeuro.getId(), docNeuro);
    }

    private synchronized void loadAppointmentsFromFile() {
        if (!dataFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: aptId,patId,patName,patPhone,patEmail,patAge,patGender,docId,slotId,bookedAtIso,status
                String[] parts = line.split(",", -1);
                if (parts.length >= 11) {
                    String aptId = parts[0];
                    String patId = parts[1];
                    String patName = parts[2];
                    String patPhone = parts[3];
                    String patEmail = parts[4];
                    int patAge = Integer.parseInt(parts[5]);
                    String patGender = parts[6];
                    String docId = parts[7];
                    String slotId = parts[8];
                    LocalDateTime bookedAt = LocalDateTime.parse(parts[9]);
                    AppointmentStatus status = AppointmentStatus.valueOf(parts[10]);

                    Patient patient = patients.computeIfAbsent(patId,
                            k -> new Patient(patId, patName, patPhone, patEmail, patAge, patGender));

                    Doctor doc = doctors.get(docId);
                    if (doc != null) {
                        TimeSlot slot = doc.findSlotById(slotId);
                        if (slot != null) {
                            if (status == AppointmentStatus.CONFIRMED) {
                                slot.setBooked(true);
                                slot.setBookedAppointmentId(aptId);
                            }
                            Appointment apt = new Appointment(aptId, patient, doc, slot, bookedAt, status);
                            appointments.put(aptId, apt);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not fully restore appointments from file: " + e.getMessage());
        }
    }

    private synchronized void persistAppointmentsToFile() {
        File parent = dataFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write("#aptId,patId,patName,patPhone,patEmail,patAge,patGender,docId,slotId,bookedAtIso,status\n");
            for (Appointment a : appointments.values()) {
                Patient p = a.getPatient();
                String line = String.join(",",
                        a.getAppointmentId(),
                        p.getId(),
                        escapeCsv(p.getName()),
                        escapeCsv(p.getPhone()),
                        escapeCsv(p.getEmail()),
                        String.valueOf(p.getAge()),
                        escapeCsv(p.getGender()),
                        a.getDoctor().getId(),
                        a.getSlot().getSlotId(),
                        a.getBookedAt().toString(),
                        a.getStatus().name()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments to file: " + e.getMessage());
        }
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace(",", " ").trim();
    }

    @Override
    public synchronized List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors.values());
    }

    @Override
    public synchronized List<Doctor> getDoctorsByDepartment(Department department) {
        List<Doctor> result = new ArrayList<>();
        for (Doctor d : doctors.values()) {
            if (d.getDepartment() == department) {
                result.add(d);
            }
        }
        return result;
    }

    @Override
    public synchronized Doctor getDoctorById(String doctorId) {
        return doctors.get(doctorId);
    }

    @Override
    public synchronized List<TimeSlot> getAvailableSlots(String doctorId) {
        Doctor doc = doctors.get(doctorId);
        if (doc != null) {
            return doc.getAvailableSlots();
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized void savePatient(Patient patient) {
        patients.put(patient.getId(), patient);
    }

    @Override
    public synchronized Patient getPatientById(String patientId) {
        return patients.get(patientId);
    }

    @Override
    public synchronized Patient findPatientByPhone(String phone) {
        for (Patient p : patients.values()) {
            if (p.getPhone().equals(phone)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public synchronized void saveAppointment(Appointment appointment) {
        savePatient(appointment.getPatient());
        appointments.put(appointment.getAppointmentId(), appointment);
        persistAppointmentsToFile();
    }

    @Override
    public synchronized void updateAppointment(Appointment appointment) {
        appointments.put(appointment.getAppointmentId(), appointment);
        persistAppointmentsToFile();
    }

    @Override
    public synchronized Appointment getAppointmentById(String appointmentId) {
        return appointments.get(appointmentId);
    }

    @Override
    public synchronized List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    @Override
    public synchronized List<Appointment> getAppointmentsByDoctor(String doctorId) {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments.values()) {
            if (a.getDoctor().getId().equalsIgnoreCase(doctorId)) {
                list.add(a);
            }
        }
        return list;
    }

    @Override
    public synchronized List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments.values()) {
            if (a.getPatient().getId().equalsIgnoreCase(patientId)) {
                list.add(a);
            }
        }
        return list;
    }
}
