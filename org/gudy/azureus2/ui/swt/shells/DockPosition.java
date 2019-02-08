/*    */ package org.gudy.azureus2.ui.swt.shells;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DockPosition
/*    */ {
/*    */   public static final int TOP_LEFT = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int BOTTOM_LEFT = 2;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int TOP_RIGHT = 3;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int BOTTOM_RIGHT = 4;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 36 */   private int position = 1;
/*    */   
/* 38 */   private Offset offset = new Offset(0, 0);
/*    */   
/*    */   public DockPosition() {
/* 41 */     this(1, null);
/*    */   }
/*    */   
/*    */   public DockPosition(int position, Offset offset) {
/* 45 */     if ((position == 1) || (position == 3) || (position == 2) || (position == 4))
/*    */     {
/* 47 */       this.position = position;
/*    */     } else {
/* 49 */       this.position = 1;
/*    */     }
/* 51 */     if (null != offset) {
/* 52 */       this.offset = offset;
/*    */     }
/*    */   }
/*    */   
/*    */   public int getPosition() {
/* 57 */     return this.position;
/*    */   }
/*    */   
/*    */   public void setPosition(int position) {
/* 61 */     this.position = position;
/*    */   }
/*    */   
/*    */   public Offset getOffset() {
/* 65 */     return this.offset;
/*    */   }
/*    */   
/*    */   public void setOffset(Offset offset) {
/* 69 */     if (null != offset) {
/* 70 */       this.offset = offset;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/DockPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */