# Краткая информация

## Запуск приложения

    1) Докер-образ
        - Собрать локально:
            docker build -t summersett/task7:v1 .
        - Выкачать с DockerHub
            docker pull summersett/task7:v1
    
    2) Запуск
        docker run -p 8080:8080 summersett/task7:v1

## Методы
    
    GET /service/v1/method
    
    Через браузер в окошке ввести логин: admin, пароль: admin, хедер отправится сам

    При отправке запроса через curl или альтернативы, нужно отправить хедер
    
    "Authorization: Basic YWRtaW46YWRtaW4="
    

        