//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- NIO file channels test ---------------------------------------------------

package massbench.task03;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import static massbench.Calc.calcMBPS;

public class SampleChannels 
{
private ByteBuffer buffer;
// private final byte A = 0;

// eliminate duplication of this procedure when optimizing
/*
public double builtBuffer( int N )
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

public double writeFile(String s, int blockCount, int cacheMode )
    {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(s);
    long t1=0, t2=0;
    long n = buffer.capacity() * blockCount;
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
        
        t1 = System.nanoTime();
        for(int i=0; i<blockCount; i++)
            {
            buffer.rewind();
            long writeCount = 1;
            while ( writeCount > 0 )
                {
                writeCount = fc.write(buffer);
                }
            }
        t2 = System.nanoTime();
        fc.close();
        // Files.delete(path);
        }
    catch (Exception e)
        {
        System.out.println( "Write error: " + e );
        }
    return calcMBPS( t1, t2, n );
    }

public double readFile(String s, int blockCount )
    {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(s);
    long t1=0, t2=0;
    long n = buffer.capacity() * blockCount;
    try
        {
        // required changes for read synchronization
        FileChannel fc = FileChannel.open( path , READ );
        t1 = System.nanoTime();
        for(int i=0; i<blockCount; i++)
            {
            long readCount = 1;
            while ( readCount > 0 )
                {
                readCount = fc.read(buffer);
                }
            buffer.rewind();
            }
        t2 = System.nanoTime();
        fc.close();
        // Files.delete(path);
        }
    catch (Exception e)
        {
        System.out.println( "Read error: " + e );
        }
    return calcMBPS( t1, t2, n );
    }

public double copyFile(String s1, String s2, int blockCount, int cacheMode )
    {
    FileSystem fs = FileSystems.getDefault();
    Path srcPath = fs.getPath(s1);
    Path dstPath = fs.getPath(s2);
    long t1=0, t2=0;
    long n = buffer.capacity() * blockCount;
    try
        {
        // required changes for read synchronization
        FileChannel srcChannel = FileChannel.open( srcPath , READ );
        // FileChannel dstChannel = FileChannel.open( dstPath, CREATE, WRITE );
        
        FileChannel dstChannel;
        switch(cacheMode)
            {
            case 0:    // mode 0 = wb = writeback cache
                {
                dstChannel = FileChannel.open( dstPath, CREATE, WRITE );
                break;
                }
            case 1:    // mode 1 = wt = writethrough cache
                {
                dstChannel = FileChannel.open( dstPath, CREATE, WRITE, DSYNC );
                break;
                }
            default :    // mode 2 = none = uncached (under construction)
                {
                dstChannel = FileChannel.open( dstPath, CREATE, WRITE );
                }
            }
        
        t1 = System.nanoTime();
        long copyCount = 0;
        while ( copyCount < n )
            {
            copyCount += srcChannel.transferTo
                ( 0, srcChannel.size(), dstChannel );
            }
        dstChannel.force(true);    
        t2 = System.nanoTime();
        srcChannel.close();
        dstChannel.close();
        }
    catch (Exception e)
        {
        System.out.println( "Copy error: " + e );
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
