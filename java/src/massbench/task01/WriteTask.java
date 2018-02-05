//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Task for one thread, write operation, object for external management -----

package massbench.task01;

import java.util.concurrent.Callable;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class WriteTask implements Callable<IOStatus>  
{
private final int num;              // id for this thread exemplar
private final int fileSize;         // memory-mapped file size
private final String fileName;      // memory-mapped file name_id.ext
private final byte[] dataArray;     // Data pattern

private int status = 0;             // 0=No errors, otherwise error code
private long t1 = 0, t2 = 0;        // t1=start time, t2=stop time
private String excName = "n/a";     // text description of exception error

private MappedByteBuffer mapBuf;
    
//--- Constructor ---
protected WriteTask ( int x1, int x2, String x3, byte[] x4 )
    {
    num = x1;         // id for this thread exemplar
    fileSize = x2;    // memory mapped file size
    fileName = x3;    // file name prefix + id + extension
    dataArray = x4;
    }    
        
//--- Target file IO operation ---
@Override public IOStatus call()
    {
    try
        {
//--- Built buffer, initializing memory mapped file ---
        FileChannel fc =        // create file channel 
            new RandomAccessFile( fileName , "rw" ).getChannel();
        // map channel to read-write buffer 
        mapBuf = fc.map( FileChannel.MapMode.READ_WRITE, 0, fileSize );
//--- Cycle for write buffer ---
        int k=0;
        for ( int i=0; i<fileSize; i++ )
            {
            byte x = dataArray[k++];
            if ( k >= dataArray.length )
                {
                k = 0;
                }
            mapBuf.put( (byte)x );  // fill buffer by '+' char
            }
//--- Write force ---
        t1 = System.nanoTime();  // t3 = start write file
        mapBuf.force();          // This must physically write disk
        t2 = System.nanoTime();  // t4 = end write file
        fc.close();
        }
//--- Errors reporting ---
    catch (Exception e)
        {
        status = 1;              // set error code
        excName =  "" + e;       // write exception description string
        }
//--- Return status object, note t1, t2 ignored ---
    return new IOStatus( status, num, fileSize, t1, t2, fileName, excName );
    }

//--- Get start time, used by coordinator by for integral measurement ---
protected long getT1()
    {
    return t1;
    }

//--- Get stop time, used by coordinator by for integral measurement ---
protected long getT2()
    {
    return t2;
    }

//--- Get created mapped buffer, required for release it and file delete ---
protected MappedByteBuffer getMapBuf()
    {
    return mapBuf;
    }

//--- Set mapped buffer, used set null for safe unmap it by caller ---
protected void setMapBuf(MappedByteBuffer x)
    {
    mapBuf = x;
    }

}
