package app.client;

import app.data.Currency;
import org.springframework.stereotype.Component;

@Component
public class Client {

    /**
     * Эмуляция вызова к стороннему API
     *
     * @return Стоимость услуги
     */
    public Double getCost() {
        return (Math.random() * 90 + 10);
    }

    /**
     * Эмуляция вызова к стороннему API
     *
     * @return Валюта услуги
     */
    public Currency getCurrency() {
        return Currency.values()[(int) (Math.random() * 3)];
    }

}
