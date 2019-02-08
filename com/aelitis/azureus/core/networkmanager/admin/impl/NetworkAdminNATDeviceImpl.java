/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNATDevice;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPluginService;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class NetworkAdminNATDeviceImpl
/*     */   implements NetworkAdminNATDevice
/*     */ {
/*     */   private final UPnPPluginService service;
/*     */   private InetAddress external_address;
/*     */   private long address_time;
/*     */   
/*     */   protected NetworkAdminNATDeviceImpl(UPnPPluginService _service)
/*     */   {
/*  43 */     this.service = _service;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  49 */     return this.service.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getAddress()
/*     */   {
/*     */     try
/*     */     {
/*  57 */       return InetAddress.getByName(this.service.getAddress());
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  61 */       Debug.printStackTrace(e);
/*     */     }
/*  63 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  70 */     return this.service.getPort();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getExternalAddress()
/*     */   {
/*  76 */     long now = SystemTime.getCurrentTime();
/*     */     
/*  78 */     if ((this.external_address != null) && (now > this.address_time) && (now - this.address_time < 60000L))
/*     */     {
/*     */ 
/*     */ 
/*  82 */       return this.external_address;
/*     */     }
/*     */     try
/*     */     {
/*  86 */       this.external_address = InetAddress.getByName(this.service.getExternalAddress());
/*     */       
/*  88 */       this.address_time = now;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  92 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*  95 */     return this.external_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean sameAs(NetworkAdminNATDeviceImpl other)
/*     */   {
/* 102 */     if ((!getAddress().equals(other.getAddress())) || (getPort() != other.getPort()))
/*     */     {
/*     */ 
/* 105 */       return false;
/*     */     }
/*     */     
/* 108 */     InetAddress e1 = getExternalAddress();
/* 109 */     InetAddress e2 = other.getExternalAddress();
/*     */     
/* 111 */     if ((e1 == null) && (e2 == null))
/*     */     {
/* 113 */       return true;
/*     */     }
/* 115 */     if ((e1 == null) || (e2 == null))
/*     */     {
/* 117 */       return false;
/*     */     }
/*     */     
/* 120 */     return e1.equals(e2);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 126 */     String res = getName();
/*     */     
/* 128 */     res = res + ": address=" + this.service.getAddress() + ":" + this.service.getPort();
/*     */     
/* 130 */     InetAddress ext = getExternalAddress();
/*     */     
/* 132 */     if (ext == null)
/*     */     {
/* 134 */       res = res + ", no public address available";
/*     */     }
/*     */     else {
/* 137 */       res = res + ", public address=" + ext.getHostAddress();
/*     */     }
/*     */     
/* 140 */     return res;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */