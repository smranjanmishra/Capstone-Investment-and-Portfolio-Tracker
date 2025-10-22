package com.zeta.backend.enums;

public enum RiskLevel {
    LOW(1, "Low Risk", "Suitable for conservative investors"),
    MEDIUM(2, "Medium Risk", "Balanced risk-return profile"),
    HIGH(3, "High Risk", "Suitable for aggressive investors");

    private final int score;
    private final String displayName;
    private final String description;

    RiskLevel(int score, String displayName, String description) {
        this.score = score;
        this.displayName = displayName;
        this.description = description;
    }

    public int getScore() {
        return score;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static RiskLevel fromString(String value) {
        for (RiskLevel level : RiskLevel.values()) {
            if (level.name().equalsIgnoreCase(value) ||
                    level.displayName.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid risk level: " + value);
    }
}
