package com.suntend.arktoolbox.database.bean;

public class ToolInfoBean {
    /**
     * 主键id
     */
    private Integer infoId;
    /**
     * 名称
     */
    private String infoName;
    /**
     * 分类id
     */
    private Integer categoryId;
    /**
     * 图标资源文件
     */
    private String icon;
    /**
     * web地址
     */
    private String addressUrl;
    /**
     * 是否收藏
     */
    private Boolean follow;
    /**
     * 排序
     */
    private Integer orderId;

    public ToolInfoBean() {

    }

    public ToolInfoBean(Integer infoId, String infoName, Integer categoryId, String icon,
                        String addressUrl, Boolean follow, Integer orderId) {
        this.infoId = infoId;
        this.infoName = infoName;
        this.categoryId = categoryId;
        this.icon = icon;
        this.addressUrl = addressUrl;
        this.follow = follow;
        this.orderId = orderId;
    }

    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
