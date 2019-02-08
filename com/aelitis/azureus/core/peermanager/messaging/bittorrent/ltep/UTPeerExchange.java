/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZStylePeerExchange;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UTPeerExchange
/*     */   implements AZStylePeerExchange, LTMessage
/*     */ {
/*  44 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final int IPv4_SIZE_WITH_PORT = 6;
/*     */   
/*     */   private static final int IPv6_SIZE_WITH_PORT = 18;
/*  49 */   private DirectByteBuffer buffer = null;
/*  50 */   private String description = null;
/*     */   private final byte version;
/*     */   private final PeerItem[] peers_added;
/*     */   private final PeerItem[] peersAddedNoSeeds;
/*     */   private final PeerItem[] peers_dropped;
/*     */   
/*     */   public UTPeerExchange(PeerItem[] _peers_added, PeerItem[] _peers_dropped, PeerItem[] peersAddedNoSeeds, byte version)
/*     */   {
/*  58 */     this.peers_added = _peers_added;
/*  59 */     this.peers_dropped = _peers_dropped;
/*  60 */     this.version = version;
/*  61 */     this.peersAddedNoSeeds = (peersAddedNoSeeds != null ? peersAddedNoSeeds : _peers_added);
/*     */   }
/*     */   
/*     */   private void insertPeers(String key_name, Map root_map, boolean include_flags, PeerItem[] peers) {
/*  65 */     if (peers == null) return;
/*  66 */     if (peers.length == 0) { return;
/*     */     }
/*  68 */     List v4_peers = null;
/*  69 */     List v6_peers = null;
/*  70 */     for (int i = 0; i < peers.length; i++) {
/*  71 */       if (!peers[i].isIPv4()) {
/*  72 */         if (v6_peers == null) {
/*  73 */           v6_peers = new ArrayList();
/*  74 */           v4_peers = new ArrayList(Arrays.asList(peers).subList(0, i));
/*     */         }
/*  76 */         v6_peers.add(peers[i]);
/*     */ 
/*     */       }
/*  79 */       else if (v4_peers != null) {
/*  80 */         v4_peers.add(peers[i]);
/*     */       }
/*     */     }
/*     */     
/*  84 */     if (v4_peers == null) { v4_peers = Arrays.asList(peers);
/*     */     }
/*  86 */     insertPeers(key_name, root_map, include_flags, v4_peers, 6);
/*  87 */     insertPeers(key_name + "6", root_map, include_flags, v6_peers, 18);
/*     */   }
/*     */   
/*     */   private void insertPeers(String key_name, Map root_map, boolean include_flags, List peers, int peer_byte_size) {
/*  91 */     if (peers == null) return;
/*  92 */     if (peers.isEmpty()) { return;
/*     */     }
/*  94 */     byte[] raw_peers = new byte[peers.size() * peer_byte_size];
/*  95 */     byte[] peer_flags = include_flags ? new byte[peers.size()] : null;
/*     */     
/*     */ 
/*  98 */     for (int i = 0; i < peers.size(); i++) {
/*  99 */       PeerItem peer = (PeerItem)peers.get(i);
/* 100 */       byte[] serialised_peer = peer.getSerialization();
/* 101 */       if (serialised_peer.length != peer_byte_size) {
/* 102 */         Debug.out("invalid serialization- " + serialised_peer.length + ":" + peer_byte_size);
/*     */       }
/* 104 */       System.arraycopy(serialised_peer, 0, raw_peers, i * peer_byte_size, peer_byte_size);
/* 105 */       if ((peer_flags != null) && (NetworkManager.getCryptoRequired(peer.getCryptoLevel()))) {
/* 106 */         int tmp163_161 = i; byte[] tmp163_159 = peer_flags;tmp163_159[tmp163_161] = ((byte)(tmp163_159[tmp163_161] | 0x1));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 112 */     root_map.put(key_name, raw_peers);
/* 113 */     if (peer_flags != null) {
/* 114 */       root_map.put(key_name + ".f", peer_flags);
/*     */     }
/*     */   }
/*     */   
/*     */   private List extractPeers(String key_name, Map root_map, int peer_byte_size, boolean noSeeds) {
/* 119 */     ArrayList peers = new ArrayList();
/*     */     
/* 121 */     byte[] raw_peer_data = (byte[])root_map.get(key_name);
/* 122 */     if (raw_peer_data != null) {
/* 123 */       if ((raw_peer_data.length % peer_byte_size != 0) && 
/* 124 */         (Logger.isEnabled())) {
/* 125 */         Logger.log(new LogEvent(LOGID, 1, "PEX (UT): peer data size not multiple of " + peer_byte_size + ": " + raw_peer_data.length));
/*     */       }
/* 127 */       int peer_num = raw_peer_data.length / peer_byte_size;
/*     */       
/* 129 */       byte[] flags = null;
/*     */       
/* 131 */       Object flags_obj = root_map.get(key_name + ".f");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 138 */       if ((flags_obj instanceof byte[])) { flags = (byte[])flags_obj;
/*     */       }
/*     */       
/* 141 */       if ((flags != null) && (flags.length != peer_num)) {
/* 142 */         if ((flags.length > 0) && 
/* 143 */           (Logger.isEnabled())) {
/* 144 */           Logger.log(new LogEvent(LOGID, 1, "PEX (UT): invalid peer flags: peers=" + peer_num + ", flags=" + flags.length));
/*     */         }
/*     */         
/* 147 */         flags = null;
/*     */       }
/*     */       
/* 150 */       for (int i = 0; i < peer_num; i++) {
/* 151 */         byte[] full_address = new byte[peer_byte_size];
/* 152 */         System.arraycopy(raw_peer_data, i * peer_byte_size, full_address, 0, peer_byte_size);
/* 153 */         byte type = 0;
/* 154 */         if ((flags != null) && ((flags[i] & 0x1) != 0))
/* 155 */           type = 1;
/* 156 */         if ((flags == null) || ((flags[i] & 0x2) == 0) || (!noSeeds))
/*     */         {
/*     */           try
/*     */           {
/* 160 */             PeerItem peer = PeerItemFactory.createPeerItem(full_address, (byte)2, type, 0, "Public");
/* 161 */             peers.add(peer);
/*     */           }
/*     */           catch (Exception e) {
/* 164 */             if (Logger.isEnabled())
/* 165 */               Logger.log(new LogEvent(LOGID, 1, "PEX (UT): invalid peer received"));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 170 */     return peers;
/*     */   }
/*     */   
/* 173 */   public PeerItem[] getAddedPeers(boolean seeds) { return seeds ? this.peers_added : this.peersAddedNoSeeds; }
/* 174 */   public PeerItem[] getAddedPeers() { return this.peers_added; }
/* 175 */   public PeerItem[] getDroppedPeers() { return this.peers_dropped; }
/* 176 */   public String getID() { return "ut_pex"; }
/* 177 */   public byte[] getIDBytes() { return LTMessage.ID_UT_PEX_BYTES; }
/* 178 */   public String getFeatureID() { return "LT1"; }
/* 179 */   public int getFeatureSubID() { return 1; }
/* 180 */   public int getType() { return 0; }
/* 181 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/* 184 */     if (this.description == null) {
/* 185 */       int add_count = this.peers_added == null ? 0 : this.peers_added.length;
/* 186 */       int drop_count = this.peers_dropped == null ? 0 : this.peers_dropped.length;
/*     */       
/* 188 */       this.description = (getID().toUpperCase() + " with " + add_count + " added and " + drop_count + " dropped peers");
/*     */     }
/*     */     
/* 191 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 196 */     if (this.buffer == null) {
/* 197 */       Map payload_map = new HashMap();
/*     */       
/* 199 */       insertPeers("added", payload_map, true, this.peers_added);
/* 200 */       insertPeers("dropped", payload_map, false, this.peers_dropped);
/* 201 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(payload_map, (byte)29);
/*     */     }
/*     */     
/* 204 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/* 209 */     Map root = MessagingUtil.convertBencodedByteStreamToPayload(data, 2, getID());
/* 210 */     List added = extractPeers("added", root, 6, false);
/* 211 */     List addedNoSeeds = extractPeers("added", root, 6, true);
/* 212 */     List dropped = extractPeers("dropped", root, 6, false);
/*     */     
/* 214 */     added.addAll(extractPeers("added6", root, 18, false));
/* 215 */     addedNoSeeds.addAll(extractPeers("added6", root, 18, true));
/* 216 */     dropped.addAll(extractPeers("dropped6", root, 18, false));
/*     */     
/* 218 */     PeerItem[] addedArr = (PeerItem[])added.toArray(new PeerItem[added.size()]);
/* 219 */     PeerItem[] addedNoSeedsArr = (PeerItem[])addedNoSeeds.toArray(new PeerItem[addedNoSeeds.size()]);
/* 220 */     PeerItem[] droppedArr = (PeerItem[])dropped.toArray(new PeerItem[dropped.size()]);
/*     */     
/* 222 */     return new UTPeerExchange(addedArr, droppedArr, addedNoSeedsArr, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 227 */     if (this.buffer != null) { this.buffer.returnToPool();
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
/*     */   public int getMaxAllowedPeersPerVolley(boolean initial, boolean added)
/*     */   {
/* 252 */     return (initial) && (added) ? 500 : 250;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/UTPeerExchange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */