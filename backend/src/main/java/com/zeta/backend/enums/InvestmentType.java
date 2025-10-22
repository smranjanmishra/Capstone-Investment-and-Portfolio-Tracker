package com.zeta.backend.enums;

public enum InvestmentType {
    STOCK("Stock", "Equity shares of a company"),
    MUTUAL_FUND("Mutual Fund", "Professionally managed investment fund"),
    BOND("Bond", "Fixed income debt security"),
    ETF("ETF", "Exchange Traded Fund"),
    REAL_ESTATE("Real Estate", "Property investment"),
    COMMODITY("Commodity", "Physical goods like gold, oil"),
    CRYPTOCURRENCY("Cryptocurrency", "Digital currency investment");

    private final String displayName;
    private final String description;

    InvestmentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // Accepts both enum name (e.g., "STOCK") and display name (e.g., "Stock") for flexible API input
    public static InvestmentType fromString(String value) {
        for (InvestmentType type : InvestmentType.values()) {
            if (type.name().equalsIgnoreCase(value) ||
                    type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        // Triggers GlobalExceptionHandler to return 400 Bad Request
        throw new IllegalArgumentException("Invalid investment type: " + value);
    }
}