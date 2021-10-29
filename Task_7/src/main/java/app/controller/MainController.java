package app.controller;

import app.data.Money;
import app.data.rest.RestMoney;
import app.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RestController
public class MainController {

    ExchangeService service;

    public MainController(ExchangeService service) {
        this.service = service;
    }

    /**
     * Получаем стоимость некой услуги
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/service/v1/method")
    public RestMoney method() throws Exception {
        Money money = service.execute();
        return service.convertMoney2Rest(money);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, String>> handleException(Exception e) {
        HashMap<String, String> response = new HashMap<>();
        if (e instanceof InterruptedException || e instanceof ExecutionException) {
            response.put("errorMsg", "Произошла ошибка при обращении к одному из API поставщика");
        } else {
            response.put("errorMsg", "Неизвестная ошибка сервера");
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
