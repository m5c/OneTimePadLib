//package eu.kartoffelquadrat.tigerencryption.otpgenerator;
//
//import java.util.Arrays;
//
//public class Cryptor {
//
//  public static String[] encrypt(String message, OneTimePad pad, String author,
//                                 int nextUnusedPadIndex)
//      throws CryptorException {
//
//    // Verify the author is legit
//    if (!Arrays.asList(pad.getParties()).contains(author)) {
//      throw new CryptorException(
//          "Message can not be decryptet. The author is not associated to this pad.");
//    }
//
//    // Verify provided pad index is legit. Must be of modulo 0 for author index. Must not exceed
//    // remaining pads amount.
//
//  }
//
//  public static String decrypt(String[] encryptedChunks, OneTimePad pad) {
//
//  }
//
//  public String xorStrings(String message, String pad) {}
//}
