/*     */ package com.aelitis.azureus.core.instancemanager.impl;
/*     */ 
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class AZOtherInstanceImpl
/*     */   extends AZInstanceImpl
/*     */ {
/*     */   private final String id;
/*     */   private final String app_id;
/*     */   
/*     */   protected static AZOtherInstanceImpl decode(InetAddress internal_address, Map map)
/*     */   {
/*  40 */     String id = new String((byte[])map.get("id"));
/*  41 */     String int_ip = new String((byte[])map.get("iip"));
/*  42 */     String ext_ip = new String((byte[])map.get("eip"));
/*  43 */     int tcp = ((Long)map.get("tp")).intValue();
/*  44 */     int udp = ((Long)map.get("dp")).intValue();
/*     */     
/*  46 */     Long l_udp_other = (Long)map.get("dp2");
/*     */     
/*  48 */     int udp_other = l_udp_other == null ? udp : l_udp_other.intValue();
/*     */     
/*  50 */     byte[] app_id_bytes = (byte[])map.get("ai");
/*     */     
/*     */     String app_id;
/*     */     String app_id;
/*  54 */     if (app_id_bytes == null)
/*     */     {
/*  56 */       app_id = "az_4.2.0.2";
/*     */     }
/*     */     else
/*     */     {
/*  60 */       app_id = new String(app_id_bytes);
/*     */     }
/*     */     
/*  63 */     Map<String, Object> props = (Map)map.get("pr");
/*     */     try
/*     */     {
/*  66 */       if (!int_ip.equals("0.0.0.0"))
/*     */       {
/*  68 */         internal_address = InetAddress.getByName(int_ip);
/*     */       }
/*     */       
/*  71 */       InetAddress external_address = InetAddress.getByName(ext_ip);
/*     */       
/*     */ 
/*     */ 
/*  75 */       if (internal_address instanceof Inet4Address == external_address instanceof Inet4Address)
/*     */       {
/*  77 */         return new AZOtherInstanceImpl(id, app_id, internal_address, external_address, tcp, udp, udp_other, props);
/*     */       }
/*     */       
/*  80 */       return null;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  84 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*  87 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  92 */   private List internal_addresses = new ArrayList();
/*     */   
/*     */ 
/*     */   private InetAddress external_address;
/*     */   
/*     */ 
/*     */   private int tcp_port;
/*     */   
/*     */ 
/*     */   private int udp_port;
/*     */   
/*     */ 
/*     */   private final int udp_non_data_port;
/*     */   
/*     */   private final Map<String, Object> props;
/*     */   
/*     */   private long alive_time;
/*     */   
/*     */ 
/*     */   protected AZOtherInstanceImpl(String _id, String _app_id, InetAddress _internal_address, InetAddress _external_address, int _tcp_port, int _udp_port, int _udp_non_data_port, Map<String, Object> _props)
/*     */   {
/* 113 */     this.id = _id;
/* 114 */     this.app_id = _app_id;
/*     */     
/* 116 */     this.internal_addresses.add(_internal_address);
/*     */     
/* 118 */     this.external_address = _external_address;
/* 119 */     this.tcp_port = _tcp_port;
/* 120 */     this.udp_port = _udp_port;
/* 121 */     this.udp_non_data_port = _udp_non_data_port;
/*     */     
/* 123 */     this.props = _props;
/*     */     
/* 125 */     this.alive_time = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean update(AZOtherInstanceImpl new_inst)
/*     */   {
/* 132 */     this.alive_time = SystemTime.getCurrentTime();
/*     */     
/* 134 */     InetAddress new_address = new_inst.getInternalAddress();
/*     */     
/* 136 */     boolean same = true;
/*     */     
/* 138 */     if (!this.internal_addresses.contains(new_address))
/*     */     {
/* 140 */       same = false;
/*     */       
/* 142 */       List new_addresses = new ArrayList(this.internal_addresses);
/*     */       
/* 144 */       new_addresses.add(0, new_address);
/*     */       
/* 146 */       this.internal_addresses = new_addresses;
/*     */     }
/*     */     
/* 149 */     same = (same) && (this.external_address.equals(new_inst.external_address)) && (this.tcp_port == new_inst.tcp_port) && (this.udp_port == new_inst.udp_port);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 155 */     this.external_address = new_inst.external_address;
/* 156 */     this.tcp_port = new_inst.tcp_port;
/* 157 */     this.udp_port = new_inst.udp_port;
/*     */     
/* 159 */     return !same;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/* 165 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getApplicationID()
/*     */   {
/* 171 */     return this.app_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getInternalAddress()
/*     */   {
/* 177 */     return (InetAddress)this.internal_addresses.get(0);
/*     */   }
/*     */   
/*     */ 
/*     */   public List getInternalAddresses()
/*     */   {
/* 183 */     return new ArrayList(this.internal_addresses);
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getExternalAddress()
/*     */   {
/* 189 */     return this.external_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTCPListenPort()
/*     */   {
/* 195 */     return this.tcp_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPListenPort()
/*     */   {
/* 201 */     return this.udp_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPNonDataListenPort()
/*     */   {
/* 207 */     return this.udp_non_data_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> getProperties()
/*     */   {
/* 213 */     return this.props;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getAliveTime()
/*     */   {
/* 219 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 221 */     if (now < this.alive_time)
/*     */     {
/* 223 */       this.alive_time = now;
/*     */     }
/*     */     
/* 226 */     return this.alive_time;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/impl/AZOtherInstanceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */