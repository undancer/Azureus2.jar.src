/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.ColorDialog;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColorParameter
/*     */   extends Parameter
/*     */   implements ParameterListener
/*     */ {
/*     */   private Button colorChooser;
/*     */   protected String sParamName;
/*     */   private Image img;
/*     */   private int r;
/*     */   private int g;
/*     */   private int b;
/*     */   
/*     */   public ColorParameter(final Composite composite, final String name, int _r, int _g, int _b)
/*     */   {
/*  67 */     super(name);
/*  68 */     this.sParamName = name;
/*  69 */     this.colorChooser = new Button(composite, 8);
/*  70 */     if (name == null) {
/*  71 */       this.r = _r;
/*  72 */       this.g = _g;
/*  73 */       this.b = _b;
/*     */     } else {
/*  75 */       this.r = COConfigurationManager.getIntParameter(name + ".red", _r);
/*  76 */       this.g = COConfigurationManager.getIntParameter(name + ".green", _g);
/*  77 */       this.b = COConfigurationManager.getIntParameter(name + ".blue", _b);
/*  78 */       COConfigurationManager.addParameterListener(this.sParamName, this);
/*     */     }
/*  80 */     updateButtonColor(composite.getDisplay(), this.r, this.g, this.b);
/*     */     
/*     */ 
/*  83 */     this.colorChooser.addListener(12, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  85 */         if (ColorParameter.this.sParamName != null) {
/*  86 */           COConfigurationManager.removeParameterListener(ColorParameter.this.sParamName, ColorParameter.this);
/*     */         }
/*  88 */         if ((ColorParameter.this.img != null) && (!ColorParameter.this.img.isDisposed())) {
/*  89 */           ColorParameter.this.img.dispose();
/*     */         }
/*     */         
/*     */       }
/*  93 */     });
/*  94 */     this.colorChooser.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  96 */         ColorDialog cd = new ColorDialog(composite.getShell());
/*     */         
/*  98 */         List<RGB> custom_colours = Utils.getCustomColors();
/*     */         
/* 100 */         if ((ColorParameter.this.r >= 0) && (ColorParameter.this.g >= 0) && (ColorParameter.this.b >= 0))
/*     */         {
/* 102 */           RGB colour = new RGB(ColorParameter.this.r, ColorParameter.this.g, ColorParameter.this.b);
/*     */           
/* 104 */           custom_colours.remove(colour);
/*     */           
/* 106 */           custom_colours.add(0, colour);
/*     */           
/* 108 */           cd.setRGB(colour);
/*     */         }
/*     */         
/* 111 */         cd.setRGBs((RGB[])custom_colours.toArray(new RGB[0]));
/*     */         
/* 113 */         RGB newColor = cd.open();
/*     */         
/* 115 */         if (newColor == null)
/*     */         {
/* 117 */           return;
/*     */         }
/*     */         
/* 120 */         Utils.updateCustomColors(cd.getRGBs());
/*     */         
/* 122 */         ColorParameter.this.newColorChosen(newColor);
/* 123 */         if (name != null) {
/* 124 */           COConfigurationManager.setRGBParameter(name, newColor.red, newColor.green, newColor.blue);
/*     */         } else {
/* 126 */           ColorParameter.this.r = newColor.red;
/* 127 */           ColorParameter.this.g = newColor.green;
/* 128 */           ColorParameter.this.b = newColor.blue;
/*     */           
/* 130 */           ColorParameter.this.updateButtonColor(ColorParameter.this.colorChooser.getDisplay(), ColorParameter.this.r, ColorParameter.this.g, ColorParameter.this.b);
/*     */         }
/* 132 */         ColorParameter.this.newColorSet(newColor);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void updateButtonColor(Display display, int rV, int gV, int bV)
/*     */   {
/* 139 */     Image oldImg = this.img;
/* 140 */     this.img = new Image(display, 25, 10);
/* 141 */     GC gc = new GC(this.img);
/* 142 */     if ((this.r >= 0) && (this.g >= 0) && (this.b >= 0)) {
/* 143 */       Color color = ColorCache.getColor(display, rV, gV, bV);
/* 144 */       gc.setBackground(color);
/* 145 */       gc.fillRectangle(0, 0, 25, 10);
/*     */     } else {
/* 147 */       Color color = this.colorChooser.getBackground();
/* 148 */       gc.setBackground(color);
/* 149 */       gc.fillRectangle(0, 0, 25, 10);
/* 150 */       new GCStringPrinter(gc, "-", new Rectangle(0, 0, 25, 10), 0, 16777216).printString();
/*     */     }
/*     */     
/* 153 */     gc.dispose();
/* 154 */     this.colorChooser.setImage(this.img);
/* 155 */     if ((oldImg != null) && (!oldImg.isDisposed()))
/* 156 */       oldImg.dispose();
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 160 */     return this.colorChooser;
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 164 */     Utils.adjustPXForDPI(layoutData);
/* 165 */     this.colorChooser.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/* 169 */     this.r = COConfigurationManager.getIntParameter(this.sParamName + ".red");
/* 170 */     this.g = COConfigurationManager.getIntParameter(this.sParamName + ".green");
/* 171 */     this.b = COConfigurationManager.getIntParameter(this.sParamName + ".blue");
/* 172 */     updateButtonColor(this.colorChooser.getDisplay(), this.r, this.g, this.b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void newColorChosen(RGB newColor) {}
/*     */   
/*     */ 
/*     */   public void newColorSet(RGB newColor) {}
/*     */   
/*     */ 
/*     */   public void setValue(Object value) {}
/*     */   
/*     */ 
/*     */   public void setColor(int _r, int _g, int _b)
/*     */   {
/* 188 */     this.r = _r;
/* 189 */     this.g = _g;
/* 190 */     this.b = _b;
/*     */     
/* 192 */     if (this.sParamName == null) {
/* 193 */       updateButtonColor(this.colorChooser.getDisplay(), this.r, this.g, this.b);
/*     */     } else {
/* 195 */       COConfigurationManager.setRGBParameter(this.sParamName, this.r, this.g, this.b);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ColorParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */