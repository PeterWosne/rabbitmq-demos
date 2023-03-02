package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SimpleSenderApp {

    private static final String QUEUE_NAME = "hello";

    private static final String EXCHANGER_NAME = "hello_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        //создаем connectionFactory и задаем хост(если порт не указан то будет по умолчанию)
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT); //проверяем существует ли такой exchanger, если нет то создаем его
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGER_NAME, "java"); // настраиваем мост -> queue, exchanger, routingKey

            String message = "Hello, world!";
            channel.basicPublish(EXCHANGER_NAME, "java", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
