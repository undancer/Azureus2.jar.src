/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.io.PrintStream;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTSkinObjectText2
/*     */   extends SWTSkinObjectBasic
/*     */   implements SWTSkinObjectText, PaintListener
/*     */ {
/*     */   String sText;
/*     */   String sDisplayText;
/*     */   String sKey;
/*  59 */   boolean bIsTextDefault = false;
/*     */   
/*     */   private int style;
/*     */   
/*     */   private Canvas canvas;
/*     */   
/*     */   private boolean isUnderline;
/*     */   
/*  67 */   private int antialiasMode = -1;
/*     */   
/*     */   private boolean isAllcaps;
/*     */   
/*     */   private boolean hasShadow;
/*     */   
/*     */   private int hpadding;
/*     */   
/*     */   private int vpadding;
/*     */   
/*  77 */   private boolean relayoutOnTextChange = true;
/*     */   
/*  79 */   private boolean isItalic = false;
/*     */   
/*  81 */   private static Font font = null;
/*     */   
/*     */   private GCStringPrinter lastStringPrinter;
/*     */   
/*     */   private Color colorUrl;
/*     */   
/*  87 */   private int alpha = 255;
/*     */   
/*  89 */   private List<SWTSkinObjectText_UrlClickedListener> listUrlClickedListeners = new ArrayList();
/*     */   
/*     */   private Color colorUrl2;
/*     */   
/*     */   private Color explicitColor;
/*     */   
/*     */   protected boolean mouseDown;
/*     */   
/*     */   private SWTColorWithAlpha colorShadow;
/*     */   
/*     */ 
/*     */   public SWTSkinObjectText2(SWTSkin skin, final SWTSkinProperties skinProperties, String sID, final String sConfigID, String[] typeParams, SWTSkinObject parent)
/*     */   {
/* 102 */     super(skin, skinProperties, sID, sConfigID, "text", parent);
/*     */     
/* 104 */     String sPrefix = sConfigID + ".text";
/*     */     
/* 106 */     if (this.properties.getBooleanValue(sPrefix + ".wrap", true)) {
/* 107 */       this.style = 64;
/*     */     } else {
/* 109 */       this.style = 0;
/*     */     }
/*     */     
/* 112 */     String sAlign = skinProperties.getStringValue(sConfigID + ".align");
/* 113 */     if (sAlign != null) {
/* 114 */       int align = SWTSkinUtils.getAlignment(sAlign, 0);
/* 115 */       if (align != 0) {
/* 116 */         this.style |= align;
/*     */       }
/*     */     }
/*     */     
/* 120 */     String sVAlign = skinProperties.getStringValue(sConfigID + ".v-align");
/* 121 */     if (sVAlign != null) {
/* 122 */       int align = SWTSkinUtils.getAlignment(sVAlign, 0);
/* 123 */       if (align != 16777216) {
/* 124 */         if (align != 0) {
/* 125 */           this.style |= align;
/*     */         } else {
/* 127 */           this.style |= 0x80;
/*     */         }
/*     */       }
/*     */     } else {
/* 131 */       this.style |= 0x80;
/*     */     }
/*     */     
/* 134 */     int canvasStyle = 536870912;
/* 135 */     if ((skinProperties.getIntValue(sConfigID + ".border", 0) == 1) || (skin.DEBUGLAYOUT)) {
/* 136 */       canvasStyle |= 0x800;
/*     */     }
/*     */     
/* 139 */     String sAntiAlias = skinProperties.getStringValue(sConfigID + ".antialias", (String)null);
/*     */     
/* 141 */     if ((sAntiAlias != null) && (sAntiAlias.length() > 0)) {
/* 142 */       this.antialiasMode = ((sAntiAlias.equals("1")) || (sAntiAlias.toLowerCase().equals("true")) ? 1 : 0);
/*     */     }
/*     */     
/*     */ 
/* 146 */     this.relayoutOnTextChange = skinProperties.getBooleanValue(sConfigID + ".text.relayoutOnChange", true);
/*     */     
/*     */     Composite createOn;
/*     */     Composite createOn;
/* 150 */     if (parent == null) {
/* 151 */       createOn = skin.getShell();
/*     */     } else {
/* 153 */       createOn = (Composite)parent.getControl();
/*     */     }
/*     */     
/* 156 */     this.canvas = new Canvas(createOn, canvasStyle) {
/* 157 */       Point ptMax = new Point(0, 0);
/*     */       
/*     */       public Point computeSize(int wHint, int hHint, boolean changed)
/*     */       {
/* 161 */         int border = getBorderWidth() * 2;
/* 162 */         if ((border == 0) && ((SWTSkinObjectText2.this.canvas.getStyle() & 0x800) > 0)) {
/* 163 */           border = 2;
/*     */         }
/* 165 */         Point pt = new Point(border, border);
/*     */         
/* 167 */         if (SWTSkinObjectText2.this.sDisplayText == null) {
/* 168 */           return pt;
/*     */         }
/*     */         
/* 171 */         Font existingFont = (Font)SWTSkinObjectText2.this.canvas.getData("font");
/* 172 */         Color existingColor = (Color)SWTSkinObjectText2.this.canvas.getData("color");
/*     */         
/* 174 */         GC gc = new GC(this);
/* 175 */         if (existingFont != null) {
/* 176 */           gc.setFont(existingFont);
/*     */         }
/* 178 */         if (existingColor != null) {
/* 179 */           gc.setForeground(existingColor);
/*     */         }
/* 181 */         if (SWTSkinObjectText2.this.antialiasMode != -1) {
/*     */           try {
/* 183 */             gc.setTextAntialias(SWTSkinObjectText2.this.antialiasMode);
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */         
/*     */ 
/* 189 */         gc.setAlpha(SWTSkinObjectText2.this.alpha);
/*     */         
/* 191 */         GCStringPrinter sp = new GCStringPrinter(gc, SWTSkinObjectText2.this.sDisplayText, new Rectangle(0, 0, wHint == -1 ? 3000 : wHint, hHint == -1 ? 3000 : hHint), true, false, SWTSkinObjectText2.this.style & 0x40);
/*     */         
/*     */ 
/* 194 */         sp.calculateMetrics();
/* 195 */         pt = sp.getCalculatedSize();
/* 196 */         pt.x += (border + SWTSkinObjectText2.this.hpadding) * 2;
/* 197 */         pt.y += (border + SWTSkinObjectText2.this.vpadding) * 2;
/* 198 */         gc.dispose();
/*     */         
/* 200 */         if (SWTSkinObjectText2.this.isUnderline) {
/* 201 */           pt.y += 1;
/*     */         }
/* 203 */         if (SWTSkinObjectText2.this.hasShadow) {
/* 204 */           pt.x += 1;
/*     */         }
/* 206 */         if (SWTSkinObjectText2.this.isItalic) {
/* 207 */           pt.x += 4;
/*     */         }
/*     */         
/* 210 */         int fixedWidth = skinProperties.getIntValue(sConfigID + ".width", -1);
/* 211 */         if (fixedWidth >= 0) {
/* 212 */           pt.x = Utils.adjustPXForDPI(fixedWidth);
/*     */         }
/*     */         
/* 215 */         int fixedHeight = skinProperties.getIntValue(sConfigID + ".height", -1);
/* 216 */         if (fixedHeight >= 0) {
/* 217 */           pt.y = Utils.adjustPXForDPI(fixedHeight);
/*     */         }
/*     */         
/*     */ 
/* 221 */         if (isVisible()) {
/* 222 */           if (pt.x > this.ptMax.x) {
/* 223 */             this.ptMax.x = pt.x;
/*     */           }
/* 225 */           if (pt.y > this.ptMax.y) {
/* 226 */             this.ptMax.y = pt.y;
/*     */           }
/*     */         }
/*     */         
/* 230 */         return pt;
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 240 */     };
/* 241 */     this.canvas.setData("font", font);
/* 242 */     setControl(this.canvas);
/* 243 */     if (typeParams.length > 1) {
/* 244 */       this.bIsTextDefault = true;
/* 245 */       this.sText = typeParams[1];
/*     */       
/* 247 */       for (int i = 2; i < typeParams.length; i++) {
/* 248 */         this.sText = (this.sText + ", " + typeParams[i]);
/*     */       }
/* 250 */       this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */     }
/*     */     
/*     */ 
/* 254 */     this.canvas.addMouseListener(new MouseListener() {
/*     */       private String lastDownURL;
/*     */       
/*     */       public void mouseUp(MouseEvent e) {
/* 258 */         SWTSkinObjectText2.this.mouseDown = false;
/* 259 */         if (SWTSkinObjectText2.this.lastStringPrinter != null) {
/* 260 */           GCStringPrinter.URLInfo hitUrl = SWTSkinObjectText2.this.lastStringPrinter.getHitUrl(e.x, e.y);
/* 261 */           if (hitUrl != null)
/*     */           {
/* 263 */             SWTSkinObjectText_UrlClickedListener[] listeners = (SWTSkinObjectText_UrlClickedListener[])SWTSkinObjectText2.this.listUrlClickedListeners.toArray(new SWTSkinObjectText_UrlClickedListener[0]);
/* 264 */             for (SWTSkinObjectText_UrlClickedListener l : listeners) {
/* 265 */               if (l.urlClicked(hitUrl)) {
/* 266 */                 return;
/*     */               }
/*     */             }
/*     */             
/* 270 */             String url = hitUrl.url;
/*     */             try {
/* 272 */               if (url.startsWith("/")) {
/* 273 */                 url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL(url, true);
/*     */               }
/*     */               
/*     */ 
/* 277 */               if (url.contains("?")) {
/* 278 */                 url = url + "&";
/*     */               } else {
/* 280 */                 url = url + "?";
/*     */               }
/* 282 */               url = url + "fromWeb=false&os.version=" + UrlUtils.encode(System.getProperty("os.version")) + "&java.version=" + UrlUtils.encode(Constants.JAVA_VERSION);
/*     */             }
/*     */             catch (Throwable t) {}
/*     */             
/*     */ 
/*     */ 
/* 288 */             Utils.launch(url);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void mouseDown(MouseEvent e) {
/* 294 */         SWTSkinObjectText2.this.mouseDown = true;
/* 295 */         if (SWTSkinObjectText2.this.lastStringPrinter != null) {
/* 296 */           GCStringPrinter.URLInfo hitUrl = SWTSkinObjectText2.this.lastStringPrinter.getHitUrl(e.x, e.y);
/* 297 */           String curURL = hitUrl == null ? "" : hitUrl.url;
/*     */           
/* 299 */           if (curURL.equals(this.lastDownURL)) {
/* 300 */             this.lastDownURL = curURL;
/* 301 */             SWTSkinObjectText2.this.canvas.redraw();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void mouseDoubleClick(MouseEvent e) {}
/* 309 */     });
/* 310 */     this.canvas.addMouseMoveListener(new MouseMoveListener() {
/* 311 */       Boolean doUrlToolTip = null;
/*     */       
/* 313 */       public void mouseMove(MouseEvent e) { if ((SWTSkinObjectText2.this.lastStringPrinter != null) && (SWTSkinObjectText2.this.lastStringPrinter.hasHitUrl())) {
/* 314 */           GCStringPrinter.URLInfo hitUrl = SWTSkinObjectText2.this.lastStringPrinter.getHitUrl(e.x, e.y);
/* 315 */           if (this.doUrlToolTip == null) {
/* 316 */             this.doUrlToolTip = Boolean.valueOf(SWTSkinObjectText2.this.getTooltipID(false) == null);
/*     */           }
/* 318 */           if (this.doUrlToolTip.booleanValue()) {
/* 319 */             String tooltip = null;
/* 320 */             if (hitUrl != null) {
/* 321 */               if (hitUrl.title == null) {
/* 322 */                 tooltip = "!" + hitUrl.url + "!";
/*     */               } else {
/* 324 */                 tooltip = "!" + hitUrl.title + " (" + hitUrl.url + ")!";
/*     */               }
/*     */             }
/* 327 */             SWTSkinObjectText2.this.setTooltipID(tooltip);
/*     */           }
/* 329 */           SWTSkinObjectText2.this.canvas.setCursor(hitUrl == null ? null : SWTSkinObjectText2.this.canvas.getDisplay().getSystemCursor(21));
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 335 */     if (skinProperties.getBooleanValue(sConfigID + ".clipboardmenu", false)) {
/* 336 */       Menu menu = new Menu(this.canvas);
/* 337 */       MenuItem menuItem = new MenuItem(menu, 8);
/* 338 */       Messages.setLanguageText(menuItem, "MyTorrentsView.menu.thisColumn.toClipboard");
/* 339 */       menuItem.addSelectionListener(new SelectionListener() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 341 */           ClipboardCopy.copyToClipBoard(SWTSkinObjectText2.this.getText());
/*     */         }
/*     */         
/*     */ 
/*     */         public void widgetDefaultSelected(SelectionEvent e) {}
/* 346 */       });
/* 347 */       this.canvas.setMenu(menu);
/*     */     }
/*     */     
/* 350 */     setAlwaysHookPaintListener(true);
/*     */     
/* 352 */     updateFont("");
/*     */   }
/*     */   
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 357 */     boolean forceSwitch = suffix == null;
/* 358 */     String oldSuffix = getSuffix();
/* 359 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/* 360 */     if (suffix == null) {
/* 361 */       return null;
/*     */     }
/* 363 */     if ((!forceSwitch) && (suffix.equals(oldSuffix))) {
/* 364 */       return suffix;
/*     */     }
/*     */     
/* 367 */     String sPrefix = this.sConfigID + ".text";
/*     */     
/* 369 */     if ((this.sText == null) || (this.bIsTextDefault)) {
/* 370 */       String text = this.properties.getStringValue(sPrefix + suffix);
/* 371 */       if (text != null) {
/* 372 */         this.sText = text;
/* 373 */         this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 378 */     final String fSuffix = suffix;
/* 379 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 381 */         if ((SWTSkinObjectText2.this.canvas == null) || (SWTSkinObjectText2.this.canvas.isDisposed())) {
/* 382 */           return;
/*     */         }
/* 384 */         SWTSkinObjectText2.this.updateFont(fSuffix);
/*     */       }
/* 386 */     });
/* 387 */     return suffix;
/*     */   }
/*     */   
/*     */   private void updateFont(String suffix) {
/* 391 */     String sPrefix = this.sConfigID + ".text";
/*     */     
/* 393 */     Color newColorURL = this.properties.getColor(sPrefix + ".urlcolor" + suffix);
/* 394 */     if (newColorURL != null) {
/* 395 */       this.colorUrl = newColorURL;
/*     */     } else {
/* 397 */       this.colorUrl = this.properties.getColor(sPrefix + ".urlcolor");
/*     */     }
/*     */     
/* 400 */     Color newColorURL2 = this.properties.getColor(sPrefix + ".urlcolor-pressed");
/* 401 */     if (newColorURL2 != null) {
/* 402 */       this.colorUrl2 = newColorURL2;
/*     */     }
/*     */     
/* 405 */     if (this.explicitColor == null) {
/* 406 */       Color color = this.properties.getColor(sPrefix + ".color" + suffix);
/* 407 */       if (this.debug) {
/* 408 */         System.out.println(this + "; " + sPrefix + ";" + suffix + "; " + color + "; " + getText());
/*     */       }
/* 410 */       if (color != null) {
/* 411 */         this.canvas.setData("color", color);
/*     */       } else {
/* 413 */         this.canvas.setData("color", this.properties.getColor(sPrefix + ".color"));
/*     */       }
/*     */     }
/*     */     
/* 417 */     this.alpha = this.properties.getIntValue(this.sConfigID + ".alpha", 255);
/*     */     
/* 419 */     this.hpadding = this.properties.getIntValue(sPrefix + ".h-padding", 0);
/* 420 */     this.vpadding = this.properties.getIntValue(sPrefix + ".v-padding", 0);
/*     */     
/* 422 */     Font existingFont = (Font)this.canvas.getData("Font" + suffix);
/* 423 */     if ((existingFont != null) && (!existingFont.isDisposed())) {
/* 424 */       this.canvas.setData("font", existingFont);
/*     */     } else {
/* 426 */       boolean bNewFont = false;
/* 427 */       float fontSize = -1.0F;
/* 428 */       int iFontWeight = -1;
/* 429 */       String sFontFace = null;
/* 430 */       FontData[] tempFontData = this.canvas.getFont().getFontData();
/*     */       
/* 432 */       sFontFace = this.properties.getStringValue(sPrefix + ".font" + suffix);
/* 433 */       if (sFontFace != null) {
/* 434 */         tempFontData[0].setName(sFontFace);
/* 435 */         bNewFont = true;
/*     */       }
/*     */       
/* 438 */       String sStyle = this.properties.getStringValue(sPrefix + ".style" + suffix);
/* 439 */       if (sStyle != null) {
/* 440 */         this.isAllcaps = false;
/* 441 */         String[] sStyles = Constants.PAT_SPLIT_COMMA.split(sStyle.toLowerCase());
/* 442 */         for (int i = 0; i < sStyles.length; i++) {
/* 443 */           String s = sStyles[i];
/*     */           
/* 445 */           if (s.equals("allcaps")) {
/* 446 */             this.isAllcaps = true;
/*     */           }
/*     */           
/* 449 */           if (s.equals("bold")) {
/* 450 */             if (iFontWeight == -1) {
/* 451 */               iFontWeight = 1;
/*     */             } else {
/* 453 */               iFontWeight |= 0x1;
/*     */             }
/* 455 */             bNewFont = true;
/*     */           }
/*     */           
/* 458 */           if (s.equals("italic")) {
/* 459 */             if (iFontWeight == -1) {
/* 460 */               iFontWeight = 2;
/*     */             } else {
/* 462 */               iFontWeight |= 0x2;
/*     */             }
/* 464 */             bNewFont = true;
/* 465 */             this.isItalic = true;
/*     */           } else {
/* 467 */             this.isItalic = false;
/*     */           }
/*     */           
/* 470 */           this.isUnderline = s.equals("underline");
/* 471 */           if (this.isUnderline) {
/* 472 */             this.canvas.addPaintListener(new PaintListener() {
/*     */               public void paintControl(PaintEvent e) {
/* 474 */                 int x = 0;
/* 475 */                 Point pt = e.gc.textExtent(SWTSkinObjectText2.this.sDisplayText);
/* 476 */                 Point size = ((Control)e.widget).getSize();
/* 477 */                 if (pt.x < size.x) {
/* 478 */                   x = size.x - pt.x;
/* 479 */                   size.x = pt.x;
/*     */                 }
/* 481 */                 e.gc.drawLine(x, size.y - 1, size.x - 1 + x, size.y - 1);
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 486 */           if (s.equals("strike")) {
/* 487 */             this.canvas.addPaintListener(new PaintListener() {
/*     */               public void paintControl(PaintEvent e) {
/* 489 */                 Point size = ((Control)e.widget).getSize();
/* 490 */                 int y = size.y / 2;
/* 491 */                 e.gc.drawLine(0, y, size.x - 1, y);
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 496 */           if (s.equals("normal")) {
/* 497 */             bNewFont = true;
/*     */           }
/*     */           
/* 500 */           if (s.equals("shadow")) {
/* 501 */             this.hasShadow = true;
/*     */           }
/*     */         }
/* 504 */         this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */       }
/*     */       
/*     */ 
/* 508 */       this.colorShadow = this.properties.getColorWithAlpha(sPrefix + ".shadow");
/* 509 */       if (this.colorShadow.color != null) {
/* 510 */         this.hasShadow = true;
/*     */       }
/*     */       
/*     */ 
/* 514 */       if (iFontWeight >= 0) {
/* 515 */         tempFontData[0].setStyle(iFontWeight);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 520 */       String sSize = this.properties.getStringValue(sPrefix + ".size" + suffix);
/* 521 */       if (sSize != null) {
/* 522 */         FontData[] fd = this.canvas.getFont().getFontData();
/*     */         
/* 524 */         sSize = sSize.trim();
/*     */         try {
/* 526 */           char firstChar = sSize.charAt(0);
/* 527 */           char lastChar = sSize.charAt(sSize.length() - 1);
/* 528 */           if ((firstChar == '+') || (firstChar == '-')) {
/* 529 */             sSize = sSize.substring(1);
/* 530 */           } else if (lastChar == '%') {
/* 531 */             sSize = sSize.substring(0, sSize.length() - 1);
/*     */           }
/*     */           
/* 534 */           float dSize = NumberFormat.getInstance(Locale.US).parse(sSize).floatValue();
/*     */           
/* 536 */           if (lastChar == '%') {
/* 537 */             fontSize = FontUtils.getHeight(fd) * (dSize / 100.0F);
/* 538 */           } else if (firstChar == '+')
/*     */           {
/*     */ 
/*     */ 
/* 542 */             fontSize = (int)(fd[0].height + dSize);
/* 543 */           } else if (firstChar == '-') {
/* 544 */             fontSize = (int)(fd[0].height - dSize);
/*     */           }
/* 546 */           else if (sSize.endsWith("px"))
/*     */           {
/* 548 */             fontSize = FontUtils.getFontHeightFromPX(this.canvas.getDisplay(), tempFontData, null, (int)dSize);
/*     */ 
/*     */           }
/* 551 */           else if (sSize.endsWith("rem")) {
/* 552 */             fontSize = FontUtils.getHeight(fd) * dSize;
/*     */           } else {
/* 554 */             fontSize = (int)dSize;
/*     */           }
/*     */           
/*     */ 
/* 558 */           bNewFont = true;
/*     */         } catch (NumberFormatException e) {
/* 560 */           e.printStackTrace();
/*     */         }
/*     */         catch (ParseException e) {
/* 563 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/* 567 */       if (bNewFont) {
/* 568 */         FontData[] fd = this.canvas.getFont().getFontData();
/*     */         
/* 570 */         if (fontSize > 0.0F) {
/* 571 */           FontUtils.setFontDataHeight(fd, fontSize);
/*     */         }
/*     */         
/* 574 */         if (iFontWeight >= 0) {
/* 575 */           fd[0].setStyle(iFontWeight);
/*     */         }
/*     */         
/* 578 */         if (sFontFace != null) {
/* 579 */           fd[0].setName(sFontFace);
/*     */         }
/*     */         
/* 582 */         final Font canvasFont = new Font(this.canvas.getDisplay(), fd);
/* 583 */         this.canvas.setData("font", canvasFont);
/* 584 */         this.canvas.addDisposeListener(new DisposeListener() {
/*     */           public void widgetDisposed(DisposeEvent e) {
/* 586 */             canvasFont.dispose();
/*     */           }
/*     */           
/* 589 */         });
/* 590 */         this.canvas.setData("Font" + suffix, canvasFont);
/*     */       }
/*     */     }
/*     */     
/* 594 */     this.canvas.redraw();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 601 */     if (text == null) {
/* 602 */       text = "";
/*     */     }
/*     */     
/* 605 */     if (text.equals(this.sText)) {
/* 606 */       return;
/*     */     }
/*     */     
/* 609 */     this.sText = text;
/* 610 */     this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */     
/* 612 */     this.sKey = null;
/* 613 */     this.bIsTextDefault = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 619 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 623 */         SWTSkinObjectText2.this.lastStringPrinter = null;
/* 624 */         if ((SWTSkinObjectText2.this.canvas != null) && (!SWTSkinObjectText2.this.canvas.isDisposed())) {
/* 625 */           SWTSkinObjectText2.this.canvas.setCursor(null);
/* 626 */           SWTSkinObjectText2.this.canvas.redraw();
/* 627 */           if (SWTSkinObjectText2.this.relayoutOnTextChange) {
/* 628 */             Utils.relayout(SWTSkinObjectText2.this.canvas);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void paintControl(GC gc)
/*     */   {
/* 637 */     if ((this.sText == null) || (this.sText.length() == 0)) {
/* 638 */       return;
/*     */     }
/*     */     
/* 641 */     super.paintControl(gc);
/*     */     
/* 643 */     Composite composite = (Composite)this.control;
/* 644 */     Rectangle clientArea = composite.getClientArea();
/*     */     
/* 646 */     clientArea.x += this.hpadding;
/* 647 */     clientArea.width -= this.hpadding * 2;
/* 648 */     clientArea.y += this.vpadding;
/* 649 */     clientArea.height -= this.vpadding * 2;
/*     */     
/* 651 */     Font existingFont = (Font)this.canvas.getData("font");
/* 652 */     Color existingColor = (Color)this.canvas.getData("color");
/*     */     
/* 654 */     if (existingFont != null) {
/* 655 */       gc.setFont(existingFont);
/*     */     }
/*     */     
/* 658 */     if (this.debug) {
/* 659 */       System.out.println("paint " + existingColor + ";" + gc.getForeground());
/*     */     }
/* 661 */     if (existingColor != null) {
/* 662 */       gc.setForeground(existingColor);
/*     */     }
/*     */     
/* 665 */     if (this.antialiasMode != -1) {
/*     */       try {
/* 667 */         gc.setTextAntialias(this.antialiasMode);
/*     */       }
/*     */       catch (Exception ex) {}
/*     */     }
/*     */     
/*     */ 
/* 673 */     if (this.hasShadow) {
/* 674 */       Rectangle r = new Rectangle(clientArea.x + 1, clientArea.y + 1, clientArea.width, clientArea.height);
/*     */       
/*     */ 
/* 677 */       Color foreground = gc.getForeground();
/* 678 */       if (this.colorShadow.color == null) {
/* 679 */         Color color = ColorCache.getColor(gc.getDevice(), 0, 0, 0);
/* 680 */         gc.setForeground(color);
/* 681 */         gc.setAlpha(64);
/*     */       } else {
/* 683 */         gc.setForeground(this.colorShadow.color);
/* 684 */         gc.setAlpha(this.colorShadow.alpha);
/*     */       }
/* 686 */       GCStringPrinter.printString(gc, this.sDisplayText, r, true, false, this.style);
/* 687 */       gc.setForeground(foreground);
/*     */     }
/*     */     
/* 690 */     gc.setAlpha(this.alpha);
/*     */     
/* 692 */     if ((this.alpha == 255) && (this.hasShadow) && ((this.colorShadow.color == null) || (this.colorShadow.alpha < 255))) {
/* 693 */       gc.setAlpha(254);
/*     */     }
/* 695 */     this.lastStringPrinter = new GCStringPrinter(gc, this.sDisplayText, clientArea, true, false, this.style);
/*     */     
/* 697 */     if (this.colorUrl != null) {
/* 698 */       this.lastStringPrinter.setUrlColor(this.colorUrl);
/*     */     }
/* 700 */     if ((this.colorUrl2 != null) && (this.mouseDown)) {
/* 701 */       this.lastStringPrinter.calculateMetrics();
/* 702 */       Display display = Display.getCurrent();
/* 703 */       Point cursorLocation = this.canvas.toControl(display.getCursorLocation());
/* 704 */       GCStringPrinter.URLInfo hitUrl = this.lastStringPrinter.getHitUrl(cursorLocation.x, cursorLocation.y);
/* 705 */       if (hitUrl != null) {
/* 706 */         hitUrl.urlColor = this.colorUrl2;
/*     */       }
/*     */     }
/*     */     
/* 710 */     this.lastStringPrinter.printString();
/*     */   }
/*     */   
/*     */   public void setTextID(String key) {
/* 714 */     setTextID(key, false);
/*     */   }
/*     */   
/*     */   private void setTextID(String key, boolean forceRefresh) {
/* 718 */     if (key == null) {
/* 719 */       setText("");
/*     */ 
/*     */     }
/* 722 */     else if ((!forceRefresh) && (key.equals(this.sKey))) {
/* 723 */       return;
/*     */     }
/*     */     
/* 726 */     this.sText = MessageText.getString(key);
/* 727 */     this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */     
/* 729 */     this.sKey = key;
/* 730 */     this.bIsTextDefault = false;
/*     */     
/* 732 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 734 */         if ((SWTSkinObjectText2.this.canvas == null) || (SWTSkinObjectText2.this.canvas.isDisposed())) {
/* 735 */           return;
/*     */         }
/* 737 */         SWTSkinObjectText2.this.canvas.redraw();
/* 738 */         if (SWTSkinObjectText2.this.relayoutOnTextChange) {
/* 739 */           SWTSkinObjectText2.this.canvas.layout(true);
/* 740 */           Utils.relayout(SWTSkinObjectText2.this.canvas);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setTextID(String key, String[] params) {
/* 747 */     if (key == null) {
/* 748 */       setText("");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 758 */     this.sText = MessageText.getString(key, params);
/* 759 */     this.sDisplayText = ((this.isAllcaps) && (this.sText != null) ? this.sText.toUpperCase() : this.sText);
/*     */     
/* 761 */     this.sKey = key;
/* 762 */     this.bIsTextDefault = false;
/*     */     
/* 764 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 766 */         if ((SWTSkinObjectText2.this.canvas == null) || (SWTSkinObjectText2.this.canvas.isDisposed())) {
/* 767 */           return;
/*     */         }
/* 769 */         SWTSkinObjectText2.this.canvas.redraw();
/* 770 */         if (SWTSkinObjectText2.this.relayoutOnTextChange) {
/* 771 */           SWTSkinObjectText2.this.canvas.layout(true);
/* 772 */           Utils.relayout(SWTSkinObjectText2.this.canvas);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void triggerListeners(int eventType, Object params)
/*     */   {
/* 780 */     if ((eventType == 6) && 
/* 781 */       (this.sKey != null)) {
/* 782 */       setTextID(this.sKey, true);
/*     */     }
/*     */     
/* 785 */     super.triggerListeners(eventType, params);
/*     */   }
/*     */   
/*     */   public int getStyle() {
/* 789 */     return this.style;
/*     */   }
/*     */   
/*     */   public void setStyle(int style) {
/* 793 */     this.style = style;
/*     */   }
/*     */   
/*     */   public String getText()
/*     */   {
/* 798 */     return this.sDisplayText;
/*     */   }
/*     */   
/*     */   public void addUrlClickedListener(SWTSkinObjectText_UrlClickedListener l) {
/* 802 */     this.listUrlClickedListeners.add(l);
/*     */   }
/*     */   
/*     */   public void removeUrlClickedListener(SWTSkinObjectText_UrlClickedListener l) {
/* 806 */     this.listUrlClickedListeners.remove(l);
/*     */   }
/*     */   
/*     */   public void setTextColor(final Color color)
/*     */   {
/* 811 */     this.explicitColor = color;
/* 812 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 814 */         if ((SWTSkinObjectText2.this.canvas == null) || (SWTSkinObjectText2.this.canvas.isDisposed())) {
/* 815 */           return;
/*     */         }
/* 817 */         SWTSkinObjectText2.this.canvas.setData("color", color);
/* 818 */         SWTSkinObjectText2.this.canvas.redraw();
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectText2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */