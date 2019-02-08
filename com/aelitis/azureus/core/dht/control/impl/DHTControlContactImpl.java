/*    */ package com.aelitis.azureus.core.dht.control.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.control.DHTControlContact;
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouterContact;
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouterContactAttachment;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
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
/*    */ public class DHTControlContactImpl
/*    */   implements DHTControlContact, DHTRouterContactAttachment
/*    */ {
/*    */   private final DHTTransportContact t_contact;
/*    */   private DHTRouterContact r_contact;
/*    */   
/*    */   protected DHTControlContactImpl(DHTTransportContact _t_contact)
/*    */   {
/* 43 */     this.t_contact = _t_contact;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTControlContactImpl(DHTTransportContact _t_contact, DHTRouterContact _r_contact)
/*    */   {
/* 51 */     this.t_contact = _t_contact;
/* 52 */     this.r_contact = _r_contact;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setRouterContact(DHTRouterContact _r_contact)
/*    */   {
/* 59 */     this.r_contact = _r_contact;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getMaxFailForLiveCount()
/*    */   {
/* 65 */     return this.t_contact.getMaxFailForLiveCount();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getMaxFailForUnknownCount()
/*    */   {
/* 71 */     return this.t_contact.getMaxFailForUnknownCount();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getInstanceID()
/*    */   {
/* 77 */     return this.t_contact.getInstanceID();
/*    */   }
/*    */   
/*    */ 
/*    */   public DHTTransportContact getTransportContact()
/*    */   {
/* 83 */     return this.t_contact;
/*    */   }
/*    */   
/*    */ 
/*    */   public DHTRouterContact getRouterContact()
/*    */   {
/* 89 */     return this.r_contact;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSleeping()
/*    */   {
/* 95 */     return this.t_contact.isSleeping();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/impl/DHTControlContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */