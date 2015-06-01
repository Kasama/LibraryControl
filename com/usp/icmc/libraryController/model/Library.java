package com.usp.icmc.libraryController.model;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class {@code Library} represents a library and manages users, books and
 * rentals. As well as blacklisting late users and saving a representation of
 * itself on the disk
 *
 * @see com.usp.icmc.libraryController.model.TimeController
 * @see com.usp.icmc.libraryController.model.Book
 * @see com.usp.icmc.libraryController.model.User
 */
public class Library implements Observer {

    private ArrayList<User> users;
    private ArrayList<Book> books;
    private Map<User, Date> blacklistStart;
    private Map<User, Date> blacklist;
    private TimeController timeController;
    private String dataDirectory;
    private long id;

    /**
     * Constructs a {@code library} that will use the directory specified to
     * store its information
     *
     * @param dataDirectory the directory in which to store the library's
     *                      information
     */
    public Library(String dataDirectory) {

        books = new ArrayList<>();
        users = new ArrayList<>();
        blacklist = new HashMap<>();
        blacklistStart = new HashMap<>();
        timeController = TimeController.getInstance();
        timeController.addObserver(this);
        this.dataDirectory = dataDirectory;

        loadFromDataDirectory(dataDirectory);
        update(null, null);

    }

    /**
     * Loads {@code library}'s information from the specified directory.
     * if the directory does not contain any {@code library}'s information,
     * the needed files are created inside of it.
     *
     * @param dataDirectory a string representing a directory's path.
     * @throws IllegalArgumentException if the string does not represent a
     *                                  directory.
     */
    private synchronized void loadFromDataDirectory(String dataDirectory) {

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        if (file.exists() && !file.isDirectory())
            throw new IllegalArgumentException(
                "argument is not a directory path"
            );

        if (
            !booksFile.exists() ||
            !blacklistFile.exists() ||
            !usersFile.exists()
            )
        {
            try {
                usersFile.createNewFile();
                booksFile.createNewFile();
                blacklistFile.createNewFile();
                booksDirectory.mkdir();
                usersDirectory.mkdir();
            } catch (IOException e) {
                System.err.println(
                    "Could not interact with filesystem, maybe it's read-only!"
                );
                System.exit(1);
            }
        } else {
            try {
                // reads books from the data file the entries are organized as
                // "Author","Title","canBeBorrowedByAnyone","ID","isAvailable"
                parseCSV(
                    booksFile,
                    tokens ->
                        addBook(
                            new Book(
                                tokens[1],
                                tokens[0],
                                Boolean.parseBoolean(tokens[2]),
                                Long.parseLong(tokens[3]),
                                Boolean.parseBoolean(tokens[4])
                            )
                        )
                );
                // read all users
                parseCSV(
                    usersFile,
                    tokens -> {
                        User user = new User(
                            tokens[0],
                            Long.parseLong(tokens[3]),
                            Integer.parseInt(tokens[1])
                        );
                        user.setBorrowExpired(Boolean.getBoolean(tokens[2]));
                        addUser(user);
                    }
                );
                // fill in books borrow logs
                for (Book book : books) {
                    long bookId = book.getId();
                    File bookFile = new File(
                        booksDirectory.getPath() + "/" + bookId + "Log.csv"
                    );

                    parseCSV(
                        bookFile,
                        tokens ->
                            book.getBorrowLog().add(
                                new BorrowedLog(
                                    getUser(
                                        Integer.parseInt(tokens[0])
                                    ), new Date(Long.parseLong(tokens[1])),
                                    new Date(Long.parseLong(tokens[2]))
                                )
                            )
                    );
                }
                // add borrowed books for each user
                for (User user : users) {
                    long userId = user.getId();
                    File userFile = new File(
                        usersDirectory.getPath() + "/" + userId + ".csv"
                    );

                    parseCSV(
                        userFile,
                        tokens -> {
                            if (doesBookExist(Long.parseLong(tokens[0])))
                                user.borrowBook(
                                    getBook(Long.parseLong(tokens[0]))
                                );
                        }
                    );
                }
                // read blacklist
                parseCSV(
                    blacklistFile,
                    tokens ->
                        addToBlacklist(
                            getUser(Integer.parseInt(tokens[0])),
                            Long.parseLong(tokens[1]),
                            Long.parseLong(tokens[2])
                        )
                );
            } catch (IOException e) {
                System.err.println(
                    "Could not interact with filesystem, maybe it's read-only!"
                );
                System.exit(1);
            }
        }
    }

