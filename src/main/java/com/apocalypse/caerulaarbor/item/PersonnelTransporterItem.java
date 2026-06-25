
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PersonnelTransporterItem extends Item {
	public PersonnelTransporterItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
	}

	public static final TagKey<EntityType<?>> HOMO_SAPIENS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "portable"));
    public static final String emptyNameHolder = "apocata";
    public static final String TAG_NAME = "name";
    public static final String TAG_PERC = "perc";

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return ItemUtils.isFilledwithPersonnel(itemstack);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.personnel_transporter.description_0"));
		list.add(Component.translatable("item.caerula_arbor.personnel_transporter.description_1"));
		list.add(Component.translatable("item.caerula_arbor.personnel_transporter.description_2"));
		list.add(getStoredEntityName(itemstack));
	}

	@Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pTarget, @NotNull InteractionHand pHand
    ) {
        InteractionResult pass  = InteractionResult.PASS;
        EntityType<?> type = pTarget.getType();
        if(type.is(HOMO_SAPIENS)){
            CompoundTag tag = pStack.getOrCreateTag();
            String name = tag.getString(TAG_NAME);
            if (name.isEmpty() || name.equals(emptyNameHolder)){
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type);
                if (key == null)return pass;
                String id = key.toString();
                CaerulaArborMod.LOGGER.info("store {}", id);
                if(pPlayer.getMainHandItem().getItem() == pStack.getItem()) {
                    pPlayer.getMainHandItem().getOrCreateTag().putString(TAG_NAME, id);
                    pPlayer.getMainHandItem().getOrCreateTag().putDouble(TAG_PERC, pTarget.getHealth() / pTarget.getMaxHealth());
                    pTarget.discard();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return pass;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext){
        Direction dire = pContext.getClickedFace();
        BlockPos pos = pContext.getClickedPos().offset(dire.getStepX(),dire.getStepY(),dire.getStepZ());
        ItemStack item = pContext.getItemInHand();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        if (player != null && !player.isShiftKeyDown()) return InteractionResult.PASS;
        CompoundTag tag = item.getOrCreateTag();
        EntityType<?> type = getStoredEntityType(item);
        if(type==null) return InteractionResult.PASS;
        if(level instanceof ServerLevel sLevel){
            Entity toSpawn = type.spawn(sLevel, pos, MobSpawnType.MOB_SUMMONED);
            double perc = tag.getDouble(TAG_PERC);
            if (toSpawn instanceof LivingEntity living) {
                living.setHealth((float) (living.getMaxHealth() * perc));
            }
            tag.putString(TAG_NAME,emptyNameHolder);
            tag.putDouble(TAG_PERC,0);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    public EntityType<?> getStoredEntityType(ItemStack item){
        if(item == null) return null;
        CompoundTag tag = item.getOrCreateTag();
        if(!tag.contains(TAG_NAME)) return null;
        String name = tag.getString(TAG_NAME);
        if (name.isEmpty() || name.equals(emptyNameHolder))return null;
        ResourceLocation location = new ResourceLocation(name);
        return ForgeRegistries.ENTITY_TYPES.getValue(location);
    }

    public Component getStoredEntityName(ItemStack itemStack){
        EntityType<?> type = getStoredEntityType(itemStack);
        if(type != null)
            return type.getDescription();
        return Component.literal("EMPTY").withStyle(ChatFormatting.AQUA);
    }
}
