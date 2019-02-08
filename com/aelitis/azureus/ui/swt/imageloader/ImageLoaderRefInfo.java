/*     */ package com.aelitis.azureus.ui.swt.imageloader;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Image;
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
/*     */ public class ImageLoaderRefInfo
/*     */ {
/*     */   private Image[] images;
/*     */   private long refcount;
/*     */   
/*     */   protected ImageLoaderRefInfo(Image[] images)
/*     */   {
/*  34 */     this.images = images;
/*  35 */     this.refcount = 1L;
/*     */   }
/*     */   
/*     */   protected ImageLoaderRefInfo(Image image) {
/*  39 */     this.images = new Image[] { image };
/*     */     
/*     */ 
/*  42 */     this.refcount = 1L;
/*     */   }
/*     */   
/*     */   protected void setNonDisposable() {
/*  46 */     this.refcount = -2L;
/*     */   }
/*     */   
/*     */   protected boolean isNonDisposable() {
/*  50 */     return this.refcount == -2L;
/*     */   }
/*     */   
/*     */   protected void addref() {
/*  54 */     synchronized (this) {
/*  55 */       if (this.refcount >= 0L) {
/*  56 */         this.refcount += 1L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void unref() {
/*  62 */     synchronized (this) {
/*  63 */       if (this.refcount >= 0L) {
/*  64 */         this.refcount -= 1L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean canDispose() {
/*  70 */     return (this.refcount == 0L) || (this.refcount == -1L);
/*     */   }
/*     */   
/*     */   protected long getRefCount() {
/*  74 */     return this.refcount;
/*     */   }
/*     */   
/*     */   protected Image[] getImages() {
/*  78 */     return this.images;
/*     */   }
/*     */   
/*     */   protected void setImages(Image[] images) {
/*  82 */     this.images = images;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/*  88 */     String img_str = "";
/*     */     
/*  90 */     for (Image i : this.images)
/*     */     {
/*     */       String s;
/*     */       String s;
/*  94 */       if (i == null)
/*     */       {
/*  96 */         s = "null";
/*     */       }
/*     */       else
/*     */       {
/* 100 */         s = i.toString() + ", disp=" + i.isDisposed();
/*     */       }
/*     */       
/* 103 */       img_str = img_str + (img_str.length() == 0 ? "" : ",") + s;
/*     */     }
/*     */     
/* 106 */     return "rc=" + this.refcount + ", images=[" + img_str + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/imageloader/ImageLoaderRefInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */