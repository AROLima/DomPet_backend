package com.dompet.api.common.errors;

import java.util.List;

public class InsufficientStockException extends RuntimeException {
    private final List<String> details;
    public InsufficientStockException(String message, List<String> details) {
        super(message);
        this.details = details;
    }
    public List<String> getDetails() { return details; }
}
