package lsdi.fogworker.Services;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.EPUndeployException;
import lsdi.fogworker.DataTransferObjects.DeployRequest;
import lsdi.fogworker.DataTransferObjects.DeployResponse;
import lsdi.fogworker.DataTransferObjects.RuleRequestResponse;
import lsdi.fogworker.Listeners.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DeployService {
    String hostUuid = System.getenv("HOST_UUID");
    EsperService esperService = EsperService.getInstance();

    public void deploy(DeployRequest deployRequest) throws EPCompileException, EPDeployException {
        EPCompiled epCompiled = esperService.compile(EsperService.buildEPL(deployRequest.getRules()));
        EPDeployment epDeployment = esperService.deploy(epCompiled);

        for (RuleRequestResponse rule : deployRequest.getRules()) {
            new Thread(() -> {
                EPStatement epStatement = esperService.getStatement(epDeployment.getDeploymentId(), rule.getName());
                epStatement.addListener(new EventListener(rule));
                System.out.println("INFO: Rule " + rule.getName() + " (" + rule.getDefinition() + ") deployed.");
            }).start();
        }
    }

    public DeployResponse undeploy(String deploymentId) throws EPUndeployException {
        esperService.undeploy(deploymentId);
        return new DeployResponse(hostUuid, deploymentId, null, "UNDEPLOYED");
    }
}
