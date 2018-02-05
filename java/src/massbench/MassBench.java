//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Main module --------------------------------------------------------------
/*
BUGS FIX AND MODIFICATIONS:
1) Can unify iops and mbps. Y=1/X.
2) Required native application: hardware random numbers generator,
   connect it to complex as daughter console application.

*/


package massbench;

import massbench.cpr.Command;       // Unified COMMAND model definition
import massbench.cpr.Raw.STATUS;    // Structure: error flag and desc. string
import massbench.models.Task01;     // Task01 = Multithread, memory-mapped files
import massbench.models.Task02;     // Task02 = Scatter-gather IO
import massbench.models.Task03;     // Task03 = File channels
import massbench.models.Task04;     // Task04 = Asynchronous File Channels

public class MassBench 
{
private static final String APP_STR =      // Application title name
    "\r\n[ Module Mass Bench v0.20 ]" +    // JCPC means class = 
    "\r\n[ (C)2018 IC Book Labs ]\r\n";    // Java Cross-Platform Console
    
public static void main(String[] args)      // Console application entry point 
    {

//---------- Debug support, emulate command line options -----------------------

    // args = new String[]
    // { "iops" , "count=10000" , "path=C:\\tmp\\q.bin" };
    // { "sgio" };
    // { "channels" };
    // { "channels" , "cache=wt" };
    // { "channels" , "data=zero" };
    // { "channels" , "data=random" };
    // { "channels" , "size=1G"};
    // { "sgio" };
    // { "sgio" , "size=20M" };
    // { "mode=ro" };
    // { "path=c:\\tmp\\tempfile.bin" , "size=5M" , "threads=10" , "pause=0" };

//---------- Detect input ------------------------------------------------------
// s1 = message about command line parameters
// s2 = test name, first word at command line
// s3 = command line parameters exclude first word = test name
        
    String s1, s2, s3;
    if ( ( args == null ) || ( args.length == 0 ) || ( args[0] == null ) )
        {
        s1 = "No command line parameters, run default mode...";
        s2 = "";
        s3 = "";
        }
    else 
        {
        s1 = "Command line parameters detected...";
        s2 = args[0];
        s3 = "";
        int n = args.length;
        for ( int i=1; i<n; i++ )
            {
            s3 = s3 + args[i] + " ";
            }
        }
    System.out.println( APP_STR + s1 );  // First string = name + option mode

//---------- Create task, show CPR\Command name --------------------------------

    Command task;               // Define benchmark task as CPR\COMMAND object
    s2 = s2.trim();             // String s2 = name of benchmark scenario
    switch(s2)                  // select benchmark scenario by name
        {
        case "" :               // also as default
        case "map" : 
            {
            task = new Task01();   // Task01 = Multithread, memory-mapped files
            break;
            }
        case "sgio" :
            {
            task = new Task02();   // Task02 = Scatter-gather IO
            break;
            }
        case "channels" :
            {
            task = new Task03();   // Task03 = File channels
            break;
            }
        case "iops" :
            {
            task = new Task04();   // Task04 = Asynchronous File Channels
            break;
            }
        default:                   // Handling error: unknown scenario name
            {
            System.out.println("Scenario name is unknown: " + s2);
            return;
            }
        }
    String moduleName = task.getName();    // Get name from selected task class
    System.out.println( moduleName );      // Visual CPR command module name

//---------- Set Command input -------------------------------------------------
// Note IPB = Input Parameters Block, OPB = Output Parameters Block
// IPB builds by command line parameters and passed to scenario executor
// OPB builds by scenario executor results and passed to user interface

    STATUS st = task.input(s3);                // s3 = command line parms
    // System.out.println("\r\n[IPB]" + st.s );   // Visual build IPB results
    System.out.printf("\r\n[IPB]");
    
//---------- Execute Command if input correct ----------------------------------    
    
    if ( !st.b )              // Check status after build IPB
        {                     // If build IPB failed, visual error description
        System.out.println("\r\nINPUT ERROR: " + st.s );
        }
    else
        {                     // If build IPB success, execute selected test
        System.out.println( st.s );
        st = task.execute();  // main part of execution flow
        if ( !st.b )          // Check execution status
            {                 // If test failed, visual error description
            System.out.println("EXECUTION ERROR: " + st.s );
            }    

//---------- Get Command output if executed success ----------------------------

        else                    // This ELSE means no errors at EXECUTION phase
            {
            st = task.output();  // regular output from agent to station
            if ( !st.b )         // check errors at STATUS phase
                {                // Visual status phase errors if detected
                System.out.println("OUTPUT ERROR: " + st.s );
                System.out.println();
                }
            else                // This ELSE means no errors at STATUS phase
                {
                System.out.println("\r\n[OPB]" + st.s );
                System.out.println();
                }    // This condition "{" for STATUS phase validity
            }        // This condition "{" for EXECUTION phase validity
        }            // This condition "{" for INPUT phase validity
    }                // This "{" for main method
}                    // This "{" for main class 
