/*     */ package com.aelitis.azureus.core.instancemanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter.StateListener;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter.VCPublicAddress;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginListener;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
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
/*     */ public class AZMyInstanceImpl
/*     */   extends AZInstanceImpl
/*     */ {
/*     */   public static final long FORCE_READ_EXT_MIN = 28800000L;
/*     */   public static final long UPNP_READ_MIN = 300000L;
/*     */   final AZInstanceManagerAdapter adapter;
/*     */   final AZInstanceManagerImpl manager;
/*     */   private String id;
/*     */   private InetAddress internal_address;
/*     */   private int tcp_port;
/*     */   private int udp_port;
/*     */   private int udp_non_data_port;
/*     */   private long last_upnp_read;
/*     */   private InetAddress dht_address;
/*     */   private long dht_address_time;
/*     */   private long last_force_read_ext;
/*     */   private InetAddress last_external_address;
/*     */   
/*     */   protected AZMyInstanceImpl(AZInstanceManagerAdapter _adapter, AZInstanceManagerImpl _manager)
/*     */   {
/*  69 */     this.adapter = _adapter;
/*  70 */     this.manager = _manager;
/*     */     
/*  72 */     this.id = this.adapter.getID();
/*     */     
/*  74 */     if (this.id.length() == 0)
/*     */     {
/*  76 */       this.id = ("" + SystemTime.getCurrentTime());
/*     */     }
/*     */     
/*  79 */     this.id = ByteFormatter.encodeString(new SHA1Simple().calculateHash(this.id.getBytes()));
/*     */     
/*  81 */     COConfigurationManager.addListener(new COConfigurationListener()
/*     */     {
/*     */ 
/*     */       public void configurationSaved()
/*     */       {
/*     */ 
/*  87 */         AZMyInstanceImpl.this.readConfig(false);
/*     */       }
/*     */       
/*  90 */     });
/*  91 */     readConfig(true);
/*     */     
/*  93 */     this.adapter.addListener(new AZInstanceManagerAdapter.StateListener()
/*     */     {
/*     */ 
/*     */       public void started()
/*     */       {
/*     */ 
/*  99 */         DHTPlugin dht = AZMyInstanceImpl.this.adapter.getDHTPlugin();
/*     */         
/* 101 */         if (dht != null)
/*     */         {
/* 103 */           dht.addListener(new DHTPluginListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void localAddressChanged(DHTPluginContact local_contact)
/*     */             {
/*     */ 
/* 110 */               InetAddress latest_dht_address = local_contact.getAddress().getAddress();
/*     */               
/* 112 */               if (AZMyInstanceImpl.this.sameFamily(AZMyInstanceImpl.this.internal_address, latest_dht_address))
/*     */               {
/* 114 */                 AZMyInstanceImpl.this.dht_address = latest_dht_address;
/* 115 */                 AZMyInstanceImpl.this.dht_address_time = SystemTime.getCurrentTime();
/*     */                 
/* 117 */                 AZMyInstanceImpl.this.manager.informChanged(AZMyInstanceImpl.this);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void stopped() {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void readConfig(boolean first_time)
/*     */   {
/* 135 */     InetAddress new_internal_address = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */     
/* 137 */     if (new_internal_address == null) {
/*     */       try
/*     */       {
/* 140 */         new_internal_address = InetAddress.getByName("0.0.0.0");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 146 */     int[] ports = this.adapter.getPorts();
/*     */     
/* 148 */     int new_tcp_port = ports[0];
/* 149 */     int new_udp_port = ports[1];
/* 150 */     int new_udp_non_data_port = ports[2];
/*     */     
/* 152 */     boolean same = true;
/*     */     
/* 154 */     if (!first_time)
/*     */     {
/* 156 */       same = (this.internal_address.equals(new_internal_address)) && (this.tcp_port == new_tcp_port) && (this.udp_port == new_udp_port) && (this.udp_non_data_port == new_udp_non_data_port);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 162 */     this.internal_address = new_internal_address;
/* 163 */     this.tcp_port = new_tcp_port;
/* 164 */     this.udp_port = new_udp_port;
/* 165 */     this.udp_non_data_port = new_udp_non_data_port;
/*     */     
/* 167 */     if (!same)
/*     */     {
/* 169 */       this.manager.informChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private InetAddress readExternalAddress()
/*     */   {
/* 176 */     InetAddress external_address = null;
/*     */     
/*     */ 
/*     */ 
/* 180 */     if (this.manager.isClosing())
/*     */     {
/* 182 */       external_address = this.last_external_address;
/*     */       
/* 184 */       if (external_address == null) {
/*     */         try
/*     */         {
/* 187 */           external_address = InetAddress.getByName("127.0.0.1");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 191 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 195 */       return external_address;
/*     */     }
/*     */     
/* 198 */     DHTPlugin dht = this.adapter.getDHTPlugin();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 203 */     if ((this.dht_address != null) && (this.dht_address_time <= SystemTime.getCurrentTime()))
/*     */     {
/* 205 */       AZInstanceManagerAdapter.VCPublicAddress a = this.adapter.getVCPublicAddress();
/*     */       
/* 207 */       if (a != null)
/*     */       {
/* 209 */         long cache_time = a.getCacheTime();
/*     */         
/* 211 */         if (cache_time <= this.dht_address_time)
/*     */         {
/* 213 */           external_address = this.dht_address;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 218 */     if ((external_address == null) && ((dht == null) || (dht.getStatus() != 3)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 223 */       AZInstanceManagerAdapter.VCPublicAddress a = this.adapter.getVCPublicAddress();
/*     */       
/* 225 */       if (a != null)
/*     */       {
/*     */         try
/*     */         {
/* 229 */           external_address = InetAddress.getByName(a.getAddress());
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 233 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 238 */     if ((external_address == null) && (dht != null))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 244 */         InetAddress latest_dht_address = dht.getLocalAddress().getAddress().getAddress();
/*     */         
/*     */ 
/*     */ 
/* 248 */         if (sameFamily(this.internal_address, latest_dht_address))
/*     */         {
/* 250 */           external_address = latest_dht_address;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 257 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 259 */     if (this.last_force_read_ext > now)
/*     */     {
/* 261 */       this.last_force_read_ext = now;
/*     */     }
/*     */     
/* 264 */     boolean ok_to_try_ext = now - this.last_force_read_ext > 28800000L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 272 */     if ((external_address == null) && (this.last_external_address != null))
/*     */     {
/* 274 */       if (this.last_upnp_read > now)
/*     */       {
/* 276 */         this.last_upnp_read = now;
/*     */       }
/*     */       
/* 279 */       if ((now - this.last_upnp_read > 300000L) || (ok_to_try_ext))
/*     */       {
/* 281 */         this.last_upnp_read = now;
/*     */         try
/*     */         {
/* 284 */           UPnPPlugin upnp = this.adapter.getUPnPPlugin();
/*     */           
/* 286 */           if (upnp != null)
/*     */           {
/* 288 */             String[] addresses = upnp.getExternalIPAddresses();
/*     */             
/* 290 */             for (int i = 0; i < addresses.length; i++)
/*     */             {
/* 292 */               if (addresses[i].equals(this.last_external_address.getHostAddress()))
/*     */               {
/* 294 */                 external_address = this.last_external_address;
/*     */                 
/* 296 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/* 305 */     if (external_address == null)
/*     */     {
/*     */ 
/*     */ 
/* 309 */       if (ok_to_try_ext)
/*     */       {
/* 311 */         this.last_force_read_ext = now;
/*     */         
/* 313 */         external_address = this.adapter.getPublicAddress();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 319 */     if (external_address == null)
/*     */     {
/* 321 */       if (this.last_external_address != null)
/*     */       {
/* 323 */         external_address = this.last_external_address;
/*     */       } else {
/*     */         try
/*     */         {
/* 327 */           external_address = InetAddress.getByName("127.0.0.1");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 331 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 336 */       this.last_external_address = external_address;
/*     */     }
/*     */     
/* 339 */     return external_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean sameFamily(InetAddress a1, InetAddress a2)
/*     */   {
/* 347 */     return a1 instanceof Inet4Address == a2 instanceof Inet4Address;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/* 353 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getApplicationID()
/*     */   {
/* 359 */     return SystemProperties.getApplicationIdentifier() + "_" + SystemProperties.getApplicationVersion();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getInternalAddress()
/*     */   {
/* 365 */     return this.internal_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public List getInternalAddresses()
/*     */   {
/* 371 */     List l = new ArrayList();
/*     */     
/* 373 */     if (this.internal_address != null)
/*     */     {
/* 375 */       l.add(this.internal_address);
/*     */     }
/*     */     
/* 378 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getExternalAddress()
/*     */   {
/* 384 */     return readExternalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTCPListenPort()
/*     */   {
/* 390 */     return this.tcp_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPListenPort()
/*     */   {
/* 396 */     return this.udp_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPNonDataListenPort()
/*     */   {
/* 402 */     return this.udp_non_data_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> getProperties()
/*     */   {
/* 408 */     return COConfigurationManager.getMapParameter("instance.manager.props", null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/impl/AZMyInstanceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */