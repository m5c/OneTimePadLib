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
import java.sql.Date;

/**
 * Generator for one time pads. Per default each pad consists of 4096 chunks, each filled with 512
 * bytes of random data.
 */
public class OneTimePadGenerator {

  // number of bytes used as chunk size
  private static final int CHUNK_SIZE = 512;

  // total number of chunks to generate in a one time pad
  private static final int ONE_TIME_PAD_SIZE = 4096;

  // Name of the created target folder, storing all chunks
  private static final String ONE_TIME_PAD_NAME = "otp-XXX";


  /**
   * Main logic for creating a new One Time Pad and storing the contents on disk.
   * TODO: persist OTP in JSON format + add a checksum file next to serialized otp.
   *
   * @param args none.
   * @throws IOException in case persisting the one time pad to disk encounter an error.
   */
  public static void main(String[] args) throws IOException {

    File otpTargetDir = createOtpTargetDir(ONE_TIME_PAD_NAME);

    // create the one time pad, consisting of many chunks for the individual messages.
    OneTimePad pad = generatePad(args);

    // Store the one time pad on disk
    for (int chunkId = 0; chunkId < pad.getChunkAmount(); chunkId++) {
      persistChunk(pad.getChunkContent(chunkId), chunkId, otpTargetDir);
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
  protected static File createOtpTargetDir(String dirName) {
    File otpTargetDir = new File(System.getProperty("user.dir") + "/" + dirName);
    if (!otpTargetDir.mkdir()) {
      throw new RuntimeException("Target directory \"" + ONE_TIME_PAD_NAME + "\" already exists.");
    }
    return otpTargetDir;
  }

  /**
   * Creates a one time pad (2D byte array) consisting of chunks. The chunks are meant for single
   * use encryption of the individual messages while the pad serves as unit for pretection of a
   * communication.
   *
   * @param parties as sting array descripbing the names of all parties using this pad.
   * @return OneTimePad object holding the requested amount of chunks and size.
   */
  public static OneTimePad generatePad(String[] parties) {
    return generatePad(ONE_TIME_PAD_SIZE, CHUNK_SIZE, parties);
  }

  /**
   * Overloaded variant of generatePad that allows for customized amount of chunks and chunk size
   * used.
   *
   * @param padSize   as the amount of chunks to generate.
   * @param chunkSize as the amount of bytes per generated chunk.
   * @param parties   as sting array descripbing the names of all parties using this pad.
   * @return OneTimePad object holding the requested amount of chunks and size.
   */
  public static OneTimePad generatePad(int padSize, int chunkSize, String[] parties) {


    // Verfies all parties follow the "name@machine" syntax, and verifies the creator appears.
    validateParties(parties);

    // Create pad meta information
    String timeStamp = new Date(System.currentTimeMillis()).toString();

    // Generate the actual random chunks, form to a 2D byte array.
    byte[][] padContent = new byte[padSize][];
    for (int chunkId = 0; chunkId < padSize; chunkId++) {
      padContent[chunkId] = generateChunk(chunkSize);
    }

    return new OneTimePad(timeStamp, parties, padContent);
  }

  /**
   * Basic cheks to ensure the array of provided parties is sane. Each entry must follow
   * "namme@machine" convention.
   *
   * @param parties as the array of individual OTP using parties.
   */
  private static void validateParties(String[] parties) {
    for (int i = 0; i < parties.length; i++) {
      if (!parties[i].matches("[a-z|A-Z|\\-]+@[a-z|A-Z|\\-]+")) {
        throw new PadGeneratorException(
            "Party \"" + parties[i] + "\" does not follow \"name@machine\" convention.");
      }
    }
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
  protected static void persistChunk(byte[] chunk, int chunkId, File targetDir)
      throws IOException {
    String zeroPaddedChunkName = String.format("%04d", chunkId);
    String pathBuilder = targetDir + "/" + zeroPaddedChunkName;
    try (FileOutputStream fos = new FileOutputStream(pathBuilder)) {
      fos.write(chunk);
      fos.flush();
    }
  }
}
