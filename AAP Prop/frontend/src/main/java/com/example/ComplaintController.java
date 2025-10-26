package com.example;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Citizen submits complaint ✅ now with autoComment auto-filled
    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        Complaint saved = complaintService.saveComplaint(complaint);

        // 🔹 Generate auto-response and update autoComment
        String autoComment = complaintService.generateAutoComment(saved.getDescription());
        saved.setAutoComment(autoComment);

        return complaintService.updateComplaintAutoComment(saved.getId(), autoComment);
    }

    // Get complaints by username
    @GetMapping("/user/{username}")
    public List<Complaint> getComplaintsByUser(@PathVariable String username) {
        return complaintService.getComplaintsByUsername(username);
    }

    // Admin: view all complaints
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // Admin: update complaint status
    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @RequestParam String status) {
        return complaintService.updateComplaintStatus(id, status);
    }

    // ✅ Admin/system can update autoComment separately
    @PutMapping("/{id}/auto-comment")
    public Complaint updateAutoComment(@PathVariable Long id, @RequestParam String autoComment) {
        return complaintService.updateComplaintAutoComment(id, autoComment);
    }
}