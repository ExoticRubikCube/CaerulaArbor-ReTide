package com.apocalypse.caerulaarbor.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class PurchaseEnemyProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity enemy = null;
		enemy = entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
		if (!(enemy == null) && enemy.isAlive()) {
			if (entity.distanceTo(enemy) > 4) {
				{
                    entity.teleportTo((enemy.getX()), (enemy.getY()), (enemy.getZ()));
					if (entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.connection.teleport((enemy.getX()), (enemy.getY()), (enemy.getZ()), entity.getYRot(), entity.getXRot());
				}
			}
		}
	}
}

// TODO: 调用次数 = 6，副作用密集（瞬移），保持原样不重构
