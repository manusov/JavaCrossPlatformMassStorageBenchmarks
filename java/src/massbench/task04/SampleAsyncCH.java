//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- NIO asynchronous file channel test, with CH = Completion Handler ---------

package massbench.task04;

import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import java.util.concurrent.CountDownLatch;
import static massbench.task04.CalcAsync.printInterval;
import static massbench.task04.CalcAsync.printPerformance;
import static massbench.task04.CalcAsync.printSpeed;

public class SampleAsyncCH extends SampleAsync 
{

private CountDownLatch msg; 
private Object attachment;
    
public SampleAsyncCH ( String s1, String s2, int x1, int x2 )
    {
    super( s1, s2, x1, x2 );
    }
    
@Override public double[] write()
    {
    System.out.println("\r\n[WRITE]");
    double[] result = new double[] { Double.NaN , Double.NaN };
    long startSync = 0 , stopSync = 0 , stopAsync = 0;
    msg = new CountDownLatch( filesCount );
    //--- Create channels pool ---
    try 
        {
        for( int i=0; i<filesCount; i++ ) 
            {
            paths[i] = Paths.get( pathString + i + extString );
            writeChannels[i] =
                AsynchronousFileChannel.open( paths[i], CREATE , WRITE );
        }
    //--- Write requests injection ---
        buffer.rewind();
        // make this for one buffer, not all buffers, not in the measured time
        startSync = System.nanoTime(); // start of requests injection
                                       // also start of execution because async
        for( int i=0; i<filesCount; i++ ) 
            { 
            ByteBuffer bufferInstance = buffer.duplicate();
/*            
            operations[i] = writeChannels[i].write( bufferInstance, 0 );
*/
            writeChannels[i].write( bufferInstance, 0, 
                attachment, new CompletionHandler<Integer,Object>()
                {
                @Override
                public void completed( Integer result, Object attachment )
                    {
                    msg.countDown();
                    }
                @Override
                public void failed( Throwable e2, Object attachment )
                    {
                    System.out.println( "Failed write completion" );
                    }
                });
            }
        
        stopSync = System.nanoTime();   // end of requests injection
        
    //--- Wait write complete ---
    // if minimum one task works, set true for still wait
    // this causes big random delay, better completionHandler ?
/*    
        boolean flag = true;
        while( flag )
            {
            flag = false;
            for( int i=0; i<filesCount; i++ )
                {  
                if ( !operations[i].isDone() )
                    {
                    flag = true; 
                    break;
                    }
                }  
            }
*/
        try
            {
            msg.await();
            }
        catch (Exception e)
            {
            System.out.println("Error: countdown write wait interrupted.");
            }

        stopAsync = System.nanoTime();    // end of requests execution
    //--- Show write results ---
        printInterval
            ( startSync , stopSync  , "Write requests injection" );
        printInterval
            ( stopSync  , stopAsync  , "Write requests completion" );
        printInterval
            ( startSync  , stopAsync , "Write process full time" );
        result[0] = printSpeed 
            ( startSync , stopAsync , dataSize , "Write speed" );
        result[1] = printPerformance
            ( startSync , stopAsync , filesCount , "Write performance" );
        printPerformance
            ( startSync , stopSync , filesCount , 
              "Write requests send performance" );
        }
    //--- Errors handling ---
        catch (Exception e1)
            {
            System.out.println( "Error: " + e1 ); 
            }
    //--- Delete temporary files, close channels ---
        finally 
            {
            System.out.println("\r\n[CLOSE WRITE]");         
            for( int i=0; i<filesCount; i++ ) 
                { 
                try { 
                    writeChannels[i].close();
                    }
                catch (Exception e2) 
                    { System.out.println( "Close error: " + e2 ); }
                } 
            }
        System.out.println("Done.");
        //--- Return ---
        return result;    
    }


@Override public double[] read()
    {
    System.out.println("\r\n[READ]");
    double[] result = new double[] { Double.NaN , Double.NaN };
    long startSync = 0 , stopSync = 0 , stopAsync = 0;
    msg = new CountDownLatch( filesCount );
    //--- Create channels pool ---
    try 
        {
        for( int i=0; i<filesCount; i++ ) 
            {
            paths[i] = Paths.get( pathString + i + extString );
            readChannels[i] =
                AsynchronousFileChannel.open( paths[i], READ );
            }
    //--- Read requests injection ---
        buffer.rewind();
        // make this for one buffer, not all buffers, not in the measured time
        startSync = System.nanoTime(); // start of requests injection
                                       // also start of execution because async
        for( int i=0; i<filesCount; i++ ) 
            { 
            ByteBuffer bufferInstance = buffer.duplicate();
            // operations[i] = readChannels[i].read( bufferInstance, 0 );
            
            readChannels[i].read( bufferInstance, 0, 
                attachment, new CompletionHandler<Integer,Object>()
                {
                @Override
                public void completed( Integer result, Object attachment )
                    {
                    msg.countDown();
                    }
                @Override
                public void failed( Throwable e2, Object attachment )
                    {
                    System.out.println( "Failed read completion" );
                    }
                });

            }
        
        stopSync = System.nanoTime();   // end of requests injection
    //--- Wait read complete ---
    // if minimum one task works, set true for still wait
    // this causes big random delay, better completionHandler ?
/*
        boolean flag = true;
        while( flag )
            {
            flag = false;
            for( int i=0; i<filesCount; i++ )
                {  
                if ( !operations[i].isDone() )
                    {
                    flag = true; 
                    break;
                    }
                }  
            }
*/
        try
            {
            msg.await();
            }
        catch (Exception e)
            {
            System.out.println("Error: countdown read wait interrupted.");
            }

        stopAsync = System.nanoTime();    // end of requests execution
    //--- Show write results ---
        printInterval
            ( startSync , stopSync  , "Read requests injection" );
        printInterval
            ( stopSync  , stopAsync  , "Read requests completion" );
        printInterval
            ( startSync  , stopAsync , "Read process full time" );
        result[0] = printSpeed 
            ( startSync , stopAsync , dataSize , "Read speed" );
        result[1] = printPerformance
            ( startSync , stopAsync , filesCount , "Read performance" );
        printPerformance
            ( startSync , stopSync , filesCount , 
              "Read requests send performance" );
        }
    //--- Errors handling ---
        catch (Exception e1)
            {
            System.out.println( "Error: " + e1 ); 
            }
    //--- Delete temporary files, close channels ---
        finally 
            {
            System.out.println("\r\n[DELETE]");         
            for( int i=0; i<filesCount; i++ ) 
                { 
                try {
                    Files.delete( paths[i] );
                    readChannels[i].close();
                    }
                catch (Exception e2) 
                    { System.out.println( "Close error: " + e2 ); }
                } 
            }
        System.out.println("Done.");
        //--- Return ---
        return result;    
    }
    
}
