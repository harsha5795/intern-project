package spring.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.project.Service.DashboardService;

import java.util.Map;

@RestController
public class DashboardControllerImp implements DashboardController{

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }

}
