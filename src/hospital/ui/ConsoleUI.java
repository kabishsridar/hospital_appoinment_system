package hospital.ui;

import hospital.exception.ResourceNotFoundException;
import hospital.exception.SlotAlreadyBookedException;
import hospital.exception.ValidationException;
import hospital.model.Appointment;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.TimeSlot;
import hospital.service.HospitalService;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Terminal-based UI for Hospital Appointment System.
 */
public class ConsoleUI {
    private final HospitalService service;
    private final Scanner scanner;

    public ConsoleUI(HospitalService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader();
            System.out.println("1. Browse Doctors & Available Slots (By Department)");
            System.out.println("2. Book an Appointment");
            System.out.println("3. Hospital Staff: View All Appointments");
            System.out.println("4. Cancel an Appointment");
            System.out.println("5. Mark Appointment as Completed");
            System.out.println("6. Print Appointment Slip");
            System.out.println("7. View Project & Team Information (SRM Trichy)");
            System.out.println("8. Exit Application");
            System.out.print("\nEnter your choice (1-8): ");

            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input) {
                case "1":
                    handleBrowseDoctors();
                    break;
                case "2":
                    handleBookAppointment();
                    break;
                case "3":
                    handleViewAllAppointments();
                    break;
                case "4":
                    handleCancelAppointment();
                    break;
                case "5":
                    handleCompleteAppointment();
                    break;
                case "6":
                    handlePrintSlip();
                    break;
                case "7":
                    printProjectInfo();
                    break;
                case "8":
                    running = false;
                    System.out.println("Thank you for using SRM Trichy Hospital Appointment System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select an option from 1 to 8.");
            }
            if (running) {
                System.out.print("\nPress ENTER to return to main menu...");
                scanner.nextLine();
            }
        }
    }

    private void printHeader() {
        System.out.println("\n==========================================================================");
        System.out.println("       SRM TRICHY  •  CSE AIML A  •  HOSPITAL DOCTOR APPOINTMENT SYSTEM    ");
        System.out.println("          A Smart and Efficient Solution for Doctor Appointment Booking   ");
        System.out.println("==========================================================================");
    }

    private void handleBrowseDoctors() {
        System.out.println("Select a Department to check availability:");
        Department[] depts = Department.values();
        for (int i = 0; i < depts.length; i++) {
            System.out.printf("  %d. %s (%s)\n", (i + 1), depts[i].getDisplayName(), depts[i].getDescription());
        }
        System.out.printf("  %d. View All Departments\n", depts.length + 1);
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        List<Doctor> doctors;
        try {
            int idx = Integer.parseInt(ch);
            if (idx >= 1 && idx <= depts.length) {
                doctors = service.getDoctorsByDepartment(depts[idx - 1]);
            } else {
                doctors = service.getAllDoctors();
            }
        } catch (Exception e) {
            doctors = service.getAllDoctors();
        }

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("DOCTOR AVAILABILITY & SLOTS:");
        System.out.println("--------------------------------------------------------------------------");
        if (doctors.isEmpty()) {
            System.out.println("No doctors currently registered in this department.");
            return;
        }

        for (Doctor doc : doctors) {
            System.out.printf("[%s] Dr. %s | %s | %s | Fee: Rs. %.2f | %s\n",
                    doc.getId(), doc.getName(), doc.getQualification(),
                    doc.getDepartment().getDisplayName(), doc.getConsultationFee(), doc.getRoomNumber());
            List<TimeSlot> freeSlots = doc.getAvailableSlots();
            if (freeSlots.isEmpty()) {
                System.out.println("   No free slots available today for this doctor.");
            } else {
                System.out.println("   Available Slots:");
                for (TimeSlot s : freeSlots) {
                    System.out.printf("     -> [%s] %s at %s\n", s.getSlotId(), s.getFormattedDate(), s.getFormattedTime());
                }
            }
            System.out.println();
        }
    }

    private void handleBookAppointment() {
        System.out.println("--- BOOK AN APPOINTMENT ---");
        System.out.print("Enter Patient Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Contact Phone Number: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter Email Address (optional): ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Patient Age: ");
        int age = 0;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Error: Age must be a numeric value.");
            return;
        }

        System.out.print("Enter Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();

        System.out.print("Enter Doctor ID (e.g., DOC-101 for Cardiology, DOC-102 for Ortho, DOC-103 for Pedia): ");
        String doctorId = scanner.nextLine().trim();

        System.out.print("Enter Slot ID (e.g., SLOT-C1, SLOT-O1, SLOT-P1): ");
        String slotId = scanner.nextLine().trim();

        try {
            Appointment apt = service.bookAppointment(name, phone, email, age, gender, doctorId, slotId);
            System.out.println("\nSUCCESS! Appointment booked successfully.");
            System.out.println(apt.generateAppointmentSlip());
        } catch (ValidationException | ResourceNotFoundException | SlotAlreadyBookedException e) {
            System.out.println("\nBOOKING FAILED: " + e.getMessage());
        }
    }

    private void handleViewAllAppointments() {
        System.out.println("--- HOSPITAL STAFF: ALL APPOINTMENT RECORDS ---");
        List<Appointment> all = service.getAllAppointments();
        if (all.isEmpty()) {
            System.out.println("No appointments have been booked yet.");
            return;
        }
        for (Appointment a : all) {
            System.out.println(a);
        }
    }

    private void handleCancelAppointment() {
        System.out.print("Enter Appointment ID to cancel (e.g. APT-1001): ");
        String aptId = scanner.nextLine().trim();
        try {
            boolean done = service.cancelAppointment(aptId);
            if (done) {
                System.out.println("Appointment " + aptId + " has been CANCELLED and the doctor slot is now free.");
            } else {
                System.out.println("Appointment was already cancelled.");
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleCompleteAppointment() {
        System.out.print("Enter Appointment ID to mark completed: ");
        String aptId = scanner.nextLine().trim();
        try {
            service.completeAppointment(aptId);
            System.out.println("Appointment " + aptId + " marked as COMPLETED.");
        } catch (ResourceNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handlePrintSlip() {
        System.out.print("Enter Appointment ID: ");
        String aptId = scanner.nextLine().trim();
        Appointment apt = service.getAppointmentById(aptId);
        if (apt != null) {
            System.out.println(apt.generateAppointmentSlip());
        } else {
            System.out.println("Appointment not found with ID: " + aptId);
        }
    }

    private void printProjectInfo() {
        System.out.println("==========================================================================");
        System.out.println("SRM TRICHY • DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING (AI & ML)");
        System.out.println("Class: CSE AIML A");
        System.out.println("Project: Hospital Doctor Appointment System");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEAM MEMBERS:");
        System.out.println("  1. DB Dhanvanth             | Reg No: RA2511026050032");
        System.out.println("  2. MV Mudapalli Vineel Kumar | Reg No: RA2511026050057");
        System.out.println("  3. KS Kabish Sridar          | Reg No: RA2511026050018");
        System.out.println("==========================================================================");
    }
}
