package org.jenkinsci.plugins.testfairy;

import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;

public class UI2APIParamsConverterTest {

    @Test
    public void metricsEnumSetToCSVString_should_return_proper_CSV_string_with_all_values_selected() throws Exception {

        EnumSet<Metric> metricsEnum = EnumSet.allOf(Metric.class);

        String result = UI2APIParamsConverter.metricsEnumSetToCSVString(metricsEnum);

        Assert.assertEquals("cpu,memory,network,phone-signal,logcat,gps,battery,mic",result);

    }

    @Test
    public void metricsEnumSetToCSVString_should_return_proper_CSV_string_with_one_value_selected() throws Exception {

        EnumSet<Metric> metricsEnum = EnumSet.of(Metric.MEMORY);

        String result = UI2APIParamsConverter.metricsEnumSetToCSVString(metricsEnum);

        Assert.assertEquals("memory",result);

    }

    @Test
    public void metricsEnumSetToCSVString_should_return_empty_string_when_no_values_selected() throws Exception {

        EnumSet<Metric> metricsEnum = EnumSet.noneOf(Metric.class);

        String result = UI2APIParamsConverter.metricsEnumSetToCSVString(metricsEnum);

        Assert.assertEquals(result,"");

    }

}
