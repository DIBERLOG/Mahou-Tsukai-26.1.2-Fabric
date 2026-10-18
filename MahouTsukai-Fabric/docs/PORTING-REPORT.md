Актуальное состояние: [0.21 — оружие и Iris, проверки готового JAR](GAMEPLAY-0.21.md). Записи ниже — история предыдущих этапов.

Актуальное состояние: [0.18 — интеграция JEI и импорт миров](GAMEPLAY-0.18.md). Ниже также сохранены исторические этапы.

> Актуальный этап: **0.17 INCOMPLETE** — [сущности, интерфейсы и эффекты](GAMEPLAY-0.17.md). 27/27 типов сущностей имеют серверные проверки. Ниже сохранена история; старые ограничения не описывают текущую сборку.

> Current build: **0.15 INCOMPLETE**; all **53/53 ritual families** have native server gameplay checks. Whole-mod parity is incomplete. See [current scope and limitations](GAMEPLAY-0.15.md); older entries below are historical.

> Current build: **0.14 INCOMPLETE**; 40/53 original ritual families server-checked, including Insight, Clairvoyance, Reversion, The Ripper, Gandr and Cup of Heaven. [Current scope and limits](GAMEPLAY-0.14.md). Below are historical records.

> Current build: **0.13 INCOMPLETE**; 34/53 complete ritual families plus checked Power Consolidation lake, native murky fluids/bucket, persistent synchronized chunk fields and proximity/submerged fog. Caliburn/Morgan acquisition and abilities remain pending. [Current scope](GAMEPLAY-0.13.md). The records below are historical.

> Current build: **0.12 INCOMPLETE**; 34/53 original ritual families checked, including Black Flame Eyes with native block/entity/first-person rendering and live tracking/removal checks. Current scope: [GAMEPLAY-0.12.md](GAMEPLAY-0.12.md). The records below are historical.

> Current build: **0.11 INCOMPLETE**; 33/53 original ritual families, Proximity Projection Keys, familiar, recall/swap and garden have native server integration checks. Current scope and remaining dependencies: [GAMEPLAY-0.11.md](GAMEPLAY-0.11.md). The records below are historical.

> Current build: **0.10 INCOMPLETE**; 27/53 original ritual families, Kodoku and Clarent have server integration checks. Current scope: [GAMEPLAY-0.10.md](GAMEPLAY-0.10.md). The records below are historical.

> Current build: **0.9 INCOMPLETE**; 24/53 original ritual families and Kodoku are connected. Current scope: [GAMEPLAY-0.9.md](GAMEPLAY-0.9.md), [FEATURE-INVENTORY.md](FEATURE-INVENTORY.md). The older stages below are historical.

> Текущий этап **0.8**: [ритуалы и Geas](RITUALS-0.8.md). Ниже история.

> Текущее состояние: **0.7**, см. [MAGIC-0.7](MAGIC-0.7.md).
> Ниже сохранена история предыдущих этапов; её старые ограничения не описывают 0.7.

# Отчёт о портировании — 15 октября 2026

**Полноценный Fabric-порт не завершён. Текущий JAR не является готовой заменой
Mahou Tsukai.** Это проверенная часть переноса с оригинальными вспомогательными
предметами и сохранением модели состояния. Большинство игровых систем пока
содержится только в восстановленном коде.

## Выполнено

- Сохранена отдельная копия оригинального JAR с атрибутом read-only и SHA-256.
  Атрибут защищает от случайной записи, но не является криптографической блокировкой.
- Проверен CRC всех записей архива; извлечены manifest, TOML, access transformers,
  enum extensions, mixins и перечень содержимого.
- Декомпилированы 878 классов в 759 Java-файлов; все исходные ресурсы сохранены.
- Сформированы перечень платформенных импортов, граф импортов, карта архитектуры
  и пофайловый список покрытия адаптацией.
- Создан Gradle wrapper 9.7.0 / Loom 1.18.4 проект с разделёнными main/client
  source sets для Minecraft 26.1.2, Loader 0.19.5 и API 0.155.3+26.1.2.
- Включены 67 файлов main/client, соответствующие исходным Java-путям; часть изменена
  или переписана под Fabric. Ещё 692 файла остаются только в recovered/.
  Эти числа не являются процентом готовности механик.
- Зарегистрированы оригинальные ID 11 предметов, 11 исходных рецептов,
  модели, локализации и нужные текстуры. Неиспользуемые текстуры временно
  исключены из частичной сборки, но сохранены в recovered/.
- Сохранена исходная логика возврата молота и ступки с пестиком через
  FabricItem.getCraftingRemainder(ItemStack), без NeoForge DeferredHolder.
- Подключены оригинальные Mahou/IMahou, MahouStorage и их вспомогательные
  типы. Vanilla save hooks вызывают исходную сериализацию в новом дочернем
  теге. COPY_FROM вызывает исходный copyMahou при замене ServerPlayer.
- Подключены оригинальные `/getmahou` и `/getmaxmahou` с исходными правами
  gamemaster; исправлены ошибки generic casts декомпиляции.

## Состояние механик

| Возможность | Результат |
|---|---|
| Молот, ступка, пестик, ступка с пестиком | Зарегистрированы; молот проверен в крафте |
| Семь порошков катализаторов | Зарегистрированы; алмазный порошок проверен в крафте |
| Модели и текстуры этих предметов | Загружены клиентом; молот и порошок видны в игре |
| Рецепты включённой части | Загружены сервером; один рецепт выполнен вручную |
| Состояние маны/баффов/прогресса | Исходная модель и сериализация подключены |
| Копирование при возрождении | Адаптировано; игровой death/respawn-тест выполнен |
| Сохранение данных игрока | Реальный файл содержит исходные поля; round-trip проверен отдельно |
| Расход маны, gems и батареи | Связанная группа перенесена и проверена; детали ECONOMY.md |
| Обычное восстановление маны | Не перенесено |
| Компонент gem_mahou | Зарегистрирован; оригинальные persistent/wire codecs, сохранение предмета после перезапуска проверены |
| Формула роста максимума и счётчика | Подключена к реальному расходу, проверена на настоящем сервере |
| World config прогрессии и maxmahou/addmaxmahou | Перенесены; изменённое состояние получено настоящим клиентом |
| PlayerMahouPacket | Перенесён исходный формат, отправка при входе/возрождении, применение на клиенте |
| HUD маны и остальные сетевые пакеты | Не перенесены |
| LivingMahou / ILivingMahou / LivingMahouStorage | Перенесены модель и сохранение состояния живых существ |
| Заклинания и магические круги | Восстановлены, не адаптированы |
| Caliburn, Morgan, Clarent и остальное оружие | Восстановлены, не адаптированы |
| Взрывы, частицы, VFX, анимации, poses | Восстановлены, не адаптированы |
| Существа и измерения | Восстановлены, не адаптированы |
| Два circuit block entity | Перенесены с рендером и сохранением; остальные BE ещё не готовы |
| Конфиг и необязательные интеграции | Восстановлены, не адаптированы |
| Миграция NeoForge-мира | Не реализована |

Сложные системы не подменялись имитациями. В частности, вместо полного
PlayerManaManager не добавлен упрощённый расход маны; вместо оригинальных
взрывов не добавлен vanilla explosion с похожим названием.

## Реально выполненные проверки

| Проверка | Факт / доказательство |
|---|---|
| CRC и SHA-256 оригинала | PASS; analysis/inventory.json, sha256.txt |
| Сборка main + client | PASS; журналы сборки |
| Проверка исходных mana defaults | PASS; portSelfCheck |
| Сериализация изменённых значений, прогресса, рецепта | PASS; полное равенство NBT до/после round-trip |
| Порядок/кратность компонентов ритуала | PASS; multiset проверка UnorderedList |
| Полный PlayerMahouPacket encode/decode | PASS; декодированное состояние сравнивается по всем NBT-полям, байты потреблены полностью |
| Получение пакета настоящим клиентом | PASS при входе и возрождении; analysis/mana-network-client.log |
| Смерть и возрождение игрока | PASS для текущих полей маны 100/100; analysis/mana-respawn-checks.txt |
| LivingMahou NBT round-trip и clearBuffs | PASS; исходные поля и семантика сброса проверены |
| LivingMahou у настоящего моба после перезапуска | PASS; корова сохранила kodoku=7, authority_hit=true, acting=3; analysis/living-mahou-checks.txt |
| Сброс баффов с сохранением маны | PASS; portSelfCheck |
| Все 11 item definitions/model/texture references | PASS; scripts/check_resources.py |
| Загрузка клиентского Minecraft | PASS; окно проверено через computer-use, атласы созданы |
| Dedicated server без клиентских зависимостей | PASS; сервер создал отдельный мир и сообщил Done |
| Подключение клиента | PASS; Player733 вошёл на 127.0.0.1:25585 |
| Оригинальный рецепт diamond + hammer | PASS; UI-крафт, алмаз 2→1, порошок 1→2, молот остался 1 |
| Команды чтения stored/max mana | PASS; обе вернули 100 |
| Сохранение player_mahou в фактическом player .dat | PASS; savedPlayerCheck прочитал файл, проверил поля и инвентарь |
| Штатное сохранение и остановка | PASS; save-all flush и stop через локальный RCON |
| Перезапуск сервера и загрузка мира | PASS; analysis/server-restart.log |
| Повторный вход после перезапуска | PASS; Player733 повторно вошёл, мана 100/100 и инвентарь 1 молот / 2 порошка / 1 алмаз сохранены; analysis/reconnect-checks.txt |
| Синхронизация заклинаний/VFX | Не выполнена: системы не перенесены |
| 5–10 игроков, профилирование и утечки памяти | Не выполнены |
| Linux/Docker/пользовательский модпак | Не выполнены |

