//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- NIO memory-mapped files test ---------------------------------------------

package massbench.task01;

import massbench.Timings;

public class SampleMappedFiles 
{
/*
private static final String FILE_STR = "\r\nTemporary file(s) = ";
private static final String SIZE_STR  =  "\r\nSize = ";
private static final String THREADS_STR = " MB, Threads = ";
private static final String W_DELAY_STR = "\r\nStart-Read pause = ";
private static final String R_DELAY_STR = " seconds, Read-Write pause = ";
private static final String SECONDS_STR = " seconds";
private static final String MODE_STR = "\r\nMode option = ";
*/
    
private static final String FILE = "myfile_";
private static final String EXT = ".bin";

private final String file;
private final String ext;
private final int io_size;
private final int n_threads;
private final int w_delay;
private final int r_delay;
private final int mode;
private final byte[] dataArray;

public SampleMappedFiles
    ( String s, int x1, int x2, int x3, int x4, int x5, byte[] x6 )
    {
    String[] sa = s.split("\\.");
    if ( ( sa!=null ) && ( sa.length > 0 ) &&
         ( sa.length == 1 ) && ( sa[0] != null ) &&
         ( sa[0].length() > 0 ) )
        {
        file = sa[0];
        ext = "";
        }
    else if ( ( sa!=null ) && ( sa.length > 0 ) &&
              ( sa.length == 2 ) && ( sa[0] != null ) && ( sa[1] != null ) &&
              ( sa[0].length() > 0 ) &&  ( sa[0].length() > 0 ) )
        {
        file = sa[0];
        ext = "." + sa[1];
        }
    else
        {
        file = FILE;
        ext = EXT;
        }
    io_size = x1;
    n_threads = x2;
    w_delay = x3;
    r_delay = x4;
    mode = x5;
    dataArray = x6;
    }

public double[] operation()
    {
    double[] results = new double[2];
/*
    //--- Title messages ---
    String s1 = FILE_STR + file + "<number>" + ext;
    String s2 = SIZE_STR + io_size / ( 1024 * 1024 );   // size at megabytes
    String s3 = THREADS_STR + n_threads;                // number of threads
    String s4 = W_DELAY_STR + w_delay +                 // start-write pause
                R_DELAY_STR + r_delay + SECONDS_STR +   // write-read pause
                MODE_STR + mode;                        // rw, ro, wo
    System.out.println( s1 + s2 + s3 + s4 );    // print all
*/
    //--- Create IO manager object ---
    IOManager iom = new IOManager
        ( n_threads, io_size, file, ext, dataArray );  // create
    
    //--- Skip write if READ ONLY (mode=1) ---
    if ( mode!=1 )
        //--- Start to Write pause---
        {
        Timings.timeDelay(w_delay);
        //--- Write temporary files ---
        results[0] = iom.write();
        }
    
    //--- Skip read and delete if WRITE ONLY (mode=2) ---
    if ( mode!= 2 )
        {
        //--- Write to Read pause---
        Timings.timeDelay(r_delay);
        //--- Read temporary files ---
        results[1] = iom.read();
        //--- Delete temporary files ---
        iom.delete();
        }
    
    //--- Executor shutdown ---
    iom.executorShutdown();
    //--- Done ---
    // System.out.println("operation done.\r\n");
    return results;
    }
    
}
