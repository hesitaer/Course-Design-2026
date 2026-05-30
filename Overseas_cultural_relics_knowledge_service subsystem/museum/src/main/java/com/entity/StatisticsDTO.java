package com.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalAntiques;
    private Integer categoryCount;
    private Integer museumCount;
    private Integer dynastyCount;
    private List<NameValueItem> categoryDistribution;
    private List<NameValueItem> dynastyDistribution;
    private List<NameValueItem> museumRanking;
    private List<NameValueItem> materialDistribution;
    private List<NameValueItem> regionDistribution;
    private List<NameValueItem> yearlyTrend;

    @Data
    public static class NameValueItem implements Serializable {
        private String name;
        private Integer value;

        public NameValueItem() {}

        public NameValueItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
}
