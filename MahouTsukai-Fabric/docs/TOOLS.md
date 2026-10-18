# Инструменты и воспроизводимость

Инструменты загружены с официальных источников, локально в `../../tools/`:

- Vineflower 1.12.0:
  https://github.com/Vineflower/vineflower/releases/download/1.12.0/vineflower-1.12.0.jar
- Eclipse Temurin JDK 25, Windows x64 HotSpot:
  https://api.adoptium.net/v3/binary/latest/25/ga/windows/x64/jdk/hotspot/normal/eclipse
  Полученная версия: 25.0.4.1+1. Этот URL динамический, для точного повторения
  используйте конкретный релиз и сохранённый SHA-256 архива.
- Gradle 9.7.0:
  https://services.gradle.org/distributions/gradle-9.7.0-bin.zip

SHA-256 фактически загруженных файлов находится в `../../analysis/tool-sha256.json`.
Это локальная фиксация содержимого, а не заявление о независимой проверке
цифровых подписей. Сторонние перепакованные декомпиляторы не использовались.

Исходный мод не запускался как инструмент. CRC архива проверен `zipfile.testzip`.
Декомпиляция выполнена через JDK 25:

```sh
java -jar tools/vineflower.jar -dgs=1 -rsy=1 original/mahoutsukai-26.1.2-v1.36.36.jar recovered
```

Журнал — `analysis/decompile.log`. Одна WARN о повторной обработке lambda
CustomTrigger не означает проверенную корректность всех декомпилированных методов.
Исходный проект целиком ещё не компилировался; только адаптированная часть Fabric.

`scripts/local_rcon.py` использует Python stdlib, читает случайный пароль только
из игнорируемого локального профиля и соединяется исключительно с 127.0.0.1:25586.
Он предназначен для временного тестового мира, не для рабочего сервера.
