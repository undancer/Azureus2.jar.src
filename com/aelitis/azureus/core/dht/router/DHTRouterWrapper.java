/*     */ package com.aelitis.azureus.core.dht.router;
/*     */ 
/*     */ import java.util.List;
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
/*     */ public class DHTRouterWrapper
/*     */   implements DHTRouter
/*     */ {
/*     */   private final DHTRouter delegate;
/*     */   
/*     */   public DHTRouterWrapper(DHTRouter _delegate)
/*     */   {
/*  37 */     this.delegate = _delegate;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTRouter getDelegate()
/*     */   {
/*  43 */     return this.delegate;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getK()
/*     */   {
/*  49 */     return this.delegate.getK();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/*  55 */     return this.delegate.getID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isID(byte[] node_id)
/*     */   {
/*  62 */     return this.delegate.isID(node_id);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTRouterContact getLocalContact()
/*     */   {
/*  68 */     return this.delegate.getLocalContact();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAdapter(DHTRouterAdapter _adapter)
/*     */   {
/*  75 */     this.delegate.setAdapter(_adapter);
/*     */   }
/*     */   
/*     */ 
/*     */   public void seed()
/*     */   {
/*  81 */     this.delegate.seed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void contactKnown(byte[] node_id, DHTRouterContactAttachment attachment, boolean force)
/*     */   {
/*  90 */     this.delegate.contactKnown(node_id, attachment, force);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void contactAlive(byte[] node_id, DHTRouterContactAttachment attachment)
/*     */   {
/*  98 */     this.delegate.contactAlive(node_id, attachment);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTRouterContact contactDead(byte[] node_id, boolean force)
/*     */   {
/* 106 */     return this.delegate.contactDead(node_id, force);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTRouterContact findContact(byte[] node_id)
/*     */   {
/* 113 */     return this.delegate.findContact(node_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<DHTRouterContact> findClosestContacts(byte[] node_id, int num_to_return, boolean live_only)
/*     */   {
/* 122 */     return this.delegate.findClosestContacts(node_id, num_to_return, live_only);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void recordLookup(byte[] node_id)
/*     */   {
/* 129 */     this.delegate.recordLookup(node_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean requestPing(byte[] node_id)
/*     */   {
/* 136 */     return this.delegate.requestPing(node_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshIdleLeaves(long idle_max)
/*     */   {
/* 143 */     this.delegate.refreshIdleLeaves(idle_max);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] refreshRandom()
/*     */   {
/* 149 */     return this.delegate.refreshRandom();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<DHTRouterContact> findBestContacts(int max)
/*     */   {
/* 156 */     return this.delegate.findBestContacts(max);
/*     */   }
/*     */   
/*     */ 
/*     */   public List<DHTRouterContact> getAllContacts()
/*     */   {
/* 162 */     return this.delegate.getAllContacts();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTRouterStats getStats()
/*     */   {
/* 168 */     return this.delegate.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSleeping(boolean sleeping)
/*     */   {
/* 175 */     this.delegate.setSleeping(sleeping);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSuspended(boolean susp)
/*     */   {
/* 182 */     this.delegate.setSuspended(susp);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 188 */     this.delegate.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */   public void print()
/*     */   {
/* 194 */     this.delegate.print();
/*     */   }
/*     */   
/*     */   public boolean addObserver(DHTRouterObserver rto)
/*     */   {
/* 199 */     return this.delegate.addObserver(rto);
/*     */   }
/*     */   
/*     */   public boolean containsObserver(DHTRouterObserver rto)
/*     */   {
/* 204 */     return this.delegate.containsObserver(rto);
/*     */   }
/*     */   
/*     */   public boolean removeObserver(DHTRouterObserver rto)
/*     */   {
/* 209 */     return this.delegate.removeObserver(rto);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */