/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.text.NumberFormat;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Sash;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableObject;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SWTSkinObjectSash
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private static final boolean FASTDRAG = true;
/*     */   protected String sControlBefore;
/*     */   protected String sControlAfter;
/*     */   private Composite createOn;
/*     */   private final boolean isVertical;
/*     */   private Sash sash;
/*     */   private Composite parentComposite;
/*  79 */   private Composite above = null;
/*     */   
/*  81 */   private int aboveMin = 0;
/*     */   
/*  83 */   private Composite below = null;
/*     */   
/*  85 */   private int belowMin = 0;
/*     */   
/*     */ 
/*     */   private String sBorder;
/*     */   
/*     */   private SWTSkinObject soAbove;
/*     */   
/*     */   private SWTSkinObject soBelow;
/*     */   
/*     */ 
/*     */   public SWTSkinObjectSash(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parent, final boolean bVertical)
/*     */   {
/*  97 */     super(skin, properties, sID, sConfigID, "sash", parent);
/*  98 */     this.isVertical = bVertical;
/*     */     
/* 100 */     int style = bVertical ? 512 : 256;
/*     */     
/* 102 */     if (typeParams.length > 2) {
/* 103 */       this.sControlBefore = typeParams[1];
/* 104 */       this.sControlAfter = typeParams[2];
/*     */     }
/*     */     
/* 107 */     if (parent == null) {
/* 108 */       this.createOn = skin.getShell();
/*     */     } else {
/* 110 */       this.createOn = ((Composite)parent.getControl());
/*     */     }
/*     */     
/* 113 */     if ((this.createOn == null) || (this.createOn.isDisposed())) {
/* 114 */       Debug.out("Can not create " + sID + " because parent is null or disposed");
/* 115 */       return;
/*     */     }
/*     */     
/* 118 */     this.sash = new Sash(this.createOn, style);
/*     */     
/* 120 */     int splitAtPX = COConfigurationManager.getIntParameter("v3." + sID + ".splitAtPX", -1);
/*     */     
/* 122 */     if (splitAtPX >= 0) {
/* 123 */       this.sash.setData("PX", new Long(splitAtPX));
/*     */     } else {
/* 125 */       String sPos = properties.getStringValue(sConfigID + ".startpos");
/* 126 */       if (sPos != null) {
/*     */         try {
/* 128 */           int l = NumberFormat.getInstance().parse(sPos).intValue();
/* 129 */           this.sash.setData("PX", new Long(Utils.adjustPXForDPI(l)));
/*     */         } catch (Exception e) {
/* 131 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 136 */     this.parentComposite = this.createOn;
/*     */     
/* 138 */     SWTSkinObject soInitializeSashAfterCreated = parent == null ? this : parent;
/* 139 */     soInitializeSashAfterCreated.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/* 142 */         if (eventType == 4) {
/* 143 */           SWTSkinObjectSash.this.initialize();
/*     */         }
/* 145 */         return null;
/*     */       }
/*     */       
/* 148 */     });
/* 149 */     this.sBorder = properties.getStringValue(sConfigID + ".border", (String)null);
/* 150 */     if (this.sBorder != null) {
/* 151 */       this.sash.addPaintListener(new PaintListener() {
/*     */         public void paintControl(PaintEvent e) {
/* 153 */           e.gc.setForeground(e.gc.getDevice().getSystemColor(18));
/*     */           
/* 155 */           Point size = SWTSkinObjectSash.this.sash.getSize();
/* 156 */           if (bVertical) {
/* 157 */             e.gc.drawLine(0, 0, 0, size.y);
/* 158 */             if (!SWTSkinObjectSash.this.sBorder.startsWith("thin-top")) {
/* 159 */               int x = size.x - 1;
/* 160 */               e.gc.drawLine(x, 0, x, 0 + size.y);
/*     */             }
/*     */           } else {
/* 163 */             e.gc.drawLine(0, 0, 0 + size.x, 0);
/* 164 */             if (!SWTSkinObjectSash.this.sBorder.startsWith("thin-top")) {
/* 165 */               int y = size.y - 1;
/* 166 */               e.gc.drawLine(0, y, 0 + size.x, y);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 173 */     setControl(this.sash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initialize()
/*     */   {
/* 184 */     SWTSkinObject skinObject = this.skin.getSkinObjectByID(this.sControlBefore);
/*     */     
/* 186 */     if (skinObject != null) {
/* 187 */       this.soAbove = skinObject;
/* 188 */       this.above = ((Composite)skinObject.getControl());
/* 189 */       this.aboveMin = skinObject.getProperties().getIntValue(getConfigID() + ".above" + (this.isVertical ? ".minwidth" : ".minheight"), 0);
/*     */       
/*     */ 
/* 192 */       this.aboveMin = Utils.adjustPXForDPI(this.aboveMin);
/* 193 */       boolean aboveVisible = COConfigurationManager.getBooleanParameter("v3." + this.sID + ".aboveVisible", true);
/*     */       
/* 195 */       this.soAbove.setVisible(aboveVisible);
/*     */     }
/*     */     
/* 198 */     skinObject = this.skin.getSkinObjectByID(this.sControlAfter);
/*     */     
/* 200 */     if (skinObject != null) {
/* 201 */       this.soBelow = skinObject;
/* 202 */       this.below = ((Composite)skinObject.getControl());
/*     */     }
/* 204 */     if (this.below == null) {
/* 205 */       return;
/*     */     }
/*     */     
/* 208 */     this.belowMin = (skinObject == null ? 0 : skinObject.getProperties().getIntValue(getConfigID() + ".below" + (this.isVertical ? ".minwidth" : ".minheight"), 0));
/*     */     
/* 210 */     this.belowMin = Utils.adjustPXForDPI(this.belowMin);
/*     */     
/* 212 */     Listener l = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 214 */         if (e.type == 4) {
/* 215 */           if ((e.button == 3) || ((e.button == 1) && ((e.stateMask & SWT.MOD1) > 0))) {
/* 216 */             String sPos = SWTSkinObjectSash.this.properties.getStringValue(SWTSkinObjectSash.this.sConfigID + ".startpos");
/* 217 */             if (sPos == null) {
/* 218 */               return;
/*     */             }
/*     */             try {
/* 221 */               int l = NumberFormat.getInstance().parse(sPos).intValue();
/* 222 */               SWTSkinObjectSash.this.sash.setData("PX", new Long(Utils.adjustPXForDPI(l)));
/*     */               
/* 224 */               e.type = 22;
/*     */             } catch (Exception ex) {
/* 226 */               Debug.out(ex);
/* 227 */               return;
/*     */             }
/*     */           } else {
/* 230 */             return;
/*     */           }
/*     */         }
/*     */         
/* 234 */         if (e.type == 22)
/*     */         {
/* 236 */           Utils.execSWTThreadLater(0, new AERunnable() {
/*     */             public void runSupport() {
/* 238 */               SWTSkinObjectSash.this.handleShow();
/*     */             }
/*     */           });
/* 241 */         } else if (e.type == 13) {
/* 242 */           if (e.detail == 1) {
/* 243 */             return;
/*     */           }
/*     */           
/* 246 */           Rectangle area = SWTSkinObjectSash.this.parentComposite.getBounds();
/* 247 */           FormData aboveData = (FormData)SWTSkinObjectSash.this.above.getLayoutData();
/*     */           
/* 249 */           if (SWTSkinObjectSash.this.isVertical)
/*     */           {
/*     */ 
/* 252 */             aboveData.width = (e.x - SWTSkinObjectSash.this.above.getBorderWidth());
/* 253 */             if (aboveData.width < SWTSkinObjectSash.this.aboveMin) {
/* 254 */               aboveData.width = SWTSkinObjectSash.this.aboveMin;
/* 255 */               e.x = SWTSkinObjectSash.this.aboveMin;
/*     */             } else {
/* 257 */               int excess = area.width - SWTSkinObjectSash.this.above.getBorderWidth() * 2 - SWTSkinObjectSash.this.sash.getSize().x;
/*     */               
/* 259 */               if (excess - aboveData.width < SWTSkinObjectSash.this.belowMin) {
/* 260 */                 aboveData.width = (excess - SWTSkinObjectSash.this.belowMin);
/* 261 */                 e.doit = false;
/*     */               }
/*     */             }
/*     */           } else {
/* 265 */             aboveData.height = (e.y - SWTSkinObjectSash.this.above.getBorderWidth());
/* 266 */             if (aboveData.height < SWTSkinObjectSash.this.aboveMin) {
/* 267 */               aboveData.height = SWTSkinObjectSash.this.aboveMin;
/* 268 */               e.y = SWTSkinObjectSash.this.aboveMin;
/*     */             } else {
/* 270 */               int excess = area.height - SWTSkinObjectSash.this.above.getBorderWidth() * 2 - SWTSkinObjectSash.this.sash.getSize().y;
/*     */               
/* 272 */               if (excess - aboveData.height < SWTSkinObjectSash.this.belowMin) {
/* 273 */                 aboveData.height = (excess - SWTSkinObjectSash.this.belowMin);
/* 274 */                 e.doit = false;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 279 */           SWTSkinObjectSash.this.parentComposite.layout(true);
/*     */           double aboveNewSize;
/*     */           double aboveNewSize;
/* 282 */           if (SWTSkinObjectSash.this.isVertical) {
/* 283 */             aboveNewSize = SWTSkinObjectSash.this.above.getBounds().width + SWTSkinObjectSash.this.sash.getSize().x / 2.0D;
/*     */           } else {
/* 285 */             aboveNewSize = SWTSkinObjectSash.this.above.getBounds().height + SWTSkinObjectSash.this.sash.getSize().y / 2.0D;
/*     */           }
/* 287 */           SWTSkinObjectSash.this.sash.setData("PX", new Long(aboveNewSize));
/*     */         }
/*     */         
/*     */       }
/* 291 */     };
/* 292 */     this.sash.addListener(13, l);
/* 293 */     this.sash.addListener(4, l);
/* 294 */     this.sash.getShell().addListener(22, l);
/*     */     
/* 296 */     handleShow();
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 300 */     Long px = (Long)this.sash.getData("PX");
/* 301 */     if ((px != null) && (px.longValue() != 0L)) {
/* 302 */       COConfigurationManager.setParameter("v3." + this.sID + ".splitAtPX", px.longValue());
/*     */     }
/*     */     
/* 305 */     super.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleShow()
/*     */   {
/* 314 */     if (this.sash.isDisposed()) {
/* 315 */       return;
/*     */     }
/*     */     
/* 318 */     Long px = (Long)this.sash.getData("PX");
/* 319 */     if (px == null) {
/*     */       return;
/*     */     }
/*     */     int newAboveSize;
/* 323 */     if (this.soAbove.isVisible()) {
/* 324 */       int newAboveSize = px.intValue();
/* 325 */       if (newAboveSize < this.aboveMin) {
/* 326 */         newAboveSize = this.aboveMin;
/*     */       }
/*     */     } else {
/* 329 */       newAboveSize = 0;
/*     */     }
/*     */     
/* 332 */     FormData aboveData = (FormData)this.above.getLayoutData();
/* 333 */     if (aboveData == null) {
/* 334 */       aboveData = Utils.getFilledFormData();
/* 335 */       this.above.setLayoutData(aboveData);
/*     */     }
/* 337 */     if (this.isVertical) {
/* 338 */       aboveData.width = newAboveSize;
/*     */     } else {
/* 340 */       aboveData.height = newAboveSize;
/*     */     }
/*     */     
/* 343 */     this.parentComposite.layout(true);
/*     */   }
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
/*     */   protected void setPercent(double pctAbove, Control sash, Composite above, Composite below, boolean bVertical, Control parentComposite, int minAbove, int belowMin)
/*     */   {
/* 357 */     FormData aboveData = (FormData)above.getLayoutData();
/* 358 */     if (aboveData == null) {
/* 359 */       return;
/*     */     }
/* 361 */     boolean layoutNeeded = false;
/* 362 */     if (bVertical) {
/* 363 */       int parentWidth = parentComposite.getBounds().width - parentComposite.getBorderWidth() * 2 - sash.getSize().x;
/*     */       
/* 365 */       int newWidth = (int)(parentWidth * pctAbove);
/* 366 */       if (newWidth != aboveData.width) {
/* 367 */         aboveData.width = newWidth;
/* 368 */         layoutNeeded = true;
/*     */       }
/*     */       
/* 371 */       if ((pctAbove != 0.0D) && (parentWidth - aboveData.width - sash.getSize().x < minAbove))
/*     */       {
/* 373 */         aboveData.width = (parentWidth - minAbove - sash.getSize().x);
/* 374 */         layoutNeeded = true;
/*     */ 
/*     */       }
/* 377 */       else if (aboveData.width < belowMin) {
/* 378 */         layoutNeeded = true;
/* 379 */         aboveData.width = belowMin;
/*     */       }
/*     */       
/* 382 */       sash.setData("PX", new Long(aboveData.width));
/*     */     } else {
/* 384 */       int parentHeight = parentComposite.getBounds().height - parentComposite.getBorderWidth() * 2 - sash.getSize().y;
/*     */       
/* 386 */       int newHeight = (int)(parentHeight * pctAbove);
/* 387 */       if (aboveData.height != newHeight) {
/* 388 */         aboveData.height = newHeight;
/* 389 */         layoutNeeded = true;
/*     */       }
/*     */       
/* 392 */       if ((pctAbove != 0.0D) && (parentHeight - aboveData.height < minAbove) && (parentHeight >= minAbove))
/*     */       {
/* 394 */         aboveData.height = (parentHeight - minAbove);
/* 395 */         layoutNeeded = true;
/* 396 */       } else if (aboveData.height < belowMin) {
/* 397 */         layoutNeeded = true;
/* 398 */         aboveData.height = belowMin;
/*     */       }
/* 400 */       sash.setData("PX", new Long(aboveData.height));
/*     */     }
/* 402 */     if (layoutNeeded) {
/* 403 */       above.getParent().layout();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBelowSize(final int px) {
/* 408 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 410 */         int sashHeight = SWTSkinObjectSash.this.isVertical ? SWTSkinObjectSash.this.sash.getSize().x : SWTSkinObjectSash.this.sash.getSize().y;
/* 411 */         int parentHeight = SWTSkinObjectSash.this.parentComposite.getBounds().height - SWTSkinObjectSash.this.parentComposite.getBorderWidth() * 2;
/*     */         
/*     */ 
/* 414 */         int wantAboveSize = parentHeight - sashHeight - px;
/* 415 */         SWTSkinObjectSash.this.sash.setData("PX", new Long(wantAboveSize));
/* 416 */         SWTSkinObjectSash.this.handleShow();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public int getAboveSize() {
/* 422 */     Object o = Utils.execSWTThreadWithObject("getAboveSize", new AERunnableObject() {
/*     */       public Object runSupport() {
/* 424 */         if ((SWTSkinObjectSash.this.sash == null) || (SWTSkinObjectSash.this.sash.isDisposed())) {
/* 425 */           return Integer.valueOf(-1);
/*     */         }
/* 427 */         return SWTSkinObjectSash.this.sash.getData("PX"); } }, 1000L);
/*     */     
/*     */ 
/* 430 */     if ((o instanceof Number)) {
/* 431 */       return ((Number)o).intValue();
/*     */     }
/* 433 */     return -1;
/*     */   }
/*     */   
/*     */   public void setAboveSize(final int px) {
/* 437 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 439 */         SWTSkinObjectSash.this.sash.setData("PX", new Long(px));
/* 440 */         SWTSkinObjectSash.this.handleShow();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void resetWidth() {
/* 446 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 448 */         String sPos = SWTSkinObjectSash.this.properties.getStringValue(SWTSkinObjectSash.this.sConfigID + ".startpos");
/* 449 */         COConfigurationManager.removeParameter("v3." + SWTSkinObjectSash.this.sID + ".splitAt");
/* 450 */         COConfigurationManager.removeParameter("v3." + SWTSkinObjectSash.this.sID + ".splitAtPX");
/* 451 */         if (sPos != null) {
/* 452 */           SWTSkinObjectSash.this.sash.setData("PX", null);
/*     */           try {
/* 454 */             int l = NumberFormat.getInstance().parse(sPos).intValue();
/* 455 */             SWTSkinObjectSash.this.setAboveSize(Utils.adjustPXForDPI(l));
/*     */           } catch (Exception e) {
/* 457 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean isAboveVisible() {
/* 465 */     if ((this.soAbove == null) || (this.soAbove.isDisposed())) {
/* 466 */       return false;
/*     */     }
/* 468 */     return this.soAbove.isVisible();
/*     */   }
/*     */   
/*     */   public void setAboveVisible(boolean visible) {
/* 472 */     if (this.soAbove == null) {
/* 473 */       return;
/*     */     }
/* 475 */     COConfigurationManager.setParameter("v3." + this.sID + ".aboveVisible", visible);
/* 476 */     this.soAbove.setVisible(visible);
/* 477 */     handleShow();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectSash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */