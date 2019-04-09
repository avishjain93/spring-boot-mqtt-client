package com.github.avishjain93;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttProducerChannel")
public interface ProducerGateway {
    void sendToMqtt(String message);

    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String message);
}
