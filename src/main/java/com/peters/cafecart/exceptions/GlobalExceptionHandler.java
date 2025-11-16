package com.peters.cafecart.exceptions;

import com.peters.cafecart.exceptions.CustomExceptions.NetworkConnectionException;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(NetworkConnectionException.class)
        public ResponseEntity<ErrorDetails> handleNetworkConnectionException(
                        NetworkConnectionException ex, WebRequest request) {

                ErrorDetails errorDetails = new ErrorDetails(
                                HttpStatus.SERVICE_UNAVAILABLE.value(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
        }

        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<ErrorDetails> handleUnauthorizedAccessException(
                        UnauthorizedAccessException ex, WebRequest request) {

                ErrorDetails errorDetails = new ErrorDetails(
                                HttpStatus.UNAUTHORIZED.value(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
                        ResourceNotFoundException ex, WebRequest request) {

                ErrorDetails errorDetails = new ErrorDetails(
                                HttpStatus.NOT_FOUND.value(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorDetails> handleValidationException(
                        ValidationException ex, WebRequest request) {

                ErrorDetails errorDetails = new ErrorDetails(
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage(),
                                request.getDescription(false));

                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorDetails> handleJsonParseError(HttpMessageNotReadableException ex) {
                ErrorDetails errorDetails = new ErrorDetails(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid request body",
                                extractMessage(ex));

                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        private String extractMessage(HttpMessageNotReadableException ex) {
                if (ex.getCause() != null) {
                        return ex.getCause().getMessage();
                }
                return ex.getMessage();
        }

}
