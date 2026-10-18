package tr.alpegonya.gercekciisik;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<LampBlock> TRADITIONAL_FLUORESCENT =
            BLOCKS.register("traditional_fluorescent", () -> new LampBlock(false,
                    BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<LampBlock> MODERN_FLUORESCENT =
            BLOCKS.register("modern_fluorescent", () -> new LampBlock(true,
                    BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Item> TRADITIONAL_ITEM =
            ITEMS.register("traditional_fluorescent",
                    () -> new BlockItem(TRADITIONAL_FLUORESCENT.get(), new Item.Properties()));
    public static final RegistryObject<Item> MODERN_ITEM =
            ITEMS.register("modern_fluorescent",
                    () -> new BlockItem(MODERN_FLUORESCENT.get(), new Item.Properties()));
    public static final RegistryObject<LightingRemoteItem> LIGHT_CONTROL_REMOTE =
            ITEMS.register("lighting_remote", () -> new LightingRemoteItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> LIGHT_TAB = TABS.register("light_control", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gercekciisik.light_control"))
                    .icon(() -> new ItemStack(LIGHT_CONTROL_REMOTE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(TRADITIONAL_ITEM.get());
                        output.accept(MODERN_ITEM.get());
                        output.accept(LIGHT_CONTROL_REMOTE.get());
                    })
                    .build());

    public GercekciIsikMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
        ModNetwork.register();
    }
}
