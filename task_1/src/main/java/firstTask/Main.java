package firstTask;

import person.Person;

/**
 * заполнение класса person.Person из аргументов командной строки
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Неправильное количество аргументов командной строки\n" +
                    "Должно быть 2 аргумента: первый - имя, второй - фамилия");
            return;
        }
        Person person = new Person(args[0], args[1]);
        System.out.printf("%s успешно создан!", person);
    }
}
