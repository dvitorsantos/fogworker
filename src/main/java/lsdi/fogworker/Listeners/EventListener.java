package lsdi.fogworker.Listeners;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lsdi.fogworker.DataTransferObjects.Event;
import lsdi.fogworker.DataTransferObjects.RuleRequestResponse;
import lsdi.fogworker.Services.MqttService;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class EventListener implements UpdateListener {
    RestTemplate restTemplate = new RestTemplate();
    private final RuleRequestResponse rule;


    private final String consumerUrl = System.getenv("CONSUMER_URL");

    MqttService mqttService = MqttService.getInstance();

    public EventListener(RuleRequestResponse rule) {
        this.rule = rule;
    }

    @Override
    public void update(EventBean[] newData, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> event = mapper.readValue(mapper.writeValueAsString(newData[0].getUnderlying()), Map.class);
            
            System.out.println("LOG: Received event: " + event);
            switch(rule.getTarget()) {
                case "FOG" -> new Thread(() -> {
                    try {
                        mqttService.publish("cdpo/FOG/event/" + rule.getOutputEventType(), mapper.writeValueAsBytes(event));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                case "CLOUD" -> new Thread(() -> {
                    try {
                        mqttService.publish("cdpo/CLOUD/event/" + rule.getOutputEventType(), mapper.writeValueAsBytes(event));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                case "WEBHOOK" -> new Thread(() -> {
                    Event eventDto = new Event();
                    eventDto.setWebhookUrl(rule.getWebhookUrl());
                    eventDto.setEvent(event);
                    restTemplate.postForObject(consumerUrl + "/event", eventDto, Map.class);
                }).start();
            }


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}