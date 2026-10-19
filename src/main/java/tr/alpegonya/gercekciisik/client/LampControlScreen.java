package tr.alpegonya.gercekciisik.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.ModNetwork;
import java.awt.Color;

public final class LampControlScreen extends Screen {
    private static final ResourceLocation KELVIN_GRADIENT=new ResourceLocation("gercekciisik","textures/gui/kelvin_gradient.png");
    private static final ResourceLocation COLOR_WHEEL=new ResourceLocation("gercekciisik","textures/gui/color_wheel.png");
    private static final int WHEEL_SIZE=92;
    private static final String[] RING_NAMES={"Beyaz","Soğuk beyaz","Kirli sarı"};
    private static final int[] RING_RGB={0xFFFFFF,0x9FC9FF,0xE7C982};
    private static final String[] BRIGHTNESS_LABELS={"Çok düşük","Düşük","Orta","Yüksek","Çok yüksek"};
    private static final String[] BRIGHTNESS_CAPTIONS={"Ç.düş.","Düşük","Orta","Yüksek","Ç.yük."};
    private static final String[] BRIGHTNESS_TOOLTIPS={"Çok düşük — loş ışık","Düşük — düşük şiddette ışık",
        "Orta — belirgin oda aydınlatması","Yüksek — güçlü aydınlatma","Çok yüksek — en yüksek standart ışık seviyesi"};
    private final BlockPos pos;
    private final int paletteMode;
    private final boolean fullPalette;
    private int color,rgb,brightnessMode,temperature;
    private boolean powered;
    private Button powerButton;

    public LampControlScreen(BlockPos pos,int color,int rgb,int brightnessMode,int temperature,boolean powered,int paletteMode,String title){
        super(Component.literal(title));
        this.pos=pos;this.color=color;this.rgb=rgb&0xFFFFFF;
        this.brightnessMode=Math.max(0,Math.min(4,brightnessMode));
        this.temperature=Math.max(0,Math.min(18,temperature));
        this.powered=powered;this.paletteMode=paletteMode;this.fullPalette=paletteMode!=2;
        if(fullPalette&&color==LampBlock.MODERN_KELVIN_COLOR)this.rgb=LampBlock.rgb(color,true,this.temperature);
        else if(fullPalette&&color!=LampBlock.CUSTOM_RGB_COLOR&&rgb==0)this.rgb=LampBlock.rgb(color,true,this.temperature);
    }
    private int offset(){return Math.max(0,(height-260)/2);}
    private int wheelX(){return Math.max(8,width/2-148);}
    private int wheelY(){return 31+offset();}
    private int kelvinX(){return wheelX()+WHEEL_SIZE+11;}
    private int kelvinWidth(){return Math.max(112,Math.min(170,width-kelvinX()-8));}
    private int kelvinY(){return 54+offset();}

