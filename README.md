# 🧳 Tours and Travels - Trip Planner & Booking System

A **JavaFX-based Tours and Travels application** that allows users to:
- Plan **one-way** or **round trips** between cities.
- View **fare details** with a user-friendly GUI.
- Fill a **booking form** with personal & travel details.
- Upload payment slips and confirm booking.
- Store booking details into a **MySQL database**.

---

## 🚀 Features
- 🎯 **City Selection** – Choose start and destination cities from a list.
- 🛣 **Trip Planner** – Supports both one-way and round trips.
- 💰 **Fare Calculation** – Displays estimated fares dynamically.
- 📝 **Booking Form** – Collects user details (Name, Email, Phone, Aadhar, Age, Gender, Date).
- 💳 **Payment Integration (Mock)** – Upload payment slip and QR code for payment.
- 💾 **Database Connectivity** – Stores booking details in MySQL.
- 🎨 **Modern UI** – Built with JavaFX, includes hover effects, shadows, and animations.

---

## 🛠️ Technologies Used
- **Java (JavaFX)** – For GUI development
- **MySQL** – For storing booking data
- **JDBC** – For database connection
- **FXML & CSS Styling** – For UI customization


Travel-Planner/
│
├── src/travelp/
│ ├── TripTester.java # Main JavaFX application
│ ├── DBConnection.java # Database connection class
│ ├── RoundTripPlanner.java # Handles trip & fare calculation
│ └── ...
│
├── Resources/
│ ├── cities.txt # List of cities
│ ├── connections.txt # Distance/fare connections
│ └── image/pay.jpg # QR code for payment
│
└── README.md


CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    aadhar VARCHAR(20),
    gender VARCHAR(10),
    age INT,
    travel_date DATE,
    departure_city VARCHAR(50),
    destination_city VARCHAR(50),
    round_trip_fare DOUBLE,
    one_way_fare DOUBLE,
    slip_file_name VARCHAR(255)
);

---

## 📂 Project Structure
