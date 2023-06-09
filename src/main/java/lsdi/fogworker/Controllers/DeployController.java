package lsdi.fogworker.Controllers;

import com.espertech.esper.runtime.client.EPUndeployException;
import lsdi.fogworker.DataTransferObjects.DeployRequest;
import lsdi.fogworker.Services.DeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {
    @Autowired
    private DeployService deployService;

    @PostMapping("/deploy")
    public void deploy(@RequestBody DeployRequest deployRequest) {
        try {
            deployService.deploy(deployRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/undeploy")
    public void undeploy(@PathVariable String deployId) {
        try {
            deployService.undeploy(deployId);
        } catch (EPUndeployException e) {
            e.printStackTrace();
        }
    }
}
