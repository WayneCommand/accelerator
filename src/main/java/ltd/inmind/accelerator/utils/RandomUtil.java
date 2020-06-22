package ltd.inmind.accelerator.utils;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomUtil {

    private static Random random = new Random();

    /**
     * 产生[m,n]闭区间的表达式为：res = random().nextInt(n-m+1) + m；
     */
    public static String number(long length) {
        return Stream.iterate(1, seq -> seq + 1)
                .limit(length)
                .map(seq -> Integer.toString(random.nextInt(10)))
                .collect(Collectors.joining());
    }

}
