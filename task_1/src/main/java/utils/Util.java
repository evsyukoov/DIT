package utils;

import person.Person;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {
    public static List<Person> fillPersonListFromIO() {
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        Person person = null;
        List<Person> persons = new ArrayList<>();
        System.out.println("Вводите имена и фамилии персон для заполнения списка, для окончания ввода нажмите CMD + D");
        System.out.printf("Введите имя %d персоны\n", 1);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                continue;
            }
            if (i % 2 == 0) {
                person = new Person();
                person.setFirstName(input);
                System.out.printf("Введите фамилию %d персоны\n", persons.size() + 1);
            } else {
                person.setLastName(input);
                persons.add(person);
                System.out.printf("Введите имя %d персоны\n", persons.size() + 1);
            }
            i++;
        }
        if (person != null && person.getLastName() == null) {
            System.out.println("WARNING: для последней персоны вы задали только имя и персона не попадет в список");
        }
        return persons;
    }

    public static void writeToFile(String fileName, List<Person> persons) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            for (Person person : persons) {
                writer.write(person.toString() + "\n");
            }
        }
    }
}
