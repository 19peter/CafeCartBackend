package com.peters.cafecart.exceptions.CustomExceptions;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String err) {
        super(err);
    }
}