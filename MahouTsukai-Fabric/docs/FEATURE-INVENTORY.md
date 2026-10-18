# Инвентаризация оригинального содержимого

Источник: оригинальный JAR 1.36.36 для Minecraft 26.1.2.

В 0.24 добавлен прямой NeoForge/Fabric-прогон 1372 сценариев катализаторов, ткани и фейских кругов для всех 53 рецептов; см. [метод и точные границы сравнения](SCENARIO-COMPARISON-0.24.md).

Серверные проверки пройдены для **53 из 53 ритуальных семейств (100% этого раздела)** и **27 из 27 зарегистрированных типов сущностей**. Второй счётчик включает снаряды и визуальные сущности.

**Это не процент готовности всего мода.** Оружие, остальные существа, интерфейс, измерения и совместимость учитываются отдельно; DOOM не увеличивает покрытие оригинала. Полное визуальное совпадение новых систем ещё требует проверки в клиенте. Готовность всего мода в процентах пока не подтверждена.

## Оригинальные ритуальные семейства

| Семейство | Катализаторы | Проверка на сервере |
|---|---|---|
| alarmBoundary | iron, iron, quartz | пройдена |
| drainLifeBoundary | iron, iron, emerald | пройдена |
| tangibleBoundary | iron, iron, diamond | пройдена |
| gravityBoundary | iron, iron, gold | пройдена |
| displacementBoundary | iron, iron, ender | пройдена |
| raiseEnclosureBoundary | iron, iron, iron | пройдена |
| projection | diamond, diamond, quartz | пройдена |
| weaponShooter | diamond, diamond, gold | пройдена |
| strengthening | diamond, diamond, iron | пройдена |
| proximityProjection | diamond, diamond, ender | пройдена |
| realityMarble | diamond, diamond, diamond | пройдена |
| powerConsolidation | diamond, diamond, emerald | пройдена |
| treasuryProjection | diamond, diamond, eye | пройдена |
| projectileDisplacement | ender, ender, diamond | пройдена |
| ascension | ender, ender, gold | пройдена |
| orderedDisplacement | ender, ender, ender | пройдена |
| equivalentDisplacement | ender, ender, emerald | пройдена |
| mentalDisplacement | ender, ender, quartz | пройдена |
| protectiveDisplacement | ender, ender, iron | пройдена |
| scrying | ender, ender, eye | пройдена |
| catalystExchange | emerald, emerald, emerald | пройдена |
| durabilityExchange | emerald, emerald, diamond | пройдена |
| damageExchange | emerald, emerald, iron | пройдена |
| chronalExchange | emerald, emerald, quartz | пройдена |
| alchemicalExchange | emerald, emerald, gold | пройдена |
| contract | emerald, emerald, ender | пройдена |
| immunity | emerald, emerald, eye | пройдена |
| mysticStaff | gold, gold, diamond | пройдена |
| borrowedAuthority | gold, gold, gold | пройдена |
| rhoAias | gold, gold, iron | пройдена |
| damageReplication | gold, gold, emerald | пройдена |
| spatialDisorientation | gold, gold, ender | пройдена |
| cupOfHeaven | gold, gold, quartz | пройдена |
| summonFamiliar | quartz, quartz, quartz | пройдена |
| recallFamiliar | quartz, quartz, ender | пройдена |
| possessEntity | quartz, quartz, gold | пройдена |
| swapFamiliar | quartz, quartz, emerald | пройдена |
| familiarGarden | quartz, quartz, iron | пройдена |
| butterflyEffect | quartz, quartz, diamond | пройдена |
| mysticEyes | eye, eye, gold | пройдена |
| prediction | eye, eye, quartz | пройдена |
| deathCollection | eye, eye, iron | пройдена |
| blackFlame | eye, eye, diamond | пройдена |
| reversionEyes | eye, eye, emerald | пройдена |
| faySight | eye, eye, eye | пройдена |
| insight | eye, eye, ender | пройдена |
| retribution | emerald, iron, diamond | пройдена |
| presenceConcealment | ender, iron, diamond | пройдена |
| gandr | gold, emerald, diamond | пройдена |
| geas | gold, emerald, iron | пройдена |
| fallenDown | gold, ender, iron | пройдена |
| probabilityAlter | gold, diamond, quartz | пройдена |
| selectiveDisplacement | eye, ender, emerald | пройдена |

