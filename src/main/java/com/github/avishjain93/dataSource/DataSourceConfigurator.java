package com.github.avishjain93.dataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

@Configuration
public class DataSourceConfigurator {
    @Value("${mqtt.connect:tcp://localhost:1883}")
    private List<String> connect;

    @Value("${mqtt.username:test}")
    private String username;

    @Value("${mqtt.password:test}")
    private String password;

    @Value("${mqtt.consumerClientId:beta_tester}")
    private String consumerClientId;

    @Value("${mqtt.consumerTopic:'#'}")
    private List<String> consumerTopic;

    @Value("${mqtt.qos:1}")
    private int qos;

    @Value("${mqtt.publisherClientId:beta_producer}")
    private String publisherClientId;

    @Value("${mqtt.defaultPublisherTopic:test}")
    private String defaultPublisherTopic;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MQTTDataSource mqttSource() {
        MQTTDataSource mqttDataSource = new MQTTDataSource();
        mqttDataSource.setConnect(connect);
        mqttDataSource.setUsername(username);
        mqttDataSource.setPassword(password);
        mqttDataSource.setConsumerClientId(consumerClientId);
        mqttDataSource.setConsumerTopic(consumerTopic);
        mqttDataSource.setQos(qos);
        mqttDataSource.setPublisherClientId(publisherClientId);
        mqttDataSource.setDefaultPublisherTopic(defaultPublisherTopic);
        return mqttDataSource;
    }
}
