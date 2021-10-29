package secondTask;

import person.Person;

import java.util.Scanner;

/**
 * Заполнение полей класса,реализовав интерактивный ввод данных,
 * используя входной поток System.in
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Введите имя");
        Scanner scanner = new Scanner(System.in);
        String firstName = scanner.nextLine();
        System.out.println("Введите фамилию");
        String secondName = scanner.nextLine();
        System.out.printf("%s создан", new Person(firstName, secondName));

    }
}
