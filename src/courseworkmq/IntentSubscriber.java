/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package courseworkmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 *
 * @author charl
 */
public class IntentSubscriber {
        
    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};
    
    
    private final static String EXCHANGE_NAME = "TRAVEL_INTENTS";
    private final static String QUEUE_NAME = "Traveller";
    

    private final static String TOPIC_KEY_NAME = ""; 
    
    public static void main(String[] argv) throws Exception {
        
        // Connect to the RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the exchange you want to connect your queue to
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.FANOUT.toString().toLowerCase()); // 2nd parameter: fanout, direct, topic, headers

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // Link the queue to the exchange

        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, TOPIC_KEY_NAME); // The last parameter is the routing key usually used for direct or topic queues
        
        System.out.println(" [*] Waiting for " + TOPIC_KEY_NAME +  " messages. To exit press CTRL+C");

        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageFormat = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + messageFormat + "'");
        };
        
       
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
