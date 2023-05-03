package com.kremnev8.electroniccookbook.common;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A utility class to hash passwords and check passwords vs hashed values. It uses a combination of hashing and unique
 * salt. The algorithm used is PBKDF2WithHmacSHA1 which, although not the best for hashing password (vs. bcrypt) is
 * still considered robust and <a href="https://security.stackexchange.com/a/6415/12614"> recommended by NIST </a>.
 * The hashed value has 256 bits.
 */
public class Passwords {

    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    /**
     * static utility class
     */
    private Passwords() { }

    /**
     * Returns a random salt to be used to hash a password.
     *
     * @return a 16 bytes random salt
     */
    public static String getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     *
     * @param password the password to be hashed
     * @param saltStr     a salt String, ideally obtained with the getNextSalt method
     *
     * @return the hashed password with a pinch of salt
     */
    public static String hash(String password, String saltStr) {
        byte[] salt = Base64.decode(saltStr, Base64.DEFAULT);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] passwordHash = skf.generateSecret(spec).getEncoded();
            return Base64.encodeToString(passwordHash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Returns true if the given password and salt match the hashed value, false otherwise.<br>
     *
     * @param password     the password to check
     * @param saltStr         the salt used to hash the password
     * @param expectedHash the expected hashed value of the password
     *
     * @return true if the given password and salt match the hashed value, false otherwise
     */
    public static boolean isExpectedPassword(String password, String saltStr, String expectedHash) {
        String pwdHash = hash(password, saltStr);

        if (pwdHash.length() != expectedHash.length()) return false;
        return pwdHash.equals(expectedHash);
    }
}