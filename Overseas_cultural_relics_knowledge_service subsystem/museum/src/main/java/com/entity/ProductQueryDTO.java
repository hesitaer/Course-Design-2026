package com.entity;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private String keyword;
    private String medium;
    private String objectType;
    private String timePeriod;
    private String geography;
    private String museum;
    private String cat1;
    private String cat2;
    private String cat3;
    private String makersName;
    private String objectName;
    
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sortField = "id";
    private String sortOrder = "asc";
}
