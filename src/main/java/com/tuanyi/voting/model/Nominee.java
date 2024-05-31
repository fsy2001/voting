package com.tuanyi.voting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nominee")
public class Nominee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String userId;
    public NominationType type;
    public String name;
    public Integer votes;
    public String contact;
    public String pic;
    public NominationState state;
    public String rejectReason;
    public String intro;
    public String reason;

    public String picPath() {
        return "/images/" + this.pic;
    }
}