//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Helper static library for console input ----------------------------------

package massbench;

public class Input 
{

// This method for make pauses with wait user input    
public static void waitConsole()
    {
    System.out.print("\r\nWRITE-READ PAUSE, PRESS ENTER...");
    try {
        System.in.read();
        }
    catch (Exception e)
        {
        System.out.println("Input error");
        }
    System.out.print("OK\r\n");
    }

/*
public static void waitEvent()
    {
    // RESERVED
    }
*/

}
