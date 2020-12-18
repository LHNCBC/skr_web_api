package gov.nih.nlm.nls.util;

import java.io.IOException;
import java.net.PasswordAuthentication;

/**
 * Provides username and password input on the terminal the program
 * was launched from with the prompts shown below:
 * 
 * <pre> 
 *  Enter your Username: username
 *  Enter your password: *************
 * </pre> 
 *
  * Created: Thu Apr  7 10:37:48 2011
 *
 * @author <a href="mailto:wjrogers@mail.nih.gov">Willie Rogers</a>
 * @version 1.0
 */
public class ConsoleAuthImpl extends Authenticator {
  public PasswordAuthentication getPasswordAuthentication() {
    char password[] = null;
    String username = null;
    try {
      username = Utils.getTextInput("Enter your Username: ");
      password = PasswordField.getPassword("Enter your password: ");
      if (password == null) {
	System.err.println("Error: Password string is empty!");
	throw new RuntimeException("Error: Password string is empty!");
      }
      return new PasswordAuthentication(username, password);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } // catch
    return null;
  }
}

