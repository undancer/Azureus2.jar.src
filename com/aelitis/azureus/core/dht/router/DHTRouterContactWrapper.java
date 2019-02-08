/*    */ package com.aelitis.azureus.core.dht.router;
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
/*    */ public class DHTRouterContactWrapper
/*    */   implements DHTRouterContact
/*    */ {
/*    */   private final DHTRouterContact delegate;
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
/*    */   public DHTRouterContactWrapper(DHTRouterContact _contact)
/*    */   {
/* 35 */     this.delegate = _contact;
/*    */   }
/*    */   
/*    */ 
/*    */   protected DHTRouterContact getDelegate()
/*    */   {
/* 41 */     return this.delegate;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getID()
/*    */   {
/* 47 */     return this.delegate.getID();
/*    */   }
/*    */   
/*    */ 
/*    */   public DHTRouterContactAttachment getAttachment()
/*    */   {
/* 53 */     return this.delegate.getAttachment();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean hasBeenAlive()
/*    */   {
/* 59 */     return this.delegate.hasBeenAlive();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isFailing()
/*    */   {
/* 65 */     return this.delegate.isFailing();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isAlive()
/*    */   {
/* 71 */     return this.delegate.isAlive();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getTimeAlive()
/*    */   {
/* 77 */     return this.delegate.getTimeAlive();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 83 */     return this.delegate.getString();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isBucketEntry()
/*    */   {
/* 89 */     return this.delegate.isBucketEntry();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isReplacement()
/*    */   {
/* 95 */     return this.delegate.isReplacement();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterContactWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */