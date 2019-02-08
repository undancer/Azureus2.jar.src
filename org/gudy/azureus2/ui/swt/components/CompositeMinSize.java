/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class CompositeMinSize
/*     */   extends Composite
/*     */ {
/*  32 */   int minWidth = -1;
/*  33 */   int minHeight = -1;
/*     */   
/*     */   public CompositeMinSize(Composite parent, int style) {
/*  36 */     super(parent, style);
/*     */   }
/*     */   
/*     */   public void setMinSize(Point pt) {
/*  40 */     this.minWidth = pt.x;
/*  41 */     this.minHeight = pt.y;
/*     */   }
/*     */   
/*     */   public Point computeSize(int wHint, int hHint, boolean changed) {
/*     */     try {
/*  46 */       Point size = super.computeSize(wHint, hHint, changed);
/*  47 */       return betterComputeSize(this, size, wHint, hHint, changed);
/*     */     } catch (Throwable t) {
/*  49 */       Debug.out(t);
/*  50 */       return new Point(wHint == -1 ? 10 : wHint, hHint == -1 ? 10 : hHint);
/*     */     }
/*     */   }
/*     */   
/*     */   public Point computeSize(int wHint, int hHint)
/*     */   {
/*     */     try {
/*  57 */       Point size = super.computeSize(wHint, hHint);
/*  58 */       return betterComputeSize(this, size, wHint, hHint);
/*     */     } catch (Throwable t) {
/*  60 */       Debug.out(t);
/*  61 */       return new Point(wHint == -1 ? 10 : wHint, hHint == -1 ? 10 : hHint);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected Point betterComputeSize(Composite c, Point size, int wHint, int hHint)
/*     */   {
/*  68 */     if ((c.getChildren().length == 0) && ((size.x == 64) || (size.y == 64))) {
/*  69 */       Object ld = c.getLayoutData();
/*  70 */       if ((ld instanceof FormData)) {
/*  71 */         FormData fd = (FormData)ld;
/*  72 */         if ((fd.width != 0) && (fd.height != 0)) {
/*  73 */           Rectangle trim = c.computeTrim(0, 0, fd.width, fd.height);
/*  74 */           return new Point(trim.width, trim.height);
/*     */         }
/*     */       }
/*  77 */       return new Point(1, 1);
/*     */     }
/*  79 */     if ((size.x == 0) || (size.y == 0)) {
/*  80 */       return size;
/*     */     }
/*  82 */     if ((this.minWidth > 0) && (size.x < this.minWidth)) {
/*  83 */       size.x = this.minWidth;
/*     */     }
/*  85 */     if ((this.minHeight > 0) && (size.y < this.minHeight)) {
/*  86 */       size.y = this.minHeight;
/*     */     }
/*  88 */     return size;
/*     */   }
/*     */   
/*     */   protected Point betterComputeSize(Composite c, Point size, int wHint, int hHint, boolean changed) {
/*  92 */     if ((c.getChildren().length == 0) && ((size.x == 64) || (size.y == 64))) {
/*  93 */       Object ld = c.getLayoutData();
/*  94 */       if ((ld instanceof FormData)) {
/*  95 */         FormData fd = (FormData)ld;
/*  96 */         if ((fd.width != 0) && (fd.height != 0)) {
/*  97 */           Rectangle trim = c.computeTrim(0, 0, fd.width, fd.height);
/*  98 */           return new Point(trim.width, trim.height);
/*     */         }
/*     */       }
/* 101 */       return new Point(1, 1);
/*     */     }
/* 103 */     if ((size.x == 0) || (size.y == 0)) {
/* 104 */       return size;
/*     */     }
/* 106 */     if ((this.minWidth > 0) && (size.x < this.minWidth)) {
/* 107 */       size.x = this.minWidth;
/*     */     }
/* 109 */     if ((this.minHeight > 0) && (size.y < this.minHeight)) {
/* 110 */       size.y = this.minHeight;
/*     */     }
/* 112 */     return size;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/CompositeMinSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */