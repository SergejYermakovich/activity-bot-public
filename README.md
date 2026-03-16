# 🎯 Activity Bot

Telegram бот и REST API для поиска и организации активностей в городе.

## 📋 Описание

Activity Bot помогает пользователям находить и организовывать интересные мероприятия:
- 🧩 Квесты и escape rooms
- ⚽ Футбольные матчи
- 🎪 Концерты и выставки
- 🔧 Мастер-классы и воркшопы
- 🎮 Настольные игры
- 🏃 Спортивные мероприятия

## 🛠️ Технологии

- **Java 21**
- **Spring Boot 3.2.3**
- **Telegram Bot API** 6.9.7.1
- **Spring Data JPA**
- **PostgreSQL / H2**
- **Liquibase** (миграции БД)
- **Lombok**
- **OpenAPI/Swagger** 2.3.0
- **Spring Validation**

## 🚀 Быстрый старт

### Требования

- Java 21+
- Maven 3.8+
- Telegram Bot Token (получить у @BotFather)

### Установка

1. **Клонируйте репозиторий:**
```bash
git clone https://github.com/SergejYermakovich/activity-bot-public.git
cd activity-bot-public
```

2. **Настройте переменные окружения:**
```bash
export TELEGRAM_BOT_TOKEN=your-bot-token-here
export TELEGRAM_BOT_USERNAME=YourBotName
```

3. **Запустите приложение:**
```bash
# Dev режим (H2 база, swagger на /swagger-ui.html)
mvn spring-boot:run

# Prod режим (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

4. **Откройте Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

## 📖 Конфигурация

### Переменные окружения

| Переменная | Описание | Пример |
|------------|----------|--------|
| `TELEGRAM_BOT_TOKEN` | Токен Telegram бота | `123456:ABC-DEF...` |
| `TELEGRAM_BOT_USERNAME` | Username бота | `ActivityBot` |
| `DB_HOST` | Хост PostgreSQL (prod) | `localhost` |
| `DB_PORT` | Порт PostgreSQL (prod) | `5432` |
| `DB_NAME` | Имя базы данных (prod) | `activitybot` |
| `DB_USER` | Пользователь БД (prod) | `postgres` |
| `DB_PASSWORD` | Пароль БД (prod) | `secret` |

### Профили Spring

- **dev** (по умолчанию) - H2 in-memory база, H2 console на `/h2-console`
- **prod** - PostgreSQL, production настройки

## 📡 API Endpoints

### REST API

#### Activities

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/activities` | Получить все активности |
| GET | `/api/activities/category/{categoryId}` | Поиск по категории |
| GET | `/api/activities/search?location=...` | Поиск по локации |
| POST | `/api/activities` | Создать активность |

#### Categories

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/categories` | Получить все категории |
| POST | `/api/categories` | Создать категорию |
| GET | `/api/categories/init` | Инициализировать категории по умолчанию |

### Telegram Bot Commands

#### Основные команды

| Команда | Описание |
|---------|----------|
| `/start` | Начать работу с ботом |
| `/search` | Показать все активности |
| `/categories` | Показать категории |
| `/upcoming` | Предстоящие события |
| `/help` | Справка |

#### Бронирование

| Команда | Описание |
|---------|----------|
| `/book <ID>` | Записаться на активность |
| `/my_bookings` | Мои бронирования |
| `/cancel_booking <ID>` | Отменить бронирование |

## 🗄️ База данных

### Миграции

Проект использует Liquibase для управления миграциями БД.

Файл миграций: `src/main/resources/db/changelog/db.changelog-master.xml`

### Таблицы

- **categories** - Категории активностей (Квесты, Футбол, Ивенты...)
- **activities** - Активности/мероприятия
- **locations** - Локации (места проведения)

### H2 Console (dev)

В dev режиме доступна H2 console:
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:activitydb
Username: sa
Password: (пусто)
```

## 🏗️ Архитектура

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│   Telegram  │────▶│ ActivityTelegram │────▶│   Database  │
│   Users     │     │      Bot         │     │ (PostgreSQL)│
└─────────────┘     └──────────────────┘     └─────────────┘
                           │
                           ▼
                    ┌──────────────────┐
                    │  ActivityService │
                    │  (Business Logic)│
                    └──────────────────┘
                           │
                           ▼
                    ┌──────────────────┐
                    │ REST Controller  │
                    │   /api/*         │
                    └──────────────────┘
```

## 📁 Структура проекта

```
src/main/java/com/github/xmlreader/activitybot/
├── ActivityBotApplication.java    # Main class
├── bot/
│   └── ActivityTelegramBot.java   # Telegram бот
├── config/
│   └── TelegramBotConfig.java     # Конфигурация бота
├── controller/
│   └── ActivityController.java    # REST API
├── dto/
│   ├── ActivityRequest.java
│   ├── ActivityResponse.java
│   ├── CategoryRequest.java
│   └── CategoryResponse.java
├── entity/
│   ├── Activity.java
│   ├── Category.java
│   └── Location.java
├── exception/
│   ├── NotFoundException.java
│   ├── ValidationException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── ActivityRepository.java
│   └── CategoryRepository.java
└── service/
    └── ActivityService.java       # Бизнес-логика
```

## 🧪 Тестирование

```bash
# Запустить все тесты
mvn test

# Запустить с покрытием
mvn clean test jacoco:report
```

## 📦 Развёртывание

### Docker (TODO)

```bash
docker build -t activity-bot .
docker run -e TELEGRAM_BOT_TOKEN=xxx -e DB_HOST=postgres activity-bot
```

### VPS

1. Склонируйте репозиторий на сервер
2. Настройте переменные окружения
3. Запустите через systemd или Docker Compose

## 🤝 Вклад

1. Fork репозиторий
2. Создай feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Открой Pull Request

## 📝 License

MIT License

## 👨‍💻 Author

**Siarhei Yermakovich**
- Telegram: [@xmlreader](https://t.me/xmlreader)
- GitHub: [@SergejYermakovich](https://github.com/SergejYermakovich)

## 📞 Контакты

Есть вопросы или предложения? Пишите в Telegram: @xmlreader
