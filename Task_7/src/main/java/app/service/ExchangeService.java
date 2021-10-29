package app.service;

import app.client.Client;
import app.data.Currency;
import app.data.Money;
import app.data.rest.RestMoney;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ExchangeService {

    Client client;

    public ExchangeService(Client client) {
        this.client = client;
    }

    public Money execute() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Double> future1 = executorService.submit(client::getCost);
        Future<Currency> future2 = executorService.submit(client::getCurrency);
        Money result = new Money(future2.get(), BigDecimal.valueOf(future1.get()));

        executorService.shutdown();
        return result;
    }

    public RestMoney convertMoney2Rest(Money money) {
        RestMoney restMoney = new RestMoney();
        restMoney.setAmount(money.getAmount());
        restMoney.setCurrency(money.getCurrency());
        return restMoney;
    }
}
