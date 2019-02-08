/*     */ package com.aelitis.net.natpmp.upnp.impl;
/*     */ 
/*     */ import com.aelitis.net.natpmp.NatPMPDevice;
/*     */ import com.aelitis.net.natpmp.upnp.NatPMPUPnP;
/*     */ import com.aelitis.net.upnp.UPnP;
/*     */ import com.aelitis.net.upnp.UPnPListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class NatPMPUPnPImpl
/*     */   implements NatPMPUPnP
/*     */ {
/*     */   private UPnP upnp;
/*     */   private NatPMPDevice nat_device;
/*     */   private NatPMPUPnPRootDeviceImpl root_device;
/*  43 */   private List listeners = new ArrayList();
/*     */   
/*  45 */   private boolean enabled = true;
/*     */   
/*     */ 
/*     */   private boolean started;
/*     */   
/*     */ 
/*     */ 
/*     */   public NatPMPUPnPImpl(UPnP _upnp, NatPMPDevice _nat_device)
/*     */   {
/*  54 */     this.upnp = _upnp;
/*  55 */     this.nat_device = _nat_device;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void start()
/*     */   {
/*  61 */     SimpleTimer.addPeriodicEvent("NATPMP:search", 60000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*  70 */         NatPMPUPnPImpl.this.search();
/*     */       }
/*     */       
/*  73 */     });
/*  74 */     search();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean _enabled)
/*     */   {
/*  81 */     this.enabled = _enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  87 */     return this.enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void search()
/*     */   {
/*  93 */     if (!this.enabled)
/*     */     {
/*  95 */       return;
/*     */     }
/*     */     
/*  98 */     if (this.root_device != null)
/*     */     {
/* 100 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 104 */       boolean found = this.nat_device.connect();
/*     */       
/* 106 */       if (found)
/*     */       {
/* 108 */         this.root_device = new NatPMPUPnPRootDeviceImpl(this.upnp, this.nat_device);
/*     */         
/* 110 */         for (int i = 0; i < this.listeners.size(); i++) {
/*     */           try
/*     */           {
/* 113 */             ((UPnPListener)this.listeners.get(i)).rootDeviceFound(this.root_device);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 117 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 123 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void addListener(UPnPListener listener)
/*     */   {
/* 131 */     this.listeners.add(listener);
/*     */     
/* 133 */     if (this.root_device == null)
/*     */     {
/* 135 */       if ((this.listeners.size() == 1) && (!this.started))
/*     */       {
/* 137 */         this.started = true;
/*     */         
/* 139 */         start();
/*     */       }
/*     */     }
/*     */     else {
/* 143 */       listener.rootDeviceFound(this.root_device);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void removeListener(UPnPListener listener)
/*     */   {
/* 151 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/upnp/impl/NatPMPUPnPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */