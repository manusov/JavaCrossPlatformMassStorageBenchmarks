//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Threads manager ----------------------------------------------------------

package massbench.task01;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import massbench.Calc;

public class IOManager 
{
private final int threadsCount;             // number of parallel threads
private final int fileSize;                 // memory-mapped filr size
private final String fileName;              // file name prefix
private final String fileExt;               // file name extension
private final String[] filesNames;          // array of files, for delete

private final ExecutorService ex;      // executor, used for all tasks
private final WriteTask[] wt;          // Write task for mapped file operation
private final ReadTask[] rt;           // Read task for mapped file operation
private final FutureTask[] fwt, frt;   // Wrappers for I/O tasks
private final byte[] dataArray;        // Data pattern

private long t1 = 0, t2 = 0;           // start time, stop time
private double mbps1 = 0.0, mbps2 = 0.0, ratio = 0.0; // speed thread, integral
private String s;                      // scratch string

//---------- Constructor -------------------------------------------------------

protected IOManager( int x1, int x2, String x3, String x4, byte[] x5 )
    {
    //--- Copy variables from input parameters ---
    threadsCount = x1;  // x1 = number of threads
    fileSize = x2;      // x2 = file size, bytes
    fileName = x3;      // x3 = file name, as string
    fileExt = x4;       // x4 = file extension, as string
    //--- Create threads pool and threads ---
    ex = Executors.newCachedThreadPool();      // executor
    wt = new WriteTask[ threadsCount ];        // target tasks
    rt = new ReadTask[ threadsCount ];
    fwt = new FutureTask[ threadsCount ];      // future tasks
    frt = new FutureTask[ threadsCount ];
    //--- Create individual objects for threads ---
    filesNames = new String[ threadsCount ];
    dataArray = x5;
    for ( int i=0; i<threadsCount; i++ )
        {
        String oneFileName = fileName + i + fileExt;
        filesNames[i] = oneFileName;
        wt[i] = new WriteTask( i, fileSize, oneFileName, dataArray );
        rt[i] = new ReadTask( i, fileSize, oneFileName );
        fwt[i] = new FutureTask( wt[i] );
        frt[i] = new FutureTask( rt[i] );
        }
    }

//---------- Carefully shutdown for executor -----------------------------------

protected void executorShutdown()
    {
    System.out.println("\r\n[SHUTDOWN]");
    try
        {
        System.out.println( "attempt to shutdown executor..." );
        ex.shutdown();                                 // try normal shutdown
        ex.awaitTermination( 5, TimeUnit.SECONDS );    // wait 5 seconds
        }
    catch (InterruptedException e)
        {
        System.out.println( "tasks interrupted..." );
        }
    finally
        {
        if ( !ex.isTerminated() )
            {
            System.out.println("cancel non-finished tasks...");
            }
        ex.shutdownNow();                              // forced shutdown
        System.out.println("executor shutdown finished.");
        }
    }

//---------- Delete files ------------------------------------------------------

protected void delete()
    {
    System.out.println("\r\n[DELETE]");
    for ( int i=0; i<threadsCount; i++ )
        {
        //--- Unmap buffers, for Write and Read ---
        closeDirectBuffer( wt[i].getMapBuf() );
        wt[i].setMapBuf(null);
        closeDirectBuffer( rt[i].getMapBuf() );
        rt[i].setMapBuf(null);
        //--- Delete files ---
            File file = new File( filesNames[i] );
        boolean b = file.delete();
        String s = "OK";
        if (!b) s = "FAILED";
        s = filesNames[i] + " : " + s;
        System.out.println(s);
        }
    }

private void closeDirectBuffer( ByteBuffer bb )
    {
    if ( bb==null || !bb.isDirect() ) return;
    //---
    // we could use this type cast and call functions without reflection code,
    // but static import from sun.* package is
    // risky for non-SUN virtual machine.
    //try { ((sun.nio.ch.DirectBuffer)cb).cleaner().clean(); }
    // catch (Exception ex) { }
    //---
    try 
        {
        Method cleaner = bb.getClass().getMethod("cleaner");
        cleaner.setAccessible(true);
        Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
        clean.setAccessible(true);
        clean.invoke(cleaner.invoke(bb));
        }
    catch (Exception exc)
        {
        System.out.println(exc);
        }
    }

//---------- File WRITE operation ----------------------------------------------

protected double write()
    {
    System.out.println("\r\n[WRITE]");
//--- Run threads execution ---
    for ( int i=0; i<threadsCount; i++ )
        {
        ex.execute( fwt[i] );
        }
//--- Wait all parallel operations completion ---
    try
        {
        boolean b = false;
        while(!b)
            {
//--- check all threads termination ---
            b = true;
            for ( int i=0; i<threadsCount; i++ )
                {
                b &= fwt[i].isDone();
                if (!b) break;
                }
            }
//--- Visual threads termination strings ---
        for ( int i=0; i<threadsCount; i++ )
            {
            String s = "Done " + ((IOStatus)(fwt[i].get())).comments;
            System.out.println(s);
            }
//--- Check errors and detect timings ---
        int a=0;              // error indicator, 0=No errors
        t1 = wt[0].getT1();
        t2 = wt[0].getT2();
        for ( int i=0; i<threadsCount; i++ )
            {
            a += ((IOStatus)(fwt[i].get())).status;
            long tt1 = wt[i].getT1();        // tt1 = start time of this thread
            long tt2 = wt[i].getT2();        // tt2 = stop time of this thread
            if ( tt1 < t1 ) { t1 = tt1; }    // detect minimum start time
            if ( tt2 > t2 ) { t2 = tt2; }    // detect maximum stop time
            }
        if ( a == 0 )
            {
//--- Speed calculation if no errors and visual values ---
//--- Measured integral result, mbps1 = ( total size ) / ( total time )                
            mbps1 = Calc.calcMBPS( t1, t2, fileSize * threadsCount );
            s = String.format
                ( "Integral write speed = %.3f MBPS" , mbps1 );
            System.out.println(s);
//--- Calculated hypotetic result, mbps2 = sum of speeds measured at threads ---
//--- named hypotetic because threads can execute non-parallel ---
            mbps2 = 0.0;
            for ( int i=0; i<threadsCount; i++ )
                {
                mbps2 += ((IOStatus)(fwt[i].get())).mbps;
                }
            s = String.format
                ( "Hypotetic parallel write speed = %.3f MBPS" , mbps2 );
            System.out.println(s);
//--- Parallelism ratio = ( Measured speed ) / ( Hypotetic speed ) ---
            ratio = mbps1/mbps2;
            s = String.format
                ( "Write parallelism ratio = %.3f" , ratio );
            System.out.println(s);
//--- Average and median ---
            double[] array = new double[threadsCount];
            for ( int i=0; i<threadsCount; i++ )
                {
                array[i] = ((IOStatus)(fwt[i].get())).mbps;
                }
            double median = Calc.calcMedian(array);
            double average = Calc.calcAverage(array);
            s = String.format
                ( "In-thread write speed median" + 
                  " = %.3f MBPS , average = %.3f MBPS" ,
                  median , average );
            System.out.println(s);
            double minimum = Calc.calcMinimum(array);
            double maximum = Calc.calcMaximum(array);
            s = String.format
                ( "In-thread write speed minimum" + 
                  " = %.3f MBPS , maximum = %.3f MBPS" ,
                  minimum , maximum );
            System.out.println(s);
            }
        }
//--- errors handling ---
    catch (Exception e)
        {
        System.out.println("ERROR IN MAIN TASK: " + e);
        }
    return mbps1;
    }

//---------- File READ operation -----------------------------------------------

protected double read()
    {
    System.out.println("\r\n[READ]");
//--- Run threads execution ---
    for ( int i=0; i<threadsCount; i++ )
        {
        ex.execute( frt[i] );
        }
//--- Wait all parallel operations completion ---
    try
        {
        boolean b = false;
        while(!b)
            {
//--- check all threads termination ---
            b = true;
            for ( int i=0; i<threadsCount; i++ )
                {
                b &= frt[i].isDone();
                if (!b) break;
                }
            }
//--- Visual threads termination strings ---
        for ( int i=0; i<threadsCount; i++ )
            {
            String s = "Done " + ((IOStatus)(frt[i].get())).comments;
            System.out.println(s);
            }
//--- Check errors and detect timings ---
        int a=0;              // error indicator, 0=No errors
        t1 = rt[0].getT1();
        t2 = rt[0].getT2();
        for ( int i=0; i<threadsCount; i++ )
            {
            a += ((IOStatus)(frt[i].get())).status;
            long tt1 = rt[i].getT1();        // tt1 = start time of this thread
            long tt2 = rt[i].getT2();        // tt2 = stop time of this thread
            if ( tt1 < t1 ) { t1 = tt1; }    // detect minimum start time
            if ( tt2 > t2 ) { t2 = tt2; }    // detect maximum stop time
            }
        if ( a == 0 )
            {
//--- Speed calculation if no errors and visual values ---
//--- Measured integral result, mbps1 = ( total size ) / ( total time )                
            mbps1 = Calc.calcMBPS( t1, t2, fileSize * threadsCount );
            s = String.format
                ( "Integral read speed = %.3f MBPS" , mbps1 );
            System.out.println(s);
//--- Calculated hypotetic result, mbps2 = sum of speeds measured at threads ---
//--- named hypotetic because threads can execute non-parallel ---
            mbps2 = 0.0;
            for ( int i=0; i<threadsCount; i++ )
                {
                mbps2 += ((IOStatus)(frt[i].get())).mbps;
                }
            s = String.format
                ( "Hypotetic parallel read speed = %.3f MBPS" , mbps2 );
            System.out.println(s);
//--- Parallelism ratio = ( Measured speed ) / ( Hypotetic speed ) ---
            ratio = mbps1/mbps2;
            s = String.format
                ( "Read parallelism ratio = %.3f" , ratio );
            System.out.println(s);
//--- Average and median ---
            double[] array = new double[threadsCount];
            for ( int i=0; i<threadsCount; i++ )
                {
                array[i] = ((IOStatus)(frt[i].get())).mbps;
                }
            double median = Calc.calcMedian(array);
            double average = Calc.calcAverage(array);
            s = String.format
                ( "In-thread read speed median" + 
                  " = %.3f MBPS , average = %.3f MBPS" ,
                  median , average );
            System.out.println(s);
            double minimum = Calc.calcMinimum(array);
            double maximum = Calc.calcMaximum(array);
            s = String.format
                ( "In-thread read speed minimum" + 
                  " = %.3f MBPS , maximum = %.3f MBPS" ,
                  minimum , maximum );
            System.out.println(s);
            }
        }
//--- errors handling ---
    catch (Exception e)
        {
        System.out.println("ERROR IN MAIN TASK: " + e);
        }
    return mbps1;
    }
}
