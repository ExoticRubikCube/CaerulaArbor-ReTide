package com.susen36.caerulaarbor.relic;

/**
 * 收藏品的稀有度等级。
 * <p>
 * 共四级：诅咒 / 普通 / 稀有 / 高级。默认等级为 {@link #NORMAL}（普通）。
 * <p>
 * 游戏规则（{@code CAConfigs.RELIC_BAN}）仅禁用 {@link #ADVANCED}（高级）级藏品。
 * 级别在 CARelics 注册 RelicType 时设置，而非在 Relic 枚举中手动标记。
 */
public enum RelicTier {
    /** 诅咒：携带负面效果的诅咒类藏品。 */
    CURSED,
    /** 普通：默认级别，绝大多数藏品。 */
    NORMAL,
    /** 稀有：中高价值的稀有类藏品。 */
    RARE,
    /** 高级：被游戏规则 ban_advanced_relics 禁用的藏品。 */
    ADVANCED
}