package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private String username;
    private Integer leftvotes;
    private Date lastVote;
    private Boolean isAdmin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLeftvotes() {
        return leftvotes;
    }

    public void setLeftvotes(Integer leftvotes) {
        this.leftvotes = leftvotes;
    }

    public Date getLastVote() {
        return lastVote;
    }

    public void setLastVote(Date lastVote) {
        this.lastVote = lastVote;
    }


    public void setAdmin(Boolean admin) {
        this.isAdmin = admin;
    }

    public Boolean isAdmin() {
        return this.isAdmin;
    }
}