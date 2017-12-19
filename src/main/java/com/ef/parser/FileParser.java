package com.ef.parser;

import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.util.ProgressBar;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class FileParser {

    public static final Integer MAX_BATCH_CHUNK_SIZE = 1000;
    private static volatile FileParser instance;

    private FileParser(){}

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
     * @param bufferedReader the buffered reader
     */
    public void loadFileToDatabase(final BufferedReader bufferedReader) {

        try {
            final ExecutorService executor = Executors.newFixedThreadPool(10);

            String readLine;
            List<String[]> dataList = new ArrayList<>();

            while ((readLine = bufferedReader.readLine()) != null) {
                dataList.add(parseLine(readLine));

                if (dataList.size() == MAX_BATCH_CHUNK_SIZE) {
                    executor.submit(new GatewayClient(dataList));
                    dataList = new ArrayList<>();
                }
            }
            bufferedReader.close();
            executor.submit(new GatewayClient(dataList));
            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] parseLine(final String string) {
        return string.split(Pattern.quote("|"));
    }

    class GatewayClient implements Runnable {
        private List<String[]> dataList;
        private AccessLogGatewaySqlImpl gateway;

        private GatewayClient(final List<String[]> dataList){
            this.gateway = new AccessLogGatewaySqlImpl();
            this.dataList = dataList;
        }

        @Override
        public void run(){
            gateway.insert(dataList);
            ProgressBar.getInstance().updateProgressByChunk();
        }
    }
}
