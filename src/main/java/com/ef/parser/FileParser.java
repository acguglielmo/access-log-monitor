package com.ef.parser;

import com.ef.dto.AccessLogDto;
import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
            Files.lines(path).map(this::parseLine)
                    .forEach(gateway::insert);
            gateway.close();
        } catch (final NoSuchFileException e) {
            System.out.println("File " + path.getFileName() + " not found");
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Parses a line to a AccessLogDto.
     *
     * @param string the string
     * @return a dto containing the information
     */
    private AccessLogDto parseLine(final String string) {
        final String[] data = string.split(Pattern.quote("|"));

        final AccessLogDto accessLogDto = new AccessLogDto();
        accessLogDto.setDate(data[0]);
        accessLogDto.setIp(data[1]);
        accessLogDto.setRequest(data[2]);
        accessLogDto.setStatus(Integer.parseInt(data[3]));
        accessLogDto.setUserAgent(data[4]);
        return accessLogDto;
    }
}
