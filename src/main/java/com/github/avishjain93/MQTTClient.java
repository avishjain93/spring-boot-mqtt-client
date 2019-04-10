package com.github.avishjain93;

import com.github.avishjain93.dataSource.MQTTDataSource;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

@SpringBootApplication
@ComponentScan(basePackageClasses = MQTTDataSource.class)
public class MQTTClient {
    @Autowired
    private MQTTDataSource dataSource;

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(dataSource.getConnect().toArray(new String[0]));
        if (dataSource.getUsername() != null && dataSource.getPassword() != null) {
            options.setUserName(dataSource.getUsername());
            options.setPassword(dataSource.getPassword().toCharArray());
        }
        factory.setConnectionOptions(options);
        try {
            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeanCreationException("mqttPahoClientFactory", "Failed to connect to MQTT Client");
        }
    }

    @Bean
    public MessageChannel mqttReceiverChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducerSupport mqttReceiver() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                dataSource.getConsumerClientId(), mqttPahoClientFactory(), dataSource.getConsumerTopic().toArray(new String[0]));
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(dataSource.getQos());
        adapter.setRecoveryInterval(10000);
        adapter.setOutputChannel(mqttReceiverChannel());
        try {
            return adapter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeanCreationException("mqttReceiver", "Unable to create MQTT receiver bean");
        }
    }

    @Bean
    public MessageChannel mqttProducerChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttProducerChannel")
    public MessageHandler mqttProducer() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(dataSource.getPublisherClientId(), mqttPahoClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(dataSource.getDefaultPublisherTopic());
        try {
            return messageHandler;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeanCreationException("mqttProducer", "Unable to create Producer Bean");
        }
    }

    @MessagingGateway(defaultRequestChannel = "mqttProducerChannel")
    public interface ProducerGateway {
        void sendToMqtt(String message);

        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String message);
    }

}
