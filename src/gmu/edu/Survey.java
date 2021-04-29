/**
 *  Team: Madeline, Vandana, Dhruv, Priya
Jersey servlet. Contains methods for get and post request
 
 */
package gmu.edu;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/survey")
public class Survey {

	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addSurveyData(String data) {
		System.out.println("body "+data);

		Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka-app-kafka-bootstrap:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kafkaProducer = new KafkaProducer(properties);
        try{
           
                
                kafkaProducer.send(new ProducerRecord("survey-data-topic", data));
                
           
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            kafkaProducer.close();
        }
		
		return "success";

	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSurvey() {
		
		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		JsonArray jsonList = new JsonArray();
		Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka-app-kafka-bootstrap:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("survey-data-topic");
        kafkaConsumer.subscribe(topics);
        
        System.out.println("Get request");
        try{
        	
        	kafkaConsumer.poll(0);
        	kafkaConsumer.seekToBeginning(kafkaConsumer.assignment());
            ConsumerRecords<String,String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String,String> record: records){
            	
            	jsonList.add(record.value());
            	
            	System.out.println("Consumed message: " + record.value());
            }
           
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            kafkaConsumer.close();
        }
    
	
		json.add("result", jsonList);
		return json.toString();
	}
}
