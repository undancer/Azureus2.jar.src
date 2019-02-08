/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterfaceAddress;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProtocol;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPMapping;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class NetworkAdminProtocolImpl
/*     */   implements NetworkAdminProtocol
/*     */ {
/*     */   private final AzureusCore core;
/*     */   private final int type;
/*     */   private final int port;
/*     */   
/*     */   protected NetworkAdminProtocolImpl(AzureusCore _core, int _type)
/*     */   {
/*  49 */     this.core = _core;
/*  50 */     this.type = _type;
/*  51 */     this.port = -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminProtocolImpl(AzureusCore _core, int _type, int _port)
/*     */   {
/*  60 */     this.core = _core;
/*  61 */     this.type = _type;
/*  62 */     this.port = _port;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  68 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  74 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress test(NetworkAdminNetworkInterfaceAddress address)
/*     */     throws NetworkAdminException
/*     */   {
/*  83 */     return test(address, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress test(NetworkAdminNetworkInterfaceAddress address, boolean upnp_map, NetworkAdminProgressListener listener)
/*     */     throws NetworkAdminException
/*     */   {
/*  94 */     InetAddress bind_ip = address == null ? null : address.getAddress();
/*     */     
/*     */     NetworkAdminProtocolTester tester;
/*     */     NetworkAdminProtocolTester tester;
/*  98 */     if (this.type == 1)
/*     */     {
/* 100 */       tester = new NetworkAdminHTTPTester(this.core, listener);
/*     */     } else { NetworkAdminProtocolTester tester;
/* 102 */       if (this.type == 2)
/*     */       {
/* 104 */         tester = new NetworkAdminTCPTester(this.core, listener);
/*     */       }
/*     */       else
/*     */       {
/* 108 */         tester = new NetworkAdminUDPTester(this.core, listener);
/*     */       }
/*     */     }
/*     */     InetAddress res;
/*     */     InetAddress res;
/* 113 */     if (this.port <= 0)
/*     */     {
/* 115 */       res = tester.testOutbound(bind_ip, 0);
/*     */     }
/*     */     else
/*     */     {
/* 119 */       UPnPMapping new_mapping = null;
/*     */       
/* 121 */       if (upnp_map)
/*     */       {
/* 123 */         PluginInterface pi_upnp = this.core.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*     */         
/* 125 */         if (pi_upnp != null)
/*     */         {
/* 127 */           UPnPPlugin upnp = (UPnPPlugin)pi_upnp.getPlugin();
/*     */           
/* 129 */           UPnPMapping mapping = upnp.getMapping(this.type != 3, this.port);
/*     */           
/* 131 */           if (mapping == null)
/*     */           {
/* 133 */             new_mapping = mapping = upnp.addMapping("NAT Tester", this.type != 3, this.port, true);
/*     */             
/*     */ 
/*     */             try
/*     */             {
/* 138 */               Thread.sleep(500L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 148 */         res = tester.testInbound(bind_ip, this.port);
/*     */       }
/*     */       finally
/*     */       {
/* 152 */         if (new_mapping != null)
/*     */         {
/* 154 */           new_mapping.destroy();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 159 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress test(NetworkAdminNetworkInterfaceAddress address, NetworkAdminProgressListener listener)
/*     */     throws NetworkAdminException
/*     */   {
/* 169 */     return test(address, false, listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTypeString()
/*     */   {
/*     */     String res;
/*     */     String res;
/* 177 */     if (this.type == 1)
/*     */     {
/* 179 */       res = "HTTP";
/*     */     } else { String res;
/* 181 */       if (this.type == 2)
/*     */       {
/* 183 */         res = "TCP";
/*     */       }
/*     */       else
/*     */       {
/* 187 */         res = "UDP";
/*     */       }
/*     */     }
/* 190 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 196 */     String res = getTypeString();
/*     */     
/* 198 */     if (this.port == -1)
/*     */     {
/* 200 */       return res + " outbound";
/*     */     }
/*     */     
/*     */ 
/* 204 */     return res + " port " + this.port + " inbound";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminProtocolImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */