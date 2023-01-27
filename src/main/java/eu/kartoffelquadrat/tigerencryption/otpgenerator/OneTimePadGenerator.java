/**
 * This class is the only code file of the One Time Pad Generator component of the Tiger Encryption
 * suite. It creates a new folder "otp-XXX" filled with random chunks of fixed size in your work
 * directory.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.tigerencryption.otpgenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Date;
import java.text.SimpleDateFormat;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;


/**
 * Generator for one time pads. Per default each pad consists of 4096 chunks, each filled with 512
 * bytes of random data.
 */
public class OneTimePadGenerator {

  // number of bytes used as chunk size. Makes sense to keep this below 80 to prevent line break
  // tampering by email providers.
  private static final int CHUNK_SIZE = 64;

  // total number of chunks to generate in a one time pad. Keep this number high, so pad can be used
  // for longer communication.
  private static final int ONE_TIME_PAD_SIZE = 16 * 1024;

  // Name of the created target folder, storing all chunks
  private static final String ONE_TIME_PAD_NAME = "otp-XXX.json";


  /**
   * Main logic for creating a new One Time Pad and storing the contents on disk.
   *
   * @param args none.
   * @throws PadGeneratorException in case persisting the one time pad to disk encounter an error.
   */
  public static void main(String[] args) throws PadGeneratorException {

    // Create target file object. Also verify the is not yet a pad thad would be erased
    File otpTargetFile = new File(System.getProperty("user.dir") + "/" + ONE_TIME_PAD_NAME);
    if (otpTargetFile.exists()) {
      throw new PadGeneratorException("Target file \"" + ONE_TIME_PAD_NAME + "\" already exists.");
    }

    // create the one time pad, consisting of many chunks for the individual messages.
    OneTimePad pad = generatePad(args);

    // Store pad on disk
    persistPad(pad, otpTargetFile, true);
  }

  /**
   * Helper function to persiste a previously created pas a JSON object on disk.
   *
   * @param pad      as the one time pad object.
   * @param location as the target location on disk.
   * @param verbose  as indicator whether user instructions should be printed to console.
   * @throws PadGeneratorException in case the writer operation failed.
   */
  public static void persistPad(OneTimePad pad, File location, boolean verbose)
      throws PadGeneratorException {

    // Store the one time pad on disk
    try {
      String padSerialized = OneTimePadSerializationTools.getGsonPadConverter().toJson(pad);
      FileUtils.writeStringToFile(location, padSerialized);
    } catch (IOException e) {
      throw new PadGeneratorException(e.getMessage());
    }

    // Provide user feedback, if requested.
    if (verbose) {
      System.out.println("Finished creation of One-Time-Pad.\nNext steps:\n" +
          " - Replace the XXX in generated filename \"otp-XXX.json\" by a unique 3-digit number." +
          " - Copy the outcome into the \".otp\" folder of all communicating end-devices.");
    }
  }

  /**
   * Creates a one time pad (2D byte array) consisting of chunks. The chunks are meant for single
   * use encryption of the individual messages while the pad serves as unit for pretection of a
   * communication.
   *
   * @param parties as sting array descripbing the names of all parties using this pad.
   * @return OneTimePad object holding the requested amount of chunks and size.
   * @throws PadGeneratorException if one of the parties does not follow string convention.
   */
  public static OneTimePad generatePad(String[] parties) throws PadGeneratorException {
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
   * @throws PadGeneratorException if one of the provided parties does not comply convention.
   */
  public static OneTimePad generatePad(int padSize, int chunkSize, String[] parties)
      throws PadGeneratorException {


    // Verfies all parties follow the "name@machine" syntax, and verifies the creator appears.
    validateParties(parties);

    // Create pad meta information
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");
    String timeStamp = format.format(new Date(System.currentTimeMillis()));

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
   * @throws PadGeneratorException if one of the porivded parties does not follow convention.
   */
  private static void validateParties(String[] parties) throws PadGeneratorException {

    // Verify there are parties
    if (parties.length == 0) {
      throw new PadGeneratorException(
          "At least one name in format name@machine required as args[].");
    }

    // Verify all parties follow naming convention
    for (int i = 0; i < parties.length; i++) {
      if (!parties[i].matches("[a-z|A-Z|\\-]+@[a-z|A-Z|\\-]+")) {
        throw new PadGeneratorException(
            "Party \"" + parties[i] + "\" does not follow \"name@machine\" convention.");
      }
    }
  }


  /**
   * Helper function to create a new chunk of requested size, filled with random content.
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
}
