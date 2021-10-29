package app.data.rest;

import app.data.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class RestMoney {

    @JsonProperty
    Currency currency;

    @JsonProperty
    BigDecimal amount;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
