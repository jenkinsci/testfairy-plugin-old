package org.jenkinsci.plugins.testfairy;

import java.util.EnumSet;
import java.util.Iterator;

public final class UI2APIParamsConverter {

    private UI2APIParamsConverter() {
        //utility class
    }

    public static EnumSet<Metric> metricsCSVStringToEnumSet(String metricsValue){

        String[] metricsArray = metricsValue.substring(0, metricsValue.length()).split(",");

        EnumSet<Metric> metricsEnum = EnumSet.noneOf(Metric.class);

        for(String s : metricsArray) {

            Metric mtr = Metric.get(s.trim());

            if(mtr!=null) {
                metricsEnum.add(mtr);
            }
        }

        return metricsEnum;
    }

    public static String metricsEnumSetToCSVString(EnumSet<Metric> metrics) {

        StringBuffer metricsValue = new StringBuffer();

        if(!metrics.isEmpty())
        {
            Iterator<Metric> iterator = metrics.iterator();
            while(iterator.hasNext()){
                if(metricsValue.length()>0)
                {
                    metricsValue.append(",");
                }
                metricsValue.append(iterator.next().toString());
            }
        }

        return metricsValue.toString();
    }


    public static Boolean iconWatermarkStringToBoolean(String iconWatermark){
        return iconWatermark.equals("on");
    }

    public static String iconWatermarkBooleanToString(Boolean iconWatermark){
        String iconWatermarkValue = "off";
        if(iconWatermark) {
            iconWatermarkValue = "on";
        }
        return iconWatermarkValue;
    }


}
