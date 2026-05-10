package com.example.ersimulator.pattern.observer;

import com.example.ersimulator.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmergencyNotifier {
    private List<EmergencyObserver> observers = new ArrayList<>();

    public void addObserver(EmergencyObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EmergencyObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Patient patient) {
        // Red kodlu (En acil) hasta geldiğinde tüm doktorları veya departmanları uyar
        for (EmergencyObserver observer : observers) {
            observer.update(patient);
        }
    }
}
