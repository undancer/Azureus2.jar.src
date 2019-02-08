/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SWTSkinObjectText1
/*     */   extends SWTSkinObjectBasic
/*     */   implements SWTSkinObjectText
/*     */ {
/*     */   String sText;
/*     */   String sKey;
/*  53 */   boolean bIsTextDefault = false;
/*     */   
/*     */   Label label;
/*     */   
/*     */   private int style;
/*     */   
/*     */   public SWTSkinObjectText1(SWTSkin skin, SWTSkinProperties skinProperties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parent)
/*     */   {
/*  61 */     super(skin, skinProperties, sID, sConfigID, "text", parent);
/*     */     
/*  63 */     String sPrefix = sConfigID + ".text";
/*     */     
/*  65 */     if (this.properties.getBooleanValue(sPrefix + ".wrap", true)) {
/*  66 */       this.style = 64;
/*     */     } else {
/*  68 */       this.style = 0;
/*     */     }
/*     */     
/*  71 */     String sAlign = skinProperties.getStringValue(sConfigID + ".align");
/*  72 */     if (sAlign != null) {
/*  73 */       int align = SWTSkinUtils.getAlignment(sAlign, 0);
/*  74 */       if (align != 0) {
/*  75 */         this.style |= align;
/*     */       }
/*     */     }
/*     */     
/*  79 */     if (skinProperties.getIntValue(sConfigID + ".border", 0) == 1) {
/*  80 */       this.style |= 0x800;
/*     */     }
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  84 */     if (parent == null) {
/*  85 */       createOn = skin.getShell();
/*     */     } else {
/*  87 */       createOn = (Composite)parent.getControl();
/*     */     }
/*     */     
/*  90 */     boolean bKeepMaxSize = this.properties.getStringValue(sConfigID + ".keepMaxSize", "").equals("1");
/*     */     
/*  92 */     this.label = (bKeepMaxSize ? new LabelNoShrink(createOn, this.style) : new Label(createOn, this.style));
/*     */     
/*  94 */     setControl(this.label);
/*  95 */     if (typeParams.length > 1) {
/*  96 */       this.bIsTextDefault = true;
/*  97 */       this.sText = typeParams[1];
/*  98 */       this.label.setText(this.sText);
/*     */     }
/*     */   }
/*     */   
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown) {
/* 103 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/* 104 */     if (suffix == null) {
/* 105 */       return null;
/*     */     }
/*     */     
/* 108 */     String sPrefix = this.sConfigID + ".text";
/*     */     
/* 110 */     if ((this.sText == null) || (this.bIsTextDefault)) {
/* 111 */       String text = this.properties.getStringValue(sPrefix + suffix);
/* 112 */       if (text != null) {
/* 113 */         this.label.setText(text);
/*     */       }
/*     */     }
/*     */     
/* 117 */     Color color = this.properties.getColor(sPrefix + ".color" + suffix);
/*     */     
/* 119 */     if (color != null) {
/* 120 */       this.label.setForeground(color);
/*     */     }
/*     */     
/* 123 */     Font existingFont = (Font)this.label.getData("Font" + suffix);
/* 124 */     if ((existingFont != null) && (!existingFont.isDisposed())) {
/* 125 */       this.label.setFont(existingFont);
/*     */     } else {
/* 127 */       boolean bNewFont = false;
/* 128 */       float fontSize = -1.0F;
/* 129 */       int iFontWeight = -1;
/* 130 */       String sFontFace = null;
/*     */       
/* 132 */       String sSize = this.properties.getStringValue(sPrefix + ".size" + suffix);
/* 133 */       if (sSize != null) {
/* 134 */         FontData[] fd = this.label.getFont().getFontData();
/*     */         try
/*     */         {
/* 137 */           char firstChar = sSize.charAt(0);
/* 138 */           if ((firstChar == '+') || (firstChar == '-')) {
/* 139 */             sSize = sSize.substring(1);
/*     */           }
/*     */           
/* 142 */           if (sSize.endsWith("%")) {
/* 143 */             sSize = sSize.substring(0, sSize.length() - 1);
/* 144 */             float pctSize = NumberFormat.getInstance(Locale.US).parse(sSize).floatValue();
/* 145 */             fontSize = FontUtils.getHeight(fd) * pctSize;
/*     */           }
/*     */           else {
/* 148 */             float dSize = NumberFormat.getInstance(Locale.US).parse(sSize).floatValue();
/*     */             
/* 150 */             if (firstChar == '+') {
/* 151 */               fontSize = (int)(fd[0].height + dSize);
/* 152 */             } else if (firstChar == '-') {
/* 153 */               fontSize = (int)(fd[0].height - dSize);
/*     */             } else {
/* 155 */               fontSize = dSize;
/*     */             }
/*     */             
/* 158 */             if (sSize.endsWith("px")) {
/* 159 */               fontSize = FontUtils.getFontHeightFromPX(this.label.getFont(), null, (int)dSize);
/* 160 */             } else if (sSize.endsWith("rem")) {
/* 161 */               fontSize = FontUtils.getHeight(fd) * dSize;
/*     */             }
/*     */           }
/*     */           
/* 165 */           bNewFont = true;
/*     */         } catch (NumberFormatException e) {
/* 167 */           e.printStackTrace();
/*     */         }
/*     */         catch (ParseException e) {
/* 170 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/* 174 */       String sStyle = this.properties.getStringValue(sPrefix + ".style" + suffix);
/* 175 */       if (sStyle != null) {
/* 176 */         String[] sStyles = Constants.PAT_SPLIT_COMMA.split(sStyle.toLowerCase());
/* 177 */         for (int i = 0; i < sStyles.length; i++) {
/* 178 */           String s = sStyles[i];
/* 179 */           if (s.equals("bold")) {
/* 180 */             if (iFontWeight == -1) {
/* 181 */               iFontWeight = 1;
/*     */             } else {
/* 183 */               iFontWeight |= 0x1;
/*     */             }
/* 185 */             bNewFont = true;
/*     */           }
/*     */           
/* 188 */           if (s.equals("italic")) {
/* 189 */             if (iFontWeight == -1) {
/* 190 */               iFontWeight = 2;
/*     */             } else {
/* 192 */               iFontWeight |= 0x2;
/*     */             }
/* 194 */             bNewFont = true;
/*     */           }
/*     */           
/* 197 */           if (s.equals("underline")) {
/* 198 */             this.label.addPaintListener(new PaintListener() {
/*     */               public void paintControl(PaintEvent e) {
/* 200 */                 Point size = ((Control)e.widget).getSize();
/* 201 */                 e.gc.drawLine(0, size.y - 1, size.x - 1, size.y - 1);
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 206 */           if (s.equals("strike")) {
/* 207 */             this.label.addPaintListener(new PaintListener() {
/*     */               public void paintControl(PaintEvent e) {
/* 209 */                 Point size = ((Control)e.widget).getSize();
/* 210 */                 int y = size.y / 2;
/* 211 */                 e.gc.drawLine(0, y, size.x - 1, y);
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 218 */       sFontFace = this.properties.getStringValue(sPrefix + ".font" + suffix);
/* 219 */       if (sFontFace != null) {
/* 220 */         bNewFont = true;
/*     */       }
/*     */       
/* 223 */       if (bNewFont) {
/* 224 */         FontData[] fd = this.label.getFont().getFontData();
/*     */         
/* 226 */         if (fontSize > 0.0F) {
/* 227 */           FontUtils.setFontDataHeight(fd, fontSize);
/*     */         }
/*     */         
/* 230 */         if (iFontWeight >= 0) {
/* 231 */           fd[0].setStyle(iFontWeight);
/*     */         }
/*     */         
/* 234 */         if (sFontFace != null) {
/* 235 */           fd[0].setName(sFontFace);
/*     */         }
/*     */         
/* 238 */         final Font labelFont = new Font(this.label.getDisplay(), fd);
/* 239 */         this.label.setFont(labelFont);
/* 240 */         this.label.addDisposeListener(new DisposeListener() {
/*     */           public void widgetDisposed(DisposeEvent e) {
/* 242 */             labelFont.dispose();
/*     */           }
/*     */           
/* 245 */         });
/* 246 */         this.label.setData("Font" + suffix, labelFont);
/*     */       }
/*     */     }
/*     */     
/* 250 */     this.label.update();
/*     */     
/* 252 */     return suffix;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 259 */     if (text == null) {
/* 260 */       text = "";
/*     */     }
/*     */     
/* 263 */     if (text.equals(this.sText)) {
/* 264 */       return;
/*     */     }
/*     */     
/* 267 */     this.sText = text;
/* 268 */     this.sKey = null;
/* 269 */     this.bIsTextDefault = false;
/*     */     
/* 271 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 273 */         if ((SWTSkinObjectText1.this.label != null) && (!SWTSkinObjectText1.this.label.isDisposed())) {
/* 274 */           SWTSkinObjectText1.this.label.setText(SWTSkinObjectText1.this.sText);
/* 275 */           Utils.relayout(SWTSkinObjectText1.this.label);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setTextID(final String key) {
/* 282 */     if (key == null) {
/* 283 */       setText("");
/*     */ 
/*     */     }
/* 286 */     else if (key.equals(this.sKey)) {
/* 287 */       return;
/*     */     }
/*     */     
/* 290 */     this.sText = MessageText.getString(key);
/* 291 */     this.sKey = key;
/* 292 */     this.bIsTextDefault = false;
/*     */     
/* 294 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 296 */         if ((SWTSkinObjectText1.this.label != null) && (!SWTSkinObjectText1.this.label.isDisposed())) {
/* 297 */           Messages.setLanguageText(SWTSkinObjectText1.this.label, key);
/* 298 */           Utils.relayout(SWTSkinObjectText1.this.label);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setTextID(final String key, final String[] params) {
/* 305 */     if (key == null) {
/* 306 */       setText("");
/* 307 */     } else if (key.equals(this.sKey)) {
/* 308 */       return;
/*     */     }
/*     */     
/* 311 */     this.sText = MessageText.getString(key);
/* 312 */     this.sKey = key;
/* 313 */     this.bIsTextDefault = false;
/*     */     
/* 315 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 317 */         if ((SWTSkinObjectText1.this.label != null) && (!SWTSkinObjectText1.this.label.isDisposed())) {
/* 318 */           Messages.setLanguageText(SWTSkinObjectText1.this.label, key, params);
/* 319 */           Utils.relayout(SWTSkinObjectText1.this.label);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LabelNoShrink
/*     */     extends Label
/*     */   {
/*     */     Point ptMax;
/*     */     
/*     */ 
/*     */ 
/*     */     public LabelNoShrink(Composite parent, int style)
/*     */     {
/* 337 */       super(style | 0x1000000);
/* 338 */       this.ptMax = new Point(0, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkSubclass() {}
/*     */     
/*     */     public Point computeSize(int wHint, int hHint, boolean changed)
/*     */     {
/* 346 */       Point pt = super.computeSize(wHint, hHint, changed);
/* 347 */       if (pt.x > this.ptMax.x) {
/* 348 */         this.ptMax.x = pt.x;
/*     */       }
/* 350 */       if (pt.y > this.ptMax.y) {
/* 351 */         this.ptMax.y = pt.y;
/*     */       }
/*     */       
/* 354 */       return this.ptMax;
/*     */     }
/*     */   }
/*     */   
/*     */   public int getStyle()
/*     */   {
/* 360 */     return this.style;
/*     */   }
/*     */   
/*     */   public void setStyle(int style)
/*     */   {
/* 365 */     this.style = style;
/*     */   }
/*     */   
/*     */   public String getText()
/*     */   {
/* 370 */     return this.sText;
/*     */   }
/*     */   
/*     */   public void addUrlClickedListener(SWTSkinObjectText_UrlClickedListener l) {}
/*     */   
/*     */   public void removeUrlClickedListener(SWTSkinObjectText_UrlClickedListener l) {}
/*     */   
/*     */   public void setTextColor(Color color) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectText1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */