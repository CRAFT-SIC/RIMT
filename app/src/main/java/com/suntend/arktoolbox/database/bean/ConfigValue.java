package com.suntend.arktoolbox.database.bean;

public class ConfigValue {
    private Integer configId;
    private String configKey;
    private String configValue;

    public ConfigValue() {

    }

    public ConfigValue(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
