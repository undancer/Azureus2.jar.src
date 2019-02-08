/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
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
/*     */ public class SWTTextPaintListener
/*     */   implements PaintListener
/*     */ {
/*     */   private int align;
/*     */   private Color bgcolor;
/*     */   private Color fgcolor;
/*     */   private Font font;
/*     */   private String text;
/*     */   private SWTSkinProperties skinProperties;
/*     */   
/*     */   public SWTTextPaintListener(SWTSkin skin, Control createOn, String sConfigID)
/*     */   {
/*  54 */     this.skinProperties = skin.getSkinProperties();
/*     */     
/*  56 */     this.bgcolor = this.skinProperties.getColor(sConfigID + ".color");
/*  57 */     this.text = this.skinProperties.getStringValue(sConfigID + ".text");
/*  58 */     this.fgcolor = this.skinProperties.getColor(sConfigID + ".text.color");
/*  59 */     this.align = 0;
/*     */     
/*  61 */     String sAlign = this.skinProperties.getStringValue(sConfigID + ".align");
/*  62 */     if (sAlign != null) {
/*  63 */       this.align = SWTSkinUtils.getAlignment(sAlign, 0);
/*     */     }
/*     */     
/*  66 */     String sSize = this.skinProperties.getStringValue(sConfigID + ".text.size");
/*     */     
/*  68 */     if (sSize != null) {
/*  69 */       FontData[] fd = createOn.getFont().getFontData();
/*     */       try
/*     */       {
/*  72 */         char firstChar = sSize.charAt(0);
/*  73 */         if ((firstChar == '+') || (firstChar == '-')) {
/*  74 */           sSize = sSize.substring(1);
/*     */         }
/*     */         
/*  77 */         int iSize = Integer.parseInt(sSize);
/*     */         
/*  79 */         if (firstChar == '+') {
/*  80 */           fd[0].height += iSize;
/*  81 */         } else if (firstChar == '-') {
/*  82 */           fd[0].height -= iSize;
/*     */         } else {
/*  84 */           fd[0].height = iSize;
/*     */         }
/*     */         
/*  87 */         this.font = new Font(createOn.getDisplay(), fd);
/*  88 */         createOn.addDisposeListener(new DisposeListener() {
/*     */           public void widgetDisposed(DisposeEvent e) {
/*  90 */             SWTTextPaintListener.this.font.dispose();
/*     */           }
/*     */         });
/*     */       } catch (NumberFormatException e) {
/*  94 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void paintControl(PaintEvent e) {
/* 100 */     e.gc.setClipping(e.x, e.y, e.width, e.height);
/*     */     
/* 102 */     if (this.bgcolor != null) {
/* 103 */       e.gc.setBackground(this.bgcolor);
/*     */     }
/* 105 */     if (this.fgcolor != null) {
/* 106 */       e.gc.setForeground(this.fgcolor);
/*     */     }
/*     */     
/* 109 */     if (this.font != null) {
/* 110 */       e.gc.setFont(this.font);
/*     */     }
/*     */     
/* 113 */     if (this.text != null) {
/* 114 */       Rectangle clientArea = ((Composite)e.widget).getClientArea();
/* 115 */       GCStringPrinter.printString(e.gc, this.text, clientArea, true, true, this.align);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTTextPaintListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */