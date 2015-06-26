package Utils;

import java.math.BigInteger;

public class Constants {
    //Network types
    public static final int SCALE_FREE = 0,
                            BARABASI_ALBERT = SCALE_FREE,
                            BARABÀSI_ALBERT = BARABASI_ALBERT,
                            RANDOM = 1,
                            SMALL_WORLD = 2;

    //SIR states
    public static final int SUSCEPTIBLE = 0,
                            INFECTED = 1,
                            RECOVERED = 2;

    //Range types
    public static final int RANGE_LINEAR = 0,
                            RANGE_GAUSSIAN = 1;

    //Error constant
    public static final BigInteger ERROR_CONSTANT = new BigInteger("1000");
}
