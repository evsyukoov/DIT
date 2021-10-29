import service.XmlService;

import javax.xml.xpath.XPathExpressionException;
import java.math.BigDecimal;
import java.util.Scanner;

public class Manager {

    private static final String NUMBER_PATTERN = "[0-9]+";

    private static final String SALARY_PATTERN ="[0-9]+\\.?[0-9]+";

    private static final String ALPHA_PATTERN ="[a-zA-Zа-яА-Я]+";

    private XmlService xmlService;

    public Manager() throws Exception {
        this.xmlService = new XmlService();
    }

    private void printMainMenu() {
        System.out.println("1.Поиск сотрудника по ID");
        System.out.println("2.Поиск сотрудника по фамилии");
        System.out.println("3.Вывод списка сотрудников");
        System.out.println("4.Редактирование зарплаты сотрудника");
        System.out.println("5.Добавление нового сотрудника");
        System.out.println("6.Выход");
    }

    public void mainMenuListener() throws Exception {
        printMainMenu();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String task = scanner.next();
            if (task.equals("1")) {
                System.out.println("Введите id сотрудника");
                    String input = scanner.next();
                    if (!input.matches(NUMBER_PATTERN)) {
                        System.out.println("Неккоректное значение ID");
                    } else {
                        System.out.println(xmlService.getEmployeeById(Integer.parseInt(input)));
                }
            } else if (task.equals("2")) {
                System.out.println("Введите фамилию сотрудника");
                String input = scanner.next();
                if (!input.matches(ALPHA_PATTERN)) {
                    System.out.println("Неккоректное значение фамилии");
                } else {
                    xmlService.getEmployeesByName(input)
                            .forEach(System.out::println);
                }

            } else if (task.equals("3")) {
                xmlService.getEmployees()
                        .forEach(System.out::println);
            } else if (task.equals("4")) {
                System.out.println("Введите ID сотрудника");
                String input = scanner.next();
                if (!input.matches(NUMBER_PATTERN)) {
                    System.out.println("Неккоректное значение ID");
                } else {
                    System.out.println("Введите зарплату сотрудника");
                    String newSalary = scanner.next();
                    if (!newSalary.matches(SALARY_PATTERN)) {
                        System.out.println("Неверное значение зарплаты.");
                    } else {
                        int id = Integer.parseInt(input);
                        xmlService.editSalary(id, new BigDecimal(newSalary));
                        System.out.printf("Зарплата у человека с ID=%d успешно отредактирована\n", id);
                    }
                }
            } else if (task.equals("5")) {
                System.out.println("Введите имя сотрудника");
                String name = scanner.next();
                System.out.println("Введите фамилию сотрудника");
                String surname = scanner.next();
                System.out.println("Введите должность сотрудника");
                String position = scanner.next();
                System.out.println("Введите зарплату");
                String salary = scanner.next();
                if (!name.matches(ALPHA_PATTERN) || !surname.matches(ALPHA_PATTERN)
                        || !position.matches(ALPHA_PATTERN) || !salary.matches(SALARY_PATTERN)) {
                    System.out.println("Неверный ввод");
                } else {
                    int id = xmlService.addNewEmployee(name, surname, position, new BigDecimal(salary));
                    System.out.printf("%d успешно добавен в xml\n", id);
                }

            } else if (task.equals("6")) {
                break;
            }
            printMainMenu();
        }
    }
}
