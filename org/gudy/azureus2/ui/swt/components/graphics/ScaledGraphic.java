/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class ScaledGraphic
/*     */   extends BackGroundGraphic
/*     */ {
/*     */   protected Scale scale;
/*     */   protected ValueFormater formater;
/*     */   protected Image bufferScale;
/*     */   private int lastMax;
/*  43 */   private int update_divider_width = 0;
/*     */   
/*     */   public ScaledGraphic(Scale scale, ValueFormater formater) {
/*  46 */     this.scale = scale;
/*  47 */     this.formater = formater;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  52 */     setSIIECSensitive(scale.isSIIECSensitive());
/*     */   }
/*     */   
/*     */   public void setUpdateDividerWidth(int width) {
/*  56 */     this.update_divider_width = width;
/*     */   }
/*     */   
/*     */   protected void drawScale(boolean sizeChanged) {
/*  60 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed()) || (!this.drawCanvas.isVisible())) {
/*  61 */       return;
/*     */     }
/*  63 */     drawBackGround(sizeChanged);
/*  64 */     if ((this.bufferBackground == null) || (this.bufferBackground.isDisposed())) {
/*  65 */       return;
/*     */     }
/*     */     
/*  68 */     boolean scaleChanged = this.lastMax != this.scale.getMax();
/*     */     
/*  70 */     if ((sizeChanged) || (scaleChanged) || (this.bufferScale == null)) {
/*  71 */       Rectangle bounds = this.drawCanvas.getClientArea();
/*  72 */       if ((bounds.height < 30) || (bounds.width < 100)) {
/*  73 */         return;
/*     */       }
/*  75 */       if ((this.bufferScale != null) && (!this.bufferScale.isDisposed())) {
/*  76 */         this.bufferScale.dispose();
/*     */       }
/*  78 */       this.bufferScale = new Image(this.drawCanvas.getDisplay(), bounds);
/*     */       
/*  80 */       GC gcBuffer = new GC(this.bufferScale);
/*     */       try {
/*  82 */         gcBuffer.drawImage(this.bufferBackground, 0, 0);
/*  83 */         gcBuffer.setForeground(Colors.black);
/*     */         
/*  85 */         this.scale.setNbPixels(bounds.height - 16);
/*  86 */         int[] levels = this.scale.getScaleValues();
/*  87 */         for (int i = 0; i < levels.length; i++) {
/*  88 */           int height = bounds.height - this.scale.getScaledValue(levels[i]) - 2;
/*  89 */           gcBuffer.drawLine(1, height, bounds.width - 70, height);
/*  90 */           gcBuffer.drawText(this.formater.format(levels[i]), bounds.width - 65, height - 12, true);
/*     */         }
/*  92 */         if (this.update_divider_width > 0) {
/*  93 */           for (int i = bounds.width - 70; i > 0; i -= this.update_divider_width) {
/*  94 */             gcBuffer.setForeground(Colors.grey);
/*  95 */             gcBuffer.drawLine(i, 0, i, bounds.height);
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/*  99 */         Debug.out(e);
/*     */       } finally {
/* 101 */         gcBuffer.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 107 */     super.dispose();
/* 108 */     if ((this.bufferScale != null) && (!this.bufferScale.isDisposed())) {
/* 109 */       this.bufferScale.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/ScaledGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */