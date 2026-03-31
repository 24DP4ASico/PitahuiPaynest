# Pitahui PayNest
Abonementu pārvaldības sistēma

Pitahui PayNest ir lietotne, kas paredzēta, lai lietotāji varētu ērti pārvaldīt visus savus abonementus vienuviet. Tā piedāvā vienkāršu, pārskatāmu un drošu veidu, kā sekot abonementu termiņiem, maksājumiem, izmaksām un paziņojumiem.

---

## 📌 Saturs
- [Projekta apraksts](#projekta-apraksts)
- [Galvenās funkcijas](#galvenās-funkcijas)
- [Mērķauditorija](#mērķauditorija)
- [Konkurentu analīze](#konkurentu-analīze)
- [Sistēmas struktūra](#sistēmas-struktūra)
- [Datu struktūra](#datu-struktūra)
- [Izmantotās tehnoloģijas](#izmantotās-tehnoloģijas)
- [Komanda](#komanda)

---

## 📖 Projekta apraksts

Mūsdienās lietotāji pārvalda arvien vairāk abonementu: straumēšanas platformas, produktivitātes rīkus, spēļu servisus, mākoņkrātuves u.c. Informācija par maksājumiem ir izkliedēta, un bieži tiek aizmirsti termiņi, kas rada liekus izdevumus un sarežģī ikdienas finanšu pārvaldību.

**Pitahui PayNest piedāvā vienotu vietu, kur:**
- apskatīt visus abonementus,
- sekot maksājumiem un termiņiem,
- saņemt paziņojumus,
- pārvaldīt maksājumus un statistiku,
- vienkāršot ikdienas finanšu uzraudzību.

Šī lietotne fokusējas uz vienkāršību, pieejamību un plašu lietotāju loku — no jauniešiem līdz senioriem.

---

## ✅ Galvenās funkcijas

### 📌 Abonementu pārvaldība
- Abonementu pievienošana, rediģēšana un dzēšana.
- Taimeris līdz abonementa beigām.
- Visi abonementi vienuviet.

### 💳 Maksājumu pārvaldība
- Automātiskie maksājumi.
- Maksājumu vēsture.
- Vienas dienas abonementu apvienošana vienā maksājumā.

### 🔔 Paziņojumi
- Atgādinājumi par abonementu termiņiem.
- Detalizēts paziņojumu saraksts.
- Atlikušo dienu aprēķins.

### 📊 Statistika
- Abonementu diagrammas un tabulas.
- Kopējo izmaksu aprēķins.
- Mēneša pārskati.

### 👤 Lietotāju pārvaldība
- Lietotāja datu ievade un rediģēšana.
- Droša informācijas glabāšana.

---

## 🎯 Mērķauditorija

Pitahui PayNest paredzēta:

- Jauniešiem (16–25), kas aktīvi izmanto digitālos pakalpojumus.
- Pieaugušajiem (26–65+), kuri regulāri pārvalda ikmēneša maksājumus.
- Senioriem un lietotājiem ar minimālām tehniskām zināšanām.

Lietotne ir maksimāli vienkārša, skaidra un pieejama visiem.

---

## 📊 Konkurentu analīze

### 🔹 Rocket Money
- Automātiska abonementu noteikšana, atcelšana un rēķinu sarunas.
- Ierobežots ASV tirgum.

### 🔹 Monarch Money
- Plaša finanšu vadība (investīcijas, budžets, analītika).
- Sarežģītāka un maksas.

### 🔹 PocketGuard
- Vienkārša ikdienas finanšu pārraudzība.
- Mazāk iespēju tieši abonementiem.

### ✅ **Pitahui PayNest priekšrocības**
- Globāls risinājums (nav tikai ASV tirgum).
- Ļoti vienkāršs interfeiss.
- Atbalsta vairākas personas (ģimenes režīms).
- Nav slēpto maksu.
- Fokusēts tieši uz abonementiem, nevis liekām funkcijām.

---

## 🏗️ Sistēmas struktūra

Sistēmu veido trīs galvenās apakšsistēmas:

### 🔸 1. Abonementu apstrādes apakšsistēma
- Abonementu pievienošana, rediģēšana, dzēšana.
- Atjaunošanas termiņu uzraudzība.

### 🔸 2. Maksājumu apstrādes apakšsistēma
- Automātiskie maksājumi.
- Maksājumu vēsture un pārskati.
- Vienotie maksājumi.

### 🔸 3. Paziņojumu apstrādes apakšsistēma
- Brīdinājumi par termiņiem.
- Lietotāja atbildes un paziņojumu arhīvs.

---

## 🗂️ Datu struktūra

Sistēma izmanto četras galvenās tabulas:

### **1. lietotajs**
- vārds, uzvārds
- tālrunis
- IBAN
- saite ar maksājumiem un paziņojumiem

### **2. abonementi**
- nosaukums
- tips
- cena
- ilgums
- saite ar maksājumiem un paziņojumiem

### **3. maksajums**
- datums
- summa
- statuss

### **4. pazinojums**
- abonementa termiņš
- brīdinājuma teksts
- atlikušās dienas

---

## 🛠️ Izmantotās tehnoloģijas

- **Java** – galvenā programmēšanas valoda
- **Visual Studio Code** – izstrādes vide
- **Teksta faili** – datu glabāšana
- **GitHub** – versiju kontrole
- **Trello** – uzdevumu pārvaldība

---

## 👥 Komanda
- **Timurs Sibiļovs**
- **Ēriks Vansovičs**
- **Anželika Sičova**

---

## 📄 Licence
*(Ja nepieciešams, varu pievienot MIT, GPL, CC BY-NC u.c.)*