import java.util.Scanner;

public class RealTimeTransaction {

    static void validateEmail(String email) throws Exception {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Invalid email format");
        }
    }
    static void validateMobile(String mobile) throws Exception {
        if (!mobile.matches("\\d{10}")) {
            throw new Exception("Mobile number must be 10 digits");
        }
    }

    static void validateAccount(String acc) throws Exception {
        if (!acc.matches("\\d{10}")) {
            throw new Exception("Account number must be 10 digits");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String transactionId = "TXN" + System.currentTimeMillis();
        String userName = "", email = "", mobile = "";
        String senderAcc = "", receiverAcc = "", receiverName = "";
        double amount = 0;

        while (true) {
            try {
                System.out.print("Enter user name: ");
                userName = sc.nextLine();
                if (userName.trim().isEmpty())
                    throw new Exception("User name cannot be empty");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter email: ");
                email = sc.nextLine();
                validateEmail(email);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter mobile number: ");
                mobile = sc.nextLine();
                validateMobile(mobile);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter sender account number: ");
                senderAcc = sc.nextLine();
                validateAccount(senderAcc);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter receiver account number: ");
                receiverAcc = sc.nextLine();
                validateAccount(receiverAcc);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter receiver name: ");
                receiverName = sc.nextLine();
                if (receiverName.trim().isEmpty())
                    throw new Exception("Receiver name cannot be empty");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter amount: ");
                amount = Double.parseDouble(sc.nextLine());

                if (amount <= 0)
                    throw new Exception("Amount must be greater than zero");

                if (amount > 50000) {
                    System.out.println("Transaction blocked due to risk check");
                    sc.close();
                    return;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Amount must be numeric");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Sender name: " + userName);
        System.out.println("Sender account: " + senderAcc);
        System.out.println("Receiver name: " + receiverName);
        System.out.println("Receiver account: " + receiverAcc);
        System.out.println("Amount: " + amount);
        System.out.println("Transaction status: SUCCESS");
        sc.close();
    }
}
