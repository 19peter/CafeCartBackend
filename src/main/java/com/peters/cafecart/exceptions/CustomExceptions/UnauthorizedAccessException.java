package com.peters.cafecart.exceptions.CustomExceptions;

public class UnauthorizedAccessException extends  RuntimeException{
    public UnauthorizedAccessException(String err) {
        super(err);
    }
}
