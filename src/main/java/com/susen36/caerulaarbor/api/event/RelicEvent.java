package com.susen36.caerulaarbor.api.event;

import com.susen36.caerulaarbor.capability.Relic;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

/**
 * 藏品变更事件：当玩家获得 / 更新 / 失去藏品时发布，供特效、UI、属性刷新等系统松耦合订阅。
 * <p>
 * 三个子事件：
 * <ul>
 *   <li>{@link Gain}   - 玩家获得藏品</li>
 *   <li>{@link Update} - 藏品数值更新</li>
 *   <li>{@link Remove} - 玩家失去藏品</li>
 * </ul>
 */
public class RelicEvent extends Event {

    public final Entity player;
    public final Relic relic;

    public RelicEvent(Entity player, Relic relic) {
        this.player = player;
        this.relic = relic;
    }

    public static class Gain extends RelicEvent {

        public Gain(Entity player, Relic relic) {
            super(player, relic);
        }
    }

    public static class Update extends RelicEvent {

        public Update(Entity player, Relic relic) {
            super(player, relic);
        }
    }

    public static class Remove extends RelicEvent {

        public Remove(Entity player, Relic relic) {
            super(player, relic);
        }
    }
}