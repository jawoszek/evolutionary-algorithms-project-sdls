package com.jawoszek.ea;

import java.util.Random;

public class Sdls {

    public static void main(String[] args) {
        int n = 30;
        Boolean[] testingTable = randomBits(n);
        System.out.println(sdls(testingTable));
    }

    private static Boolean[] randomBits(int n) {
        Boolean[] s = new Boolean[n];
        for (int i = 0; i < n; i++) {
            s[i] = new Random().nextBoolean();
        }
        return s;
    }

    private static Boolean[][] T(Boolean[] bits) {
        Boolean[][] table = new Boolean[bits.length - 1][bits.length - 1];
        for (int i = 0; i < bits.length - 1; i++) {
            table[i] = new Boolean[bits.length - 1];
            for (int j = 0; j < bits.length - 1 - i; j++) {
                table[i][j] = bits[j] == bits[i + j + 1];
            }
        }

        return table;
    }

    private static Integer[] C(Boolean[][] t) {
        Integer[] row = new Integer[t.length];
        for (int i = 0; i < t.length; i++) {
            int sum = 0;
            for (int j = 0; j < t.length - i; j++) {
                sum += t[i][j] ? 1 : -1;
            }
            row[i] = sum;
        }
        return row;
    }

    private static Boolean[][] N(Boolean[] bits) {
        Boolean[][] n = new Boolean[bits.length][bits.length];
        for (int i = 0; i < bits.length; i++) {
            n[i] = flip(bits, i);
        }
        return n;
    }

    private static Boolean[] flip(Boolean[] bits, int i) {
        Boolean[] clone = bits.clone();
        clone[i] = !bits[i];
        return clone;
    }

    private static Integer valueFlip(Boolean[] bits, int i, Boolean[][] t, Integer[] c) {
        int f = 0;

        for (int p = 0; p < bits.length - 1; p++) {
            int v = c[p];
            if (p <= bits.length - 2 - i) {
                v -= 2 * (t[p][i] ? 1 : -1);
            }
            if (p < i) {
                v -= 2 * (t[p][i - p - 1] ? 1 : -1);
            }
            f += v * v;
        }
        return f;
    }

    private static LabsResult sdls(Boolean[] bits) {
        Boolean[][] t = T(bits);
        Integer[] c = C(t);

        Boolean[] sStar = bits.clone();
        Boolean[] sPlus = null;
        int fStar = 0;
        for (int i = 0; i < c.length; i++) {
            fStar += c[i] * c[i];
        }
        boolean improved = true;
        while (improved) {
            int fPlus = Integer.MAX_VALUE;
            for (int i = 0; i < bits.length; i++) {
                Boolean[] sPrime = flip(sStar, i);
                Integer fPrime = valueFlip(sStar, i, t, c);
                if (fPrime < fPlus) {
                    fPlus = fPrime;
                    sPlus = sPrime;
                }
            }
            if (fPlus < fStar) {
                sStar = sPlus;
                fStar = fPlus;
                improved = true;
                t = T(sStar);
                c = C(t);
            } else {
                improved = false;
            }
        }
        return new LabsResult(sStar, fStar);
    }
}
