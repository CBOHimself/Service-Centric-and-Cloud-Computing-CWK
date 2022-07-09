/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package courseworkmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author Charles Bryan
 */
public class IntentPublisher {
    
    
    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};

    private final static String EXCHANGE_NAME = "TRAVEL_INTENTSHol";


    private final static String TOPIC_KEY_NAME = ""; // For topic the format is keyword1.keyword2.keyword3. and so on.
    
    public static void main(String[] argv) throws Exception {
        
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); 
        factory.setUsername("guest");
        factory.setPassword("guest"); 
        
        try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
 
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.FANOUT.toString().toLowerCase()); // 2nd parameter: fanout, direct, topic, headers
            Scanner msgScan = new Scanner(System.in);
            System.out.println("Enter Intent for Trip: ");
            String message = msgScan.nextLine();
          
            channel.basicPublish(EXCHANGE_NAME, 
                    TOPIC_KEY_NAME, 
                    new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(1)
                        .userId("guest")
                      
                        .build(),
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + TOPIC_KEY_NAME + ":" + message + "'");
        }
    }
    }
