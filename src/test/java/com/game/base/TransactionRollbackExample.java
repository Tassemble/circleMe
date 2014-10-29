package com.game.base;
 
import java.sql.*;
 
public class TransactionRollbackExample {
    private static final String url = "jdbc:mysql://198.100.99.235:3306/wordpress";
    private static final String username = "chq";
    private static final String password = "chq$1234";
 
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);
 
            String query = "INSERT INTO IDGenerator (ID) " +
                           "VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, 1000023);
            stmt.execute();
 
            ResultSet keys = stmt.getGeneratedKeys();
            int id = 1;
            if (keys.next()) {
                id = keys.getInt(1);
            }
 
            //
            // This is an invalid statement that will cause exception to 
            // demonstrate a rollback.
            //
            query = "INSERT INTO order_details (order_id, product_id, " +
                    "quantity, price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(query);
            detailStmt.setInt(1, id);
            detailStmt.setString(2, "P0000001");
            detailStmt.setInt(3, 10);
            detailStmt.setDouble(4, 100);
            detailStmt.execute();
 
            //
            // Commit transaction to mark it as a success database operation
            //
            conn.commit();
            System.out.println("Transaction commit...");
        } catch (SQLException e) {
            //
            // Rollback any database transaction due to exception occurred
            //
            if (conn != null) {
                conn.rollback();
                System.out.println("Transaction rollback...");
            }
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
    }
}
