package app.service;

import app.data.RestCounter;

public class RestHelper {
    public static RestCounter createRestCounter(Integer val, String name) {
        RestCounter counter = new RestCounter();
        counter.setName(name);
        counter.setValue(val);
        return counter;
    }
}
