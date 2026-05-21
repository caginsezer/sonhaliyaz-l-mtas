package com.example.ersimulator.pattern.decorator;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  DECORATOR DESENİ — Interface (Component)                       ║
// ║  Tüm tedavi nesnelerinin uyması gereken sözleşme.               ║
// ║  "Açıklaman ve fiyatın olacak" der.                             ║
// ║  Implement eden: BasicTreatment (doğrudan)                      ║
// ║                  TreatmentDecorator (abstract, 6 alt sınıf)     ║
// ║  Client: EmergencyRoomFacade → dischargePatient() metodu        ║
// ╚══════════════════════════════════════════════════════════════════╝
public interface Treatment {
    String getDescription();  // Tedavinin açıklamasını döndür (örn: "Temel Muayene, Yatak Ücreti")
    double getCost();          // Tedavinin toplam maliyetini döndür (örn: 600.0)
}
