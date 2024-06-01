package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String username;
    public Integer leftVotes;
    public LocalDateTime lastVote;
    public Boolean isAdmin;
}