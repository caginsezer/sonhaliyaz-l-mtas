package com.example.ersimulator.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class MedicalInventoryService {
    // Başlangıç değerleri gerçekçi acil servis seviyelerine kalibre edildi
    private int bloodUnits = 20;
    private int serums = 50;
    private int syringes = 100;
    private int painkillers = 50;
    private int bandages = 80;
    private int antibiotics = 40;
    private int vaccines = 30;

    public boolean hasStockForRed() {
        return bloodUnits >= 2 && serums >= 1 && syringes >= 1 && antibiotics >= 1;
    }

    public void consumeForRed() {
        bloodUnits -= 2; serums -= 1; syringes -= 1; antibiotics -= 1;
    }

    public boolean hasStockForYellow() {
        return serums >= 1 && painkillers >= 1 && syringes >= 1 && antibiotics >= 1;
    }

    public void consumeForYellow() {
        serums -= 1; painkillers -= 1; syringes -= 1; antibiotics -= 1;
    }

    public boolean hasStockForGreen() {
        return (bandages >= 1 && painkillers >= 1) || vaccines >= 1;
    }

    public void consumeForGreen() {
        if (bandages >= 1) {
            bandages -= 1; painkillers -= 1;
        } else {
            vaccines -= 1; syringes -= 1;
        }
    }

    public void restockAll() {
        this.bloodUnits += 100;
        this.serums += 200;
        this.syringes += 300;
        this.painkillers += 200;
        this.bandages += 300;
        this.antibiotics += 150;
        this.vaccines += 100;
    }

    public Map<String, Integer> getInventoryStatus() {
        Map<String, Integer> status = new HashMap<>();
        status.put("bloodUnits", bloodUnits);
        status.put("serums", serums);
        status.put("syringes", syringes);
        status.put("painkillers", painkillers);
        status.put("bandages", bandages);
        status.put("antibiotics", antibiotics);
        status.put("vaccines", vaccines);
        return status;
    }
}
