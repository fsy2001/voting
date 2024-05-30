package com.tuanyi.voting.model;

public enum NominationType {
    SELF, OTHER;

    public String getTypeName() {
        return switch (this) {
            case SELF -> "自荐";
            case OTHER -> "推荐";
        };
    }
}