## Оригинальные сущности

| Тип | Класс | Статус |
|---|---|---|
| GANDR | GandrEntity | server-checked |
| WEAPON_PROJECTILE | WeaponProjectileEntity | server-checked |
| GATE_WEAPON_PROJECTILE | GateWeaponProjectileEntity | server-checked |
| MENTAL_DISPLACEMENT | MentalDisplacementEntity | server-checked |
| EXPLOSION_MAHOUJIN | MysticStaffMahoujinEntity | server-checked |
| AOE_EXPLOSION_MAHOUJIN | MysticStaffAOEMahoujinEntity | server-checked |
| GUN_ENTITY | GunEntity | server-checked |
| REPLICA_TELEPORT | ReplicaTeleportMahoujinEntity | server-checked |
| BEAM_MAHOUJIN | MysticStaffBeamMahoujinEntity | server-checked |
| FAMILIAR | FamiliarEntity | server-checked |
| KODOKU | KodokuEntity | server-checked |
| SMITE | SmiteEntity | server-checked |
| LIGHTNING | LightningEntity | server-checked |
| LIGHTNING_BOLT | MahouLightningBoltEntity | server-checked |
| MORGAN_BALL | MorganBallEntity | server-checked |
| RHO_AIAS | RhoAiasMahoujinEntity | server-checked |
| GATE_CHAIN | GateChainEntity | server-checked |
| BUTTERFLY | ButterflyEntity | server-checked |
| SPATIAL_DISORIENTATION | SpatialDisorientationMahoujinEntity | server-checked |
| AUTHORITY | AuthorityMahoujinEntity | server-checked |
| FAE | FaeEntity | server-checked |
| BEAM_PROJECTILE | BeamProjectileEntity | server-checked |
| FALLEN_DOWN | FallenDownEntity | server-checked |
| FALLEN_DOWN_BEAM | FallenDownBeamEntity | server-checked |
| WILLIAM | WilliamEntity | server-checked |
| RHONGOMYNIAD | RhongomyniadEntity | server-checked |
| GUN_BEAM_PROJECTILE | GunBeamProjectileEntity | server-checked |

## Оригинальные предметы

Регистрация означает наличие предмета в игре; она не подтверждает перенос всех связанных механик.

