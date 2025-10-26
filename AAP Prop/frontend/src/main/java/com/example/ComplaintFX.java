package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Ignores any extra fields from backend JSON
public class ComplaintFX {
    private Long id;
    private String title;
    private String status;
    private String date; // we'll just keep it String for now
    private String autoComment; // 🆕 new field for AI response

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getAutoComment() { return autoComment; }  // ✅ getter
    public void setAutoComment(String autoComment) { this.autoComment = autoComment; }  // ✅ setter
}