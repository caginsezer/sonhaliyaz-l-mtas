const API_BASE = "/api/emergency";

let chartInstance = null;
let simInterval = null;

let knownTreatingIds = new Set();
let knownDischargedIds = new Set();

// ==========================================
// 1. UYGULAMA BAŞLATMA (INIT)
// ==========================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("Sistem başlatılıyor... DOM Yüklendi.");
    
    // Alt sistemleri başlat
    initChart();
    initRouter();
    startClock();
    
    // Arka plan görevlerini başlat
    fetchSimulationStatus();
    startPolling();

    // Event Listener'ları GÜVENLİ ŞEKİLDE bağla
    bindEventsSafely();
    bindLoginForm();
});

// ==========================================
// 1.5. KİMLİK DOĞRULAMA (LOGIN)
// ==========================================
function bindLoginForm() {
    const loginForm = document.getElementById("loginForm");
    if(loginForm) {
        loginForm.addEventListener("submit", (e) => {
            e.preventDefault();
            const user = document.getElementById("loginUser").value;
            const pass = document.getElementById("loginPass").value;
            
            // Asenkron Login Kontrolü (Hardcoded Auth Check)
            if(user === "admin" && pass === "1234") {
                // Giriş Başarılı
                showToast("Sisteme başarıyla giriş yapıldı.", "success");
                
                // Fade-out animasyonu
                const overlay = document.getElementById("loginOverlay");
                overlay.style.opacity = "0";
                
                setTimeout(() => {
                    overlay.style.display = "none";
                    
                    // Ana ekranı göster ve fade-in yap
                    const app = document.getElementById("appContainer");
                    app.style.display = "flex";
                    app.style.opacity = "0";
                    app.style.transition = "opacity 0.6s ease-in-out";
                    
                    // Tarayıcı render için küçük bir bekleme
                    setTimeout(() => {
                        app.style.opacity = "1";
                        // Başlangıçta simülasyonu çalıştırıp terminale bilgi geç
                        logToTerminal("SİSTEM GİRİŞİ: Kullanıcı doğrulandı (Admin).");
                    }, 50);
                }, 600); // css transition süresi (0.6s) kadar bekle
            } else {
                showToast("Hatalı kullanıcı adı veya şifre!", "error");
            }
        });
    }
}

// ==========================================
// 2. SPA ROUTER (Sayfalar Arası Geçiş)
// ==========================================
function initRouter() {
    const navItems = document.querySelectorAll('.nav-item');
    const views = document.querySelectorAll('.view-section');

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Aktif sınıfını menüden temizle ve tıklanana ekle
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            // Hedef view'ı bul ve göster
            const targetId = item.getAttribute('data-target');
            views.forEach(view => {
                if(view.id === targetId) {
                    view.classList.add('active');
                } else {
                    view.classList.remove('active');
                }
            });
            
            console.log(`Geçiş yapıldı: ${targetId}`);
        });
    });
}

// ==========================================
// 3. GÜVENLİ OLAY DİNLEYİCİLERİ
// ==========================================
function bindEventsSafely() {
    // 3.1 Triyaj Formu Gönderimi
    const admitForm = document.getElementById("admitForm");
    if(admitForm) {
        admitForm.addEventListener("submit", admitPatient);
        console.log("Triyaj formu başarıyla bağlandı.");
    } else {
        console.error("KRİTİK HATA: admitForm DOM'da bulunamadı!");
    }

    // 3.2 Simülasyon Duraklat/Başlat
    const simToggleBtn = document.getElementById("simToggleBtn");
    if(simToggleBtn) {
        simToggleBtn.addEventListener("click", toggleSimulation);
        console.log("Simülasyon Toggle butonu bağlandı.");
    }

    // 3.3 Salgın Alarmı
    const epidemicBtn = document.getElementById("epidemicBtn");
    if(epidemicBtn) {
        epidemicBtn.addEventListener("click", triggerEpidemic);
    }

    const quarantineToggle = document.getElementById("quarantineToggle");
    if(quarantineToggle) {
        quarantineToggle.addEventListener("change", toggleQuarantine);
    }

    const restockBtn = document.getElementById("restockBtn");
    if(restockBtn) {
        restockBtn.addEventListener("click", requestRestock);
    }

    const closeModalBtn = document.getElementById("closeModal");
    if(closeModalBtn) {
        closeModalBtn.addEventListener("click", closeModal);
    }

    // 3.4 Çıkış Yap (Logout)
    const logoutBtn = document.getElementById("logoutBtn");
    if(logoutBtn) {
        logoutBtn.addEventListener("click", handleLogout);
        console.log("Çıkış Yap butonu bağlandı.");
    }
}

