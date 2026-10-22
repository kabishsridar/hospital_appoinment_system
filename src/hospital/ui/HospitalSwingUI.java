package hospital.ui;

import hospital.exception.ResourceNotFoundException;
import hospital.exception.SlotAlreadyBookedException;
import hospital.exception.ValidationException;
import hospital.model.Appointment;
import hospital.model.AppointmentStatus;
import hospital.model.Department;
import hospital.model.Doctor;
import hospital.model.TimeSlot;
import hospital.service.HospitalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Java Swing Graphical User Interface replicating the PPTX Slide 1 mockup.
 */
public class HospitalSwingUI extends JFrame {
    private final HospitalService service;

    // UI Components for Booking
    private JComboBox<Department> departmentCombo;
    private JComboBox<String> doctorCombo;
    private JComboBox<String> slotCombo;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JSpinner ageSpinner;
    private JComboBox<String> genderCombo;
    private JLabel doctorDetailsLabel;
    private JPanel cardsPreviewPanel;

    // UI Components for Staff Table
    private DefaultTableModel tableModel;
    private JTable appointmentTable;

    // Color Palette
    private static final Color PRIMARY_NAVY = new Color(15, 32, 67);
    private static final Color ACCENT_BLUE = new Color(24, 119, 242);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color BG_LIGHT = new Color(243, 246, 250);
    private static final Color TEXT_DARK = new Color(28, 37, 54);
    private static final Color SUCCESS_GREEN = new Color(16, 185, 129);

    public HospitalSwingUI(HospitalService service) {
        super("Doctor Appointment System - SRM Trichy (CSE AIML A)");
        this.service = service;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        // 1. Top Header Banner
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Tabbed Pane for Navigation
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("  Appointment Booking  ", createBookingTab());
        tabbedPane.addTab("  Hospital Staff Dashboard  ", createStaffTab());
        tabbedPane.addTab("  Project & Team Members  ", createTeamTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Seed initial UI selections
        updateDoctorDropdown();
        refreshAppointmentTable();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        JLabel subTitle = new JLabel("SRM TRICHY   •   CSE AIML A");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subTitle.setForeground(new Color(173, 206, 255));

        JLabel title = new JLabel("Doctor Appointment System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        titleBox.add(subTitle);
        titleBox.add(title);

        JLabel tag = new JLabel("Smart Doctor Appointment Booking");
        tag.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tag.setForeground(new Color(220, 235, 255));
        tag.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(titleBox, BorderLayout.WEST);
        header.add(tag, BorderLayout.EAST);
        return header;
    }

    private JPanel createBookingTab() {
        JPanel tab = new JPanel(new BorderLayout(20, 20));
        tab.setBackground(BG_LIGHT);
        tab.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top: Mockup Cards (Cardiology, Orthopedics, Pediatrics from Slide 1)
        cardsPreviewPanel = createSlideMockupCards();
        tab.add(cardsPreviewPanel, BorderLayout.NORTH);

        // Center split: Form on Left, Doctor Preview on Right
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);

        // Left Form: Patient Details & Doctor Selection
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel formTitle = new JLabel("Book Consultation Slot");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(TEXT_DARK);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        int row = 0;

        // Department
        gbc.gridx = 0; gbc.gridy = row++;
        formFields.add(new JLabel("Medical Department:"), gbc);
        departmentCombo = new JComboBox<>(Department.values());
        departmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentCombo.addActionListener(e -> updateDoctorDropdown());
        gbc.gridy = row++;
        formFields.add(departmentCombo, gbc);

        // Doctor
        gbc.gridy = row++;
        formFields.add(new JLabel("Select Doctor:"), gbc);
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.addActionListener(e -> updateSlotsDropdown());
        gbc.gridy = row++;
        formFields.add(doctorCombo, gbc);

        // Slot
        gbc.gridy = row++;
        formFields.add(new JLabel("Available Time Slot:"), gbc);
        slotCombo = new JComboBox<>();
        slotCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = row++;
        formFields.add(slotCombo, gbc);

        // Patient Name
        gbc.gridy = row++;
        formFields.add(new JLabel("Patient Full Name:"), gbc);
        nameField = new JTextField();
        gbc.gridy = row++;
        formFields.add(nameField, gbc);

        // Phone Number
        gbc.gridy = row++;
        formFields.add(new JLabel("Mobile Phone Number:"), gbc);
        phoneField = new JTextField();
        gbc.gridy = row++;
        formFields.add(phoneField, gbc);

        // Age & Gender inline
        JPanel ageGenderPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        ageGenderPanel.setOpaque(false);

        JPanel ageBox = new JPanel(new BorderLayout());
        ageBox.setOpaque(false);
        ageBox.add(new JLabel("Age:"), BorderLayout.NORTH);
        ageSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        ageBox.add(ageSpinner, BorderLayout.CENTER);

        JPanel genderBox = new JPanel(new BorderLayout());
        genderBox.setOpaque(false);
        genderBox.add(new JLabel("Gender:"), BorderLayout.NORTH);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.add(genderCombo, BorderLayout.CENTER);

        ageGenderPanel.add(ageBox);
        ageGenderPanel.add(genderBox);

        gbc.gridy = row++;
        formFields.add(ageGenderPanel, gbc);

        // Email
        gbc.gridy = row++;
        formFields.add(new JLabel("Email Address (Optional):"), gbc);
        emailField = new JTextField();
        gbc.gridy = row++;
        formFields.add(emailField, gbc);

        formCard.add(formFields, BorderLayout.CENTER);

        // Book Button
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bookBtn.setBackground(ACCENT_BLUE);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setPreferredSize(new Dimension(200, 42));
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> handleBookingAction());
        formCard.add(bookBtn, BorderLayout.SOUTH);

        centerGrid.add(formCard);

        // Right side: Doctor Profile & Real-Time Availability View
        JPanel detailsCard = new JPanel(new BorderLayout());
        detailsCard.setBackground(CARD_BG);
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel detailsTitle = new JLabel("Doctor & Hospital Info");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailsTitle.setForeground(TEXT_DARK);
        detailsCard.add(detailsTitle, BorderLayout.NORTH);

        doctorDetailsLabel = new JLabel("Select a doctor to view details.");
        doctorDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doctorDetailsLabel.setVerticalAlignment(SwingConstants.TOP);
        detailsCard.add(doctorDetailsLabel, BorderLayout.CENTER);

        centerGrid.add(detailsCard);

        tab.add(centerGrid, BorderLayout.CENTER);
        return tab;
    }

