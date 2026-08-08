package com.susen36.caerulaarbor.client.renderer.block.listener;


import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.client.model.entity.ModelSealeatherChitinArmor;
import com.susen36.caerulaarbor.client.renderer.block.*;
import com.susen36.caerulaarbor.client.renderer.entity.ChitinComplexArmorRenderer;
import com.susen36.caerulaarbor.client.renderer.entity.KnightIronArmorRenderer;
import com.susen36.caerulaarbor.client.renderer.entity.TrailriteArmorArmorRenderer;
import com.susen36.caerulaarbor.client.renderer.entity.WearableCrownArmorRenderer;
import com.susen36.caerulaarbor.client.renderer.item.*;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(modid = CaerulaArbor.MODID)
public class ClientListener {
	private static final IClientMobEffectExtensions HIDDEN_EFFECT_EXTENSIONS = new IClientMobEffectExtensions() {
		@Override
		public boolean isVisibleInInventory(MobEffectInstance effect) {
			return false;
		}

		@Override
		public boolean renderInventoryText(MobEffectInstance instance, net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen<?> screen, net.minecraft.client.gui.GuiGraphics guiGraphics, int x, int y, int blitOffset) {
			return false;
		}

		@Override
		public boolean isVisibleInGui(MobEffectInstance effect) {
			return false;
		}
	};

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(CABlockEntities.TIDEWAY_CRADLE.get(), context -> new TidewayCradleTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CHESTMEGA_SPAWNER.get(), context -> new ChestmegaSpawnerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.VIVIPAROUS_LILY.get(), context -> new ViviparousLilyTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HUGE_LILY.get(), context -> new HugeLilyTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HIGHMORE_SPAWNBLOCK.get(), context -> new HighmoreSpawnblockTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CRISIS_TABLE.get(), context -> new CrisisTableTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.HIGHMORE_SPAWNING_BLOCK.get(), context -> new HighmoreSpawningBlockTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.MIZUKI_STATUE.get(), context -> new MizukiStatueTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.POCKET_SEA_DOLL.get(), context -> new PocketSeaDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.SWARMCALLER_DOLL.get(), context -> new SwarmcallerDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.STONECUTTER_DOLL.get(), context -> new StonecutterDollTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.ABANDONED_SULPTURE.get(), context -> new AbandonedSulptureTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.CENTRIFUGER.get(), context -> new CentrifugerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.ILLUSIONER_BANNER.get(), context -> new IllusionerBannerTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.LIVING_ARMORSTAND.get(), context -> new LivingArmorstandTileRenderer());
		event.registerBlockEntityRenderer(CABlockEntities.TRAILRITE_ARMORSTAND.get(), context -> new TrailriteArmorstandTileRenderer());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		MobEffect[] hiddenEffects = new MobEffect[] {
			CAMobEffects.HAEMOPHILIA.get(),
			CAMobEffects.KINGS_BREATH.get(),
			CAMobEffects.KINGS_BOOST.get(),
			CAMobEffects.SPEAR_FIGHT.get(),
			CAMobEffects.HANDS_SPEED.get(),
			CAMobEffects.BUTCHERS_POWER.get(),
			CAMobEffects.WIPE_DUSTS.get(),
			CAMobEffects.SACREFICE.get(),
			CAMobEffects.ENGRAVED_TRIUMPH.get(),
			CAMobEffects.FLAG_SWINGS.get(),
			CAMobEffects.KEEP_BEDDING.get(),
			CAMobEffects.SURVIVORS_GUIDE.get(),
			CAMobEffects.ADD_REACH.get(),
			CAMobEffects.TRAIL_BUFF.get(),
			CAMobEffects.TIDE_OF_CHITIN.get(),
			CAMobEffects.SANIDY_DEFENDER.get(),
			CAMobEffects.UNTAME_CONFIRM.get(),
			CAMobEffects.ROCK_BREAK.get(),
			CAMobEffects.POWER_OF_ANCHOR.get(),
			CAMobEffects.COOLDOWN_SINAL.get(),
			CAMobEffects.FLESHDEFORMITY.get(),
			CAMobEffects.BOOST_OF_SILENCE.get(),
			CAMobEffects.STRENGTH_OF_CROWD.get(),
			CAMobEffects.ANGER_OF_TIDE.get(),
			CAMobEffects.DEDUCT_ONE_SANITY.get(),
			CAMobEffects.FIRST_TELLER_SKILL.get(),
			CAMobEffects.PET_REAP.get(),
			CAMobEffects.RUNNING_ON_TRAIL.get(),
			CAMobEffects.ANGER_OF_BISHOP.get(),
			CAMobEffects.FAKE_DEATH.get(),
			CAMobEffects.HEMOSTATIC.get(),
			CAMobEffects.LESS_ATTACKSPEED.get(),
			CAMobEffects.COW_BUFF.get(),
			CAMobEffects.UNRIPE_THOUGHTS.get(),
			CAMobEffects.IZUMIK_LEARN.get(),
			CAMobEffects.IZUMIK_SHOCK.get(),
			CAMobEffects.INFANTRY.get(),
			CAMobEffects.GUIDED_EVO.get(),
			CAMobEffects.MAGIC_RESIS_BUFF.get(),
			CAMobEffects.ENDSPEAER_BRANDGUIDE_BUFF.get(),
			CAMobEffects.FLEXIBILITY_BUFF.get(),
			CAMobEffects.ADD_DEF_TINY.get(),
			CAMobEffects.ADD_DEF_PERCLY_TINY.get(),
			CAMobEffects.ADD_RESIS_TINY.get(),
			CAMobEffects.ADD_ATTACK_SPEED_TINY.get(),
			CAMobEffects.REDUCE_SANITY_MODIFIER.get(),
			CAMobEffects.ADD_MISS_RATE.get(),
			CAMobEffects.ADD_DAMAGE_TINY.get(),
			CAMobEffects.SANITY_HEAL.get()
		};
		for (MobEffect effect : hiddenEffects) {
			event.registerMobEffect(HIDDEN_EFFECT_EXTENSIONS, effect);
		}

