package com.jawoszek.ea;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LabsResult {

    public final static LabsResult EMPTY_RESULT = new LabsResult(new Boolean[0], Integer.MAX_VALUE);

    private final Boolean[] bits;
    private final int energy;

    public LabsResult(Boolean[] bits, int energy) {
        this.bits = bits.clone();
        this.energy = energy;
    }

    public Boolean[] getBits() {
        return bits.clone();
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public String toString() {
        return "LabsResult{" +
                "bits=[" + Arrays.stream(bits).map(bit -> bit ? "1" : "-1").collect(Collectors.joining(",")) +
                "], length=" + bits.length + " energy=" + energy +
                '}';
    }
}
