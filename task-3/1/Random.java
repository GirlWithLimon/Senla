public class Random{
    public static void main(String[] args) {
        int chislo = (new java.util.Random()).nextInt(899)+100;
        System.out.println("Случайное трехзначное число: " + chislo);
        int sym = chislo/100+ chislo%100/10 +chislo%10;
        System.out.println("Сумма цифр числа: " + sym);
    }
}