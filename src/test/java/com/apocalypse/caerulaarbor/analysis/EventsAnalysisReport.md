# Events 包事件处理器分析报告

> 自动生成时间: 2026-04-21T17:35:13.993

---

## 概览

- **总事件处理类数量**: 27
- **事件类型数量**: 11

---

## 按事件类型分组

以下表格展示了哪些类使用的是同一个事件：

### 1. PlayerTickEvent (7 个类)

**完整事件类型**: `TickEvent.PlayerTickEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `ArmorEnchantFuncProcedure` | `onPlayerTick` | NORMAL | 1 |
| 2 | `BanRElicFuncProcedure` | `onPlayerTick` | NORMAL | 1 |
| 3 | `EssenceResistanceWithIceProcedure` | `onPlayerTick` | NORMAL | 1 |
| 4 | `HandSwipeFuncProcedure` | `onPlayerTick` | NORMAL | 1 |
| 5 | `NetherseaWalkerExtraFuncProcedure` | `onPlayerTick` | NORMAL | 1 |
| 6 | `PlayerEvolutionTickFUncProcedure` | `onPlayerTick` | NORMAL | 1 |
| 7 | `PlayerTickFuncProcedure` | `onPlayerTick` | NORMAL | 1 |

**说明**: 以上 7 个类都监听同一个事件 `PlayerTickEvent`

---

### 2. LivingTickEvent (6 个类)

**完整事件类型**: `LivingEvent.LivingTickEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `ArrowTickFuncProcedure` | `onEntityTick` | NORMAL | 1 |
| 2 | `ChangeAttackGoalProcedure` | `onEntityTick` | NORMAL | 1 |
| 3 | `DefensiveModeFuncProcedure` | `onEntityTick` | NORMAL | 1 |
| 4 | `MobTickFuncProcedure` | `onEntityTick` | NORMAL | 1 |
| 5 | `SanityTickFuncProcedure` | `onEntityTick` | NORMAL | 1 |
| 6 | `SeabornAggresiveProcedure` | `onEntityTick` | NORMAL | 1 |

**说明**: 以上 6 个类都监听同一个事件 `LivingTickEvent`

---

### 3. EntityInteract (4 个类)

**完整事件类型**: `PlayerInteractEvent.EntityInteract`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `CatalystFuncProcedure` | `onRightClickEntity` | NORMAL | 1 |
| 2 | `FeedCandyProcedure` | `onRightClickEntity` | NORMAL | 1 |
| 3 | `RightClickFuncProcedure` | `onRightClickEntity` | NORMAL | 1 |
| 4 | `TransporterUseProcedure` | `onRightClickEntity` | NORMAL | 1 |

**说明**: 以上 4 个类都监听同一个事件 `EntityInteract`

---

### 4. BossEventProgress (1 个类)

**完整事件类型**: `BossEventProgress`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `CustomBossBarProcedure` | `customBossBar` | NORMAL | 1 |

---

### 5. ClientTickEvent (1 个类)

**完整事件类型**: `ClientTickEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `DisconcentrationFuncProcedure` | `onClientTick` | NORMAL | 1 |

---

### 6. LeftClickEmpty (3 个类)

**完整事件类型**: `PlayerInteractEvent.LeftClickEmpty`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `HighmoreScytheAirAttackProcedure` | `onLeftClick` | NORMAL | 2 |
| 2 | `PlayerLeftClickOnHelperProcedure` | `onLeftClick` | NORMAL | 2 |
| 3 | `RangedLightningProcedure` | `onLeftClick` | NORMAL | 2 |

**说明**: 以上 3 个类都监听同一个事件 `LeftClickEmpty`

---

### 7. Finish (1 个类)

**完整事件类型**: `Finish`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `PlayerEatFuncProcedure` | `onUseItemFinish` | NORMAL | 1 |

---

### 8. PlayerLoggedInEvent (1 个类)

**完整事件类型**: `PlayerEvent.PlayerLoggedInEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `PlayerLogInProcedure` | `onPlayerLoggedIn` | NORMAL | 1 |

---

### 9. PlayerRespawnEvent (1 个类)

**完整事件类型**: `PlayerEvent.PlayerRespawnEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `PlayerRebornProcedure` | `onPlayerRespawned` | NORMAL | 1 |

---

### 10. EntityPlaceEvent (1 个类)

**完整事件类型**: `BlockEvent.EntityPlaceEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `SummonOceanWitherProcedure` | `onBlockPlace` | NORMAL | 1 |

---

### 11. ItemCraftedEvent (1 个类)

**完整事件类型**: `PlayerEvent.ItemCraftedEvent`

| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |
|------|------|--------|--------|-------------------|
| 1 | `SyncNetherseaSwordProcedure` | `onItemCrafted` | NORMAL | 1 |

---

## 事件类型统计

| 事件类型 | 类数量 | 占比 |
|----------|--------|------|
| `PlayerTickEvent` | 7 | 25.9% |
| `LivingTickEvent` | 6 | 22.2% |
| `EntityInteract` | 4 | 14.8% |
| `BossEventProgress` | 1 | 3.7% |
| `ClientTickEvent` | 1 | 3.7% |
| `LeftClickEmpty` | 3 | 11.1% |
| `Finish` | 1 | 3.7% |
| `PlayerLoggedInEvent` | 1 | 3.7% |
| `PlayerRespawnEvent` | 1 | 3.7% |
| `EntityPlaceEvent` | 1 | 3.7% |
| `ItemCraftedEvent` | 1 | 3.7% |

