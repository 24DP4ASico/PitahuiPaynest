# Pitahui PayNest

Pitahui PayNest ir termināļa tipa abonementu pārvaldības sistēma, kas palīdz lietotājam vienuviet pārskatīt abonementus, veikt maksājumus, sekot paziņojumiem par termiņiem un kontrolēt ikmēneša izdevumus.

## Saturs
- [Projekta apraksts](#projekta-apraksts)
- [Galvenās funkcijas](#galvenās-funkcijas)
- [Mērķauditorija](#mērķauditorija)
- [Konkurentu analīze](#konkurentu-analīze)
- [Sistēmas struktūra](#sistēmas-struktūra)
- [Datu struktūra](#datu-struktūra)
- [Izmantotās tehnoloģijas](#izmantotās-tehnoloģijas)
- [Komanda un lietotāju instrukcija](#komanda-un-lietotāju-instrukcija)

## Projekta apraksts

Mūsdienās lietotāji izmanto vairākus digitālos servisus vienlaikus, tāpēc abonementu termiņi un izmaksas bieži tiek aizmirstas. Pitahui PayNest risina šo problēmu, nodrošinot centralizētu sistēmu, kur lietotājs var:
- reģistrēt un pārvaldīt savus abonementus,
- redzēt maksājumu vēsturi,
- saņemt paziņojumus par abonementu beigām,
- sekot līdzi mēneša kopējiem izdevumiem.

Sistēma darbojas komandrindā un glabā datus SQLite datubāzē.

## Galvenās funkcijas

- Lietotāju pārvaldība: reģistrācija, pieteikšanās, profila labošana, paroles maiņa, valodas maiņa (LV/EN/RU), konta dzēšana.
- Abonementu pārvaldība: abonementu pievienošana, skatīšana, rediģēšana, dzēšana, filtrēšana un meklēšana.
- Maksājumi: abonementa apmaksa no lietotāja konta bilances, maksājuma statusa reģistrēšana (veiksmīgs/neveiksmīgs), aktivizācijas datuma atjaunošana pēc apmaksas.
- Maksājumu vēsture: detalizēts saraksts ar summām, datumiem un statusiem.
- Paziņojumi: automātiska paziņojumu ģenerēšana par abonementiem, kuri beidzas drīz vai jau beigušies.
- Karšu pārvaldība: kartes pievienošana, apskate un dzēšana.
- Mēneša kopsumma: lietotāja mēneša izdevumu aprēķins un saglabāšana.

## Mērķauditorija

- Privātpersonas, kurām ir vairāki regulāri abonementi (piem., mūzika, video, mākoņpakalpojumi, programmatūras rīki).
- Studenti un jaunie profesionāļi, kuri vēlas kontrolēt ikmēneša izdevumus.
- Lietotāji, kuri dod priekšroku vienkāršai komandrindas lietotnei bez sarežģītas grafiskās saskarnes.

## Konkurentu analīze

Tipiski tirgus risinājumi (piem., Rocket Money, Monarch Money, PocketGuard) bieži fokusējas uz plašu personīgo finanšu pārvaldību vai konkrētiem reģioniem.

Pitahui PayNest atšķiras ar:
- fokusu tieši uz abonementu ciklu (abonements -> maksājums -> paziņojums),
- vienkāršu termināļa saskarni,
- vieglu izstrādi un testēšanu akadēmiskā/prototipa vidē,
- daudzvalodu atbalstu lietotāja interfeisā.

## Sistēmas struktūra

Sistēma ir veidota no šādiem slāņiem:

- Lietotāja saskarne (CLI): izvēlnes un lietotāja darbību plūsmas klasē `Main`.
- Domēna modeļi: `User`, `Subscription`, `Payment`, `Card`, `BankAccount`, `Notification`.
- Datu piekļuves slānis (DAO): `UserDAO`, `SubscriptionDAO`, `PaymentDAO`, `NotificationDAO`, `CardDAO`, `BankAccountDAO`, `PaymentSummaryDAO`.
- Datubāzes inicializācija: `DBSetup` izveido tabulas un sākotnējos datus.
- Savienojums ar DB: `DBConnection` pārvalda SQLite pieslēgumu.

## Datu struktūra

Galvenās SQLite tabulas:

- `Lietotajs`: lietotāja identitāte, kontaktdati, parole, valoda.
- `Abonements`: abonementa nosaukums, veids, cena, ilgums, aktivizācijas datums, saite uz lietotāju.
- `Maksajums`: saite uz abonementu un lietotāju, summa, datums/laiks, statuss.
- `Pazinojums`: paziņojuma teksts, izveides datums, dienas līdz termiņam, saites uz lietotāju un abonementu.
- `Kartes`: lietotāja kartes informācija.
- `Bankas_konts`: lietotāja konta bilance un saite uz karti.
- `Kopejas_izmaksas`: lietotāja mēneša kopējie izdevumi.

## Izmantotās tehnoloģijas

- Java 21
- Maven
- SQLite (`sqlite-jdbc`)
- VS Code (izstrādes vide)
- Git/GitHub (versiju kontrole)

## Komanda un lietotāju instrukcija

### Komanda
- Timurs Sibiļovs
- Ēriks Vansovičs
- Anželika Sičova

### Lietotāju instrukcija

1. Atveriet projektu un pārliecinieties, ka ir pieejams JDK 21.
2. Komandrindā projekta mapē izpildiet:

```bash
mvn clean package
```

3. Palaidiet lietotni no IDE, izmantojot galveno klasi `pitahui.paynest.Main`.
4. Galvenajā izvēlnē izvēlieties:
- `1` lai reģistrētu jaunu kontu,
- `2` lai pieteiktos,
- `3` lai dzēstu kontu,
- `4` lai izietu.
5. Pēc pieteikšanās lietotāja izvēlnē varēsiet:
- pārvaldīt abonementus,
- mainīt konta iestatījumus,
- veikt maksājumu,
- apskatīt maksājumu vēsturi,
- pārvaldīt paziņojumus,
- pārvaldīt kartes,
- apskatīt mēneša kopējos izdevumus.

Ieteikums testēšanai: reģistrējiet jaunu lietotāju, pievienojiet abonementu, veiciet maksājumu un pēc tam pārbaudiet maksājumu vēsturi un paziņojumu sadaļu.