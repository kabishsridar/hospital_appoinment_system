# Doctor Appointment System

**Institution**: SRM TRICHY • Department of Computer Science & Engineering (AI & ML)  
**Course**: Semester 3 - Advanced Programming Practice (APP)  
**Class**: CSE AIML A  

---

## 👥 Project Team Members (Slide 2)
1. **DB Dhanvanth** — Register No: `RA2511026050032`
2. **MV Mudapalli Vineel Kumar** — Register No: `RA2511026050057`
3. **Kabish Sridar** — Register No: `RA2511026050018`

---

## 📋 Problem Statement & Abstract (Slides 3 & 4)
Patients often face difficulties booking doctor appointments due to manual scheduling, long waiting times, repeated phone calls, and a lack of clear information regarding doctor availability.

The **Doctor Appointment System** provides a smart, reliable, and organized solution to:
- Browse doctor specialties (**Cardiology**, **Orthopedics**, **Pediatrics**, **General Medicine**, **Neurology**).
- Check real-time slot availability (e.g., `10:30 AM`, `11:15 AM`, `12:00 PM`).
- Prevent double bookings and scheduling conflicts.
- Issue appointment tokens and slips for patients.
- Give hospital staff an organized management dashboard to track and update patient queues.

---

## 🏗️ Project Architecture & OOP Paradigms

```
app_project/
├── build_and_run.bat       # DOS script: Compiles and starts the Java Swing GUI
├── run_cli.bat             # DOS script: Compiles and starts the interactive CLI
├── data/
│   └── appointments.csv    # Persistent file storage for appointments
├── bin/                    # Compiled Java .class bytecode
└── src/
    └── hospital/
        ├── Main.java                        # Application Launcher
        ├── model/                           # Domain Entities
        │   ├── Person.java                  # Abstract Base Class (Encapsulation/Inheritance)
        │   ├── Patient.java                 # Concrete Subclass
        │   ├── Doctor.java                  # Concrete Subclass (synchronized slots)
        │   ├── Department.java              # Enum for Specialties
        │   ├── TimeSlot.java                # Slot timing with Comparable
        │   ├── Appointment.java             # Booking record & slip generator
        │   └── AppointmentStatus.java       # Status Lifecycle Enum
        ├── dao/                             # Data Access Object Layer
        │   ├── HospitalDataStore.java       # Interface
        │   └── FileHospitalDataStore.java   # File persistence & seed data
        ├── service/                         # Business Logic Layer
        │   └── HospitalService.java         # Thread-safe booking & conflict checks
        ├── exception/                       # Custom Exception Hierarchy
        │   ├── SlotAlreadyBookedException.java
        │   ├── ValidationException.java
        │   └── ResourceNotFoundException.java
        ├── ui/                              # User Interface Layer
        │   ├── HospitalSwingUI.java         # Desktop GUI (Replicates Slide 1 Mockup)
        │   └── ConsoleUI.java               # Interactive Terminal Menu
        └── test/
            └── TestAppointmentSystem.java  # Automated Verification Test Suite
```

### Key Java & OOP Concepts Applied:
1. **Inheritance & Abstraction**: `abstract class Person` extended by `Doctor` and `Patient`.
2. **Encapsulation**: Private fields with validated accessors and state encapsulation.
3. **Polymorphism**: Dynamic method overriding (`getRoleDescription()`, `toString()`).
4. **Collections Framework**: `ArrayList`, `Map<String, Doctor>`, `Comparable<TimeSlot>` for automatic chronological ordering.
5. **Thread Safety**: Synchronized booking validation avoiding race conditions and duplicate slot allocations.
6. **Custom Exceptions**: Specialized business exceptions (`SlotAlreadyBookedException`, `ValidationException`).
7. **Persistence**: Local CSV storage and state hydration between sessions.
8. **Modern GUI**: Multi-tab Java Swing application styled according to Slide 1 of the presentation.

---

## 🚀 How to Run

### Option 1: Modern Desktop GUI (Recommended)
Double-click [build_and_run.bat](build_and_run.bat) or run in the terminal:
```cmd
cd d:\srm_university\semester_3\APP\app_project
build_and_run.bat
```
*(Or directly with Java)*:
```cmd
javac -d bin -sourcepath src src/hospital/Main.java src/hospital/model/*.java src/hospital/dao/*.java src/hospital/service/*.java src/hospital/exception/*.java src/hospital/ui/*.java
java -cp bin hospital.Main
```

### Option 2: Interactive Terminal / Console Mode
Double-click [run_cli.bat](run_cli.bat) or run:
```cmd
cd d:\srm_university\semester_3\APP\app_project
run_cli.bat
```

### Option 3: Run Automated Verification Tests
```cmd
javac -d bin -sourcepath src src/hospital/test/TestAppointmentSystem.java
java -cp bin hospital.test.TestAppointmentSystem
```
