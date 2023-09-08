package com.suntend.arktoolbox.database.bean;

/**
 * 工具箱分类表
 */
public class ToolCategoryBean {
    /**
     * 分类主键id
     */
    private Integer categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 排序
     */
    private Integer orderId;

    public ToolCategoryBean() {

    }

    public ToolCategoryBean(Integer categoryId, String categoryName, Integer orderId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.orderId = orderId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
