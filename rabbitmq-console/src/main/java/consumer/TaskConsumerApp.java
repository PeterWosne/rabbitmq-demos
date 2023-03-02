package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TaskConsumerApp {

    private static final String TASK_EXCHANGER = "task_exchanger";
    private static final String TASK_QUEUE = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE, true, false, false, null);
        channel.exchangeDeclare(TASK_EXCHANGER, BuiltinExchangeType.FANOUT);
        channel.queueBind(TASK_QUEUE, TASK_EXCHANGER, "");
        System.out.println(" [x] Waiting for messages");

        //настраиваем префетч
        channel.basicQos(3);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received message '" + message + "'");

            //симулируем ошибку
//            if(1 < 10) {
//                throw new RuntimeException("Oops");
//            }

            doWork(message);

            System.out.println(" [x] Done");

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        channel.basicConsume(TASK_QUEUE, false, deliverCallback, consumerTag -> { }); // флаг autoacknowledge -> им надо управлять самим
    }

    private static void doWork(String task) {
        for(char ch : task.toCharArray()) {
            if(ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