---

## 完整类列表

| 序号 | 类名 | 事件类型 | 优先级 | 说明 |
|------|------|----------|--------|------|
| 1 | `ArmorEnchantFuncProcedure` | PlayerTickEvent | NORMAL | 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋� |
| 2 | `ArrowTickFuncProcedure` | LivingTickEvent | NORMAL | 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋� |
| 3 | `BanRElicFuncProcedure` | PlayerTickEvent | NORMAL | 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋� |
| 4 | `CatalystFuncProcedure` | EntityInteract | NORMAL | 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋� |
| 5 | `ChangeAttackGoalProcedure` | LivingTickEvent | NORMAL | 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋� |
| 6 | `CustomBossBarProcedure` | BossEventProgress | NORMAL | - |
| 7 | `DefensiveModeFuncProcedure` | LivingTickEvent | NORMAL | - |
| 8 | `DisconcentrationFuncProcedure` | ClientTickEvent | NORMAL | - |
| 9 | `EssenceResistanceWithIceProcedure` | PlayerTickEvent | NORMAL | 浜嬩欢澶勭悊鍣紝鏃犻渶閲嶆瀯 |
| 10 | `FeedCandyProcedure` | EntityInteract | NORMAL | 浜嬩欢澶勭悊鍣紝鏃犻渶閲嶆瀯 |
| 11 | `HandSwipeFuncProcedure` | PlayerTickEvent | NORMAL | 杩欐槸涓�涓簨浠跺鐞嗗櫒锛園SubscribeEvent锛夛紝鐩存帴鍝嶅簲 TickEvent.PlayerTickEvent 浜嬩欢 |
| 12 | `HighmoreScytheAirAttackProcedure` | LeftClickEmpty | NORMAL | 杩欐槸涓�涓簨浠跺鐞嗗櫒锛園SubscribeEvent锛夛紝鐩存帴鍝嶅簲 PlayerInteractEvent.LeftClickEmpty 浜嬩欢 |
| 13 | `MobTickFuncProcedure` | LivingTickEvent | NORMAL | - |
| 14 | `NetherseaWalkerExtraFuncProcedure` | PlayerTickEvent | NORMAL | - |
| 15 | `PlayerEatFuncProcedure` | Finish | NORMAL | - |
| 16 | `PlayerEvolutionTickFUncProcedure` | PlayerTickEvent | NORMAL | - |
| 17 | `PlayerLeftClickOnHelperProcedure` | LeftClickEmpty | NORMAL | - |
| 18 | `PlayerLogInProcedure` | PlayerLoggedInEvent | NORMAL | - |
| 19 | `PlayerRebornProcedure` | PlayerRespawnEvent | NORMAL | - |
| 20 | `PlayerTickFuncProcedure` | PlayerTickEvent | NORMAL | - |
| 21 | `RangedLightningProcedure` | LeftClickEmpty | NORMAL | - |
| 22 | `RightClickFuncProcedure` | EntityInteract | NORMAL | - |
| 23 | `SanityTickFuncProcedure` | LivingTickEvent | NORMAL | - |
| 24 | `SeabornAggresiveProcedure` | LivingTickEvent | NORMAL | - |
| 25 | `SummonOceanWitherProcedure` | EntityPlaceEvent | NORMAL | - |
| 26 | `SyncNetherseaSwordProcedure` | ItemCraftedEvent | NORMAL | - |
| 27 | `TransporterUseProcedure` | EntityInteract | NORMAL | - |

---

## 使用同一事件的类组详细分析

### 组 1: PlayerTickEvent

以下类都使用 `PlayerTickEvent` 事件：

- **ArmorEnchantFuncProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋�
- **BanRElicFuncProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋�
- **EssenceResistanceWithIceProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝鏃犻渶閲嶆瀯
- **HandSwipeFuncProcedure**
  - 说明: 杩欐槸涓�涓簨浠跺鐞嗗櫒锛園SubscribeEvent锛夛紝鐩存帴鍝嶅簲 TickEvent.PlayerTickEvent 浜嬩欢
- **NetherseaWalkerExtraFuncProcedure**
- **PlayerEvolutionTickFUncProcedure**
- **PlayerTickFuncProcedure**

### 组 2: LivingTickEvent

以下类都使用 `LivingTickEvent` 事件：

- **ArrowTickFuncProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋�
- **ChangeAttackGoalProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋�
- **DefensiveModeFuncProcedure**
- **MobTickFuncProcedure**
- **SanityTickFuncProcedure**
- **SeabornAggresiveProcedure**

### 组 3: EntityInteract

以下类都使用 `EntityInteract` 事件：

- **CatalystFuncProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝涓嶉渶瑕侀噸鏋�
- **FeedCandyProcedure**
  - 说明: 浜嬩欢澶勭悊鍣紝鏃犻渶閲嶆瀯
- **RightClickFuncProcedure**
- **TransporterUseProcedure**

### 组 4: LeftClickEmpty

以下类都使用 `LeftClickEmpty` 事件：

- **HighmoreScytheAirAttackProcedure**
  - 说明: 杩欐槸涓�涓簨浠跺鐞嗗櫒锛園SubscribeEvent锛夛紝鐩存帴鍝嶅簲 PlayerInteractEvent.LeftClickEmpty 浜嬩欢
- **PlayerLeftClickOnHelperProcedure**
- **RangedLightningProcedure**

