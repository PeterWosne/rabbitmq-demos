package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class BlogConsumerApp {

    private static final String EXCHANGE_NAME = "blog_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        //set_topic php  add_topic c++  delete_topic php
        Scanner scanner = new Scanner(System.in);

        String routingKey = null;
        String command = scanner.nextLine();
        String[] arr = command.split(" ");
        if(arr[0].equals("set_topic")) routingKey = arr[1];

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queue = channel.queueDeclare().getQueue();

        //самое важное
        channel.queueBind(queue, EXCHANGE_NAME, routingKey);

        System.out.println(" [x] Waiting for messages");


        Runnable task = new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    String newCommand = sc.nextLine();

                    if(newCommand.equals("quit")) {
                        System.exit(0);
                        break;
                    }

                    String prefix = newCommand.split(" ")[0];
                    String newRoutingKey = newCommand.split(" ")[1];

                    if(prefix.equals("delete_topic")) {
                        try {
                            channel.queueUnbind(queue, EXCHANGE_NAME, newRoutingKey);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(prefix.equals("add_topic")) {
                        try {
                            channel.queueBind(queue, EXCHANGE_NAME, newRoutingKey);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [X] '" + message + "'");
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> { });
    }
}
