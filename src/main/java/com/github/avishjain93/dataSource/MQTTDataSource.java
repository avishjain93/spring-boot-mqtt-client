package com.github.avishjain93.dataSource;

import lombok.Data;

import java.util.List;

@Data
public class MQTTDataSource {
    private List<String> connect;
    private String username;
    private String password;
    private String consumerClientId;
    private List<String> consumerTopic;
    private int qos;
    private String publisherClientId;
    private String defaultPublisherTopic;
}
