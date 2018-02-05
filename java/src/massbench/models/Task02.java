//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Scatter-Gather file IO benchmarks ----------------------------------------

package massbench.models;

import static massbench.Calc.calcAverage;
import static massbench.Calc.calcMaximum;
import static massbench.Calc.calcMedian;
import static massbench.Calc.calcMinimum;
import massbench.Data;                          // Data array for disk read-write
import massbench.Timings;
import massbench.cpr.Command;
import massbench.task02.SampleScatterGather;

public class Task02 extends Command
{
//---------- Object name support -----------------------------------------------

private final String name = "Scatter-Gather file IO benchmarks";
@Override public String getName()  // support get name of this test
    {
    return name;
    }

//---------- Class constructor -------------------------------------------------
    
public Task02()
    {
    ipb = new Parm[]   // Assign Input Parameters Block (IPB)
        {
        new ParmString( "path" , "myfile.bin" ) ,
        new ParmInt( "size" , "bytes" , 1024*1024*100 ) ,
        new ParmInt( "block" , "bytes" ,1024*1024*10 ) ,
        new ParmInt( "count" , "files" , 10 ) ,
        new ParmInt( "wpause" , "seconds", 3 ) ,
        new ParmInt( "rpause" , "seconds" , 5 ) ,
        new ParmEnum( "mode" , new String[] { "rw", "ro", "wo"} , 0 ) ,
        new ParmEnum( "data" , new String[] { "zero", "random" } , 0 ) ,
        new ParmEnum( "cache" , new String[] { "wb", "wt", "none" } , 0 )
        };
    opb = new Parm[]   // Assign Output Parameters Block (OPB) 
        {
        new ParmDouble( "Built buffer speed" , "MBPS" , 0.0 ) ,
        new ParmDouble( "WRITE speed, median" , "MBPS" , 0.0 ) ,
        new ParmDouble( "average" , "MBPS" , 0.0 ) ,
        new ParmDouble( "minimum" , "MBPS" , 0.0 ) ,
        new ParmDouble( "maximum" , "MBPS" , 0.0 ) ,
        new ParmDouble( "READ speed, median" , "MBPS" , 0.0 ) ,
        new ParmDouble( "average" , "MBPS" , 0.0 ) ,
        new ParmDouble( "minimum" , "MBPS" , 0.0 ) ,
        new ParmDouble( "maximum" , "MBPS" , 0.0 ) ,
        };
    }

//---------- Execute target command --------------------------------------------

@Override public STATUS execute()
    {
    //--- Get input parameters ---
    String sname = ((ParmString)ipb[0]).value;   // file path, name, extension
    int size = ((ParmInt)ipb[1]).value;          // file size, bytes
    int block = ((ParmInt)ipb[2]).value;         // block size, bytes
    int count = ((ParmInt)ipb[3]).value;         // number of files
    int wpause = ((ParmInt)ipb[4]).value;        // start to write pause, sec.
    int rpause = ((ParmInt)ipb[5]).value;        // write to read pause, seconds
    int mode = ((ParmEnum)ipb[6]).value;         // mode: 0=RW, 1=RO, 2=WO
    int dataMode = ((ParmEnum)ipb[7]).value;     // data: 0=Zero, 1=Random
    int cacheMode = ((ParmEnum)ipb[8]).value;    // cache: 0=WB, 1=WT, 2=None
    
    //--- Create buffer with zeroes or pseudo-random data ---
    Data data = new Data( block , dataMode );
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
    
    //--- Start execution, create target class object ---
    String s, fullname;
    SampleScatterGather ssg = new SampleScatterGather();
    
    //--- Built buffer, store speed ---
    System.out.println("\r\n[INITIALIZING]");
    double mbps = ssg.builtBuffer( dataArray );
    ((ParmDouble)opb[0]).value = mbps;
    s = String.format ( "Built buffer , %.3f MBPS" ,  mbps );
    System.out.println(s);
    //--- Check parameters ---
    if ( size % block != 0 )
        {
        System.out.println("ERROR: size is not a multiple of block");
        }
    int blocks = size / block;
    
    //--- Initializing output arrays ---
    double[] writeMBPS = new double[count];
    double[] readMBPS = new double[count];
    
    if ( mode != 1 )              // skip write if read only
    {
        //--- Write files with pre-pause ---
        Timings.timeDelay(wpause);
        System.out.println("\r\n[WRITE]");
        for ( int i=0; i<count; i++ )
            {
            fullname = fname + "_" + i + fext;
            mbps = ssg.writeFileGathering
                ( fullname , block, blocks, cacheMode );
            s = String.format
                ( "Write %s , %.3f MBPS" , fullname, mbps );
            System.out.println(s);
            writeMBPS[i] = mbps;
            }
    
    }

    if ( mode != 2 )              // skip read and delete if write only
        {
        //--- Read files with pre-pause ---
        Timings.timeDelay(rpause);
        System.out.println("\r\n[READ]");
        for ( int i=0; i<count; i++ )
            {
            fullname = fname + "_" + i + fext;
            mbps = ssg.readFileGathering
                ( fullname , block, blocks, cacheMode );
            s = String.format
                ( "Read %s , %.3f MBPS" , fullname, mbps );
            System.out.println(s);
            readMBPS[i] = mbps;
            }
        System.out.println();
    
        //--- Delete files ---
        System.out.println("\r\n[DELETE]");
        System.out.println("Delete temporary files...");
        for ( int i=0; i<count; i++ )
            {
            fullname = fname + "_" + i + fext;
            ssg.deleteFile( fullname );
            }
        
        }
        
    //--- Set output parameters ---
    ((ParmDouble)opb[1]).value = calcMedian(writeMBPS);
    ((ParmDouble)opb[2]).value = calcAverage(writeMBPS);
    ((ParmDouble)opb[3]).value = calcMinimum(writeMBPS);
    ((ParmDouble)opb[4]).value = calcMaximum(writeMBPS);
    ((ParmDouble)opb[5]).value = calcMedian(readMBPS);
    ((ParmDouble)opb[6]).value = calcAverage(readMBPS);
    ((ParmDouble)opb[7]).value = calcMinimum(readMBPS);
    ((ParmDouble)opb[8]).value = calcMaximum(readMBPS);
    
    //--- Validate output parameters ---
    if (mode==1)                               // reject write results if ro
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
