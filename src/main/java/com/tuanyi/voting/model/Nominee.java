package com.tuanyi.voting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nominee")
public class Nominee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private NominationType type;
    private String name;
    private Integer votes;
    private String contact;
    private String pic;
    private NominationState state;
    private String rejectReason;
    private String intro;
    private String reason;

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

    public NominationType getType() {
        return type;
    }

    public void setType(NominationType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String picPath() {
        return "/images/" + this.pic;
    }

    public NominationState getState() {
        return state;
    }

    public void setState(NominationState state) {
        this.state = state;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String approveFuntion() {
        return "approveNominee('" + this.id + "')";
    }

    public String rejectFuntion() {
        return "rejectNominee('" + this.id + "')";
    }
}