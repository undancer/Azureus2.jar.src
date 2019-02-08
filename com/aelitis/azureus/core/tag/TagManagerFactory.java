/*    */ package com.aelitis.azureus.core.tag;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.impl.TagManagerImpl;
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
/*    */ public class TagManagerFactory
/*    */ {
/*    */   public static TagManager getTagManager()
/*    */   {
/* 31 */     return TagManagerImpl.getSingleton();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */