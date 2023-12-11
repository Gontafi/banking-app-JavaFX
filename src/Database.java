import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Database {
    public static final String JDBC_URL = "jdbc:sqlite:temp.db";

    public Database() {
    }
    public static List<UserData> getAllUsers() {
        List<UserData> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String selectDataSQL = "SELECT * FROM UserData;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        int isAdmin = resultSet.getInt("IsAdmin");
                        int isManager = resultSet.getInt("IsManager");
                        double balance = resultSet.getDouble("balance");
                        double debt = resultSet.getDouble("debt");

                        UserData user = new UserData(username, password, isAdmin == 1, isManager == 1, debt);
                        user.setBalance(balance);

                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS UserData ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password TEXT NOT NULL,"
                    + "IsAdmin INTEGER NOT NULL,"
                    + "IsManager INTEGER NOT NULL,"
                    + "debt REAL NOT NULL DEFAULT 0,"
                    + "balance REAL NOT NULL);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertInitialDataUser() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String checkUserExistsSQL = "SELECT COUNT(*) FROM UserData WHERE username = ?";
            try (PreparedStatement checkUserExistsStmt = connection.prepareStatement(checkUserExistsSQL)) {
                checkUserExistsStmt.setString(1, "admin");
                try (ResultSet resultSet = checkUserExistsStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        System.out.println("Skipping insertion.");
                    } else {
                        String insertDataSQL = "INSERT INTO UserData (username, password, IsAdmin, IsManager, balance) VALUES (?, ?, ?, ?, ?);";

                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
                            preparedStatement.setString(1, "admin");
                            preparedStatement.setString(2, "123");
                            preparedStatement.setInt(3, 1);
                            preparedStatement.setInt(4, 0);
                            preparedStatement.setDouble(5, 0.0);
                            preparedStatement.execute();

                            preparedStatement.setString(1, "manager");
                            preparedStatement.setString(2, "123");
                            preparedStatement.setInt(3, 0);
                            preparedStatement.setInt(4, 1);
                            preparedStatement.setDouble(5, 0.0);
                            preparedStatement.execute();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertUserData(UserData user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            try {
                String insertDataSQL = "INSERT INTO UserData (username, password, IsAdmin, IsManager, balance) VALUES (?, ?, ?, ?, ?);";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.setString(2, user.getPassword());
                    preparedStatement.setInt(3, user.isAdmin() ? 1 : 0);
                    preparedStatement.setInt(4, user.isManager() ? 1 : 0);
                    preparedStatement.setDouble(5, user.getBalance());
                    preparedStatement.execute();
                    System.out.println("User '" + user.getUsername() + "' inserted successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void createTableTransaction() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS TransactionUser ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "amount REAL NOT NULL,"
                    + "from_account TEXT NOT NULL,"
                    + "to_account TEXT NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "time TEXT NOT NULL);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(UserData userData) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String updateUserSQL = "UPDATE UserData SET password=?, IsAdmin=?, IsManager=?, balance=? , debt=? WHERE username=?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL)) {
                preparedStatement.setString(1, userData.getPassword());
                preparedStatement.setInt(2, userData.isAdmin() ? 1 : 0);
                preparedStatement.setInt(3, userData.isManager() ? 1 : 0);
                preparedStatement.setDouble(4, userData.getBalance());
                preparedStatement.setString(5, String.valueOf(userData.debt));
                preparedStatement.setString(6, userData.getUsername());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User '" + userData.getUsername() + "' updated successfully.");
                } else {
                    System.out.println("User '" + userData.getUsername() + "' not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static UserData getUserByUsername(String username) {
        UserData user = null;

        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String selectUserSQL = "SELECT * FROM UserData WHERE username = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        int isAdmin = resultSet.getInt("IsAdmin");
                        int isManager = resultSet.getInt("IsManager");
                        double balance = resultSet.getDouble("balance");
                        double debt = resultSet.getDouble("debt");

                        user = new UserData(username, password, isAdmin == 1, isManager == 1, debt);
                        user.setBalance(balance);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    public static boolean userExists(String username) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String checkUserExistsSQL = "SELECT COUNT(*) FROM UserData WHERE username = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(checkUserExistsSQL)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        return true; // User exists
                    } else {
                        return false; // User does not exist
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred, treat as if user does not exist
        }
    }

    public static void insertTransaction(Transaction tx) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        java.util.Date date = new Date(System.currentTimeMillis());

        String insertDataSQL = "INSERT INTO TransactionUser (amount, from_account, to_account, description, time) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            preparedStatement.setDouble(1, tx.amount);
            preparedStatement.setString(2, tx.from);
            preparedStatement.setString(3, tx.to);
            preparedStatement.setString(4, tx.description);
            preparedStatement.setString(5, formatter.format(date));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String selectDataSQL = "SELECT * FROM TransactionUser;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

                    while (resultSet.next()) {
                        double amount = resultSet.getDouble("amount");
                        String from = resultSet.getString("from_account");
                        String to = resultSet.getString("to_account");
                        String description = resultSet.getString("description");
                        String time = resultSet.getString("time");

                        Transaction transaction = new Transaction(amount, from, to, description, formatter.parse(time));
                        transactions.add(transaction);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}

