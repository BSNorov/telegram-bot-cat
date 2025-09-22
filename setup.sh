#!/bin/bash

echo "🐱 Скрипт настройки Котобота 🐱"
echo "=================================="

if [ ! -f .env ]; then
    echo "Создание файла .env из шаблона..."
    cat > .env << EOF
# Конфигурация Telegram Bot
BOT_USERNAME=your_bot_username
BOT_TOKEN=your_bot_token

# Конфигурация базы данных (опционально, предоставлены значения по умолчанию)
DATABASE_URL=jdbc:postgresql://localhost:5432/telegram_cat_bot
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# Конфигурация Kafka (для микросервисов)
KAFKA_SERVERS=localhost:9092
EOF
    echo "✅ Файл .env создан! Пожалуйста, отредактируйте его с данными вашего бота."
    echo ""
else
    echo "✅ Файл .env уже существует."
    echo ""
fi

echo "📋 Следующие шаги:"
echo "1. Отредактируйте файл .env с токеном и именем вашего бота от @BotFather"
echo "2. Выберите вариант развертывания:"
echo ""
echo "   🏠 Монолит (рекомендуется для разработки):"
echo "   docker-compose up -d"
echo ""
echo "   🏢 Микросервисы (для продакшена):"
echo "   docker-compose -f docker-compose-microservices.yml up -d"
echo ""
echo "3. Проверьте логи:"
echo "   docker-compose logs -f telegram-cat-bot"
echo ""
echo "4. Протестируйте бота, отправив /start в Telegram"
echo ""
echo "🔍 Полезные эндпоинты:"
echo "   Проверка здоровья: http://localhost:8080/actuator/health"
echo "   Метрики: http://localhost:8080/actuator/metrics"
echo "   Kafka UI (микросервисы): http://localhost:8081"
echo ""
echo "Удачного кодинга! 🚀"

