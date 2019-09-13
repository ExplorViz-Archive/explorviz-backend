package net.explorviz.security.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * This repository contains peer-reviewed libraries for password storage. Passwords are "hashed"
 * with PBKDF2 (64,000 iterations of SHA1 by default) using a cryptographically-random salt.
 *
 * @see <a href=
 *      "https://github.com/defuse/password-hashing">https://github.com/defuse/password-hashing</a>
 *
 */
public class PasswordStorage {

  // CHECKSTYLE.OFF: javadoc

  /*
   * Copied from https://github.com/defuse/password-hashing
   *
   */

  public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

  // These constants may be changed without breaking existing hashes.
  public static final int SALT_BYTE_SIZE = 24;
  public static final int HASH_BYTE_SIZE = 18;
  public static final int PBKDF2_ITERATIONS = 64000;

  // These constants define the encoding and may not be changed.
  public static final int HASH_SECTIONS = 5;
  public static final int HASH_ALGORITHM_INDEX = 0;
  public static final int ITERATION_INDEX = 1;
  public static final int HASH_SIZE_INDEX = 2;
  public static final int SALT_INDEX = 3;
  public static final int PBKDF2_INDEX = 4;

  @SuppressWarnings("serial")
  public static class InvalidHashException extends Exception { // no pmd
    public InvalidHashException(final String message) {
      super(message);
    }

    public InvalidHashException(final String message, final Throwable source) {
      super(message, source);
    }
  }


  @SuppressWarnings("serial")
  public static class CannotPerformOperationException extends Exception {
    public CannotPerformOperationException(final String message) {
      super(message);
    }

    public CannotPerformOperationException(final String message, final Throwable source) {
      super(message, source);
    }
  }

  private PasswordStorage() {

  }



  public static String createHash(final String password) throws CannotPerformOperationException {
    return createHash(password.toCharArray());
  }

  public static String createHash(final char[] password) throws CannotPerformOperationException {
    // Generate a random salt
    final SecureRandom random = new SecureRandom();
    final byte[] salt = new byte[SALT_BYTE_SIZE];
    random.nextBytes(salt);

    // Hash the password
    final byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
    final int hashSize = hash.length;

    // format: algorithm:iterations:hashSize:salt:hash
    final String parts =
        "sha1:" + PBKDF2_ITERATIONS + ":" + hashSize + ":" + toBase64(salt) + ":" + toBase64(hash);
    return parts;
  }

  public static boolean verifyPassword(final String password, final String correctHash)
      throws CannotPerformOperationException, InvalidHashException {
    return verifyPassword(password.toCharArray(), correctHash);
  }


  public static boolean verifyPassword(final char[] password, final String correctHash)
      throws CannotPerformOperationException, InvalidHashException {
    // Decode the hash into its parameters
    final String[] params = correctHash.split(":");
    if (params.length != HASH_SECTIONS) {
      throw new InvalidHashException("Fields are missing from the password hash.");
    }

    // Currently, Java only supports SHA1.
    if (!params[HASH_ALGORITHM_INDEX].equals("sha1")) {
      throw new CannotPerformOperationException("Unsupported hash type.");
    }

    int iterations = 0;
    try {
      iterations = Integer.parseInt(params[ITERATION_INDEX]);
    } catch (final NumberFormatException ex) {
      throw new InvalidHashException("Could not parse the iteration count as an integer.", ex);
    }

    if (iterations < 1) {
      throw new InvalidHashException("Invalid number of iterations. Must be >= 1.");
    }

    byte[] salt = null;
    try {
      salt = fromBase64(params[SALT_INDEX]);
    } catch (final IllegalArgumentException ex) {
      throw new InvalidHashException("Base64 decoding of salt failed.", ex);
    }

    byte[] hash = null;
    try {
      hash = fromBase64(params[PBKDF2_INDEX]);
    } catch (final IllegalArgumentException ex) {
      throw new InvalidHashException("Base64 decoding of pbkdf2 output failed.", ex);
    }

    int storedHashSize = 0;
    try {
      storedHashSize = Integer.parseInt(params[HASH_SIZE_INDEX]);
    } catch (final NumberFormatException ex) {
      throw new InvalidHashException("Could not parse the hash size as an integer.", ex);
    }

    if (storedHashSize != hash.length) {
      throw new InvalidHashException("Hash length doesn't match stored hash length.");
    }

    // Compute the hash of the provided password, using the same salt,
    // iteration count, and hash length
    final byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
    // Compare the hashes in constant time. The password is correct if
    // both hashes match.
    return slowEquals(hash, testHash);
  }

  private static boolean slowEquals(final byte[] a, final byte[] b) {
    int diff = a.length ^ b.length;
    for (int i = 0; i < a.length && i < b.length; i++) {
      diff |= a[i] ^ b[i];
    }
    return diff == 0;
  }

  private static byte[] pbkdf2(final char[] password, final byte[] salt, final int iterations,
      final int bytes) throws CannotPerformOperationException {
    try {
      final PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
      final SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
      return skf.generateSecret(spec).getEncoded();
    } catch (final NoSuchAlgorithmException ex) {
      throw new CannotPerformOperationException("Hash algorithm not supported.", ex);
    } catch (final InvalidKeySpecException ex) {
      throw new CannotPerformOperationException("Invalid key spec.", ex);
    }
  }

  private static byte[] fromBase64(final String hex) throws IllegalArgumentException {
    return DatatypeConverter.parseBase64Binary(hex);
  }

  private static String toBase64(final byte[] array) {
    return DatatypeConverter.printBase64Binary(array);
  }

}
