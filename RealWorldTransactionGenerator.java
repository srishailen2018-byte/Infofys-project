    import java.util.*;
    import java.text.SimpleDateFormat;

    class Transaction {
        private String txnId;
        private String mobileNo;
        private String location;
        private String merchant;
        private String txnType;
        private double amount;
        private String status;
        private String timestamp;

        public Transaction(String txnId, String mobileNo, String location,
                        String merchant, String txnType,
                        double amount, String status, String timestamp) {
            this.txnId = txnId;
            this.mobileNo = mobileNo;
            this.location = location;
            this.merchant = merchant;
            this.txnType = txnType;
            this.amount = amount;
            this.status = status;
            this.timestamp = timestamp;
        }

        public String getTxnId() { return txnId; }
        public String getMobileNo() { return mobileNo; }
        public String getLocation() { return location; }
        public double getAmount() { return amount; }

        @Override
        public String toString() {
            return "Transaction{" +
                    "txnId='" + txnId + '\'' +
                    ", mobileNo='" + mobileNo + '\'' +
                    ", location='" + location + '\'' +
                    ", merchant='" + merchant + '\'' +
                    ", txnType='" + txnType + '\'' +
                    ", amount=" + amount +
                    ", status='" + status + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }

    class TransactionGenerator {

        private static final Random random = new Random();

        private static final String[] locations = {
                "Chennai", "Bangalore", "Mumbai",
                "Delhi", "Hyderabad", "Pune"
        };

        private static final String[] merchants = {
                "Amazon", "Flipkart", "Swiggy",
                "Zomato", "Uber", "IRCTC"
        };

        private static final String[] txnTypes = {
                "UPI", "CARD", "NET_BANKING", "WALLET"
        };

        private static final String[] statusTypes = {
                "SUCCESS", "FAILED", "PENDING"
        };

        public static Transaction generateTransaction() {

            String txnId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            String mobileNo = "9" + (100000000 + random.nextInt(900000000));

            String location = locations[random.nextInt(locations.length)];

            String merchant = merchants[random.nextInt(merchants.length)];

            String txnType = txnTypes[random.nextInt(txnTypes.length)];

            double amount = Math.round((10 + random.nextDouble() * 50000) * 100.0) / 100.0;

            String status = statusTypes[random.nextInt(statusTypes.length)];

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date());

            return new Transaction(txnId, mobileNo, location,
                    merchant, txnType, amount, status, timestamp);
        }
        public static List<Transaction> generateTransactions(int count) {
            List<Transaction> list = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                list.add(generateTransaction());
            }
            return list;
        }
    }
    public class RealWorldTransactionGenerator {

        public static void main(String[] args) {
            System.out.println("----- Single Transaction -----");
            System.out.println(TransactionGenerator.generateTransaction());
            System.out.println("\n----- 500 Transactions -----");
            List<Transaction> bulk500 = TransactionGenerator.generateTransactions(500);
            bulk500.forEach(System.out::println);
            System.out.println("\n----- 10 Transactions -----");
            List<Transaction> custom = TransactionGenerator.generateTransactions(10);
            custom.forEach(System.out::println);
        }
    }

