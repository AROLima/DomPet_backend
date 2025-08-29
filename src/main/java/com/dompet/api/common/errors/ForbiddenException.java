package com.dompet.api.common.errors;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
