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
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BubbleTextBox;
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
/*     */ public class SWTSkinObjectTextbox
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private Text textWidget;
/*     */   private Composite cBubble;
/*  58 */   private String text = "";
/*     */   
/*     */   public SWTSkinObjectTextbox(SWTSkin skin, SWTSkinProperties properties, String id, String configID, SWTSkinObject parentSkinObject)
/*     */   {
/*  62 */     super(skin, properties, id, configID, "textbox", parentSkinObject);
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  65 */     if (this.parent == null) {
/*  66 */       createOn = skin.getShell();
/*     */     } else {
/*  68 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*  71 */     boolean doBubble = false;
/*  72 */     int style = 2048;
/*     */     
/*  74 */     String styleString = properties.getStringValue(this.sConfigID + ".style");
/*  75 */     if (styleString != null) {
/*  76 */       String[] styles = Constants.PAT_SPLIT_COMMA.split(styleString.toLowerCase());
/*  77 */       Arrays.sort(styles);
/*  78 */       if (Arrays.binarySearch(styles, "readonly") >= 0) {
/*  79 */         style |= 0x8;
/*     */       }
/*  81 */       if (Arrays.binarySearch(styles, "wrap") >= 0) {
/*  82 */         style |= 0x40;
/*     */       }
/*  84 */       if (Arrays.binarySearch(styles, "multiline") >= 0) {
/*  85 */         style |= 0x202;
/*     */       } else {
/*  87 */         style |= 0x4;
/*     */       }
/*  89 */       if (Arrays.binarySearch(styles, "search") >= 0) {
/*  90 */         style |= 0x380;
/*  91 */         if ((Constants.isWindows) || ((Constants.isLinux) && (!getDefaultVisibility())))
/*     */         {
/*  93 */           doBubble = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  98 */     if (((style & 0x40) == 0) && ((style & 0x2) > 0) && (!properties.getBooleanValue(this.sConfigID + ".nohbar", false))) {
/*  99 */       style |= 0x100;
/*     */     }
/*     */     
/*     */ 
/* 103 */     if (!doBubble) {
/* 104 */       this.textWidget = new Text(createOn, style);
/*     */     } else {
/* 106 */       BubbleTextBox bubbleTextBox = new BubbleTextBox(createOn, style);
/* 107 */       this.textWidget = bubbleTextBox.getTextWidget();
/* 108 */       this.cBubble = bubbleTextBox.getParent();
/*     */     }
/*     */     
/* 111 */     this.textWidget.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/* 113 */         SWTSkinObjectTextbox.this.text = SWTSkinObjectTextbox.this.textWidget.getText();
/*     */       }
/*     */       
/* 116 */     });
/* 117 */     String message = properties.getStringValue(configID + ".message", (String)null);
/* 118 */     if ((message != null) && (message.length() > 0)) {
/* 119 */       this.textWidget.setMessage(message);
/*     */     }
/*     */     
/* 122 */     setControl(this.cBubble == null ? this.textWidget : this.cBubble);
/* 123 */     updateFont("");
/*     */   }
/*     */   
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 129 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/* 131 */     if (suffix == null) {
/* 132 */       return null;
/*     */     }
/*     */     
/* 135 */     String sPrefix = this.sConfigID + ".text";
/* 136 */     String text = this.properties.getStringValue(sPrefix + suffix);
/* 137 */     if (text != null) {
/* 138 */       setText(text);
/*     */     }
/*     */     
/* 141 */     return suffix;
/*     */   }
/*     */   
/*     */   public void setText(final String val) {
/* 145 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 147 */         if ((SWTSkinObjectTextbox.this.textWidget != null) && (!SWTSkinObjectTextbox.this.textWidget.isDisposed())) {
/* 148 */           SWTSkinObjectTextbox.this.textWidget.setText(val == null ? "" : val);
/* 149 */           SWTSkinObjectTextbox.this.text = val;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public String getText()
/*     */   {
/* 157 */     return this.text;
/*     */   }
/*     */   
/*     */   public Text getTextControl() {
/* 161 */     return this.textWidget;
/*     */   }
/*     */   
/*     */   private void updateFont(String suffix)
/*     */   {
/* 166 */     String sPrefix = this.sConfigID + ".text";
/*     */     
/* 168 */     Font existingFont = (Font)this.textWidget.getData("Font" + suffix);
/* 169 */     if ((existingFont != null) && (!existingFont.isDisposed())) {
/* 170 */       this.textWidget.setFont(existingFont);
/*     */     } else {
/* 172 */       boolean bNewFont = false;
/* 173 */       float fontSize = -1.0F;
/* 174 */       String sFontFace = null;
/* 175 */       FontData[] tempFontData = this.textWidget.getFont().getFontData();
/*     */       
/* 177 */       sFontFace = this.properties.getStringValue(sPrefix + ".font" + suffix);
/* 178 */       if (sFontFace != null) {
/* 179 */         tempFontData[0].setName(sFontFace);
/* 180 */         bNewFont = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 185 */       String sSize = this.properties.getStringValue(sPrefix + ".size" + suffix);
/* 186 */       if (sSize != null) {
/* 187 */         FontData[] fd = this.textWidget.getFont().getFontData();
/*     */         
/* 189 */         sSize = sSize.trim();
/*     */         try {
/* 191 */           char firstChar = sSize.charAt(0);
/* 192 */           char lastChar = sSize.charAt(sSize.length() - 1);
/* 193 */           if ((firstChar == '+') || (firstChar == '-')) {
/* 194 */             sSize = sSize.substring(1);
/* 195 */           } else if (lastChar == '%') {
/* 196 */             sSize = sSize.substring(0, sSize.length() - 1);
/*     */           }
/*     */           
/* 199 */           float dSize = NumberFormat.getInstance(Locale.US).parse(sSize).floatValue();
/*     */           
/* 201 */           if (lastChar == '%') {
/* 202 */             fontSize = FontUtils.getHeight(fd) * (dSize / 100.0F);
/* 203 */           } else if (firstChar == '+')
/*     */           {
/*     */ 
/*     */ 
/* 207 */             fontSize = (int)(fd[0].height + dSize);
/* 208 */           } else if (firstChar == '-') {
/* 209 */             fontSize = (int)(fd[0].height - dSize);
/*     */           }
/* 211 */           else if (sSize.endsWith("px"))
/*     */           {
/* 213 */             fontSize = FontUtils.getFontHeightFromPX(this.textWidget.getDisplay(), tempFontData, null, (int)dSize);
/*     */ 
/*     */           }
/* 216 */           else if (sSize.endsWith("rem")) {
/* 217 */             fontSize = FontUtils.getHeight(fd) * dSize;
/*     */           } else {
/* 219 */             fontSize = FontUtils.getFontHeightFromPX(this.textWidget.getDisplay(), tempFontData, null, Utils.adjustPXForDPI((int)dSize));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 224 */           bNewFont = true;
/*     */         } catch (NumberFormatException e) {
/* 226 */           e.printStackTrace();
/*     */         }
/*     */         catch (ParseException e) {
/* 229 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/* 233 */       if (bNewFont) {
/* 234 */         FontData[] fd = this.textWidget.getFont().getFontData();
/*     */         
/* 236 */         if (fontSize > 0.0F) {
/* 237 */           FontUtils.setFontDataHeight(fd, fontSize);
/*     */         }
/*     */         
/* 240 */         if (sFontFace != null) {
/* 241 */           fd[0].setName(sFontFace);
/*     */         }
/*     */         
/* 244 */         final Font textWidgetFont = new Font(this.textWidget.getDisplay(), fd);
/* 245 */         this.textWidget.setFont(textWidgetFont);
/* 246 */         this.textWidget.addDisposeListener(new DisposeListener() {
/*     */           public void widgetDisposed(DisposeEvent e) {
/* 248 */             textWidgetFont.dispose();
/*     */           }
/*     */           
/* 251 */         });
/* 252 */         this.textWidget.setData("Font" + suffix, textWidgetFont);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectTextbox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */