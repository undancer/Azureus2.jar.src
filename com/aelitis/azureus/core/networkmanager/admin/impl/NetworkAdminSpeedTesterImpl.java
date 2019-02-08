/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTester;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public abstract class NetworkAdminSpeedTesterImpl
/*     */   implements NetworkAdminSpeedTester
/*     */ {
/*  36 */   private final CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   private boolean result_reported;
/*     */   
/*     */ 
/*     */   protected abstract void abort(String paramString);
/*     */   
/*     */ 
/*     */   protected abstract void abort(String paramString, Throwable paramThrowable);
/*     */   
/*     */ 
/*     */   public void addListener(NetworkAdminSpeedTesterListener listener)
/*     */   {
/*  50 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*  53 */   public void removeListener(NetworkAdminSpeedTesterListener listener) { this.listeners.remove(listener); }
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
/*     */   protected void sendResultToListeners(NetworkAdminSpeedTesterResult r)
/*     */   {
/*  67 */     synchronized (this)
/*     */     {
/*  69 */       if (this.result_reported)
/*     */       {
/*  71 */         return;
/*     */       }
/*     */       
/*  74 */       this.result_reported = true;
/*     */     }
/*     */     
/*  77 */     Iterator it = this.listeners.iterator();
/*     */     
/*  79 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/*  82 */         ((NetworkAdminSpeedTesterListener)it.next()).complete(this, r);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  86 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void sendStageUpdateToListeners(String status)
/*     */   {
/*  96 */     Iterator it = this.listeners.iterator();
/*     */     
/*  98 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 101 */         ((NetworkAdminSpeedTesterListener)it.next()).stage(this, status);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 105 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminSpeedTesterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */