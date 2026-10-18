> Current stage: **0.15**. All 53 ritual families have native Fabric bindings and dedicated-server gameplay checks; see [scope, checks and limitations](GAMEPLAY-0.15.md). Older entries below are historical.

> Current build: **0.14 INCOMPLETE**; 40/53 original ritual families server-checked, including Insight, Clairvoyance, Reversion, The Ripper, Gandr and Cup of Heaven. [Current scope and limits](GAMEPLAY-0.14.md). Below are historical records.

> Current build: **0.13 INCOMPLETE**; 34/53 complete ritual families plus checked Power Consolidation lake, native murky fluids/bucket, persistent synchronized chunk fields and proximity/submerged fog. Caliburn/Morgan acquisition and abilities remain pending. [Current scope](GAMEPLAY-0.13.md). The records below are historical.

> Current build: **0.12 INCOMPLETE**; 34/53 original ritual families checked, including Black Flame Eyes with native block/entity/first-person rendering and live tracking/removal checks. Current scope: [GAMEPLAY-0.12.md](GAMEPLAY-0.12.md). The records below are historical.

> Current build: **0.11 INCOMPLETE**; 33/53 original ritual families, Proximity Projection Keys, familiar, recall/swap and garden have native server integration checks. Current scope and remaining dependencies: [GAMEPLAY-0.11.md](GAMEPLAY-0.11.md). The records below are historical.

> Current build: **0.10 INCOMPLETE**; 27/53 original ritual families, Kodoku and Clarent have server integration checks. Current scope: [GAMEPLAY-0.10.md](GAMEPLAY-0.10.md). The records below are historical.

> Current build: **0.9 INCOMPLETE**; 24/53 original ritual families and Kodoku are connected. Current scope: [GAMEPLAY-0.9.md](GAMEPLAY-0.9.md), [FEATURE-INVENTORY.md](FEATURE-INVENTORY.md). The older stages below are historical.

# Карта архитектуры исходного мода

Источник: фактический JAR, его manifest/TOML и декомпиляция Vineflower.
Имена Minecraft и мода читаемы; дополнительный remapping не применялся.
Сходство восстановленного текста с утраченным исходным проектом не утверждается.

## Масштаб и метаданные

- 878 class-файлов, major version 69 (Java 25).
- 759 восстановленных Java-файлов; разница включает вложенные классы.
- В 168 Java-файлах найдены прямые импорты `net.neoforged.*`.
- Вложенных JAR в исходном архиве нет.
- Версия мода `26.1.2-v1.36.36`; версия Minecraft в имени/manifest подтверждается
  использованием API 26.1.2 и сборкой адаптированного кода против этой версии.
  Обязательная зависимость от Minecraft или точная версия NeoForge в TOML не
  объявлены. `loaderVersion="[1,)"` — диапазон javafml, не версия NeoForge.
  Полную бинарную совместимость оригинала с конкретным NeoForge по этому полю
  определить нельзя. NeoForge-версия отдельно не запускалась.
- TOML: `All Rights Reserved`, `authors="stepsword"`.
- 23 mixin-записи: 12 клиентских и 11 общих. Они сохранены в recovered/;
  Fabric-сборка 0.7 использует отдельные mixin сохранения, сна/ProbabilityAlter/RuleBreaker и доступа к combat/durability полям.

## Потоки выполнения и платформенная граница

| Система | Исходные точки входа | Что требуется для Fabric |
|---|---|---|
| Инициализация | MahouTsukaiMod → CommonProxy / ClientProxy | ModInitializer / ClientModInitializer, строгое разделение сторон |
| Регистры | ModItems, ModBlocks, ModEntities, ModEffects, ModFluids, ModTileEntities | Native Registry + Fabric builders, прежние ID |
| События | ServerHandler, ServerHandlerModEventBus | Разобрать каждый SubscribeEvent на Fabric event или точечный mixin |
| Данные | DataAttachments, dataattachments/*, datacomponents/* | Сохранение, клонирование, sync и отдельный конвертер NeoForge-данных |
| Мана | ManaEvents → PlayerManaManager | Tick/sleep/damage hooks, gems, batteries, progression, packets |
| Круги | block/spells → tile/* → effects/* | Регистрация block entities, tickers, взаимодействия и синхронизация |
| Оружие | item/caliburn, morgan, clarent, mysticcode и др. | Attributes, combat hooks, component codecs, anim/render |
| Сеть | networking/*, PacketHandler, ClientPacketHandler | PayloadTypeRegistry, Client/ServerPlayNetworking, tracking, threading |
| Команды | commands/* | Brigadier с сохранением permission checks |
| Эффекты | effects/*, potion/* | Registry, tick/damage/equipment hooks |
| Измерения | dimension/*, util/MahouTsukaiTeleporter | Registry/dimension transitions, tickets и player state |
| Рендеринг | render/*, client/*, proxy/ClientProxy | Современные Fabric render hooks, слои, модельные состояния |
| Конфигурация | config/MTConfig | Заменить NeoForge config spec, сохранив значения и server/client scope |
| Интеграции | integration/jei, apotheosis, gamestages, jousting | Проверить наличие Fabric-аналогов и вынести необязательный код |

`PlayerManaManager.drainMana` использует не только поле storedMana: он учитывает
креатив, FakePlayer, инвентарные самоцветы, ближайшие батареи, счётчик увеличения
максимума, урон владельцу и отправку пакетов. Поэтому эта система не заменена
упрощённым счётчиком. Перенесённая Mahou — только оригинальная модель состояния.

Пять семейств attachments: chunk_mahou, living_mahou, player_mahou,
mahoujin_entity и settings_mahou. WorldMahou/MahouSavedData отдельно хранят
данные мира. Текущая адаптация сохраняет player_mahou в собственном дочернем
NBT-узле `mahoutsukai:player_mahou`, используя исходные имена полей и codecs.
Автоматического чтения NeoForge attachment container пока нет.

## Сложные зависимости от NeoForge

PlayerMahouPacket теперь адаптирован: сохранены исходный ID и StreamCodec,
NeoForge IPayloadContext заменён клиентским Fabric receiver. Регистрация только
clientbound; клиент не может этим пакетом менять серверное состояние маны.
Вход и AFTER_RESPAWN инициируют отправку состояния. Полный менеджер маны и
его последующие вызовы синхронизации пока остаются следующей задачей.

Полный перечень найденных платформенных импортов с файлами использования:
`../../analysis/platform-dependencies.json`. Граф всех импортов:
`../../analysis/source-import-graph.json`. Это статический граф прямых импортов,
не доказательство отсутствия зависимостей через наследование или bytecode hooks.

- Event bus/lifecycle, DeferredRegister/DeferredHolder и registry events.
- AttachmentType/IAttachmentSerializer и синхронизация сущностей.
- PacketDistributor и события регистрации payload.
- FakePlayer, chunk tickets, fluid types и extensions игровых классов.
- IItemExtension, combat, capability/transfer и client render APIs.
- Access transformers: исходный `META-INF/accesstransformer.cfg` открывает
  поля и методы боёвки, сущностей, UI, рендера, взрывов и частиц. Его нельзя
  просто добавить в Fabric JAR: каждое реально используемое обращение требует
  поддерживаемого API, access widener или accessor mixin.
- Enum extensions: две HumanoidModel.ArmPose (rifle/clarent) и один
  ItemUseAnimation. Fabric не обрабатывает NeoForge enumextensions.json;
  нужна адаптация анимации и pose hooks без поддельных enum-констант.

Оригинальные вызовы CustomPacketPayload и StreamCodec пригодны как основа
переноса, но PacketHandler смешивает импорт клиентского Minecraft с серверным
tracking. Его следует разделить перед включением в универсальный Fabric JAR.

## Следующая последовательность переноса

1. Завершить player/living/chunk/settings/world state, синхронизацию и конфиг.
2. Перенести базовые эффекты и полный PlayerManaManager вместе с батареями и gems.
3. Перенести регистрацию кругов и block entities, затем каждую группу ритуалов.
4. Перенести оружие и сущности вместе с компонентами/боевыми hook.
5. Адаптировать клиентские render states, частицы, poses и исходные mixins.
6. Проверить каждую механику против оригинала на отдельном NeoForge-стенде.
7. Проверить multiplayer, сохранение/смерть/перезапуск, Linux и реальный модпак.
8. Замерить взрывы на бюджете 2 vCPU / 2700 МБ heap с 5–10 игроками.

Результат декомпилятора требует исправления generic casts: например,
непараметризованные RequiredArgumentBuilder превращают CommandSourceStack
в Object. В двух адаптированных командах эти casts удалены. Во всём recovered/
исправления ещё не выполнены; единственная найденная WARN Vineflower о повторной
обработке lambda в CustomTrigger сохранена в decompile.log, ERROR не найдено.


## Подключённые пути Fabric 0.7

END_LEVEL_TICK → PlayerManaManager.onPlayerTick → regenMana/getManaRegen
и FaySightEyesPotion (период 4 ticks). Player.stopSleepInBed HEAD сохраняет
исходную позицию PlayerWakeUpEvent перед vanilla-пробуждением. Общая
LivingEntity.tick HEAD восстанавливает обычный RNG после ProbabilityAlter;
LivingEntity.hurtServer HEAD переносит исходный incoming-damage hook Rule Breaker.
ItemStack.getMaxDamage HEAD заменяет NeoForge dynamic durability extension.

FaeNoisePacket сохраняет исходный wire layout; отправка ограничена измерением,
радиусом и hasMagic. FaeRenderer использует native extraction/SubmitNodeCollector
для исходных core/glow quads. Rule Breaker extra model загружается через
Fabric ModelLoadingPlugin; исходная геометрия лезвия и цикл цветов сохранены.

DOOM — отдельный Entity с server-authoritative возрастом и persisted queue;
это дополнение пользователя. Положение и возраст передаются штатной сетью
Entity/SynchedEntityData. Новый damage type использует data pack tags.
Тестовые sourceSets selfCheck/visualCheck не входят в production jar.


## Подключения 0.8

KeyMapping M → KeyPressPacket serverbound → исходные проверки Bleeding,
поверхности и ткани → MahoujinTileEntity → PowderedCatalyst → таблица
четырёх адаптированных рецептов → SingleUse/ScrollGiver или AboveChecker.
Повтор рецепта использует Fabric ContainerStorage/ItemStorage и transaction.
Исходные типы NeoForge ResourceHandler в production-код не добавлялись.

Geas использует исходное пересечение луча/коробки и выбор ближайшей прямой
цели. AttackEntityCallback и ALLOW_DAMAGE запрещают нарушение обета;
ItemEntity.playerTouch mixin запрещает подбор. Второй точечный hook перед
native ItemStack.onDestroyed передаёт DamageSource в исходную fire/lava
ветку GeasScroll: native Item.onDestroyed имеет только один аргумент.
Fae Essence, root trigger и лут феи подключены без имитации поведения.
Не перенесены GameStages gating и остальные типы кругов.
