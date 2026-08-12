package com.susen36.caerulaarbor.item.relic;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.relic.RelicType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏品物品基类：把「Relic ↔ Item」双向绑定 + 通用右键激活逻辑收拢到一处。
 * <p>
 * 注册系统说明（对应 NeoForge 原版机制）：
 * <ol>
 *   <li>原版 Item 构造器：{@link Item#Item(Item.Properties)} 只是把 Properties
 *       里的 DataComponents / requiredFeatures / descriptionId 收进 final 字段，
 *       并调用 {@code BuiltInRegistries.ITEM.createIntrusiveHolder(this)} 建立 intrusive holder，
 *       <b>完全不会自己注册</b>。真正的注册必须通过：
 *       <pre>DeferredRegister.create(BuiltInRegistries.ITEM, MODID)
 *             .register("name", () -> new XxxItem(...));</pre>
 *       也就是项目 [CAItems.java] 里的用法。</li>
 *   <li>本基类在构造器里执行 {@code BINDING.put(relic, this)}，这样：
 *       无论是 SimpleRelicItem 工厂实例、还是自定义类 extends RelicItemBase，
 *       只要 DeferredRegister 触发了构造器，绑定表就有对应条目——
 *       保证每一个 Relic 枚举都能反查到 Item，GUI / LootTable 可以直接拿。</li>
 * </ol>
 */
public abstract class RelicItemBase extends Item {

    /** RelicType → Item 双向绑定：构造器写入；反查用 {@link #byRelic(RelicType)}。*/
    private static final Map<RelicType, RelicItemBase> BY_RELIC = new HashMap<>();

    public final RelicType relic;

    protected RelicItemBase(RelicType relic, Item.Properties properties) {
        super(properties);
        this.relic = relic;
        BY_RELIC.put(relic, this);
    }

    public static RelicItemBase byRelic(RelicType relic) {
        return BY_RELIC.get(relic);
    }

    public static boolean hasBinding(RelicType relic) {
        return BY_RELIC.containsKey(relic);
    }

    /**
     * 通用 tooltip：读取 {@code item.caerula_arbor.<itemPath>.description_0/1}。
     * 与现有 HandOfEngraveItem / SurvivorContractItem 格式完全一致，
     * 本地化文件不用改一行。
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        String descKey = this.getDescriptionId() + ".description";
        String key0 = descKey + "_0";
        String key1 = descKey + "_1";
        Component line0 = Component.translatable(key0);
        if (!line0.getString().equals(key0)) {
            tooltip.add(line0);
        }
        Component line1 = Component.translatable(key1);
        if (!line1.getString().equals(key1)) {
            tooltip.add(line1);
        }
    }

    /**
     * 通用激活钩子：子类或 SimpleRelicItem 调用此方法执行
     * 「播放声音 → 发射粒子 → relic.set() → sync → 手持激活动画」。
     *
     * @return true 表示确实执行了激活（用于判定是否 shrink / hurt）
     */
    protected boolean performActivate(Level world, Player player, ItemStack stack, ActivateParams params) {
        if (!shouldActivate(player, params.mode())) {
            return false;
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), params.soundEvent(), SoundSource.NEUTRAL, params.volume(), params.pitch());
        }
        if (world instanceof ServerLevel level) {
            level.sendParticles(params.particle(), x, y + params.particleYOffset(), z, params.paticleCount(), 1, 1, 1, params.particleSpeed());
        }
        PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
        this.relic.set(capability, params.setValue());
        capability.syncPlayerVariables(player);
        if (params.showActivationOverlay() && world.isClientSide()) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
        }
        if (params.shrinkAfterUse()) {
            stack.shrink(1);
        }
        return true;
    }

    private boolean shouldActivate(Player player, ActivateParams.ActivateMode mode) {
        if (mode == ActivateParams.ActivateMode.BELOW_ZERO) {
            return this.relic.get(player) < 0;
        }
        // NOT_GAINED：默认 = defaultLevel 还没拿到遗物
        return !this.relic.gained(player);
    }

    /**
     * 默认右键行为：回退到 Item 基类的 {@link Item#use(Level, Player, InteractionHand)}。
     * 这样做同时解决了两个编译问题：
     * <ol>
     *   <li>自定义子类（有自己的 use 逻辑）调用 {@code super.use(...)} 是合法的；</li>
     *   <li>纯食物类 / 没有主动右键行为的类不需要再重写此方法。</li>
     * </ol>
     * SimpleRelicItem 会 override 此方法并调用 performActivate。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return super.use(world, player, hand);
    }
}