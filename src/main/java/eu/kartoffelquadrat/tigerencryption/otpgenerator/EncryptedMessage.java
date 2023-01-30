package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import org.apache.commons.codec.binary.Hex;

/**
 * This class represents an encrypted message in form of a map. Every entry consists of an index to
 * indicate the otp-chunk that has been used for encryption as well as the resulting byte array for
 * he corresponding chunk.
 */
public class EncryptedMessage {

  // Name reference to the one time pad used for encryption.


  // private map to store the individual chops of the message. Every chop lists the index of the
  // chunk used for encryption, as well as the resulting byte array.
  private final LinkedHashMap<Integer, byte[]> choppedMessage = new LinkedHashMap<>();

  // Reference to one time pad used for encryption. Forst 6 characters of OTP hash are used to identify the right pad.
  private final String otpHash;

  // Depending on amount of chunks usesd in pad, more padding might be needed.
  private final int maxPaddingLength;

  public EncryptedMessage(OneTimePad pad, int startChunkIndex, byte[][] chops) {

    // The distance between two chunks used for encryption equals the amount of parties this pad was built for.
    int chunkHop = pad.getParties().length;

    // Store hash of oth used
    otpHash = pad.getHash();

    // We will never need more padding there are digits in the max amount of chunks.
    maxPaddingLength = Integer.toString(pad.getChunkAmount()).length();

    // Store received chops in internal map
    int currentChunkIndex = startChunkIndex;
    for (int i = 0; i < chops.length; i++) {
      choppedMessage.put(currentChunkIndex, chops[i]);
      currentChunkIndex += chunkHop;
    }
  }

  /**
   * Helper method that creates a quasi unique prefix telling the hash of one time pad used and
   * intern chunk used for encryption.
   *
   * @param chunkId      as the number of the chunk used for encryption.
   * @param chunkPadding as the target length for padding the number with leading zeroes.
   * @return a prefix string that can concatenated with a string representation of an encrypted
   * message chop.
   */
  public String getPrefix(int chunkId, int chunkPadding) {

    String paddedChunkId = String.format("%1$" + chunkPadding + "s", chunkId).replace(' ', '0');
    return otpHash.substring(0, 6) + "-" + paddedChunkId + "-";
  }

  /**
   * Public access method to retrieve the amount of chops this encrypted message consists of.
   *
   * @return length of the internal data structure, storing all chops.
   */
  public int getChopAmount() {
    return choppedMessage.size();
  }

  /**
   * Public access methof to retireve a copy of the byte array representing a singe encrypted
   * message chop.
   *
   * @param chopIndex as the index of the mssage chop to receive.
   * @return a copy of the requested byte array.
   */
  public byte[] getChop(int chopIndex) {
    byte[] targetChop = choppedMessage.get(chopIndex);
    return Arrays.copyOf(targetChop, targetChop.length);
  }

  /**
   * Utils method that serializes an encrypted to a printable string, conaining only ASCII
   * characters. The outcome can be safely transmitted over insecure channels.
   *
   * @return ascii serialized version of encrypted message.
   */
  public String serializeToHex() {

    StringBuilder hexSerializationBuilder = new StringBuilder("");

    // for each chop, append prefix + serialization + newline
    for (int chopKey : choppedMessage.keySet()) {
      hexSerializationBuilder.append(getPrefix(chopKey, maxPaddingLength));
      String hexSerializedChop = Hex.encodeHexString(choppedMessage.get(chopKey)).toUpperCase();
      hexSerializationBuilder.append(hexSerializedChop);
      hexSerializationBuilder.append("\n");
    }

    return hexSerializationBuilder.toString();
  }
}
