/*     */ package com.aelitis.azureus.core.instancemanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
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
/*     */ public class AZPortClashHandler
/*     */   implements AZInstanceManagerListener
/*     */ {
/*     */   private final AZInstance my_instance;
/*     */   private int last_warned_tcp;
/*     */   private int last_warned_udp;
/*     */   private int last_warned_udp2;
/*     */   
/*     */   protected AZPortClashHandler(AZInstanceManagerImpl inst_man)
/*     */   {
/*  46 */     this.my_instance = inst_man.getMyInstance();
/*     */     
/*  48 */     inst_man.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void check(AZInstance instance)
/*     */   {
/*  55 */     if (instance == this.my_instance)
/*     */     {
/*  57 */       return;
/*     */     }
/*     */     
/*  60 */     InetAddress my_ext = this.my_instance.getExternalAddress();
/*  61 */     InetAddress other_ext = instance.getExternalAddress();
/*     */     
/*  63 */     if ((my_ext.isLoopbackAddress()) || (other_ext.isLoopbackAddress()) || (my_ext.equals(other_ext)))
/*     */     {
/*     */ 
/*     */ 
/*  67 */       String warning = null;
/*     */       
/*  69 */       int my_tcp = this.my_instance.getTCPListenPort();
/*     */       
/*  71 */       if ((my_tcp != 0) && (my_tcp != this.last_warned_tcp) && (my_tcp == instance.getTCPListenPort()))
/*     */       {
/*  73 */         warning = "TCP " + my_tcp;
/*     */         
/*  75 */         this.last_warned_tcp = my_tcp;
/*     */       }
/*     */       
/*  78 */       int my_udp = this.my_instance.getUDPListenPort();
/*  79 */       int my_udp2 = this.my_instance.getUDPNonDataListenPort();
/*     */       
/*  81 */       int other_udp = instance.getUDPListenPort();
/*  82 */       int other_udp2 = instance.getUDPNonDataListenPort();
/*     */       
/*  84 */       if ((my_udp != 0) && (my_udp != this.last_warned_udp) && ((my_udp == other_udp) || (my_udp == other_udp2)))
/*     */       {
/*  86 */         warning = (warning == null ? "" : new StringBuilder().append(warning).append(", ").toString()) + "UDP " + my_udp;
/*     */         
/*  88 */         this.last_warned_udp = my_udp;
/*     */       }
/*     */       
/*  91 */       if ((my_udp != my_udp2) && (my_udp2 != 0) && (my_udp2 != this.last_warned_udp2) && ((my_udp2 == other_udp) || (my_udp2 == other_udp2)))
/*     */       {
/*  93 */         warning = (warning == null ? "" : new StringBuilder().append(warning).append(", ").toString()) + "UDP " + my_udp2;
/*     */         
/*  95 */         this.last_warned_udp2 = my_udp2;
/*     */       }
/*     */       
/*     */ 
/*  99 */       if (warning != null)
/*     */       {
/* 101 */         Logger.logTextResource(new LogAlert(true, 1, "azinstancehandler.alert.portclash"), new String[] { warning, String.valueOf(10000), String.valueOf(65535) });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void instanceFound(AZInstance instance)
/*     */   {
/* 115 */     check(instance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void instanceChanged(AZInstance instance)
/*     */   {
/* 122 */     check(instance);
/*     */   }
/*     */   
/*     */   public void instanceLost(AZInstance instance) {}
/*     */   
/*     */   public void instanceTracked(AZInstanceTracked instance) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/impl/AZPortClashHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */