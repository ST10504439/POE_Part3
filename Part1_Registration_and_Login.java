/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Part1_Registration_and_Login;
import java.util.List;
import java.util.Scanner;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.swing.JOptionPane;
/**
 *
 * @author sibus
 */
public class Part1_Registration_and_Login {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load previously stored JSON messages into the arrays
        Message.loadStoredMessagesFromJson();

        // --- PART 1: REGISTRATION ---
        System.out.println("=== USER REGISTRATION ===");
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine().trim();

        Login loginSystem = new Login(firstName, lastName);
        String username, password, cellNumber;

        while (true) {
            System.out.print("Enter a username (must contain '_' and be <= 5 chars): ");
            username = scanner.nextLine().trim();
            if (loginSystem.checkUserName(username)) {
                System.out.println("Username successfully captured.");
                break;
            }
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        while (true) {
            System.out.print("Enter a password (min 8 chars, 1 uppercase, 1 number, 1 special char): ");
            password = scanner.nextLine();
            if (loginSystem.checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
                break;
            }
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        }

        while (true) {
            System.out.print("Enter your South African cell phone number (e.g., +27838968976): ");
            cellNumber = scanner.nextLine().trim();
            if (loginSystem.checkCellPhoneNumber(cellNumber)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
        }

        loginSystem.registerUser(username, password, cellNumber);

        // --- PART 1: LOGIN ---
        System.out.println("\n=== USER LOGIN ===");
        boolean isLoggedIn = false;

        while (!isLoggedIn) {
            System.out.print("Enter your username to login: ");
            String loginUser = scanner.nextLine().trim();
            System.out.print("Enter your password to login: ");
            String loginPass = scanner.nextLine();

            String status = loginSystem.returnLoginStatus(loginUser, loginPass);
            System.out.println(status);

            if (loginSystem.loginUser(loginUser, loginPass)) {
                isLoggedIn = true;
            }
        }

        // --- PART 2 & 3: QUICKCHAT MAIN MENU ---
        System.out.println("\nWelcome to QuickChat.");
        int maxMessages = readValidInt(scanner, "How many messages do you wish to enter? ", 1, 100);
        int messagesEnteredCount = 0;

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesEnteredCount >= maxMessages) {
                        System.out.println("You have reached your limit of " + maxMessages + " message(s).");
                        break;
                    }

                    String recipient;
                    Message validator = new Message();
                    while (true) {
                        System.out.print("Enter recipient cell phone number (with +27 international code): ");
                        recipient = scanner.nextLine().trim();
                        String recipientStatus = validator.checkRecipientCell(recipient);
                        if (recipientStatus.equals("Cell phone number successfully captured.")) {
                            break;
                        }
                        System.out.println(recipientStatus);
                    }

                    String msgContent;
                    while (true) {
                        System.out.print("Enter your message (max 250 characters): ");
                        msgContent = scanner.nextLine();
                        String lengthStatus = validator.checkMessageLength(msgContent);
                        if (lengthStatus.equals("Message ready to send.")) {
                            System.out.println(lengthStatus);
                            break;
                        }
                        System.out.println(lengthStatus);
                    }

                    String fullSenderName = firstName + " " + lastName;
                    Message msg = new Message(fullSenderName, messagesEnteredCount, recipient, msgContent);
                    messagesEnteredCount++;

                    System.out.println("\nChoose an action for this message:");
                    System.out.println("1) Send Message");
                    System.out.println("2) Disregard Message");
                    System.out.println("3) Store Message to send later");
                    int action = readValidInt(scanner, "Option: ", 1, 3);

                    String actionResult = msg.sentMessage(action);
                    System.out.println(actionResult);

                    if (action == 1) {
                        String details = "Message ID: " + msg.getMessageID()
                                + "\nMessage Hash: " + msg.getMessageHash()
                                + "\nRecipient: " + msg.getRecipient()
                                + "\nMessage: " + msg.getMessageContent();

                        System.out.println("\n--- Sent Message Details ---\n" + details);
                        JOptionPane.showMessageDialog(null, details, "Message Details", JOptionPane.INFORMATION_MESSAGE);
                    }

                    if (messagesEnteredCount == maxMessages) {
                        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    }
                    break;

                case "2":
                    System.out.println("\n=== RECENTLY SENT MESSAGES ===");
                    System.out.println(Message.displaySentMessagesReport());
                    break;

                case "3":
                    System.out.println("Exiting QuickChat. Total messages sent: " + Message.returnTotalMessages());
                    running = false;
                    break;

                case "4":
                    handleStoredMessagesMenu(scanner);
                    break;

                default:
                    System.out.println("Invalid option. Please select 1, 2, 3, or 4.");
            }
        }
        scanner.close();
    }

    private static int readValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter digits only.");
            }
        }
    }

    private static void handleStoredMessagesMenu(Scanner scanner) {
        System.out.println("\n--- Stored Messages Menu ---");
        System.out.println("a) Display the sender and recipient of all stored messages");
        System.out.println("b) Display the longest stored message");
        System.out.println("c) Search for a message ID and display the corresponding recipient and message");
        System.out.println("d) Search for all the messages stored for a particular recipient");
        System.out.println("e) Delete a message using the message hash");
        System.out.println("f) Display a report that lists the full details of all the stored/sent messages");
        System.out.print("Select sub-option (a-f): ");
        String subChoice = scanner.nextLine().trim().toLowerCase();

        switch (subChoice) {
            case "a":
                System.out.println("\n" + Message.displaySenderAndRecipient());
                break;
            case "b":
                System.out.println("\nLongest Message: " + Message.displayLongestMessage());
                break;
            case "c":
                System.out.print("Enter Message ID to search: ");
                String searchId = scanner.nextLine().trim();
                System.out.println("\n" + Message.searchByMessageID(searchId));
                break;
            case "d":
                System.out.print("Enter Recipient cell number to search: ");
                String searchRec = scanner.nextLine().trim();
                List<String> results = Message.searchByRecipient(searchRec);
                if (results.isEmpty()) {
                    System.out.println("No messages found for recipient: " + searchRec);
                } else {
                    System.out.println("\nMessages for " + searchRec + ":");
for (String m : results) {
System.out.println("- " + m);
}
}
break;
case "e":
System.out.print("Enter Message Hash to delete: ");
String deleteHash = scanner.nextLine().trim();
System.out.println("\n" + Message.deleteMessageByHash(deleteHash));
break;
case "f":
System.out.println("\n=== SENT MESSAGES REPORT ===");
System.out.println(Message.displaySentMessagesReport());
break;
default:
System.out.println("Invalid sub-option. Please enter a, b, c, d, e, or f.");
}
}
}
                