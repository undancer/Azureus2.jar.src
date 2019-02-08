/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTSkinObjectCombo
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private Combo widget;
/*  52 */   private String text = "";
/*     */   
/*     */   public SWTSkinObjectCombo(SWTSkin skin, SWTSkinProperties properties, String id, String configID, SWTSkinObject parentSkinObject)
/*     */   {
/*  56 */     super(skin, properties, id, configID, "combo", parentSkinObject);
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  59 */     if (this.parent == null) {
/*  60 */       createOn = skin.getShell();
/*     */     } else {
/*  62 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*  65 */     int style = 2048;
/*     */     
/*  67 */     String styleString = properties.getStringValue(this.sConfigID + ".style");
/*  68 */     if (styleString != null) {
/*  69 */       String[] styles = Constants.PAT_SPLIT_COMMA.split(styleString.toLowerCase());
/*  70 */       Arrays.sort(styles);
/*  71 */       if (Arrays.binarySearch(styles, "readonly") >= 0) {
/*  72 */         style |= 0x8;
/*     */       }
/*     */     }
/*     */     
/*  76 */     this.widget = new Combo(createOn, style);
/*     */     
/*  78 */     this.widget.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/*  80 */         SWTSkinObjectCombo.this.text = SWTSkinObjectCombo.this.widget.getText();
/*     */       }
/*     */       
/*  83 */     });
/*  84 */     setControl(this.widget);
/*  85 */     updateFont("");
/*     */   }
/*     */   
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/*  91 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/*  93 */     if (suffix == null) {
/*  94 */       return null;
/*     */     }
/*     */     
/*  97 */     String sPrefix = this.sConfigID + ".text";
/*  98 */     String text = this.properties.getStringValue(sPrefix + suffix);
/*  99 */     if (text != null) {
/* 100 */       setText(text);
/*     */     }
/*     */     
/* 103 */     return suffix;
/*     */   }
/*     */   
/*     */   public void setText(final String val) {
/* 107 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 109 */         if ((SWTSkinObjectCombo.this.widget != null) && (!SWTSkinObjectCombo.this.widget.isDisposed())) {
/* 110 */           SWTSkinObjectCombo.this.widget.setText(val == null ? "" : val);
/* 111 */           SWTSkinObjectCombo.this.text = val;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public String getText()
/*     */   {
/* 119 */     return this.text;
/*     */   }
/*     */   
/*     */   public void setList(final String[] list) {
/* 123 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 125 */         if ((SWTSkinObjectCombo.this.widget != null) && (!SWTSkinObjectCombo.this.widget.isDisposed())) {
/* 126 */           SWTSkinObjectCombo.this.widget.setItems(list);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Combo getComboControl() {
/* 133 */     return this.widget;
/*     */   }
/*     */   
/*     */   private void updateFont(String suffix)
/*     */   {
/* 138 */     String sPrefix = this.sConfigID + ".text";
/*     */     
/* 140 */     Font existingFont = (Font)this.widget.getData("Font" + suffix);
/* 141 */     if ((existingFont != null) && (!existingFont.isDisposed())) {
/* 142 */       this.widget.setFont(existingFont);
/*     */     } else {
/* 144 */       boolean bNewFont = false;
/* 145 */       float fontSize = -1.0F;
/* 146 */       String sFontFace = null;
/* 147 */       FontData[] tempFontData = this.widget.getFont().getFontData();
/*     */       
/* 149 */       sFontFace = this.properties.getStringValue(sPrefix + ".font" + suffix);
/* 150 */       if (sFontFace != null) {
/* 151 */         tempFontData[0].setName(sFontFace);
/* 152 */         bNewFont = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 157 */       String sSize = this.properties.getStringValue(sPrefix + ".size" + suffix);
/* 158 */       if (sSize != null) {
/* 159 */         FontData[] fd = this.widget.getFont().getFontData();
/*     */         
/* 161 */         sSize = sSize.trim();
/*     */         try {
/* 163 */           char firstChar = sSize.charAt(0);
/* 164 */           char lastChar = sSize.charAt(sSize.length() - 1);
/* 165 */           if ((firstChar == '+') || (firstChar == '-')) {
/* 166 */             sSize = sSize.substring(1);
/* 167 */           } else if (lastChar == '%') {
/* 168 */             sSize = sSize.substring(0, sSize.length() - 1);
/*     */           }
/*     */           
/* 171 */           float dSize = NumberFormat.getInstance(Locale.US).parse(sSize).floatValue();
/*     */           
/* 173 */           if (lastChar == '%') {
/* 174 */             fontSize = FontUtils.getHeight(fd) * (dSize / 100.0F);
/* 175 */           } else if (firstChar == '+')
/*     */           {
/*     */ 
/*     */ 
/* 179 */             fontSize = (int)(fd[0].height + dSize);
/* 180 */           } else if (firstChar == '-') {
/* 181 */             fontSize = (int)(fd[0].height - dSize);
/*     */           }
/* 183 */           else if (sSize.endsWith("px"))
/*     */           {
/* 185 */             fontSize = FontUtils.getFontHeightFromPX(this.widget.getDisplay(), tempFontData, null, (int)dSize);
/*     */ 
/*     */           }
/* 188 */           else if (sSize.endsWith("rem")) {
/* 189 */             fontSize = FontUtils.getHeight(fd) * dSize;
/*     */           } else {
/* 191 */             fontSize = FontUtils.getFontHeightFromPX(this.widget.getDisplay(), tempFontData, null, Utils.adjustPXForDPI((int)dSize));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 196 */           bNewFont = true;
/*     */         } catch (NumberFormatException e) {
/* 198 */           e.printStackTrace();
/*     */         }
/*     */         catch (ParseException e) {
/* 201 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/* 205 */       if (bNewFont) {
/* 206 */         FontData[] fd = this.widget.getFont().getFontData();
/*     */         
/* 208 */         if (fontSize > 0.0F) {
/* 209 */           FontUtils.setFontDataHeight(fd, fontSize);
/*     */         }
/*     */         
/* 212 */         if (sFontFace != null) {
/* 213 */           fd[0].setName(sFontFace);
/*     */         }
/*     */         
/* 216 */         final Font textWidgetFont = new Font(this.widget.getDisplay(), fd);
/* 217 */         this.widget.setFont(textWidgetFont);
/* 218 */         this.widget.addDisposeListener(new DisposeListener() {
/*     */           public void widgetDisposed(DisposeEvent e) {
/* 220 */             textWidgetFont.dispose();
/*     */           }
/*     */           
/* 223 */         });
/* 224 */         this.widget.setData("Font" + suffix, textWidgetFont);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectCombo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */