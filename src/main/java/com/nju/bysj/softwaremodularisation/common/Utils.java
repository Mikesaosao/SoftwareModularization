package com.nju.bysj.softwaremodularisation.common;


import java.io.FileFilter;
import java.util.Locale;

public class Utils {

    public static FileFilter javaAndDirectoryFilter = pathname -> (pathname.getName().endsWith(".java") || pathname.isDirectory())
            && !pathname.getName().toLowerCase(Locale.ROOT).endsWith("test");

    private static double calculateMaxQualityPercentage(double distance) {
        return 1 - Math.sqrt(distance *  distance / 3.0);
    }

    public static void main(String[] args) {
//        double[] input = {
//                1.2153,
//                1.3534,
//                1.112,
//                1.4482,
//                1.2114,
//                1.2906,
//                1.1228,
//                1.2916,
//                1.2357,
//                1.2538,
//                1.2209,
//                1.2271,
//                1.155,
//                1.4389,
//                1.1683,
//                1.4009,
//                1.1842,
//                1.2592,
//                1.2607,
//                1.3211
//        };

        double[] input = {
                1.0778,
                1.5653,
                1.0937,
                1.6055,
                1.0941,
                1.5647,
                1.1094,
                1.5590,
                1.1176,
                1.4697,
                1.1014,
                1.4690,
                1.0799,
                1.6043,
                1.0766,
                1.6035,
                1.1458,
                1.5332,
                1.0933,
                1.5295
        };

//        for (double dis : input) {
//            System.out.println(dis + " -> " + calculateMaxQualityPercentage(dis));
//        }

        double nsgaSum = 0, randomSum = 0;
        for (int i = 0; i < input.length; i++) {
            if (i % 2 == 0) {
                nsgaSum += input[i];
//                nsgaSum += calculateMaxQualityPercentage(input[i]);
            } else {
                randomSum += input[i];
//                randomSum += calculateMaxQualityPercentage(input[i]);
            }
        }

        System.out.println(nsgaSum / 10);
        System.out.println(randomSum / 10);
        System.out.println((nsgaSum - randomSum) / 10);

    }
}
