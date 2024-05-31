package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public String ip;
    public Integer nomineeId;
    public Date voteTime;
}
