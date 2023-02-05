/**
 * Helper class to bundle details and content of an unencrypted message.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.sql.Date;
import java.text.SimpleDateFormat;


public class PlainMessage {

  /**
   * Constructor without timestamp parameter. Automatically sets timestamp to now. Use this
   * constructor if you want to add a new message to the conversation, manually.
   *
   * @param author  as the natural name of the message creator without spaces.
   * @param machine as the client the author used while composing the message.
   * @param message as the actual message payload.
   * @throws InvalidPartyException if the provided name or machine are useing non-alphabet chars.
   */
  public PlainMessage(String author, String machine, String creation, String message)
      throws InvalidPartyException {

    // Verify author and machine contain not illegal characters (all lower, no non-alphanumerics)
    this.author = validate(author);
    this.machine = validate(machine);

    this.creation = creation;

    // store the actual message.
    this.message = message;
  }

  /**
   * Constructor without timestamp parameter. Automatically sets timestamp to now. Use this
   * constructor if you want to add a new message to the conversation, manually.
   *
   * @param author  as the natural name of the message creator without spaces.
   * @param machine as the client the author used while composing the message.
   * @param message as the actual message payload.
   * @throws InvalidPartyException if the provided name or machine are useing non-alphabet chars.
   */
  public PlainMessage(String author, String machine, String message) throws InvalidPartyException {

    // Calls extended constructor with current time as creation timestamp.
    this(author, machine, "...", message);
    SimpleDateFormat format =
        new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss").format(new Date(System.currentTimeMillis());

  }

  private String validate(String stringToCheck) throws InvalidPartyException {
    if (!stringToCheck.matches("[a-zA-Z]+")) {
      throw new InvalidPartyException(
          "Provided string: " + stringToCheck + "is not valid for party creation");
    }
    return stringToCheck;
  }

  // Original author of the message. This refers to the natural name, without the machine suffix.
  private final String author;

  // Party suffix, used to assiate author to a specific machine.
  private final String machine;

  // Sting encoding timestamp of message creation or decryption. The timestamp is not encryted on
  // message conversion.
  private final String creation;

  // The actual plain payload of the message
  private final String message;

  public String getPartyString() {
    return author + "@" + machine;
  }
}
