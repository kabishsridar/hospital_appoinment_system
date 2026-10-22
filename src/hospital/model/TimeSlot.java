package hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a doctor's consultation time slot.
 */
public class TimeSlot implements Comparable<TimeSlot> {
    private String slotId;
    private LocalDate date;
    private LocalTime startTime;
    private boolean isBooked;
    private String bookedAppointmentId;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public TimeSlot(String slotId, LocalDate date, LocalTime startTime) {
        this.slotId = slotId;
        this.date = date;
        this.startTime = startTime;
        this.isBooked = false;
        this.bookedAppointmentId = null;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getBookedAppointmentId() {
        return bookedAppointmentId;
    }

    public void setBookedAppointmentId(String bookedAppointmentId) {
        this.bookedAppointmentId = bookedAppointmentId;
    }

    public String getFormattedTime() {
        return startTime.format(TIME_FMT);
    }

    public String getFormattedDate() {
        return date.format(DATE_FMT);
    }

    @Override
    public int compareTo(TimeSlot o) {
        int dateCmp = this.date.compareTo(o.date);
        if (dateCmp != 0) return dateCmp;
        return this.startTime.compareTo(o.startTime);
    }

    @Override
    public String toString() {
        return String.format("%s at %s (%s)", getFormattedDate(), getFormattedTime(), isBooked ? "BOOKED" : "AVAILABLE");
    }
}
