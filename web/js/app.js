/**
 * Doctor Appointment System - Modern Frontend Engine
 * Powered by Bootstrap 5 & Vanilla ES6+
 */

// Fallback seed data matching Java FileHospitalDataStore & appointments.csv
const INITIAL_DOCTORS = [
  {
    id: "DOC-101",
    name: "Dr. Rajesh Sharma",
    phone: "9876543210",
    email: "r.sharma@hospital.org",
    department: "Cardiology",
    qualifications: "MD, DM (Cardiology)",
    fee: 800.0,
    room: "Room 201 (Block A)",
    slots: [
      { id: "SLOT-C1", time: "10:30 AM", date: "Today" },
      { id: "SLOT-C2", time: "11:30 AM", date: "Today" },
      { id: "SLOT-C3", time: "04:00 PM", date: "Today" },
      { id: "SLOT-C4", time: "10:30 AM", date: "Tomorrow" }
    ]
  },
  {
    id: "DOC-102",
    name: "Dr. Priya Sundaram",
    phone: "9876543211",
    email: "p.sundaram@hospital.org",
    department: "Orthopedics",
    qualifications: "MS (Ortho), DNB, MCh",
    fee: 750.0,
    room: "Room 105 (Block B)",
    slots: [
      { id: "SLOT-O1", time: "11:15 AM", date: "Today" },
      { id: "SLOT-O2", time: "02:00 PM", date: "Today" },
      { id: "SLOT-O3", time: "05:30 PM", date: "Today" },
      { id: "SLOT-O4", time: "11:15 AM", date: "Tomorrow" }
    ]
  },
  {
    id: "DOC-103",
    name: "Dr. Arvind Menon",
    phone: "9876543212",
    email: "a.menon@hospital.org",
    department: "Pediatrics",
    qualifications: "MD (Pediatrics), DCH",
    fee: 600.0,
    room: "Room 110 (Child Care)",
    slots: [
      { id: "SLOT-P1", time: "12:00 PM", date: "Today" },
      { id: "SLOT-P2", time: "03:30 PM", date: "Today" },
      { id: "SLOT-P3", time: "06:00 PM", date: "Today" },
      { id: "SLOT-P4", time: "12:00 PM", date: "Tomorrow" }
    ]
  },
  {
    id: "DOC-104",
    name: "Dr. Ananya Rao",
    phone: "9876543213",
    email: "a.rao@hospital.org",
    department: "General Medicine",
    qualifications: "MBBS, MD (Internal Med)",
    fee: 500.0,
    room: "Room 101 (OPD)",
    slots: [
      { id: "SLOT-G1", time: "09:30 AM", date: "Today" },
      { id: "SLOT-G2", time: "10:00 AM", date: "Today" },
      { id: "SLOT-G3", time: "04:30 PM", date: "Today" }
    ]
  },
  {
    id: "DOC-105",
    name: "Dr. Vikram Seth",
    phone: "9876543214",
    email: "v.seth@hospital.org",
    department: "Neurology",
    qualifications: "DM (Neurology), Fellowship AIIMS",
    fee: 900.0,
    room: "Room 304 (Block C)",
    slots: [
      { id: "SLOT-N1", time: "11:00 AM", date: "Today" },
      { id: "SLOT-N2", time: "03:00 PM", date: "Today" }
    ]
  }
];

const INITIAL_APPOINTMENTS = [
  {
    aptId: "APT-1001",
    patId: "PAT-4895",
    patName: "Sample Patient",
    patPhone: "919548764958",
    patEmail: "patient@sample.com",
    patAge: "19",
    patGender: "Male",
    docId: "DOC-104",
    slotId: "SLOT-G2",
    bookedAt: new Date().toISOString(),
    status: "CONFIRMED"
  }
];

// App State
let state = {
  doctors: [],
  appointments: [],
  selectedDepartment: "ALL",
  selectedDoctorId: null,
  selectedSlotId: null,
  isApiAvailable: false
};

// Department styling maps
const DEPT_BADGE_CLASS = {
  Cardiology: "badge-cardio",
  Orthopedics: "badge-ortho",
  Pediatrics: "badge-pedia",
  "General Medicine": "badge-gen",
  Neurology: "badge-neuro"
};

const DEPT_AVATAR_CLASS = {
  Cardiology: "avatar-cardio",
  Orthopedics: "avatar-ortho",
  Pediatrics: "avatar-pedia",
  "General Medicine": "avatar-gen",
  Neurology: "avatar-neuro"
};

