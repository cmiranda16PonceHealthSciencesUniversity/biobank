package edu.ualberta.med.biobank.server.applicationservice.exceptions;


public class DuplicatePropertySetException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicatePropertySetException(String message) {
        super(message);
    }
}
