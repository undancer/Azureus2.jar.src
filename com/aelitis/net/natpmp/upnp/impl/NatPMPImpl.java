/*     */ package com.aelitis.net.natpmp.upnp.impl;
/*     */ 
/*     */ import com.aelitis.net.natpmp.NatPMPDevice;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionPortMapping;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public class NatPMPImpl
/*     */ {
/*     */   private NatPMPDevice natDevice;
/*  33 */   private List mappings = new ArrayList();
/*     */   
/*     */   public NatPMPImpl(NatPMPDevice device) throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  39 */       this.natDevice = device;
/*     */     } catch (Exception e) {
/*  41 */       throw new UPnPException("Error in getting NatPMP Service!");
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
/*     */ 
/*     */   public void addPortMapping(boolean tcp, int port, String description)
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  58 */       this.natDevice.addPortMapping(tcp, port, port);
/*     */     } catch (Exception e) {
/*  60 */       throw new UPnPException("addPortMapping failed", e);
/*     */     }
/*     */     
/*  63 */     synchronized (this)
/*     */     {
/*  65 */       Iterator it = this.mappings.iterator();
/*  66 */       while (it.hasNext()) {
/*  67 */         portMapping m = (portMapping)it.next();
/*     */         
/*  69 */         if ((m.getExternalPort() == port) && (m.isTCP() == tcp)) {
/*  70 */           it.remove();
/*     */         }
/*     */       }
/*  73 */       this.mappings.add(new portMapping(port, tcp, this.natDevice.getLocalAddress().getHostAddress(), description));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void deletePortMapping(boolean tcp, int port)
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  83 */       this.natDevice.deletePortMapping(tcp, port, port);
/*     */     } catch (Exception e) {
/*  85 */       throw new UPnPException("deletePortMapping failed", e);
/*     */     }
/*     */     
/*  88 */     synchronized (this)
/*     */     {
/*  90 */       Iterator it = this.mappings.iterator();
/*  91 */       while (it.hasNext()) {
/*  92 */         portMapping m = (portMapping)it.next();
/*     */         
/*  94 */         if ((m.getExternalPort() == port) && (m.isTCP() == tcp)) {
/*  95 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public UPnPWANConnectionPortMapping[] getPortMappings() throws UPnPException
/*     */   {
/* 103 */     synchronized (this)
/*     */     {
/* 105 */       UPnPWANConnectionPortMapping[] res2 = new UPnPWANConnectionPortMapping[this.mappings.size()];
/* 106 */       this.mappings.toArray(res2);
/* 107 */       return res2;
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getStatusInfo()
/*     */     throws UPnPException
/*     */   {
/* 114 */     String connection_status = null;
/* 115 */     String connection_error = null;
/* 116 */     String uptime = null;
/*     */     
/*     */ 
/* 119 */     uptime = "" + this.natDevice.getEpoch();
/* 120 */     return new String[] { connection_status, connection_error, uptime };
/*     */   }
/*     */   
/*     */ 
/*     */   public String getExternalIPAddress()
/*     */   {
/* 126 */     return this.natDevice.getExternalIPAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   private static class portMapping
/*     */     implements UPnPWANConnectionPortMapping
/*     */   {
/*     */     protected int external_port;
/*     */     
/*     */     protected boolean tcp;
/*     */     
/*     */     protected String internal_host;
/*     */     
/*     */     protected String description;
/*     */     
/*     */     protected portMapping(int _external_port, boolean _tcp, String _internal_host, String _description)
/*     */     {
/* 143 */       this.external_port = _external_port;
/* 144 */       this.tcp = _tcp;
/* 145 */       this.internal_host = _internal_host;
/* 146 */       this.description = _description;
/*     */     }
/*     */     
/*     */     public boolean isTCP()
/*     */     {
/* 151 */       return this.tcp;
/*     */     }
/*     */     
/*     */     public int getExternalPort()
/*     */     {
/* 156 */       return this.external_port;
/*     */     }
/*     */     
/*     */     public String getInternalHost()
/*     */     {
/* 161 */       return this.internal_host;
/*     */     }
/*     */     
/*     */     public String getDescription()
/*     */     {
/* 166 */       return this.description;
/*     */     }
/*     */     
/*     */     protected String getString()
/*     */     {
/* 171 */       return getDescription() + " [" + getExternalPort() + ":" + (isTCP() ? "TCP" : "UDP") + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/upnp/impl/NatPMPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */