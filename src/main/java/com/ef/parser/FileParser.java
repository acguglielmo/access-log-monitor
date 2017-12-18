package com.ef.parser;

import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileParser {

    /**
     * Parses the file.
     *
     * @param path the path
     */
    public void parseFile(final Path path) {

        final AccessLogGateway gateway = new AccessLogGatewaySqlImpl();

        try {
            gateway.open();
            //Files.lines(path).map(this::parseLine)
               //     .forEach(gateway::insert);

            File file = new File(path.toUri());
            BufferedReader b = new BufferedReader(new FileReader(file));
            String readLine = "";

            List<String[]> dataList = new ArrayList<>();

            while ((readLine = b.readLine()) != null) {
                dataList.add(parseLine(readLine));

                if(dataList.size() > 999) {
                    gateway.insert(dataList);
                    dataList = new ArrayList<>();
                }
            }
            gateway.insert(dataList);

            gateway.close();
        } catch (final NoSuchFileException e) {
            System.out.println("File " + path.getFileName() + " not found");
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] parseLine(final String string) {
        return string.split(Pattern.quote("|"));
    }
}
