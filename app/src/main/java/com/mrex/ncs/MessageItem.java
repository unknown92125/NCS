package com.mrex.ncs;

public class MessageItem implements Comparable<MessageItem> {
    private String name, message, time, type;
    private Long timeMilli;

    public MessageItem() {
    }

    public MessageItem(String name, String message, String time, String type, Long timeMilli) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.type = type;
        this.timeMilli = timeMilli;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(Long timeMilli) {
        this.timeMilli = timeMilli;
    }

    @Override
    public int compareTo(MessageItem o) {

        long timeMilli0 = this.getTimeMilli();
        long timeMilli1 = o.getTimeMilli();

        if (timeMilli0 == timeMilli1) return 0;
        else if (timeMilli0 > timeMilli1) return 1;
        else return -1;
        
    }
}
