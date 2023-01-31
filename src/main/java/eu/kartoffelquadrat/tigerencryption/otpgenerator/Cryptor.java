package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Utils class for conversion of messages. Conversion works both ways since this is  a synmmetric
 * encryption algorithm. Internaly it uses the same "cryptChunkSizedMethod" for decryption and
 * enryption of messages.
 *
 * @author Maximilian Schiedermeier
 */
public class Cryptor {

  /**
   * Decryptor method to cencrypt a plain message by applying a series of chunks.
   *
   * @param plainMessage
   * @param pad
   * @param startChunkId
   * @return
   */
  public static EncryptedMessage encryptMessage(byte[] plainMessage, OneTimePad pad,
                                                int startChunkId) throws CryptorException {

    // Iterate in hops over the needed amount of chunks.
    byte[][] messageChops = chop(plainMessage, pad.getChunkSize());

    // Create target array for encrypted messages payload.
    byte[][] encryptedMessageChops = new byte[messageChops.length][];

    // encrypt every message chunk and append to list of encrypted messages
    int currentChunkId = startChunkId;
    int hopSize = startChunkId % pad.getParties().length;
    for (int i = 0; i < messageChops.length; i++) {
      encryptedMessageChops[i] =
          cryptChunkSizedMessage(messageChops[i], pad.getChunkContent(currentChunkId));
      currentChunkId += hopSize;
    }
    return new EncryptedMessage(pad, startChunkId, encryptedMessageChops);
  }

  /**
   * Cuts a provided byte[] into smaller byte arrays that each have the requested size. The lase
   * chunk is filled up with whitespace bytes, to reach the requested size.
   *
   * @param plainMessage byte array of the original message that may or may not fit into a series of
   *                     chunks.
   * @param chopSize     the target size for byte[] in the return array.
   * @returna byte[][] where each element is guaranteed to have exactly the requested size.
   */
  protected static byte[][] chop(byte[] plainMessage, int chopSize) {

    // compute amount of required chops
    int targetChops = plainMessage.length / chopSize;
    if (plainMessage.length % chopSize != 0) {
      targetChops += 1;
    }
    byte[][] choppedMessage = new byte[targetChops][];

    // Iterate over plainmessage and break into smaller byte[]s (except for last chunk, which needs extra handling)
    for (int i = 0; i < targetChops - 1; i++) {
      choppedMessage[i] = Arrays.copyOfRange(plainMessage, i * chopSize, ((i + 1) * chopSize));
    }

    // Copy the remainder (may not be exactly of chunkSize, so we copy the bytes individually)
    // Initialize with whitespace.
    byte[] plainMessageRemainder =
        Arrays.copyOfRange(plainMessage, (targetChops - 1) * chopSize, plainMessage.length - 1);
    byte[] lastChop = getWhiteSpaceByteArray(chopSize);
    for (int i = 0; i < plainMessageRemainder.length; i++) {
      lastChop[i] = plainMessageRemainder[i];
    }
    choppedMessage[targetChops - 1] = lastChop;

    // Return the result array
    return choppedMessage;
  }

  /**
   * Herlper method to get a byte array that correcpons a whitespace only message of desired
   * length.
   *
   * @param length target length for the whitespace array.
   * @return a whitespace only byte array.
   */
  protected static byte[] getWhiteSpaceByteArray(int length) {
    String whitespace = "";
    for (int i = 0; i < length; i++) {
      whitespace += " ";
    }
    return whitespace.getBytes();
  }

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
  protected static byte[] cryptChunkSizedMessage(byte[] message, byte[] chunk)
      throws CryptorException {

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
   * @param message      as the original message,
   * @param targetLength as expected output message size. Cannot be smaller than message length.
   * @return padded message, with trailing whitespaces.
   * @throws CryptorException if the message is too long.
   */
  public static String padString(String message, int targetLength) throws CryptorException {

    if (message.length() > targetLength) {
      throw new CryptorException(
          "Message cannot be padded. It already exceeds the maximum targetLength size.");
    }

    String paddingString = "%" + targetLength + "s";
    return String.format(paddingString, message);
  }
}
