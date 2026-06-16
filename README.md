# 🎓 SAMS – Student Attendance Management System

A full-featured **JavaFX** desktop application for managing student attendance in educational institutions.
Built using **Layered Architecture** (Presentation → Service/BO → Data Access) with **Hibernate ORM** and **MySQL**.

---

## 📋 Project Overview

SAMS allows admin staff and lecturers to:
- Manage **Courses**, **Subjects**, **Students**, and **Lecturers**
- **Schedule** class sessions
- **Mark attendance** per student per class (Present / Absent / Late / Excused)
- Generate and **export** detailed attendance reports (CSV)
- Role-based login (**Admin** and **Lecturer**)

---



---

## 🛠️ Technologies Used

| Layer          | Technology                    |
|----------------|-------------------------------|
| Language       | Java 21                       |
| UI Framework   | JavaFX 21 + FXML              |
| ORM            | Hibernate 6.x (ORM)           |
| Database       | MySQL 8+                      |
| Build Tool     | Maven                         |
| Password Hash  | BCrypt (jbcrypt)              |
| Utilities      | Lombok                        |

---

## ⚙️ Setup Instructions

### Prerequisites
- **Java 21** (JDK)
- **MySQL 8.0+**
- **Maven 3.9+**
- IntelliJ IDEA (recommended)

### 1. Clone / Open the Project
```bash
git clone <your-repo-url>
cd StudentAttendanceManagementSystem
```

### 2. Create the Database
Open MySQL Workbench or CLI and run:
```sql
source src/main/resources/sams_db.sql
```
Or let Hibernate auto-create it on first run (`hbm2ddl.auto=update` + `createDatabaseIfNotExist=true`).

### 3. Configure Database Connection
Edit `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="connection.url">
    jdbc:mysql://localhost:3306/sams_db?createDatabaseIfNotExist=true
</property>
<property name="connection.username">root</property>
<property name="connection.password">YOUR_MYSQL_PASSWORD</property>
```

### 4. Build & Run
```bash
mvn clean javafx:run
```
Or in IntelliJ: Run `AppInitializer.java`

---

## 🔑 Default Login Credentials

| Role    | Username | Password    |
|---------|----------|-------------|
| Admin   | admin    | Admin@1234  |

> The default admin account is auto-seeded on first launch if no users exist.

### Adding a Lecturer Account
1. Login as Admin
2. Go to **User Management** (sidebar)
3. Create a new user with role **LECTURER**

---

## Security

- Passwords hashed with **BCrypt** (never stored in plain text)
- Role-based access: Admin sees User Management; Lecturer does not
- Session management via in-memory `SessionManager`

---

## 📝 Notes

- Press **F11** to toggle fullscreen mode
- All IDs are auto-generated (e.g., STU001, C001, SES001)
- Attendance records are **upserted** — marking the same session twice updates the existing record




##  Features

### 🏠 Dashboard
- Summary cards: Total Courses, Students, Lecturers, Sessions
- Quick action tiles for common tasks
- Live date/time clock

### 📚 Course Management
- Add, edit, delete courses with code and duration
- Live search filtering

### 📖 Subject Management
- Subjects linked to courses
- Credit hour tracking

### 👨‍🎓 Student Management
- Auto-generated student ID and registration number
- Date of birth, contact details, course assignment
- Search by name, reg number, or email

### 👨‍🏫 Lecturer Management
- Qualification and department tracking
- Active/inactive status

### 🗓️ Class Scheduling
- Schedule sessions with date, time (HH:mm), and venue
- Filter by course → subject → lecturer

### ✅ Attendance Marking
- Select course → subject → session
- Inline ComboBox to mark each student: PRESENT / ABSENT / LATE / EXCUSED
- Bulk "All Present" / "All Absent" buttons
- Live summary counter (Present / Absent / Late / Total)
- Save all in one click

### 📊 Attendance Reports
- Filter by: All / By Student / By Course / By Date Range
- Colour-coded status cells (green/red/orange)
- Attendance percentage calculation
- **Export to CSV**

---
<img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/3890623b-17ed-48ef-8765-5a673fc83597" />
<img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/67ca7dd2-af4c-46b8-9cd6-813f08d26e7c" />
<img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/244e03ba-bf76-4e55-b455-b2070c63bdae" />
<img width="1600" height="867" alt="image" src="https://github.com/user-attachments/assets/c747e5bd-dd7b-49fe-a976-f5770930c47a" />
<img width="1600" height="866" alt="image" src="https://github.com/user-attachments/assets/d35d9eb1-dfe2-4421-90d2-84188bcf049e" />
<img width="1364" height="739" alt="image" src="https://github.com/user-attachments/assets/39397ed1-3142-41c3-8496-a1aca6fc7257" />


## 🎨 UI Theme

- **Primary color**: Deep Red (`#7f1d1d` → `#ef4444`)
- **Background**: Clean White / Light Gray (`#f9fafb`)
- **Accent**: Red gradient buttons with drop shadows
- All cards use **rounded corners** and **subtle drop shadows**
- Fully **responsive layout** — sidebar + content area

---










