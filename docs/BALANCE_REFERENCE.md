# Balance Reference (from current code)

This document lists gameplay/balance values **as implemented in code** (not design intent).

Generated: 2026-04-20

## Time scale (important for respawn/TTL)

- Many timers use `GameTimeManager.totalSeconds` which represents **in‑game seconds**.
- Code comments indicate the game runs at about **24 in‑game seconds per 1 real second** at `timeScale=1`. See:
  - `D:\jimina\Metro_project\rust_and_roots\src\controller\Left_mapController.java`
  - `D:\jimina\Metro_project\rust_and_roots\src\model\MobEntity.java`

## Mobs (currently implemented)

Defined runtime kinds: `SLIME`, `ORC` (`D:\jimina\Metro_project\rust_and_roots\src\model\MobEntity.java`).

Spawn/setup and base stats currently live in `D:\jimina\Metro_project\rust_and_roots\src\controller\Upper_mapController.java`.

### Common mob mechanics (Slime + Orc)

- **Daily growth catch‑up** (applies when the mob is being updated):
  - `deltaDays = currentDay - lastUpdatedDayNumber` (if positive).
  - `livedDays += deltaDays`, `lastUpdatedDayNumber = currentDay`.
- **Regen (HP + SP)**:
  - Per in‑game second: `(0.65 + vit*0.07) / 24` (both HP and SP), accumulated with carry.
  - See `applyRegenIfNeeded` in `D:\jimina\Metro_project\rust_and_roots\src\model\MobEntity.java`.
- **Attack**
  - Can only start an attack if `sp > 0`.
  - Starting an attack consumes `1 SP`.
  - Damage to player per hit: `max(1, mobStr - playerVit)`.
  - If the hit kills the player, the mob receives a **kill bonus** stat increase (see below).
- **Drops expiry**
  - Mob drops expire after **12 in‑game hours**: `MOB_DROP_TTL_SECONDS = 12*60*60`.
  - Drops are stored in `MobMapState.MobData.drops`.

### Slime

**Base stats on first setup** (`Upper_mapController.initializeMobDefaultsIfNeeded`):

| Stat | Value |
|---|---:|
| Max HP | 10 |
| Max SP | 30 |
| STR | 3 |
| AGI | 2 |
| VIT | 6 |
| DEX | 1 |

**Daily growth (per in‑game day delta):** (`MobEntity.applyDailyGrowthIfNeeded`)

- `STR += delta`
- `AGI += delta`
- `maxSP += delta`
- `SP += delta` (capped to new `maxSP`)

**Kill bonus weights (when slime kills player):** (`MobEntity.chooseKillBonusStat`)

- HP +1: 24%
- SP +1: 21%
- STR +1: 24%
- AGI +1: 20%
- VIT +1: 10%
- DEX +1: 1%

**Drop on death (killed by player):** (`Upper_mapController.handleMobKilledByPlayer`)

- Always drops **`sand` x1** (if item exists; fallback is also `sand`).

**Player raw EXP gain on kill:** (`Upper_mapController.computeExpGain`)

`exp = max(0, (mobSTR+mobAGI+mobVIT+mobDEX) - (playerSTR+playerAGI+playerVIT+playerDEX) + 1)`

### Orc

**Base stats on first setup** (`Upper_mapController.initializeMobDefaultsIfNeeded`):

| Stat | Value |
|---|---:|
| Max HP | 20 |
| Max SP | 25 |
| STR | 4 |
| AGI | 3 |
| VIT | 3 |
| DEX | 1 |

**Daily growth (per in‑game day delta):** (`MobEntity.applyDailyGrowthIfNeeded`)

- `STR += delta`
- `VIT += delta`
- `maxHP += delta`
- `HP += delta` (capped to new `maxHP`)

**Kill bonus weights (when orc kills player):** (`MobEntity.chooseKillBonusStat`)

- HP +1: 23%
- SP +1: 23%
- STR +1: 22%
- AGI +1: 15%
- VIT +1: 15%
- DEX +1: 2%

**Drop on death (killed by player):** (`Upper_mapController.chooseOrcLootItemId`)

Weighted pick (only counts items that exist in the DB):

| Item | Weight | Approx chance (if all 3 exist) |
|---|---:|---:|
| `stringy_meat` | 70 | 70% |
| `small_meat_chunk` | 25 | 25% |
| `Steak` | 5 | 5% |

If no valid orc loot item exists, it falls back to `sand`.

**Player raw EXP gain on kill:** same formula as Slime.

### Mob respawn

No mob respawn timer/logic is present in current code (searching `Upper_mapController` + `MobEntity` shows death is final).

## Tree + Stone resources (Left_map)

Tree/Stone persistent state is stored in `TreeMapState.TreeData` (`D:\jimina\Metro_project\rust_and_roots\src\model\TreeMapState.java`).

Runtime logic lives in `D:\jimina\Metro_project\rust_and_roots\src\controller\Left_mapController.java`.

### Shared resource model (TreeData)

Stages:

- **Stage 1**: removed/empty, waiting to respawn.
- **Stage 2**: “tree1” / stone.
- **Stage 3**: “tree2” (tree only; stone still uses stage values but visuals always stone).

Stage is derived from lived days:

- `stage = (livedDays >= 1) ? 3 : 2`

Vit grows with lived days:

- `vit = max(1, livedDays + 1)`

### Respawn + TTL values

- **Respawn time** (stage 1 → stage 2): `12 hours` in‑game.
  - `TREE_RESPAWN_SECONDS = 12*60*60`
- **Drop TTL**: `12 hours` in‑game.
  - `TREE_DROP_TTL_SECONDS = 12*60*60`

### HP regeneration and max‑HP growth

Growth uses in‑game seconds (`totalSeconds`):

- If `hp < maxHp`: regenerate `+1 HP` per `48 in‑game seconds`.
  - `TREE_HP_REGEN_SECONDS = 48`
- If `hp == maxHp`: increase `maxHp` by `+1` per `1440 in‑game seconds` and keep HP full.
  - `TREE_HP_CAP_GROW_SECONDS = 1440`

### Tree (MAIN_TREE_ID = "tree")

**Initial/default state** (when missing/invalid):

- `typeKey`: random valid tree type key (see list below)
- `currentStage`: stage derived from `livedDays` (defaults to stage 2)
- `livedDays`: 0
- `vit`: 1
- `maxHp`: at least 10
- `hp`: defaults to `maxHp`
- `dropsThisLife`: 0
- `dropRatePity`: 0
- `damageSinceLastDropRoll`: 0..9 (mod 10)

**Tree type keys (currently present in `New.sql`):**

- `banana`, `blue`, `coconut`, `red`, `yellow`

These correspond to items like:

- `${type}_tree1`, `${type}_tree2`, `${type}_fruit`

**Player damage to tree** (`applyPlayerAttackToResource`):

- `baseDamage = playerSTR + (axeEquipped ? 1 : 0)`
- `receivedDamage = max(0, baseDamage - treeVit)`
- `hp -= min(receivedDamage, hp)`

**Drops**

Per 10 HP lost (`TREE_DROP_ROLL_HP_CHUNK = 10`), a roll occurs (`applyResourceDropRollsForDamage`):

- **If HP after the chunk is `< 20`**
  - Always: `tree_log` x1
  - 49%: `plant_fiber` x1
  - No alive‑drop rolls in this low‑HP zone
- **If HP after the chunk is `>= 20`**
  - 49%: `plant_fiber` x1
  - Alive‑drop roll (limited by caps):
    - `rollTreeDropNumber` returns `0` (no progress) or `1..10`.
    - If roll = `10`: spawn alive drop immediately.
    - If roll in `1..9`: add to `dropRatePity`; when `dropRatePity >= 10`, spawn alive drop.
    - “Zero” roll chance depends on `playerDEX` vs `treeVIT`:
      - baseline zero‑weight is 10 (≈9.09% at equal weights)
      - decreases as DEX exceeds VIT
      - becomes 0 when `DEX > VIT*2`
    - Each of `1..10` has equal weight.
  - Alive‑drop item:
    - Stage 2: always `tree_log`
    - Stage 3: 50% `tree_log`, 50% `${typeKey}_fruit`

Drop caps:

- Per life total drops: `TREE_MAX_DROPS_PER_LIFE = 10`
- Alive‑drop cap:
  - Stage 2: `TREE_STAGE2_MAX_ALIVE_DROPS = 9`
  - Stage 3: `TREE_STAGE3_MAX_ALIVE_DROPS = 8`

### Stone (MAIN_STONE_ID = "stone")

**Initial/default state**

- `typeKey`: `"stone"`
- `currentStage`: stage derived from `livedDays` (defaults to stage 2)
- `livedDays`: 0
- `vit`: 1
- `maxHp`: at least 10
- `hp`: defaults to `maxHp`

**Player damage to stone**

- `baseDamage = playerSTR + (pickaxeEquipped ? 1 : 0)`
- `receivedDamage = max(0, baseDamage - stoneVit)`

**Drops** (same roll system, different items)

Per 10 HP lost:

- Common drop: `stone` x1
- Uncommon drop: `sand` x1 with **49%** chance
- Alive‑drop item: `stone` (subject to the same drop caps)

### Resource respawn result (after stage 1)

After respawn (12 in‑game hours later):

- Tree:
  - stage 2
  - `livedDays = 0`, `vit = 1`
  - `hp = 10`, `maxHp = 10`
  - `typeKey = randomTreeTypeKey()`
  - drop counters reset to 0
- Stone:
  - stage 2
  - `livedDays = 0`, `vit = 1`
  - `hp = 10`, `maxHp = 10`
  - `typeKey = "stone"`
  - drop counters reset to 0

### Note: unused “guaranteed death drops”

`Left_mapController` contains helper methods:

- `applyGuaranteedDeathDrops(...)`
- `applyGuaranteedStoneDeathDrops(...)`
- `computePlantFiberDropChancePercent(...)`

They are **not called** from the current kill/respawn flow, so they do **not** affect live drop rates.

## Player status, caps, costs, skills, EXP

### New game starting values

Set on new game creation (`D:\jimina\Metro_project\rust_and_roots\src\controller\New_gameController.java`):

- Max HP: 45 (HP filled to max)
- Max Stamina: 50 (Stamina filled to max)
- STR/VIT/DEX/AGI: 1
- Starting raw EXP: 100

New game coins + chest bonus (`D:\jimina\Metro_project\rust_and_roots\src\game\GameState.java`):

- Starting coins: 100
- Starter chest items: `watering_can` x1, `sickle` x1 (if those items exist)

### Stat caps (“growth limit”)

Core stat cap depends on **in‑game day number** (`BaseMapController.getCurrentCoreStatCap`):

- Day 1–5: cap 10
- Day 6–10: cap 20
- Day 11+: cap 30

Core stats using this cap: `STR`, `VIT`, `DEX`, `AGI`.

HP/SP (Max HP / Max Stamina) use a separate cap (`BaseMapController.getHpSpPointCap`):

- `hpSpCap = averageCoreStatWithPending + coreCap`
- Invested points:
  - HP invested points = `maxHp - 45`
  - SP invested points = `maxStamina - 50`
- Both HP and SP are capped by the same `hpSpCap`.

### Stat increase cost (raw EXP)

All stat upgrades spend **player raw EXP** (`BaseMapController.getPendingExpCostForStat`).

Baseline (“committed increases”) assumes:

- HP baseline = 45
- SP baseline = 50
- STR/VIT/DEX/AGI baseline = 1

Cost per point increases every 10 committed points:

- Cost for the **next** point:
  - `nextCost = 1 + floor((committedPoints + pendingPoints) / 10)`
- Total cost for buying `pendingPoints` at once:
  - sum of the per‑point costs for each pending point.

Practical meaning:

- Points 1–10 cost 1 EXP each
- Points 11–20 cost 2 EXP each
- Points 21–30 cost 3 EXP each
- …and so on.

### Skills

Skill data container: `GameState.PlayerSkills` (`D:\jimina\Metro_project\rust_and_roots\src\game\GameState.java`).

Max skill level:

- `MAX_LEVEL = 10`

#### Skill “level up cost” (total EXP needed per level)

