/*    */ package org.gudy.azureus2.ui.common.util;
/*    */ 
/*    */ import java.util.Hashtable;
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
/*    */ 
/*    */ 
/*    */ public class LegacyHashtable
/*    */   extends Hashtable
/*    */ {
/*    */   public Object get(Object key)
/*    */   {
/* 39 */     if (containsKey(key)) {
/* 40 */       return super.get(key);
/*    */     }
/* 42 */     return "";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/util/LegacyHashtable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */