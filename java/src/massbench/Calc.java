//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Mathematics helper library -----------------------------------------------
// Helper static library for megabytes per second and IO per second
// calculation, include median, averaging, minimum and maximum.

package massbench;

public class Calc 
{

//--- Calculate speed at Megabytes Per Second (MBPS) ---
// INPUT:  t1 = Time Start(ns), t2 = Time End(ns), bytes = size(bytes)
// OUTPUT: Return speed as DOUBLE
public static double calcMBPS( long t1, long t2, long bytes )
    {
    double megabytes = bytes / 1048576.0;         // divide by Bytes per MB
    double seconds = ( t2 - t1 ) / 1000000000.0;  // divide by ns per second
    double speed = megabytes / seconds;           // calculate MBPS
    return speed;
    }

//--- Calculate average for array ---
// INPUT: value = array of doubles
// OUTPUT: median value

// This entry point for entire array, set length = full length
public static double calcMedian( double[] value )
    {
    return calcMedian( value, value.length );
    }

// This entry point can be used for part of array, set length = required value
public static double calcMedian( double[] value , int n )
    {
    //--- Pre-checks ---
    if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN;
    //--- Execution if pre-checks OK ---
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
        {  // array length is EVEN, get average of central pair of elements
        return ( value [ n/2-1 ] + value [ n/2 ] ) / 2.0;
        }
    else
        {  // array length is ODD, get central element
        return value[ n/2 ];
        }
    }

//--- Calculate average for array ---
// INPUT: value = array of doubles
// OUTPUT: average value

// This entry point for entire array, set length = full length
public static double calcAverage( double[] value )
    {
    return calcAverage( value, value.length );
    }

// This entry point can be used for part of array, set length = required value
public static double calcAverage( double[] value , int n )
    {
    //--- Pre-checks ---
    if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN; 
    //--- Execution if pre-checks OK ---
    double sum = 0.0;
    for ( int i=0; i<n; i++ )
        {
        sum += value[i];
        }
    return sum/n;
    }

//--- Calculate minimum for array ---
// INPUT: value = array of doubles
// OUTPUT: minimum value

// This entry point for entire array, set length = full length
public static double calcMinimum( double[] value )
    {
    return calcMinimum( value, value.length );
    }

// This entry point can be used for part of array, set length = required value
public static double calcMinimum( double[] value , int n )
    {
    //--- Pre-checks ---
    if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN; 
    //--- Execution if pre-checks OK ---
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

//--- Calculate minimum for array ---
// INPUT: value = array of doubles
// OUTPUT: maximum value

// This entry point for entire array, set length = full length
public static double calcMaximum( double[] value )
    {
    return calcMaximum( value, value.length );
    }

// This entry point can be used for part of array, set length = required value
public static double calcMaximum( double[] value , int n )
    {
    //--- Pre-checks ---
    if ( ( value == null ) || ( n <= 0 ) ) return Double.NaN; 
    //--- Execution if pre-checks OK ---
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

}
