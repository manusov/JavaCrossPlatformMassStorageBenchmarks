//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- CPR/RAW simplest basic object definition ---------------------------------
// This object is common parent class for Commands, Pins, Registers

package massbench.cpr;

public class Raw 
{

//---------- Return object name ------------------------------------------------
// This method override when class instantion.
    
public String getName()
    {
    return null;
    }
    
//---------- Status object for return to caller --------------------------------
    
public class STATUS
    {
    public final boolean b;          // error flag: FALSE=Error, TRUE=Success
    public final String s;           // status description string
    public STATUS( boolean x1, String x2 )
        {
        b = x1;
        s = x2;
        }
    }
    
}
