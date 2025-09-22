package com.peters.cafecart.exceptions.CustomExceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String err) {
        super(err);
    }
}
