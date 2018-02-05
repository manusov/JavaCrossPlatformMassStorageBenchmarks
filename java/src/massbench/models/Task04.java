//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Asynchronous File Channel MBPS/IOPS benchmark ----------------------------

package massbench.models;

import massbench.Data;                          // Data array for disk read-write
import massbench.Timings;
import massbench.cpr.Command;
import massbench.task04.SampleAsync;
// import test1.task04.SampleAsyncCH;

public class Task04 extends Command 
{
//---------- Object name support -----------------------------------------------

private final String name = "Asynchronous File Channel MBPS/IOPS benchmark";
@Override public String getName()  // support get name of this test
    {
    return name;
    }
    
//---------- Class constructor -------------------------------------------------
    
public Task04()
    {
    ipb = new Parm[]   // Assign Input Parameters Block (IPB)
        {
        new ParmString( "path" , "myfile.bin" ) ,
        new ParmInt( "size" , "bytes" , 4096*4 ) ,
        new ParmInt( "count" , 10000 ) ,
        new ParmInt( "wpause" , "seconds", 3 ) ,
        new ParmInt( "rpause" , "seconds" , 5 ) ,
        new ParmEnum( "mode" , new String[] { "rw", "ro", "wo"} , 0 ) ,
        new ParmEnum( "data" , new String[] { "zero", "random" } , 0 )
        };
    opb = new Parm[]   // Assign Output Parameters Block (OPB) 
        {
        new ParmDouble( "Built buffer speed" , "MBPS" , 0.0 ) ,
        new ParmDouble( "Write speed"        , "MBPS" , 0.0 ) ,
        new ParmDouble( "Write performance"  , "IOPS" , 0.0 ) ,
        new ParmDouble( "Read speed"         , "MBPS" , 0.0 ) ,
        new ParmDouble( "Read performance"   , "IOPS" , 0.0 ) ,
        };
    }

//---------- Execute target command --------------------------------------------

@Override public STATUS execute()
    {
    //--- Get input parameters ---
    String sname = ((ParmString)ipb[0]).value;   // file path, name, extension
    int size = ((ParmInt)ipb[1]).value;          // file size, bytes
    int count = ((ParmInt)ipb[2]).value;         // number of files
    int wpause = ((ParmInt)ipb[3]).value;        // start to write pause, sec.
    int rpause = ((ParmInt)ipb[4]).value;        // write to read pause, seconds
    int mode = ((ParmEnum)ipb[5]).value;         // mode: 0=RW, 1=RO, 2=WO
    int datamode = ((ParmEnum)ipb[6]).value;     // cache: 0=WB, 1=WT, 2=None
    
    //--- Create buffer with zeroes or pseudo-random data ---
    Data data = new Data( size , datamode );
    byte[] dataArray = data.getData();
    
    //--- Extract file name and extension ---
    String fname = "" , fext = "";
    String[] sa = sname.split("\\.");
    if ( ( sa!=null ) && ( sa.length > 0 ) &&
         ( sa.length == 1 ) && ( sa[0] != null ) &&
         ( sa[0].length() > 0 ) )
        {
    fname = sa[0];
    fext = "";
        }
    else if ( ( sa!=null ) && ( sa.length > 0 ) &&
              ( sa.length == 2 ) && ( sa[0] != null ) && ( sa[1] != null ) &&
              ( sa[0].length() > 0 ) &&  ( sa[0].length() > 0 ) )
        {
        fname = sa[0];
        fext = "." + sa[1];
        }
    else
        {
        fname = "myfile";
        fext = ".bin";
        }
    
    //--- Create target class object ---
    String s;
    SampleAsync sas = new 
           SampleAsync   ( fname, fext, size, count );
        // SampleAsyncCH ( fname, fext, size, count );  // this slower
   
    //--- Built buffer, store speed ---
    System.out.println("\r\n[INITIALIZING]");
    double mbps = sas.builtBuffer( dataArray );
    ((ParmDouble)opb[0]).value = mbps;
    s = String.format ( "Built buffer , %.3f MBPS" ,  mbps );
    System.out.println(s);
    
    //--- Start execution ---
    double[] writeResult = null, readResult = null;
    //--- Write ---
    if ( mode != 1 )
        {
        //--- Write files with pre-pause ---
        Timings.timeDelay(wpause);
        writeResult = sas.write();
        }
    //--- Read ---
    if ( mode != 2 )
        {
        //--- Write files with pre-pause ---
        Timings.timeDelay(rpause);
        readResult = sas.read();
        }
    
    //--- Set output parameters ---
    if ( ( writeResult != null ) && ( writeResult.length == 2 ) )
        {
        ((ParmDouble)opb[1]).value  = writeResult[0];
        ((ParmDouble)opb[2]).value  = writeResult[1];
        }
    if ( ( readResult != null ) && ( readResult.length == 2 ) )
        {
        ((ParmDouble)opb[3]).value  = readResult[0];
        ((ParmDouble)opb[4]).value  = readResult[1];
        }
    
    //--- Validate output parameters ---
    if (mode==1)                       // reject write+copy results if ro
        {
        ((ParmDouble)opb[1]).valid = false;
        ((ParmDouble)opb[2]).valid = false;
        ((ParmDouble)opb[3]).valid = false;
        ((ParmDouble)opb[4]).valid = false;
        }
    if (mode==2)                               // reject read results if wo
        {
        ((ParmDouble)opb[5]).valid = false;
        ((ParmDouble)opb[6]).valid = false;
        ((ParmDouble)opb[7]).valid = false;
        ((ParmDouble)opb[8]).valid = false;
        }
    
    //--- Return ---
    return new STATUS (true, "" );
    }

}
