package com.tuanyi.voting.model;

public enum NominationState {
    DRAFT, PENDING, APPROVED, REJECTED;

    public String getStatusName() {
        return switch (this) {
            case DRAFT -> "草稿";
            case PENDING -> "待审核";
            case APPROVED -> "已通过";
            case REJECTED -> "已拒绝";
        };
    }
}
