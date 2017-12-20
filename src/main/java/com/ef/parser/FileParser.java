package com.ef.parser;

import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.util.ApplicationException;
import com.ef.util.ApplicationStatus;

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
    public void loadFileToDatabase(final File file) throws IOException, InterruptedException {

        final FileReader fileReader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);

        final ExecutorService executor = Executors.newFixedThreadPool(10);

        String readLine;
        List<String[]> dataList = new ArrayList<>();

        while ((readLine = bufferedReader.readLine()) != null) {
            dataList.add(parseLine(readLine));

            if (dataList.size() == MAX_BATCH_CHUNK_SIZE) {
                Future<?> future = executor.submit(new GatewayClient(dataList));
                ApplicationStatus.getInstance().addFuture(future);
                dataList = new ArrayList<>();
            }
        }
        fileReader.close();
        executor.submit(new GatewayClient(dataList));
        executor.shutdown();

        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    private String[] parseLine(final String string) {
        return string.split(Pattern.quote("|"));
    }

    /**
     * The type Gateway client.
     */
    class GatewayClient implements Runnable {
        private List<String[]> dataList;
        private AccessLogGatewaySqlImpl gateway;

        private GatewayClient(final List<String[]> dataList){
            this.gateway = new AccessLogGatewaySqlImpl();
            this.dataList = dataList;
        }

        @Override
        public void run(){
            try {
                gateway.insert(dataList);
                ApplicationStatus.getInstance().updateProgressByChunk();
            } catch (final SQLException e) {
                throw new ApplicationException(e);
            }
        }
    }
}
