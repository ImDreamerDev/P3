package dk.aau.ds304e18.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void testCorrectPass() {
        byte[] salt = Password.getNextSalt();
        String pass = "testPassword123!/";
        byte[] hashedPass = Password.hash(pass.toCharArray(), salt);
        assertTrue(Password.isExpectedPassword(pass.toCharArray(), salt, hashedPass));
    }

    @Test
    void testWrongPass() {
        byte[] salt = Password.getNextSalt();
        String pass = "testPassword123!/";
        byte[] hashedPass = Password.hash(pass.toCharArray(), salt);
        assertFalse(Password.isExpectedPassword("ljdkhasjhbd6556!".toCharArray(), salt, hashedPass));
    }
}
