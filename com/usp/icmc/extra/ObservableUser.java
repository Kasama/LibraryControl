package com.usp.icmc.extra;

import com.usp.icmc.library.User;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ObservableUser {
    LongProperty ID;
    SimpleStringProperty name;
    SimpleIntegerProperty borrowed;
    SimpleStringProperty type;
    SimpleStringProperty status;

    public ObservableUser(User user) {
        this.ID = new SimpleLongProperty(user.getId());
        this.name = new SimpleStringProperty(user.getName());
        this.borrowed = new SimpleIntegerProperty(
            user.getBorrowedBooks().size()
        );
        switch (user.getType()) {
            case User.COMMUNITY_MEMBER:
                this.type = new SimpleStringProperty("Community Member");
                break;
            case User.PROFESSOR:
                this.type = new SimpleStringProperty("Professor");
                break;
            case User.STUDENT:
                this.type = new SimpleStringProperty("Student");
                break;
        }
        this.status = new SimpleStringProperty(
            user.isBorrowExpired() ? "Blacklisted" : "Clear"
        );
    }

    public long getID() {
        return ID.get();
    }

    public LongProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getBorrowed() {
        return borrowed.get();
    }

    public SimpleIntegerProperty borrowedProperty() {
        return borrowed;
    }

    public void setBorrowed(int borrowed) {
        this.borrowed.set(borrowed);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return getID() + ": " + getName();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