// Çıkış İşlemi
function handleLogout() {
    // Ana uygulamayı gizle
    const app = document.getElementById("appContainer");
    app.style.opacity = "0";
    
    setTimeout(() => {
        app.style.display = "none";
        
        // Giriş ekranını göster
        const overlay = document.getElementById("loginOverlay");
        overlay.style.display = "flex";
        
        // Formu temizle
        const loginForm = document.getElementById("loginForm");
        if(loginForm) loginForm.reset();
        
        setTimeout(() => {
            overlay.style.opacity = "1";
        }, 50);
        
        showToast("Sistemden güvenli çıkış yapıldı.", "success");
        logToTerminal("SİSTEM ÇIKIŞI: Kullanıcı oturumu sonlandırdı.");
    }, 600);
}

// ==========================================
// 4. İŞ MANTIĞI VE API ÇAĞRILARI
// ==========================================

// Hasta Kabul Etme (Triyaj Form Submit)
function admitPatient(e) {
    e.preventDefault();
    console.log("Yeni hasta kayıt işlemi başlatıldı...");
    
    try {
        const nameInput = document.getElementById("name").value;
        // Age değerini KESİNLİKLE String yapıyoruz ki Jackson çökmesin
        const ageInput = String(document.getElementById("age").value);
        const complaintInput = document.getElementById("complaint").value;
        const urgencyNode = document.querySelector('input[name="urgency"]:checked');
        const urgencyLevel = urgencyNode ? urgencyNode.value : "GREEN";

        const payload = {
            name: nameInput,
            age: ageInput,
            complaint: complaintInput,
            urgencyLevel: urgencyLevel
        };

        fetch(`${API_BASE}/admit`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if(!res.ok) throw new Error(`HTTP Hata Kodu: ${res.status}`);
            return res.json();
        })
        .then(data => {
            showToast(`Başarılı: ${nameInput} sisteme kaydedildi.`, "success");
            logToTerminal(`YENİ KAYIT [${urgencyLevel}]: ${nameInput} - ${complaintInput}`);
            document.getElementById("admitForm").reset();
            fetchData(); // Listeleri anında güncelle
            
            // Eğer kullanıcı Triyaj sayfasındaysa, form dolduktan sonra Hasta Takibine yönlendirsek güzel olur
            // Ancak şimdilik sadece formu temizliyoruz.
        })
        .catch(err => {
            console.error("Admit Error:", err);
            showToast("Hasta kayıt işlemi başarısız oldu!", "error");
            logToTerminal(`KAYIT HATASI: ${err.message}`);
        });
    } catch(error) {
        console.error("JS İçi Form Hatası:", error);
    }
}

// Salgın Tetikleme
function triggerEpidemic() {
    console.log("Salgın alarmı API'ye gönderiliyor...");
    fetch(`${API_BASE}/epidemic`, { method: "POST" })
        .then(res => {
            if(!res.ok) throw new Error("API yanıt vermedi.");
            return res.text();
        })
        .then(msg => {
            showToast("SALGIN ALARMI! Kırmızı Kod!", "error");
            logToTerminal("KIRMIZI KOD: Epidemik salgın tespit edildi. Karantina aktif.");
            fetchData();
        })
        .catch(err => {
            console.error(err);
            showToast("Alarm tetiklenemedi.", "error");
        });
}

// Simülasyon Durumunu Değiştirme
function toggleSimulation() {
    console.log("Motor durumu değiştiriliyor...");
    fetch(`${API_BASE}/simulation/toggle`, { method: "POST" })
        .then(res => res.json())
        .then(data => {
            updateSimUI(data.isRunning);
            logToTerminal(`MOTOR BİLGİSİ: Zaman akışı ${data.isRunning ? 'BAŞLATILDI' : 'DONDURULDU'}`);
            showToast(`Simülasyon ${data.isRunning ? 'Başlatıldı' : 'Durduruldu'}`, data.isRunning ? "success" : "error");
        })
        .catch(err => console.error("Toggle error:", err));
}

