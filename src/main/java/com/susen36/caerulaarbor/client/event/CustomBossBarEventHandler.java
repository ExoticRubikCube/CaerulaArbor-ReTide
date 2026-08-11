package com.susen36.caerulaarbor.client.event;

import com.mojang.datafixers.util.Pair;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CAConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class CustomBossBarEventHandler {
	public static final Map<BossEvent, BossBarRenderContext> CACHE = new HashMap<>();
	public static final Map<BossEvent, String> CACHE_NAME = new HashMap<>();
	public static final Map<BossEvent, BossEvent.BossBarColor> CACHE_COLOR = new HashMap<>();
	public static final Map<BossEvent, BossEvent.BossBarOverlay> CACHE_OVERLAY = new HashMap<>();
	public static final Set<BossEvent> BLACK_LIST = new HashSet<>();

    public static final ResourceLocation GENERIC = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/generic_bossbar.png");
    public static final ResourceLocation PATHSHAPER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/path_shaper_bossbar.png");
    public static final ResourceLocation LINGERING = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/lingering_shaper_bossbar.png");
    public static final ResourceLocation QUINTUS = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/quintus_bossbar.png");
    public static final ResourceLocation T_BISHOP = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/tidelinked_bishop_bossbar.png");
    public static final ResourceLocation T_IMMORTAL = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/tidelinked_immortal_bossbar.png");
    public static final ResourceLocation T_ARCHON = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/tidelinked_archon_bossbar.png");
    public static final ResourceLocation HIGHMORE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/highmore_bossbar.png");
    public static final ResourceLocation IZUMIK = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/izumik_bossbar.png");
    public static final ResourceLocation MARTUS = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/martus_bossbar.png");
    public static final ResourceLocation LAST_KNIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/the_last_knight_bossbar.png");
    public static final ResourceLocation CORRUPTED = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/corrupted_bossbar.png");
    public static final ResourceLocation PURIFIED = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/corrupted_bossbar_purify.png");
    public static final ResourceLocation ISHARMLA_HUMAN = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/isharmla_bossbar.png");
    public static final ResourceLocation ISHARMLA_M3 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/isharmla_monster_bossbar.png");
    public static final ResourceLocation THIRSTER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/thirster_bossbar.png");
    public static final ResourceLocation THIRSTER_BARRIER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/thirster_bossbar_barrier.png");
    public static final ResourceLocation FLAMARINE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/flamarine_bossbar.png");
    public static final ResourceLocation WITHER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/wither_bossbar.png");
    public static final ResourceLocation WITHERIA = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/witheria_bossbar.png");
    public static final ResourceLocation WARDEN = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/warden_bossbar.png");
    public static final ResourceLocation WARDENIS = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/wardenis_bossbar.png");
    public static final ResourceLocation ENDERINA = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/enderina_bossbar.png");
    public static final ResourceLocation ENDSPEAKER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/endspeaker_bossbar.png");
    public static final ResourceLocation WITHER_STYLE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/wither_bossstyle.png");
    public static final ResourceLocation WARDEN_STYLE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/bossbar/warden_bossstyle.png");
    public static final ResourceLocation ENDERINA_STYLE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/enderina_style.png");

    public static final BossBarRenderContext CONTEXT_GENERIC = BossBarRenderContext.of(GENERIC).frame(158,26).bar(156,3,1,11).offset(-9, 5);
    public static final BossBarRenderContext CONTEXT_PATHSHAPER = BossBarRenderContext.of(PATHSHAPER).frame(182,13).bar(180,7,1,4).offset(-1, -5).color(0x628CFE);
    public static final BossBarRenderContext CONTEXT_LINGERING = BossBarRenderContext.of(LINGERING).frame(182,13).bar(180,7,1,4).offset(-1, -5).color(0x7ABB66);
    public static final BossBarRenderContext CONTEXT_QUINTUS = BossBarRenderContext.of(QUINTUS).frame(182,14).bar(180,4,1,6).offset(-11, 2);
    public static final BossBarRenderContext CONTEXT_TIDELINKED_BISHOP = BossBarRenderContext.of(T_BISHOP).frame(182,12).bar(180,4,1,4).offset(-3, -8).color(0xDCE4CE);
    public static final BossBarRenderContext CONTEXT_TIDELINKED_IMMORTAL = BossBarRenderContext.of(T_IMMORTAL).frame(182,16).bar(180,5,1,6).offset(-6, -9).color(0xC7DCE0);
    public static final BossBarRenderContext CONTEXT_TIDELINKED_ARCHON = BossBarRenderContext.of(T_ARCHON).frame(182,16).bar(180,5,1,6).offset(-6, -9).color(0xDDD607);
    public static final BossBarRenderContext CONTEXT_HIGHMORE = BossBarRenderContext.of(HIGHMORE).frame(182,19).bar(180,3,1,5).offset(-9, 4).color(0xFEBC9C);
    public static final BossBarRenderContext CONTEXT_IZUMIK = BossBarRenderContext.of(IZUMIK).frame(188,26).bar(180,3,4,11).offset(-12, 5).color(0xF1F1F9);
    public static final BossBarRenderContext CONTEXT_MARTUS = BossBarRenderContext.of(MARTUS).frame(186,22).bar(180,3,1,13).offset(-12, -8);
    public static final BossBarRenderContext CONTEXT_TIDE_HUNT = BossBarRenderContext.of(LAST_KNIGHT).frame(184,27).bar(180,3,2,18).offset(-12, -7).color(0xFFFFFF);
    public static final BossBarRenderContext CONTEXT_LAST_KNIGHT = BossBarRenderContext.of(LAST_KNIGHT).frame(184,27).bar(180,3,2,18).offset(-12, -7).color(0x7380F3);
    public static final BossBarRenderContext CONTEXT_CORRUPTED = BossBarRenderContext.of(CORRUPTED).frame(182,23).bar(180,5,1,12).offset(-10, 5).color(0xDD5260);
    public static final BossBarRenderContext CONTEXT_PURIFIED = BossBarRenderContext.of(PURIFIED).frame(182,23).bar(180,5,1,12).offset(-10, 5).color(0x7FCDFF);
    public static final BossBarRenderContext CONTEXT_ISHARMLA_HUMAN = BossBarRenderContext.of(ISHARMLA_HUMAN).frame(182,24).bar(180,3,1,13).offset(-12, 5).color(0x12E0FF);
    public static final BossBarRenderContext CONTEXT_ISHARMLA_M3 = BossBarRenderContext.of(ISHARMLA_M3).frame(182,18).bar(180,4,1,9).offset(-8, 5).color(0x12E0FF);
    public static final BossBarRenderContext CONTEXT_THIRSTER = BossBarRenderContext.of(THIRSTER).frame(182,18).bar(180,3,1,12).offset(-9, -5).color(0xA49CEE);
    public static final BossBarRenderContext CONTEXT_THIRSTER_BARRIER = BossBarRenderContext.of(THIRSTER_BARRIER).frame(182,18).bar(180,3,1,12).offset(-9, -5).color(0xA49CEE);
    public static final BossBarRenderContext CONTEXT_FLAMARINE = BossBarRenderContext.of(FLAMARINE).frame(184,11).bar(180,5,2,3).offset(-3, -10).color(0x7EFEFD);
    public static final BossBarRenderContext CONTEXT_WITHER = BossBarRenderContext.of(WITHER).frame(182,23).bar(180,3,1,13).offset(-12, 7).color(0x77B2DB).withStyle(WITHER_STYLE, 16);
    public static final BossBarRenderContext CONTEXT_WITHERIA = BossBarRenderContext.of(WITHERIA).frame(182,23).bar(180,3,1,13).offset(-12, 7).color(0x77B2DB).withStyle(WITHER_STYLE, 16);
    public static final BossBarRenderContext CONTEXT_WARDEN = BossBarRenderContext.of(WARDEN).frame(182,21).bar(180,3,1,8).offset(-10, 7).color(0x3FD2FA).withStyle(WARDEN_STYLE, 32);
    public static final BossBarRenderContext CONTEXT_WARDENIS = BossBarRenderContext.of(WARDENIS).frame(182,21).bar(180,3,1,8).offset(-10, 7).color(0x3FD2FA).withStyle(WARDEN_STYLE, 32);
    public static final BossBarRenderContext CONTEXT_ENDERINA = BossBarRenderContext.of(ENDERINA).frame(182,23).bar(180,3,1,13).offset(-12, 6).color(0x7B75D7);
    public static final BossBarRenderContext CONTEXT_ENDSPEAKER = BossBarRenderContext.of(ENDSPEAKER).frame(182,24).bar(180,3,1,11).offset(-10, 6).color(0xD6E0F2);

	public static final Map<BossEvent, Integer> CYCLE_MAP = new HashMap<>();

	@OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void customBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event){
    	if(!CAConfigs.BOSSBAR.get()) return;
    	if(event.isCanceled()) return;
        LerpingBossEvent bossEvent = event.getBossEvent();
        BossBarRenderContext context = getContext(bossEvent);
        GuiGraphics gui = event.getGuiGraphics();
        if(context != null) {
            float progress = bossEvent.getProgress();
            context = context.loc((gui.guiWidth() - context.frame_x)/2, event.getY())
                    .name(bossEvent.getName().getString(), gui.guiWidth()/2, event.getY());
            ResourceLocation style = context.style;
            int cycle = 0;
            if(style != null || context.equals(CONTEXT_ENDERINA)){
                if (CYCLE_MAP.containsKey(bossEvent)) {
                    cycle = CYCLE_MAP.get(bossEvent);
                    CYCLE_MAP.replace(bossEvent, cycle + 1);
                }
                else CYCLE_MAP.put(bossEvent, 0);
            }
            renderBossBar(gui, context, progress, context.color, cycle);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void customBossBarCancel(CustomizeGuiOverlayEvent.BossEventProgress event){
    	if(!CAConfigs.BOSSBAR.get()) return;
    	if(event.isCanceled()) return;
        LerpingBossEvent bossEvent = event.getBossEvent();
        BossBarRenderContext context = getContext(bossEvent);
        if(context != null) {
            event.setCanceled(true);
        }
    }

    private static void renderBossBar(GuiGraphics gui, BossBarRenderContext context, float progress, int pColor, int cycle_progress){
        ResourceLocation texture = context.texture;
        int offset_y = context.render_offset_y;
        int real_px = (int) (context.bar_x * progress);
        gui.blit(texture, context.x, context.y + offset_y, 0, 0,
                context.frame_x, context.frame_y, 256 ,32);
        int bx = context.x + context.bar_offset_x, by = context.y + context.bar_offset_y + offset_y;
        gui.blit(texture, bx, by,
                0, context.frame_y, real_px, context.bar_y, 256, 32);
        ResourceLocation style = context.style;
        if (style != null){
            int VOffset = (cycle_progress / 4) % context.style_cycle;
            gui.blit(style, bx, by,
                    0, VOffset, real_px, context.bar_y, 256, 32);
        } else if (context.equals(CONTEXT_ENDERINA)){
            int uOffset = (cycle_progress * 5 / 16) % 218;
            int remain_len = real_px - 90;
            gui.blit(ENDERINA_STYLE, bx, by,
                    uOffset, 0, Math.min(90, real_px), context.bar_y, 308, 134);
            if (remain_len > 0){
	            gui.blit(ENDERINA_STYLE, bx + 90, by,
	            			uOffset, 67, remain_len, context.bar_y, 308, 134);
            }
        }
        gui.drawCenteredString(Minecraft.getInstance().font, context.name, context.name_x, context.name_y + context.name_offset_y, pColor);
    }

    public static final Set<Pair<String, BossBarRenderContext>> INDEX = new HashSet<>();
    static{
        INDEX.add(Pair.of("entity.caerula_arbor.route_shaper", CONTEXT_PATHSHAPER));
        INDEX.add(Pair.of("entity.caerula_arbor.lingering_pathshaper", CONTEXT_LINGERING));
        INDEX.add(Pair.of("entity.caerula_arbor.bishop_fish", CONTEXT_QUINTUS));
        INDEX.add(Pair.of("entity.caerula_arbor.tidelinked_bishop", CONTEXT_TIDELINKED_BISHOP));
        INDEX.add(Pair.of("entity.caerula_arbor.tidelinked_immortal", CONTEXT_TIDELINKED_IMMORTAL));
        INDEX.add(Pair.of("entity.caerula_arbor.tidelinked_archon", CONTEXT_TIDELINKED_ARCHON));
        INDEX.add(Pair.of("entity.caerula_arbor.highmore",CONTEXT_HIGHMORE));
        INDEX.add(Pair.of("entity.caerula_arbor.the_last_knight",CONTEXT_TIDE_HUNT));
        INDEX.add(Pair.of("entity.caerula_arbor.last_knight_and_horse",CONTEXT_LAST_KNIGHT));
        INDEX.add(Pair.of("entity.caerula_arbor.martus",CONTEXT_MARTUS));
        INDEX.add(Pair.of("entity.caerula_arbor.izumik",CONTEXT_IZUMIK));
        INDEX.add(Pair.of("entity.caerula_arbor.flamarine_golem",CONTEXT_FLAMARINE));
        INDEX.add(Pair.of("entity.caerula_arbor.oceanized_wither",CONTEXT_WITHER));
        INDEX.add(Pair.of("entity.caerula_arbor.oceanized_witheria",CONTEXT_WITHERIA));
        INDEX.add(Pair.of("entity.caerula_arbor.oceanized_warden",CONTEXT_WARDEN));
        INDEX.add(Pair.of("entity.caerula_arbor.oceanized_wardenis",CONTEXT_WARDENIS));
        INDEX.add(Pair.of("entity.caerula_arbor.oceanized_enderina",CONTEXT_ENDERINA));
        INDEX.add(Pair.of("entity.caerula_arbor.endspeaker_0",CONTEXT_ENDSPEAKER));
        INDEX.add(Pair.of("entity.caerula_arbor.endspeaker_1",CONTEXT_ENDSPEAKER));
        INDEX.add(Pair.of("entity.caerula_arbor.endspeaker_2",CONTEXT_ENDSPEAKER));
        INDEX.add(Pair.of("entity.caerula_arbor.endspeaker_3",CONTEXT_ENDSPEAKER));
    }

    @Nullable
    public static BossBarRenderContext getContext(BossEvent bossEvent){
        if(CACHE.containsKey(bossEvent)) {
            String cachedName = CACHE_NAME.get(bossEvent);
            BossEvent.BossBarColor cachedColor = CACHE_COLOR.get(bossEvent);
            BossEvent.BossBarOverlay cachedOverlay = CACHE_OVERLAY.get(bossEvent);
            String currentName = bossEvent.getName().getString();
            BossEvent.BossBarColor currentColor = bossEvent.getColor();
            BossEvent.BossBarOverlay currentOverlay = bossEvent.getOverlay();
            if (Objects.equals(cachedName, currentName) && cachedColor == currentColor && cachedOverlay == currentOverlay) {
                return CACHE.get(bossEvent);
            } else {
                CACHE.remove(bossEvent);
                CACHE_NAME.remove(bossEvent);
                CACHE_COLOR.remove(bossEvent);
                CACHE_OVERLAY.remove(bossEvent);
            }
        }
        if(BLACK_LIST.contains(bossEvent)) return null;
        String display_name = bossEvent.getName().getString();
        BossBarRenderContext context = null;
        for (Pair<String, BossBarRenderContext> p: INDEX){
            if(display_name.equals(Component.translatable(p.getFirst()).getString())){
                context = p.getSecond();
                break;
            }
        }
        if (context == null){
	        if (display_name.equals(Component.translatable("entity.caerula_arbor.skadi_corrupted").getString())){
	        	if (bossEvent.getColor() == LerpingBossEvent.BossBarColor.BLUE)
	        		return CONTEXT_PURIFIED;
	        	else return CONTEXT_CORRUPTED;
	        } else if (display_name.equals(Component.translatable("entity.caerula_arbor.isharmla").getString())){
	        	if (bossEvent.getColor() == LerpingBossEvent.BossBarColor.WHITE)
	        		return CONTEXT_ISHARMLA_M3;
	        	else return CONTEXT_ISHARMLA_HUMAN;
	        } else if (display_name.equals(Component.translatable("entity.caerula_arbor.thirster").getString())){
	        	if (bossEvent.getColor() == LerpingBossEvent.BossBarColor.WHITE)
	        		return CONTEXT_THIRSTER_BARRIER;
	        	else return CONTEXT_THIRSTER;
	        } else if (useGenericBossBar(display_name)){
	        	context = CONTEXT_GENERIC;
	        }
        }
        if(context != null) {
            CACHE.put(bossEvent, context);
            CACHE_NAME.put(bossEvent, bossEvent.getName().getString());
            CACHE_COLOR.put(bossEvent, bossEvent.getColor());
            CACHE_OVERLAY.put(bossEvent, bossEvent.getOverlay());
        } else BLACK_LIST.add(bossEvent);
        return context;
    }

    private static final Set<String> nameList = Set.of(
		"entity.caerula_arbor.oceanized_brute",
		"entity.caerula_arbor.oceanized_illusioner",
		"entity.caerula_arbor.mega_chest",
		"entity.caerula_arbor.super_big_cat",
		"entity.caerula_arbor.super_slider",
		"entity.caerula_arbor.tide_chimera"
    	);

    private static boolean useGenericBossBar(String name){
    	for (String s: nameList){
    		if(name.equals(Component.translatable(s).getString())) return true;
    	}
    	return false;
    }

    public static class BossBarRenderContext{
        public ResourceLocation texture;
        public int x, y, frame_x, frame_y, bar_x, bar_y, bg_x, bg_y, name_x, name_y, bar_offset_x, bar_offset_y;
        public String name;
        public int color, name_offset_y, render_offset_y;
        public ResourceLocation style;
        public int style_cycle;
        public BossBarRenderContext(ResourceLocation texture){
            this.texture = texture;
        }
        public static BossBarRenderContext of(ResourceLocation texture){
            BossBarRenderContext context = new BossBarRenderContext(texture);
            context.x = 0; context.y = 0; context.name = "";
            context.frame_x = 0; context.frame_y = 0; context.bar_x = 0; context.bar_y = 5;
            context.name_x = 0; context.name_y = 0;
            context.color = 0xFFFFFF;
            return context;
        }
        public BossBarRenderContext loc(int x, int y){
            this.x = x; this.y = y;
            return this;
        }
        public BossBarRenderContext frame(int x, int y){
            this.frame_x = x; this.frame_y = y;
            return this;
        }
        public BossBarRenderContext bar(int size_x, int size_y, int offset_x, int offset_y){
            this.bar_x = size_x; this.bar_y = size_y;
            this.bar_offset_x = offset_x; this.bar_offset_y = offset_y;
            return this;
        }
        public BossBarRenderContext name(String n, int x, int y){
            this.name = n;
            this.name_x = x; this.name_y = y;
            return this;
        }
        public BossBarRenderContext offset(int main, int name){
        	this.name_offset_y = name;
        	this.render_offset_y = main;
        	return this;
        }
        public BossBarRenderContext color(int color){
            this.color = color;
            return this;
        }
        public BossBarRenderContext withStyle(ResourceLocation style, int pixel_per_cycle){
            this.style = style; this.style_cycle = pixel_per_cycle;
            return this;
        }
    }
}
