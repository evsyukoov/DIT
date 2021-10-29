package forthTask;

import person.Person;
import utils.Util;

import java.util.List;

/**
 * Ввод, сортировка, вывод списка объектов Person
 */
public class Main {
    public static void main(String[] args) {
        List<Person> persons = Util.fillPersonListFromIO();
        persons.stream()
                .sorted((p1, p2) -> p1.getLastName().compareTo(p2.getLastName()))
                .forEach(System.out::println);
    }
}
