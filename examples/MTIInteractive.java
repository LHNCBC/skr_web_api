/*
  ===========================================================================
  *
  *                            PUBLIC DOMAIN NOTICE                          
  *               National Center for Biotechnology Information
  *         Lister Hill National Center for Biomedical Communications
  *                                                                          
  *  This software is a "United States Government Work" under the terms of the
  *  United States Copyright Act.  It was written as part of the authors' official
  *  duties as a United States Government contractor and thus cannot be
  *  copyrighted.  This software is freely available to the public for use. The
  *  National Library of Medicine and the U.S. Government have not placed any
  *  restriction on its use or reproduction.  
  *                                                                          
  *  Although all reasonable efforts have been taken to ensure the accuracy  
  *  and reliability of the software and data, the NLM and the U.S.          
  *  Government do not and cannot warrant the performance or results that    
  *  may be obtained by using this software or data. The NLM and the U.S.    
  *  Government disclaim all warranties, express or implied, including       
  *  warranties of performance, merchantability or fitness for any particular
  *  purpose.                                                                
  *                                                                          
  *  Please cite the authors in any work or product based on this material.   
  *
  ===========================================================================
*/

/**
 * Example program for submitting an Interactive MetaMap request.
 *
 * This example shows how to setup a basic Interactive Medical Text
 * Indexer (MTI) request.
 * 
 * @author	Jim Mork
 * @version	1.0, June 15, 2011
 **/


import java.io.*;
import gov.nih.nlm.nls.skr.*;

public class MTIInteractive
{
  public static void main(String args[])
    throws IOException, FileNotFoundException
  {
    StringBuffer inputBuf = new StringBuffer();
    String emailAddress = "youraddress@goeshere";

    if (args.length < 1) {
      // printHelp();
      System.out.println("java MTIInteractive --email-address youraddress@goeshere inputfilename");
      System.exit(1);
    }

    int i = 0; 
    while (i < args.length) {
      if (args[i].charAt(0) == '-') {
	if ( args[i].equals("--email-address") || args[i].equals("--email")) {
	  i++;
	  emailAddress = args[i];
	}
      } else {
	inputBuf.append(args[i]).append(" "); 
      }
      i++;
    }

    GenericObject myIntMTIObj = new GenericObject(300);

    // REQUIRED FIELDS:
    //    -- Email_Address
    //    -- APIText
    //
    // NOTE: The maximum length is 10,000 characters for APIText.  The
    //       submission script will reject your request if it is larger.
    //       APIText is also Required.


    if (inputBuf.length() > 0) {
      StringBuffer stringBuf = new StringBuffer();
      File inFile = new File(inputBuf.toString().trim()); 
      if (inFile.exists()) {
       	BufferedReader br = 
       	  new BufferedReader(new FileReader(inputBuf.toString().trim()));
       	String line;
       	while ((line = br.readLine()) != null) {
       	  stringBuf.append(line);
       	}
       	br.close();
      	myIntMTIObj.setField("APIText", stringBuf.toString());
      }
    }

    // myIntMTIObj.setField("Email_Address", "youraddress@goeshere");
    myIntMTIObj.setField("Email_Address", emailAddress);
    myIntMTIObj.setField("COMMAND_ARGS", "-opt1L_DCMS");

    // Submit the job request
    try
      {
	String results = myIntMTIObj.handleSubmission();
	System.out.print(results);
      } catch (RuntimeException ex) {
      System.err.println("");
      System.err.print("An ERROR has occurred while processing your");
      System.err.println(" request, please review any");
      System.err.print("lines beginning with \"Error:\" above and the");
      System.err.println(" trace below for indications of");
      System.err.println("what may have gone wrong.");
      System.err.println("");
      System.err.println("Trace:");
      ex.printStackTrace();
    } // catch
  } // main
} // class MTIInteractive
