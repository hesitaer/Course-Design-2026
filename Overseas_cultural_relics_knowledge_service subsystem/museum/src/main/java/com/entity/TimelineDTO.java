package com.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class TimelineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TimelineItem> items;
    private Integer totalAntiques;
    private Integer periodCount;
    private Integer museumCount;

    @Data
    public static class TimelineItem implements Serializable {
        private String dynasty;
        private String dynastyEn;
        private Integer count;
        private Integer startYear;
        private Integer endYear;
        private List<AntiqueBrief> antiques;

        public TimelineItem() {}

        public TimelineItem(String dynasty, String dynastyEn, Integer count, Integer startYear, Integer endYear) {
            this.dynasty = dynasty;
            this.dynastyEn = dynastyEn;
            this.count = count;
            this.startYear = startYear;
            this.endYear = endYear;
        }
    }

    @Data
    public static class AntiqueBrief implements Serializable {
        private String objectId;
        private String title;
        private String museum;
        private String imageUrl;

        public AntiqueBrief() {}

        public AntiqueBrief(String objectId, String title, String museum, String imageUrl) {
            this.objectId = objectId;
            this.title = title;
            this.museum = museum;
            this.imageUrl = imageUrl;
        }
    }
}
