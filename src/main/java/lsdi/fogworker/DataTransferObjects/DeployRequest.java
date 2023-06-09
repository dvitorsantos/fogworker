package lsdi.fogworker.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class DeployRequest {
    @Nullable
    @JsonProperty("webhook_url")
    public String webhookUrl;
    @JsonProperty("rules")
    public List<RuleRequestResponse> rules;
}
