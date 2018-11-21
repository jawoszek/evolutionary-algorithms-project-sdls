package com.jawoszek.ea;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Comparator.comparingInt;

public class SdlsExperiment {

    private static final String DEFAULT_TIME_LIMIT = "1000";
    private static final String DEFAULT_BITS_LENGTH = "39";
    private static final String DEFAULT_THREADS_COUNT = "4";
    private static final String TIME_LIMIT_OPTION_NAME = "time-limit";
    private static final String BITS_LENGTH_OPTION_NAME = "sequence-length";
    private static final String THREADS_COUNT_OPTION_NAME = "threads-count";

    private final long timeLimit;
    private final int bitsLength;
    private final int threadsCount;

    public SdlsExperiment(long timeLimit, int bitsLength, int threadsCount) {
        this.timeLimit = timeLimit;
        this.bitsLength = bitsLength;
        this.threadsCount = threadsCount;
    }

    public static void main(String[] args) {
        CommandLine cmd = parseCommand(args);

        long timeLimit = parseLong(cmd.getOptionValue(TIME_LIMIT_OPTION_NAME, DEFAULT_TIME_LIMIT));
        int bitsLength = parseInt(cmd.getOptionValue(BITS_LENGTH_OPTION_NAME, DEFAULT_BITS_LENGTH));
        int threadsCount = parseInt(cmd.getOptionValue(THREADS_COUNT_OPTION_NAME, DEFAULT_THREADS_COUNT));

        SdlsExperiment experiment = new SdlsExperiment(timeLimit, bitsLength, threadsCount);
        System.out.println(experiment.run());
    }

    private static CommandLine parseCommand(String[] args) {
        Options cmdOptions = new Options();

        Option timeLimitOption = new Option("t", TIME_LIMIT_OPTION_NAME, true, "time limit for experiment");
        timeLimitOption.setRequired(false);
        timeLimitOption.setType(Long.class);
        cmdOptions.addOption(timeLimitOption);

        Option bitsLengthOption = new Option("l", BITS_LENGTH_OPTION_NAME, true, "binary sequence length");
        bitsLengthOption.setRequired(false);
        bitsLengthOption.setType(Integer.class);
        cmdOptions.addOption(bitsLengthOption);

        Option threadsCountOption = new Option("c", THREADS_COUNT_OPTION_NAME, true, "count of threads to execute experiment on");
        threadsCountOption.setRequired(false);
        bitsLengthOption.setType(Integer.class);
        cmdOptions.addOption(threadsCountOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            return parser.parse(cmdOptions, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", cmdOptions);
            System.exit(1);
        }
        return null;
    }

    private static LabsResult unpackTaskResult(FutureTask<LabsResult> task) {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return LabsResult.EMPTY_RESULT;
    }

    public LabsResult run() {
        List<FutureTask<LabsResult>> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsCount; i++) {
            Sdls sdls = new Sdls(bitsLength);
            FutureTask<LabsResult> task = new FutureTask<>(sdls);
            Thread thread = new Thread(task);

            tasks.add(task);
            threads.add(thread);
        }

        threads.forEach(Thread::start);

        try {
            Thread.sleep(timeLimit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threads.forEach(Thread::interrupt);

        return tasks
                .stream()
                .map(SdlsExperiment::unpackTaskResult)
                .min(comparingInt(LabsResult::getEnergy))
                .orElse(LabsResult.EMPTY_RESULT);
    }
}
