package com.acguglielmo.accesslogmonitor.parser;

import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * The type File parser.
 */
public final class FileParser {

    /**
     * The constant MAX_BATCH_CHUNK_SIZE.
     */
    public static final Integer MAX_BATCH_CHUNK_SIZE = 1000;

    private Pattern regex = Pattern.compile(Pattern.quote("|"));

    private static volatile FileParser instance;

    private FileParser(){}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static FileParser getInstance() {
        if (instance == null) {
            synchronized (FileParser.class) {
                if (instance == null) {
                    instance = new FileParser();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the file to the database.
     *
     * @param file the file
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public void loadFileToDatabase(final File file) throws IOException, InterruptedException, SQLException {

        final FileReader fileReader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);

        final ExecutorService executor = Executors.newFixedThreadPool(10);

        String readLine;
        List<String> readLines = new ArrayList<>();

        while ((readLine = bufferedReader.readLine()) != null) {
            readLines.add(readLine);

            if (readLines.size() == MAX_BATCH_CHUNK_SIZE) {
                Future<?> future = executor.submit(new GatewayClient(readLines));
                ApplicationStatus.getInstance().addFuture(future);
                readLines = new ArrayList<>();
            }
        }
        fileReader.close();
        Future<?> future = executor.submit(new GatewayClient(readLines));
        ApplicationStatus.getInstance().addFuture(future);

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    /**
     * The type Gateway client.
     * <p>
     * Do the string splitting and polling.
     */
    class GatewayClient implements Runnable {
        private List<String> readLines;

        /**
         * Instantiates a new Gateway client.
         *
         * @param readLines the read lines
         */
        GatewayClient(final List<String> readLines){
            this.readLines = readLines;
        }

        @Override
        public void run(){
            try {
                final List<String[]> stringArrayList = new ArrayList<>();

                for (final String readLine : readLines) {
                    final String[] stringArray = regex.split(readLine);
                    stringArrayList.add(doStringPolling(stringArray));
                }

                new AccessLogGatewaySqlImpl().insert(stringArrayList);
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private String[] doStringPolling(final String[] strings) {
            for (int i =0; i < strings.length ; i++) {
                strings[i] = strings[i].intern();
            }
            return strings;
        }
    }
}
