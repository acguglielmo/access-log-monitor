package com.ef.dto;

public class AccessLogDto {

    private String date;
    private String ip;
    private String request;
    private int status;
    private String userAgent;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.date).append("|")
                .append(this.ip).append("|")
                .append(this.request).append("|")
                .append(this.status).append("|")
                .append(this.userAgent);
        return stringBuilder.toString();
    }
}
