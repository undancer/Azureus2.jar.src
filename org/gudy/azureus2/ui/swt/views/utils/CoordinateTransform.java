/*    */ package org.gudy.azureus2.ui.swt.views.utils;
/*    */ 
/*    */ import org.eclipse.swt.graphics.Rectangle;
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
/*    */ public class CoordinateTransform
/*    */ {
/*    */   final int extWidth;
/*    */   final int extHeight;
/*    */   
/*    */   public CoordinateTransform(Rectangle exteriorBounds)
/*    */   {
/* 27 */     this.extWidth = exteriorBounds.width;
/* 28 */     this.extHeight = exteriorBounds.height;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 34 */   int offsetX = 0;
/* 35 */   int offsetY = 0;
/* 36 */   double scaleX = 1.0D;
/* 37 */   double scaleY = 1.0D;
/*    */   
/*    */   public int x(int x)
/*    */   {
/* 41 */     return (int)Math.round(this.offsetX + x * this.scaleX);
/*    */   }
/*    */   
/*    */   public int y(int y)
/*    */   {
/* 46 */     return (int)Math.round(this.offsetY + y * this.scaleY);
/*    */   }
/*    */   
/*    */   private int w(int w)
/*    */   {
/* 51 */     return (int)Math.round(w * this.scaleX);
/*    */   }
/*    */   
/*    */   private int h(int h)
/*    */   {
/* 56 */     return (int)Math.ceil(h * this.scaleY);
/*    */   }
/*    */   
/*    */   public void scale(double x, double y)
/*    */   {
/* 61 */     this.scaleX *= x;
/* 62 */     this.scaleY *= y;
/*    */   }
/*    */   
/*    */   public void shiftExternal(int x, int y)
/*    */   {
/* 67 */     this.offsetX += x;
/* 68 */     this.offsetY += y;
/*    */   }
/*    */   
/*    */ 
/*    */   public void shiftInternal(int x, int y)
/*    */   {
/* 74 */     this.offsetX = ((int)(this.offsetX + x * this.scaleX));
/* 75 */     this.offsetY = ((int)(this.offsetY + y * this.scaleY));
/*    */   }
/*    */   
/*    */   private void calcFromDimensions(int internalWidth, int internalHeight, int marginLeft, int marginRight, int marginTop, int marginBottom, boolean leftToRight, boolean topDown)
/*    */   {
/* 80 */     shiftExternal(leftToRight ? 0 : this.extWidth, topDown ? 0 : this.extHeight);
/* 81 */     scale(leftToRight ? 1.0D : -1.0D, topDown ? 1.0D : -1.0D);
/* 82 */     shiftInternal(leftToRight ? marginLeft : marginRight, topDown ? marginTop : marginBottom);
/* 83 */     scale(Math.round((this.extWidth - marginLeft - marginRight) / (1.0D * internalWidth)), Math.round((this.extHeight - marginTop - marginBottom) / (1.0D * internalHeight)));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/CoordinateTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */