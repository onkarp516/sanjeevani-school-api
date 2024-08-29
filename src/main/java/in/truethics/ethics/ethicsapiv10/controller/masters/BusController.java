package in.truethics.ethics.ethicsapiv10.controller.masters;


import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.service.master_service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BusController {

    @Autowired
    private BusService busService;

    @PostMapping(path = "/create_bus_stop")
    public Object createBus(HttpServletRequest request) {

        return busService.createBus(request);
    }

    @PostMapping(path = "/updateBusStop")
    public Object updateBusStop(HttpServletRequest request) {
        return busService.updateBusStop(request);
    }


    @GetMapping(path = "/getAllBusStop")
    public Object getAllBusStops(HttpServletRequest request) {
        JsonObject result = busService.getAllBusStop(request);
        return result.toString();
    }


    @PostMapping(path = "/getBusStopDetailsById")
    public Object getBusStopById(HttpServletRequest request) {
        return busService.getBusStopById(request);
    }


    @PostMapping(path = "/deleteBus")
    public Object deleteFeestransactions(HttpServletRequest request) {
        JsonObject result = busService.deleteBus(request);
        return result.toString();
    }


}


