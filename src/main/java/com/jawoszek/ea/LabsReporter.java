package com.jawoszek.ea;

public interface LabsReporter {

    void report(LabsResult result);

    LabsResult getBest();
}
