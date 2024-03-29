# Клиент сервиса Deezer

## О клиенте

Данное приложение является графическим клиентом для работы с сервисом потоковой музыки [Deezer](https://www.deezer.com "Ссылка на главный сайт сервиса Deezer").  
Также данное приложение никоим образом не связано с разработчиками Deezer и является исключительно моим проектом для самообразования (хотя если вдруг когда-нибудь оно заинтересует разработчиков Deezer в качестве клиента для платформы Linux или MacOS, милости просим :) ).  
Вдохновение для системы классов Java-SDK для Deezer (разработанного мной параллельно с клиентским приложением) почёрпнуто из [официального SDK Deezer для Android](https://developers.deezer.com/sdk/android "Ссылка на Deezer Android SDK").  
Стоит отметить что разработанный SDK является платформонезависимым.
В остальном система классов и предоставляемая функциональность основана исключительно на [API Deezer](https://developers.deezer.com/api "Ссылка на описание API Deezer").  
Следовательно, прослушивание музыки в клиенте на данный момент никак не предоставляется.  

## Особенности реализации

В SDK авторизация пользователя реализована с использованием технологии OAuth 2, поэтому никакие личные данные пользователя клиентом не хранятся. Единственное, что сохраняет приложение (в зашифрованном виде) - токен, получаемый от системы авторизации Deezer.  
Для упрощения реализации авторизации использована библиотека [ScribeJava](https://github.com/scribejava/scribejava "Ссылка на официальный репозиторий ScribeJava"), с небольшой доработкой специфических для системы авторизации Deezer моментов.  
Графический интерфейс реализован с использованием фреймворка JavaFX.  
Так как первая версия разрабатывалась в сжатые сроки, изначально не было выделено никаких компонентов интерфейса - всё необходимое было задано в коде окна (и он был просто огромен). Сейчас эта ситуация постепенно исправляется, однако ещё не всё было вынесено в компоненты, и часть кода находится в малость подвешенном состоянии, поэтому приложение может быть нестабильно. Гарантированно рабочая версия приложения может быть найдена по состоянию на 8-9 января (совершенно точно рабочая версия - от 7 ноября).
