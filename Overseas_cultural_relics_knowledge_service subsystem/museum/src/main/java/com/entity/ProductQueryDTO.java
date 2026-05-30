package com.entity;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private String keyword;           // 关键词搜索
    private String title;             // 文物名称
    private String artist;            // 作者
    private String dynasty;           // 朝代
    private String period;            // 时期
    private String type;              // 类型
    private String material;          // 材质
    private String culture;           // 文化
    private String museum;            // 博物馆名称
    private Integer museumId;         // 博物馆ID：1史密森尼 2哈佛 3波士顿MFA
    
    private Integer pageNum = 1;      // 页码
    private Integer pageSize = 10;    // 每页大小
    private String sortField = "title"; // 排序字段
    private String sortOrder = "asc"; // 排序方向

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
