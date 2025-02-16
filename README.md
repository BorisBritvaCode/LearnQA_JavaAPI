LearnQA_JavaAPI - это фреймворк, для тестирования REST API на Java, написанный в рамках курса по автоматизации апи.
Объектом тестирования является открытое апи, предоставляющее методы по взаимодействию с сущностью пользователя. В основном, это CRUD операции, такие как регистрация, авторизация, редактирование и т.д. 

[Документация по API](https://playground.learnqa.ru/api/map) - вкладка Open user Api

## Стек
- [Java](https://www.java.com/)
- [JUnit5](https://junit.org/junit5/)
- [REST Assured](https://rest-assured.io/)
- [Maven](https://maven.apache.org/)
- [Allure Report](https://allurereport.org)
- [Docker](https://www.docker.com/)

## Структура фреймворка

`src/test/java/lib/`

- `BaseTestCase` - базовый класс, от которого наследуются все классы автотестов, содержит переменные окружения и общие методы по получению данных из запросов
- `ApiCoreRequests` - содержит методы для отправки апи запросов
- `Assertions` - содержит методы для проверки данных полученных в ответах на запросы
- `DataGenerator` - содержит методы для генерации данных для тестов


`src/test/java/test/`

- `UserRegisterTest`
- `UserAuthTest`
- `UserGetTest`
- `UserEditTest`
- `UserDeleteTest`

В данных классах реализованы сами автотесты. Они разделены по фичам (регистрация, авторизация, редактирование пользователя и т.д.)

## Запуск тестов

- инструкция на примере MacOS / Linux
- для сборки билда необходим [Docker](https://docs.docker.com/install/), убедитесь, что он установлен и запущен на вашей ОС
- для просмотра отчета с результатами прогона тестов необходимо установить [Allure](https://allurereport.org/docs/install-for-macos/)


```sh
$ git clone https://github.com/BorisBritvaCode/LearnQA_JavaAPI.git  # скачайте репозиторий
$ cd LearnQA_JavaAPI  # перейдите в папку с проектом
$ docker-compose up --build  # сборка docker образа, запуск контейнера и прогон всех автотестов
```
- среднее время прогона (с учетом сборки): **~1-2,5мин**
- важно выполнять команду, находясь в директории с файлом `docker-compose.yml`
- для повторного прогона образ не обязательно собирать заново, можно не указывать ключ `--build`

для просмотра отчета после прогона тестов используйте команду:
```sh
$ allure serve allure-results/  # сгенерировать отчет по тестам в HTML и открыть в браузере
```
