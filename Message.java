/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe_part3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author sibus
 */


public class Message {
    // Part 3 Required Dynamic Lists / Arrays
    public static List<String> sentMessages = new ArrayList<>();
    public static List<String> disregardedMessages = new ArrayList<>();
    public static List<String> storedMessages = new ArrayList<>();
    public static List<String> messageHashes = new ArrayList<>();
    public static List<String> messageIDs = new ArrayList<>();

    // Parallel Lists for Sender, Recipient, Message Content, and Flag
    public static List<String> senders = new ArrayList<>();
    public static List<String> recipients = new ArrayList<>();
    public static List<String> allMessages = new ArrayList<>();
    public static List<String> flags = new ArrayList<>();

    private static int totalMessagesSent = 0;

    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String messageContent;
    private String messageHash;

    public Message(String sender, int messageNumber, String recipient, String messageContent) {
        this.messageID = generateMessageID();
        this.sender = sender;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = createMessageHash();
    }

    public Message(int messageNumber, String recipient, String messageContent) {
        this("Developer", messageNumber, recipient, messageContent);
    }

    public Message(String messageID, String sender, int messageNumber, String recipient, String messageContent) {
        this.messageID = messageID;
        this.sender = sender;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = createMessageHash();
    }

    public Message() {
    }

    public String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long) (rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    public String checkRecipientCell(String cellNumber) {
        if (cellNumber != null && Pattern.matches("^\\+27\\d{9}$", cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength(String msg) {
        if (msg == null || msg.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = msg.length() - 250;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    public String createMessageHash() {
        String idPrefix = (messageID != null && messageID.length() >= 2) ? messageID.substring(0, 2) : "00";
        String cleanText = (messageContent != null) ? messageContent.trim().replaceAll("[^a-zA-Z0-9\\s]", "") : "";
        String[] words = cleanText.split("\\s+");

        String firstWord = (words.length > 0 && !words[0].isEmpty()) ? words[0] : "";
        String lastWord = (words.length > 0 && !words[words.length - 1].isEmpty()) ? words[words.length - 1] : "";

        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Populates arrays dynamically: 1 = Send, 2 = Disregard, 3 = Store.
     */
    public String sentMessage(int option) {
        messageIDs.add(this.messageID);
        messageHashes.add(this.messageHash);
        senders.add(this.sender);
        recipients.add(this.recipient);
        allMessages.add(this.messageContent);

        switch (option) {
            case 1:
                totalMessagesSent++;
                sentMessages.add(this.messageContent);
                flags.add("Sent");
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this.messageContent);
                flags.add("Disregard");
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this.messageContent);
                flags.add("Stored");
                storeMessage();
                return "Message successfully stored.";
            default:
                flags.add("Unknown");
                return "Invalid option selected.";
        }
    }

    /**
     * Appends stored message to stored_messages.json.
     * Reference: OpenAI. (2024). JSON File Serialization and Parsing in Java.
     */
    public void storeMessage() {
        String jsonEntry = String.format(
                "{\"MessageID\":\"%s\",\"Sender\":\"%s\",\"MessageHash\":\"%s\",\"Recipient\":\"%s\",\"Message\":\"%s\"}\n",
                escapeJson(messageID),
                escapeJson(sender),
                escapeJson(messageHash),
                escapeJson(recipient),
                escapeJson(messageContent)
        );

        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            writer.write(jsonEntry);
        } catch (IOException e) {
            System.out.println("Error storing message to JSON: " + e.getMessage());
        }
    }

    private static void rewriteStoredMessagesJson() {
        try (FileWriter writer = new FileWriter("stored_messages.json", false)) {
            for (int i = 0; i < allMessages.size(); i++) {
                if ("Stored".equals(flags.get(i))) {
                    String jsonEntry = String.format(
                            "{\"MessageID\":\"%s\",\"Sender\":\"%s\",\"MessageHash\":\"%s\",\"Recipient\":\"%s\",\"Message\":\"%s\"}\n",
                            escapeJson(messageIDs.get(i)),
                            escapeJson(senders.get(i)),
                            escapeJson(messageHashes.get(i)),
                            escapeJson(recipients.get(i)),
                            escapeJson(allMessages.get(i))
                    );
                    writer.write(jsonEntry);
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating JSON file: " + e.getMessage());
        }
    }

    /**
     * Reads stored_messages.json into the storedMessages array and parallel lists.
     */
    public static void loadStoredMessagesFromJson() {
        File file = new File("stored_messages.json");
        if (!file.exists()) {
            return;
        }

        Pattern pattern = Pattern.compile(
                "\"MessageID\":\"(.*?)\",\"Sender\":\"(.*?)\",\"MessageHash\":\"(.*?)\",\"Recipient\":\"(.*?)\",\"Message\":\"(.*?)\""
        );

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String id = matcher.group(1);
                    String snd = matcher.group(2);
                    String hash = matcher.group(3);
                    String rec = matcher.group(4);
                    String msg = matcher.group(5).replace("\\\"", "\"").replace("\\\\", "\\");

                    if (!messageIDs.contains(id)) {
                        messageIDs.add(id);
                        senders.add(snd);
                        messageHashes.add(hash);
                        recipients.add(rec);
                        allMessages.add(msg);
                        storedMessages.add(msg);
                        flags.add("Stored");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading stored messages from JSON: " + e.getMessage());
        }
    }

    // --- PART 3: MENU OPTION 4 METHODS (2a - 2f) ---

    /**
     * 2a. Display the sender and recipient of all stored messages.
     */
    public static String displaySenderAndRecipient() {
        if (allMessages.isEmpty()) {
            return "No messages available.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allMessages.size(); i++) {
            sb.append("Sender: ").append(senders.get(i))
              .append(" | Recipient: ").append(recipients.get(i))
              .append(" (").append(flags.get(i)).append(")\n");
        }
        return sb.toString().trim();
    }

    /**
     * 2b. Display the longest message.
     */
    public static String displayLongestMessage() {
        String longest = "";
        for (String msg : allMessages) {
            if (msg != null && msg.length() > longest.length()) {
                longest = msg;
            }
        }
        return longest.isEmpty() ? "No messages available." : longest;
    }

    /**
     * 2c. Search for a Message ID and display the corresponding recipient and message.
     */
    public static String searchByMessageID(String searchID) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(searchID)) {
                return "Recipient: " + recipients.get(i) + "\nMessage: " + allMessages.get(i);
            }
        }
        return "Message ID not found.";
    }

    /**
     * 2d. Search for all messages sent or stored regarding a particular recipient.
     */
    public static List<String> searchByRecipient(String searchRecipient) {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equals(searchRecipient) &&
                    ("Sent".equals(flags.get(i)) || "Stored".equals(flags.get(i)))) {
matches.add(allMessages.get(i));
}
}
return matches;
}
/**
* 2e. Delete a message using the Message Hash.
*/
public static String deleteMessageByHash(String hashToDelete) {
for (int i = 0; i < messageHashes.size(); i++) {
if (messageHashes.get(i).equalsIgnoreCase(hashToDelete)) {
String deletedMsg = allMessages.get(i);
String flag = flags.get(i);
if ("Sent".equals(flag)) {
sentMessages.remove(deletedMsg);
totalMessagesSent--;
} else if ("Stored".equals(flag)) {
storedMessages.remove(deletedMsg);
} else if ("Disregard".equals(flag)) {
disregardedMessages.remove(deletedMsg);
}
messageIDs.remove(i);
messageHashes.remove(i);
senders.remove(i);
recipients.remove(i);
allMessages.remove(i);
flags.remove(i);
if ("Stored".equals(flag)) {
rewriteStoredMessagesJson();
}
return "Message \"" + deletedMsg + "\" successfully deleted.";
}
}
return "Message hash not found.";
}
/**
* 2f. Display a report that lists the full details of all sent messages.
*/
public static String displaySentMessagesReport() {
StringBuilder sb = new StringBuilder();
boolean found = false;
for (int i = 0; i < allMessages.size(); i++) {
if ("Sent".equals(flags.get(i))) {
found = true;
sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n")
.append("Recipient: ").append(recipients.get(i)).append("\n")
.append("Message: ").append(allMessages.get(i)).append("\n")
.append("----------------------------------------\n");
}
}
return found ? sb.toString().trim() : "No sent messages to display.";
}
public static int returnTotalMessages() {
return totalMessagesSent;
}
public static void resetAllData() {
sentMessages.clear();
disregardedMessages.clear();
storedMessages.clear();
messageHashes.clear();
messageIDs.clear();
senders.clear();
recipients.clear();
allMessages.clear();
flags.clear();
totalMessagesSent = 0;
}
private static String escapeJson(String text) {
if (text == null) return "";
return text.replace("\\", "\\\\").replace("\"", "\\\"");
}
public String getMessageID() { return messageID; }
public String getMessageHash() { return messageHash; }
public String getRecipient() { return recipient; }
public String getMessageContent() { return messageContent; }
}