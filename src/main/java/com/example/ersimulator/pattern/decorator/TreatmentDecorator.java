package com.example.ersimulator.pattern.decorator;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  DECORATOR DESENİ — Abstract Decorator (Sarmalayıcı Taban)      ║
// ║  Treatment interface'ini implement eder.                        ║
// ║  İÇ İÇE SARMA (WRAPPING) mekanizmasını burada kurar.           ║
// ║  decoratedTreatment alanı: İçine sardığı nesneyi tutar.        ║
// ║  6 concrete decorator (BedFee, Serum, BloodUnit, Antibiotic,    ║
// ║  Painkiller, Vaccine) bu sınıfı extends eder.                  ║
// ║  Benzetme: Ambalaj makinesi — her şeyin üstüne sarılabilir.    ║
// ╚══════════════════════════════════════════════════════════════════╝
public abstract class TreatmentDecorator implements Treatment {
    
    // İçine sardığı Treatment nesnesi (BasicTreatment veya başka bir Decorator olabilir)
    protected Treatment decoratedTreatment;

    // Constructor: "Bana bir Treatment ver, ben onu sarmalayayım"
    public TreatmentDecorator(Treatment decoratedTreatment) {
        this.decoratedTreatment = decoratedTreatment;
    }

    @Override
    public String getDescription() {
        // Varsayılan: İçindekinin açıklamasını döndür (alt sınıflar override eder)
        return decoratedTreatment.getDescription();
    }

    @Override
    public double getCost() {
        // Varsayılan: İçindekinin fiyatını döndür (alt sınıflar override edip kendi fiyatını ekler)
        return decoratedTreatment.getCost();
    }
}
