/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ public class AZHandshake
/*     */   implements AZMessage
/*     */ {
/*     */   public static final int HANDSHAKE_TYPE_PLAIN = 0;
/*     */   public static final int HANDSHAKE_TYPE_CRYPTO = 1;
/*     */   private static final byte bss = 11;
/*     */   private final byte version;
/*  50 */   private DirectByteBuffer buffer = null;
/*  51 */   private String description = null;
/*     */   
/*     */ 
/*     */   private final byte[] identity;
/*     */   
/*     */   private final HashWrapper sessionID;
/*     */   
/*     */   private final HashWrapper reconnectID;
/*     */   
/*     */   private final String client;
/*     */   
/*     */   private final String client_version;
/*     */   
/*     */   private final String[] avail_ids;
/*     */   
/*     */   private final byte[] avail_versions;
/*     */   
/*     */   private int tcp_port;
/*     */   
/*     */   private int udp_port;
/*     */   
/*     */   private int udp_non_data_port;
/*     */   
/*     */   private final int handshake_type;
/*     */   
/*     */   private final boolean uploadOnly;
/*     */   
/*     */   private final InetAddress ipv6;
/*     */   
/*     */   private final int md_size;
/*     */   
/*     */ 
/*     */   public AZHandshake(byte[] peer_identity, HashWrapper sessionID, HashWrapper reconnectID, String _client, String version, int tcp_listen_port, int udp_listen_port, int udp_non_data_listen_port, InetAddress ipv6addr, int md_size, String[] avail_msg_ids, byte[] avail_msg_versions, int _handshake_type, byte _version, boolean uploadOnly)
/*     */   {
/*  85 */     this.identity = peer_identity;
/*  86 */     this.sessionID = sessionID;
/*  87 */     this.reconnectID = reconnectID;
/*  88 */     this.client = _client;
/*  89 */     this.client_version = version;
/*  90 */     this.avail_ids = avail_msg_ids;
/*  91 */     this.avail_versions = avail_msg_versions;
/*  92 */     this.tcp_port = tcp_listen_port;
/*  93 */     this.udp_port = udp_listen_port;
/*  94 */     this.udp_non_data_port = udp_non_data_listen_port;
/*  95 */     this.handshake_type = _handshake_type;
/*  96 */     this.version = _version;
/*  97 */     this.uploadOnly = uploadOnly;
/*  98 */     this.ipv6 = ipv6addr;
/*  99 */     this.md_size = md_size;
/*     */     
/*     */ 
/* 102 */     if ((this.tcp_port < 0) || (this.tcp_port > 65535)) {
/* 103 */       Debug.out("given TCP listen port is invalid: " + this.tcp_port);
/* 104 */       this.tcp_port = 0;
/*     */     }
/*     */     
/* 107 */     if ((this.udp_port < 0) || (this.udp_port > 65535)) {
/* 108 */       Debug.out("given UDP listen port is invalid: " + this.udp_port);
/* 109 */       this.udp_port = 0;
/*     */     }
/*     */     
/* 112 */     if ((this.udp_non_data_port < 0) || (this.udp_non_data_port > 65535)) {
/* 113 */       Debug.out("given UDP non-data listen port is invalid: " + this.udp_non_data_port);
/* 114 */       this.udp_non_data_port = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 120 */   public byte[] getIdentity() { return this.identity; }
/* 121 */   public HashWrapper getRemoteSessionID() { return this.sessionID; }
/* 122 */   public HashWrapper getReconnectSessionID() { return this.reconnectID; }
/* 123 */   public boolean isUploadOnly() { return this.uploadOnly; }
/*     */   
/*     */ 
/* 126 */   public String getClient() { return this.client; }
/*     */   
/* 128 */   public String getClientVersion() { return this.client_version; }
/*     */   
/* 130 */   public String[] getMessageIDs() { return this.avail_ids; }
/*     */   
/* 132 */   public byte[] getMessageVersions() { return this.avail_versions; }
/*     */   
/* 134 */   public int getTCPListenPort() { return this.tcp_port; }
/* 135 */   public int getUDPListenPort() { return this.udp_port; }
/* 136 */   public int getUDPNonDataListenPort() { return this.udp_non_data_port; }
/* 137 */   public InetAddress getIPv6() { return this.ipv6; }
/* 138 */   public int getMetadataSize() { return this.md_size; }
/* 139 */   public int getHandshakeType() { return this.handshake_type; }
/*     */   
/*     */ 
/* 142 */   public String getID() { return "AZ_HANDSHAKE"; }
/* 143 */   public byte[] getIDBytes() { return AZMessage.ID_AZ_HANDSHAKE_BYTES; }
/*     */   
/* 145 */   public String getFeatureID() { return "AZ1"; }
/*     */   
/* 147 */   public int getFeatureSubID() { return 0; }
/*     */   
/*     */ 
/* 150 */   public int getType() { return 0; }
/*     */   
/* 152 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/* 155 */     if (this.description == null) {
/* 156 */       String msgs_desc = "";
/* 157 */       for (int i = 0; i < this.avail_ids.length; i++) {
/* 158 */         String id = this.avail_ids[i];
/* 159 */         byte ver = this.avail_versions[i];
/* 160 */         if (!id.equals(getID()))
/* 161 */           msgs_desc = msgs_desc + "[" + id + ":" + ver + "]";
/*     */       }
/* 163 */       this.description = (getID() + " from [" + ByteFormatter.nicePrint(this.identity, true) + ", " + this.client + " " + this.client_version + ", TCP/UDP ports " + this.tcp_port + "/" + this.udp_port + "/" + this.udp_non_data_port + ", handshake " + (getHandshakeType() == 0 ? "plain" : "crypto") + ", upload_only = " + (isUploadOnly() ? "1" : "0") + (this.ipv6 != null ? ", ipv6 = " + this.ipv6.getHostAddress() : "") + ", md_size=" + this.md_size + (this.sessionID != null ? ", sessionID: " + this.sessionID.toBase32String() : "") + (this.reconnectID != null ? ", reconnect request: " + this.reconnectID.toBase32String() : "") + "] supports " + msgs_desc);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 174 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData() {
/* 178 */     if (this.buffer == null)
/*     */     {
/* 180 */       Map payload_map = new HashMap();
/*     */       
/* 182 */       payload_map.put("identity", this.identity);
/* 183 */       if (this.sessionID != null)
/* 184 */         payload_map.put("session", this.sessionID.getBytes());
/* 185 */       if (this.reconnectID != null)
/* 186 */         payload_map.put("reconn", this.reconnectID.getBytes());
/* 187 */       payload_map.put("client", this.client);
/* 188 */       payload_map.put("version", this.client_version);
/* 189 */       payload_map.put("tcp_port", new Long(this.tcp_port));
/* 190 */       payload_map.put("udp_port", new Long(this.udp_port));
/* 191 */       payload_map.put("udp2_port", new Long(this.udp_non_data_port));
/* 192 */       payload_map.put("handshake_type", new Long(this.handshake_type));
/* 193 */       payload_map.put("upload_only", new Long(this.uploadOnly ? 1L : 0L));
/* 194 */       if (this.ipv6 != null)
/* 195 */         payload_map.put("ipv6", this.ipv6.getAddress());
/* 196 */       if (this.md_size > 0) {
/* 197 */         payload_map.put("mds", new Long(this.md_size));
/*     */       }
/*     */       
/* 200 */       List message_list = new ArrayList();
/* 201 */       for (int i = 0; i < this.avail_ids.length; i++)
/*     */       {
/* 203 */         String id = this.avail_ids[i];
/* 204 */         byte ver = this.avail_versions[i];
/* 205 */         if (!id.equals(getID()))
/*     */         {
/* 207 */           Map msg = new HashMap();
/* 208 */           msg.put("id", id);
/* 209 */           msg.put("ver", new byte[] { ver });
/* 210 */           message_list.add(msg);
/*     */         }
/*     */       }
/* 213 */       payload_map.put("messages", message_list);
/*     */       
/*     */ 
/* 216 */       if (this.handshake_type == 1) {
/* 217 */         payload_map.put("pad", new byte[RandomUtils.nextInt(64)]);
/*     */       }
/* 219 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(payload_map, (byte)13);
/* 220 */       if (this.buffer.remaining((byte)11) > 1200) {
/* 221 */         System.out.println("Generated AZHandshake size = " + this.buffer.remaining((byte)11) + " bytes");
/*     */       }
/*     */     }
/* 224 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/* 229 */     Map root = MessagingUtil.convertBencodedByteStreamToPayload(data, 100, getID());
/*     */     
/* 231 */     byte[] id = (byte[])root.get("identity");
/* 232 */     if (id == null) throw new MessageException("id == null");
/* 233 */     if (id.length != 20) { throw new MessageException("id.length != 20: " + id.length);
/*     */     }
/* 235 */     byte[] session = (byte[])root.get("session");
/* 236 */     byte[] reconnect = (byte[])root.get("reconn");
/*     */     
/* 238 */     byte[] raw_name = (byte[])root.get("client");
/* 239 */     if (raw_name == null) throw new MessageException("raw_name == null");
/* 240 */     String name = new String(raw_name);
/*     */     
/* 242 */     byte[] raw_ver = (byte[])root.get("version");
/* 243 */     if (raw_ver == null) throw new MessageException("raw_ver == null");
/* 244 */     String client_version = new String(raw_ver);
/*     */     
/* 246 */     Long tcp_lport = (Long)root.get("tcp_port");
/* 247 */     if (tcp_lport == null) {
/* 248 */       tcp_lport = new Long(0L);
/*     */     }
/*     */     
/* 251 */     Long udp_lport = (Long)root.get("udp_port");
/* 252 */     if (udp_lport == null) {
/* 253 */       udp_lport = new Long(0L);
/*     */     }
/*     */     
/* 256 */     Long udp2_lport = (Long)root.get("udp2_port");
/* 257 */     if (udp2_lport == null) {
/* 258 */       udp2_lport = udp_lport;
/*     */     }
/*     */     
/* 261 */     Long h_type = (Long)root.get("handshake_type");
/* 262 */     if (h_type == null) {
/* 263 */       h_type = new Long(0L);
/*     */     }
/*     */     
/* 266 */     InetAddress ipv6 = null;
/* 267 */     if ((root.get("ipv6") instanceof byte[]))
/*     */     {
/*     */       try
/*     */       {
/* 271 */         InetAddress.getByAddress((byte[])root.get("ipv6"));
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/* 277 */     int md_size = 0;
/* 278 */     Long mds = (Long)root.get("mds");
/* 279 */     if (mds != null) {
/* 280 */       md_size = mds.intValue();
/*     */     }
/* 282 */     List raw_msgs = (List)root.get("messages");
/* 283 */     if (raw_msgs == null) { throw new MessageException("raw_msgs == null");
/*     */     }
/* 285 */     String[] ids = new String[raw_msgs.size()];
/* 286 */     byte[] vers = new byte[raw_msgs.size()];
/*     */     
/* 288 */     int pos = 0;
/*     */     
/* 290 */     for (Iterator i = raw_msgs.iterator(); i.hasNext();) {
/* 291 */       Map msg = (Map)i.next();
/*     */       
/* 293 */       byte[] mid = (byte[])msg.get("id");
/* 294 */       if (mid == null) throw new MessageException("mid == null");
/* 295 */       ids[pos] = new String(mid);
/*     */       
/* 297 */       byte[] ver = (byte[])msg.get("ver");
/* 298 */       if (ver == null) { throw new MessageException("ver == null");
/*     */       }
/* 300 */       if (ver.length != 1) throw new MessageException("ver.length != 1");
/* 301 */       vers[pos] = ver[0];
/*     */       
/* 303 */       pos++;
/*     */     }
/*     */     
/* 306 */     Long ulOnly = (Long)root.get("upload_only");
/* 307 */     boolean uploadOnly = (ulOnly != null) && (ulOnly.longValue() > 0L);
/*     */     
/* 309 */     if (name.equals("Azureus")) {
/* 310 */       name = "Vuze";
/*     */     }
/* 312 */     return new AZHandshake(id, session == null ? null : new HashWrapper(session), reconnect == null ? null : new HashWrapper(reconnect), name, client_version, tcp_lport.intValue(), udp_lport.intValue(), udp2_lport.intValue(), ipv6, md_size, ids, vers, h_type.intValue(), version, uploadOnly);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 317 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZHandshake.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */