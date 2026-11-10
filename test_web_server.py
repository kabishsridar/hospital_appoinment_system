"""
Verification test for Doctor Appointment System Web Server & Data Synchronization.
"""
import os
import server

def test_web_server():
    print("[1/4] Checking CSV loading...")
    apts = server.load_appointments_csv()
    assert len(apts) >= 1, f"Expected at least 1 appointment, got {len(apts)}"
    print(f" -> Successfully loaded {len(apts)} appointment(s). Sample: {apts[0]['aptId']} ({apts[0]['patName']})")

    print("[2/4] Checking Doctor configuration...")
    docs = server.DEFAULT_DOCTORS
    assert len(docs) == 5, f"Expected 5 doctors, got {len(docs)}"
    depts = [d["department"] for d in docs]
    print(f" -> Found 5 doctors across departments: {', '.join(depts)}")

    print("[3/4] Checking Bootstrap vendor assets...")
    bootstrap_css = os.path.join("web", "vendor", "bootstrap", "bootstrap.min.css")
    bootstrap_js = os.path.join("web", "vendor", "bootstrap", "bootstrap.bundle.min.js")
    icons_css = os.path.join("web", "vendor", "bootstrap-icons", "bootstrap-icons.min.css")
    assert os.path.exists(bootstrap_css), "Missing bootstrap.min.css"
    assert os.path.exists(bootstrap_js), "Missing bootstrap.bundle.min.js"
    assert os.path.exists(icons_css), "Missing bootstrap-icons.min.css"
    print(" -> All Bootstrap 5 CSS, JS, and Icons assets verified locally!")

    print("[4/4] Checking HTML & JS files...")
    assert os.path.exists(os.path.join("web", "index.html")), "Missing web/index.html"
    assert os.path.exists(os.path.join("web", "css", "styles.css")), "Missing web/css/styles.css"
    assert os.path.exists(os.path.join("web", "js", "app.js")), "Missing web/js/app.js"
    print(" -> Frontend application files verified successfully!")

    print("\n[SUCCESS] All Web Portal verification tests passed cleanly!")

if __name__ == "__main__":
    test_web_server()

