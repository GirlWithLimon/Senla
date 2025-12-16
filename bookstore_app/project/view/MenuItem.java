package bookstore_app.project.view;

public class MenuItem {
    private final  String title;
    private IAction action;
    private Menu nextMenu;
    
    public MenuItem(String title, IAction action) {
        this.title = title;
        this.action = action;
    }
    
    public MenuItem(String title, Menu nextMenu) {
        this.title = title;
        this.nextMenu = nextMenu;
    }
    
    public void doAction() {
        if (action != null) {
            action.execute();
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public Menu getSubMenu() {
        return nextMenu;
    }
    
    public boolean hasSubMenu() {
        return nextMenu != null;
    }
}