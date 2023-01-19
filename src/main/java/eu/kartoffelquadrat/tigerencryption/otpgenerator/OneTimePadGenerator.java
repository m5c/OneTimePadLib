/**
 * This class is the only code file of the One Time Pad Generator component of the Tiger Encryption
 * suite. It creates a new folder "otp-XXX" filled with random chunks of fixed size in your work
 * directory.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Generator for one time pads. Per default each pad consists of 4096 chunks, each filled with 512
 * bytes of random data.
 */
public class OneTimePadGenerator {

  // number of bytes used as chunk size
  private static final int CHUNK_SIZE = 512;

  // total number of chunks to generate
  private static final int CHUNK_AMOUNT = 4096;

  // Name of the created target folder, storing all chunks
  private static final String OTP_NAME = "otp-XXX";


  /**
   * Main logic for creation of new One Time Pad.
   *
   * @param args none.
   * @throws IOException in case persisting the one time pad to disk encounter an error.
   */
  public static void main(String[] args) throws IOException {

    File otpTargetDir = createOtpTargetDir(OTP_NAME);


    // create s many chunks as configured in CHUNK_AMOUNT, persist them all in target dir.
    for (int chunkId = 1; chunkId <= CHUNK_AMOUNT; chunkId++) {
      byte[] chunk = generateChunk(CHUNK_SIZE);
      persistChunk(chunk, chunkId, otpTargetDir);
    }


    System.out.println("Finished creation of One-Time-Pad.\n"
        + "Next steps:\n"
        + " - Replace the XXX in the generated folder opt-XXX by a unique 3-digit number."
        + " - Copy the outcome into the \".otp\" folder of all communicating end-devices.");
  }

  /**
   * Safely creates the target directory for the new otp. This directory will contain all created
   * chunks. Throws a runtimeException if the directory already exists.
   *
   * @param dirName as the name of the folder to create.
   * @return File object pointing to the newly created otp target direcotry.
   */
  public static File createOtpTargetDir(String dirName) {
    File otpTargetDir = new File(System.getProperty("user.dir") + "/" + dirName);
    if (!otpTargetDir.mkdir()) {
      throw new RuntimeException("Target directory \"" + OTP_NAME + "\" already exists.");
    }
    return otpTargetDir;
  }

  /**
   * Helper function to create a new chunk of requested, filled with random content.
   *
   * @param chunkSize as the requested array length in bytes.
   * @return a byte array of the requested sie, filled with random content.
   */
  public static byte[] generateChunk(int chunkSize) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[chunkSize];
    random.nextBytes(bytes);
    return bytes;
  }

  /**
   * Stores a provided chunk on disk, into the folder belonging to the specified one-time-pad.
   *
   * @param chunk     as the random data to persist.
   * @param chunkId   as index of the chunk to persist. Should be a number from 1-4069. The number
   *                  will be used as filename, padded with leading zeroes as a four digit numner.
   * @param targetDir as the target otp dir the new chunk file should be added to.
   * @throws IOException in case writing to disk fails.
   */
  public static void persistChunk(byte[] chunk, int chunkId, File targetDir)
      throws IOException {
    String zeroPaddedChunkName = String.format("%04d", chunkId);
    String pathBuilder = targetDir + "/" + zeroPaddedChunkName;
    try (FileOutputStream fos = new FileOutputStream(pathBuilder)) {
      fos.write(chunk);
      fos.flush();
    }
  }

}
