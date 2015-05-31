package com.usp.icmc.libraryController.model;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Library implements Observer {

    private ArrayList<User> users;
    private ArrayList<Book> books;
    private Map<User, Date> blacklistStart;
    private Map<User, Date> blacklist;
    private TimeController timeController;
    private String dataDirectory;

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

    private synchronized void loadFromDataDirectory(String dataDirectory) {

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

        if (file.exists() && !file.isDirectory())
            throw new IllegalArgumentException(
                "argument is not a dataDirectory path"
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

    public synchronized void storeToDataDirectory(String dataDirectory) {

        File file = new File(dataDirectory);
        File booksDirectory = new File(dataDirectory + "/books");
        File usersDirectory = new File(dataDirectory + "/users");
        File booksFile = new File(dataDirectory + "/books.csv");
        File usersFile = new File(dataDirectory + "/users.csv");
        File blacklistFile = new File(dataDirectory + "/blacklist.csv");

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

    public void removeUserFromBlacklist(User user) {
        blacklist.remove(user);
        user.setBorrowExpired(false);
    }

    private void parseCSV(File file, ReadableField rf)
        throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(file));
        String[] tokens;
        while ((tokens = csvReader.readNext()) != null) {
            rf.read(tokens);
        }

    }

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

    public User getUser(long id) {
        Optional<User> u;
        u = users
            .stream()
            .parallel()
            .filter(
                uid -> uid.getId() == id
            )
            .findFirst();
        return u.get();
    }

    public void addUser(User user) {
        if (!users.contains(user))
            users.add(user);
    }

    public void addBook(Book book) {
        if (!books.contains(book))
            books.add(book);
    }

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

    public boolean returnBook(User user, Book book) {
        if (user.hasBook(book)) {
            user.returnBook(book);
            book.setAvailableForBorrow(true);
            return true;
        }
        return false;
    }

    public boolean doesBookExist(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getAuthor().equals(Author))
            .filter(book -> book.getTitle().equals(Title))
            .filter(Book::isAvailableForBorrow)
            .findFirst();
        return b.isPresent();
    }

    public boolean doesBookExist(long id) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getId() == id)
            .findFirst();
        return b.isPresent();
    }

    public Book getBook(String Author, String Title) {
        Optional<Book> b;
        b = books.stream()
            .filter(book -> book.getAuthor().equals(Author))
            .filter(book -> book.getTitle().equals(Title))
            .filter(Book::isAvailableForBorrow)
            .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

    public Book getBook(long id) {
        Optional<Book> b;
        b = books
            .stream()
            .filter(book -> book.getId() == id)
                //            .filter(Book::isAvailableForBorrow)
            .findFirst();
        if (!b.isPresent()) throw new noBookFoundException();
        return b.get();
    }

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

    private boolean isBlacklisted(User user) {
        return blacklist.containsKey(user);
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void removeUser(long id) {
        users.remove(getUser(id));
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void removeBook(long id) {
        books.remove(getBook(id));
    }

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
