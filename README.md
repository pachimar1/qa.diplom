# Дипломный проект по профессии «Тестировщик ПО»

## Основная задача: 
Автоматизация тестирования сервиса по покупке тура по определённой цене двумя способами: по дебетовой карте и в кредит, взаимодействующего с СУБД и API Банка. [Описание задачи]([https://github.com/netology-code/qa-diploma](https://github.com/pachimar1/task-qa-diploma)).

## Что было сделано:
1. Настроено окружение для запуска симулятора и SUT с поддержкой СУБД MySQL и PostgreSQL. 
2. Составлено [Планирование автоматизации](https://github.com/pachimar1/qa.diplom/blob/main/documents/Plan.md). В нем описаны по 23 сценариев для каждой формы заявки.
3. Написаны авто-тесты для всех сценариев, прописанных в плане. 
4. Отчётные документы по итогам тестирования (не реализовано, перейду к этому шагу после проверки автотестов)
5. Отчётные документы по итогам автоматизации (не реализовано, после проверки предыдущего шага)

## Запуск авто-тестов

### Перед началом работы
На компьютере пользователя должна быть установлена:
   - Git Bash
   - Intellij IDEA
   - Docker Dekstop
     

### Шаги для запуска авто-тестов из консоли
1. Склонировать репозиторий: `git clone https://github.com/pachimar1/qa.diplom`
4. Сменить папку: `cd qa.diplom`
5. Запустить контейнеры (СУБД MySQL, PostgreSQL, gate-simulator) через терминал Intellij IDEA: `docker-compose up -d`
6. Запустить SUT
   1. Команда для запуска с поддержкой СУБД MySQL: `java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar`
   2. Команда для запуска с поддержкой СУБД PostgreSQL: `java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar`
7. В браузере сервис будет доступен по адресу [http://localhost:8080/](http://localhost:8080/) 
8. Запустить авто-тесты
   1. Команда для запуска тестов с MySQL: `gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"`
   2. Команда для запуска тестов с PostgreSQL: `gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"`
9. После завершения прогона авто-тестов сгенерировать отчёт командой `gradlew allureServe`. Отчёт откроется в браузере.
10. Для завершения работы allureServe выполнить команду `Ctrl + С`, далее `Y`
11. Остановить все контейнеры командой `docker-compose down`
12. Остановить wsl и работу Docker Dekstop `wsl --shutdown`     
