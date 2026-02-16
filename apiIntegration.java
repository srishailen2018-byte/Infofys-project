import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Random;

public class apiIntegration {

    private static final String URL_DB = "jdbc:mysql://localhost:3306/frauddb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "sriram3107";

    private static final String VERIFY_API_URL = "http://localhost:8080/verifyTransaction";

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

                // 🔹 CALL REAL-TIME API
                boolean apiValid = callVerificationAPI(txn);

                if (!apiValid) {
                    rejected++;
                    System.out.println(" API Rejected → " + txn.txnId);
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

            System.out.println(" Inserted: " + inserted);
            System.out.println(" Rejected by API: " + rejected);

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

    private static Transaction generateTransaction(int i) {

        Transaction t = new Transaction();

        t.txnId = "TXN" + System.currentTimeMillis() + i;
        t.mobileNo = "9" + (long)(Math.random() * 1000000000L);
        t.location = "Chennai";
        t.merchant = "Amazon";
        t.txnType = "UPI";
        t.amount = Math.round((10 + (100000 - 10) * random.nextDouble()) * 100.0) / 100.0;
        t.status = "SUCCESS";
        t.timestamp = new Timestamp(System.currentTimeMillis());
        t.ipAddress = random.nextInt(255) + "." +
                      random.nextInt(255) + "." +
                      random.nextInt(255) + "." +
                      random.nextInt(255);

        t.isFraud = t.amount > 50000;

        return t;
    }

    // ================= REAL-TIME API CALL =================
    private static boolean callVerificationAPI(Transaction txn) {

        try {
            URL url = new URL(VERIFY_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{"
                    + "\"txnId\":\"" + txn.txnId + "\","
                    + "\"mobileNo\":\"" + txn.mobileNo + "\","
                    + "\"amount\":" + txn.amount
                    + "}";

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                return false;
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String response = br.readLine();

            br.close();

            // Expecting: {"status":"VALID"}
            return response.contains("VALID");

        } catch (Exception e) {
            System.out.println(" API ERROR → " + e.getMessage());
            return false;
        }
    }
}
