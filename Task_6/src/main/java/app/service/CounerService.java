package app.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CounerService {
    HashMap<String, Integer> counters;

    public CounerService() {
        counters = new HashMap<>();
    }

    public void addCounter(String name) {
        if (!counters.containsKey(name)) {
            counters.put(name, 0);
        } else {
            throw new RuntimeException("Счетчик с таким именем уже есть в системе");
        }
    }

    public void incrementCounter(String name) {
        handleCounterExist(name);
        counters.put(name, counters.get(name) + 1);
    }

    public Integer getCounterValue(String name) {
        handleCounterExist(name);
        return counters.get(name);
    }

    public void removeCounter(String name) {
        handleCounterExist(name);
        counters.remove(name);
    }

    public int getCountersSum() {
        return counters.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<String> getCountersNames() {
        return new ArrayList<>(counters.keySet());
    }

    private void handleCounterExist(String name) {
        if (!counters.containsKey(name)) {
            throw new RuntimeException("Нет счетчика с таким именем");
        }
    }
}
