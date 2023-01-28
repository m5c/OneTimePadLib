package eu.kartoffelquadrat.tigerencryption.otpgenerator;

/**
 * Utils class for conversion of messages. Works both ways (encryption and decryption).
 *
 * @author Maximilian Schiedermeier
 */
public class Cryptor {

  /**
   * Decryptor / Cryptor helper method implementation for a single message that is short enough to
   * go into a single chunk. The encryption algorithm is symmetric, that is to say the same chunk is
   * used for encryption and decryption.
   *
   * @param message as byte array of plain or encrypted message to convert.
   * @param chunk   as the chunk to use for encryption / decryption.
   * @return byte array representing the converted message.
   * @throws CryptorException in case the message is longer than the chunk size used.
   */
  protected static byte[] crypt(byte[] message, byte[] chunk) throws CryptorException {

    // verify the message fits within a chunk of the provided pad
    if (message.length != chunk.length) {
      throw new CryptorException("Message is too long, does not fit within a chunk.");
    }

    // Appply XOR, using provided chunk
    byte[] convertedMessage = new byte[chunk.length];
    for (int i = 0; i < convertedMessage.length; i++) {

      // outcome is xor bitmask representation of input.
      convertedMessage[i] =
          (byte) ((int) message[i] ^ chunk[i]); // I think this implementation is wrong
    }
    return convertedMessage;
  }

  /**
   * Helper method to pad a String with trailing whitespaces. Can be used to adjust the length of a
   * plain String message that is shorter than a chunk, prior to calling the cryptor method.
   *
   * @param message as the original message,
   * @param padding as expected output message size. Cannot be smaller than message length.
   * @return padded message, with trailing whitespaces.
   * @throws CryptorException if the message is too long.
   */
  public String padString(String message, int padding) throws CryptorException {

    if (message.length() > padding) {
      throw new CryptorException(
          "Message cannot be padded. It already exceeds the maximum padding size.");
    }

    String paddingString = "%" + padding + "s";
    return String.format(paddingString, message);
  }
}
