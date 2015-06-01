package com.usp.icmc.libraryController.model;

/**
 * Thrown to indicate that the searched {@code User} was not found.
 *
 * @see com.usp.icmc.libraryController.model.Library#getUser(long)
 * @see com.usp.icmc.libraryController.model.Library#getUser(String)
 */
public class noUserFoundException extends RuntimeException {

    /**
     * Constructs a {@code noUserFoundException} with no detail message
     */
    public noUserFoundException() {
        super();
    }

    /**
     * constructs a {@code noUserFoundException} with the not found user id
     * as message
     *
     * @param user the invalid user id
     */
    public noUserFoundException(long user) {
        super("user " + user + " not found");
    }

    /**
     * constructs a {@code noUserFoundException} with the not found user name
     * as message
     *
     * @param user the invalid user name
     */
    public noUserFoundException(String user) {
        super("user " + user + " not found");
    }
}

