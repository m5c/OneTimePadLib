package eu.kartoffelquadrat.otpgenerator;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Immutable implementation of One Time Pad object. Cannot be changed once created. The one time pad
 * holds a series of chunks which are meant for single encryption use by a given communication
 * party.
 *
 * @author Maximilian Schiedermeier
 */
public class OneTimePad {

  // Indicates timestamp of creation
  private final String timeStamp;

  // First party is implicitly assumed creator
  private final String[] parties;

  // Hash of parties and moment of creation. This is not to integrity portet the chunks, but to
  // ensure the right otp is referenced by encrypted messagtes.
  String hash;
  private final byte[][] chunks;

  /**
   * Constructor for the One Time Pad class.
   *
   * @param chunks as a 2D byte array, representing the content of the individual chunks.
   */
  OneTimePad(String timeStamp, String[] parties, byte[][] chunks) {
    this.chunks = chunks;
    this.parties = parties;
    this.timeStamp = timeStamp;
    hash = computeCreationMessageDigest5(timeStamp, parties);
  }

  /**
   * Herlper method to look up the first chunk index to use by a party, for their first encrypted
   * message in a conversation.
   *
   * @param party as the name@machine convention string to search for.
   * @return index of the povided party, which can be used as chunk index for the first encrypted
   * message by that party. Chunk ids for follow-up messages are retreived from the encrypted
   * message objects.
   * @throws InvalidPartyException if the porivded party is not associated to this one time pad.
   */
  public int getStarterChunkIndexforParty(String party) throws InvalidPartyException {

    // Iterate over parties and search for provided string
    for (int i = 0; i < parties.length; i++) {
      if (parties[i].equals(party)) {
        return i;
      }
    }

    // if at end of array and no match, reject input
    throw new InvalidPartyException(
        "Index for provided party cannot be retrieved for they are not associated with this one time pad.");


    // Reject lookup if provided party is unknown
  }

  private static String computeCreationMessageDigest5(String timeStamp, String[] parties) {

    String concatIdentifier = timeStamp;
    for (int i = 0; i < parties.length; i++) {
      concatIdentifier += "-";
      concatIdentifier += parties[i];
    }
    return Hex.encodeHexString(DigestUtils.md5(concatIdentifier)).toUpperCase();
  }

  /**
   * Helper method to get the hash formed of creator name and time of creation string. Note that
   * this hash does not cover the chunks.
   *
   * @return Hash of one time pad creation details.
   */
  public String getHash() {
    return hash;
  }


  /**
   * Look up parties registered for this one time pad. Returns a deep copy to ensure integrity.
   *
   * @return String array containtn gthe names and assiciated machines of all parties using the pad.
   */
  public String[] getParties() {
    return Arrays.copyOf(parties, parties.length);
  }


  /**
   * Getter to look up size per chunk in this one time pad. All chunks have identical size.
   *
   * @return integer telling the amount of chunks held in one time pad.
   */
  public int getChunkAmount() {
    return chunks.length;
  }

  /**
   * Getter to look up size per chunk in this one time pad. All chunks have identical size.
   *
   * @return integer telling the amount of bytes per chunk.
   */
  public int getChunkSize() {
    return chunks[0].length;
  }

  /**
   * Protected access to the contents of one specific chunk.
   *
   * @param chunkId as index of the target chunk in the One Time Pad.
   * @return byte array holding copy of requested chunk contents.
   * @throws OutOfChunksException if the requests index exceeds the stored chunk array.
   */
  public byte[] getChunkContent(int chunkId) throws OutOfChunksException {

    // Verify the requested index exists. Throw custom exception otherwise.
    if (chunkId >= chunks.length) {
      throw new OutOfChunksException("Chunk with id " + chunkId +
          " cannot be retrieved because the one time pad was exceeded.");
    }

    byte[] chunk = chunks[chunkId];
    return Arrays.copyOf(chunk, chunk.length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OneTimePad that = (OneTimePad) o;
    return Objects.equals(timeStamp, that.timeStamp) && Arrays.equals(parties, that.parties) &&
        Arrays.deepEquals(chunks, that.chunks);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(timeStamp);
    result = 31 * result + Arrays.hashCode(parties);
    result = 31 * result + Arrays.hashCode(chunks);
    return result;
  }
}
