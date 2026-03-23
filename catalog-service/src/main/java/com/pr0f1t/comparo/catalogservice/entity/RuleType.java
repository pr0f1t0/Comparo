package com.pr0f1t.comparo.catalogservice.entity;

public enum RuleType {
    NUMERIC_GREATER_THAN,
    NUMERIC_GREATER_THAN_OR_EQUAL,
    NUMERIC_LESS_THAN,
    NUMERIC_LESS_THAN_OR_EQUAL,
    NUMERIC_EQUALS,


    EXACT_MATCH,
    KEYWORD_MATCH,
    NOT_CONTAINS,


    EXISTS,
    NOT_EXISTS
}
