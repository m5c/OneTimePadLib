/**
 * The default Gson library has inefficiant handling of byte errays *converts them to long CSV
 * lists. This util class provides a moded de/serializer that stores byte arrays in Hexadecimal
 * character encoding. See:
 * https://gist.github.com/orip/3635246?permalink_comment_id=2187632#gistcomment-2187632
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
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Util class with a single public method that provides Gson object for conventient One Time Pad
 * handling.
 */
public class OneTimePadSerializationTools {


  /**
   * Returns a custom Gson deserializer/serializer that encodes byte codes in hexadecimal for
   * improved readbility.
   *
   * @return Custom Gson object.
   */
  public static Gson getGsonPadConverter() {
    // Gson de/serialization is overloaded, to store disk space (better compression of byte arrays
    // contained in one time pad object)
    // See: https://gist.github.com/orip/3635246?permalink_comment_id=2187632#gistcomment-2187632
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(byte[].class,
        (JsonSerializer<byte[]>) (src, typeOfSrc, context) -> new JsonPrimitive(
            Hex.encodeHexString(src).toUpperCase()));
    builder.registerTypeAdapter(byte[].class,
        (JsonDeserializer<byte[]>) (json, typeOfT, context) -> decodeOtpJsonBytes(json));
    return builder.create();
  }

  /**
   * Helper method to properly handle conversion exception outside of lambda. See:
   * https://gist.github.com/orip/3635246?permalink_comment_id=2187632#gistcomment-2187632
   *
   * @param json as the json element to parse. This rule only applies for byte arrays.
   * @return parsed json element.
   */
  private static byte[] decodeOtpJsonBytes(JsonElement json) {
    try {
      return Hex.decodeHex(json.getAsString());
    } catch (DecoderException e) {
      throw new RuntimeException(e);
    }
  }
}