    /**
     * Stores this {@code Library}'s information to the specified directory.
     *
     * @param dataDirectory a string representing a directory's path.
     * @throws IllegalArgumentException if the string does not represent a
     *                                  directory.
     */
    public synchronized void storeToDataDirectory(String dataDirectory) {

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        if (file.exists() && !file.isDirectory())
            throw new IllegalArgumentException(
                "argument is not a directory path"
            );

        CSVWriter csvWriter;
        try {
            csvWriter = new CSVWriter(new FileWriter(blacklistFile));
            final CSVWriter blackListWriter = csvWriter;
            blacklist.forEach(
                (user, dateDifference) -> {
                    String[] nextLine;
                    Date startDate;
                    startDate = blacklistStart.get(user);
                    nextLine = new String[3];
                    nextLine[0] = String.valueOf(user.getId());
                    nextLine[1] = String.valueOf(startDate.getTime());
                    nextLine[2] = String.valueOf(dateDifference.getTime());
                    blackListWriter.writeNext(nextLine);
                }
            );
            csvWriter.flush();

            csvWriter = new CSVWriter(new FileWriter(usersFile));
            for (User user : users) {
                File userFile = new File(
                    usersDirectory.getPath() + "/" + user.getId() + ".csv"
                );
                CSVWriter writer = new CSVWriter(new FileWriter(userFile));
                ArrayList<Book> borrowedBooks;
                borrowedBooks = user.getBorrowedBooks();
                for (Book book : borrowedBooks) {
                    String[] nextLine = new String[1];
                    nextLine[0] = String.valueOf(book.getId());
                    writer.writeNext(nextLine);
                }
                writer.flush();
                String[] nextLine = new String[4];
                nextLine[0] = user.getName();
                nextLine[1] = String.valueOf(user.getType());
                nextLine[2] = String.valueOf(user.isBorrowExpired());
                nextLine[3] = String.valueOf(user.getId());
                csvWriter.writeNext(nextLine);
            }
            csvWriter.flush();

            csvWriter = new CSVWriter(new FileWriter(booksFile));
            for (Book book : books) {
                CSVWriter writer = new CSVWriter(
                    new FileWriter(
                        new File(
                            booksDirectory.getPath() + "/" + book.getId() +
                            "Log.csv"
                        )
                    )
                );
                for (
                    BorrowedLog borrowedLog :
                    book.getBorrowLog()
                    ) {
                    String[] nextLine = new String[3];
                    nextLine[0] = String.valueOf(borrowedLog.getUser().getId());
                    nextLine[1] = String
                        .valueOf(borrowedLog.getBorrowedDate().getTime());
                    nextLine[2] = String
                        .valueOf(borrowedLog.getReturnDate().getTime());
                    writer.writeNext(nextLine);
                }
                writer.flush();
                String[] nextLine = new String[5];
                nextLine[0] = book.getAuthor();
                nextLine[1] = book.getTitle();
                nextLine[2] = String.valueOf(book.canBeBorrowedByAnyone());
                nextLine[3] = String.valueOf(book.getId());
                nextLine[4] = String.valueOf(book.isAvailableForBorrow());
                csvWriter.writeNext(nextLine);
            }
            csvWriter.flush();

        } catch (IOException e) {
            System.err.println(
                "Could not interact with filesystem, maybe it's read-only!"
            );
            System.exit(1);
        }

    }

    /**
     * Removes the user from the blacklist.
     *
     * @param user the user to be removed from the blacklist.
     * @see com.usp.icmc.libraryController.model.Library#addBlacklistTime
     * @see com.usp.icmc.libraryController.model.Library#addToBlacklist
     */
    public void removeUserFromBlacklist(User user) {
        blacklist.remove(user);
        user.setBorrowExpired(false);
    }