После того как пользователь самостоятельно обработал запрос Windows, выполнен
повторный dev-запуск с тем же именем Player733. Подключение к перезапущенному
тестовому серверу подтверждено окном игры и командами сервера. Повторно проверены
поля player_mahou и точный инвентарь после крафта. Сервер сохранён и штатно остановлен.
Согласованный замысел дополнительного заклинания сохранён в DOOM.md; реализации
DOOM в текущем JAR ещё нет.

Тестовая EULA явно принята пользователем. Offline authentication использовалась
только для локального dev-клиента; сервер и RCON привязаны к 127.0.0.1.
В dev-клиенте есть сообщения 401 при попытке получить profile key/Realms с
тестовой сессией FabricMC. Они не помешали локальному подключению; проверка
настоящей авторизованной online-mode сессии не проводилась.

В первом клиентском запуске неиспользуемая оригинальная текстура runes 199×16
снижала mipmap блока до 0. Она исключена только из активных ресурсов текущей
частичной сборки. В следующем запуске blocks atlas создан с mip level 4.
Неполный код явно пишет предупреждение INCOMPLETE при старте.

Ранние неуспешные попытки сборки обнаружили: несовместимый Gradle 9.4,
переименование Fabric creative tab API, различия crafting remainder и raw generic
casts. Они исправлены в текущей части проекта. Весь recovered/ не компилируется
как Fabric и не включён скрыто в готовый JAR.

## Что блокирует полноценный результат

Актуальное дополнение 0.5 — ECONOMY.md: подключён расход маны вместе с настоящими
самоцветами и батареями, исходной графикой и воспроизводимыми серверными проверками.
Это не подтверждение 50% готовности; боевые заклинания и оружие всё ещё отсутствуют.

Дополнение 0.4: перенесён компонент данных самоцветов. Зарядка и предметы
AttunedGem ещё не подключены; границы проверки описаны в GEM-COMPONENT.md.

Дополнение 0.3: формула прогрессии, world config, команды и точные границы
выполненных проверок описаны в MANA-PROGRESSION.md. Новая конфигурация не
заменяет весь MTConfig; игровой расход и регенерация ещё не реализованы.

Нет одного переключателя, который превращает эти исходники в Fabric-мод.
Нужно адаптировать оставшиеся registry/event/attachment/network/render связи,
описанные в ARCHITECTURE.md, затем проверить исходную семантику каждой системы.
Особенно существенны NeoForge enum extensions, access transformers, FakePlayer,
сохранение мира и синхронизация. Это оставшаяся реализация, а не ограничение
компилятора или отсутствие разрешения на локальную разработку.

Для независимого подтверждения полного соответствия нужен отдельный запуск
оригинала на подходящем NeoForge и сравнительная матрица игровых сценариев.
Точная версия NeoForge по предоставленным метаданным не установлена.
Для проверки модпака нужны его фактические версии и конфиги; для распространения
— подтверждение объёма авторского разрешения. Рабочий Beget-сервер, Compose и
существующий пользовательский мир не менялись.


### 0.6 — восстановление во сне

Подключены исходные условия завершения сна и его восстановление маны,
включая штрафы за души и настройки. Проверены native wake hook, погода,
время, флаги прерывания и границы запаса в отдельном dedicated server.
Сборка и runEconomyChecks прошли; см. analysis/sleep-delivery-build.log.
Периодическая регенерация, заклинания и DOOM остаются неперенесёнными.
У runEconomyChecks теперь обязательна свежая отметка успешного завершения:
Minecraft способен обработать серверное исключение и завершиться с кодом 0.