// Karantina Modunu Değiştirme
function toggleQuarantine(e) {
    const isChecked = e.target.checked;
    fetch(`${API_BASE}/quarantine/toggle`, { method: "POST" })
        .then(res => res.json())
        .then(data => {
            logToTerminal(`KARANTİNA YÖNETİMİ: Karantina Bölgesi ${data.isQuarantineMode ? 'AKTİF' : 'KAPALI'}`);
            showToast(`Karantina Modu ${data.isQuarantineMode ? 'Açıldı' : 'Kapatıldı'}`, data.isQuarantineMode ? "error" : "success");
        });
}

// Tedarik Talebi
function requestRestock() {
    fetch(`${API_BASE}/inventory/restock`, { method: "POST" })
        .then(res => res.text())
        .then(msg => {
            logToTerminal("LOJİSTİK: Yeni tıbbi stoklar depoya ulaştı.");
            showToast("Tıbbi stoklar başarıyla yenilendi.", "success");
            fetchData();
        });
}

function updateSimUI(isRunning) {
    const dot = document.getElementById("simDot");
    const text = document.getElementById("simStatusText");
    const btn = document.getElementById("simToggleBtn");
    
    if(!dot || !text || !btn) return;

    if (isRunning) {
        dot.classList.add("active");
        text.innerText = "SİSTEM AKTİF";
        btn.innerText = "Simülasyonu Duraklat";
        btn.className = "btn btn-outline";
    } else {
        dot.classList.remove("active");
        text.innerText = "SİSTEM DURAKLATILDI";
        btn.innerText = "Simülasyonu Başlat";
        btn.className = "btn btn-primary";
    }
}

// Simülasyon Durumunu İlk Yüklemede Alma
function fetchSimulationStatus() {
    fetch(`${API_BASE}/simulation/status`)
        .then(res => res.json())
        .then(data => updateSimUI(data.isRunning))
        .catch(err => console.error("Status fetch error", err));
        
    fetch(`${API_BASE}/quarantine/status`)
        .then(res => res.json())
        .then(data => {
            const qt = document.getElementById("quarantineToggle");
            if(qt) qt.checked = data.isQuarantineMode;
        });
}

// ==========================================
// 5. POLLING (Gerçek Zamanlı Veri Akışı)
// ==========================================
function startPolling() {
    simInterval = setInterval(() => {
        fetchData();
        checkAutoSpawn();
    }, 1000);
}

function checkAutoSpawn() {
    const toggle = document.getElementById("autoSpawnToggle");
    if(toggle && toggle.checked && Math.random() < 0.15) { 
        const names = ["Cemre Ateş", "Can Polat", "Defne Yılmaz", "Burak Kılıç", "Elif Demir", "Ali Veli", "Ayşe Can", "Veli Yıldız", "Fatma Nur", "Kerem Sancak"];
        const diseases = [
            { comp: "Kalp Krizi Şüphesi", urg: "RED" },
            { comp: "Ağır Trafik Kazası", urg: "RED" },
            { comp: "Şiddetli İç Kanama", urg: "RED" },
            { comp: "Yüksek Ateş ve Öksürük", urg: "YELLOW" }, // Bulaşıcı
            { comp: "Covid Şüphesi (Enfeksiyon)", urg: "YELLOW" }, // Bulaşıcı
            { comp: "Şiddetli Karın Ağrısı", urg: "YELLOW" },
            { comp: "Ağır Grip ve Halsizlik", urg: "YELLOW" }, // Bulaşıcı
            { comp: "Hafif Kesik", urg: "GREEN" },
            { comp: "Basit Soğuk Algınlığı", urg: "GREEN" },
            { comp: "Ayak Burkulması", urg: "GREEN" }
        ];
        
        const randomName = names[Math.floor(Math.random() * names.length)];
        const randomDisease = diseases[Math.floor(Math.random() * diseases.length)];
        const randomAge = String(20 + Math.floor(Math.random() * 50));
        
        fetch(`${API_BASE}/admit`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: randomName, age: randomAge, complaint: randomDisease.comp, urgencyLevel: randomDisease.urg })
        }).catch(err => console.error(err));
    }
}

