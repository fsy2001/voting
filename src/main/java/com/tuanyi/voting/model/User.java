package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String username;
    public Integer leftVotes;
    public Date lastVote;
    public Boolean isAdmin;
}