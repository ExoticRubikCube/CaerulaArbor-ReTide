package com.apocalypse.caerulaarbor.network;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.network.message.receive.PlayerVariablesSyncMessage;
import com.apocalypse.caerulaarbor.network.message.receive.SavedDataSyncMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CaerulaArborModVariables {
	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(PlayerVariables.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide())
				event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level().isClientSide())
				event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide())
				event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			PlayerVariables original = event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
			PlayerVariables clone = event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
			clone.player_light = original.player_light;
			clone.player_lives = original.player_lives;
			clone.player_maxlive = original.player_maxlive;
			clone.player_shield = original.player_shield;
			clone.disoclusion = original.disoclusion;
			clone.show_stats = original.show_stats;
			clone.relic_cursed_EMELIGHT = original.relic_cursed_EMELIGHT;
			clone.relic_cursed_GLOWBODY = original.relic_cursed_GLOWBODY;
			clone.relic_cursed_RESEARCH = original.relic_cursed_RESEARCH;
			clone.relic_king_CROWN = original.relic_king_CROWN;
			clone.relic_king_ARMOR = original.relic_king_ARMOR;
			clone.relic_king_SPEAR = original.relic_king_SPEAR;
			clone.relic_king_EXTENSION = original.relic_king_EXTENSION;
			clone.kingShowPtc = original.kingShowPtc;
			clone.relic_king_CRYSTAL = original.relic_king_CRYSTAL;
			clone.relic_hand_THORNS = original.relic_hand_THORNS;
			clone.relic_hand_STRANGLE = original.relic_hand_STRANGLE;
			clone.relic_hand_FERTILITY = original.relic_hand_FERTILITY;
			clone.relic_hand_SPEED = original.relic_hand_SPEED;
			clone.relic_hand_BARREN = original.relic_hand_BARREN;
			clone.relic_hand_SWIPE = original.relic_hand_SWIPE;
			clone.relic_archfi_ARTIFACT = original.relic_archfi_ARTIFACT;
			clone.relic_hand_FIREWORK = original.relic_hand_FIREWORK;
			clone.relic_archfi_FLAG = original.relic_archfi_FLAG;
			clone.relic_hand_ENGRAVE = original.relic_hand_ENGRAVE;
			clone.relic_archfi_BED = original.relic_archfi_BED;
			clone.relic_SURVIVOR = original.relic_SURVIVOR;
			clone.relic_TREATY = original.relic_TREATY;
			clone.relic_archifi_RYLFATE = original.relic_archifi_RYLFATE;
			clone.relic_util_MEATCAN = original.relic_util_MEATCAN;
			clone.relic_util_SEAGRASS = original.relic_util_SEAGRASS;
			clone.relic_util_ORANGE = original.relic_util_ORANGE;
			clone.relic_util_COFFEE = original.relic_util_COFFEE;
			clone.relic_util_BERRIES = original.relic_util_BERRIES;
			clone.player_util_RAINBOW = original.player_util_RAINBOW;
			clone.player_util_AROMATIC = original.player_util_AROMATIC;
			clone.relic_util_MUSICBOX = original.relic_util_MUSICBOX;
			clone.relic_util_IRIS = original.relic_util_IRIS;
			clone.relic_util_FLUTE = original.relic_util_FLUTE;
			clone.relic_util_VOYGOLD = original.relic_util_VOYGOLD;
			clone.relic_util_DURIN = original.relic_util_DURIN;
			clone.relic_util_TOPONYM = original.relic_util_TOPONYM;
			clone.relic_util_KETTLE = original.relic_util_KETTLE;
			clone.relic_legend_CHITIN = original.relic_legend_CHITIN;
			clone.relic_util_ALLEY = original.relic_util_ALLEY;
			clone.relic_util_BATBED = original.relic_util_BATBED;
			clone.relic_util_LONGEVITY = original.relic_util_LONGEVITY;
			clone.relic_util_OMNIKEY = original.relic_util_OMNIKEY;
			clone.relic_util_score = original.relic_util_score;
			clone.relic_util_RESCISSION = original.relic_util_RESCISSION;
			clone.relic_util_STARE = original.relic_util_STARE;
			clone.relic_hand_SWORD = original.relic_hand_SWORD;
			clone.relic_util_ALLAY = original.relic_util_ALLAY;
			clone.relic_util_RAINBOW = original.relic_util_RAINBOW;
			clone.relic_diso = original.relic_diso;
			clone.relic_diso_FLESH = original.relic_diso_FLESH;
			clone.relic_diso_BLOOD = original.relic_diso_BLOOD;
			clone.relic_diso_NEURO = original.relic_diso_NEURO;
			clone.relic_ahnd_SWIPE = original.relic_ahnd_SWIPE;
			clone.relic_diso_ATTENTION = original.relic_diso_ATTENTION;
			clone.relic_hanshand_SPIKE = original.relic_hanshand_SPIKE;
			clone.relic_royalfate = original.relic_royalfate;
			clone.player_king_suit = original.player_king_suit;
			clone.player_demon_suit = original.player_demon_suit;
			clone.player_oceanization = original.player_oceanization;
			clone.relic_cursed_HEART = original.relic_cursed_HEART;
			clone.relic_HEMOST = original.relic_HEMOST;
			clone.relic_YEARNING = original.relic_YEARNING;
			clone.plauyer_balance = original.plauyer_balance;
			clone.can_player_evo = original.can_player_evo;
			clone.reserve_quantity = original.reserve_quantity;
			clone.reserve_quality = original.reserve_quality;
			clone.PEVO_NEXUS_no_rejection = original.PEVO_NEXUS_no_rejection;
			clone.PEVO_NEXUS_reg_sanity = original.PEVO_NEXUS_reg_sanity;
			clone.PEVO_NODE_add_def = original.PEVO_NODE_add_def;
			clone.PEVO_NODE_add_resis = original.PEVO_NODE_add_resis;
			clone.PEVO_NODE_add_speed = original.PEVO_NODE_add_speed;
			clone.PEVO_NODE_add_sanity = original.PEVO_NODE_add_sanity;
			clone.PEVO_NEXUS_reg_lights = original.PEVO_NEXUS_reg_lights;
			clone.PEVO_NODE_add_damage = original.PEVO_NODE_add_damage;
			clone.PEVO_NODE_less_damage = original.PEVO_NODE_less_damage;
			clone.PEVO_NODE_living_barrier = original.PEVO_NODE_living_barrier;
			clone.PEVO_NODE_add_miss = original.PEVO_NODE_add_miss;
			clone.PEVO_NEXUS_perc_damage = original.PEVO_NEXUS_perc_damage;
			clone.PEVO_NODE_real_damage = original.PEVO_NODE_real_damage;
			clone.PEVO_NODE_heal_damage = original.PEVO_NODE_heal_damage;
			clone.PEVO_NODE_worse_break = original.PEVO_NODE_worse_break;
			clone.PEVO_NEXUS_expo_shield = original.PEVO_NEXUS_expo_shield;
			clone.PEVO_NODE_eunectes = original.PEVO_NODE_eunectes;
			clone.PEVO_NODE_less_armor = original.PEVO_NODE_less_armor;
			if (!event.isWasDeath()) {
				clone.chitin_knife_selected = original.chitin_knife_selected;
			}
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "caerula_arbor_worldvars";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		public static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "caerula_arbor_mapvars";
		public double evo_point_grow = 0;
		public double evo_point_subsisting = 0;
		public double evo_point_breed = 0;
		public double evo_point_migration = 0;
		public double strategy_grow = 0;
		public double strategy_subsisting = 0;
		public double strategy_breed = 0.0;
		public double strategy_migration = 0;
		public double strategy_silence = 0;
		public double evo_point_silence = 0;
		public boolean silence_enabled = false;
		public double endspeaker_abolities = 0;
		public boolean endspeakerSummon = true;
		public double incandescentAnimaUseTick = 0;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			evo_point_grow = nbt.getDouble("evo_point_grow");
			evo_point_subsisting = nbt.getDouble("evo_point_subsisting");
			evo_point_breed = nbt.getDouble("evo_point_breed");
			evo_point_migration = nbt.getDouble("evo_point_migration");
			strategy_grow = nbt.getDouble("strategy_grow");
			strategy_subsisting = nbt.getDouble("strategy_subsisting");
			strategy_breed = nbt.getDouble("strategy_breed");
			strategy_migration = nbt.getDouble("strategy_migration");
			strategy_silence = nbt.getDouble("strategy_silence");
			evo_point_silence = nbt.getDouble("evo_point_silence");
			silence_enabled = nbt.getBoolean("silence_enabled");
			endspeaker_abolities = nbt.getDouble("endspeaker_abolities");
			endspeakerSummon = nbt.getBoolean("endspeakerSummon");
			incandescentAnimaUseTick = nbt.getDouble("incandescentAnimaUseTick");
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putDouble("evo_point_grow", evo_point_grow);
			nbt.putDouble("evo_point_subsisting", evo_point_subsisting);
			nbt.putDouble("evo_point_breed", evo_point_breed);
			nbt.putDouble("evo_point_migration", evo_point_migration);
			nbt.putDouble("strategy_grow", strategy_grow);
			nbt.putDouble("strategy_subsisting", strategy_subsisting);
			nbt.putDouble("strategy_breed", strategy_breed);
			nbt.putDouble("strategy_migration", strategy_migration);
			nbt.putDouble("strategy_silence", strategy_silence);
			nbt.putDouble("evo_point_silence", evo_point_silence);
			nbt.putBoolean("silence_enabled", silence_enabled);
			nbt.putDouble("endspeaker_abolities", endspeaker_abolities);
			nbt.putBoolean("endspeakerSummon", endspeakerSummon);
			nbt.putDouble("incandescentAnimaUseTick", incandescentAnimaUseTick);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		public static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() {
	});

	@Mod.EventBusSubscriber
	private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
				event.addCapability(new ResourceLocation(CaerulaArborMod.MODID, "player_variables"), new PlayerVariablesProvider());
		}

		private final PlayerVariables playerVariables = new PlayerVariables();
		private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return playerVariables.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			playerVariables.readNBT(nbt);
		}
	}

	public static class PlayerVariables {
		public double player_light = 100.0;
		public double player_lives = 6.0;
		public double player_maxlive = 6.0;
		public double player_shield = 0;
		public double disoclusion = 0;
		public boolean show_stats = true;
		public boolean relic_cursed_EMELIGHT = false;
		public boolean relic_cursed_GLOWBODY = false;
		public boolean relic_cursed_RESEARCH = false;
		public boolean relic_king_CROWN = false;
		public boolean relic_king_ARMOR = false;
		public boolean relic_king_SPEAR = false;
		public boolean relic_king_EXTENSION = false;
		public boolean kingShowPtc = true;
		public boolean relic_king_CRYSTAL = false;
		public boolean relic_hand_THORNS = false;
		public boolean relic_hand_STRANGLE = false;
		public boolean relic_hand_FERTILITY = false;
		public boolean relic_hand_SPEED = false;
		public boolean relic_hand_BARREN = false;
		public boolean relic_hand_SWIPE = false;
		public boolean relic_archfi_ARTIFACT = false;
		public boolean relic_hand_FIREWORK = false;
		public boolean relic_archfi_FLAG = false;
		public double relic_hand_ENGRAVE = -1.0;
		public boolean relic_archfi_BED = false;
		public double relic_SURVIVOR = -1.0;
		public boolean relic_TREATY = false;
		public boolean relic_archifi_RYLFATE = false;
		public boolean relic_util_MEATCAN = false;
		public boolean relic_util_SEAGRASS = false;
		public boolean relic_util_ORANGE = false;
		public boolean relic_util_COFFEE = false;
		public boolean relic_util_BERRIES = false;
		public boolean player_util_RAINBOW = false;
		public boolean player_util_AROMATIC = false;
		public boolean relic_util_MUSICBOX = false;
		public boolean relic_util_IRIS = false;
		public boolean relic_util_FLUTE = false;
		public boolean relic_util_VOYGOLD = false;
		public boolean relic_util_DURIN = false;
		public boolean relic_util_TOPONYM = false;
		public boolean relic_util_KETTLE = false;
		public boolean relic_legend_CHITIN = false;
		public ItemStack chitin_knife_selected = ItemStack.EMPTY;
		public boolean relic_util_ALLEY = false;
		public boolean relic_util_BATBED = false;
		public boolean relic_util_LONGEVITY = false;
		public boolean relic_util_OMNIKEY = false;
		public boolean relic_util_score = false;
		public boolean relic_util_RESCISSION = false;
		public boolean relic_util_STARE = false;
		public boolean relic_hand_SWORD = false;
		public boolean relic_util_ALLAY = false;
		public boolean relic_util_RAINBOW = false;
		public boolean relic_diso = false;
		public boolean relic_diso_FLESH = false;
		public boolean relic_diso_BLOOD = false;
		public boolean relic_diso_NEURO = false;
		public boolean relic_ahnd_SWIPE = false;
		public boolean relic_diso_ATTENTION = false;
		public boolean relic_hanshand_SPIKE = false;
		public boolean relic_royalfate = false;
		public double player_king_suit = 0;
		public double player_demon_suit = 0;
		public double player_oceanization = 0;
		public boolean relic_cursed_HEART = false;
		public boolean relic_HEMOST = false;
		public boolean relic_YEARNING = false;
		public double plauyer_balance = 0;
		public boolean can_player_evo = false;
		public double reserve_quantity = 0;
		public double reserve_quality = 0;
		public boolean PEVO_NEXUS_no_rejection = false;
		public boolean PEVO_NEXUS_reg_sanity = false;
		public double PEVO_NODE_add_def = 0;
		public double PEVO_NODE_add_resis = 0;
		public double PEVO_NODE_add_speed = 0;
		public double PEVO_NODE_add_sanity = 0;
		public boolean PEVO_NEXUS_reg_lights = false;
		public double PEVO_NODE_add_damage = 0;
		public double PEVO_NODE_less_damage = 0;
		public double PEVO_NODE_living_barrier = 0;
		public double PEVO_NODE_add_miss = 0;
		public boolean PEVO_NEXUS_perc_damage = false;
		public double PEVO_NODE_real_damage = 0;
		public double PEVO_NODE_heal_damage = 0;
		public double PEVO_NODE_worse_break = 0;
		public boolean PEVO_NEXUS_expo_shield = false;
		public double PEVO_NODE_eunectes = 0;
		public double PEVO_NODE_less_armor = 0;

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				CaerulaArborModNetwork.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PlayerVariablesSyncMessage(this));
		}

		public Tag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putDouble("player_light", player_light);
			nbt.putDouble("player_lives", player_lives);
			nbt.putDouble("player_maxlive", player_maxlive);
			nbt.putDouble("player_shield", player_shield);
			nbt.putDouble("disoclusion", disoclusion);
			nbt.putBoolean("show_stats", show_stats);
			nbt.putBoolean("relic_cursed_EMELIGHT", relic_cursed_EMELIGHT);
			nbt.putBoolean("relic_cursed_GLOWBODY", relic_cursed_GLOWBODY);
			nbt.putBoolean("relic_cursed_RESEARCH", relic_cursed_RESEARCH);
			nbt.putBoolean("relic_king_CROWN", relic_king_CROWN);
			nbt.putBoolean("relic_king_ARMOR", relic_king_ARMOR);
			nbt.putBoolean("relic_king_SPEAR", relic_king_SPEAR);
			nbt.putBoolean("relic_king_EXTENSION", relic_king_EXTENSION);
			nbt.putBoolean("kingShowPtc", kingShowPtc);
			nbt.putBoolean("relic_king_CRYSTAL", relic_king_CRYSTAL);
			nbt.putBoolean("relic_hand_THORNS", relic_hand_THORNS);
			nbt.putBoolean("relic_hand_STRANGLE", relic_hand_STRANGLE);
			nbt.putBoolean("relic_hand_FERTILITY", relic_hand_FERTILITY);
			nbt.putBoolean("relic_hand_SPEED", relic_hand_SPEED);
			nbt.putBoolean("relic_hand_BARREN", relic_hand_BARREN);
			nbt.putBoolean("relic_hand_SWIPE", relic_hand_SWIPE);
			nbt.putBoolean("relic_archfi_ARTIFACT", relic_archfi_ARTIFACT);
			nbt.putBoolean("relic_hand_FIREWORK", relic_hand_FIREWORK);
			nbt.putBoolean("relic_archfi_FLAG", relic_archfi_FLAG);
			nbt.putDouble("relic_hand_ENGRAVE", relic_hand_ENGRAVE);
			nbt.putBoolean("relic_archfi_BED", relic_archfi_BED);
			nbt.putDouble("relic_SURVIVOR", relic_SURVIVOR);
			nbt.putBoolean("relic_TREATY", relic_TREATY);
			nbt.putBoolean("relic_archifi_RYLFATE", relic_archifi_RYLFATE);
			nbt.putBoolean("relic_util_MEATCAN", relic_util_MEATCAN);
			nbt.putBoolean("relic_util_SEAGRASS", relic_util_SEAGRASS);
			nbt.putBoolean("relic_util_ORANGE", relic_util_ORANGE);
			nbt.putBoolean("relic_util_COFFEE", relic_util_COFFEE);
			nbt.putBoolean("relic_util_BERRIES", relic_util_BERRIES);
			nbt.putBoolean("player_util_RAINBOW", player_util_RAINBOW);
			nbt.putBoolean("player_util_AROMATIC", player_util_AROMATIC);
			nbt.putBoolean("relic_util_MUSICBOX", relic_util_MUSICBOX);
			nbt.putBoolean("relic_util_IRIS", relic_util_IRIS);
			nbt.putBoolean("relic_util_FLUTE", relic_util_FLUTE);
			nbt.putBoolean("relic_util_VOYGOLD", relic_util_VOYGOLD);
			nbt.putBoolean("relic_util_DURIN", relic_util_DURIN);
			nbt.putBoolean("relic_util_TOPONYM", relic_util_TOPONYM);
			nbt.putBoolean("relic_util_KETTLE", relic_util_KETTLE);
			nbt.putBoolean("relic_legend_CHITIN", relic_legend_CHITIN);
			nbt.put("chitin_knife_selected", chitin_knife_selected.save(new CompoundTag()));
			nbt.putBoolean("relic_util_ALLEY", relic_util_ALLEY);
			nbt.putBoolean("relic_util_BATBED", relic_util_BATBED);
			nbt.putBoolean("relic_util_LONGEVITY", relic_util_LONGEVITY);
			nbt.putBoolean("relic_util_OMNIKEY", relic_util_OMNIKEY);
			nbt.putBoolean("relic_util_score", relic_util_score);
			nbt.putBoolean("relic_util_RESCISSION", relic_util_RESCISSION);
			nbt.putBoolean("relic_util_STARE", relic_util_STARE);
			nbt.putBoolean("relic_hand_SWORD", relic_hand_SWORD);
			nbt.putBoolean("relic_util_ALLAY", relic_util_ALLAY);
			nbt.putBoolean("relic_util_RAINBOW", relic_util_RAINBOW);
			nbt.putBoolean("relic_diso", relic_diso);
			nbt.putBoolean("relic_diso_FLESH", relic_diso_FLESH);
			nbt.putBoolean("relic_diso_BLOOD", relic_diso_BLOOD);
			nbt.putBoolean("relic_diso_NEURO", relic_diso_NEURO);
			nbt.putBoolean("relic_ahnd_SWIPE", relic_ahnd_SWIPE);
			nbt.putBoolean("relic_diso_ATTENTION", relic_diso_ATTENTION);
			nbt.putBoolean("relic_hanshand_SPIKE", relic_hanshand_SPIKE);
			nbt.putBoolean("relic_royalfate", relic_royalfate);
			nbt.putDouble("player_king_suit", player_king_suit);
			nbt.putDouble("player_demon_suit", player_demon_suit);
			nbt.putDouble("player_oceanization", player_oceanization);
			nbt.putBoolean("relic_cursed_HEART", relic_cursed_HEART);
			nbt.putBoolean("relic_HEMOST", relic_HEMOST);
			nbt.putBoolean("relic_YEARNING", relic_YEARNING);
			nbt.putDouble("plauyer_balance", plauyer_balance);
			nbt.putBoolean("can_player_evo", can_player_evo);
			nbt.putDouble("reserve_quantity", reserve_quantity);
			nbt.putDouble("reserve_quality", reserve_quality);
			nbt.putBoolean("PEVO_NEXUS_no_rejection", PEVO_NEXUS_no_rejection);
			nbt.putBoolean("PEVO_NEXUS_reg_sanity", PEVO_NEXUS_reg_sanity);
			nbt.putDouble("PEVO_NODE_add_def", PEVO_NODE_add_def);
			nbt.putDouble("PEVO_NODE_add_resis", PEVO_NODE_add_resis);
			nbt.putDouble("PEVO_NODE_add_speed", PEVO_NODE_add_speed);
			nbt.putDouble("PEVO_NODE_add_sanity", PEVO_NODE_add_sanity);
			nbt.putBoolean("PEVO_NEXUS_reg_lights", PEVO_NEXUS_reg_lights);
			nbt.putDouble("PEVO_NODE_add_damage", PEVO_NODE_add_damage);
			nbt.putDouble("PEVO_NODE_less_damage", PEVO_NODE_less_damage);
			nbt.putDouble("PEVO_NODE_living_barrier", PEVO_NODE_living_barrier);
			nbt.putDouble("PEVO_NODE_add_miss", PEVO_NODE_add_miss);
			nbt.putBoolean("PEVO_NEXUS_perc_damage", PEVO_NEXUS_perc_damage);
			nbt.putDouble("PEVO_NODE_real_damage", PEVO_NODE_real_damage);
			nbt.putDouble("PEVO_NODE_heal_damage", PEVO_NODE_heal_damage);
			nbt.putDouble("PEVO_NODE_worse_break", PEVO_NODE_worse_break);
			nbt.putBoolean("PEVO_NEXUS_expo_shield", PEVO_NEXUS_expo_shield);
			nbt.putDouble("PEVO_NODE_eunectes", PEVO_NODE_eunectes);
			nbt.putDouble("PEVO_NODE_less_armor", PEVO_NODE_less_armor);
			return nbt;
		}

		public void readNBT(Tag tag) {
			CompoundTag nbt = (CompoundTag) tag;
			player_light = nbt.getDouble("player_light");
			player_lives = nbt.getDouble("player_lives");
			player_maxlive = nbt.getDouble("player_maxlive");
			player_shield = nbt.getDouble("player_shield");
			disoclusion = nbt.getDouble("disoclusion");
			show_stats = nbt.getBoolean("show_stats");
			relic_cursed_EMELIGHT = nbt.getBoolean("relic_cursed_EMELIGHT");
			relic_cursed_GLOWBODY = nbt.getBoolean("relic_cursed_GLOWBODY");
			relic_cursed_RESEARCH = nbt.getBoolean("relic_cursed_RESEARCH");
			relic_king_CROWN = nbt.getBoolean("relic_king_CROWN");
			relic_king_ARMOR = nbt.getBoolean("relic_king_ARMOR");
			relic_king_SPEAR = nbt.getBoolean("relic_king_SPEAR");
			relic_king_EXTENSION = nbt.getBoolean("relic_king_EXTENSION");
			kingShowPtc = nbt.getBoolean("kingShowPtc");
			relic_king_CRYSTAL = nbt.getBoolean("relic_king_CRYSTAL");
			relic_hand_THORNS = nbt.getBoolean("relic_hand_THORNS");
			relic_hand_STRANGLE = nbt.getBoolean("relic_hand_STRANGLE");
			relic_hand_FERTILITY = nbt.getBoolean("relic_hand_FERTILITY");
			relic_hand_SPEED = nbt.getBoolean("relic_hand_SPEED");
			relic_hand_BARREN = nbt.getBoolean("relic_hand_BARREN");
			relic_hand_SWIPE = nbt.getBoolean("relic_hand_SWIPE");
			relic_archfi_ARTIFACT = nbt.getBoolean("relic_archfi_ARTIFACT");
			relic_hand_FIREWORK = nbt.getBoolean("relic_hand_FIREWORK");
			relic_archfi_FLAG = nbt.getBoolean("relic_archfi_FLAG");
			relic_hand_ENGRAVE = nbt.getDouble("relic_hand_ENGRAVE");
			relic_archfi_BED = nbt.getBoolean("relic_archfi_BED");
			relic_SURVIVOR = nbt.getDouble("relic_SURVIVOR");
			relic_TREATY = nbt.getBoolean("relic_TREATY");
			relic_archifi_RYLFATE = nbt.getBoolean("relic_archifi_RYLFATE");
			relic_util_MEATCAN = nbt.getBoolean("relic_util_MEATCAN");
			relic_util_SEAGRASS = nbt.getBoolean("relic_util_SEAGRASS");
			relic_util_ORANGE = nbt.getBoolean("relic_util_ORANGE");
			relic_util_COFFEE = nbt.getBoolean("relic_util_COFFEE");
			relic_util_BERRIES = nbt.getBoolean("relic_util_BERRIES");
			player_util_RAINBOW = nbt.getBoolean("player_util_RAINBOW");
			player_util_AROMATIC = nbt.getBoolean("player_util_AROMATIC");
			relic_util_MUSICBOX = nbt.getBoolean("relic_util_MUSICBOX");
			relic_util_IRIS = nbt.getBoolean("relic_util_IRIS");
			relic_util_FLUTE = nbt.getBoolean("relic_util_FLUTE");
			relic_util_VOYGOLD = nbt.getBoolean("relic_util_VOYGOLD");
			relic_util_DURIN = nbt.getBoolean("relic_util_DURIN");
			relic_util_TOPONYM = nbt.getBoolean("relic_util_TOPONYM");
			relic_util_KETTLE = nbt.getBoolean("relic_util_KETTLE");
			relic_legend_CHITIN = nbt.getBoolean("relic_legend_CHITIN");
			chitin_knife_selected = ItemStack.of(nbt.getCompound("chitin_knife_selected"));
			relic_util_ALLEY = nbt.getBoolean("relic_util_ALLEY");
			relic_util_BATBED = nbt.getBoolean("relic_util_BATBED");
			relic_util_LONGEVITY = nbt.getBoolean("relic_util_LONGEVITY");
			relic_util_OMNIKEY = nbt.getBoolean("relic_util_OMNIKEY");
			relic_util_score = nbt.getBoolean("relic_util_score");
			relic_util_RESCISSION = nbt.getBoolean("relic_util_RESCISSION");
			relic_util_STARE = nbt.getBoolean("relic_util_STARE");
			relic_hand_SWORD = nbt.getBoolean("relic_hand_SWORD");
			relic_util_ALLAY = nbt.getBoolean("relic_util_ALLAY");
			relic_util_RAINBOW = nbt.getBoolean("relic_util_RAINBOW");
			relic_diso = nbt.getBoolean("relic_diso");
			relic_diso_FLESH = nbt.getBoolean("relic_diso_FLESH");
			relic_diso_BLOOD = nbt.getBoolean("relic_diso_BLOOD");
			relic_diso_NEURO = nbt.getBoolean("relic_diso_NEURO");
			relic_ahnd_SWIPE = nbt.getBoolean("relic_ahnd_SWIPE");
			relic_diso_ATTENTION = nbt.getBoolean("relic_diso_ATTENTION");
			relic_hanshand_SPIKE = nbt.getBoolean("relic_hanshand_SPIKE");
			relic_royalfate = nbt.getBoolean("relic_royalfate");
			player_king_suit = nbt.getDouble("player_king_suit");
			player_demon_suit = nbt.getDouble("player_demon_suit");
			player_oceanization = nbt.getDouble("player_oceanization");
			relic_cursed_HEART = nbt.getBoolean("relic_cursed_HEART");
			relic_HEMOST = nbt.getBoolean("relic_HEMOST");
			relic_YEARNING = nbt.getBoolean("relic_YEARNING");
			plauyer_balance = nbt.getDouble("plauyer_balance");
			can_player_evo = nbt.getBoolean("can_player_evo");
			reserve_quantity = nbt.getDouble("reserve_quantity");
			reserve_quality = nbt.getDouble("reserve_quality");
			PEVO_NEXUS_no_rejection = nbt.getBoolean("PEVO_NEXUS_no_rejection");
			PEVO_NEXUS_reg_sanity = nbt.getBoolean("PEVO_NEXUS_reg_sanity");
			PEVO_NODE_add_def = nbt.getDouble("PEVO_NODE_add_def");
			PEVO_NODE_add_resis = nbt.getDouble("PEVO_NODE_add_resis");
			PEVO_NODE_add_speed = nbt.getDouble("PEVO_NODE_add_speed");
			PEVO_NODE_add_sanity = nbt.getDouble("PEVO_NODE_add_sanity");
			PEVO_NEXUS_reg_lights = nbt.getBoolean("PEVO_NEXUS_reg_lights");
			PEVO_NODE_add_damage = nbt.getDouble("PEVO_NODE_add_damage");
			PEVO_NODE_less_damage = nbt.getDouble("PEVO_NODE_less_damage");
			PEVO_NODE_living_barrier = nbt.getDouble("PEVO_NODE_living_barrier");
			PEVO_NODE_add_miss = nbt.getDouble("PEVO_NODE_add_miss");
			PEVO_NEXUS_perc_damage = nbt.getBoolean("PEVO_NEXUS_perc_damage");
			PEVO_NODE_real_damage = nbt.getDouble("PEVO_NODE_real_damage");
			PEVO_NODE_heal_damage = nbt.getDouble("PEVO_NODE_heal_damage");
			PEVO_NODE_worse_break = nbt.getDouble("PEVO_NODE_worse_break");
			PEVO_NEXUS_expo_shield = nbt.getBoolean("PEVO_NEXUS_expo_shield");
			PEVO_NODE_eunectes = nbt.getDouble("PEVO_NODE_eunectes");
			PEVO_NODE_less_armor = nbt.getDouble("PEVO_NODE_less_armor");
		}
	}

}
