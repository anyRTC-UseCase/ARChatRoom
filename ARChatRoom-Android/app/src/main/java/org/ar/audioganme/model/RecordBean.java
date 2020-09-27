package org.ar.audioganme.model;

public class RecordBean {

    private String name;
    private String time;


    public RecordBean(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String item) {
        this.time = item;
    }

}
