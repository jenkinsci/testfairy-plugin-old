package org.jenkinsci.plugins.testfairy;

import java.util.HashMap;
import java.util.Map;

public enum Metric {

    CPU("cpu"), MEMORY("memory"), NETWORK("network"), PHONE_SIGNAL("phone-signal"),
    LOGCAT("logcat"), GPS("gps"), BATTERY("battery"), MIC("mic");

    private String value;

    private static final Map<String,Metric> LOOKUP = new HashMap<String,Metric>();
    static {
        for (Metric m : Metric.values()){
            LOOKUP.put(m.getValue(), m);
        }
    }

    private Metric(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static Metric get(String val){
        return LOOKUP.get(val);
    }

    public String toString() {
        return value;
    }
}