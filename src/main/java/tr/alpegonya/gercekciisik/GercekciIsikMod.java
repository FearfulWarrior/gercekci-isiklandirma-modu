package tr.alpegonya.gercekciisik;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GercekciIsikMod.MOD_ID)
public final class GercekciIsikMod {
    public static final String MOD_ID = "gercekciisik";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final RegistryObject<LampBlock> TRADITIONAL_FLUORESCENT =
            BLOCKS.register("traditional_fluorescent", () -> new LampBlock(false,
                    BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<LampBlock> MODERN_FLUORESCENT =
            BLOCKS.register("modern_fluorescent", () -> new LampBlock(true,
                    BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<ConfigurableLightBlock> SQUARE_SPOT =
            BLOCKS.register("square_spot", () -> new ConfigurableLightBlock(1, 13, 2, 0, fixtureProperties()));
    public static final RegistryObject<ConfigurableLightBlock> ROUND_SPOT =
            BLOCKS.register("round_spot", () -> new ConfigurableLightBlock(1, 13, 2, 0, fixtureProperties()));
    public static final RegistryObject<ConfigurableLightBlock> TRADITIONAL_RING_LIGHT =
            BLOCKS.register("traditional_ring_light", () -> new ConfigurableLightBlock(2, 0, 3, 0, fixtureProperties()));
    public static final RegistryObject<ConfigurableLightBlock> MODERN_RING_LIGHT =
            BLOCKS.register("modern_ring_light", () -> new ConfigurableLightBlock(1, 13, 2, 0, fixtureProperties()));
    public static final RegistryObject<ConfigurableLightBlock> HORIZONTAL_LED_STRIP =
            BLOCKS.register("horizontal_led_strip", () -> new ConfigurableLightBlock(1, 13, 2, 1, fixtureProperties()));
    public static final RegistryObject<ConfigurableLightBlock> VERTICAL_LED_STRIP =
            BLOCKS.register("vertical_led_strip", () -> new ConfigurableLightBlock(1, 13, 2, 2, fixtureProperties()));
    public static final RegistryObject<Block> LED_CEILING_PANEL =
            BLOCKS.register("led_ceiling_panel", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0F).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<LedStairBlock> LED_STAIRS =
            BLOCKS.register("led_stairs", () -> new LedStairBlock(
                    BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<BlockEntityType<LampBlockEntity>> LAMP_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("lamp", () -> BlockEntityType.Builder.of(
                    LampBlockEntity::new, TRADITIONAL_FLUORESCENT.get(), MODERN_FLUORESCENT.get()).build(null));
    public static final RegistryObject<BlockEntityType<AdjustableLightBlockEntity>> ADJUSTABLE_LIGHT_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("adjustable_light", () -> BlockEntityType.Builder.of(
                    AdjustableLightBlockEntity::new, SQUARE_SPOT.get(), ROUND_SPOT.get(),
                    TRADITIONAL_RING_LIGHT.get(), MODERN_RING_LIGHT.get(), HORIZONTAL_LED_STRIP.get(),
                    VERTICAL_LED_STRIP.get(), LED_STAIRS.get()).build(null));

    public static final RegistryObject<Item> TRADITIONAL_ITEM = registerBlockItem("traditional_fluorescent", TRADITIONAL_FLUORESCENT);
    public static final RegistryObject<Item> MODERN_ITEM = registerBlockItem("modern_fluorescent", MODERN_FLUORESCENT);
    public static final RegistryObject<Item> SQUARE_SPOT_ITEM = registerBlockItem("square_spot", SQUARE_SPOT);
    public static final RegistryObject<Item> ROUND_SPOT_ITEM = registerBlockItem("round_spot", ROUND_SPOT);
    public static final RegistryObject<Item> TRADITIONAL_RING_ITEM = registerBlockItem("traditional_ring_light", TRADITIONAL_RING_LIGHT);
    public static final RegistryObject<Item> MODERN_RING_ITEM = registerBlockItem("modern_ring_light", MODERN_RING_LIGHT);
    public static final RegistryObject<Item> HORIZONTAL_LED_STRIP_ITEM = registerBlockItem("horizontal_led_strip", HORIZONTAL_LED_STRIP);
    public static final RegistryObject<Item> VERTICAL_LED_STRIP_ITEM = registerBlockItem("vertical_led_strip", VERTICAL_LED_STRIP);
    public static final RegistryObject<Item> LED_CEILING_PANEL_ITEM = registerBlockItem("led_ceiling_panel", LED_CEILING_PANEL);
    public static final RegistryObject<Item> LED_STAIRS_ITEM = registerBlockItem("led_stairs", LED_STAIRS);
    public static final RegistryObject<LightingRemoteItem> LIGHT_CONTROL_REMOTE =
            ITEMS.register("lighting_remote", () -> new LightingRemoteItem(new Item.Properties().stacksTo(1)));

    private static BlockBehaviour.Properties fixtureProperties() {
        return BlockBehaviour.Properties.of().strength(0.4F).sound(SoundType.GLASS).noOcclusion();
    }

    private static RegistryObject<Item> registerBlockItem(String id, RegistryObject<? extends Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final RegistryObject<CreativeModeTab> LIGHT_TAB = TABS.register("light_control", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gercekciisik.light_control"))
                    .icon(() -> new ItemStack(LIGHT_CONTROL_REMOTE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(TRADITIONAL_ITEM.get());
                        output.accept(MODERN_ITEM.get());
                        output.accept(SQUARE_SPOT_ITEM.get());
                        output.accept(ROUND_SPOT_ITEM.get());
                        output.accept(TRADITIONAL_RING_ITEM.get());
                        output.accept(MODERN_RING_ITEM.get());
                        output.accept(HORIZONTAL_LED_STRIP_ITEM.get());
                        output.accept(VERTICAL_LED_STRIP_ITEM.get());
                        output.accept(LED_CEILING_PANEL_ITEM.get());
                        output.accept(LED_STAIRS_ITEM.get());
                        output.accept(LIGHT_CONTROL_REMOTE.get());
                    })
                    .build());

    public GercekciIsikMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        ModNetwork.register();
    }
}
