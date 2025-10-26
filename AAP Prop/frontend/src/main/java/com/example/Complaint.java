package com.example;

import jakarta.persistence.*;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
    private String username; // 👈 linked to citizen/user

    // ✅ NEW field for AI/AutoResponse
    @Column(name = "auto_comment", columnDefinition = "TEXT")
    private String autoComment;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // ✅ AutoResponse comment field getter & setter
    public String getAutoComment() {
        return autoComment;
    }
    public void setAutoComment(String autoComment) {
        this.autoComment = autoComment;
    }
}