/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Message_Part3_test;
import com.mycompany.poe_part3.Message;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 *
 * @author sibus
 */
public class Message_Part3_test {
    

    private Message msg1;
    private Message msg2;
    private Message msg3;
    private Message msg4;
    private Message msg5;

    @BeforeEach
    public void setUp() {
        Message.resetAllData();

        // Test Data Message 1 (Sent)
        msg1 = new Message("1000000001", "Developer", 0, "+27834557896", "Did you get the cake?");
        msg1.sentMessage(1);

        // Test Data Message 2 (Stored)
        msg2 = new Message("1000000002", "Developer", 1, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3);

        // Test Data Message 3 (Disregard)
        msg3 = new Message("1000000003", "Developer", 2, "+27834484567", "Yohoooo, I am at your gate.");
        msg3.sentMessage(2);

        // Test Data Message 4 (Sent - Developer ID/Recipient 0838884567)
        msg4 = new Message("0838884567", "Developer", 3, "0838884567", "It is dinner time!");
        msg4.sentMessage(1);

        // Test Data Message 5 (Stored)
        msg5 = new Message("1000000005", "Developer", 4, "+27838884567", "Ok, I am leaving without you.");
        msg5.sentMessage(3);
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time!");
        assertEquals(expected, Message.sentMessages);
    }

    @Test
    public void testDisplayLongestMessage() {
        String expected = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expected, Message.displayLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        String result = Message.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."
        );
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String hashToDelete = msg2.getMessageHash();
        String expected = "Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";
        assertEquals(expected, Message.deleteMessageByHash(hashToDelete));
    }

    @Test
    public void testDisplayReport() {
        String report = Message.displaySentMessagesReport();
        assertTrue(report.contains(msg1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));

        assertTrue(report.contains(msg4.getMessageHash()));
        assertTrue(report.contains("0838884567"));
        assertTrue(report.contains("It is dinner time!"));
    }

    private void assertTrue(boolean contains) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertEquals(String expected, String deleteMessageByHash) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void assertEquals(List<String> expected, List<String> searchByRecipient) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}