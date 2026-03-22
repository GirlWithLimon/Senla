Данные для базового пользователя
{
    "username": "user1",
    "password": "12345"
}

Данные для администратора:
{
    "username": "admin",
    "password": "admin"
}

При регистрации можно создать только пользователя. Права администратора назначала через:
docker exec bookstore_postgres psql -U bookstore_user -d bookstore_db -c "UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'admin';"      
в IntelliJ IDEA Community Edition
