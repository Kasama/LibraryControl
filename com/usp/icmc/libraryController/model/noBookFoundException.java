package com.usp.icmc.libraryController.model;

/**
 * Thrown to indicate that the searched {@code Book} was not found.
 *
 * @see com.usp.icmc.libraryController.model.Library#getBook(long)
 * @see com.usp.icmc.libraryController.model.Library#getBook(String, String)
 */
public class noBookFoundException extends RuntimeException {

    /**
     * Constructs a {@code noBookFoundException} with no detail message
     */
    public noBookFoundException() {
        super();
    }

    /**
     * constructs a {@code noBookFoundException} with the not found book id
     * as message
     *
     * @param book the invalid book id
     */
    public noBookFoundException(long book) {
        super("book " + book + " not found");
    }

    /**
     * constructs a {@code noBookFoundException} with the not found book author
     * and title as message
     *
     * @param title  the invalid book title
     * @param author the invalid book author
     */
    public noBookFoundException(String author, String title) {
        super(
            "book with author: " + author + "and title: " + title + " not found"
        );
    }
}


