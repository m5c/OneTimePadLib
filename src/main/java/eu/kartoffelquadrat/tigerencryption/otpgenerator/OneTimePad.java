package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable implementation of One Time Pad object. Cannot be changed once created. The one time pad
 * holds a series of chunks which are meant for single encryption use by a given communication
 * party.
 *
 * @author Maximilian Schiedermeier
 */
public class OneTimePad {
  private final String timeStamp;

  // First party is implicitly assumed creator
  private final String[] parties;
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
   */
  public byte[] getChunkContent(int chunkId) {
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
    return Objects.equals(timeStamp, that.timeStamp)
        && Arrays.equals(parties, that.parties)
        && Arrays.deepEquals(chunks, that.chunks);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(timeStamp);
    result = 31 * result + Arrays.hashCode(parties);
    result = 31 * result + Arrays.hashCode(chunks);
    return result;
  }
}
