package tr.alpegonya.gercekciisik;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class LightingRemoteItem extends Item {
    public LightingRemoteItem(Properties p){super(p);}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();BlockPos pos=context.getClickedPos();BlockState state=level.getBlockState(pos);
        int color,rgb,temp=9,brightness,palette;boolean powered;String title;
        if(state.getBlock() instanceof LampBlock lamp){
            color=state.getValue(LampBlock.COLOR);
            if(level.getBlockEntity(pos) instanceof LampBlockEntity d){temp=d.getTemperature();rgb=d.getRgb();}
            else rgb=LampBlock.rgb(color==LampBlock.CUSTOM_RGB_COLOR?13:color,lamp.isModern(),temp);
            brightness=LampBlock.modeForLightLevel(state.getValue(LampBlock.BRIGHTNESS));
            powered=state.getValue(LampBlock.POWERED);palette=1;
            title=lamp.isModern()?"Modern Floresan Kontrolü":"Geleneksel Floresan Kontrolü";
        }else if(state.getBlock() instanceof ConfigurableLightBlock light){
            if(level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity d){
                color=d.getColor();temp=d.getTemperature();rgb=d.getRgb();
            }else{color=light.getDefaultColor();rgb=light.colorFor(color,temp,LampBlock.rgb(color,true,temp));}
            brightness=state.getValue(ConfigurableLightBlock.BRIGHTNESS);powered=state.getValue(ConfigurableLightBlock.POWERED);palette=light.getPaletteMode();
            title=palette==2?"Geleneksel Simit Armatür Kontrolü":light.isVerticalStrip()?"Dikey Şerit LED Kontrolü":
                light.isStrip()?"Yatay Şerit LED Kontrolü":light==GercekciIsikMod.SQUARE_SPOT.get()?"Kare Spot Kontrolü":
                light==GercekciIsikMod.ROUND_SPOT.get()?"Daire Spot Kontrolü":light==GercekciIsikMod.MODERN_RING_LIGHT.get()?
                "Modern Simit LED Kontrolü":"Işık Kontrolü";
        }else return InteractionResult.PASS;
        if(level.isClientSide){
            final int c=color,r=rgb,t=temp,b=brightness,pm=palette;final boolean p=powered;final String titleCopy=title;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->tr.alpegonya.gercekciisik.client.ClientScreenOpener.open(pos,c,r,b,t,p,pm,titleCopy));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
