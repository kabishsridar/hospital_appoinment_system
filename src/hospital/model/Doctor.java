package hospital.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Doctor class extending Person.
 */
public class Doctor extends Person {
    private Department department;
    private String qualification;
    private double consultationFee;
    private String roomNumber;
    private final List<TimeSlot> slots;

    public Doctor(String id, String name, String phone, String email, Department department, String qualification, double consultationFee, String roomNumber) {
        super(id, name, phone, email);
        this.department = department;
        this.qualification = qualification;
        this.consultationFee = consultationFee;
        this.roomNumber = roomNumber;
        this.slots = new ArrayList<>();
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public synchronized void addSlot(TimeSlot slot) {
        slots.add(slot);
        Collections.sort(slots);
    }

    public synchronized List<TimeSlot> getAllSlots() {
        return new ArrayList<>(slots);
    }

    public synchronized List<TimeSlot> getAvailableSlots() {
        List<TimeSlot> available = new ArrayList<>();
        for (TimeSlot s : slots) {
            if (!s.isBooked()) {
                available.add(s);
            }
        }
        return available;
    }

    public synchronized TimeSlot findSlotById(String slotId) {
        for (TimeSlot s : slots) {
            if (s.getSlotId().equalsIgnoreCase(slotId)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String getRoleDescription() {
        return String.format("Dr. %s (%s - %s) Room: %s", name, qualification, department.getDisplayName(), roomNumber);
    }
}
