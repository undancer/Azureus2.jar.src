/*    */ package com.aelitis.azureus.core.tag;
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
/*    */ public class TagTypeAdapter
/*    */   implements TagTypeListener
/*    */ {
/*    */   public void tagTypeChanged(TagType tag_type) {}
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
/*    */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*    */   {
/* 35 */     int type = event.getEventType();
/* 36 */     Tag tag = event.getTag();
/* 37 */     if (type == 0) {
/* 38 */       tagAdded(tag);
/* 39 */     } else if (type == 1) {
/* 40 */       tagChanged(tag);
/* 41 */     } else if (type == 2) {
/* 42 */       tagRemoved(tag);
/*    */     }
/*    */   }
/*    */   
/*    */   public void tagAdded(Tag tag) {}
/*    */   
/*    */   public void tagChanged(Tag tag) {}
/*    */   
/*    */   public void tagRemoved(Tag tag) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagTypeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */