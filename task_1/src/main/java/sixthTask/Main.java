package sixthTask;

import person.Person;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private interface Exec {
        void exec(List<Person> data) throws Exception;
    }

    private static class MenuItem {
        private String name;

        private Exec exec;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Exec getExec() {
            return exec;
        }

        public void setExec(Exec exec) {
            this.exec = exec;
        }
    }

    private static class Menu {
        private List<MenuItem> items;

        private Scanner scanner;

        public Menu(Scanner scanner) {
            MenuItem read = new MenuItem();
            read.setName("1.Read from file");
            read.setExec((data) -> {
                InputStream inputStream = getClass()
                        .getClassLoader().getResourceAsStream("input.txt");
                if (inputStream == null) {
                    throw new Exception("Файл загрузки не найден");
                }
                Scanner fileScan = new Scanner(inputStream);
                while (fileScan.hasNext()) {
                    String[] arr = fileScan.next().split(";");
                    if (arr.length != 3) {
                        throw new RuntimeException("Не удалось распарсить файл с персонами. Загрузка прервана");
                    }
                    data.add(new Person(arr[1], arr[2]));
                }
            });

            MenuItem clear = new MenuItem();
            clear.setName("2.Clear data in memory");
            clear.setExec((data) -> data.clear());

            MenuItem show = new MenuItem();
            show.setName("3.Show uploaded data");
            show.setExec((data) -> {
                if (data.isEmpty()) {
                    System.out.println("Список пользователей пуст!");
                } else {
                    data.forEach(System.out::println);
                }
            });

            items = new ArrayList<>();
            items.add(read);
            items.add(clear);
            items.add(show);
            this.scanner = scanner;
        }

        private String getMenuItemsName() {
            return this.items.stream()
                    .map(MenuItem::getName)
                    .collect(Collectors.joining("\n"));
        }

        public void listener() {
            System.out.println(getMenuItemsName());
            List<Person> data = new ArrayList<>();
            while (scanner.hasNext()) {
                try {
                    int input = Integer.parseInt(scanner.next());
                    MenuItem item = this.items.stream()
                            .filter(i -> i.getName().startsWith(String.valueOf(input)))
                            .findFirst()
                            .orElse(null);
                    if (item == null) {
                        System.out.println("Неправильный пользовательский ввод");
                        continue;
                    }
                    item.getExec().exec(data);
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage() == null ? e.getCause()
                            : e.getMessage());
                    continue;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return;
                }
                System.out.println("Операция выполнена успешно");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner);
        menu.listener();

    }
}
