package com.usp.icmc.libraryController.model;

import java.io.File;

/**
 * The {@code ReadableField} interface should be implemented by any class
 * whose instances are meant to specify a way to user CSV file tokens for
 * each line.
 * This interface is used by {@link Library#parseCSV(File, ReadableField)}
 * and the method {@code read} is called with the line tokens as arguments
 * and should handle what to do with them
 *
 * @see Library#parseCSV(File, ReadableField)
 */
@FunctionalInterface
interface ReadableField {
    /**
     * This method should be implemented by any class whose instances will
     * specify a way to handle CSV tokens for every line in that CSV file
     *
     * @param tokens the CSV file tokens that should be handled
     */
    void read(String[] tokens);
}
