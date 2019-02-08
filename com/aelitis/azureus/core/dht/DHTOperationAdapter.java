/*     */ package com.aelitis.azureus.core.dht;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTOperationAdapter
/*     */   implements DHTOperationListener
/*     */ {
/*     */   private final DHTOperationListener delegate;
/*     */   
/*     */   public DHTOperationAdapter()
/*     */   {
/*  39 */     this.delegate = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTOperationAdapter(DHTOperationListener _delegate)
/*     */   {
/*  46 */     this.delegate = _delegate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void searching(DHTTransportContact contact, int level, int active_searches)
/*     */   {
/*  55 */     if (this.delegate != null) {
/*  56 */       this.delegate.searching(contact, level, active_searches);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean diversified(String desc)
/*     */   {
/*  64 */     if (this.delegate != null) {
/*  65 */       return this.delegate.diversified(desc);
/*     */     }
/*  67 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void found(DHTTransportContact contact, boolean is_closest)
/*     */   {
/*  75 */     if (this.delegate != null) {
/*  76 */       this.delegate.found(contact, is_closest);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(DHTTransportContact contact, DHTTransportValue value)
/*     */   {
/*  85 */     if (this.delegate != null) {
/*  86 */       this.delegate.read(contact, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void wrote(DHTTransportContact contact, DHTTransportValue value)
/*     */   {
/*  95 */     if (this.delegate != null) {
/*  96 */       this.delegate.wrote(contact, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void complete(boolean timeout)
/*     */   {
/* 104 */     if (this.delegate != null) {
/* 105 */       this.delegate.complete(timeout);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTOperationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */