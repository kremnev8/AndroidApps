package com.kremnev8.electroniccookbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.kremnev8.electroniccookbook.common.Passwords;

import org.junit.Test;


public class PasswordsTest {
    @Test
    public void password_check_works() {
        String salt = Passwords.getNextSalt();
        String goodPassword = Passwords.hash("VeryGoodPassword", salt);
        String badPassword = Passwords.hash("BadPassword", salt);

        String salt2 = Passwords.getNextSalt();
        assertNotEquals(salt, salt2);

        assertTrue(Passwords.isExpectedPassword("VeryGoodPassword", salt, goodPassword));
        assertTrue(Passwords.isExpectedPassword("BadPassword", salt, badPassword));

        assertFalse(Passwords.isExpectedPassword("BadPassword", salt, goodPassword));

        assertFalse(Passwords.isExpectedPassword("VeryGoodPassword", salt2, goodPassword));
        assertFalse(Passwords.isExpectedPassword("BadPassword", salt2, badPassword));
    }
}