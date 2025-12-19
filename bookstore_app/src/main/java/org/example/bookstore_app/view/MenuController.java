package org.example.bookstore_app.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuController {
    private final Navigator navigator;
    private final Scanner scanner;

    public MenuController(MenuBuilder menuBuilder) {
        this.navigator = new Navigator(menuBuilder.getRootMenu());
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("DEBUG: Scanner object = " + scanner);
        System.out.println("Добро пожаловать в систему управления книжным магазином!");

        while (true) {
            navigator.printMenu();
            System.out.print("Выберите пункт меню: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                System.out.println("DEBUG: User selected option: " + choice);
                navigator.navigate(choice);
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода! Пожалуйста, введите ЧИСЛО.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Произошла ошибка при выполнении команды: " + e.getClass().getSimpleName());
                System.out.println("Сообщение: " + e.getMessage());
                e.printStackTrace();
                scanner.nextLine();
            }
        }
    }
}