# Activity Bot 🎯

Telegram bot for finding and booking activities in your city (quests, football, events, etc.)

## 🚀 Features

- **Search activities** by category, location, date
- **Categories**: quests, football, events, workshops, sports
- **Location-based** search
- **Booking system** (future)
- **Admin panel** for activity providers (future)

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.2.3**
- **Telegram Bot API**
- **PostgreSQL** (production) / **H2** (development)
- **Maven**

## 📋 Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL (optional for production)
- Telegram Bot Token from [@BotFather](https://t.me/botfather)

## ⚙️ Configuration

1. Create `.env` file:
```bash
TELEGRAM_BOT_TOKEN=your_bot_token_here
TELEGRAM_BOT_USERNAME=your_bot_username_here
```

2. Or set environment variables:
```bash
export TELEGRAM_BOT_TOKEN=your_token
export TELEGRAM_BOT_USERNAME=your_username
```

## 🏃‍♂️ Running the Application

### Development (with H2 database):
```bash
mvn spring-boot:run
```

### Production (with PostgreSQL):
```bash
# Set database properties in application.yml
mvn clean package
java -jar target/activity-bot-0.0.1-SNAPSHOT.jar
```

## 📁 Project Structure

```
src/main/java/com/github/xmlreader/activitybot/
├── ActivityBotApplication.java      # Main application class
├── config/                          # Configuration classes
├── bot/                             # Telegram bot handlers
├── entity/                          # JPA entities
├── repository/                      # Spring Data repositories
├── service/                         # Business logic
├── controller/                      # REST controllers (if needed)
└── dto/                             # Data transfer objects
```

## 🤖 Bot Commands

- `/start` - Welcome message
- `/help` - Show available commands
- `/search` - Search activities
- `/categories` - Show activity categories
- `/nearby` - Find activities near you
- `/city` - Set your city
- `/notifications` - Manage notifications

## 🗺️ Roadmap

### MVP (Current)
- [x] Basic bot structure
- [x] Commands: /start, /help
- [ ] Activity search
- [ ] Categories

### Phase 1
- [ ] Database integration
- [ ] Activity CRUD
- [ ] Location-based search

### Phase 2
- [ ] Booking system
- [ ] Payment integration
- [ ] Admin panel

### Phase 3
- [ ] Reviews & ratings
- [ ] Notifications
- [ ] Multi-language support

## 📝 License

MIT License - see [LICENSE](LICENSE) file for details

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For questions and support, contact [@xmlreader](https://t.me/xmlreader)
