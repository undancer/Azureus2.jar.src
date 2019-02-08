/*     */ package com.aelitis.azureus.core.peermanager.peerdb;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.plugins.peers.PeerDescriptor;
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
/*     */ public class PeerItem
/*     */   implements PeerDescriptor
/*     */ {
/*     */   private final byte[] address;
/*     */   private final short udp_port;
/*     */   private final short tcp_port;
/*     */   private final byte source;
/*     */   private final int hashcode;
/*     */   private final byte handshake;
/*     */   private final byte crypto_level;
/*     */   private final short up_speed;
/*     */   private final int priority;
/*     */   private final String network;
/*     */   
/*     */   protected PeerItem(String _address, int _tcp_port, byte _source, byte _handshake, int _udp_port, byte _crypto_level, int _up_speed)
/*     */   {
/*  53 */     this.network = AENetworkClassifier.categoriseAddress(_address);
/*     */     byte[] raw;
/*  55 */     try { if (this.network == "Public") {
/*     */         try
/*     */         {
/*  58 */           InetAddress ip = InetAddress.getByName(_address);
/*  59 */           raw = ip.getAddress();
/*     */         }
/*     */         catch (UnknownHostException e)
/*     */         {
/*  63 */           byte[] raw = _address.getBytes("ISO8859-1");
/*     */         }
/*     */       } else {
/*  66 */         raw = _address.getBytes("ISO8859-1");
/*     */       }
/*     */     } catch (UnsupportedEncodingException e) {
/*  69 */       raw = _address.getBytes();
/*     */     }
/*     */     
/*  72 */     this.address = raw;
/*  73 */     this.tcp_port = ((short)_tcp_port);
/*  74 */     this.udp_port = ((short)_udp_port);
/*  75 */     this.source = _source;
/*  76 */     this.hashcode = (new String(this.address).hashCode() + this.tcp_port);
/*  77 */     this.handshake = _handshake;
/*  78 */     this.crypto_level = _crypto_level;
/*  79 */     this.up_speed = ((short)_up_speed);
/*     */     
/*  81 */     this.priority = PeerUtils.getPeerPriority(this.address, this.tcp_port);
/*     */   }
/*     */   
/*     */   protected PeerItem(byte[] _serialization, byte _source, byte _handshake, int _udp_port, String _network) throws Exception
/*     */   {
/*  86 */     if ((_serialization.length < 6) || (_serialization.length > 32)) {
/*  87 */       throw new Exception("PeerItem: invalid serialisation length - " + _serialization.length);
/*     */     }
/*     */     
/*  90 */     this.address = new byte[_serialization.length - 2];
/*  91 */     System.arraycopy(_serialization, 0, this.address, 0, _serialization.length - 2);
/*     */     
/*  93 */     byte p0 = _serialization[(_serialization.length - 2)];
/*  94 */     byte p1 = _serialization[(_serialization.length - 1)];
/*  95 */     this.tcp_port = ((short)((p1 & 0xFF) + ((p0 & 0xFF) << 8)));
/*     */     
/*  97 */     this.source = _source;
/*  98 */     this.hashcode = (new String(this.address).hashCode() + this.tcp_port);
/*  99 */     this.handshake = _handshake;
/* 100 */     this.udp_port = ((short)_udp_port);
/* 101 */     this.crypto_level = 1;
/* 102 */     this.up_speed = 0;
/*     */     
/* 104 */     this.priority = PeerUtils.getPeerPriority(this.address, this.tcp_port);
/*     */     
/* 106 */     this.network = _network;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getSerialization()
/*     */   {
/* 113 */     byte[] full_address = new byte[this.address.length + 2];
/* 114 */     System.arraycopy(this.address, 0, full_address, 0, this.address.length);
/* 115 */     full_address[this.address.length] = ((byte)(this.tcp_port >> 8));
/* 116 */     full_address[(this.address.length + 1)] = ((byte)(this.tcp_port & 0xFF));
/* 117 */     return full_address;
/*     */   }
/*     */   
/*     */   public String getAddressString()
/*     */   {
/*     */     try {
/* 123 */       if (this.network == "Public") {
/*     */         try
/*     */         {
/* 126 */           return InetAddress.getByAddress(this.address).getHostAddress();
/*     */         }
/*     */         catch (UnknownHostException e)
/*     */         {
/* 130 */           return new String(this.address, "ISO8859-1");
/*     */         }
/*     */       }
/* 133 */       return new String(this.address, "ISO8859-1");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {}
/* 136 */     return new String(this.address);
/*     */   }
/*     */   
/*     */ 
/* 140 */   public String getIP() { return getAddressString(); }
/*     */   
/* 142 */   public String getNetwork() { return this.network; }
/*     */   
/* 144 */   public int getTCPPort() { return this.tcp_port & 0xFFFF; }
/*     */   
/* 146 */   public int getUDPPort() { return this.udp_port & 0xFFFF; }
/*     */   
/* 148 */   public byte getSource() { return this.source; }
/*     */   
/* 150 */   public String getPeerSource() { return convertSourceString(this.source); }
/*     */   
/* 152 */   public byte getHandshakeType() { return this.handshake; }
/*     */   
/* 154 */   public byte getCryptoLevel() { return this.crypto_level; }
/*     */   
/* 156 */   public boolean useCrypto() { return this.crypto_level != 0; }
/*     */   
/*     */   public boolean equals(Object obj) {
/* 159 */     if (this == obj) return true;
/* 160 */     if ((obj != null) && ((obj instanceof PeerItem))) {
/* 161 */       PeerItem other = (PeerItem)obj;
/* 162 */       if ((this.tcp_port == other.tcp_port) && (this.udp_port == other.udp_port) && (this.handshake == other.handshake) && (Arrays.equals(this.address, other.address)))
/*     */       {
/*     */ 
/* 165 */         return true; }
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */   
/* 170 */   public int hashCode() { return this.hashcode; }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(PeerItem other)
/*     */   {
/* 176 */     int res = this.tcp_port - other.tcp_port;
/*     */     
/* 178 */     if (res == 0)
/*     */     {
/* 180 */       res = this.udp_port - other.udp_port;
/*     */       
/* 182 */       if (res == 0)
/*     */       {
/* 184 */         res = this.address.length - other.address.length;
/*     */         
/* 186 */         if (res == 0)
/*     */         {
/* 188 */           for (int i = 0; i < this.address.length; i++)
/*     */           {
/* 190 */             res = this.address[i] - other.address[i];
/*     */             
/* 192 */             if (res != 0) {
/*     */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 201 */     return res;
/*     */   }
/*     */   
/* 204 */   public long getPriority() { return this.priority & 0xFFFFFFFF; }
/*     */   
/*     */   public static String convertSourceString(byte source_id)
/*     */   {
/* 208 */     switch (source_id) {
/* 209 */     case 0:  return "Tracker";
/* 210 */     case 1:  return "DHT";
/* 211 */     case 2:  return "PeerExchange";
/* 212 */     case 3:  return "Plugin";
/* 213 */     case 4:  return "Incoming"; }
/* 214 */     return "<unknown>";
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte convertSourceID(String source)
/*     */   {
/* 220 */     if (source.equals("Tracker")) return 0;
/* 221 */     if (source.equals("DHT")) return 1;
/* 222 */     if (source.equals("PeerExchange")) return 2;
/* 223 */     if (source.equals("Plugin")) return 3;
/* 224 */     if (source.equals("Incoming")) return 4;
/* 225 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean isIPv4() {
/* 229 */     return this.address.length == 4;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/peerdb/PeerItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */