package com.jawoszek.ea;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LabsResult {

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
                "], energy=" + energy +
                '}';
    }
}
