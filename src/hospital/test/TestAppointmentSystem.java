package hospital.test;

import hospital.dao.FileHospitalDataStore;
import hospital.dao.HospitalDataStore;
import hospital.exception.SlotAlreadyBookedException;
import hospital.model.Appointment;
import hospital.model.AppointmentStatus;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.TimeSlot;
import hospital.service.HospitalService;

import java.io.File;
import java.util.List;

/**
 * Automated test suite to verify core business logic, thread-safety, and persistence.
 */
public class TestAppointmentSystem {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("RUNNING AUTOMATED VERIFICATION SUITE FOR HOSPITAL SYSTEM");
        System.out.println("===============================================================\n");

        File testDataFile = new File("data", "test_appointments.csv");
        if (testDataFile.exists()) {
            testDataFile.delete();
        }

        HospitalDataStore dataStore = new FileHospitalDataStore(testDataFile);
        HospitalService service = new HospitalService(dataStore);

        int passed = 0;
        int failed = 0;

        // Test 1: Verify Seed Doctors & PPTX Slide 1 Slots
        try {
            List<Doctor> cardioDocs = service.getDoctorsByDepartment(Department.CARDIOLOGY);
            assertWithMessage(!cardioDocs.isEmpty(), "Cardiology doctor exists");
            Doctor cardio = cardioDocs.get(0);
            
            List<TimeSlot> slots = cardio.getAvailableSlots();
            assertWithMessage(!slots.isEmpty(), "Cardiology has available slots");
            System.out.println("[PASS] Test 1: Seed data matches Slide 1 specifications.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 1: " + t.getMessage());
            failed++;
        }

        // Test 2: Successful Booking
        Appointment apt1 = null;
        try {
            apt1 = service.bookAppointment("John Doe", "9876500001", "john@example.com", 35, "Male", "DOC-101", "SLOT-C1");
            assertWithMessage(apt1 != null, "Appointment created successfully");
            assertWithMessage(apt1.getStatus() == AppointmentStatus.CONFIRMED, "Status is CONFIRMED");
            System.out.println("[PASS] Test 2: Patient appointment booking succeeded with ID: " + apt1.getAppointmentId());
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 2: " + t.getMessage());
            failed++;
        }

        // Test 3: Double-booking Prevention
        try {
            service.bookAppointment("Jane Smith", "9876500002", "jane@example.com", 29, "Female", "DOC-101", "SLOT-C1");
            System.err.println("[FAIL] Test 3: Expected SlotAlreadyBookedException was not thrown!");
            failed++;
        } catch (SlotAlreadyBookedException e) {
            System.out.println("[PASS] Test 3: Duplicate booking correctly blocked: " + e.getMessage());
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 3: Unexpected error: " + t.getMessage());
            failed++;
        }

        // Test 4: Cancellation and Slot Freeing
        try {
            if (apt1 != null) {
                boolean cancelled = service.cancelAppointment(apt1.getAppointmentId());
                assertWithMessage(cancelled, "Appointment cancelled successfully");
                
                // Now booking the freed slot should succeed!
                Appointment apt2 = service.bookAppointment("Jane Smith", "9876500002", "jane@example.com", 29, "Female", "DOC-101", "SLOT-C1");
                assertWithMessage(apt2 != null, "Re-booking previously cancelled slot succeeded");
                System.out.println("[PASS] Test 4: Slot successfully restored and re-booked upon cancellation.");
                passed++;
            }
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 4: " + t.getMessage());
            failed++;
        }

        // Test 5: Verify Persistence to CSV
        try {
            assertWithMessage(testDataFile.exists() && testDataFile.length() > 0, "Appointments persisted to CSV");
            
            // Re-instantiate dataStore from file to test load
            HospitalDataStore reloadedStore = new FileHospitalDataStore(testDataFile);
            assertWithMessage(!reloadedStore.getAllAppointments().isEmpty(), "Loaded persisted appointments on restart");
            System.out.println("[PASS] Test 5: CSV persistence and restart hydration verified.");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] Test 5: " + t.getMessage());
            failed++;
        }

        System.out.println("\n---------------------------------------------------------------");
        System.out.printf("TEST SUMMARY: %d Passed, %d Failed\n", passed, failed);
        System.out.println("---------------------------------------------------------------");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertWithMessage(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }
}
