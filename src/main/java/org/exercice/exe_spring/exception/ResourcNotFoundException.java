package org.exercice.exe_spring.exception;

public class ResourcNotFoundException extends RuntimeException{
    public ResourcNotFoundException(String message) {
        super (message);
    }
}
