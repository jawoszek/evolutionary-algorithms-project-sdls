package com.jawoszek.ea;

public class BasicLabsReporter implements LabsReporter {

    private LabsResult bestResult = LabsResult.EMPTY_RESULT;

    @Override
    public synchronized void report(LabsResult result) {
        if (result.getEnergy() < bestResult.getEnergy()) {
            bestResult = result;
        }
    }

    @Override
    public synchronized LabsResult getBest() {
        return bestResult;
    }
}
