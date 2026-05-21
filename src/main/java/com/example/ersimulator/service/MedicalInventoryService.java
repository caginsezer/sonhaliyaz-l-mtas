package com.example.ersimulator.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  TIBBİ ENVANTER SERVİSİ (MedicalInventoryService)               ║
// ║  Bu servis, hastanenin ilaç ve tıbbi malzeme deposudur.          ║
// ║  FACADE deseninin ALT SİSTEMİDİR (Subsystem).                   ║
// ║  Facade, doktor atamadan önce bu servisten stok sorgular:       ║
// ║  - hasStockForRed() / hasStockForYellow() / hasStockForGreen()   ║
// ║  Stok varsa malzemeyi tüketir: consumeForRed() vs.              ║
// ║  Buradaki malzemeler DECORATOR desenindeki faturalandırma       ║
// ║  öğeleriyle (Kan, Serum, Antibiyotik vb.) doğrudan ilişkilidir. ║
// ╚══════════════════════════════════════════════════════════════════╝
@Service // Spring bu sınıfı Singleton bir Bean olarak yönetir
public class MedicalInventoryService {
    
    // Tıbbi malzeme stokları (Başlangıç değerleri gerçekçi acil servis seviyelerine kalibre edildi)
    private int bloodUnits = 20;    // Kan Ünitesi (Kırmızı Kod ve BloodUnitDecorator ile ilişkili)
    private int serums = 50;        // Serum (Kırmızı/Sarı Kod ve SerumDecorator ile ilişkili)
    private int syringes = 100;     // Şırınga (Tüm enjeksiyonlarda kullanılır)
    private int painkillers = 50;    // Ağrı Kesici (Sarı Kod ve PainkillerDecorator ile ilişkili)
    private int bandages = 80;      // Bandaj (Yeşil Kod ile ilişkili)
    private int antibiotics = 40;   // Antibiyotik (Enfeksiyon vakaları ve AntibioticDecorator ile ilişkili)
    private int vaccines = 30;      // Aşı (Isırık/Kesik vakaları ve VaccineDecorator ile ilişkili)

    // ─── KIRMIZI KOD STOK KONTROLÜ ───
    // Kırmızı kod için: 2 Kan, 1 Serum, 1 Şırınga ve 1 Antibiyotik gerekir
    public boolean hasStockForRed() {
        return bloodUnits >= 2 && serums >= 1 && syringes >= 1 && antibiotics >= 1;
    }

    // ─── KIRMIZI KOD STOK TÜKETİMİ ───
    public void consumeForRed() {
        bloodUnits -= 2; serums -= 1; syringes -= 1; antibiotics -= 1;
    }

    // ─── SARI KOD STOK KONTROLÜ ───
    // Sarı kod için: 1 Serum, 1 Ağrı Kesici, 1 Şırınga ve 1 Antibiyotik gerekir
    public boolean hasStockForYellow() {
        return serums >= 1 && painkillers >= 1 && syringes >= 1 && antibiotics >= 1;
    }

    // ─── SARI KOD STOK TÜKETİMİ ───
    public void consumeForYellow() {
        serums -= 1; painkillers -= 1; syringes -= 1; antibiotics -= 1;
    }

    // ─── YEŞİL KOD STOK KONTROLÜ ───
    // Yeşil kod için: Bandaj + Ağrı Kesici VEYA Aşı gerekir
    public boolean hasStockForGreen() {
        return (bandages >= 1 && painkillers >= 1) || vaccines >= 1;
    }

    // ─── YEŞİL KOD STOK TÜKETİMİ ───
    public void consumeForGreen() {
        if (bandages >= 1) {
            bandages -= 1; painkillers -= 1;
        } else {
            vaccines -= 1; syringes -= 1;
        }
    }

    // ─── TEDARİK TALEBİ (Stokları Yenileme) ───
    // Arayüzdeki "Tedarik Talebi" butonu tıklandığında çalışır
    public void restockAll() {
        this.bloodUnits += 100;
        this.serums += 200;
        this.syringes += 300;
        this.painkillers += 200;
        this.bandages += 300;
        this.antibiotics += 150;
        this.vaccines += 100;
    }

    // ─── STOK DURUMUNU PAZARLA (API için Map formatında) ───
    // Arayüzdeki "Tıbbi Envanter" panelini besleyen verileri hazırlar
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