    private JPanel createSlideMockupCards() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        // Card 1: Cardiology (Slide 1 Mockup)
        panel.add(createMiniCard("Cardiology", "Available today 10:30", "Dr. Rajesh Sharma", new Color(239, 246, 255), ACCENT_BLUE));
        // Card 2: Orthopedics (Slide 1 Mockup)
        panel.add(createMiniCard("Orthopedics", "Available today 11:15", "Dr. Priya Sundaram", new Color(240, 253, 244), SUCCESS_GREEN));
        // Card 3: Pediatrics (Slide 1 Mockup)
        panel.add(createMiniCard("Pediatrics", "Available today 12:00", "Dr. Arvind Menon", new Color(254, 242, 242), new Color(239, 68, 68)));

        return panel;
    }

    private JPanel createMiniCard(String dept, String slotText, String doctorName, Color bg, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accent, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel deptLbl = new JLabel(dept);
        deptLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deptLbl.setForeground(TEXT_DARK);

        JLabel docLbl = new JLabel(doctorName);
        docLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        docLbl.setForeground(Color.GRAY);

        JLabel slotLbl = new JLabel("● " + slotText);
        slotLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        slotLbl.setForeground(accent);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(deptLbl);
        top.add(docLbl);

        card.add(top, BorderLayout.CENTER);
        card.add(slotLbl, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createStaffTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(BG_LIGHT);
        tab.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top Controls
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topBar.setOpaque(false);

        JLabel lbl = new JLabel("Scheduled Appointments Record (Live View)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(lbl);

        JButton refreshBtn = new JButton("Refresh Table");
        refreshBtn.addActionListener(e -> refreshAppointmentTable());
        topBar.add(refreshBtn);

        tab.add(topBar, BorderLayout.NORTH);

        // Appointments Table
        String[] columns = {"ID", "Patient Name", "Phone", "Age", "Doctor", "Department", "Date", "Slot Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        appointmentTable.setRowHeight(28);
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        appointmentTable.getTableHeader().setBackground(new Color(230, 235, 245));
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        appointmentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(new LineBorder(new Color(220, 226, 235), 1));
        tab.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel Selected Appointment");
        cancelBtn.setForeground(new Color(220, 38, 38));
        cancelBtn.addActionListener(e -> handleCancelAction());

        JButton completeBtn = new JButton("Mark as Completed");
        completeBtn.addActionListener(e -> handleCompleteAction());

        JButton printBtn = new JButton("View / Print Appointment Slip");
        printBtn.setBackground(ACCENT_BLUE);
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> handlePrintAction());

        actions.add(cancelBtn);
        actions.add(completeBtn);
        actions.add(printBtn);

        tab.add(actions, BorderLayout.SOUTH);

        return tab;
    }

    private JPanel createTeamTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(BG_LIGHT);
        tab.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel container = new JPanel(new GridLayout(3, 1, 20, 20));
        container.setOpaque(false);

        // Header Card
        JPanel headerCard = new JPanel(new GridLayout(3, 1, 5, 5));
        headerCard.setBackground(PRIMARY_NAVY);
        headerCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel inst = new JLabel("SRM TRICHY  •  DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING (AI & ML)");
        inst.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inst.setForeground(new Color(173, 206, 255));

        JLabel title = new JLabel("Doctor Appointment System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel cls = new JLabel("Class: CSE AIML A  |  Semester 3: Advanced Programming Practice");
        cls.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cls.setForeground(new Color(220, 235, 255));

        headerCard.add(inst);
        headerCard.add(title);
        headerCard.add(cls);
        container.add(headerCard);

        // Team Members Card (Slide 2)
        JPanel teamCard = new JPanel(new GridLayout(1, 3, 20, 0));
        teamCard.setOpaque(false);

        teamCard.add(createMemberBox("DB Dhanvanth", "RA2511026050032", "CSE AIML A"));
        teamCard.add(createMemberBox("MV Mudapalli Vineel Kumar", "RA2511026050057", "CSE AIML A"));
        teamCard.add(createMemberBox("KS Kabish Sridar", "RA2511026050018", "CSE AIML A"));

        container.add(teamCard);

        // Abstract Box (Slide 4)
        JPanel abstractCard = new JPanel(new BorderLayout(10, 10));
        abstractCard.setBackground(CARD_BG);
        abstractCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel absTitle = new JLabel("Project Abstract (Slide 4)");
        absTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        absTitle.setForeground(TEXT_DARK);

        JTextArea absText = new JTextArea(
                "“The Hospital Doctor Appointment System aims to simplify the process of booking medical " +
                "consultations for patients and managing schedules for hospitals. The proposed system allows " +
                "patients to view doctor details, check available appointment slots, and book appointments " +
                "conveniently. It helps hospital staff organize schedules and maintain appointment records systematically. " +
                "By reducing manual coordination, the system aims to save time, minimize scheduling errors, and improve " +
                "the overall experience for patients and healthcare providers.”"
        );
        absText.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        absText.setLineWrap(true);
        absText.setWrapStyleWord(true);
        absText.setEditable(false);
        absText.setOpaque(false);

        abstractCard.add(absTitle, BorderLayout.NORTH);
        abstractCard.add(absText, BorderLayout.CENTER);
        container.add(abstractCard);

        tab.add(container, BorderLayout.CENTER);
        return tab;
    }

    private JPanel createMemberBox(String name, String regNo, String dept) {
        JPanel box = new JPanel(new GridLayout(3, 1, 4, 4));
        box.setBackground(CARD_BG);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLbl.setForeground(TEXT_DARK);

        JLabel regLbl = new JLabel("Register No: " + regNo);
        regLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        regLbl.setForeground(ACCENT_BLUE);

        JLabel deptLbl = new JLabel(dept);
        deptLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deptLbl.setForeground(Color.GRAY);

        box.add(nameLbl);
        box.add(regLbl);
        box.add(deptLbl);
        return box;
    }

    private void updateDoctorDropdown() {
        Department selectedDept = (Department) departmentCombo.getSelectedItem();
        doctorCombo.removeAllItems();

        if (selectedDept != null) {
            List<Doctor> docs = service.getDoctorsByDepartment(selectedDept);
            for (Doctor d : docs) {
                doctorCombo.addItem(d.getId() + " - " + d.getName());
            }
        }
        updateSlotsDropdown();
    }

    private void updateSlotsDropdown() {
        slotCombo.removeAllItems();
        String selectedDocStr = (String) doctorCombo.getSelectedItem();

        if (selectedDocStr != null) {
            String docId = selectedDocStr.split(" - ")[0];
            try {
                Doctor doc = service.getDoctorById(docId);
                StringBuilder sb = new StringBuilder("<html><body style='padding:5px;'>");
                sb.append("<b>Doctor:</b> ").append(doc.getName()).append("<br>");
                sb.append("<b>Department:</b> ").append(doc.getDepartment().getDisplayName()).append("<br>");
                sb.append("<b>Qualification:</b> ").append(doc.getQualification()).append("<br>");
                sb.append("<b>Room:</b> ").append(doc.getRoomNumber()).append("<br>");
                sb.append("<b>Consultation Fee:</b> Rs. ").append(String.format("%.2f", doc.getConsultationFee())).append("<br><br>");
                sb.append("<b>Available Time Slots:</b><br>");

                List<TimeSlot> slots = doc.getAvailableSlots();
                if (slots.isEmpty()) {
                    sb.append("<font color='red'><i>No free slots currently available.</i></font>");
                    slotCombo.addItem("No slots available");
                } else {
                    for (TimeSlot s : slots) {
                        slotCombo.addItem(s.getSlotId() + " (" + s.getFormattedDate() + " at " + s.getFormattedTime() + ")");
                        sb.append("• ").append(s.getFormattedDate()).append(" at ").append(s.getFormattedTime()).append("<br>");
                    }
                }
                sb.append("</body></html>");
                doctorDetailsLabel.setText(sb.toString());
            } catch (ResourceNotFoundException e) {
                doctorDetailsLabel.setText("Doctor details not available.");
            }
        } else {
            doctorDetailsLabel.setText("No doctor selected.");
        }
    }

    private void handleBookingAction() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String gender = (String) genderCombo.getSelectedItem();

        String selectedDocStr = (String) doctorCombo.getSelectedItem();
        String selectedSlotStr = (String) slotCombo.getSelectedItem();

        if (selectedDocStr == null || selectedSlotStr == null || selectedSlotStr.startsWith("No slots")) {
            JOptionPane.showMessageDialog(this, "Please select an available doctor and time slot.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String docId = selectedDocStr.split(" - ")[0];
        String slotId = selectedSlotStr.split(" ")[0];

        try {
            Appointment apt = service.bookAppointment(name, phone, email, age, gender, docId, slotId);

            // Show slip in modal
            JTextArea slipArea = new JTextArea(apt.generateAppointmentSlip());
            slipArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            slipArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(slipArea), "Booking Confirmed! (Token Issued)", JOptionPane.INFORMATION_MESSAGE);

            // Clear inputs
            nameField.setText("");
            phoneField.setText("");
            emailField.setText("");

            // Refresh UI
            updateSlotsDropdown();
            refreshAppointmentTable();

        } catch (ValidationException | ResourceNotFoundException | SlotAlreadyBookedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAppointmentTable() {
        tableModel.setRowCount(0);
        List<Appointment> all = service.getAllAppointments();
        for (Appointment a : all) {
            tableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getPatient().getName(),
                    a.getPatient().getPhone(),
                    a.getPatient().getAge(),
                    a.getDoctor().getName(),
                    a.getDoctor().getDepartment().getDisplayName(),
                    a.getSlot().getFormattedDate(),
                    a.getSlot().getFormattedTime(),
                    a.getStatus().name()
            });
        }
    }

    private void handleCancelAction() {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to cancel.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String aptId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel appointment " + aptId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean done = service.cancelAppointment(aptId);
                if (done) {
                    JOptionPane.showMessageDialog(this, "Appointment " + aptId + " was cancelled and the slot has been freed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAppointmentTable();
                    updateSlotsDropdown();
                } else {
                    JOptionPane.showMessageDialog(this, "Appointment is already cancelled.", "Notice", JOptionPane.WARNING_MESSAGE);
                }
            } catch (ResourceNotFoundException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCompleteAction() {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to mark completed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String aptId = (String) tableModel.getValueAt(row, 0);
        try {
            service.completeAppointment(aptId);
            JOptionPane.showMessageDialog(this, "Appointment " + aptId + " marked as COMPLETED.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointmentTable();
        } catch (ResourceNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePrintAction() {
        int row = appointmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view slip.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String aptId = (String) tableModel.getValueAt(row, 0);
        Appointment apt = service.getAppointmentById(aptId);
        if (apt != null) {
            JTextArea slipArea = new JTextArea(apt.generateAppointmentSlip());
            slipArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            slipArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(slipArea), "Appointment Slip - " + aptId, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
