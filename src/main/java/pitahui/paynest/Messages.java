package pitahui.paynest;

import java.util.Locale;

public class Messages {
    private static String lang = "LV";

    public static void setLanguage(String code) {
        // funkcija setLanguage pieņem String tipa vērtību code un atgriež void tipa vērtību
        if (code == null) return;
        lang = code.toUpperCase(Locale.ROOT);
    }

    public static String t(String key) {
        // funkcija t pieņem String tipa vērtību key un atgriež String tipa vērtību (tulkots ziņojums)
        switch (lang) {
            case "EN": return tEn(key);
            case "RU": return tRu(key);
            default: return tLv(key);
        }
    }

    private static String tEn(String k) {
        // funkcija tEn pieņem String tipa vērtību k un atgriež String tipa vērtību (angļu tulkojums)
        return switch (k) {
            case "app.title" -> "Pitahui Paynest - Terminal Interface";
            case "menu.main.title" -> "Main menu\n";
            case "menu.main.register" -> " 1) Register";
            case "menu.main.login" -> " 2) Login";
            case "menu.main.delete" -> " 3) Delete account";
            case "menu.main.exit" -> " 4) Exit";
            case "exiting" -> "Exiting. Goodbye.";
            case "unknown.choice" -> "Unknown choice — please enter 1, 2, 3 or 4";
            case "create.title" -> "Create new account\n";
            case "account.created" -> "\nAccount created for ";
            case "initial.balance" -> "Initial bank account balance: ";
            case "error.create" -> "Error creating account: ";
            case "login.title" -> "Login\n";
            case "login.success" -> "\nLogin success: ";
            case "login.failed" -> "\nLogin failed: wrong phone or password";
            case "error.login" -> "Error during login: ";
            case "delete.title" -> "Delete account\n";
            case "account.deleted" -> "\nAccount deleted";
            case "no.match" -> "\nNo matching account";
            case "user.menu.title" -> "User menu\n";
            case "user.menu.subs" -> " 1) Subscriptions";
            case "user.menu.settings" -> " 2) Account settings";
            case "user.menu.pay" -> " 3) Pay for subscription";
            case "user.menu.history" -> " 4) Payment history";
            case "user.menu.notifications" -> " 5) Notifications";
            case "user.menu.managecards" -> " 6) Manage cards";
            case "user.menu.monthly" -> " 7) Monthly total payments";
            case "user.menu.logout" -> " 8) Logout";
            case "logged.out" -> "Logged out";
            default -> k;
        };
    }

    private static String tLv(String k) {
        // funkcija tLv pieņem String tipa vērtību k un atgriež String tipa vērtību (latviešu tulkojums)
        return switch (k) {
            case "app.title" -> "Pitahui Paynest - Termināļa saskarne";
            case "menu.main.title" -> "Galvenā izvēlne\n";
            case "menu.main.register" -> " 1) Reģistrēties";
            case "menu.main.login" -> " 2) Pieslēgties";
            case "menu.main.delete" -> " 3) Dzēst kontu";
            case "menu.main.exit" -> " 4) Iziet";
            case "exiting" -> "Iziet. Uz redzēšanos.";
            case "unknown.choice" -> "Nezināma izvēle — lūdzu ievadiet 1, 2, 3 vai 4";
            case "create.title" -> "Izveidot jaunu kontu\n";
            case "account.created" -> "\nKonts izveidots priekš ";
            case "initial.balance" -> "Sākotnējais bankas konta atlikums: ";
            case "error.create" -> "Kļūda, izveidojot kontu: ";
            case "login.title" -> "Pieteikšanās\n";
            case "login.success" -> "\nPieteikšanās veiksmīga: ";
            case "login.failed" -> "\nNeizdevās pieteikties: nepareizs telefons vai parole";
            case "error.login" -> "Kļūda pieteikumā: ";
            case "delete.title" -> "Dzēst kontu\n";
            case "account.deleted" -> "\nKonts dzēsts";
            case "no.match" -> "\nNav atbilstoša konta";
            case "user.menu.title" -> "Lietotāja izvēlne\n";
            case "user.menu.subs" -> " 1) Abonementi";
            case "user.menu.settings" -> " 2) Konta iestatījumi";
            case "user.menu.pay" -> " 3) Apmaksāt abonementu";
            case "user.menu.history" -> " 4) Maksājumu vēsture";
            case "user.menu.notifications" -> " 5) Paziņojumi";
            case "user.menu.managecards" -> " 6) Pārvaldīt kartes";
            case "user.menu.monthly" -> " 7) Mēneša kopējie izdevumi";
            case "user.menu.logout" -> " 8) Atslēgties";
            case "logged.out" -> "Atslēdzies";
            default -> k;
        };
    }

    private static String tRu(String k) {
        // funkcija tRu pieņem String tipa vērtību k un atgriež String tipa vērtību (krievu tulkojums)
        return switch (k) {
            case "app.title" -> "Pitahui Paynest - Терминальный интерфейс";
            case "menu.main.title" -> "Главное меню\n";
            case "menu.main.register" -> " 1) Регистрация";
            case "menu.main.login" -> " 2) Вход";
            case "menu.main.delete" -> " 3) Удалить аккаунт";
            case "menu.main.exit" -> " 4) Выход";
            case "exiting" -> "Выход. До свидания.";
            case "unknown.choice" -> "Неизвестный выбор — введите 1, 2, 3 или 4";
            case "create.title" -> "Создать новый аккаунт\n";
            case "account.created" -> "\nАккаунт создан для ";
            case "initial.balance" -> "Начальный баланс банковского счёта: ";
            case "error.create" -> "Ошибка при создании аккаунта: ";
            case "login.title" -> "Вход\n";
            case "login.success" -> "\nВход выполнен: ";
            case "login.failed" -> "\nОшибка входа: неверный телефон или пароль";
            case "error.login" -> "Ошибка при входе: ";
            case "delete.title" -> "Удалить аккаунт\n";
            case "account.deleted" -> "\nАккаунт удалён";
            case "no.match" -> "\nНет подходящего аккаунта";
            case "user.menu.title" -> "Меню пользователя\n";
            case "user.menu.subs" -> " 1) Подписки";
            case "user.menu.settings" -> " 2) Настройки аккаунта";
            case "user.menu.pay" -> " 3) Оплатить подписку";
            case "user.menu.history" -> " 4) История платежей";
            case "user.menu.notifications" -> " 5) Уведомления";
            case "user.menu.managecards" -> " 6) Управление картами";
            case "user.menu.monthly" -> " 7) Месячные расходы";
            case "user.menu.logout" -> " 8) Выйти";
            case "logged.out" -> "Вы вышли";
            default -> k;
        };
    }
}
