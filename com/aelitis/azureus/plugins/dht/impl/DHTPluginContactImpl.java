/*     */ package com.aelitis.azureus.plugins.dht.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginProgressListener;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Map;
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
/*     */ public class DHTPluginContactImpl
/*     */   implements DHTPluginContact
/*     */ {
/*     */   private DHTPluginImpl plugin;
/*     */   private DHTTransportContact contact;
/*     */   
/*     */   protected DHTPluginContactImpl(DHTPluginImpl _plugin, DHTTransportContact _contact)
/*     */   {
/*  44 */     this.plugin = _plugin;
/*  45 */     this.contact = _contact;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTPluginImpl getDHT()
/*     */   {
/*  51 */     return this.plugin;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportContact getContact()
/*     */   {
/*  57 */     return this.contact;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/*  63 */     return this.contact.getID();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  69 */     return this.contact.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetwork()
/*     */   {
/*  75 */     return this.plugin.getDHT().getTransport().getNetwork();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/*  81 */     return this.contact.getProtocolVersion();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/*  87 */     return this.contact.getAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> exportToMap()
/*     */   {
/*  93 */     return this.contact.exportContactToMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlive(long timeout)
/*     */   {
/* 100 */     return this.contact.isAlive(timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void isAlive(long timeout, final DHTPluginOperationListener listener)
/*     */   {
/* 108 */     this.contact.isAlive(new DHTTransportReplyHandlerAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void pingReply(DHTTransportContact contact)
/*     */       {
/*     */ 
/* 115 */         listener.complete(null, false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */       public void failed(DHTTransportContact contact, Throwable error) { listener.complete(null, true); } }, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isOrHasBeenLocal()
/*     */   {
/* 132 */     return this.plugin.isRecentAddress(this.contact.getAddress().getAddress().getHostAddress());
/*     */   }
/*     */   
/*     */ 
/*     */   public Map openTunnel()
/*     */   {
/* 138 */     DHTNATPuncher puncher = this.plugin.getDHT().getNATPuncher();
/*     */     
/* 140 */     if (puncher == null)
/*     */     {
/* 142 */       return null;
/*     */     }
/*     */     
/* 145 */     return puncher.punch("Tunnel", this.contact, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map openTunnel(DHTPluginContact[] rendezvous, Map client_data)
/*     */   {
/* 153 */     DHTNATPuncher puncher = this.plugin.getDHT().getNATPuncher();
/*     */     
/* 155 */     if (puncher == null)
/*     */     {
/* 157 */       return null;
/*     */     }
/*     */     
/* 160 */     if ((rendezvous == null) || (rendezvous.length == 0))
/*     */     {
/* 162 */       return puncher.punch("Tunnel", this.contact, null, client_data);
/*     */     }
/*     */     
/*     */ 
/* 166 */     DHTTransportContact[] r = new DHTTransportContact[rendezvous.length];
/*     */     
/* 168 */     for (int i = 0; i < r.length; i++)
/*     */     {
/* 170 */       r[0] = ((DHTPluginContactImpl)rendezvous[i]).contact;
/*     */     }
/*     */     
/* 173 */     Map result = puncher.punch("Tunnel", this.contact, r, client_data);
/*     */     
/* 175 */     DHTTransportContact used = r[0];
/*     */     
/* 177 */     if (used != null)
/*     */     {
/* 179 */       rendezvous[0] = new DHTPluginContactImpl(this.plugin, used);
/*     */     }
/*     */     
/* 182 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] read(DHTPluginProgressListener listener, byte[] handler_key, byte[] key, long timeout)
/*     */   {
/* 193 */     return this.plugin.read(listener, this, handler_key, key, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(DHTPluginProgressListener listener, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*     */   {
/* 204 */     this.plugin.write(listener, this, handler_key, key, data, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] call(DHTPluginProgressListener listener, byte[] handler_key, byte[] data, long timeout)
/*     */   {
/* 214 */     return this.plugin.call(listener, this, handler_key, data, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 220 */     return this.contact.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/impl/DHTPluginContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */