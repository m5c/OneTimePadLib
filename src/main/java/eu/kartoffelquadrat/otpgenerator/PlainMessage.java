/**
 * Helper class to bundle details and content of an unencrypted message.
 *
 * @author Maximilian Schiedermeier
 */

package eu.kartoffelquadrat.otpgenerator;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;


/**
 * Plain messages are either created by the library user, for adding a message to the conversation
 * object. Or they are created by the conversation object upon export of the ful conversation
 * history.
 */
public class PlainMessage {

  // Original author of the message. This refers to the natural name, without the machine suffix.
  private final String author;

  // Party suffix, used to assiate author to a specific machine.
  private final String machine;

  // Sting encoding timestamp of message creation or decryption. The timestamp is not encryted on
  // message conversion.
  private final String creation;

  // The actual plain payload of the message
  private final String message;


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
    this(author, machine,
        new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss").format(new Date(System.currentTimeMillis())),
        message);
  }

  /**
   * Helper method to verify if a provided string contains only alphabet characters.
   *
   * @param stringToCheck as the string to test.
   * @return the input string itself.
   * @throws InvalidPartyException if the provided string contains any non alphbet characters.
   */
  private String validate(String stringToCheck) throws InvalidPartyException {
    if (!stringToCheck.matches("[a-zA-Z]+")) {
      throw new InvalidPartyException(
          "Provided string: " + stringToCheck + "is not valid for party creation");
    }
    return stringToCheck;
  }

  /**
   * Helper method to convert the individual author and machine fields into standardized "@"
   * notation.
   *
   * @return a single string separating author and machine name by the "@" symbol.
   */
  public String getPartyString() {
    return author + "@" + machine;
  }

  /**
   * Getter for the author field.
   *
   * @return the plain author name.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Getter for the machine field.
   *
   * @return the plain machine name.
   */
  public String getMachine() {
    return machine;
  }

  /**
   * Getter for the creation timestamp field.
   *
   * @return the plain moment of creation string.
   */
  public String getCreation() {
    return creation;
  }

  /**
   * Getter for the message field.
   *
   * @return the plain message payload string.
   */
  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlainMessage that = (PlainMessage) o;
    return Objects.equals(author, that.author) && Objects.equals(machine, that.machine)
        && Objects.equals(creation, that.creation) && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, machine, creation, message);
  }
}
