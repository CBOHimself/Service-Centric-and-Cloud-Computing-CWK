
package courseworkmq;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.net.HttpURLConnection;
import java.net.URL;


import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Charles Bryan Addo-Oware
 */
public class OffersPublisher {

    // Notice how the exchange types have been defined as enums and how they get converted to strings on line 45
    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};

    private final static String EXCHANGE_NAME = "TRAVEL_OFFERS";
    
    private final static String TOPIC_KEY_NAME = "TravelOffers";
    
    public static void main(String[] argv) throws Exception {
        
        // Log in to the RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
        try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
            
            Scanner msgScan = new Scanner(System.in);
            System.out.println("Enter City for Trip Proposal: ");
            String message = msgScan.nextLine();
            
            //Random Generator for User ID API
            String userIDlink = "https://www.random.org/integers/?num=1&min=1000&max=9999&col=1&base=10&format=plain&rnd=new";
            //Random Generator for Message ID API
            String messageIDlink = "https://www.random.org/integers/?num=1&min=10000&max=99999&col=1&base=10&format=plain&rnd=new";
            // Weather API Link
            String apikey = "281a5596875cae9561b2195ba39b422a";
            String weatherlink = "http://api.openweathermap.org/data/2.5/weather?q="+ message +"&APPID=" + apikey;
           
            URL weatherURL = new URL(weatherlink); 
            URL url = new URL(userIDlink);
            URL messageUrl = new URL(messageIDlink);
            
            HttpURLConnection usercon = (HttpURLConnection) url.openConnection();
            usercon.setRequestMethod("GET");
            usercon.connect();
            
                        
            HttpURLConnection msgcon = (HttpURLConnection) messageUrl.openConnection();
            msgcon.setRequestMethod("GET");
            msgcon.connect();
            

            HttpURLConnection weatherCon = (HttpURLConnection) weatherURL.openConnection();
            weatherCon.setRequestMethod("GET");
            weatherCon.connect();
            //Check if connect is made
            int responseCode = usercon.getResponseCode();
            int msgresponseCode = msgcon.getResponseCode();
            int weathresponseCode = weatherCon.getResponseCode();
            // 200 OK
            if (responseCode != 200 && msgresponseCode != 200 && weathresponseCode !=200) {
                throw new RuntimeException("User ID HttpResponseCode: " + responseCode + "\n Message ID HttpResponseCode: " + msgresponseCode + "\n Weather ID HttpResponseCode: " + weathresponseCode);
            } else {
                
                
                StringBuilder msgIDcode = new StringBuilder();
                StringBuilder userIDcode = new StringBuilder();
                StringBuilder weatherCode = new StringBuilder();
                
                Scanner userscanner = new Scanner(url.openStream());
                Scanner msgscanner = new Scanner(messageUrl.openStream());
                Scanner weatherscanner = new Scanner(weatherURL.openStream());
                

                while (userscanner.hasNext() && msgscanner.hasNext() && weatherscanner.hasNext()) {
                    weatherCode.append(weatherscanner.nextLine());
                    msgIDcode.append(userscanner.nextLine());
                    userIDcode.append(msgscanner.nextLine());
                }
                //Close the scanner
                weatherscanner.close();
                userscanner.close();
                msgscanner.close();

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase()); // 2nd parameter: fanout, direct, topic, headers

            String msgFormat = TOPIC_KEY_NAME + "; Weather Forecast for " + message + "\n" +weatherCode +" "+ "\n User ID: " + userIDcode + "\n Message ID: " + msgIDcode ;
            

            channel.basicPublish(EXCHANGE_NAME, 
                    TOPIC_KEY_NAME, // This parameter is used for the routing key, which is usually used for direct or topic queues.
                    new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(1)
                        .userId("guest")
                        .build(),
                    msgFormat.getBytes(StandardCharsets.UTF_8));
            System.out.println("Message Sent: " + msgFormat);
            
                    }
    }
    }
}
