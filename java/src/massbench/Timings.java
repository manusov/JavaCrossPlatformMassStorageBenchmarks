//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Helper static library for time delays ------------------------------------

package massbench;

public class Timings 
{

// Time dealy
// INPUT: seconds value for delay
public static void timeDelay( int seconds )
    {
    // Message about delay and seconds value
    System.out.print("\r\n[DELAY " + seconds + " SECONDS] ");
    for ( int i=0; i<seconds; i++ )
        {
        try
            {
            Thread.sleep(1000);      // 1000 ms = 1 second
            }
        catch (Exception exc)
            {
            System.out.println(exc);
            }
        System.out.print(">");       // Print this each second
        }                            // Cycle for seconds with console output
    System.out.print(" DONE\r\n");
    }
}
