package com.jawoszek.ea;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

public class SdlsExperiment {

    private static final String DEFAULT_TIME_LIMIT = "1000";
    private static final String DEFAULT_REPORTING_INTERVAL = "200";
    private static final String DEFAULT_BITS_LENGTH = "39";
    private static final String DEFAULT_THREADS_COUNT = "4";
    private static final String TIME_LIMIT_OPTION_NAME = "time-limit";
    private static final String REPORTING_INTERVAL_OPTION_NAME = "reporting-interval";
    private static final String BITS_LENGTH_OPTION_NAME = "sequence-length";
    private static final String THREADS_COUNT_OPTION_NAME = "threads-count";

    private final long timeLimit;
    private final long reportingInterval;
    private final int bitsLength;
    private final int threadsCount;

    public SdlsExperiment(long timeLimit, long reportingInterval, int bitsLength, int threadsCount) {
        this.timeLimit = timeLimit;
        this.reportingInterval = reportingInterval;
        this.bitsLength = bitsLength;
        this.threadsCount = threadsCount;
    }

    public static void main(String[] args) {
        CommandLine cmd = parseCommand(args);

        long timeLimit = parseLong(cmd.getOptionValue(TIME_LIMIT_OPTION_NAME, DEFAULT_TIME_LIMIT));
        long reportingInterval = parseLong(cmd.getOptionValue(REPORTING_INTERVAL_OPTION_NAME, DEFAULT_REPORTING_INTERVAL));
        int bitsLength = parseInt(cmd.getOptionValue(BITS_LENGTH_OPTION_NAME, DEFAULT_BITS_LENGTH));
        int threadsCount = parseInt(cmd.getOptionValue(THREADS_COUNT_OPTION_NAME, DEFAULT_THREADS_COUNT));

        SdlsExperiment experiment = new SdlsExperiment(timeLimit, reportingInterval, bitsLength, threadsCount);
        System.out.println(format("Best result: %s", experiment.run()));
    }

    private static CommandLine parseCommand(String[] args) {
        Options cmdOptions = new Options();

        Option timeLimitOption = new Option("t", TIME_LIMIT_OPTION_NAME, true, "time limit for experiment");
        timeLimitOption.setRequired(false);
        timeLimitOption.setType(Long.class);
        cmdOptions.addOption(timeLimitOption);

        Option reportingIntervalOption = new Option("i", REPORTING_INTERVAL_OPTION_NAME, true, "reporting interval for experiment");
        reportingIntervalOption.setRequired(false);
        reportingIntervalOption.setType(Long.class);
        cmdOptions.addOption(reportingIntervalOption);

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

    public LabsResult run() {
        LabsReporter reporter = new BasicLabsReporter();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsCount; i++) {
            Sdls sdls = new Sdls(bitsLength, reporter);
            Thread thread = new Thread(sdls);

            threads.add(thread);
        }

        threads.forEach(Thread::start);

        long startTime = currentTimeMillis();

        try {
            while (currentTimeMillis() - startTime < timeLimit) {
                long timeLeft = Math.max(0, timeLimit + startTime - currentTimeMillis());
                sleep(Math.min(reportingInterval, timeLeft));
                System.out.println(format("Best result after %d ms running: %s", currentTimeMillis() - startTime, reporter.getBest()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threads.forEach(Thread::interrupt);

        return reporter.getBest();
    }
}
