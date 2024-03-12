# SpringTinkoffAPI

В проекте реализовано: регистрация, авторизация, связь с базой данных, поиск по акциям, покупка и продажа акций отдельно на каждый аккаунт, отмена покупки или продажи, просмотра портфеля через Tinkoff API.

## Build
Собираем war файл
```
./gradlew war
```
Дальше запускаем docker
```
docker-compose up
```
И подключаем по ссылке http://localhost:8080/tinkoff/
Если нужно запустить локально нужно изменить переменные в [persistence-pg.properties](src%2Fmain%2Fwebapp%2Fpersistence-pg.properties)
и [hibernate.cfg.xml](src%2Fmain%2Fresources%2Fhibernate.cfg.xml). Поменять ссылку на выша базу данных.

## База данных
Команды для создания базы данных:
```sql
CREATE DATABASE tinkoff;

CREATE TABLE users
(id bigserial,
 username varchar(256) not null UNIQUE,
 password varchar(256),
 email varchar(256) UNIQUE,
 token varchar(512),
 primary key(id)
);

CREATE TABLE roles
(id serial,
name varchar(256) not null,
primary key(id)
);

CREATE TABLE users_roles
(user_id bigint not null,
 role_id int not null,
 primary key(user_id, role_id),
 foreign key(user_id) references users(id),
 foreign key(role_id) references roles(id)
);

INSERT INTO roles(id, name) VALUES(1, 'ROLE_ADMIN');
INSERT INTO roles(id, name) VALUES(2, 'ROLE_USER');
```
