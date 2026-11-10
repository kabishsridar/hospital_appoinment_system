"""
Doctor Appointment System - Web Backend Server
Zero-dependency HTTP Server providing REST API endpoints connected directly
to data/appointments.csv and serving the web frontend with Bootstrap 5.
"""

import csv
import json
import os
import sys
import webbrowser
from datetime import datetime
from http.server import HTTPServer, SimpleHTTPRequestHandler
from urllib.parse import parse_qs, urlparse

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_FILE = os.path.join(BASE_DIR, "data", "appointments.csv")
WEB_DIR = os.path.join(BASE_DIR, "web")

# Seed doctors matching Java FileHospitalDataStore
DEFAULT_DOCTORS = [
    {
        "id": "DOC-101",
        "name": "Dr. Rajesh Sharma",
        "phone": "9876543210",
        "email": "r.sharma@hospital.org",
        "department": "Cardiology",
        "qualifications": "MD, DM (Cardiology)",
        "fee": 800.0,
        "room": "Room 201 (Block A)",
        "slots": [
            {"id": "SLOT-C1", "time": "10:30 AM", "date": "Today"},
            {"id": "SLOT-C2", "time": "11:30 AM", "date": "Today"},
            {"id": "SLOT-C3", "time": "04:00 PM", "date": "Today"},
            {"id": "SLOT-C4", "time": "10:30 AM", "date": "Tomorrow"}
        ]
    },
    {
        "id": "DOC-102",
        "name": "Dr. Priya Sundaram",
        "phone": "9876543211",
        "email": "p.sundaram@hospital.org",
        "department": "Orthopedics",
        "qualifications": "MS (Ortho), DNB, MCh",
        "fee": 750.0,
        "room": "Room 105 (Block B)",
        "slots": [
            {"id": "SLOT-O1", "time": "11:15 AM", "date": "Today"},
            {"id": "SLOT-O2", "time": "02:00 PM", "date": "Today"},
            {"id": "SLOT-O3", "time": "05:30 PM", "date": "Today"},
            {"id": "SLOT-O4", "time": "11:15 AM", "date": "Tomorrow"}
        ]
    },
    {
        "id": "DOC-103",
        "name": "Dr. Arvind Menon",
        "phone": "9876543212",
        "email": "a.menon@hospital.org",
        "department": "Pediatrics",
        "qualifications": "MD (Pediatrics), DCH",
        "fee": 600.0,
        "room": "Room 110 (Child Care)",
        "slots": [
            {"id": "SLOT-P1", "time": "12:00 PM", "date": "Today"},
            {"id": "SLOT-P2", "time": "03:30 PM", "date": "Today"},
            {"id": "SLOT-P3", "time": "06:00 PM", "date": "Today"},
            {"id": "SLOT-P4", "time": "12:00 PM", "date": "Tomorrow"}
        ]
    },
    {
        "id": "DOC-104",
        "name": "Dr. Ananya Rao",
        "phone": "9876543213",
        "email": "a.rao@hospital.org",
        "department": "General Medicine",
        "qualifications": "MBBS, MD (Internal Med)",
        "fee": 500.0,
        "room": "Room 101 (OPD)",
        "slots": [
            {"id": "SLOT-G1", "time": "09:30 AM", "date": "Today"},
            {"id": "SLOT-G2", "time": "10:00 AM", "date": "Today"},
            {"id": "SLOT-G3", "time": "04:30 PM", "date": "Today"}
        ]
    },
    {
        "id": "DOC-105",
        "name": "Dr. Vikram Seth",
        "phone": "9876543214",
        "email": "v.seth@hospital.org",
        "department": "Neurology",
        "qualifications": "DM (Neurology), Fellowship AIIMS",
        "fee": 900.0,
        "room": "Room 304 (Block C)",
        "slots": [
            {"id": "SLOT-N1", "time": "11:00 AM", "date": "Today"},
            {"id": "SLOT-N2", "time": "03:00 PM", "date": "Today"}
        ]
    }
]


