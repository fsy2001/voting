package com.tuanyi.voting.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private String ip;
    private Integer nomineeId;
    private Date voteTime;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getNomineeId() {
        return nomineeId;
    }

    public void setNomineeId(Integer nomineeId) {
        this.nomineeId = nomineeId;
    }

    public Date getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Date voteTime) {
        this.voteTime = voteTime;
    }
}
