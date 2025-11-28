package project.view;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String name;
    private final List<MenuItem> menuItems = new ArrayList<>();
    
    public Menu(String name) {
        this.name = name;
    }
    
    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }
    
    public String getName() {
        return name;
    }
    
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
    
    public void display() {
        System.out.println("\n=== " + name.toUpperCase() + " ===");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.println((i + 1) + ". " + menuItems.get(i).getTitle());
        }
        System.out.println("0. " + (name.equals("Главное меню") ? "Выход" : "Назад"));
    }
}