function fetchData() {
    Promise.all([
        fetch(`${API_BASE}/patients/waiting`).then(r => r.json()),
        fetch(`${API_BASE}/patients/quarantined`).then(r => r.json()),
        fetch(`${API_BASE}/patients/treating`).then(r => r.json()),
        fetch(`${API_BASE}/patients/discharged`).then(r => r.json()),
        fetch(`${API_BASE}/doctors`).then(r => r.json()),
        fetch(`${API_BASE}/inventory`).then(r => r.json())
    ]).then(([waiting, quarantined, treating, discharged, doctors, inventory]) => {
        
        // 1. Dashboard Stat Güncellemeleri
        const totalWaiting = waiting.length + quarantined.length;
        if(document.getElementById("statWaiting")) document.getElementById("statWaiting").innerText = totalWaiting;
        if(document.getElementById("waitingCount")) document.getElementById("waitingCount").innerText = waiting.length;
        if(document.getElementById("quarantineCount")) document.getElementById("quarantineCount").innerText = quarantined.length;
        
        if(document.getElementById("statTreating")) document.getElementById("statTreating").innerText = treating.length;
        if(document.getElementById("treatingCount")) document.getElementById("treatingCount").innerText = treating.length;
        
        const availableDocs = doctors.filter(d => d.available).length;
        if(document.getElementById("statDoctors")) document.getElementById("statDoctors").innerText = availableDocs;
        if(document.getElementById("statTotalDocs")) document.getElementById("statTotalDocs").innerText = doctors.length;
        
        if(document.getElementById("dischargedCount")) document.getElementById("dischargedCount").innerText = discharged.length;

        // --- CANLI LOGLAMA MANTIĞI ---
        treating.forEach(p => {
            if (!knownTreatingIds.has(p.id)) {
                knownTreatingIds.add(p.id);
                const docName = p.assignedDoctor ? p.assignedDoctor.name : "Bir doktor";
                logToTerminal(`SİSTEM: ${p.name} adlı hasta ${docName} tarafından tedaviye alındı.`);
            }
        });

        discharged.forEach(p => {
            if (!knownDischargedIds.has(p.id)) {
                knownDischargedIds.add(p.id);
                // Eğer tedavi listesinde varsa oradan çıkaralım
                if(knownTreatingIds.has(p.id)) knownTreatingIds.delete(p.id);
                
                logToTerminal(`TABURCU: ${p.name} taburcu edildi. (${p.finalBill})`);
            }
        });
        // -----------------------------

        // 2. Arayüz Listelerini Render Et
        renderWaitingList(waiting);
        renderQuarantineList(quarantined);
        renderTreatingList(treating);
        renderDischargedList(discharged);
        renderDoctorsList(doctors, treating);
        renderInventory(inventory);
        
        // 3. Grafiği Güncelle
        updateChart(waiting, quarantined, treating);
        
    }).catch(err => {
        // Polling çok hızlı çalıştığı için konsolu boğmamak adına sessiz bırakıyoruz
    });
}

// ==========================================
// 6. RENDER MOTORU (DOM Manipülasyonları)
// ==========================================

function getDotClass(urgency) {
    if (urgency === 'RED') return 'dot-red';
    if (urgency === 'YELLOW') return 'dot-yellow';
    return 'dot-green';
}

function renderWaitingList(waiting) {
    const list = document.getElementById("waitingList");
    if(!list) return;
    
    let html = "";
    
    waiting.forEach(p => {
        html += `
            <div class="list-item">
                <div class="patient-info">
                    <div class="urgency-dot ${getDotClass(p.urgencyLevel)}"></div>
                    <div>
                        <div class="p-name">${p.name}</div>
                        <div class="p-desc">${p.complaint}</div>
                    </div>
                </div>
            </div>
        `;
    });
    
    if(html === "") {
        html = `<div class="p-4 text-center text-muted">Bekleyen hasta bulunmuyor.</div>`;
    }
    
    list.innerHTML = html;
}

function renderQuarantineList(quarantined) {
    const list = document.getElementById("quarantineList");
    if(!list) return;
    
    let html = "";
    
    quarantined.forEach(p => {
        html += `
            <div class="list-item quarantined-item">
                <div class="patient-info">
                    <div class="urgency-dot dot-red"></div>
                    <div>
                        <div class="p-name"><span class="material-icons-round text-red" style="font-size:16px; vertical-align:middle;">coronavirus</span> ${p.name}</div>
                        <div class="p-desc">${p.complaint}</div>
                    </div>
                </div>
            </div>
        `;
    });
    
    if(html === "") {
        html = `<div class="p-4 text-center text-muted">Karantina bölgesi temiz.</div>`;
    }
    
    list.innerHTML = html;
}

