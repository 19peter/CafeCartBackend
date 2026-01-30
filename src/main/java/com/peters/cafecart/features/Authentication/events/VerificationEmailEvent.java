package com.peters.cafecart.features.Authentication.events;

public record VerificationEmailEvent(
        String email,
        String token
) {}