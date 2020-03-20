package com.google.firebase;

public class FirebaseException extends Exception {
    public FirebaseException() {
    }

    public FirebaseException(String message) {
        super(message);
    }

    public FirebaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirebaseException(Throwable cause) {
        super(cause);
    }

    public FirebaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
