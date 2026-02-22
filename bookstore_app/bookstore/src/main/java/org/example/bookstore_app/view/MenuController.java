package org.example.bookstore_app.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class MenuController {
    private Navigator navigator;
    private Scanner scanner;

    public MenuController() {
        this.scanner = new Scanner(System.in);
    }
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    public MenuController(Navigator navigator) {
        this.navigator = navigator;
        this.scanner = new Scanner(System.in);
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void run() {
        logger.debug("DEBUG: Scanner object = " + scanner);
        logger.debug("DEBUG: Navigator = " + navigator);
        if (navigator == null) {
            logger.error("ОШИБКА: Navigator не установлен!");
            return;
        }

        System.out.println("Добро пожаловать в систему управления "
                           +"книжным магазином!");

        while (true) {
            try {
                navigator.printMenu();
                System.out.print("Выберите пункт меню: ");

                int choice = scanner.nextInt();
                scanner.nextLine();
                logger.info("Пользователь выбрал: {}", choice);
                navigator.navigate(choice);
            } catch (InputMismatchException e) {
                System.out.print("Ошибка ввода! Пожалуйста, введите ЧИСЛО.");
                scanner.nextLine();
            } catch (NullPointerException e) {
                logger.error("Ошибка: меню не инициализировано! Сообщение: {}",
                              e.getMessage());
                return;
            } catch (Exception e) {
                logger.error("Произошла ошибка при выполнении команды: {} "
                             +"Сообщение: {}", e.getClass().getSimpleName(),
                             e.getMessage());
                scanner.nextLine();
            }
        }
    }
}
