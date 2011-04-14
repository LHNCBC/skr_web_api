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
 * Example program for submitting a new Generic Batch with Validation job
 * ("GenericObject(true)" turns on validation) request to the Scheduler to run.
 * You will be prompted for your username and password and if they are alright,
 * the job is submitted to the Scheduler and the results are returned in the
 * String "results" below.
 *
 * NOTE: There is no Interactive facility for Generic jobs at this point.
 *
 * This example shows how to setup a basic Generic Batch with Validation job
 * with a small file (sample.txt) with ASCII MEDLINE formatted citations as
 * input data. You must set the Email_Address variable and use the UpLoad_File
 * to specify the data to be processed.  This example also shows the user
 * setting the silentEmail option which tells the Scheduler to NOT send email
 * upon completing the job.
 *
 * This example is set to run the MTI (Medical Text Indexer) program using
 * the -opt1_DCMS and -E options. You can also setup any environment variables
 * that will be needed by the program by setting the Batch_Env field.
 * 
 * @author	Jim Mork
 * @version	1.0, September 18, 2006
**/


import java.io.*;
import gov.nih.nlm.nls.skr.*;

public class GenericBatch
{
   public static void main(String args[])
   {
        GenericObject myGenericObj = new GenericObject();

        // NOTE: You MUST specify an email address because it is used for
        //       logging purposes.

        myGenericObj.setField("Email_Address", "wjrogers@mail.nih.gov");
        myGenericObj.setFileField("UpLoad_File", "./sample.txt");
        myGenericObj.setField("Batch_Command", "metamap -% format -E");
        myGenericObj.setField("BatchNotes", "SKR API test");
        myGenericObj.setField("silentEmail", false);

        // Submit the job request

        try
        {
           String results = myGenericObj.handleSubmission();
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
} // class GenericBatch
