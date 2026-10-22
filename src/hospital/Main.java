package hospital;

import hospital.dao.FileHospitalDataStore;
import hospital.dao.HospitalDataStore;
import hospital.service.HospitalService;
import hospital.ui.ConsoleUI;
import hospital.ui.HospitalSwingUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main application launcher for the SRM Trichy Hospital Doctor Appointment System.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Initialize persistent data store (stored in app_project/data/appointments.csv)
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(dataDir, "appointments.csv");
        HospitalDataStore dataStore = new FileHospitalDataStore(dataFile);
        HospitalService service = new HospitalService(dataStore);

        // 2. Check execution mode flag
        boolean runConsole = false;
        if (args.length > 0 && (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("--console"))) {
            runConsole = true;
        }

        // Check if headless environment
        if (GraphicsEnvironment.isHeadless() || runConsole) {
            System.out.println("Starting in Console CLI Mode...");
            ConsoleUI console = new ConsoleUI(service);
            console.start();
        } else {
            // Launch Modern Swing GUI
            SwingUtilities.invokeLater(() -> {
                try {
                    // Try system native Look & Feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                HospitalSwingUI gui = new HospitalSwingUI(service);
                gui.setVisible(true);
            });
        }
    }
}
