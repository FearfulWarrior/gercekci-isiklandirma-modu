package tr.alpegonya.gercekciisik.client;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tr.alpegonya.gercekciisik.AdjustableLightBlockEntity;
import tr.alpegonya.gercekciisik.ConfigurableLightBlock;
import tr.alpegonya.gercekciisik.GercekciIsikMod;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.LampBlockEntity;
@Mod.EventBusSubscriber(modid=GercekciIsikMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents(){}
    @SubscribeEvent public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        event.register((state,level,pos,tintIndex)->{
            if(tintIndex!=0||!(state.getBlock() instanceof LampBlock))return 0xFFFFFF;
            if(!state.getValue(LampBlock.POWERED))return 0xBEBEBE;
            int color=state.getValue(LampBlock.COLOR),temp=9,rgb=0xFFFFFF;
            if(level!=null&&pos!=null&&level.getBlockEntity(pos) instanceof LampBlockEntity d){temp=d.getTemperature();rgb=d.getRgb();}
            return color==LampBlock.CUSTOM_RGB_COLOR?rgb&0xFFFFFF:LampBlock.rgb(color,true,temp);
        },GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(),GercekciIsikMod.MODERN_FLUORESCENT.get());
        event.register((state,level,pos,tintIndex)->{
            if(tintIndex!=0||!(state.getBlock() instanceof ConfigurableLightBlock light))return 0xFFFFFF;
            if(!state.getValue(ConfigurableLightBlock.POWERED))return 0xBEBEBE;
            int color=light.getDefaultColor(),temp=9,rgb=LampBlock.rgb(color,true,temp);
            if(level!=null&&pos!=null&&level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity d){
                color=d.getColor();temp=d.getTemperature();rgb=d.getRgb();
            }
            return light.colorFor(color,temp,rgb);
        },GercekciIsikMod.SQUARE_SPOT.get(),GercekciIsikMod.ROUND_SPOT.get(),GercekciIsikMod.TRADITIONAL_RING_LIGHT.get(),
            GercekciIsikMod.MODERN_RING_LIGHT.get(),GercekciIsikMod.HORIZONTAL_LED_STRIP.get(),GercekciIsikMod.VERTICAL_LED_STRIP.get());
    }
}
