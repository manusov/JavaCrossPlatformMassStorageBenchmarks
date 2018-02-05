//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Operation status object with it builder in constructor -------------------

package massbench.task01;

import massbench.Calc;

public class IOStatus 
{
    
protected final int status;       // execution status, 0=No errors
protected final int num;          // number of this thread
protected final int iosize;       // size of memory mapped file
protected final double mbps;      // spped result at megabytes per second
protected final String comments;  // comments string about operation results

protected IOStatus
    ( int x1, int x2, int x3, long t1, long t2, String s1, String s2 )
    {
    status = x1;
    num = x2;
    iosize = x3;
    if ( status == 0 )
//--- This executed if no errors ---
        {
        mbps = Calc.calcMBPS( t1, t2, iosize );
        String speed = String.format( ", speed = %.3f MBPS" , mbps );
        comments = "thread = " + num + ", file = " + s1 + speed;
        }
    else
//--- This executed if error detected ---
        {
        mbps = 0.0;
        comments = 
        "thread = " + num + ", file = " + s1 + ", error = " + status +
        "\r\nException = " + s2;
        }
    }
}
