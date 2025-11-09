package first.model;
public enum OrderStatus {
    NEW("Новый"),
    IN_PROCESS("В обработке"),
    PARTIALLY_COMPLETED("Частично выполнен"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}