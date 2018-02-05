//--- Multi-mode storage benchmarks. (C)2017 IC Book Labs ----------------------
//--- Data pattern generation library, class with data array -------------------

// REQUIRED EXTERNAL MODULE, CONSOLE APPLICATION SAVE HARDWARE GENERATED
// RANDOM NUMBERS ARRAY AS FILE.
// SEE NIOBENCH SOURCES FOR RDRAND INSTRUCTION USAGE BY JNI.

package massbench;

import java.util.Random;  // Software pseudo-random numbers generator

public class Data 
{
private byte[] data;      // storage for generated data

// Constructor with parameters:
// size of generated array, bytes
// mode: 0 = zero-fill, 1 = pseudo-random
public Data( int size, int mode )
    {
    data = new byte[size];   // create byte array of required size
    switch(mode)
        {
        case 0:  // this branch for zero-fill
            {
            for ( int i=0; i<size; i++ )
                {
                data[i]=0;
                }
            }
        case 1:   // this branch for pseudo-random
            {
            Random softRng = new Random();
            softRng.nextBytes(data);
            }
        }
    }

// Getter for data array, created by constructor
public byte[] getData()
    {
    return data;
    }
    
}