    private void addBrightnessModeButtons(int center,int y){
        int w=47,h=20,gap=3,total=w*5+gap*4,start=center-total/2;
        for(int i=0;i<5;i++){final int selected=i;
            addRenderableWidget(new BrightnessModeButton(start+i*(w+gap),y,w,h,i,BRIGHTNESS_CAPTIONS[i],
                ()->brightnessMode,this::selectBrightnessMode,BRIGHTNESS_TOOLTIPS[i]));
        }
    }
    @Override protected void init(){
        int center=width/2;
        if(fullPalette){
            int kx=kelvinX(),kw=kelvinWidth(),each=Math.max(48,(kw-8)/2);
            addRenderableWidget(Button.builder(Component.literal("− 500 K"),b->changeTemperature(-1)).bounds(kx,76+offset(),each,18).build());
            addRenderableWidget(Button.builder(Component.literal("+ 500 K"),b->changeTemperature(1)).bounds(kx+each+8,76+offset(),each,18).build());
            addBrightnessModeButtons(center,130+offset());
            powerButton=Button.builder(powerLabel(),b->togglePower()).bounds(center-65,164+offset(),130,20).build();
            addRenderableWidget(powerButton);
        } else {
            int w=66,h=30,gap=8,start=center-(3*w+2*gap)/2;
            for(int i=0;i<3;i++){final int selected=i;
                addRenderableWidget(new ColorSwatchButton(start+i*(w+gap),48+offset(),w,h,i,RING_RGB[i],()->color,
                    this::selectRingColor,Component.literal(RING_NAMES[i])));
            }
            addBrightnessModeButtons(center,108+offset());
            powerButton=Button.builder(powerLabel(),b->togglePower()).bounds(center-65,145+offset(),130,20).build();
            addRenderableWidget(powerButton);
        }
        addRenderableWidget(Button.builder(Component.literal("Kapat"),b->onClose()).bounds(center-40,height-25,80,20).build());
    }
    private Component powerLabel(){return Component.literal(powered?"IŞIĞI KAPAT":"IŞIĞI AÇ");}
    private void selectRingColor(int selected){color=Math.max(0,Math.min(2,selected));rgb=RING_RGB[color];send();}
    private void selectBrightnessMode(int value){brightnessMode=Math.max(0,Math.min(4,value));send();}
    private void changeTemperature(int steps){
        temperature=Math.max(0,Math.min(18,temperature+steps));
        color=LampBlock.MODERN_KELVIN_COLOR;rgb=LampBlock.rgb(color,true,temperature);send();
    }
    private void togglePower(){powered=!powered;send();if(powerButton!=null)powerButton.setMessage(powerLabel());}
    private void send(){ModNetwork.sendConfig(pos,color,rgb,brightnessMode,temperature,powered);}
    private int rgbFromWheel(double dx,double dy){
        double radius=WHEEL_SIZE/2.0-2.0;
        float saturation=(float)Math.max(0.0,Math.min(1.0,Math.sqrt(dx*dx+dy*dy)/radius));
        float hue=(float)((Math.atan2(dy,dx)/(Math.PI*2.0)+1.0)%1.0);
        return Color.HSBtoRGB(hue,saturation,1.0F)&0xFFFFFF;
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        if(button==0&&fullPalette){
            double cx=wheelX()+WHEEL_SIZE/2.0,cy=wheelY()+WHEEL_SIZE/2.0,dx=x-cx,dy=y-cy;
            if(dx*dx+dy*dy<=Math.pow(WHEEL_SIZE/2.0,2)){
                rgb=rgbFromWheel(dx,dy);color=LampBlock.CUSTOM_RGB_COLOR;send();return true;
            }
            int kx=kelvinX(),kw=kelvinWidth(),ky=kelvinY();
            if(x>=kx&&x<kx+kw&&y>=ky&&y<ky+9){
                double f=(x-kx)/Math.max(1.0,kw-1.0);
                temperature=Math.max(0,Math.min(18,(int)Math.round(f*18)));
                color=LampBlock.MODERN_KELVIN_COLOR;rgb=LampBlock.rgb(color,true,temperature);send();return true;
            }
        }
        return super.mouseClicked(x,y,button);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick){
        renderBackground(g);super.render(g,mouseX,mouseY,partialTick);
        int center=width/2,off=offset();g.drawCenteredString(font,title,center,8+off,0xFFFFFF);
        if(fullPalette){
            int wx=wheelX(),wy=wheelY();g.drawCenteredString(font,Component.literal("Renk çarkı"),wx+WHEEL_SIZE/2,19+off,0xD5DDE8);
            g.blit(COLOR_WHEEL,wx,wy,WHEEL_SIZE,WHEEL_SIZE,0,0,128,128,128,128);
            float[] hsv=Color.RGBtoHSB((rgb>>16)&255,(rgb>>8)&255,rgb&255,null);
            double a=hsv[0]*Math.PI*2,r=hsv[1]*(WHEEL_SIZE/2.0-2.0);
            int px=wx+WHEEL_SIZE/2+(int)Math.round(Math.cos(a)*r),py=wy+WHEEL_SIZE/2+(int)Math.round(Math.sin(a)*r);
            g.fill(px-4,py-4,px+4,py+4,0xFF111111);g.fill(px-3,py-3,px+3,py+3,0xFFFFFFFF);g.fill(px-1,py-1,px+1,py+1,0xFF000000);
            int kx=kelvinX(),kw=kelvinWidth(),ky=kelvinY();
            g.drawCenteredString(font,Component.literal((1000+temperature*500)+" K"),kx+kw/2,34+off,0xFFFFFF);
            g.blit(KELVIN_GRADIENT,kx,ky,kw,9,0,0,256,9,256,9);
            g.fill(kx-1,ky-1,kx+kw+1,ky,0xFFFFFFFF);g.fill(kx-1,ky+9,kx+kw+1,ky+10,0xFF4A4A4A);
            int mark=kx+Math.round((temperature/18.0F)*(kw-1));g.fill(mark-1,ky-2,mark+2,ky+11,0xFFFFFFFF);
            g.drawString(font,Component.literal("1000 K"),kx,ky+11,0xFFD7D7D7);
            g.drawString(font,Component.literal("10000 K"),kx+kw-font.width("10000 K"),ky+11,0xFFD7D7D7);
            g.drawString(font,Component.literal(String.format("#%06X",rgb)),kx,98+off,rgb|0xFF000000);
            g.drawCenteredString(font,Component.literal("Parlaklık: "+BRIGHTNESS_LABELS[brightnessMode]),center,116+off,0xFFFFFF);
            g.drawCenteredString(font,Component.literal(powered?"Lamba açık":"Lamba kapalı"),center,188+off,powered?0xBFFFC7:0xFFB8B8);
        }else{
            g.drawCenteredString(font,Component.literal("Üç sabit renk seçeneği"),center,32+off,0xC8D5E5);
            g.drawCenteredString(font,Component.literal("Parlaklık: "+BRIGHTNESS_LABELS[brightnessMode]),center,90+off,0xFFFFFF);
            g.drawCenteredString(font,Component.literal(powered?"Lamba açık":"Lamba kapalı"),center,134+off,powered?0xBFFFC7:0xFFB8B8);
        }
        g.drawCenteredString(font,Component.literal("Işık Kontrol Kumandası"),center,height-40,0x9EB8D2);
    }
    @Override public boolean isPauseScreen(){return false;}
}
