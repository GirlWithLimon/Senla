package org.example.bookstore_app.view;

import org.example.annotation.Inject;
import org.example.annotation.Component;
import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class MenuController {
    private Navigator navigator;
    private Scanner scanner;

    public MenuController() {
        this.scanner = new Scanner(System.in);
    }

    @Inject
    public MenuController(Navigator navigator) {
        this.navigator = navigator;
        this.scanner = new Scanner(System.in);
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void run() {
        System.out.println("DEBUG: Scanner object = " + scanner);
        System.out.println("DEBUG: Navigator = " + navigator);
        if (navigator == null) {
            System.err.println("ОШИБКА: Navigator не установлен!");
            return;
        }

        System.out.println("Добро пожаловать в систему управления книжным магазином!");

        while (true) {
            try {
                navigator.printMenu();
                System.out.print("Выберите пункт меню: ");

                int choice = scanner.nextInt();
                scanner.nextLine();
                System.out.println("DEBUG: User selected option: " + choice);
                navigator.navigate(choice);
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода! Пожалуйста, введите ЧИСЛО.");
                scanner.nextLine();
            } catch (NullPointerException e) {
                System.err.println("Ошибка: меню не инициализировано!");
                System.err.println("Сообщение: " + e.getMessage());
                return;
            } catch (Exception e) {
                System.out.println("Произошла ошибка при выполнении команды: " + e.getClass().getSimpleName());
                System.out.println("Сообщение: " + e.getMessage());
                e.printStackTrace();
                scanner.nextLine();
            }
        }
    }
}