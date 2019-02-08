/*    */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UISWTGraphicImpl
/*    */   implements UISWTGraphic
/*    */ {
/*    */   Image img;
/*    */   
/*    */   public UISWTGraphicImpl(Image newImage)
/*    */   {
/* 40 */     this.img = newImage;
/*    */   }
/*    */   
/*    */   public Image getImage() {
/* 44 */     if ((this.img == null) || (this.img.isDisposed())) {
/* 45 */       return null;
/*    */     }
/* 47 */     return this.img;
/*    */   }
/*    */   
/*    */   public boolean setImage(Image newImage) {
/* 51 */     if (this.img == newImage)
/* 52 */       return false;
/* 53 */     this.img = newImage;
/* 54 */     return true;
/*    */   }
/*    */   
/*    */   public boolean equals(Object obj)
/*    */   {
/* 59 */     if (super.equals(obj)) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((obj instanceof UISWTGraphic)) {
/* 63 */       Image img2 = ((UISWTGraphic)obj).getImage();
/* 64 */       if (img2 == null) {
/* 65 */         return this.img == null;
/*    */       }
/* 67 */       return img2.equals(this.img);
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTGraphicImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */