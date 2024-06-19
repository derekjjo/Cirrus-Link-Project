package org.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceEventGenerator {
    private static final Logger errorLOGGER = Logger.getLogger(ResourceEventGenerator.class.getName());
    private final List<ResourceEventHandler> handlers = new ArrayList<>();
    private MqttClient client;

    public void registerHandler(ResourceEventHandler handler) {
        handlers.add(handler);
    }

    public void start() {
        // info to connect to mosquitto MQTT broker where the random int is published every one second
        String broker = "tcp://test.mosquitto.org:1883";
        String clientId = "JavaSample";
        String topic = "cirrus/link/projectsub";

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    errorLOGGER.log(Level.WARNING, "Connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    try {
                        int value = Integer.parseInt(payload);
                        Map<String, Object> resource = new HashMap<>();
                        resource.put("name", "TestResource");
                        resource.put("timestamp", new Date());
                        resource.put("datatype", "int");
                        resource.put("value", value);
                        resourceEventGenerated(resource);
                    } catch (NumberFormatException e) {
                        errorLOGGER.log(Level.WARNING, "Invalid integer received: {0}", payload);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used here
                }
            });

            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");
            client.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            errorLOGGER.log(Level.SEVERE, "MQTT Exception occurred", me);
        }
    }

    private void resourceEventGenerated(Map<String, Object> resource) {
        handlers.forEach(handler -> handler.handleEvent(resource));
    }

    public static void main(String[] args) {
        ErrorLoggingConfiguration.configure();
        ResourceEventGenerator generator = new ResourceEventGenerator();
        ResourceEventHandler handler = new ResourceEventHandler();
        generator.registerHandler(handler);
        generator.start();
    }
}
