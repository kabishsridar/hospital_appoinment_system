package hospital.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appointment record linking Patient, Doctor, and TimeSlot.
 */
public class Appointment {
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private TimeSlot slot;
    private LocalDateTime bookedAt;
    private AppointmentStatus status;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    public Appointment(String appointmentId, Patient patient, Doctor doctor, TimeSlot slot) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.slot = slot;
        this.bookedAt = LocalDateTime.now();
        this.status = AppointmentStatus.CONFIRMED;
    }

    public Appointment(String appointmentId, Patient patient, Doctor doctor, TimeSlot slot, LocalDateTime bookedAt, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.slot = slot;
        this.bookedAt = bookedAt;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String generateAppointmentSlip() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         SRM TRICHY HOSPITAL - APPOINTMENT SLIP     \n");
        sb.append("====================================================\n");
        sb.append(String.format("Appointment ID : %s\n", appointmentId));
        sb.append(String.format("Status         : %s\n", status));
        sb.append(String.format("Booked On      : %s\n", bookedAt.format(FMT)));
        sb.append("----------------------------------------------------\n");
        sb.append("PATIENT DETAILS:\n");
        sb.append(String.format("  Name   : %s\n", patient.getName()));
        sb.append(String.format("  Age    : %d | Gender: %s\n", patient.getAge(), patient.getGender()));
        sb.append(String.format("  Phone  : %s\n", patient.getPhone()));
        sb.append("----------------------------------------------------\n");
        sb.append("DOCTOR DETAILS:\n");
        sb.append(String.format("  Doctor : Dr. %s (%s)\n", doctor.getName(), doctor.getQualification()));
        sb.append(String.format("  Dept   : %s\n", doctor.getDepartment().getDisplayName()));
        sb.append(String.format("  Room   : %s\n", doctor.getRoomNumber()));
        sb.append(String.format("  Fee    : Rs. %.2f\n", doctor.getConsultationFee()));
        sb.append("----------------------------------------------------\n");
        sb.append("SCHEDULED TIME:\n");
        sb.append(String.format("  Date   : %s\n", slot.getFormattedDate()));
        sb.append(String.format("  Time   : %s\n", slot.getFormattedTime()));
        sb.append("====================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient: %s | Dr. %s (%s) | %s %s | Status: %s",
                appointmentId, patient.getName(), doctor.getName(), doctor.getDepartment().getDisplayName(),
                slot.getFormattedDate(), slot.getFormattedTime(), status);
    }
}
