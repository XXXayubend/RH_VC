package org.exercice.exe_spring.exception;

public class DuplicateTitreException extends RuntimeException{
    public DuplicateTitreException(String message) {
        super(message);
    }
}
