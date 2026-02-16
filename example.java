import java.sql.*;
import java.util.Random;

public class example {

    private static final String URL_DB = "jdbc:mysql://localhost:3306/frauddb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "sriram3107";

    private static final String INSERT_QUERY =
            "INSERT INTO transactions " +
            "(txn_id, mobile_no, location, merchant, txn_type, amount, status, timestamp, ip_address, is_fraud) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final Random random = new Random();

    public static void main(String[] args) {

        int totalTransactions = 100;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);

            int inserted = 0;
            int rejected = 0;

            for (int i = 1; i <= totalTransactions; i++) {

                Transaction txn = generateTransaction(i);

                // 🔹 INTERNAL API CALL
                boolean apiValid = verifyTransaction(txn);

                if (!apiValid) {
                    rejected++;
                    System.out.println("❌ Rejected by API → " + txn.txnId);
                    continue;
                }

                ps.setString(1, txn.txnId);
                ps.setString(2, txn.mobileNo);
                ps.setString(3, txn.location);
                ps.setString(4, txn.merchant);
                ps.setString(5, txn.txnType);
                ps.setDouble(6, txn.amount);
                ps.setString(7, txn.status);
                ps.setTimestamp(8, txn.timestamp);
                ps.setString(9, txn.ipAddress);
                ps.setBoolean(10, txn.isFraud);

                ps.addBatch();
                inserted++;
            }

            ps.executeBatch();
            conn.commit();

            System.out.println("✅ Inserted: " + inserted);
            System.out.println("❌ Rejected by API: " + rejected);

            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TRANSACTION MODEL =================
    static class Transaction {
        String txnId, mobileNo, location, merchant, txnType, status, ipAddress;
        double amount;
        Timestamp timestamp;
        boolean isFraud;
    }

    // ================= TRANSACTION GENERATOR =================
    private static Transaction generateTransaction(int i) {

        Transaction t = new Transaction();

        t.txnId = "TXN" + System.currentTimeMillis() + i;
        t.mobileNo = "9" + (long)(Math.random() * 1000000000L);
        t.location = randomLocation();
        t.merchant = randomMerchant();
        t.txnType = randomTxnType();
        t.amount = Math.round((10 + (100000 - 10) * random.nextDouble()) * 100.0) / 100.0;
        t.status = "SUCCESS";
        t.timestamp = new Timestamp(System.currentTimeMillis());
        t.ipAddress = generateIP();
        t.isFraud = t.amount > 50000;

        return t;
    }

    // ================= INTERNAL API LOGIC =================
    private static boolean verifyTransaction(Transaction txn) {

        // 🔹 Null checks
        if (txn.txnId == null || txn.mobileNo == null || txn.location == null ||
            txn.merchant == null || txn.txnType == null || txn.ipAddress == null) {
            return false;
        }

        // 🔹 Mobile number validation (must be 10 digits and start with 6-9)
        if (!txn.mobileNo.matches("^[6-9][0-9]{9}$")) {
            return false;
        }

        // 🔹 Amount validation
        if (txn.amount <= 0) {
            return false;
        }

        // 🔹 Fraud rules (simulate real-time engine)
        if (txn.amount > 80000) {
            return false; // reject high-risk
        }

        if (txn.location.equals("Kolkata") && txn.amount > 20000) {
            return false;
        }

        return true; // ✅ valid transaction
    }

    // ================= HELPERS =================
    private static String generateIP() {
        return random.nextInt(255) + "." +
               random.nextInt(255) + "." +
               random.nextInt(255) + "." +
               random.nextInt(255);
    }

    private static String randomLocation() {
        String[] LOCATIONS = {"Chennai", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Kolkata"};
        return LOCATIONS[random.nextInt(LOCATIONS.length)];
    }

    private static String randomMerchant() {
        String[] MERCHANTS = {"Amazon", "Flipkart", "Swiggy", "Zomato", "Uber"};
        return MERCHANTS[random.nextInt(MERCHANTS.length)];
    }

    private static String randomTxnType() {
        String[] TYPES = {"UPI", "Debit Card", "Credit Card", "Net Banking", "Wallet"};
        return TYPES[random.nextInt(TYPES.length)];
    }
}
