/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.util.AbstractMap;
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
/*    */ public class LightHashMapEx<S, T>
/*    */   extends LightHashMap<S, T>
/*    */   implements Cloneable
/*    */ {
/*    */   public static final byte FL_MAP_ORDER_INCORRECT = 1;
/*    */   private byte flags;
/*    */   
/*    */   public LightHashMapEx(AbstractMap<S, T> m)
/*    */   {
/* 39 */     super(m);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setFlag(byte flag, boolean set)
/*    */   {
/* 47 */     if (set)
/*    */     {
/* 49 */       this.flags = ((byte)(this.flags | flag));
/*    */     }
/*    */     else
/*    */     {
/* 53 */       this.flags = ((byte)(this.flags & (flag ^ 0xFFFFFFFF)));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean getFlag(byte flag)
/*    */   {
/* 61 */     return (this.flags & flag) != 0;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/LightHashMapEx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */