# Название мода в меню паузы

Начиная с **0.41** надпись «Меню» в окне паузы заменена логотипом **MAHOU TSUKAI** с наклонной подписью **Fabric port**. Убрана надпись о сезоне; добавлены два сдержанных красных рунических круга слева сверху и справа снизу. Обычные кнопки, их действия и управление с клавиатуры сохраняются.

Логотип уменьшается по свободному месту над первой кнопкой. Главный экран Minecraft и компактная надпись паузы при загрузке мира не изменяются.

## Заменить картинку через ресурспак

У игроков должен быть установлен **Mahou Tsukai Fabric 0.41 или новее**: один серверный JAR не рисует интерфейс на чужом клиенте.

Готовый шаблон: [MahouTsukai-pause-menu-resourcepack-0.41.zip](https://github.com/DIBERLOG/Mahou-Tsukai-26.1.2-Fabric/releases/download/v1.36.36-fabric.0.41/MahouTsukai-pause-menu-resourcepack-0.41.zip).

Внутри ZIP замените только файл:

```text
assets/mahoutsukai/textures/gui/pause_logo.png
```

Используйте прозрачный PNG с пропорциями **2100 × 749**, как у вложенного шаблона. Сохраните расположение файлов: `pack.mcmeta` должен находиться в корне ZIP. Пересобирать мод не требуется.

Для локальной проверки поместите ZIP в `resourcepacks` своего игрового профиля и включите его в настройках ресурсов. Готовый шаблон можно раздать с сервера стандартными параметрами Minecraft в `server.properties`:

```properties
resource-pack=https://github.com/DIBERLOG/Mahou-Tsukai-26.1.2-Fabric/releases/download/v1.36.36-fabric.0.41/MahouTsukai-pause-menu-resourcepack-0.41.zip
resource-pack-sha1=439b4a2db75b7b8f65d9b58ef541b5c4c5ced5b8
```

Для собственной картинки загрузите изменённый ZIP по своей прямой HTTPS-ссылке и замените адрес в параметрах. После каждого изменения ZIP пересчитайте его SHA-1. В Windows это можно сделать так:

```powershell
(Get-FileHash -Algorithm SHA1 -LiteralPath .\mahou-menu.zip).Hash.ToLowerInvariant()
```

Игрок принимает серверный ресурспак обычным способом. После его отключения снова используется логотип, встроенный в мод.

## Ресурс и происхождение

Исходный PNG: [pause_logo.png](../src/main/resources/assets/mahoutsukai/textures/gui/pause_logo.png). Итоговый ресурс создан встроенным imagegen по пользовательскому референсу, затем проверен в настоящем игровом меню. Ранние варианты с избыточным свечением не используются.

Финальный промпт (редактирование выбранного изображения):

> Edit this logo to look like a restrained, deliberately designed Minecraft mod wordmark, LESS like glossy AI fantasy art. Preserve exact text: top "MAHOU TSUKAI"; bottom "Fabric port" with the same forward-slanted 3D block lettering. REMOVE ALL glitter, starbursts, little stars, crescents, gem shapes, elaborate pseudo-writing, glowing jewelry, grainy gold patterns and noisy tiny cracks. Main letters: clean warm light-gray/ivory voxel stone faces, restrained bevel shading, just a few simple substantial cracks, black charcoal extrusion, crisp black keyline and a consistent white outside contour. No gold engravings covering the letters. Bottom Fabric port: muted teal stone faces, black 3D depth and white contour, leaning forward about 12 degrees like a classic Minecraft server subtitle. Keep TWO simple flat DARK CRIMSON rune-circle ornaments only, at the top-left and bottom-right corners, tucked behind the lettering: simple clean concentric circles with a handful of large angular geometric rune marks, no moons, no starburst flares, no neon glow, no excessive detail. Make the overall identity legible, calm, hand-arranged and coherent, with crisp pixel-oriented materials rather than painterly texture. Every letter and corner circle entirely inside frame with clear transparent margin. Transparent PNG alpha background, no rectangle, no checkerboard, no extra text, no season, no watermark. Wide horizontal game-menu logo, readable at small display size.
