# Трекер привычек

Проект выполнен в рамках курса по Android-разработке от DoubletApp. Приложение представляет собой менеджер привычек с возможностью локального и серверного хранения, фильтрации, синхронизации и Clean Architecture.

---

## Описание приложения

Приложение позволяет создавать, редактировать и отслеживать привычки. Каждая привычка содержит:

* название
* описание
* приоритет
* тип (хорошая / плохая)
* периодичность
* цвет

Реализованы:

* Локальная база данных (Room)
* Синхронизация с сервером
* Clean Architecture
* Навигация через Jetpack Compose Navigation
* Фильтрация и поиск
* корутины, Flow, LiveData
* Dependency Injection (Dagger)
* Unit и UI тесты

---

## Архитектура

Проект реализован с использованием **Clean Architecture** и разделён на **3 модуля**:

* **data** — работа с базой данных, сетью и репозиториями, маппинг моделей
* **domain** — бизнес-логика, use cases, модели
* **app** (presentation) — Jetpack Compose UI, ViewModels, навигация

Связывание модулей осуществляется через Dagger Hilt, асинхронная работа — с помощью Kotlin Coroutines и Flow.

---

## 🛠 Используемые технологии

* Kotlin
* Jetpack Compose (100% Compose UI)
* Material 3 Design
* MVVM + Clean Architecture
* Room (локальная база данных)
* Coroutines + Flow + StateFlow
* LiveData
* Dagger Hilt (dependency injection)
* Retrofit + Gson (сетевые запросы)
* Compose Navigation (type-safe навигация)
* HorizontalPager (Compose аналог ViewPager2)
* ModalBottomSheet (Compose)
* Coil (загрузка изображений в Compose)
* Kotlin Serialization
* Unit Tests + UI Tests (Compose UI тесты)
  
### Jetpack Compose компоненты:
* Material 3 темы и компоненты
* SwipeToDismiss для удаления привычек
* PullToRefreshBox для обновления списка
* ModalNavigationDrawer для бокового меню
* Экспериментальные Material 3 API (PullToRefreshBox, SwipeToDismissBox)

---

## Выполненные задания с оценками, полученными за время курса

### Hw3 — Базовый UI и список привычек (оценка: **10/10**)

* Экран со списком привычек
* Экран добавления привычки
* Floating Action Button для добавления
* Пустой список при старте

---

### Hw4 — Навигация и фрагменты (оценка: **8/10**)

* Переход с Activity на Fragments
* Боковая навигация (DrawerLayout)
* Экраны: список, информация о приложении
* ViewPager для переключения между хорошими и плохими привычками
* Дополнительно: NavController

Т.к. проект сразу на Compose, по факту реализовано:

* Единственная Activity с Jetpack Compose
* Боковая навигация ModalNavigationDrawer в Compose
* HorizontalPager для переключения между хорошими и плохими привычками
* Type-safe навигация с Compose Navigation и sealed class Routes
  
---

### Hw5 — ViewModel и фильтрация (оценка: **8/10**)

* 2 ViewModel:

  * список привычек
  * создание / редактирование
* BottomSheet с фильтрацией и сортировкой
* Поиск по названию
* Сортировка по дате создания

---

### Hw6 — База данных (Room) (оценка: **9/10**)

* Реализована локальная база данных Room
* Хранение привычек в БД
* Получение данных через LiveData

---

### Hw7 — Асинхронная работа с БД (оценка: **10/10**)

* Запись в базу в фоновом потоке
* Использование Kotlin Coroutines
* Чтение через LiveData

---

### Hw8 — Синхронизация с сервером (оценка: **9/10**)

* Загрузка данных с сервера в локальную БД
* Отправка локальных данных на сервер
* Повтор запросов при ошибке
* Аватар пользователя в боковом меню:

  * загрузка с интернета через Coil
  * placeholder
  * обработка ошибок
  * круглая форма

Backend API:
[https://doublet.app/droid/8/api](https://doublet.app/droid/8/api)

---

### Hw9 — Clean Architecture + Dagger + бизнес-логика (оценка: **9/10**)

* Разделение проекта на 3 модуля

* Dependency Injection через Dagger

* Передача данных через Coroutines и Flow

* Логика выполнения привычек:

  **Для плохих привычек:**

  * если выполнений меньше лимита — Toast: `Можете выполнить еще N раз`
  * если больше — Toast: `Хватит это делать`

  **Для хороших привычек:**

  * если выполнений меньше — Toast: `Стоит выполнить еще N раз`
  * иначе — `You are breathtaking!`

* Дополнительно: сабкомпоненты и инжект ViewModel

---

### Hw10 — Тестирование (оценка: **9/10**)

* Unit-тесты для одного из модулей
* UI-тест одного из экранов

Реализовано:
* Unit-тесты для всех use cases (доменный слой)
* UI-тесты для экрана редактирования привычки (Compose UI тесты)
* Использование createComposeRule для тестирования Compose интерфейсов
* Mocking ViewModel и навигации с помощью MockK
  
---

## Запуск проекта

### 1. Склонировать репозиторий:

#### Требования
- Android Studio Flamingo или выше
- JDK 17
- Android SDK 34+

```bash
git clone https://github.com/NurseLady/doubletapp_hw2025.git
```
### 2. Открыть проект в Android Studio
### 3. Конфигурация API
- Создайте файл `local.properties` в корне проекта
- Добавьте ваш API токен: API_TOKEN="ваш_токен_здесь"
- Токен можно получить в телеграмм-боте DoubletApp
- Команда для телеграм-бота @DoubleDroidStudentBot для генерации токена для сервера: /generate_server_token

### 3. Собрать проект и запустить на эмуляторе или устройстве

