package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.sql.Date;
import java.util.Arrays;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Immutable implementation of One Time Pad object. Cannot be changed once created. The one time pad
 * holds a series of chunks which are meant for single encryption use by a given communication
 * party.
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
}