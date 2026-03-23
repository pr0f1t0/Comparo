package com.pr0f1t.comparo.searchservice.exception;

public class ProductNotFoundInIndexException extends RuntimeException {
    public ProductNotFoundInIndexException(String message) {
        super(message);
    }
}
