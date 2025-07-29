package ru.mazegen.controllers;

import lombok.Data;

/**
 * Helper class to be able to return verbose errors with GraphQL
 */
@Data
public class Error {
    private int code;
    private String description;

    public enum Code {
        NOT_EXISTS(228),
        BAD_FORMAT(230),
        INVALID_PARAMETERS(242);

        private final int val;
        Code(int val){this.val = val;}
    }

    public Error(Code code, String description) {
        this.code = code.val;
        this.description = description;
    }

}
