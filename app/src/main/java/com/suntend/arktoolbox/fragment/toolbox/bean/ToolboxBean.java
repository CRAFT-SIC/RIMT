package com.suntend.arktoolbox.fragment.toolbox.bean;

/**
 * 工具箱页面的列表对象类
 */
public class ToolboxBean {
    private Integer id;//主键
    private Integer categoryId;//分类主键
    private String name;//名称
    private String icon;//资源图标
    private String addressUrl;//web地址
    private Boolean follow;//是否关注
    private Integer orderId;//排序
    private ToolInfoType type;//列表对象分类

    public ToolboxBean() {

    }

    public ToolboxBean(Integer categoryId, String name, ToolInfoType type) {
        this.categoryId = categoryId;
        this.name = name;
        this.type = type;
    }

    public ToolboxBean(Integer id, Integer categoryId, String name, String icon,
                       String addressUrl, Boolean follow, Integer orderId, ToolInfoType type) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.follow = follow;
        this.icon = icon;
        this.addressUrl = addressUrl;
        this.orderId = orderId;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public ToolInfoType getType() {
        return type;
    }

    public void setType(ToolInfoType type) {
        this.type = type;
    }

    public enum ToolInfoType {
        //工具箱列表的类型标签
        TYPE_CATEGORY,
        //工具箱列表的详情标签
        TYPE_INFO,
        TYPE_BOTTOM
    }
}
