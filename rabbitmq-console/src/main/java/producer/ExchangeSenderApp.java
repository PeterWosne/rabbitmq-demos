package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

//делаем широковещательную рассылку чтоб сообщение доходило до любого количества consumer'ов
//на стороне сервера очереди не создаем, создаем их у консьюмеров
public class ExchangeSenderApp {

    private static final String EXCHANGER_NAME = "broadcast_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);

            String message = "Message we want to broadcast!";
            channel.basicPublish(EXCHANGER_NAME, "key", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        };
    }
}
