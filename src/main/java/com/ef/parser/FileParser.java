package com.ef.parser;

import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class FileParser {

    /**
     * Parses the file.
     *
     * @param path the path
     */
    public void parseFile(final Path path) {

        try {

            final File file = new File(path.toUri());
            final BufferedReader b = new BufferedReader(new FileReader(file));
            final ExecutorService executor = Executors.newFixedThreadPool(10);

            String readLine = "";
            List<String[]> dataList = new ArrayList<>();

            while ((readLine = b.readLine()) != null) {
                dataList.add(parseLine(readLine));

                if(dataList.size() > 999) {
                    executor.submit(new Inserter(dataList));
                    dataList = new ArrayList<>();
                }
            }
            executor.submit(new Inserter(dataList));
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

    class Inserter implements Runnable {
        private List<String[]> dataList;
        private AccessLogGateway gateway;

        public Inserter(final List<String[]> dataList){
            this.gateway = new AccessLogGatewaySqlImpl();
            this.dataList = dataList;
        }

        @Override
        public void run(){
            gateway.insert(dataList);
        }
    }
}