// Initialization
document.addEventListener("DOMContentLoaded", async () => {
  await loadData();
  setupEventListeners();
  renderAll();
});

// Load Data (Try REST API first, fallback to localStorage)
async function loadData() {
  try {
    const resDocs = await fetch("/api/doctors");
    if (resDocs.ok) {
      state.doctors = await resDocs.json();
      const resApts = await fetch("/api/appointments");
      state.appointments = await resApts.json();
      state.isApiAvailable = true;
      showServerConnectedBadge(true);
      return;
    }
  } catch (err) {
    // API not reachable, fallback to offline localStorage
  }

  showServerConnectedBadge(false);
  state.isApiAvailable = false;
  const storedDocs = localStorage.getItem("das_doctors");
  const storedApts = localStorage.getItem("das_appointments");

  state.doctors = storedDocs ? JSON.parse(storedDocs) : JSON.parse(JSON.stringify(INITIAL_DOCTORS));
  state.appointments = storedApts ? JSON.parse(storedApts) : JSON.parse(JSON.stringify(INITIAL_APPOINTMENTS));
  syncSlotBookedStatus();
}

function showServerConnectedBadge(connected) {
  const badge = document.getElementById("serverSyncBadge");
  if (!badge) return;
  if (connected) {
    badge.className = "badge bg-success-subtle text-success border border-success-subtle";
    badge.innerHTML = `<i class="bi bi-cloud-check-fill me-1"></i> Live CSV Sync Active`;
  } else {
    badge.className = "badge bg-warning-subtle text-warning border border-warning-subtle";
    badge.innerHTML = `<i class="bi bi-hdd me-1"></i> Local Storage Mode`;
  }
}

function syncSlotBookedStatus() {
  const confirmedSlots = new Set(
    state.appointments.filter(a => a.status === "CONFIRMED").map(a => a.slotId)
  );

  state.doctors.forEach(doc => {
    doc.slots.forEach(slot => {
      slot.isBooked = confirmedSlots.has(slot.id);
    });
  });
}

function saveLocalState() {
  localStorage.setItem("das_doctors", JSON.stringify(state.doctors));
  localStorage.setItem("das_appointments", JSON.stringify(state.appointments));
}

// Render Functions
function renderAll() {
  renderStats();
  renderDoctorCards();
  populateBookingDoctorDropdown();
  renderAppointmentsTable();
}

function renderStats() {
  const activeBookings = state.appointments.filter(a => a.status === "CONFIRMED").length;
  const completedCount = state.appointments.filter(a => a.status === "COMPLETED").length;
  
  // Calculate total fees
  let totalRevenue = 0;
  state.appointments.forEach(a => {
    if (a.status === "CONFIRMED" || a.status === "COMPLETED") {
      const doc = state.doctors.find(d => d.id === a.docId);
      if (doc) totalRevenue += doc.fee;
    }
  });

  const statActiveEl = document.getElementById("statActiveAppointments");
  const statCompletedEl = document.getElementById("statCompletedAppointments");
  const statRevenueEl = document.getElementById("statRevenue");

  if (statActiveEl) statActiveEl.textContent = activeBookings;
  if (statCompletedEl) statCompletedEl.textContent = completedCount;
  if (statRevenueEl) statRevenueEl.textContent = `₹${totalRevenue.toLocaleString()}`;
}

