A full-stack web application built to simplify and digitalize the process of lodging, tracking, and resolving grievances. The platform allows users to submit complaints, monitor progress, and receive updates, while administrators efficiently manage and resolve issues through a centralized dashboard.

🚀 Features

👤 User
Register and log in securely.
Submit grievances with detailed descriptions and categories.
Track the real-time status of their complaints.
Receive updates when grievances are resolved or acted upon.

🛠️ Admin
View all submitted grievances in an intuitive dashboard.
Assign, update, and resolve complaints.
Manage users and maintain records for transparency.

🧰 Technologies Used
Backend: Java, Spring Boot
Frontend: Thymeleaf, HTML, CSS
Database: MySQL
Security: Spring Security (for authentication and role-based access)

⚙️ Installation & Setup
Prerequisites
Java 17+
Maven
MySQL

Steps

Clone the repository:
git clone https://github.com/yourusername/grievance-redressal-system.git
cd grievance-redressal-system


Configure Database:
Create a new MySQL database named grievance_db.

Update the credentials in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/grievance_db
spring.datasource.username=root
spring.datasource.password=yourpassword


Build and Run the Project:

mvn spring-boot:run


Access the Application:
Open your browser and go to:

http://localhost:8080
