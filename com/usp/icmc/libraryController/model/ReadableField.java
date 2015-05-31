package com.usp.icmc.libraryController.model;

@FunctionalInterface
interface ReadableField {
    void read(String[] tokens);
}
