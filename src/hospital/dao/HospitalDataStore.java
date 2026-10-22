package hospital.dao;

import hospital.model.Appointment;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.TimeSlot;

import java.util.List;

/**
 * Interface defining the persistence contracts for the Hospital Appointment System.
 */
public interface HospitalDataStore {
    List<Doctor> getAllDoctors();
    List<Doctor> getDoctorsByDepartment(Department department);
    Doctor getDoctorById(String doctorId);
    
    List<TimeSlot> getAvailableSlots(String doctorId);
    
    void savePatient(Patient patient);
    Patient getPatientById(String patientId);
    Patient findPatientByPhone(String phone);

    void saveAppointment(Appointment appointment);
    void updateAppointment(Appointment appointment);
    Appointment getAppointmentById(String appointmentId);
    List<Appointment> getAllAppointments();
    List<Appointment> getAppointmentsByDoctor(String doctorId);
    List<Appointment> getAppointmentsByPatient(String patientId);
}