function renderTreatingList(treating) {
    const list = document.getElementById("treatingList");
    if(!list) return;
    
    let html = "";
    treating.forEach(p => {
        let doctorName = p.assignedDoctor ? p.assignedDoctor.name : "Bilinmiyor";
        html += `
            <div class="list-item">
                <div class="patient-info">
                    <div class="urgency-dot ${getDotClass(p.urgencyLevel)}"></div>
                    <div>
                        <div class="p-name">${p.name}</div>
                        <div class="p-desc">${p.complaint}</div>
                    </div>
                </div>
                <div class="treatment-info">
                    <span class="time-left">${p.treatmentTimeRemaining}s</span>
                    <span class="doc-name"><span class="material-icons-round" style="font-size:12px">medical_services</span> Dr. ${doctorName}</span>
                </div>
            </div>
        `;
    });
    
    if(html === "") {
        html = `<div class="p-4 text-center text-muted">Tedavide hasta bulunmuyor.</div>`;
    }
    
    list.innerHTML = html;
}

function renderDischargedList(discharged) {
    const list = document.getElementById("dischargedList");
    if(!list) return;
    
    let html = "";
    discharged.forEach(p => {
        let billText = p.finalBill ? p.finalBill : "Fatura Çıkarılamadı";
        html += `
            <div class="list-item">
                <div class="patient-info">
                    <div class="urgency-dot dot-green"></div>
                    <div>
                        <div class="p-name">${p.name} <span style="font-size: 0.8rem; color: var(--green);">(Taburcu)</span></div>
                        <div class="p-desc">${p.complaint}</div>
                    </div>
                </div>
                <div class="treatment-info" style="flex: 2; text-align: right;">
                    <div style="font-size: 0.85rem; color: var(--text-main); font-weight: 500;">
                        ${billText.replace("|", "<br><strong style='color:var(--accent);'>")}</strong>
                    </div>
                </div>
            </div>
        `;
    });
    
    if(html === "") {
        html = `<div class="p-4 text-center text-muted">Henüz taburcu edilen hasta bulunmuyor.</div>`;
    }
    
    list.innerHTML = html;
}

function renderDoctorsList(doctors, treating) {
    const list = document.getElementById("doctorsList");
    if(!list) return;
    
    let html = "";
    doctors.forEach(d => {
        const statusIcon = d.available ? 'check_circle' : (d.resting ? 'bed' : 'healing');
        const statusColor = d.available ? 'var(--green)' : (d.resting ? 'var(--yellow)' : 'var(--accent)');
        const statusText = d.available ? 'Müsait' : (d.resting ? 'Dinleniyor' : 'Tedavide');
        
        let extraInfo = `Baktığı Hasta: ${d.patientsTreated}`;
        if (!d.available && !d.resting && treating) {
            const patient = treating.find(p => p.assignedDoctor && p.assignedDoctor.id === d.id);
            if (patient) {
                extraInfo = `<span style="color:var(--accent); font-weight:700;">${patient.treatmentTimeRemaining}s sonra boşalacak</span> | Toplam: ${d.patientsTreated}`;
            }
        } else if (d.resting) {
            extraInfo = `<span style="color:var(--yellow); font-weight:700;">Enerji doluyor...</span> | Toplam: ${d.patientsTreated}`;
        }
        
        html += `
            <div class="list-item">
                <div class="patient-info">
                    <div class="avatar" style="width:36px; height:36px; display:flex; align-items:center; justify-content:center; background:#e2e8f0;">
                        <span class="material-icons-round" style="color:var(--text-muted); font-size:20px;">person</span>
                    </div>
                    <div>
                        <div class="p-name">${d.name}</div>
                        <div class="p-desc">${d.specialization} | Enerji: %${d.energyLevel}</div>
                    </div>
                </div>
                <div class="treatment-info">
                    <span style="color:${statusColor}; font-weight:700; font-size:0.85rem; display:flex; align-items:center; gap:4px; justify-content:flex-end;">
                        <span class="material-icons-round" style="font-size:16px;">${statusIcon}</span> ${statusText}
                    </span>
                    <span class="doc-name">${extraInfo}</span>
                </div>
            </div>
        `;
    });
    
    list.innerHTML = html;
}