| ID | Класс | Статус регистрации |
|---|---|---|
| guidebook | MahouTsukaiGuidebook | registered |
| funbook | MahouTsukaiFunbook | registered |
| dagger | Dagger | registered |
| weapon_projectile_bow | WeaponProjectileBow | registered |
| caliburn | Caliburn | registered |
| clarent | Clarent | registered |
| morgan | Morgan | registered |
| hammer | Hammer | registered |
| mystic_code | MysticCode | registered |
| attuner | Attuner | registered |
| attuned_diamond | AttunedDiamond | registered |
| attuned_emerald | AttunedEmerald | registered |
| pestle | Pestle | registered |
| theripper | TheRipper | registered |
| staff_emrys | StaffEmrys | registered |
| replica | Replica | registered |
| mystic_code_first_sorcery | MysticCodeFirstSorcery | registered |
| mortar | Mortar | registered |
| kodoku | KodokuItem | registered |
| faecake | Faecake | registered |
| mortar_and_pestle | MortarAndPestle | registered |
| proximity_projection_keys | ProximityProjectionKeys | registered |
| murky_bucket | BucketItem | registered |
| treasury_projection_gauntlet | TreasuryProjectionGauntlet | registered |
| powdered_diamond | PowderedDiamond | registered |
| powdered_emerald | PowderedEmerald | registered |
| powdered_ender | PowderedEnder | registered |
| powdered_eye | PowderedEye | registered |
| powdered_gold | PowderedGold | registered |
| powdered_iron | PowderedIron | registered |
| powdered_quartz | PowderedQuartz | registered |
| fae_essence | FaeEssence | registered |
| mystic_staff | MysticStaff | registered |
| spatial_disorientation_staff | SpatialDisorientationStaff | registered |
| rule_breaker | RuleBreaker | registered |
| scroll_boundary_displacement | DisplacementBoundarySpellScroll | registered |
| scroll_boundary_alarm | AlarmBoundarySpellScroll | registered |
| scroll_boundary_raise_enclosure | RaiseEnclosureBoundarySpellScroll | registered |
| scroll_boundary_tangible | TangibleBoundarySpellScroll | registered |
| scroll_boundary_gravity | GravityBoundarySpellScroll | registered |
| scroll_boundary_drain_life | DrainLifeBoundarySpellScroll | registered |
| scroll_weapon_shooter | WeaponShooterSpellScroll | registered |
| scroll_projection | ProjectionSpellScroll | registered |
| scroll_reality_marble | RealityMarbleSpellScroll | registered |
| scroll_strengthening | StrengtheningSpellScroll | registered |
| scroll_treasury_projection | TreasuryProjectionSpellScroll | registered |
| scroll_proximity_projection | ProximityProjectionSpellScroll | registered |
| scroll_damage_exchange | DamageExchangeSpellScroll | registered |
| scroll_immunity_exchange | ImmunityExchangeSpellScroll | registered |
| scroll_projectile_displacement | ProjectileDisplacementSpellScroll | registered |
| scroll_protective_displacement | ProtectiveDisplacementSpellScroll | registered |
| scroll_ascension | AscensionSpellScroll | registered |
| scroll_mental_displacement | MentalDisplacementSpellScroll | registered |
| scroll_mystic_eyes | BindingEyesSpellScroll | registered |
| scroll_black_flame | BlackFlameEyesSpellScroll | registered |
| scroll_prediction | ClairvoyanceEyesSpellScroll | registered |
| scroll_death_collection | DeathCollectionEyesSpellScroll | registered |
| scroll_fay_sight | FaySightEyesSpellScroll | registered |
| scroll_reversion_eyes | ReversionEyesSpellScroll | registered |
| scroll_insight | InsightEyesSpellScroll | registered |
| scroll_familiars_garden | FamiliarsGardenSpellScroll | registered |
| scroll_possess_entity | PossessEntitySpellScroll | registered |
| scroll_recall_familiar | RecallFamiliarSpellScroll | registered |
| scroll_summon_familiar | SummonFamiliarSpellScroll | registered |
| scroll_butterfly_effect | ButterflyEffectSpellScroll | registered |
| scroll_swap_familiar | SwapFamiliarSpellScroll | registered |
| scroll_spatial_disorientation | SpatialDisorientationSpellScroll | registered |
| scroll_rho_aias | RhoAiasSpellScroll | registered |
| scroll_damage_replication | DamageReplicationSpellScroll | registered |
| scroll_mystic_staff | MysticStaffSpellScroll | registered |
| scroll_borrowed_authority | BorrowedAuthoritySpellScroll | registered |
| scroll_cup_of_heaven | CupOfHeavenSpellScroll | registered |
| scroll_retribution | RetributionSpellScroll | registered |
| scroll_presence_concealment | PresenceConcealmentSpellScroll | registered |
| scroll_gandr | GandrSpellScroll | registered |
| scroll_geas | GeasSpellScroll | registered |
| scroll_fallen_down | FallenDownSpellScroll | registered |
| scroll_probability_alter | ProbabilityAlterSpellScroll | registered |
| scroll_selective_displacement | SelectiveDisplacementSpellScroll | registered |
| nobu | Nobu | registered |
| william | William | registered |
