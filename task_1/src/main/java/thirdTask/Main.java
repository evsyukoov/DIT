package thirdTask;

import person.Person;
import utils.Util;

import java.util.List;

/**
 * Заполнение списка объектов Person,
 * реализовав интерактивный ввод данных,
 * используя входной поток System.in
 */
public class Main {
    public static void main(String[] args) {
        List<Person> persons = Util.fillPersonListFromIO();
        System.out.println("Ваш список персон: ");
        persons.forEach(System.out::println);
    }
}
