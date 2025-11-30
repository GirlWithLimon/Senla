package bookstore_app.view;

import java.util.Scanner;

public class MenuController {
    private final MenuBuilder builder;
    private final Navigator navigator;
    private final Scanner scanner;
    
    public MenuController() {
        this.builder = MenuBuilder.getInstance();
        this.navigator = new Navigator(builder.getRootMenu());
        this.scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("Добро пожаловать в систему управления книжным магазином!");
        
        while (true) {
            navigator.printMenu();
            System.out.print("Выберите пункт меню: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 
                navigator.navigate(choice);
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Пожалуйста, введите число.");
                scanner.nextLine(); 
            }
        }
    }
}