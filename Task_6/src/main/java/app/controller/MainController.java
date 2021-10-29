package app.controller;

import app.data.RestCounter;
import app.service.CounerService;
import app.service.RestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class MainController {

    CounerService service;

    public MainController(CounerService service) {
        this.service = service;
    }

    @GetMapping(path = "/service/v1/createCounter")
    public void createCounter(@RequestParam(value = "name") String name) {
        service.addCounter(name);
    }

    @GetMapping(path = "/service/v1/incrementCounter")
    public void incrementCounter(@RequestParam(value = "name") String name) {
        service.incrementCounter(name);
    }

    @GetMapping(path = "/service/v1/getCounterValue")
    public RestCounter getValue(@RequestParam(value = "name") String name) {
        return RestHelper.createRestCounter(service.getCounterValue(name), name);
    }

    @GetMapping(path = "/service/v1/removeCounter")
    public void removeCounter(@RequestParam(value = "name") String name) {
       service.removeCounter(name);
    }

    @GetMapping(path = "/service/v1/getCountersSum")
    public HashMap<String, Integer> getCountersSum() {
        HashMap<String, Integer> response = new HashMap<>();
        response.put("sum", service.getCountersSum());
        return response;
    }

    @GetMapping(path = "/service/v1/getCountersNames")
    public HashMap<String, List<String>> getCountersNames() {
        HashMap<String, List<String>> response = new HashMap<>();
        response.put("names", service.getCountersNames());
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HashMap<String, String>> handleException(RuntimeException e) {
        HashMap<String, String> response = new HashMap<>();
        response.put("errorMsg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
