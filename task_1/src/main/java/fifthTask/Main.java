package fifthTask;

import person.Person;
import utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Реализация интерактивного меню для работы со списком объектов Person
 * Добавлен пункт меню Show
 */
public class Main {
    private static void printInfo() {
        System.out.println("1.Add");
        System.out.println("2.Show");
        System.out.println("3.Exit");
        System.out.println("4.Show sorted unique");
        System.out.println("5.Save to file");
    }

    public static void main(String[] args) {
        System.out.println("Menu:");
        printInfo();
        Scanner scanner = new Scanner(System.in);
        List<Person> persons = new ArrayList<>();
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                continue;
            }
            if (input.equals("1")) {
                System.out.println("Введите имя и фамилию через пробел");
                String name = scanner.nextLine();
                String[] arr = name.split(" ");
                if (arr.length == 2) {
                    persons.add(new Person(arr[0], arr[1]));
                    System.out.println("person успешно добавлен");
                } else {
                    System.out.println("Ошибка! Нужно ввести имя и фамилию через пробел");
                }
            } else if (input.equals("2")) {
                persons.forEach(System.out::println);
            } else if (input.equals("3")) {
                break;
            } else if (input.equals("4")) {
                HashMap<String, Person> uniqueMap = new HashMap<>();
                persons.stream()
                        .sorted(Comparator.comparing(Person::getLastName))
                        .forEach(p -> uniqueMap.put(p.getLastName(), p));
                uniqueMap.forEach((k, v) -> System.out.println(v));
            } else if (input.equals("5")) {
                try {
                    Util.writeToFile("output.txt", persons);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Неизвестная команда. Список доступных комманд:");
            }
            printInfo();
        }
    }
}