function renderDoctorCards() {
  const container = document.getElementById("doctorCardsContainer");
  if (!container) return;

  const filtered = state.selectedDepartment === "ALL"
    ? state.doctors
    : state.doctors.filter(d => d.department.toUpperCase() === state.selectedDepartment.toUpperCase());

  container.innerHTML = filtered.map(doc => {
    const avatarClass = DEPT_AVATAR_CLASS[doc.department] || "avatar-gen";
    const badgeClass = DEPT_BADGE_CLASS[doc.department] || "badge-gen";
    const availableSlots = doc.slots.filter(s => !s.isBooked);

    return `
      <div class="col-lg-4 col-md-6 mb-4">
        <div class="card doctor-card h-100 shadow-sm">
          <div class="card-body p-4 d-flex flex-column">
            <div class="d-flex align-items-center mb-3">
              <div class="doc-avatar-wrap ${avatarClass} me-3">
                <i class="bi bi-heart-pulse"></i>
              </div>
              <div>
                <span class="badge ${badgeClass} badge-dept mb-1">${doc.department}</span>
                <h5 class="fw-bold mb-0 text-dark">${doc.name}</h5>
                <small class="text-muted">${doc.qualifications}</small>
              </div>
            </div>

            <div class="bg-light p-2.5 rounded-3 mb-3 small">
              <div class="d-flex justify-content-between text-muted mb-1">
                <span><i class="bi bi-geo-alt-fill text-danger me-1"></i>Location:</span>
                <span class="fw-semibold text-dark">${doc.room}</span>
              </div>
              <div class="d-flex justify-content-between text-muted mb-1">
                <span><i class="bi bi-cash me-1 text-success"></i>Consultation Fee:</span>
                <span class="fw-bold text-success">₹${doc.fee}</span>
              </div>
              <div class="d-flex justify-content-between text-muted">
                <span><i class="bi bi-clock me-1 text-primary"></i>Available Slots:</span>
                <span class="badge ${availableSlots.length > 0 ? 'bg-success' : 'bg-danger'}">${availableSlots.length} Open</span>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label text-muted small fw-semibold mb-2">Today's Slots:</label>
              <div class="d-flex flex-wrap gap-1">
                ${doc.slots.map(s => `
                  <span class="slot-pill ${s.isBooked ? 'booked' : ''}" title="${s.isBooked ? 'Booked' : 'Available'}">
                    <i class="bi ${s.isBooked ? 'bi-x-circle' : 'bi-check-circle-fill text-success'}"></i>
                    ${s.time}
                  </span>
                `).join("")}
              </div>
            </div>

            <div class="mt-auto pt-2">
              <button class="btn btn-primary w-100 rounded-pill fw-semibold shadow-sm" onclick="openBookModal('${doc.id}')">
                <i class="bi bi-calendar-plus me-1"></i> Book Appointment
              </button>
            </div>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

function populateBookingDoctorDropdown() {
  const select = document.getElementById("bookDoctorSelect");
  if (!select) return;

  const currentVal = select.value;
  select.innerHTML = `<option value="">-- Choose Specialist Doctor --</option>` +
    state.doctors.map(d => `
      <option value="${d.id}" ${d.id === currentVal ? "selected" : ""}>
        ${d.name} (${d.department}) — Fee: ₹${d.fee}
      </option>
    `).join("");
}

function onDoctorSelectChanged() {
  const select = document.getElementById("bookDoctorSelect");
  state.selectedDoctorId = select.value;
  state.selectedSlotId = null;
  renderSlotPicker();
}

function renderSlotPicker() {
  const container = document.getElementById("slotPickerContainer");
  const helper = document.getElementById("slotHelperText");
  if (!container) return;

  if (!state.selectedDoctorId) {
    container.innerHTML = `<div class="text-muted small p-2"><i class="bi bi-arrow-up-circle me-1"></i>Please select a doctor first to see available consultation slots.</div>`;
    return;
  }

  const doctor = state.doctors.find(d => d.id === state.selectedDoctorId);
  if (!doctor) return;

  container.innerHTML = `
    <div class="d-flex flex-wrap gap-2">
      ${doctor.slots.map(s => `
        <button type="button" 
          class="slot-pill ${s.isBooked ? 'booked' : ''} ${s.id === state.selectedSlotId ? 'active' : ''}" 
          ${s.isBooked ? 'disabled' : ''}
          onclick="selectSlot('${s.id}')">
          <i class="bi ${s.isBooked ? 'bi-lock-fill' : (s.id === state.selectedSlotId ? 'bi-check-circle-fill' : 'bi-clock')}"></i>
          ${s.time} (${s.date})
        </button>
      `).join("")}
    </div>
  `;

  if (helper) {
    helper.textContent = `Doctor: ${doctor.name} | Room: ${doctor.room} | Fee: ₹${doctor.fee}`;
  }
}

function selectSlot(slotId) {
  state.selectedSlotId = slotId;
  renderSlotPicker();
}

// Quick Modal Booker
function openBookModal(docId) {
  state.selectedDoctorId = docId;
  const select = document.getElementById("bookDoctorSelect");
  if (select) {
    select.value = docId;
  }
  onDoctorSelectChanged();
  
  // Scroll smoothly to booking section
  const section = document.getElementById("booking-section");
  if (section) {
    section.scrollIntoView({ behavior: "smooth" });
  }
}

// Submit Booking Form
async function handleBookingSubmit(e) {
  e.preventDefault();

  const nameInput = document.getElementById("patientName");
  const phoneInput = document.getElementById("patientPhone");
  const emailInput = document.getElementById("patientEmail");
  const ageInput = document.getElementById("patientAge");
  const genderInput = document.getElementById("patientGender");

  const name = nameInput.value.trim();
  const phone = phoneInput.value.trim();
  const email = emailInput.value.trim();
  const age = ageInput.value.trim();
  const gender = genderInput.value;

  if (!state.selectedDoctorId) {
    alert("Please select a Doctor!");
    return;
  }
  if (!state.selectedSlotId) {
    alert("Please select an available Time Slot!");
    return;
  }
  if (!phone || phone.length < 10) {
    alert("Please enter a valid 10-digit phone number!");
    return;
  }

  const payload = {
    docId: state.selectedDoctorId,
    slotId: state.selectedSlotId,
    patName: name,
    patPhone: phone,
    patEmail: email || "patient@hospital.org",
    patAge: age || "25",
    patGender: gender || "Other"
  };

  let newAppointment = null;

  if (state.isApiAvailable) {
    try {
      const res = await fetch("/api/appointments", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (!res.ok) {
        alert(data.error || "Failed to book appointment");
        return;
      }
      newAppointment = data.appointment;
      // Refresh state from API
      await loadData();
    } catch (err) {
      alert("Error contacting server. Falling back to local booking.");
    }
  }

  if (!newAppointment) {
    // Local booking fallback
    const aptNum = state.appointments.length + 1001;
    const patNum = Math.floor(1000 + Math.random() * 9000);
    newAppointment = {
      aptId: `APT-${aptNum}`,
      patId: `PAT-${patNum}`,
      patName: payload.patName,
      patPhone: payload.patPhone,
      patEmail: payload.patEmail,
      patAge: payload.patAge,
      patGender: payload.patGender,
      docId: payload.docId,
      slotId: payload.slotId,
      bookedAt: new Date().toISOString(),
      status: "CONFIRMED"
    };

    state.appointments.push(newAppointment);
    syncSlotBookedStatus();
    saveLocalState();
  }

  // Clear Form
  nameInput.value = "";
  phoneInput.value = "";
  emailInput.value = "";
  ageInput.value = "";
  state.selectedSlotId = null;

  renderAll();
  renderSlotPicker();

  // Show Success Token Slip Modal
  showAppointmentSlip(newAppointment);
}

// Show Appointment Slip Modal
function showAppointmentSlip(apt) {
  const doctor = state.doctors.find(d => d.id === apt.docId) || {};
  let slotTime = "Scheduled Time";
  if (doctor.slots) {
    const slot = doctor.slots.find(s => s.id === apt.slotId);
    if (slot) slotTime = `${slot.time} (${slot.date})`;
  }

  const tokenNum = apt.aptId.replace("APT-", "TK-");

  document.getElementById("slipToken").textContent = tokenNum;
  document.getElementById("slipAptId").textContent = apt.aptId;
  document.getElementById("slipPatId").textContent = apt.patId;
  document.getElementById("slipPatName").textContent = apt.patName;
  document.getElementById("slipPatPhone").textContent = apt.patPhone;
  document.getElementById("slipPatAgeGender").textContent = `${apt.patAge} yrs • ${apt.patGender}`;
  document.getElementById("slipDocName").textContent = doctor.name || apt.docId;
  document.getElementById("slipDocDept").textContent = doctor.department || "Specialist";
  document.getElementById("slipDocRoom").textContent = doctor.room || "Consultation Room";
  document.getElementById("slipDocFee").textContent = `₹${doctor.fee || 500}`;
  document.getElementById("slipSlotTime").textContent = slotTime;
  document.getElementById("slipBookedAt").textContent = new Date(apt.bookedAt).toLocaleString();
  document.getElementById("slipBarcode").textContent = `*${apt.aptId}*`;

  const modalEl = document.getElementById("printableSlipModal");
  if (modalEl && window.bootstrap) {
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
  }
}

// Appointments Table & Queue Management
function renderAppointmentsTable() {
  const tbody = document.getElementById("appointmentsTableBody");
  if (!tbody) return;

  const searchInput = document.getElementById("queueSearchInput");
  const filterQuery = (searchInput ? searchInput.value : "").toLowerCase();

  const statusFilterSelect = document.getElementById("queueStatusFilter");
  const statusFilter = statusFilterSelect ? statusFilterSelect.value : "ALL";

  const filtered = state.appointments.filter(a => {
    const matchSearch =
      a.aptId.toLowerCase().includes(filterQuery) ||
      a.patName.toLowerCase().includes(filterQuery) ||
      a.patPhone.includes(filterQuery);

    const matchStatus = statusFilter === "ALL" || a.status === statusFilter;
    return matchSearch && matchStatus;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" class="text-center py-4 text-muted">
          <i class="bi bi-inbox fs-3 d-block mb-2 text-secondary"></i>
          No appointments found matching your search.
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = filtered.map(a => {
    const doctor = state.doctors.find(d => d.id === a.docId) || { name: a.docId, department: "Clinic", fee: 0, room: "OPD" };
    let slotLabel = a.slotId;
    if (doctor.slots) {
      const slot = doctor.slots.find(s => s.id === a.slotId);
      if (slot) slotLabel = `${slot.time} (${slot.date})`;
    }

    const isConfirmed = a.status === "CONFIRMED";

    return `
      <tr>
        <td>
          <span class="fw-bold font-monospace text-primary">${a.aptId}</span>
          <br><small class="text-muted">${a.patId}</small>
        </td>
        <td>
          <span class="fw-semibold text-dark">${a.patName}</span>
          <br><small class="text-muted"><i class="bi bi-telephone me-1"></i>${a.patPhone}</small>
        </td>
        <td>
          <span class="text-dark">${doctor.name}</span>
          <br><span class="badge ${DEPT_BADGE_CLASS[doctor.department] || 'badge-gen'} badge-dept">${doctor.department}</span>
        </td>
        <td>
          <span class="fw-medium text-dark"><i class="bi bi-clock text-primary me-1"></i>${slotLabel}</span>
          <br><small class="text-muted">${doctor.room}</small>
        </td>
        <td>
          <span class="fw-bold text-success">₹${doctor.fee}</span>
        </td>
        <td>
          <span class="status-badge status-${a.status}">${a.status}</span>
        </td>
        <td class="text-end">
          <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-secondary" title="View & Print Slip" onclick='showAppointmentSlipById("${a.aptId}")'>
              <i class="bi bi-receipt"></i>
            </button>
            ${isConfirmed ? `
              <button class="btn btn-outline-success" title="Mark Completed" onclick='updateAppointmentStatus("${a.aptId}", "COMPLETED")'>
                <i class="bi bi-check2-circle"></i>
              </button>
              <button class="btn btn-outline-danger" title="Cancel Appointment" onclick='updateAppointmentStatus("${a.aptId}", "CANCELLED")'>
                <i class="bi bi-x-circle"></i>
              </button>
            ` : ""}
          </div>
        </td>
      </tr>
    `;
  }).join("");
}

function showAppointmentSlipById(aptId) {
  const apt = state.appointments.find(a => a.aptId === aptId);
  if (apt) {
    showAppointmentSlip(apt);
  }
}

async function updateAppointmentStatus(aptId, newStatus) {
  if (!confirm(`Are you sure you want to mark appointment ${aptId} as ${newStatus}?`)) {
    return;
  }

  if (state.isApiAvailable) {
    try {
      const res = await fetch("/api/appointments/status", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ aptId, status: newStatus })
      });
      if (res.ok) {
        await loadData();
        renderAll();
        return;
      }
    } catch (e) {
      // Fallback
    }
  }

  const apt = state.appointments.find(a => a.aptId === aptId);
  if (apt) {
    apt.status = newStatus;
    syncSlotBookedStatus();
    saveLocalState();
    renderAll();
  }
}

// Event Listeners Setup
function setupEventListeners() {
  // Department filter buttons
  document.querySelectorAll(".btn-dept-filter").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".btn-dept-filter").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      state.selectedDepartment = btn.getAttribute("data-dept");
      renderDoctorCards();
    });
  });

  // Doctor Select in Booking Form
  const docSelect = document.getElementById("bookDoctorSelect");
  if (docSelect) {
    docSelect.addEventListener("change", onDoctorSelectChanged);
  }

  // Booking Form Submission
  const bookingForm = document.getElementById("appointmentBookingForm");
  if (bookingForm) {
    bookingForm.addEventListener("submit", handleBookingSubmit);
  }

  // Search in Queue Table
  const searchInput = document.getElementById("queueSearchInput");
  if (searchInput) {
    searchInput.addEventListener("input", renderAppointmentsTable);
  }

  const statusFilterSelect = document.getElementById("queueStatusFilter");
  if (statusFilterSelect) {
    statusFilterSelect.addEventListener("change", renderAppointmentsTable);
  }
}

