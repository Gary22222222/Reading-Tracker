Reading Activity Management System
1. Overview

The Reading Activity Management System is designed to help users record and manage their reading activities, including book or article titles, authors, reading time, notes, and more. The system also supports administrators in removing logs that contain inappropriate content.

The system is developed using Spring Boot, relies on Spring Data JPA for data persistence, and exposes RESTful APIs for interaction with the frontend.

2. Main Components

This module includes the following core components:

ReadingLog (Entity): Represents a user’s reading log record.

ReadingLogDto (Data Transfer Object): Handles incoming data from frontend requests.

ReadingLogRepository (Repository Layer): Provides database access and query capabilities.

ReadingLogService (Service Layer): Implements the business logic for creating, retrieving, updating, and deleting logs.

ReadingLogController (Controller): Exposes REST API endpoints to the frontend.

ViolationLog (Entity): Records logs that administrators delete due to inappropriate content.

ViolationLogRepository (Repository): Stores violation log records.

3. Code Structure
com.group20.dailyreadingtracker.readinglog
│── ReadingLog.java               // Reading log entity
│── ReadingLogDto.java            // Reading log DTO
│── ReadingLogRepository.java     // Repository layer
│── ReadingLogService.java        // Business logic layer
│── ReadingLogController.java     // REST controller
│
com.group20.dailyreadingtracker.violationlog
│── ViolationLog.java             // Violation log entity
│── ViolationLogRepository.java   // Violation log repository
│
com.group20.dailyreadingtracker.user
│── User.java                     // User entity
│── UserRepository.java           // User repository

4. Detailed Functionality
4.1 Reading Log Management
Create a Reading Log

Method: POST /api/reading-logs
Description: Allows a user to create a new reading log.

Request Body (JSON):

{
  "title": "Spring Boot Basics",
  "author": "John Doe",
  "date": "2024-03-27",
  "timeSpent": 60,
  "notes": "Learned foundational Spring Boot concepts."
}


Response:

{
  "id": 1,
  "message": "Reading log created successfully"
}

Get All Logs for Current User

Method: GET /api/reading-logs
Description: Retrieves all reading logs belonging to the current user.

Get a Specific Log

Method: GET /api/reading-logs/{logId}
Description: Retrieves details of a specific reading log.

Update a Reading Log

Method: PUT /api/reading-logs/{logId}
Description: Updates the content of an existing reading log.

Delete a Reading Log

Method: DELETE /api/reading-logs/{logId}
Description: Only the owner of the log is allowed to delete it.

4.2 Administrator Features
Get All Logs

Method: GET /api/reading-logs/all
Description: Allows administrators to view all logs in the system.

Delete an Inappropriate Log

Method: DELETE /api/admin/reading-logs/{logId}
Description: Allows administrators to delete logs containing inappropriate content and record them in the violation log table.

Process:

Retrieve the log from the database.

Create and save a ViolationLog entry.

Delete the original reading log.

Code Example:

public void deleteInappropriateLog(Long logId) {
    ReadingLog log = readingLogRepository.findById(logId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found"));

    // Record violation log
    ViolationLog violationLog = new ViolationLog();
    violationLog.setLogId(log.getId());
    violationLog.setReason("Inappropriate content");
    violationLogRepository.save(violationLog);

    // Delete the reading log
    readingLogRepository.delete(log);
}

5. Exception Handling

The system includes global exception handling mechanisms:

Log Not Found

{ "error": "Reading log not found" }


Access Denied

{ "error": "Access denied" }


Invalid Log ID

{ "error": "Invalid log ID format" }

6. Conclusion

The system provides a complete suite of reading log management features, including creating, viewing, updating, and deleting user logs. It also enables administrators to manage and remove inappropriate content effectively. All APIs follow RESTful design principles, and Spring Data JPA ensures reliable and consistent data persistence.