def load_appointments_csv():
    """Load appointments from CSV matching the Java file format."""
    appointments = []
    if not os.path.exists(DATA_FILE):
        return appointments

    try:
        with open(DATA_FILE, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                parts = line.split(",")
                if len(parts) >= 11:
                    appointments.append({
                        "aptId": parts[0].strip(),
                        "patId": parts[1].strip(),
                        "patName": parts[2].strip(),
                        "patPhone": parts[3].strip(),
                        "patEmail": parts[4].strip(),
                        "patAge": parts[5].strip(),
                        "patGender": parts[6].strip(),
                        "docId": parts[7].strip(),
                        "slotId": parts[8].strip(),
                        "bookedAt": parts[9].strip(),
                        "status": parts[10].strip()
                    })
    except Exception as e:
        print(f"Warning loading CSV: {e}")
    return appointments


def save_appointments_csv(appointments):
    """Save appointments list back to the CSV."""
    os.makedirs(os.path.dirname(DATA_FILE), exist_ok=True)
    with open(DATA_FILE, "w", encoding="utf-8", newline="") as f:
        f.write("#aptId,patId,patName,patPhone,patEmail,patAge,patGender,docId,slotId,bookedAtIso,status\n")
        for a in appointments:
            line = f"{a['aptId']},{a['patId']},{a['patName']},{a['patPhone']},{a['patEmail']},{a['patAge']},{a['patGender']},{a['docId']},{a['slotId']},{a['bookedAt']},{a['status']}\n"
            f.write(line)


class HospitalRequestHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=BASE_DIR, **kwargs)

    def do_GET(self):
        parsed = urlparse(self.path)
        if parsed.path == "/" or parsed.path == "/index.html":
            self.send_response(302)
            self.send_header("Location", "/web/index.html")
            self.end_headers()
            return

        if parsed.path == "/api/doctors":
            # Compute live slot booked statuses
            apts = load_appointments_csv()
            confirmed_slots = {a["slotId"] for a in apts if a["status"] == "CONFIRMED"}

            doctors_copy = json.loads(json.dumps(DEFAULT_DOCTORS))
            for doc in doctors_copy:
                for slot in doc["slots"]:
                    slot["isBooked"] = slot["id"] in confirmed_slots

            self.send_json_response(doctors_copy)
            return

        if parsed.path == "/api/appointments":
            apts = load_appointments_csv()
            self.send_json_response(apts)
            return

        return super().do_GET()

    def do_POST(self):
        parsed = urlparse(self.path)
        content_length = int(self.headers.get("Content-Length", 0))
        post_body = self.rfile.read(content_length)

        try:
            data = json.loads(post_body.decode("utf-8"))
        except Exception:
            data = {}

        if parsed.path == "/api/appointments":
            apts = load_appointments_csv()
            slot_id = data.get("slotId")
            doc_id = data.get("docId")

            # Check for conflict
            for a in apts:
                if a["slotId"] == slot_id and a["status"] == "CONFIRMED":
                    self.send_json_response({"error": "Selected time slot is already booked!"}, status=409)
                    return

            # Generate IDs
            new_apt_num = 1001
            for a in apts:
                if a["aptId"].startswith("APT-"):
                    try:
                        num = int(a["aptId"].replace("APT-", ""))
                        if num >= new_apt_num:
                            new_apt_num = num + 1
                    except ValueError:
                        pass
            apt_id = f"APT-{new_apt_num}"

            import random
            pat_id = f"PAT-{random.randint(1000, 9999)}"

            booked_at = datetime.now().isoformat()

            new_apt = {
                "aptId": apt_id,
                "patId": pat_id,
                "patName": str(data.get("patName", "")).replace(",", " ").strip(),
                "patPhone": str(data.get("patPhone", "")).replace(",", " ").strip(),
                "patEmail": str(data.get("patEmail", "")).replace(",", " ").strip(),
                "patAge": str(data.get("patAge", "25")).replace(",", " ").strip(),
                "patGender": str(data.get("patGender", "Other")).replace(",", " ").strip(),
                "docId": doc_id,
                "slotId": slot_id,
                "bookedAt": booked_at,
                "status": "CONFIRMED"
            }

            apts.append(new_apt)
            save_appointments_csv(apts)
            self.send_json_response({"success": True, "appointment": new_apt})
            return

        if parsed.path == "/api/appointments/status":
            apts = load_appointments_csv()
            apt_id = data.get("aptId")
            new_status = data.get("status")

            updated = False
            for a in apts:
                if a["aptId"] == apt_id:
                    a["status"] = new_status
                    updated = True
                    break

            if updated:
                save_appointments_csv(apts)
                self.send_json_response({"success": True})
            else:
                self.send_json_response({"error": "Appointment not found"}, status=404)
            return

        self.send_response(404)
        self.end_headers()

    def send_json_response(self, obj, status=200):
        body = json.dumps(obj).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Access-Control-Allow-Origin", "*")
        self.end_headers()
        self.wfile.write(body)


def run_server(port=8000):
    server_address = ("", port)
    httpd = HTTPServer(server_address, HospitalRequestHandler)
    print("=" * 65)
    print(f"🏥 Doctor Appointment System Web Server Running!")
    print(f"🌐 Access URL: http://localhost:{port}/web/index.html")
    print(f"📁 Synchronized with CSV: {DATA_FILE}")
    print("=" * 65)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping server.")
        httpd.server_close()


if __name__ == "__main__":
    port = 8000
    if len(sys.argv) > 1:
        try:
            port = int(sys.argv[1])
        except ValueError:
            pass
    run_server(port)

