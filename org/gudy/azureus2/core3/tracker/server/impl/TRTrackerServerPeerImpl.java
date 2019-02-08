/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolverListener;
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
/*     */ public class TRTrackerServerPeerImpl
/*     */   implements TRTrackerServerPeer, TRTrackerServerSimplePeer, HostNameToIPResolverListener, TRTrackerServerNatCheckerListener
/*     */ {
/*     */   private final HashWrapper peer_id;
/*     */   private final int key_hash_code;
/*     */   private byte[] ip;
/*     */   private final boolean ip_override;
/*     */   private short tcp_port;
/*     */   private short udp_port;
/*     */   private short http_port;
/*     */   private byte crypto_level;
/*     */   private byte az_ver;
/*     */   private String ip_str;
/*     */   private byte[] ip_bytes;
/*  49 */   private byte NAT_status = 0;
/*     */   
/*     */ 
/*     */   private long timeout;
/*     */   
/*     */ 
/*     */   private long uploaded;
/*     */   
/*     */ 
/*     */   private long downloaded;
/*     */   
/*     */ 
/*     */   private long amount_left;
/*     */   
/*     */ 
/*     */   private long last_contact_time;
/*     */   
/*     */ 
/*     */   private boolean download_completed;
/*     */   
/*     */ 
/*     */   private boolean biased;
/*     */   
/*     */ 
/*     */   private short up_speed;
/*     */   
/*     */ 
/*     */   private DHTNetworkPosition network_position;
/*     */   
/*     */ 
/*     */   private Object user_data;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRTrackerServerPeerImpl(HashWrapper _peer_id, int _key_hash_code, byte[] _ip, boolean _ip_override, int _tcp_port, int _udp_port, int _http_port, byte _crypto_level, byte _az_ver, long _last_contact_time, boolean _download_completed, byte _last_nat_status, int _up_speed, DHTNetworkPosition _network_position)
/*     */   {
/*  85 */     this.peer_id = _peer_id;
/*  86 */     this.key_hash_code = _key_hash_code;
/*  87 */     this.ip = _ip;
/*  88 */     this.ip_override = _ip_override;
/*  89 */     this.tcp_port = ((short)_tcp_port);
/*  90 */     this.udp_port = ((short)_udp_port);
/*  91 */     this.http_port = ((short)_http_port);
/*  92 */     this.crypto_level = _crypto_level;
/*  93 */     this.az_ver = _az_ver;
/*  94 */     this.last_contact_time = _last_contact_time;
/*  95 */     this.download_completed = _download_completed;
/*  96 */     this.NAT_status = _last_nat_status;
/*  97 */     this.up_speed = (_up_speed > 32767 ? Short.MAX_VALUE : (short)_up_speed);
/*  98 */     this.network_position = _network_position;
/*     */     
/* 100 */     resolveAndCheckNAT();
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
/*     */   protected TRTrackerServerPeerImpl(HashWrapper _peer_id, int _key_hash_code, byte[] _ip, boolean _ip_override, short _tcp_port, short _udp_port, short _http_port, byte _crypto_level, byte _az_ver, String _ip_str, byte[] _ip_bytes, byte _NAT_status, long _timeout, long _uploaded, long _downloaded, long _amount_left, long _last_contact_time, boolean _download_completed, boolean _biased, short _up_speed)
/*     */   {
/* 130 */     this.peer_id = _peer_id;
/* 131 */     this.key_hash_code = _key_hash_code;
/* 132 */     this.ip = _ip;
/* 133 */     this.ip_override = _ip_override;
/* 134 */     this.tcp_port = _tcp_port;
/* 135 */     this.udp_port = _udp_port;
/* 136 */     this.http_port = _http_port;
/* 137 */     this.crypto_level = _crypto_level;
/* 138 */     this.az_ver = _az_ver;
/* 139 */     this.ip_str = _ip_str;
/* 140 */     this.ip_bytes = _ip_bytes;
/* 141 */     this.NAT_status = _NAT_status;
/* 142 */     this.timeout = _timeout;
/* 143 */     this.uploaded = _uploaded;
/* 144 */     this.downloaded = _downloaded;
/* 145 */     this.amount_left = _amount_left;
/* 146 */     this.last_contact_time = _last_contact_time;
/* 147 */     this.download_completed = _download_completed;
/* 148 */     this.biased = _biased;
/* 149 */     this.up_speed = _up_speed;
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
/*     */ 
/*     */   protected boolean update(byte[] _ip, int _port, int _udp_port, int _http_port, byte _crypto_level, byte _az_ver, int _up_speed, DHTNetworkPosition _network_position)
/*     */   {
/* 163 */     this.udp_port = ((short)_udp_port);
/* 164 */     this.http_port = ((short)_http_port);
/* 165 */     this.crypto_level = _crypto_level;
/* 166 */     this.az_ver = _az_ver;
/* 167 */     this.up_speed = (_up_speed > 32767 ? Short.MAX_VALUE : (short)_up_speed);
/* 168 */     this.network_position = _network_position;
/*     */     
/* 170 */     boolean res = false;
/*     */     
/* 172 */     if (_port != getTCPPort())
/*     */     {
/* 174 */       this.tcp_port = ((short)_port);
/*     */       
/* 176 */       res = true;
/*     */     }
/*     */     
/* 179 */     if (!Arrays.equals(_ip, this.ip))
/*     */     {
/* 181 */       this.ip = _ip;
/*     */       
/* 183 */       res = true;
/*     */     }
/*     */     
/* 186 */     if (res)
/*     */     {
/* 188 */       resolveAndCheckNAT();
/*     */     }
/*     */     
/* 191 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void NATCheckComplete(boolean ok)
/*     */   {
/* 198 */     if (ok)
/*     */     {
/* 200 */       this.NAT_status = 3;
/*     */     }
/*     */     else
/*     */     {
/* 204 */       this.NAT_status = 4;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNATStatus(byte status)
/*     */   {
/* 212 */     this.NAT_status = status;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getNATStatus()
/*     */   {
/* 218 */     return this.NAT_status;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isNATStatusBad()
/*     */   {
/* 224 */     return (this.NAT_status == 4) || (this.NAT_status == 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void resolveAndCheckNAT()
/*     */   {
/* 232 */     this.ip_str = new String(this.ip);
/* 233 */     this.ip_bytes = null;
/*     */     
/* 235 */     HostNameToIPResolver.addResolverRequest(this.ip_str, this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 240 */     if (this.tcp_port == 0)
/*     */     {
/* 242 */       this.NAT_status = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 248 */     else if (this.NAT_status == 0)
/*     */     {
/* 250 */       this.NAT_status = 2;
/*     */       
/* 252 */       if (!TRTrackerServerNATChecker.getSingleton().addNATCheckRequest(this.ip_str, getTCPPort(), this))
/*     */       {
/* 254 */         this.NAT_status = 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void hostNameResolutionComplete(InetAddress address)
/*     */   {
/* 264 */     if (address != null)
/*     */     {
/* 266 */       this.ip_str = address.getHostAddress();
/*     */       
/* 268 */       this.ip_bytes = address.getAddress();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastContactTime()
/*     */   {
/* 275 */     return this.last_contact_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getDownloadCompleted()
/*     */   {
/* 281 */     return this.download_completed;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setDownloadCompleted()
/*     */   {
/* 287 */     this.download_completed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isBiased()
/*     */   {
/* 293 */     return this.biased;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBiased(boolean _biased)
/*     */   {
/* 300 */     this.biased = _biased;
/*     */   }
/*     */   
/*     */ 
/*     */   public HashWrapper getPeerId()
/*     */   {
/* 306 */     return this.peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerID()
/*     */   {
/* 312 */     return this.peer_id.getBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getKeyHashCode()
/*     */   {
/* 318 */     return this.key_hash_code;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIPAsRead()
/*     */   {
/* 324 */     return this.ip;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getIPRaw()
/*     */   {
/* 330 */     return new String(this.ip);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getIP()
/*     */   {
/* 341 */     return this.ip_str;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isIPOverride()
/*     */   {
/* 347 */     return this.ip_override;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getIPAddressBytes()
/*     */   {
/* 358 */     return this.ip_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTCPPort()
/*     */   {
/* 364 */     return this.tcp_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPPort()
/*     */   {
/* 370 */     return this.udp_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHTTPPort()
/*     */   {
/* 376 */     return this.http_port & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getCryptoLevel()
/*     */   {
/* 382 */     return this.crypto_level;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getAZVer()
/*     */   {
/* 388 */     return this.az_ver;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUpSpeed()
/*     */   {
/* 394 */     return this.up_speed & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTNetworkPosition getNetworkPosition()
/*     */   {
/* 400 */     return this.network_position;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setTimeout(long _now, long _timeout)
/*     */   {
/* 408 */     this.last_contact_time = _now;
/*     */     
/* 410 */     this.timeout = _timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTimeout()
/*     */   {
/* 416 */     return this.timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSecsToLive()
/*     */   {
/* 422 */     return (int)((this.timeout - SystemTime.getCurrentTime()) / 1000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setStats(long _uploaded, long _downloaded, long _amount_left)
/*     */   {
/* 431 */     this.uploaded = _uploaded;
/* 432 */     this.downloaded = _downloaded;
/* 433 */     this.amount_left = _amount_left;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 439 */     return this.uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 445 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAmountLeft()
/*     */   {
/* 451 */     return this.amount_left;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSeed()
/*     */   {
/* 457 */     return this.amount_left == 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/* 465 */     if (this.user_data == null)
/*     */     {
/* 467 */       this.user_data = new Object[] { key, data };
/*     */     }
/* 469 */     else if ((this.user_data instanceof Object[]))
/*     */     {
/* 471 */       Object[] x = (Object[])this.user_data;
/*     */       
/* 473 */       if (x[0] == key)
/*     */       {
/* 475 */         x[1] = data;
/*     */       }
/*     */       else
/*     */       {
/* 479 */         HashMap map = new HashMap();
/*     */         
/* 481 */         this.user_data = map;
/*     */         
/* 483 */         map.put(x[0], x[1]);
/*     */         
/* 485 */         map.put(key, data);
/*     */       }
/*     */     }
/*     */     else {
/* 489 */       ((Map)this.user_data).put(key, data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 497 */     if (this.user_data == null)
/*     */     {
/* 499 */       return null;
/*     */     }
/* 501 */     if ((this.user_data instanceof Object[]))
/*     */     {
/* 503 */       Object[] x = (Object[])this.user_data;
/*     */       
/* 505 */       if (x[0] == key)
/*     */       {
/* 507 */         return x[1];
/*     */       }
/*     */       
/*     */ 
/* 511 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 515 */     return ((Map)this.user_data).get(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map export()
/*     */   {
/* 522 */     Map map = new HashMap();
/*     */     
/* 524 */     map.put("peer_id", this.peer_id.getBytes());
/* 525 */     map.put("key_hash_code", new Long(this.key_hash_code));
/* 526 */     map.put("ip", this.ip);
/* 527 */     map.put("ip_override", new Long(this.ip_override ? 1L : 0L));
/* 528 */     map.put("tcp_port", new Long(this.tcp_port));
/* 529 */     map.put("udp_port", new Long(this.udp_port));
/* 530 */     map.put("http_port", new Long(this.http_port));
/* 531 */     map.put("crypto_level", new Long(this.crypto_level));
/* 532 */     map.put("az_ver", new Long(this.az_ver));
/* 533 */     map.put("ip_str", this.ip_str);
/* 534 */     if (this.ip_bytes != null) {
/* 535 */       map.put("ip_bytes", this.ip_bytes);
/*     */     }
/* 537 */     map.put("NAT_status", new Long(this.NAT_status));
/* 538 */     map.put("timeout", new Long(this.timeout));
/* 539 */     map.put("uploaded", new Long(this.uploaded));
/* 540 */     map.put("downloaded", new Long(this.downloaded));
/* 541 */     map.put("amount_left", new Long(this.amount_left));
/* 542 */     map.put("last_contact_time", new Long(this.last_contact_time));
/* 543 */     map.put("download_completed", new Long(this.download_completed ? 1L : 0L));
/* 544 */     map.put("biased", new Long(this.biased ? 1L : 0L));
/* 545 */     map.put("up_speed", new Long(this.up_speed));
/*     */     
/* 547 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   public static TRTrackerServerPeerImpl importPeer(Map map)
/*     */   {
/*     */     try
/*     */     {
/* 555 */       HashWrapper peer_id = new HashWrapper((byte[])map.get("peer_id"));
/* 556 */       int key_hash_code = ((Long)map.get("key_hash_code")).intValue();
/* 557 */       byte[] ip = (byte[])map.get("ip");
/* 558 */       boolean ip_override = ((Long)map.get("ip_override")).intValue() == 1;
/* 559 */       short tcp_port = ((Long)map.get("tcp_port")).shortValue();
/* 560 */       short udp_port = ((Long)map.get("udp_port")).shortValue();
/* 561 */       short http_port = ((Long)map.get("http_port")).shortValue();
/* 562 */       byte crypto_level = ((Long)map.get("crypto_level")).byteValue();
/* 563 */       byte az_ver = ((Long)map.get("az_ver")).byteValue();
/* 564 */       String ip_str = new String((byte[])map.get("ip_str"));
/* 565 */       byte[] ip_bytes = (byte[])map.get("ip_bytes");
/* 566 */       byte NAT_status = ((Long)map.get("NAT_status")).byteValue();
/* 567 */       long timeout = ((Long)map.get("timeout")).longValue();
/* 568 */       long uploaded = ((Long)map.get("uploaded")).longValue();
/* 569 */       long downloaded = ((Long)map.get("downloaded")).longValue();
/* 570 */       long amount_left = ((Long)map.get("amount_left")).longValue();
/* 571 */       long last_contact_time = ((Long)map.get("last_contact_time")).longValue();
/* 572 */       boolean download_completed = ((Long)map.get("download_completed")).intValue() == 1;
/* 573 */       boolean biased = ((Long)map.get("biased")).intValue() == 1;
/* 574 */       short up_speed = ((Long)map.get("up_speed")).shortValue();
/*     */       
/* 576 */       return new TRTrackerServerPeerImpl(peer_id, key_hash_code, ip, ip_override, tcp_port, udp_port, http_port, crypto_level, az_ver, ip_str, ip_bytes, NAT_status, timeout, uploaded, downloaded, amount_left, last_contact_time, download_completed, biased, up_speed);
/*     */     }
/*     */     catch (Throwable e) {}
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
/* 601 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 608 */     return new String(this.ip) + ":" + getTCPPort() + "(" + new String(this.peer_id.getHash()) + ")";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerPeerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */