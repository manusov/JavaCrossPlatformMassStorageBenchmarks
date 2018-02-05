//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Additional library for calc. asynchronous file channels test results -----

package massbench.task04;

public class CalcAsync 
{

//---------- Print time interval -----------------------------------------------
// t1 = start (nanoseconds)
// t2 = end (nanoseconds)
// s1 = parameter name string
    
protected static void printInterval( long t1, long t2, String s1 )
    {   
    double x = t2-t1;   // x = interval from t1 to t2
    String s2 = "error, ";
    if (x<0)  { s2 = s2 + "negative time interval"; }
    if (x==0) { s2 = s2 + "too small time"; }
    if (x>0)
        {
        x /= 1000000;   // convert nanoseconds to milliseconds
        s2 = String.format("%.3f ms", x);
        }
    System.out.println( s1 + " = " + s2 );
    }

//---------- Print speed at megabytes per second -------------------------------
// t1 = start (nanoseconds)
// t2 = end (nanoseconds)
// n1 = number of bytes transferred
// s1 = parameter name string
// Returns MBPS result as double

protected static double printSpeed ( long t1, long t2, long n1, String s1 )
    {
    double x = t2-t1;
    double y = n1;
    double mbps = 0.0;
    String s2 = "error, ";
    if (x<0)  { s2 = s2 + "negative time interval"; }
    if (x==0) { s2 = s2 + "too small time"; }
    if (x>0)
        {
        x /= 1000000000;  // Nanoseconds to Seconds
        y /= 1048576;     // Bytes to Megabytes
        mbps = y / x;
        s2 = String.format( "%.3f MBPS", mbps );
        }
    System.out.println( s1 + " = " + s2 );
    return mbps;
    }

//---------- Print performance at IO operations per second ---------------------
// t1 = start (nanoseconds)
// t2 = end (nanoseconds)
// n1 = number of IO operations executed
// s1 = parameter name string
// Returns IOPS result as double

protected static double printPerformance 
        ( long t1, long t2, long n1, String s1 )
    {
    double x = t2-t1;
    double y = n1;
    double iops = 0.0;
    String s2 = "error, ";
    if (x<0)  { s2 = s2 + "negative time interval"; }
    if (x==0) { s2 = s2 + "too small time"; }
    if (x>0)
        {
        x /= 1000000000;  // Nanoseconds to Seconds
        iops = n1 / x;
        s2 = String.format( "%.3f IOPS", iops );
        }
    System.out.println( s1 + " = " + s2 );
    return iops;
    }

    
}
