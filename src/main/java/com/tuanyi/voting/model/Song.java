package com.tuanyi.voting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String name;
    public String artist;
    public Integer votes;
    public NominationState state;
    public String rejectReason;
    public String reason;
}