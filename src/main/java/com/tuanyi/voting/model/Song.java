package com.tuanyi.voting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nominee")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String name;
    public String artist;
    public Integer votes;
    public String pic;
    public NominationState state;
    public String rejectReason;
    public String intro;
    public String reason;
}