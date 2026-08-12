package com.susen36.caerulaarbor.item.relic;

import com.susen36.caerulaarbor.relic.RelicType;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * 无特殊逻辑的简单收藏品物品：只需指定 Relic + 激活参数，一行即可注册。
 * <p>
 * 对于 99% 只是"右键用一次 → 声音 + 粒子 + 写入 Relic 标记"的收藏品，
 * 不需要每个都写独立 Java 类。
 */
public class SimpleRelicItem extends RelicItemBase {

    private final Supplier<ActivateParams> paramsSupplier;

    public SimpleRelicItem(RelicType relic, Item.Properties properties, Supplier<ActivateParams> paramsSupplier) {
        super(relic, properties);
        this.paramsSupplier = paramsSupplier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        performActivate(world, player, stack, this.paramsSupplier.get());
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    /** Boolean 型收藏品：rarity=UNCOMMON / stacksTo=1 / LEVELUP / HAPPY_VILLAGER。*/
    public static Supplier<Item> simpleBoolean(Holder<RelicType> relic) {
        return () -> new SimpleRelicItem(relic.value(),
                new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                ActivateParams::standardBoolean);
    }

    /** Boolean 型收藏品（自定义 rarity + 自定义声音/粒子）。*/
    public static Supplier<Item> simpleBoolean(Holder<RelicType> relic, Rarity rarity, Supplier<ActivateParams> params) {
        return () -> new SimpleRelicItem(relic.value(),
                new Item.Properties().stacksTo(1).rarity(rarity),
                params);
    }

    /** Numeric(<0→0) 型收藏品（对应 HAND_ENGRAVE、SURVIVOR_CONTRACT 这种）。*/
    public static Supplier<Item> simpleNumericNeg1(Holder<RelicType> relic, Rarity rarity, Supplier<ActivateParams> params) {
        return () -> new SimpleRelicItem(relic.value(),
                new Item.Properties().stacksTo(1).rarity(rarity),
                params);
    }

    /** 消耗型：使用后 shrink(1)（用于 PROOF_OF_LONGEVITY 这类"吃掉"的收藏品，副作用要写独立类）。*/
    public static Supplier<Item> simpleConsumable(Holder<RelicType> relic, Rarity rarity, Supplier<ActivateParams> params) {
        return () -> new SimpleRelicItem(relic.value(),
                new Item.Properties().stacksTo(1).rarity(rarity),
                () -> params.get().shrinkAfterUse() ? params.get()
                        : ActivateParams.builder()
                            .mode(params.get().mode())
                            .setValue(params.get().setValue())
                            .sound(params.get().soundEvent(), params.get().volume(), params.get().pitch())
                            .particle(params.get().particle(), params.get().paticleCount())
                            .particleYOffset(params.get().particleYOffset())
                            .particleSpeed(params.get().particleSpeed())
                            .showOverlay(params.get().showActivationOverlay())
                            .shrink(true)
                            .build());
    }
}
