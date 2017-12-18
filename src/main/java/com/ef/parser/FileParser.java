package com.ef.parser;

import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.util.PropertiesHolder;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public final class FileParser {

    private static FileParser instance;

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
     * Parses the file.
     *
     * @param path the path
     */
    public void parseFile(final Path path) {
        try {

            final File file = new File(path.toUri());
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            final ExecutorService executor = Executors.newFixedThreadPool(10);

            String readLine;
            List<String[]> dataList = new ArrayList<>();

            while ((readLine = reader.readLine()) != null) {
                dataList.add(parseLine(readLine));

                if(dataList.size() > 999) {
                    executor.submit(new GatewayClient(dataList));
                    dataList = new ArrayList<>();
                }
            }
            reader.close();
            executor.submit(new GatewayClient(dataList));
            executor.shutdown();

            while (!executor.isTerminated()) {}

        } catch (final NoSuchFileException e) {
            System.out.println("File " + path.getFileName() + " not found");
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] parseLine(final String string) {
        return string.split(Pattern.quote("|"));
    }

    class GatewayClient implements Runnable {
        private List<String[]> dataList;
        private AccessLogGateway gateway;

        private GatewayClient(final List<String[]> dataList){
            this.gateway = new AccessLogGatewaySqlImpl();
            this.dataList = dataList;
        }

        @Override
        public void run(){
            gateway.insert(dataList);
        }
    }
}