    /**
     * Parses a file as CSV and for every line, parse the tokens using the
     * {@code read} method of the {@link ReadableField} argument
     *
     * @param file the file to be parsed
     * @param rf   the ReadableField that implements the read method to be
     *             called for each line of tokens
     * @throws IOException if there is a problem while reading the file
     */
    private void parseCSV(File file, ReadableField rf)
        throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(file));
        String[] tokens;
        while ((tokens = csvReader.readNext()) != null) {
            rf.read(tokens);
        }

    }

    /**
     * Adds a user to the blacklist for a certain amount of time
     *
     * @param user           the user to be blacklisted
     * @param startTime      the starting date of the blacklist (Unix Timestamp)
     * @param timeDifference the time (in milliseconds) the user will be
     *                       blacklisted
     */
    private void addToBlacklist(
        User user, long startTime, long timeDifference
    ) {
        if (blacklist.containsKey(user)) {
            blacklistStart.replace(user, new Date(startTime));
            blacklist.replace(user, new Date(timeDifference));
        } else {
            blacklistStart.put(user, new Date(startTime));
            blacklist.put(user, new Date(timeDifference));
        }
        user.setBorrowExpired(true);
    }

    /**
     * Gets a user from its ID.
     * Should be used in conjunction with {@link Library#doesUserExists(long)}
     * <p>
     * Example code:
     * <code>
     * if (library.doesUserExist(id)){
     * user = library.getUser(id);
     * }
     * </code>
     *
     * @param id the ID to be searched for
     * @return the found user
     * @throws noUserFoundException if the user was not found
     * @see com.usp.icmc.libraryController.model.Library#doesUserExists(long)
     */
    public User getUser(long id) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getId() == id
            )
            .findFirst();
        if (!u.isPresent()) throw new noUserFoundException(id);
        return u.get();
    }

    /**
     * Gets a user from its name
     * Should be used in conjunction with {@link Library#doesUserExists(String)}
     * <p>
     * Example code:
     * <code>
     * if (library.doesUserExist(name)){
     * user = library.getUser(name);
     * }
     * </code>
     *
     * @param name the name to be searched for
     * @return the found user
     * @throws noUserFoundException if the user was not found
     * @see com.usp.icmc.libraryController.model.Library#doesUserExists(String)
     */
    public User getUser(String name) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getName().equals(name)
            )
            .findFirst();
        if (!u.isPresent()) throw new noUserFoundException(name);
        return u.get();
    }

    /**
     * Checks if the user exists
     *
     * @param name the name to be searched for
     * @return {@code true} if the user exists and {@code false} otherwise
     * @see com.usp.icmc.libraryController.model.Library#getUser(String)
     */
    public boolean doesUserExists(String name) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getName().equals(name)
            )
            .findFirst();
        return u.isPresent();
    }

    /**
     * Checks if the user exists
     *
     * @param id the id to be searched for
     * @return {@code true} if the user exists and {@code false} otherwise
     * @see com.usp.icmc.libraryController.model.Library#getUser(long)
     */
    public boolean doesUserExists(long id) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getId() == id
            )
            .findFirst();
        return u.isPresent();
    }

    /**
     * Adds a {@code User} to the list of users of this {@code library}
     *
     * @param user the user to be added
     */
    public void addUser(User user) {
        if (!users.contains(user))
            users.add(user);
    }

    /**
     * Adds a {@code Book} to the list of books of this {@code library}
     *
     * @param book the book to be added
     */
    public void addBook(Book book) {
        if (!books.contains(book))
            books.add(book);
    }

    /**
     * Check if a user can borrow certain book
     *
     * @param user the user to be checked
     * @param book the book to be checked
     * @return {@code true} if the user can borrow that book and {@code
     * false} otherwise
     */
    public boolean canBorrowBook(User user, Book book) {
        return user.canBorrowBook() && !isBlacklisted(user) &&
               book.isAvailableForBorrow() && !(
            (!book.canBeBorrowedByAnyone()) &&
            (user.getType() == User.COMMUNITY_MEMBER)
        );

    }

    /**
     * Borrow a {@code book} to a {@code user} if possible.
     * will return {@code false} if the {@code user} cannot borrow the {@code
     * book} for any reason
     *
     * @param user the user to receive the book
     * @param book the book to be borrowed
     * @return {@code true} if the book was borrowed, {@code false} otherwise
     */
    public boolean borrowBook(User user, Book book) {
        if (!user.canBorrowBook()) return false;
        if (isBlacklisted(user)) return false;
        if (!book.isAvailableForBorrow()) return false;
        if (
            (!book.canBeBorrowedByAnyone()) &&
            (user.getType() == User.COMMUNITY_MEMBER)
            ) return false;

        user.borrowBook(book);
        book.setAvailableForBorrow(false);
        book.writeBorrowLog(user);

        return true;
    }

    /**
     * returns a {@code book} previously borrowed by {@code user}
     * will return {@code false} if the {@code user} does not have the {@code
     * book}
     *
     * @param user the user that has the book
     * @param book the book to be returned
     * @return {@code true} if the user had the book and {@code false} if not
     * @see com.usp.icmc.libraryController.model.User#hasBook(Book)
     */
    public boolean returnBook(User user, Book book) {
        if (user.hasBook(book)) {
            user.returnBook(book);
            book.setAvailableForBorrow(true);
            return true;
        }
        return false;
    }

    /**
     * Check if a book exists
     *
     * @param Author the author of the book
     * @param Title  the title of the book
     * @return {@code true} if the book exists and {@code false} otherwise
     */
    public boolean doesBookExist(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getAuthor().equals(Author))
            .filter(book -> book.getTitle().equals(Title))
            .findFirst();
        return b.isPresent();
    }

    /**
     * Check if a book exists
     *
     * @param id the id of the book
     * @return {@code true} if the book exists and {@code false} otherwise
     */
    public boolean doesBookExist(long id) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getId() == id)
            .findFirst();
        return b.isPresent();
    }

    /**
     * Finds a book by Author and Title.
     * Should be used in conjunction with {@link Library#doesBookExist
     * (String, String)}
     * Example code:
     * <code>
     * if (library.doesBookExist(author, title)){
     * book = getBook(author, title);
     * }
     * </code>
     *
     * @param Author the author to be searched for
     * @param Title  the tile to be searched for
     * @return the found book
     * @throws noBookFoundException if the book was not found
     */
    public Book getBook(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getAuthor().equals(Author))
            .filter(book -> book.getTitle().equals(Title))
            .findFirst();
        if (!b.isPresent()) throw new noBookFoundException(Author, Title);
        return b.get();
    }

    /**
     * Finds a book by id
     * Should be used in conjunction with {@link Library#doesBookExist
     * (long)}
     * Example code:
     * <code>
     * if (library.doesBookExist(id)){
     * book = getBook(id);
     * }
     * </code>
     *
     * @param id the id to be searched for
     * @return the found book
     * @throws noBookFoundException if the book was not found
     */
    public Book getBook(long id) {
        Optional<Book> b;
        b = books
            .stream()
            .filter(book -> book.getId() == id)
            .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    /**
     * Adds blacklist time to a user.
     * if the user is already blacklisted, time is added to the current time
     *
     * @param user the user to be blacklisted
     * @param time the time to be added in milliseconds
     */
    private void addBlacklistTime(User user, long time) {
        if (isBlacklisted(user)) {
            blacklist.replace(
                user,
                timeController.addTime(blacklist.get(user), time)
            );
        } else {
            addToBlacklist(user, timeController.getDate().getTime(), time);
        }
    }

    /**
     * Checks if a user is blacklisted or not.
     *
     * @param user the user to be checked for
     * @return {@code true} if the user is blacklisted and {@code false} if not
     */
    public boolean isBlacklisted(User user) {
        return blacklist.containsKey(user);
    }

    /**
     * Gets the data directory currently in use
     *
     * @return the path to the directory
     */
    public String getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Gets a list of all registered users
     *
     * @return a ArrayList of all register users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Removes a {@code user} from the list of registered users
     *
     * @param id the id of the user to be removed
     */
    public void removeUser(long id) {
        users.remove(getUser(id));
    }

    /**
     * Gets a list of all registered books
     *
     * @return a ArrayList of all registered books
     */
    public ArrayList<Book> getBooks() {
        return books;
    }

    /**
     * Removes a {@code book} from the list of registered books
     *
     * @param id the id of the book to be removed
     */
    public void removeBook(long id) {
        books.remove(getBook(id));
    }

    /**
     * this method is called to update the blacklist when the day changes.
     * the {@link TimeController} object notifies when the day changes
     *
     * @param ignored1
     * @param ignored2
     * @see com.usp.icmc.libraryController.model.TimeController
     */
    @Override
    public void update(Observable ignored1, Object ignored2) {
        Date today = timeController.getDate();
        for (User user : users) {
            if (blacklist.containsKey(user)) {
                Date expiration;
                expiration = new Date(
                    blacklistStart.get(user).getTime() +
                    blacklist.get(user).getTime()
                );
                if (expiration.before(today))
                    removeUserFromBlacklist(user);
            }
            user.getBorrowedBooks().stream()
                .filter(book -> book.getBorrowLog().size() != 0)
                .forEach(
                    book -> {
                        int pos = book.getBorrowLog().size();
                        BorrowedLog log = book.getBorrowLog().get(pos - 1);
                        Date returnDate = log.getReturnDate();
                        int retCmpToday = returnDate.compareTo(today);
                        if (retCmpToday < 0) {
                            long difference = today.getTime() -
                                              returnDate.getTime();
                            addBlacklistTime(user, difference);
                            new Thread(
                                () -> storeToDataDirectory(dataDirectory)
                            ).start();
                        }
                    }
                );
        }
    }
}
