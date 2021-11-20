package org.thingsboard.rule.engine.action;

import org.thingsboard.rule.engine.api.NodeConfiguration;

public class DowntimeEntryNodeConfiguration implements NodeConfiguration<DowntimeEntryNodeConfiguration> {

    private String inputKey;
    private Integer stateCode;

    @Override
    public DowntimeEntryNodeConfiguration defaultConfiguration() {
        DowntimeEntryNodeConfiguration configuration = new DowntimeEntryNodeConfiguration();
        configuration.setInputKey("state");
        configuration.setStateCode(1001);
        return configuration;
    }

    private void setStateCode(Integer code) {
        this.stateCode = code;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    private void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public String getInputKey() {
        return inputKey;
    }
}
