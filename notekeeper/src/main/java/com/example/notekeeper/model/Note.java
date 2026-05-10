package com.example.notekeeper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private String text;

    @Enumerated(EnumType.STRING)
    private Category category;

    public Note() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}