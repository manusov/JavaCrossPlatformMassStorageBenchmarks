//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- CPR/COMMAND object definition --------------------------------------------

package massbench.cpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Command extends Raw
{
//---------- Parameters blocks and visual control ------------------------------

protected Parm[] ipb;                   // IPB = input parameters block
protected Parm[] opb;                   // OPB = output parameters block
protected ArrayList<String> log;        // array of text log strings

//---------- Parameters items --------------------------------------------------

//--- Basic parameter definition, for some types of parameters ---
public class Parm
    {
    public String name;                                 // parm. name string
    public String units = "";                           // parm. units string
    public boolean valid = true;                        // parm. validity flag
    public Parm(String s) { name = s; }                 // constructor, set name
    public boolean extract(String s) { return false; }  // extract p. from str.
    public String print() { return null; }              // print parameter
    }

//--- INTEGER parameter ---
public class ParmInt extends Parm
    {
    public int value;
    public ParmInt(String s)
        {
        super(s); 
        }
    public ParmInt(String s, int x)
        {
        super(s);
        value = x;  // set default
        }
    public ParmInt(String s1, String s2, int x)
        {
        super(s1);
        units = " " + s2;
        value = x;  // set default
        }
    @Override public boolean extract(String s)
        {
        boolean b = false;                // status flag current state = error
        UPARM u = extractUnits(s);
        int k = u.unitsMultiplier;
        s = u.parameterName;
        if ( s!=null )
            {
            if ( s.matches("[0-9]*") )
                {
                value = Integer.parseInt(s) * k;   // extract value from string
                b = true;                          // change status = no errors
                }
            }
        return b;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + value + units;
        else return name + " = n/a";
        }
    }

//--- LONG parameter ---
public class ParmLong extends Parm
    {
    public long value;
    public ParmLong(String s)
        {
        super(s); 
        }
    public ParmLong(String s, long x)
        {
        super(s);
        value = x;  // set default
        }
    public ParmLong(String s1, String s2, long x)
        {
        super(s1);
        units = " " + s2;
        value = x;  // set default
        }
    @Override public boolean extract(String s)
        {
        boolean b = false;                // status flag current state = error
        UPARM u = extractUnits(s);
        int k = u.unitsMultiplier;
        s = u.parameterName;
        if ( s!=null )
            {
            if ( s.matches("[0-9]*") )
                {
                value = Long.parseLong(s) * k;   // extract value from string
                b = true;                        // change status = no errors
                }
            }
        return b;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + value + units;
        else return name + " = n/a";
        }
    }

//--- DOUBLE parameter ---
public class ParmDouble extends Parm
    {
    public double value;
    public ParmDouble(String s)
        {
        super(s); 
        }
    public ParmDouble(String s, double x)
        {
        super(s);
        value = x;  // set default
        }
    public ParmDouble(String s1, String s2, double x)
        {
        super(s1);
        units = " " + s2;
        value = x;  // set default
        }
    @Override public boolean extract(String s)
        {
        boolean b = false;                // status flag current state = error
        UPARM u = extractUnits(s);
        int k = u.unitsMultiplier;
        s = u.parameterName;
        if ( s!=null )
            {
            if ( s.matches("[0-9]*") )
                {
                value = Double.parseDouble(s) * k;  // extract value from str.
                b = true;                           // change status = no err.
                }
            }
        return b;
        }
    @Override public String print()
        {
        if (valid) return name + " = " +
            String.format("%.3f", value) + units;
        else return name + " = n/a";
        }
    }

//--- INTEGER ARRAY parameter ---
public class ParmIntArray extends Parm
    {
    public int[] value;
    public ParmIntArray(String s)
        {
        super(s); 
        }
    public ParmIntArray(String s1, String s2)
        {
        super(s1);
        units = " " + s2;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + Arrays.toString(value) + units;
        else return name + " = n/a";
        }
    }

//--- LONG ARRAY parameter ---
public class ParmLongArray extends Parm
    {
    public long[] value;
    public ParmLongArray(String s)
        {
        super(s); 
        }
    public ParmLongArray(String s1, String s2)
        {
        super(s1);
        units = " " + s2;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + Arrays.toString(value) + units;
        else return name + " = n/a";
        }
    }

//--- DOUBLE ARRAY parameter ---
public class ParmDoubleArray extends Parm  // plus median, average, min, max
    {
    public double[] value;
    public ParmDoubleArray(String s) 
        {
        super(s);
        }
    public ParmDoubleArray(String s1, String s2)
        {
        super(s1);
        units = " " + s2;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + Arrays.toString(value) + units;
        else return name + " = n/a";
        }

/*
// TRANSFERRED TO CALC LIBRARY    
       
    public double median()
        {
        return median(value.length);
        }
    public double median(int n)
        {
        if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN;
        boolean flag = true;
        while(flag)
            {
            flag = false;
            for ( int i=0; i<(n-1); i++ )
                {
                if ( value[i] > value[i+1] )
                    {
                    double temp = value[i];
                    value[i] = value[i+1];
                    value[i+1] = temp;
                    flag = true;
                    }
                }
            }   
        if ( n%2 == 0 )
            {  // array length is EVEN, get average of central pair
            return ( value [ n/2-1 ] + value [ n/2 ] ) / 2.0;
            }
        else
            {  // array length is ODD, get central element
            return value[ n/2 ];
            }
        }
    public double average()
        {
        return average(value.length);
        }
    public double average(int n)
        {
        if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN;
        double sum = 0.0;
        for ( int i=0; i<n; i++ )
            {
            sum += value[i];
            }
        return sum/n;
        }
    public double minimum()
        {
        return minimum(value.length);
        }
    public double minimum(int n)
        {
        if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN;
        double temp = value[0];
        for ( int i=0; i<n; i++ )
            {
            if ( value[i] < temp )
                {
                temp = value[i];
                }
            }
        return temp;
        }
    public double maximum()
        {
        return maximum(value.length);
        }
    public double maximum(int n)
        {
        if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN;
        double temp = value[0];
        for ( int i=0; i<n; i++ )
            {
            if ( value[i] > temp )
                {
                temp = value[i];
                }
            }
        return temp;
        }
*/
    
    
    }

//--- STRING parameter ---
public class ParmString extends Parm
    {
    public String value;
    public ParmString(String s)
        {
        super(s); 
        }
    public ParmString(String s, String x)
        {
        super(s);
        value = x;  // set default
        }
    public ParmString(String s1, String s2, String x )
        {
        super(s1);
        value = x;
        units = " " + s2;
        }
    @Override public boolean extract(String s)
        {
        boolean b = false;
        if ( s != null )
            {
            value = s;
            b = true;
            }
        return b;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + value + units;
        else return name + " = n/a";
        }
    }

//--- ENUMERATION parameter, detect name string in the array of strings ---
public class ParmEnum extends Parm
    {
    public String[] options;
    public int value;
    public ParmEnum(String s1, String[] s2 )
        {
        super(s1);     // name of this parameter
        options = s2;  // names of options
        }
    public ParmEnum(String s1, String[] s2, int x)
        {
        super(s1);     // name of this parameter
        options = s2;  // names of options
        value = x;
        }
    public ParmEnum(String s1, String s2, String[] s3, int x)
        {
        super(s1);     // name of this parameter
        units = " " + s2;
        options = s3;  // names of options
        value = x;
        }
    @Override public boolean extract(String s)
        {
        boolean b = false;
        if ( s != null )
            {
            s = s.trim();
            for ( int i=0; i<options.length; i++ )
                {
                if ( s.equals(options[i]) )
                    {
                    value = i;
                    b = true;
                    break;
                    }
                }
            }
        return b;
        }
    @Override public String print()
        {
        if (valid) return name + " = " + value + 
           " (" + options[value] + ")" + units;
        else return name + " = n/a";
        }
    }

//--- DRAWINGS (SEQUENCING) ARRAY parameter ---
// Define operation scenario for argument values from START to STOP with STEP
public class ParmDraw extends Parm
    {
    public String parmName;
    public double valueStart, valueStop, valueStep;
    public ParmDraw( String s1, String s2 ) 
        {
        super(s1);       // name of this parameter
        parmName = s2;   // name of controlled sub-parameter
        }
    public ParmDraw( String s1, String s2, double x1, double x2, double x3 ) 
        {
        super(s1);        // name of this parameter
        parmName = s2;    // name of controlled sub-parameter
        valueStart = x1;  // defaults
        valueStop = x2;
        valueStep = x3;
        }
    
    }

//---------- Object for return by helper method for support units: K, M, G -----

private class UPARM
    {
    public final String parameterName;
    public final int unitsMultiplier;
    public UPARM ( String s , int k )
        {
        parameterName = s;
        unitsMultiplier = k;
        }
    }

//---------- Helper method for support units: K, M, G --------------------------
// string can be numeric value and units, for example "16M" means 16*1024*1024

private UPARM extractUnits(String s)
    {
    int k = 1;                     // default multiplier = 1
    if ( s != null )
        {
        int n = s.length();
        if ( ( s.endsWith("K") ) && ( n >=2 ) )    // detect "Kilo" units
            {
            s = s.substring(0,n-1);  // delete last char = units K/M/G
            k = 1024;                // set multiplier for Kilo = 1024
            }
        if ( ( s.endsWith("M") ) && ( n >=2 ) )    // detect "Mega" units
            {
            s = s.substring(0,n-1);
            k = 1024*1024;    
            }
        if ( ( s.endsWith("G") ) && ( n >=2 ) )    // detect "Giga" units
            {
            s = s.substring(0,n-1);
            k = 1024*1024*1024;    
            }
        }                   // s = string (usually numeric)
    return new UPARM(s,k);  // k = multiplier
    }


//---------- Methods for IPB and OPB fields ------------------------------------

// IPB = Input Parameters Block for Command
// IPB builded by command line parse, transmitted to command scenario
// IPB also can be received from high level script controller

// OPB = Output Parameters Block for Command
// OPB builded by command scenario execution results, visualized for user
// OPB also can be transmitted to high level script controller

public Parm[] getIPB()  // get IPB from COMMAND object
    {
    return ipb;
    }

public Parm[] getOPB()  // get OPB from COMMAND object
    {
    return opb;
    }

//---------- Method initializes this class fields by input parameters string ---
// Parse command line and setup IPB, setup menas override defaults,
// print IPB also.

public STATUS input( String s )
    {
    boolean statusFlag = true;
    String statusString = "none";
    
    if ( s==null ) return new STATUS ( statusFlag , statusString );
    
    s = s.trim();
    String[] s1 = s.split(" |=");                // " " and "=" as separators
    ArrayList<String> a1 = new ArrayList();
    for ( int i=0; i<s1.length; i++ )
        {
        if ( ! s1[i].equals("") ) 
            {
            a1.add(s1[i]);
            }
        }
    Iterator it = a1.iterator();
    String s2="?", s3="?";
    while ( statusFlag && it.hasNext() )  // this cycle for input data
        {
        statusFlag = false;               // v0.18 bug fix
        s2 = (String)it.next();           // s2 = part of input string: name
        if ( it.hasNext() )
            {
            s3 = (String)it.next();       // s3 = part of input string: value
            for ( int i=0; i<ipb.length; i++ )    // this cycle for detect
                {
                if ( s2.equals(ipb[i].name) )     // compare with IPB template
                    {
                    statusFlag = ipb[i].extract(s3);  // type-specific extract
                    }
                }
            }
        else
            {
            statusFlag = false;
            }
        }
    if ( ! statusFlag )
        {
        statusString = "PARAMETER NAME: " + s2 + ", PARAMETER VALUE: " + s3;
        }
    
    //--- Built output string with input parameters list ---
    if (statusFlag)
        {
        s = "";
        //--- Get and show IPB=arguments ---
        if ( ipb != null )
            {
            int ni = ipb.length;
            s = s + "\r\nInput parameters count: " + ni;
            for ( int i=0; i<ni; i++ )
                {
                s = s + "\r\n" + ipb[i].print();
                }
            }
        else
            {
            s = s + "\r\n" + "IPB not initialized";
            }
        statusString = s;
        }
    //--- Return ---
    return new STATUS( statusFlag , statusString );
    }

//---------- Method builts and returns full output strings table ---------------
// Used for regular output, print OPB only.

public STATUS output()
    {
    String s = "";
/*    
    //--- Get and show IPB=arguments ---
    if ( ipb != null )
        {
        int ni = ipb.length;
        s = s + "\r\nInput parameters count: " + ni;
        for ( int i=0; i<ni; i++ )
            {
            s = s + "\r\n" + ipb[i].print();
            }
        }
    else
        {
        s = s + "\r\n" + "IPB not initialized";
        }
*/
    //--- Get and show OPB=results ---
    if ( opb != null )
        {
        int no = opb.length;
        s = s + "\r\nOutput parameters count: " + no;
        for ( int i=0; i<no; i++ )
            {
            s = s + "\r\n" + opb[i].print();
            }
        }
    else
        {
        s = s + "\r\n" + "OPB not initialized";
        }
    //--- Dump ---
    // reserved
    //--- Return ---
    return new STATUS( true , s );
    }


//---------- Execute target command --------------------------------------------
// When COMMAND instantiation, 
// this empty method override by command execution method.

public STATUS execute()
    {

    return null;    
    }

}
