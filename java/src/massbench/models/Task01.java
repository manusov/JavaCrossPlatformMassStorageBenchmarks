//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Multithread memory-mapped files benchmarks -------------------------------

package massbench.models;

import massbench.Data;                      // Data array for disk read-write
import massbench.cpr.Command;               // CPR/COMMAND template for this test
import massbench.task01.SampleMappedFiles;  // Disk read-write scenario

public class Task01 extends Command 
{
//---------- Object name support -----------------------------------------------

private final String name = "Multithread memory-mapped files benchmarks";
@Override public String getName()  // support get name of this test
    {
    return name;
    }
    
//---------- Class constructor -------------------------------------------------
    
public Task01()
    {
    ipb = new Parm[]   // Assign Input Parameters Block (IPB)
        {
        new ParmString( "path" , "myfile.bin" ) ,
        new ParmInt( "size" , "bytes" , 1024*1024*100 ) ,
        new ParmInt( "threads" , 4 ) ,
        new ParmInt( "wpause" , "seconds", 3 ) ,
        new ParmInt( "rpause" , "seconds" , 5 ) ,
        new ParmEnum( "mode" , new String[] { "rw", "ro", "wo"} , 0 ) ,
        new ParmEnum( "data" , new String[] { "zero", "random" } , 0 )
        };
    opb = new Parm[]   // Assign Output Parameters Block (OPB) 
        {
        new ParmDouble( "Integral write" , "MBPS" , 0.0 ) ,
        new ParmDouble( "Integral read" , "MBPS" , 0.0 ) ,
        };
    }

//---------- Execute target command --------------------------------------------

@Override public STATUS execute()
    {
    //--- Get input parameters ---
    String path = ((ParmString)ipb[0]).value;   // file path, name, extension
    int size = ((ParmInt)ipb[1]).value;         // file size, bytes
    int threads = ((ParmInt)ipb[2]).value;      // number of execution threads
    int wpause = ((ParmInt)ipb[3]).value;       // start to write pause, sec.
    int rpause = ((ParmInt)ipb[4]).value;       // write to read pause, seconds
    int mode = ((ParmEnum)ipb[5]).value;        // mode: 0=RW, 1=RO, 2=WO
    int datamode = ((ParmEnum)ipb[6]).value;    // data: 0=Zero, 1=Random
    
    //--- Create buffer with zeroes or pseudo-random data ---
    Data data = new Data( 1024*1024 , datamode );
    byte[] dataArray = data.getData();
    
    //--- Execution ---
    SampleMappedFiles smf = new SampleMappedFiles
        ( path, size, threads, wpause, rpause, mode, dataArray );
    double[] results = smf.operation();
    
    //--- Set output parameters ---
    if ( ( results == null ) || ( results.length<2 ) )
        {
        results = new double[] { Double.NaN , Double.NaN };
        }
    ((ParmDouble)opb[0]).value = results[0];
    ((ParmDouble)opb[1]).value = results[1];
    if (mode==1) ((ParmDouble)opb[0]).valid = false;  // reject write if ro
    if (mode==2) ((ParmDouble)opb[1]).valid = false;  // reject read if wo
    
    //--- Return ---
    return new STATUS (true, "" );
    }

}
