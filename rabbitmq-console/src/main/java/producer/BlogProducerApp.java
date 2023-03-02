package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class BlogProducerApp {
    // php some message
    // будут статьи по java, c++, php
    private static final String EXCHANGE_NAME = "blog_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Scanner scanner = new Scanner(System.in);
        String command = null;

        while(true) {
            command = scanner.nextLine();
            if(command.equals("quit")) break;
            int index = command.indexOf(" ");
            String message = command.substring(index + 1);
            String routingKey = command.substring(0, index);
            sendMessage(message, routingKey);
        }
    }

    private static void sendMessage(String message, String routingKey) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
