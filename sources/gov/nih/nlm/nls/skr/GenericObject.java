package gov.nih.nlm.nls.skr;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.PasswordAuthentication;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import gov.nih.nlm.nls.util.Authenticator;
import gov.nih.nlm.nls.util.PostUtils;
import gov.nih.nlm.nls.cas.CasAuth;

/**
 * Generic job specific fields and handling routines.
 *<br><br>
 * NOTES:<br>
 *    1. Generic jobs can only be Batch jobs, we don't support Interactive
 *       Generic jobs.
 *<br><br>
 *    2. Generic jobs must have commands that reside in /nfsvol/nls/bin<br>
 *       Specified via the Batch_Command field.  For example, 
 *       'setField("Batch_Command", "MTI -opt1_DCMS -E")'.
 *<br><br>
 *    3. Commands for the Generic jobs must not have ".." embedded in their path
 *<br><br>
 *    4. Generic jobs can be normal (without validation), or with validation
 *       where the Scheduler expects an "<< EOT >>" end of result marker so
 *       it can verify it received a complete result.
 *<br><br>
 *    5. Generic jobs also allow you to specify any special environment<br>
 *       variables you might need inorder to run your command.
 *       Specified via the Batch_Env field.  For example, 
 *       'setField("Batch_Env", "NLS=/nfsvol/nls#CC=gcc")'.  Where each
 *       environment variable is separated by a "#". Note that these are not
 *       needed to run the MTI command in the above example, but are here for
 *       illustrative purposes only.
 *
 * @author	Jim Mork
 * @version	1.0, September 18, 2006
 */

public class GenericObject
{
  /** url of cas authentication server, property: skrapi.cas.serverurl */
  public final String casAuthServer =
    System.getProperty("skrapi.cas.serverurl",
		       "https://utslogin.nlm.nih.gov/cas/v1/tickets");
  /** url of skr api service, property:  skrapi.serviceurl*/
  public final String service =
    System.getProperty("skrapi.serviceurl",
		       "http://skr.nlm.nih.gov/cgi-bin/SKR/Restricted_CAS/API_batchValidationII.pl");
  // public String service = "http://indlx1.nlm.nih.gov:8000/perl/batch_validation.pl";
  /** cas service ticket */
  private String serviceTicket = "";
  /** authenticator class name, property: nls.service.authenticator,
   * default get username and password from console : @see gov.nih.nls.util.ConsoleAuthImpl
   * see also java.net.Authenticator and java.net.PasswordAuthentication
   */
  private String authenticatorClassName = 
    System.getProperty("nls.service.authenticator", "gov.nih.nlm.nls.util.ConsoleAuthImpl");
  /** get the password for CAS using this method */
  private Authenticator authenticator = null;
  /** container for username and password */
  private PasswordAuthentication pa = null;
  /** service ticket timestamp, when ticket was acquired. */
  private Calendar ticketTimeStamp = Calendar.getInstance();
  /** service ticket timeout: default 8 minutes */
  public final static int ticketTimeout =
    Integer.parseInt(System.getProperty("skrapi.cas.ticket.timeout", "480000"));
  /** storage for form elements */
  Map<String,ContentBody> formMap = new HashMap<String,ContentBody>();
  // MultipartEntity formEntity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
 

  // ************************************************************************

  /** default constructor */
  public GenericObject() {
    this.promptCredentials();
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(), new String(this.pa.getPassword()), service);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("RUN_PROG", new StringBody
		       ("GENERIC", "text/plain", Charset.forName( "UTF-8" ))); // no validation
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a new GenericObject object using the specified information.  This
   * version prompts the user for the username and password via the command
   * line with the password covered by "*" as it's typed.
   * 
   * @param  withValidation  Validate results?
   */
  public GenericObject(boolean withValidation)
  {
    this.promptCredentials();
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(), new String(this.pa.getPassword()), service);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
      if(withValidation)
	this.formMap.put("RUN_PROG", new StringBody
			 ("GENERIC_V", "text/plain", Charset.forName( "UTF-8" ))); // GENERIC_V w/ validation
      else
	this.formMap.put("RUN_PROG", new StringBody
			 ("GENERIC", "text/plain", Charset.forName( "UTF-8" ))); // no validation
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // GenericObject

