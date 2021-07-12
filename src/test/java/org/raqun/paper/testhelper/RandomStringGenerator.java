package org.raqun.paper.testhelper;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomStringGenerator {

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomStringGenerator() {
        this.random = new SecureRandom();
        int length = 0;
        while (length < 3) {
            length = random.nextInt(10);
        }
        this.symbols = alphanum.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}
