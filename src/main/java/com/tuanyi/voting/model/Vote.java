package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String ip;
    public Integer songId;
    public LocalDateTime voteTime;
}
