# Distributed system

## Требования к системе:
Необходимо разработать систему с кодовым именем CrackHash, которая будет распределенно взламывать хэши. В основе её работы лежит простой перебор словаря, созданного на основе указанного алфавита (brute-force метод).

В общих чертах, система должна функционировать следующим образом:

1. Существует менеджер, который принимает запросы от пользователей. Запрос включает в себя MD-5 хэш слова и максимальную длину слова.
2. Менеджер обрабатывает запрос, создавая задачи для перебора слов из заданного алфавита в соответствии с количеством вычислительных узлов (воркеров). Затем он отправляет эти задачи на выполнение воркерам.
3. Каждый воркер принимает задачу, перебирает слова в заданном диапазоне и вычисляет их хэш. При обнаружении слова с совпадающим хэшем он передает результаты обратно менеджеру через очередь.

Также следует разработать графический интерфейс для системы, позволяющий отправлять запросы на взлом менеджеру и отслеживать процесс взлома.

## Основные требования к отказоустойчивости:
1. Гарантировать сохранность данных при сбое работы менеджера:
   - Для этого требуется сохранение информации о текущих запросах в базе данных.
   - Также, необходимо организовать взаимодействие между воркерами и менеджером через RabbitMQ.
   - При недоступности менеджера, сообщения должны сохраняться в очереди до момента восстановления его работы.

2. Обеспечить частичную устойчивость базы данных:
   - База данных должна быть устойчивой к отказам. Для этого необходимо реализовать простое реплицирование для MongoDB.
   - Минимальная рабочая схема включает одну primary ноду и две secondary.
   - Клиенту следует сообщать, что задача принята только после успешного сохранения и репликации в базе данных.

3. Гарантировать сохранность данных при сбое работы воркера(-ов):
   - В docker-compose должно быть размещено минимум два воркера.
   - Взаимодействие между менеджером и воркерами должно осуществляться через отдельную очередь RabbitMQ, используя direct exchange.
   - Если воркер "падает" в процессе выполнения задачи, задача должна быть переназначена другому воркеру с корректной обработкой acknowledgment.
   - В случае отсутствия доступных воркеров на момент создания задач, сообщения должны ожидать их появления в очереди, прежде чем быть отправленными на выполнение.

4. Гарантировать сохранность данных при сбое работы очереди:
   - Если менеджер не может отправить задачи в очередь, он должен сохранить их в базе данных до восстановления доступности очереди, после чего повторно отправить накопившиеся задачи.
   - Очередь не должна потерять сообщения при рестарте или падении из-за ошибки. Для этого все сообщения должны быть персистентными при отправке.
  
# Стек
- Java 17
- Gradle
- Spring Boot
- RabbitMQ
- MongoDB
- Docker API
- Flutter

## Схема:
![DS3](https://github.com/oleg-barabulka/distributed-system/assets/114875626/46576065-6786-4943-acff-5297bc9045e8)

## Запуск:

### Сборка воркера из коневой папки проекта:
    cd Distributed-Systems-master
    cd worker
    gradle build

### Сборка менджера из коневой папки проекта:
    cd Distributed-Systems-master
    cd manager
    gradle build

### Запуск системы:
    cd Distributed-Systems-master
    docker-compose up


### Запуск GUI:
    cd flutter_application
    flutter build
    flutter run
    docker-compose up

# GUI
Необходимо ввессти в поле Hash - хэш строки, которую мы хотим найти. В поле HashLength ввести длинну искомой строки. Кнопка Send request отправляет запрос компоненту manager для дальнейшей обработки. Кнопка Get result позволяет просмотреть текущий статус запрощенной задачи.

