package lsdi.fogworker.DataTransferObjects;

import lombok.Data;

import java.util.Map;

@Data
public class Event {
    private String webhookUrl;
    private Map<String, Object> event;
}