  // ************************************************************************

  /** Prompt user for username/password. */
  void promptCredentials() {
    try {
      Class authenticatorClass = Class.forName(this.authenticatorClassName);
      // Authenticator.setDefault((Authenticator)authenticatorClass.newInstance());
      this.authenticator = (Authenticator)authenticatorClass.newInstance();
    } catch (java.lang.ClassNotFoundException exception) {
      System.err.println("Class " + authenticatorClassName + " not found!");
      exception.printStackTrace(System.err);
    } catch (java.lang.InstantiationException exception) {
      System.err.println("Unable to instantiate Class " + authenticatorClassName);
      exception.printStackTrace(System.err);
    } catch (java.lang.IllegalAccessException exception) {
      System.err.println("Illegal access of Class " + authenticatorClassName);
      exception.printStackTrace(System.err);
    }
  }

  /**
   * Print content of entity.
   *
   * @param respEntity http response entity
   * @return string containing content of entity.
   */
  String printEntity(HttpEntity respEntity)
    throws IOException
  {
    if (respEntity.getContentType().equals("text/html")) {
      StringBuffer sb = new StringBuffer();
      BufferedReader br = new BufferedReader(new InputStreamReader(respEntity.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
	sb.append(line).append('\n');
      }
      br.close();
      System.out.print("response content: " + sb.toString());
      return sb.toString();
    } 
    return null;
  }

  /**
   * Determine if email entry of form has a well formed email address.
   * @parm addressBody content of email entry of form.
   * @return true if email is well formed.
   */
  public boolean emailIsWellFormed(ContentBody addressBody) {
    if (addressBody != null)
      if (addressBody instanceof StringBody) {
	if (((StringBody)addressBody).getContentLength() == 0) {
	  return false;
	} else {
	  try {
	    StringBuilder sb = new StringBuilder();
	    BufferedReader reader =
	      new BufferedReader(((StringBody)addressBody).getReader());
	    String line;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line);
	    }
	    reader.close();
	    String address = sb.toString();
	    if (address.indexOf("@",1) == -1) { return false; }
	    return true;
	  } catch (IOException e) {
	    System.err.println("problems reading address string body");
	    e.printStackTrace();
	    throw new RuntimeException(e);
	  }
	}
      }
    return false;
  }

  /**
   * Determine if email entry of form is valid.
   * @return true if email is valid.
   */
  public boolean validEmail() {
    return this.formMap.containsKey("Email_Address") &&
      this.emailIsWellFormed(this.formMap.get("Email_Address"));
  }

  // ************************************************************************

  /**
   * Control the Batch job submission after validating command.
   * handleSubmission checks to make sure the command being run is valid and
   * then calls the JobObj routine to do the actual handling of the job.
   *
   * @return string containing content of server response.
   */
  public String handleSubmission()
  {
    HttpClient client = new DefaultHttpClient();
    try {
      // check service ticket age
      if (Calendar.getInstance().compareTo(this.ticketTimeStamp) > ticketTimeout) {
	// get a new ticket and reset timestamp
	this.serviceTicket =
	  CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
			    new String(this.pa.getPassword()), service);
	this.ticketTimeStamp = Calendar.getInstance();
      }
      if (this.validEmail()) {
	MultipartEntity formEntity = PostUtils.buildMultipartEntity( this.formMap );
	HttpPost post = new HttpPost(this.service + "?ticket=" + this.serviceTicket);
	post.setEntity(formEntity);
	// System.out.println("post request: " + post.getRequestLine() );
	HttpResponse response = client.execute(post);
	if (response.getStatusLine().getStatusCode() == 302) {
	  // System.out.println("PAGE :" + EntityUtils.toString(response.getEntity()));
	  EntityUtils.consume(response.getEntity()); // consume response input to release connection.
	  // ignore 302 redirect and resubmit request with ticket.
	  post = new HttpPost(this.service + "?ticket=" + this.serviceTicket);
	  post.setEntity(formEntity);
	  // System.out.println("post request: " + post.getRequestLine() );
	  response = client.execute(post);
	  HttpEntity respEntity = response.getEntity();
	  if (respEntity != null) {
	    StringBuffer rtn = new StringBuffer();
	    BufferedReader in = new BufferedReader(new InputStreamReader(respEntity.getContent()));
	    String line = "";
	    while((line = in.readLine()) != null)
	      {
		if(!line.startsWith("NOT DONE LOOP")) {
		  rtn.append(line);
		  rtn.append("\n");
		} // fi
	      }
	    return rtn.toString();
	  }
	} else {
	  HttpEntity respEntity = response.getEntity();
	  if (respEntity != null) {
	    StringBuffer rtn = new StringBuffer();
	    BufferedReader in = new BufferedReader(new InputStreamReader(respEntity.getContent()));
	    String line = "";
	    while((line = in.readLine()) != null)
	      {
		if(!line.startsWith("NOT DONE LOOP")) {
		  rtn.append(line);
		  rtn.append("\n");
		} // fi
	      }
	    return rtn.toString();
	  }
	}
      } else {
	System.err.println("Error: Email Address must be specified");
	throw new RuntimeException();
      }
    } catch (java.io.UnsupportedEncodingException e) {
      //LOG.warning(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      client.getConnectionManager().shutdown();
    }
    return null;
  } // handleSubmission

  // ************************************************************************

  /**
   * Validating the Generic Batch command.
   * This routine ensures that the command specified actually resides in the
   * /nfsvol/nls/bin directory and doesn't contain any ".." path redirections.
   * If both of these conditions are met, validBatchCommand returns true.
   *
   * @return   boolean determination of whether command is valid or not
   */
  private boolean validBatchCommand()
  {
    //    NameValuePair CommandPair = this.formEntity.get("Batch_Command");
    //    String Command = CommandPair.getValue();
    String Command = "skr";
    if(Command.indexOf("..") == -1)
      {
	int pos = Command.indexOf(' ');
	String t2 = Command;
	if(pos > -1)
	  t2 = Command.substring(0, pos);
	String pathname = "/nfsvol/nls/bin/" + t2;
	File file = new File(pathname);
	boolean exists = file.exists();

	if(exists)
	  return(true);
	else
          {
	    System.err.println("Error: Unable to find command" +
			       " in /nfsvol/nls/bin: #" + t2 + "#");
	    throw new RuntimeException();
          } // else
      } // fi

    else
      {
	System.err.println("Error: Batch Command contains \"..\"");
	System.err.println("       Batch Command: " + Command);
	throw new RuntimeException();
      } // else
  } // validBatchCommand

  // ************************************************************************

  /**
   * Insert and configure Generic Batch specific fields into the fieldsList.
   * This builds on the default fields that are already in the list via the
   * JobObj module.
   */
  public void initFields()
  {
    // Note: In RFC 2388 - Returning Values from Forms:
    // multipart/form-data only field name and field value information
    // is transmitted in the http request.  All other information is
    // discarded.  
    try {
      this.formMap.put("Batch_Command", new StringBody
		       ("", "text/plain", Charset.forName( "UTF-8" )));
      
      this.formMap.put("Batch_Env", new StringBody
		       ("", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // initFields

  // ************************************************************************

  /**
   * Set the stringFieldValue of the requested field/option for this job.
   *
   * @param  fieldName   Name of the field to be updated
   * @param  fieldValue  New String value for the field
   */
  public void setField(String fieldName, String fieldValue)
  {
    try {
      this.formMap.put(fieldName, new StringBody
		       (fieldValue, "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // setField

  // ************************************************************************

  /**
   * Set the booleanFieldValue of the requested field/option for this job.
   *
   * @param  fieldName    Name of the field to be updated
   * @param  fieldState  New String value for the field
   */
  public void setField(String fieldName, boolean fieldState)
  {
    try {
      this.formMap.put(fieldName, new StringBody
		       (Boolean.toString(fieldState), "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // setField


  // ************************************************************************

  /**
   * Set file field of the requested field/option for this job.
   *
   * @param  fieldName     Name of the field to be updated
   * @param  localFilename Name of file to add to POST request
   */
  public void setFileField(String fieldName, String localFilename)
  {
    File localFile = new File(localFilename);
    this.formMap.put(fieldName, new FileBody( localFile, "text/html" ));
  } // setFileField

} // class GenericObject
