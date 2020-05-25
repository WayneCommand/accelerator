package ltd.inmind.accelerator.utils;

import java.util.Random;

public class RandomUtil {

    private static Random random = new Random();


    public static String number(long length) {
        return getNumber(length, "");
    }

    /**
     * 递归生产随机数
     * 产生[m,n]闭区间的表达式为：res = random().nextInt(n-m+1) + m；
     * @param length 长度
     * @param val 当前的值
     */
    private static String getNumber(long length, String val) {
        if (val.length() == length)
            return val;

        val += random.nextInt(10);

        return getNumber(length, val);
    }

}
