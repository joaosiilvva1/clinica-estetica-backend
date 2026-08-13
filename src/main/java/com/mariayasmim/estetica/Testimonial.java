package com.mariayasmim.estetica;

import jakarta.persistence.*;

@Entity
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientName;
    private int rating;
    private String comment;

    // Depoimento só aparece no site depois que a administradora aprovar.
    // Fica false por padrão (pendente) até a aprovação manual no painel admin.
    private boolean approved = false;

    // Gerar Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}