The EXP required to advance from level `L` to `L+1` is:

- `needed(L) = round((4*(L+1))^2.1) - round((4*L)^2.1)` (minimum 1)

Table (computed with the same formula used in code):

| Level → Next | EXP needed | Total EXP to reach next level |
|---:|---:|---:|
| 0 → 1 | 18 | 18 |
| 1 → 2 | 61 | 79 |
| 2 → 3 | 106 | 185 |
| 3 → 4 | 153 | 338 |
| 4 → 5 | 202 | 540 |
| 5 → 6 | 251 | 791 |
| 6 → 7 | 303 | 1094 |
| 7 → 8 | 354 | 1448 |
| 8 → 9 | 407 | 1855 |
| 9 → 10 | 459 | 2314 |

#### Manual skill EXP allocation cost

In the status/skills UI, you can convert raw EXP into skill EXP (`BaseMapController.applyPendingSkillChanges`):

- **1 raw EXP spent = 1 skill EXP added**
- You cannot allocate beyond level 10.

#### Skill EXP gain from actions (implemented)

- Farming:
  - Each harvested plot (sickle action) grants `+1 Farming EXP` on farm commit.
  - See `pendingFarmingExp++` in `D:\jimina\Metro_project\rust_and_roots\src\controller\Farm_tableController.java`.
- Crafting and Cooking:
  - Each craft/cook action grants:
    - `skillExp = base * (success ? 2 : 1)`
    - `base = clamp(ingredientsCount, 1..4)`
  - See:
    - `D:\jimina\Metro_project\rust_and_roots\src\controller\CraftController.java`
    - `D:\jimina\Metro_project\rust_and_roots\src\controller\CookController.java`
    - `D:\jimina\Metro_project\rust_and_roots\src\controller\ProcessingRules.java`
- Recipe items (using a recipe item for a recipe you already unlocked):
  - Grants `+min(3, max(1, ingredientsCount))` skill EXP to that recipe’s domain.
  - See `BaseMapController.tryUnlockRecipeItem`.

#### Cooking/Crafting skill EXP cap + overflow → raw EXP

Cooking and Crafting skill EXP are capped at the exact total required to reach level 10 (`2314` total EXP).

If an action would push these skills past level 10:

- The extra (“overflow”) is converted to **player raw EXP**.
- See `GameState.addRecipeSkillExp`.

(This overflow conversion does **not** exist for Farming/Gathering/Fishing/Battling in current code.)

### Raw EXP gains (implemented)

Raw EXP is the value shown/consumed by stat + skill allocation.

Current sources:

- **Mob kills** (`Upper_mapController.computeExpGain`):
  - `exp = max(0, mobCoreTotal - playerCoreTotal + 1)`
  - where `coreTotal = STR + AGI + VIT + DEX`
- **Smelt completion** (`GameState.applySmeltCompletionRewards`):
  - Each completed smelt adds `1.0 * multiplier` to an EXP carry.
  - You receive raw EXP when the carry reaches the next integer (floor).
  - Multiplier by attempt number (attempt = completedCount+1):
    - Attempt 1: `1.0`
    - Attempt 2–9: `0.5`
    - Attempt 10–19: `0.25`
    - Attempt 20–29: `0.125`
    - …halves every 10 attempts after 10
- **Cooking/Crafting overflow** when already at max skill EXP:
  - Extra skill EXP becomes raw EXP (see “overflow” note above).

### Stamina regeneration and running drain

Implemented in `D:\jimina\Metro_project\rust_and_roots\src\model\PlayerState.java`.

- Base stamina regen per second:
  - `0.65 + vit*0.07`
- Regen multipliers by mode:
  - Sleep: `1.00`
  - Idle: `1.00`
  - Walk: `0.55`
  - Run: `0.20`
- Running stamina drain per second:
  - `1.35 + max(0, 0.75 - vit*0.05)`
- While not sleeping, regen is limited by a “mode regen cap”:
  - Idle: up to `0.90 * maxStamina`
  - Walk: up to `0.80 * maxStamina`
  - Run: up to `0.70 * maxStamina`
  - Sleep: up to `1.00 * maxStamina`

