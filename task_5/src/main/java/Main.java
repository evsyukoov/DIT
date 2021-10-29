public class Main {
    public static void main(String[] args) {
        try {
            Manager manager = new Manager();
            manager.mainMenuListener();
        } catch (Exception e) {
            System.err.println("Некорректный XML файл");
            e.printStackTrace();
        }
    }
}