function renderInventory(inv) {
    const grid = document.getElementById("inventoryGrid");
    if(!grid) return;

    grid.innerHTML = `
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
            <div style="padding:10px; background:#f8fafc; border-radius:8px; border:1px solid #e2e8f0;">
                <div style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Kan Üniteleri</div>
                <div style="font-size:1.4rem; font-weight:800; color:var(--red);">${inv.bloodUnits}</div>
            </div>
            <div style="padding:10px; background:#f8fafc; border-radius:8px; border:1px solid #e2e8f0;">
                <div style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Serum & Şırınga</div>
                <div style="font-size:1.4rem; font-weight:800; color:var(--accent);">${inv.serums} / ${inv.syringes}</div>
            </div>
            <div style="padding:10px; background:#f8fafc; border-radius:8px; border:1px solid #e2e8f0;">
                <div style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Antibiyotik / Aşı</div>
                <div style="font-size:1.4rem; font-weight:800; color:var(--green);">${inv.antibiotics} / ${inv.vaccines}</div>
            </div>
            <div style="padding:10px; background:#f8fafc; border-radius:8px; border:1px solid #e2e8f0;">
                <div style="font-size:0.75rem; color:var(--text-muted); text-transform:uppercase; font-weight:700;">Ağrı Kesici / Bandaj</div>
                <div style="font-size:1.4rem; font-weight:800; color:var(--yellow);">${inv.painkillers} / ${inv.bandages}</div>
            </div>
        </div>
    `;
}

// ==========================================
// 7. YARDIMCI FONKSİYONLAR (UI / UTILS)
// ==========================================

function initChart() {
    const ctxEl = document.getElementById('triageChart');
    if(!ctxEl) return;
    
    chartInstance = new Chart(ctxEl.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: ['Kırmızı (Acil)', 'Sarı (Orta)', 'Yeşil (Hafif)'],
            datasets: [{
                data: [0, 0, 0],
                backgroundColor: ['#e11d48', '#f59e0b', '#0d9488'], // Enterprise renkleri
                borderWidth: 0,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '75%',
            plugins: {
                legend: { position: 'bottom', labels: { color: '#64748b', font: { family: 'Inter', size: 13 } } }
            }
        }
    });
}

function updateChart(waiting, quarantined, treating) {
    if(!chartInstance) return;
    let red = 0, yellow = 0, green = 0;
    
    [...waiting, ...quarantined, ...treating].forEach(p => {
        if(p.urgencyLevel === 'RED') red++;
        else if(p.urgencyLevel === 'YELLOW') yellow++;
        else green++;
    });

    const total = red + yellow + green;
    const rPct = total === 0 ? 0 : Math.round((red / total) * 100);
    const yPct = total === 0 ? 0 : Math.round((yellow / total) * 100);
    const gPct = total === 0 ? 0 : Math.round((green / total) * 100);

    chartInstance.data.labels = [
        `Kırmızı / Acil (%${rPct})`, 
        `Sarı / Orta (%${yPct})`, 
        `Yeşil / Hafif (%${gPct})`
    ];

    chartInstance.data.datasets[0].data = [red, yellow, green];
    chartInstance.update();
}

function startClock() {
    setInterval(() => {
        const clockEl = document.getElementById("liveClock");
        if(clockEl) clockEl.innerText = new Date().toLocaleTimeString('tr-TR');
    }, 1000);
}

function showToast(msg, type = "success") {
    const container = document.getElementById("toastContainer");
    if(!container) return;
    
    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    
    const icon = document.createElement("span");
    icon.className = "material-icons-round";
    icon.innerText = type === 'success' ? 'check_circle' : 'error';
    icon.style.color = type === 'success' ? 'var(--green)' : 'var(--red)';
    
    const textSpan = document.createElement("span");
    textSpan.innerText = msg;
    
    toast.appendChild(icon);
    toast.appendChild(textSpan);
    
    container.appendChild(toast);
    setTimeout(() => { if(container.contains(toast)) container.removeChild(toast); }, 4000);
}

function logToTerminal(msg) {
    const terminal = document.getElementById("systemLogs");
    if(!terminal) return;

    const div = document.createElement("div");
    div.className = "log-entry";
    
    const timeSpan = document.createElement("span");
    timeSpan.className = "log-time";
    timeSpan.innerText = `[${new Date().toLocaleTimeString('tr-TR')}]`;
    
    const textSpan = document.createElement("span");
    textSpan.innerText = msg;
    
    div.appendChild(timeSpan);
    div.appendChild(textSpan);
    
    terminal.appendChild(div);
    terminal.scrollTop = terminal.scrollHeight;
}

function showModal(title, content) {
    const m = document.getElementById("medModal");
    if(!m) return;
    document.getElementById("modalTitle").innerText = title;
    document.getElementById("modalBody").innerHTML = content;
    m.style.display = "flex";
}

function closeModal() {
    const m = document.getElementById("medModal");
    if(m) m.style.display = "none";
}
