# Краткое описание приложения

## Сборка и запуск

    1) Использовать IDE, создав проект в Task_6
    2) Собрать и запустить вручную: 
        В папке с проектом:
            - mvn package
            - java -jar target/Task_6-1.0-SNAPSHOT.jar
    Адрес localhost:8080
## Методы

    1) Создание счетчика:
        GET /service/v1/createCounter?name=counter_name
        Коды возврата:
            200 - успех
            400 - BAD_REQUEST, такой счетчик уже есть

    2) Инкремент значения
        GET /service/v1/incrementCounter?name=counter_name
        Коды возврата:
            200 - успех
            400 - BAD_REQUEST, нет счетчика с таким именем
    
    3) Получить значение счетчика
        GET /service/v1/getCounterValue?name=counter_name
        Коды возврата:
            200 - успех
            400 - BAD_REQUEST, нет счетчика с таким именем

    4) Удаление счетчика по имени
        GET /service/v1/removeCounter?name=counter_name
        Коды возврата:
            200 - успех
            400 - BAD_REQUEST, нет счетчика с таким именем

    5) Получить значение всех счетчиков
        GET /service/v1/getCountersSum

    6) Получить список имен счетчиков
        GET /service/v1/getCountersNames

