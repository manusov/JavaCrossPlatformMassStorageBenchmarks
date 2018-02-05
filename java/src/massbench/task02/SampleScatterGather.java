//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- NIO scatter-gather test --------------------------------------------------

package massbench.task02;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;

import static massbench.Calc.calcMBPS;

public class SampleScatterGather
{
private ByteBuffer buffer;
// private final byte A = 0;

/*
private static final String FILE = "myfile_";
private static final String EXT = ".bin";

private final String file;
private final String ext;
private final int io_size;
private final int io_block;
private final int io_count;
private final int w_delay;
private final int r_delay;
private final int mode;

public SampleScatterGather
        ( String s, int x1, int x2, int x3, int x4, int x5, int x6 )
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
    io_block = x2;
    io_count = x3;
    w_delay = x4;
    r_delay = x5;
    mode = x6;
    }
*/

/*
public double builtBuffer ( int N )
    {
    buffer = ByteBuffer.allocateDirect(N);
    long t1 = System.nanoTime();
    for ( int i=0; i<N; i++ )
        {
        buffer.put( i, A );
        }
    long t2 = System.nanoTime();
    return calcMBPS( t1, t2, N );
    }
*/

public double builtBuffer ( byte[] data )
    {
    int n = data.length;
    buffer = ByteBuffer.allocateDirect(n);
    long t1 = System.nanoTime();
    for ( int i=0; i<n; i++ )
        {
        buffer.put( i, data[i] );
        }
    long t2 = System.nanoTime();
    return calcMBPS( t1, t2, n );
    }

public double writeFileGathering
        ( String s, int blockSize, int blockCount, int cacheMode )
    {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(s);
    long t1=0, t2=0;
    long n = buffer.capacity() * blockCount;

    ByteBuffer[] buffers = new ByteBuffer[blockCount];
    buffer.rewind();
    for ( int i=0; i<blockCount; i++ )
        {
        buffers[i] = buffer.duplicate();
        }
    try
        {
        Files.createFile( path );
        FileChannel fc;
        switch(cacheMode)
            {
            case 0:    // mode 0 = wb = writeback cache
                {
                fc = FileChannel.open( path , APPEND );
                break;
                }
            case 1:    // mode 1 = wt = writethrough cache
                {
                fc = FileChannel.open( path , APPEND , DSYNC );
                break;
                }
            default :    // mode 2 = none = uncached (under construction)
                {
                // fc = FileChannel.open( path , APPEND  );
                fc = FileChannel.open( path , APPEND , DSYNC );  
                // v0.19 changes
                }
            }
        GatheringByteChannel gatherer = fc;
        t1 = System.nanoTime();
        long writteCount = 1;
        while ( writteCount > 0 )
            {
            writteCount = gatherer.write(buffers);
            }
        t2 = System.nanoTime();
        fc.close();
        // Files.delete(path);
        }
    catch (Exception e)
        {
        System.out.println( "Gathering write error: " + e );
        }
    return calcMBPS( t1, t2, n );
    }

public double readFileGathering
        ( String s, int blockSize, int blockCount, int cacheMode )
    {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(s);
    long t1=0, t2=0;
    long n = buffer.capacity() * blockCount;

    ByteBuffer[] buffers = new ByteBuffer[blockCount];
    buffer.rewind();
    for ( int i=0; i<blockCount; i++ )
        {
        buffers[i] = buffer.duplicate();
        }
    try
        {
        // required changes for read synchronization
        FileChannel fc = FileChannel.open( path , READ );
        ScatteringByteChannel scatterer = fc;
        t1 = System.nanoTime();
        long readCount = 1;
        while ( readCount > 0 )
            {
            readCount = scatterer.read(buffers);
            }
        t2 = System.nanoTime();
        fc.close();
        // Files.delete(path);
        }
    catch (Exception e)
        {
        System.out.println( "Scattering read error: " + e );
        }
    return calcMBPS( t1, t2, n );
    }

public void deleteFile(String s)
    {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(s);
    try
        {
        Files.delete(path);
        }
    catch (Exception exc)
        {
        System.out.println( "File delete error: " + exc );
        }
    }
        
        
}
