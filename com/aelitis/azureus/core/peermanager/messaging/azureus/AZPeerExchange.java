/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class AZPeerExchange
/*     */   implements AZMessage, AZStylePeerExchange
/*     */ {
/*  39 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final byte bss = 11;
/*     */   
/*  43 */   private DirectByteBuffer buffer = null;
/*  44 */   private String description = null;
/*     */   
/*     */   private final byte version;
/*     */   
/*     */   private final byte[] infohash;
/*     */   private final PeerItem[] peers_added;
/*     */   private final PeerItem[] peers_dropped;
/*     */   
/*     */   public AZPeerExchange(byte[] _infohash, PeerItem[] _peers_added, PeerItem[] _peers_dropped, byte version)
/*     */   {
/*  54 */     this.infohash = _infohash;
/*  55 */     this.peers_added = _peers_added;
/*  56 */     this.peers_dropped = _peers_dropped;
/*  57 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */   private void insertPeers(String key_name, Map root_map, PeerItem[] peers)
/*     */   {
/*  63 */     if ((peers != null) && (peers.length > 0)) {
/*  64 */       ArrayList raw_peers = new ArrayList();
/*  65 */       byte[] handshake_types = new byte[peers.length];
/*  66 */       byte[] udp_ports = new byte[peers.length * 2];
/*  67 */       int num_valid_udp = 0;
/*     */       
/*  69 */       for (int i = 0; i < peers.length; i++) {
/*  70 */         raw_peers.add(peers[i].getSerialization());
/*  71 */         handshake_types[i] = peers[i].getHandshakeType();
/*  72 */         int udp_port = peers[i].getUDPPort();
/*  73 */         if (udp_port > 0) {
/*  74 */           num_valid_udp++;
/*  75 */           udp_ports[(i * 2)] = ((byte)(udp_port >> 8));
/*  76 */           udp_ports[(i * 2 + 1)] = ((byte)udp_port);
/*     */         }
/*     */       }
/*     */       
/*  80 */       root_map.put(key_name, raw_peers);
/*  81 */       root_map.put(key_name + "_HST", handshake_types);
/*  82 */       if (num_valid_udp > 0) {
/*  83 */         root_map.put(key_name + "_UDP", udp_ports);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private PeerItem[] extractPeers(String key_name, Map root_map)
/*     */   {
/*  91 */     PeerItem[] return_peers = null;
/*  92 */     ArrayList peers = new ArrayList();
/*     */     
/*  94 */     List raw_peers = (List)root_map.get(key_name);
/*  95 */     byte[] handshake_types; byte[] udp_ports; int pos; Iterator it; if (raw_peers != null) {
/*  96 */       int peer_num = raw_peers.size();
/*  97 */       handshake_types = (byte[])root_map.get(key_name + "_HST");
/*  98 */       udp_ports = (byte[])root_map.get(key_name + "_UDP");
/*  99 */       pos = 0;
/*     */       
/* 101 */       if ((handshake_types != null) && (handshake_types.length != peer_num)) {
/* 102 */         Logger.log(new LogEvent(LOGID, 1, "PEX: invalid handshake types received: peers=" + peer_num + ",handshakes=" + handshake_types.length));
/* 103 */         handshake_types = null;
/*     */       }
/*     */       
/* 106 */       if ((udp_ports != null) && (udp_ports.length != peer_num * 2)) {
/* 107 */         Logger.log(new LogEvent(LOGID, 1, "PEX: invalid udp ports received: peers=" + peer_num + ",udp_ports=" + udp_ports.length));
/* 108 */         udp_ports = null;
/*     */       }
/*     */       
/* 111 */       for (it = raw_peers.iterator(); it.hasNext();) {
/* 112 */         byte[] full_address = (byte[])it.next();
/*     */         
/* 114 */         byte type = 0;
/* 115 */         if (handshake_types != null) {
/* 116 */           type = handshake_types[pos];
/*     */         }
/* 118 */         int udp_port = 0;
/* 119 */         if (udp_ports != null) {
/* 120 */           udp_port = (udp_ports[(pos * 2)] << 8 & 0xFF00) + (udp_ports[(pos * 2 + 1)] & 0xFF);
/*     */         }
/*     */         try {
/* 123 */           PeerItem peer = PeerItemFactory.createPeerItem(full_address, (byte)2, type, udp_port, "Public");
/* 124 */           peers.add(peer);
/*     */         } catch (Exception t) {
/* 126 */           Logger.log(new LogEvent(LOGID, 1, "PEX: invalid peer received"));
/*     */         }
/* 128 */         pos++;
/*     */       }
/*     */     }
/*     */     
/* 132 */     if (!peers.isEmpty()) {
/* 133 */       return_peers = new PeerItem[peers.size()];
/* 134 */       peers.toArray(return_peers);
/*     */     }
/*     */     
/* 137 */     return return_peers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */   public byte[] getInfoHash() { return this.infohash; }
/*     */   
/* 146 */   public PeerItem[] getAddedPeers() { return this.peers_added; }
/*     */   
/* 148 */   public PeerItem[] getDroppedPeers() { return this.peers_dropped; }
/*     */   
/*     */ 
/*     */ 
/* 152 */   public String getID() { return "AZ_PEER_EXCHANGE"; }
/* 153 */   public byte[] getIDBytes() { return AZMessage.ID_AZ_PEER_EXCHANGE_BYTES; }
/*     */   
/* 155 */   public String getFeatureID() { return "AZ1"; }
/*     */   
/* 157 */   public int getFeatureSubID() { return 1; }
/*     */   
/* 159 */   public int getType() { return 0; }
/*     */   
/* 161 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/* 164 */     if (this.description == null) {
/* 165 */       int add_count = this.peers_added == null ? 0 : this.peers_added.length;
/* 166 */       int drop_count = this.peers_dropped == null ? 0 : this.peers_dropped.length;
/*     */       
/* 168 */       this.description = (getID() + " for infohash " + ByteFormatter.nicePrint(this.infohash, true) + " with " + add_count + " added and " + drop_count + " dropped peers");
/*     */     }
/*     */     
/* 171 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 176 */     if (this.buffer == null) {
/* 177 */       Map payload_map = new HashMap();
/*     */       
/* 179 */       payload_map.put("infohash", this.infohash);
/* 180 */       insertPeers("added", payload_map, this.peers_added);
/* 181 */       insertPeers("dropped", payload_map, this.peers_dropped);
/*     */       
/* 183 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(payload_map, (byte)14);
/*     */       
/* 185 */       if (this.buffer.remaining((byte)11) > 2000) { System.out.println("Generated AZPeerExchange size = " + this.buffer.remaining((byte)11) + " bytes");
/*     */       }
/*     */     }
/* 188 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/* 193 */     if (data.remaining((byte)11) > 2000) { System.out.println("Received PEX msg byte size = " + data.remaining((byte)11));
/*     */     }
/* 195 */     Map root = MessagingUtil.convertBencodedByteStreamToPayload(data, 10, getID());
/*     */     
/* 197 */     byte[] hash = (byte[])root.get("infohash");
/* 198 */     if (hash == null) throw new MessageException("hash == null");
/* 199 */     if (hash.length != 20) { throw new MessageException("hash.length != 20: " + hash.length);
/*     */     }
/* 201 */     PeerItem[] added = extractPeers("added", root);
/* 202 */     PeerItem[] dropped = extractPeers("dropped", root);
/*     */     
/* 204 */     if ((added == null) && (dropped == null)) { throw new MessageException("[" + getID() + "] received exchange message without any adds or drops");
/*     */     }
/* 206 */     return new AZPeerExchange(hash, added, dropped, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 211 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */   
/*     */   public int getMaxAllowedPeersPerVolley(boolean initial, boolean added) {
/* 215 */     return 50;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZPeerExchange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */