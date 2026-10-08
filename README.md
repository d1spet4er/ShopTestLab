# ShopTestLab

Pet-проект для портфолио Java / Automation QA.

Небольшой backend интернет-магазина: REST API на Spring Boot с PostgreSQL, JWT-аутентификацией, ролями, корзиной и заказами. Проект дополнен API-тестами, Docker Compose и GitHub Actions CI.

## Стек

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Security
- JWT (JJWT)
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Maven
- JUnit 5
- REST Assured
- MockMvc / Spring Security Test
- Swagger / OpenAPI
- Docker / Docker Compose
- GitHub Actions

## Что реализовано

### Авторизация и безопасность

- регистрация и логин через REST API;
- пароли хранятся в виде BCrypt-хеша;
- stateless-аутентификация через JWT;
- роли `USER` и `ADMIN`;
- обычный пользователь не может создавать, изменять или удалять товары;
- защищённые endpoint'ы возвращают 401 без валидного JWT;
- проверка доступа к защищённым ресурсам;
- единый формат ошибок API.

### Товары

- CRUD товаров;
- пагинация;
- поиск по названию;
- фильтрация по категории;
- валидация входных данных;
- стартовые данные;
- публичное чтение товаров;
- изменение товаров только для ADMIN.

### Корзина

- добавление товара;
- изменение количества через повторное добавление;
- удаление позиции;
- очистка корзины;
- расчёт суммы;
- проверка наличия товара на складе;
- корзина привязана к текущему пользователю.

### Заказы

- создание заказа из корзины;
- сохранение снимка названия и цены товара в заказе;
- автоматический расчёт итоговой суммы;
- списание товара со склада;
- просмотр списка своих заказов;
- просмотр конкретного своего заказа;
- пользователь не получает чужие заказы;
- блокировка товара на время операции со складом через pessimistic lock.

### Тестирование и CI

- unit-тесты сервисов;
- REST Assured API-тесты;
- MockMvc security/integration-тесты;
- проверка JWT;
- проверка ролей USER/ADMIN;
- проверка валидации;
- GitHub Actions запускает тесты на Java 21 и PostgreSQL 17.

## API

Base URL:

`http://localhost:8080/api/v1`

### Auth

- `POST /auth/register`
- `POST /auth/login`

### Users

- `GET /users/me`

### Products

- `GET /products`
- `GET /products/{id}`
- `POST /products` — ADMIN
- `PUT /products/{id}` — ADMIN
- `DELETE /products/{id}` — ADMIN

### Cart

- `GET /cart`
- `POST /cart/items`
- `DELETE /cart/items/{id}`
- `DELETE /cart`

### Orders

- `POST /orders`
- `GET /orders`
- `GET /orders/{id}`

Swagger:

`http://localhost:8080/swagger-ui.html`

OpenAPI JSON:

`http://localhost:8080/v3/api-docs`

## Локальный запуск

Нужны Java 21, Maven и Docker.

### Вариант 1 — PostgreSQL через Docker

Запустить базу:

```bash
docker compose up -d postgres
```

Запустить приложение:

```bash
mvn spring-boot:run
```

### Вариант 2 — всё через Docker Compose

```bash
docker compose up --build
```

После запуска API будет доступен на:

`http://localhost:8080`

### Тесты

```bash
mvn clean test
```

## Переменные окружения

Приложение поддерживает:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`

Для реального окружения нужно задавать свой длинный случайный `JWT_SECRET`, а не использовать значение из примеров.

## Архитектура

Основной поток:

`Controller → Service → Repository → PostgreSQL`

DTO используются на границе API, а бизнес-логика находится в сервисах.

Для корзины и заказов данные пользователя выбираются по email из текущей JWT-аутентификации, поэтому клиент не передаёт чужой `userId`.

## CI

GitHub Actions автоматически:

1. поднимает PostgreSQL 17;
2. устанавливает Java 21;
3. запускает `mvn test`;
4. завершает workflow только при успешном прохождении тестов.

## Идеи для дальнейшего развития

- Flyway/Liquibase для миграций;
- Testcontainers вместо отдельной H2-конфигурации интеграционных тестов;
- Allure-отчёты;
- отдельный API Client для автотестов;
- больше позитивных и негативных сценариев;
- GitHub Actions с отдельными этапами build/test;
- UI-тесты;
- обработка оплаты и переходов статусов заказа.