		event.registerItem(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@SuppressWarnings("removal")
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (this.renderer == null)
					this.renderer = new ChitinComplexArmorRenderer();
				this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return this.renderer;
			}
		}, CAItems.COMPLEXCHITIN_ARMOR_HELMET.get(), CAItems.COMPLEXCHITIN_ARMOR_CHESTPLATE.get(), CAItems.COMPLEXCHITIN_ARMOR_LEGGINGS.get(), CAItems.COMPLEXCHITIN_ARMOR_BOOTS.get());

		event.registerItem(new IClientItemExtensions() {
			@Override
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
						Map.of("head", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).helmet, "hat",
								new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
								"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
								new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, CAItems.SEALEATHER_CHITIN_HELMET.get());

		event.registerItem(new IClientItemExtensions() {
			@Override
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
						Map.of("body", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chestplt, "left_arm",
							new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chesarmL, "right_arm",
							new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chestarmR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
								"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
								new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, CAItems.SEALEATHER_CHITIN_CHESTPLATE.get());

		event.registerItem(new IClientItemExtensions() {
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				HumanoidModel<?> armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
						Map.of("left_leg", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).legL, "right_leg",
							new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).legR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
								"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm",
								new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, CAItems.SEALEATHER_CHITIN_LEGGINGS.get());

		event.registerItem(new IClientItemExtensions() {
			@Override
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
						Map.of("left_leg", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).bootL, "right_leg",
							new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).bootR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
								"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm",
								new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, CAItems.SEALEATHER_CHITIN_BOOTS.get());

		event.registerItem(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@SuppressWarnings("removal")
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (this.renderer == null)
					this.renderer = new KnightIronArmorRenderer();
				this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return this.renderer;
			}
		}, CAItems.KNIGHT_IRON_HELMET.get(), CAItems.KNIGHT_IRON_CHESTPLATE.get(), CAItems.KNIGHT_IRON_LEGGINGS.get(), CAItems.KNIGHT_IRON_BOOTS.get());

		event.registerItem(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@SuppressWarnings("removal")
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (this.renderer == null)
					this.renderer = new WearableCrownArmorRenderer();
				this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return this.renderer;
			}
		}, CAItems.WEARABLE_CROWN_HELMET.get());

		event.registerItem(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@SuppressWarnings("removal")
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (this.renderer == null)
					this.renderer = new TrailriteArmorArmorRenderer();
				this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return this.renderer;
			}
		}, CAItems.TRAILRITE_ARMOR_HELMET.get(), CAItems.TRAILRITE_ARMOR_CHESTPLATE.get(), CAItems.TRAILRITE_ARMOR_LEGGINGS.get(), CAItems.TRAILRITE_ARMOR_BOOTS.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new CircularSawItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.CIRCULAR_SAW.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new HighmoreScytheItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.HIGHMORE_SCYTHE.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new LegendarySpearItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.LEGENDARY_SPEAR.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new MartusBookItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}

			@Override
			public boolean applyForgeHandTransform(com.mojang.blaze3d.vertex.PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				int i = arm == HumanoidArm.RIGHT ? 1 : -1;
				poseStack.translate(i * 0.56F, -0.52F, -0.72F);
				if (player.getUseItem() == itemInHand) {
					poseStack.translate(0.05, 0.05, 0.05);
				}
				return true;
			}
		}, CAItems.MARTUS_BOOK.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new WavecleaverItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.WAVECLEAVER.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new PhloemBowItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.PHLOEM_BOW.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new UninishedBeautyItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.UNFINISHED_BEAUTY.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new ViviparousLilyDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.VIVIPAROUS_LILY.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new TrailriteArmorstandDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.TRAILRITE_ARMORSTAND.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new TidewayCradleDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.TIDEWAY_CRADLE.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new SwarmcallerDollDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.SWARMCALLER_DOLL.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new StonecutterDollDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.STONECUTTER_DOLL.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new PocketSeaDollDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.POCKET_SEA_DOLL.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new MizukiStatueDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.MIZUKI_STATUE.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new LivingArmorstandDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.LIVING_ARMORSTAND.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new IllusionerBannerDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.ILLUSIONER_BANNER.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new HugeLilyDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.HUGE_LILY.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new HighmoreSpawningBlockDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.HIGHMORE_SPAWNING_BLOCK.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new HighmoreSpawnblockDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.HIGHMORE_SPAWNBLOCK.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new CrisisTableDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.CRISIS_TABLE.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new ChestmegaSpawnerDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.CHESTMEGA_SPAWNER.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new CentrifugerDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.CENTRIFUGER.get());

		event.registerItem(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new AbandonedSulptureDisplayItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		}, CAItems.ABANDONED_SULPTURE.get());
	}
}
