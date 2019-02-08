/*    */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UploadSlot
/*    */ {
/*    */   protected static final int TYPE_NORMAL = 0;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected static final int TYPE_OPTIMISTIC = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int slot_type;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 35 */   private long expire_round = 0L;
/*    */   
/*    */   private UploadSession session;
/*    */   
/*    */   protected UploadSlot(int _slot_type)
/*    */   {
/* 41 */     this.slot_type = _slot_type;
/*    */   }
/*    */   
/* 44 */   protected int getSlotType() { return this.slot_type; }
/*    */   
/*    */ 
/* 47 */   protected void setSession(UploadSession _session) { this.session = _session; }
/* 48 */   protected UploadSession getSession() { return this.session; }
/*    */   
/* 50 */   protected void setExpireRound(long round) { this.expire_round = round; }
/* 51 */   protected long getExpireRound() { return this.expire_round; }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/UploadSlot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */