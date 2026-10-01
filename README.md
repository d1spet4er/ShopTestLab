# ShopTestLab

Pet-проект для портфолио Java Automation QA.

## Стек
Java 21, Spring Boot, Maven, PostgreSQL, JPA/Hibernate, JUnit 5, REST Assured, Docker.

## Сейчас реализовано
- REST API товаров
- PostgreSQL
- валидация данных
- обработка 404
- стартовые данные
- API-тесты на REST Assured
- Docker Compose

## Запуск
Нужны Java 21, Maven и Docker.

```bash
docker compose up -d
mvn spring-boot:run
```

API: http://localhost:8080/api/products

Тесты:
```bash
mvn test
```

## Следующие этапы
- JWT авторизация
- корзина и заказы
- API Client
- позитивные/негативные сценарии
- Allure
- UI-тесты
- GitHub Actions CI
- Docker Compose для полного окружения
