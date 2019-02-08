/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.utils.PeerClassifier;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ public class BTHandshake
/*     */   implements BTMessage, RawMessage
/*     */ {
/*     */   public static final String PROTOCOL = "BitTorrent protocol";
/*  36 */   private static final byte[] BT_RESERVED = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */   
/*  38 */   private static final byte[] LT_RESERVED = { 0, 0, 0, 0, 0, 16, 0, 0 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  46 */   private static final byte[] AZ_RESERVED = { Byte.MIN_VALUE, 0, 0, 0, 0, 19, 0, 0 };
/*     */   
/*     */   public static final int BT_RESERVED_MODE = 0;
/*     */   
/*     */   public static final int LT_RESERVED_MODE = 1;
/*     */   public static final int AZ_RESERVED_MODE = 2;
/*  52 */   private static final byte[][] RESERVED = { BT_RESERVED, LT_RESERVED, AZ_RESERVED };
/*     */   public static final boolean FAST_EXTENSION_ENABLED = true;
/*     */   
/*  55 */   public static void setMainlineDHTEnabled(boolean enabled) { if (enabled) {
/*  56 */       LT_RESERVED[7] = ((byte)(LT_RESERVED[7] | 0x1));
/*  57 */       AZ_RESERVED[7] = ((byte)(AZ_RESERVED[7] | 0x1));
/*     */     }
/*     */     else {
/*  60 */       LT_RESERVED[7] = ((byte)(LT_RESERVED[7] & 0xFE));
/*  61 */       AZ_RESERVED[7] = ((byte)(AZ_RESERVED[7] & 0xFE));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setFastExtensionEnabled(boolean enabled)
/*     */   {
/*  68 */     if (enabled) {
/*  69 */       LT_RESERVED[7] = ((byte)(LT_RESERVED[7] | 0x4));
/*  70 */       AZ_RESERVED[7] = ((byte)(AZ_RESERVED[7] | 0x4));
/*     */     }
/*     */     else {
/*  73 */       LT_RESERVED[7] = ((byte)(LT_RESERVED[7] & 0xF3));
/*  74 */       AZ_RESERVED[7] = ((byte)(AZ_RESERVED[7] & 0xF3));
/*     */     }
/*     */   }
/*     */   
/*     */   static {
/*  79 */     setFastExtensionEnabled(true);
/*     */   }
/*     */   
/*  82 */   private DirectByteBuffer buffer = null;
/*  83 */   private String description = null;
/*     */   private final byte[] reserved_bytes;
/*     */   private final byte[] datahash_bytes;
/*     */   private final byte[] peer_id_bytes;
/*     */   private final byte version;
/*     */   
/*     */   private static byte[] duplicate(byte[] b)
/*     */   {
/*  91 */     byte[] r = new byte[b.length];
/*  92 */     System.arraycopy(b, 0, r, 0, b.length);
/*  93 */     return r;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BTHandshake(byte[] data_hash, byte[] peer_id, int reserved_mode, byte version)
/*     */   {
/* 103 */     this(duplicate(RESERVED[reserved_mode]), data_hash, peer_id, version);
/*     */   }
/*     */   
/*     */   private BTHandshake(byte[] reserved, byte[] data_hash, byte[] peer_id, byte version)
/*     */   {
/* 108 */     this.reserved_bytes = reserved;
/* 109 */     this.datahash_bytes = data_hash;
/* 110 */     this.peer_id_bytes = peer_id;
/* 111 */     this.version = version;
/*     */   }
/*     */   
/*     */   private void constructBuffer() {
/* 115 */     this.buffer = DirectByteBufferPool.getBuffer((byte)16, 68);
/* 116 */     this.buffer.put((byte)11, (byte)"BitTorrent protocol".length());
/* 117 */     this.buffer.put((byte)11, "BitTorrent protocol".getBytes());
/* 118 */     this.buffer.put((byte)11, this.reserved_bytes);
/* 119 */     this.buffer.put((byte)11, this.datahash_bytes);
/* 120 */     this.buffer.put((byte)11, this.peer_id_bytes);
/* 121 */     this.buffer.flip((byte)11);
/*     */   }
/*     */   
/* 124 */   public byte[] getReserved() { return this.reserved_bytes; }
/*     */   
/* 126 */   public byte[] getDataHash() { return this.datahash_bytes; }
/*     */   
/* 128 */   public byte[] getPeerId() { return this.peer_id_bytes; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */   public String getID() { return "BT_HANDSHAKE"; }
/* 136 */   public byte[] getIDBytes() { return BTMessage.ID_BT_HANDSHAKE_BYTES; }
/*     */   
/* 138 */   public String getFeatureID() { return "BT1"; }
/*     */   
/* 140 */   public int getFeatureSubID() { return 10; }
/*     */   
/* 142 */   public int getType() { return 0; }
/*     */   
/* 144 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/* 147 */     if (this.description == null) {
/* 148 */       this.description = ("BT_HANDSHAKE of dataID: " + ByteFormatter.nicePrint(this.datahash_bytes, true) + " peerID: " + PeerClassifier.getPrintablePeerID(this.peer_id_bytes));
/*     */     }
/*     */     
/* 151 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 156 */     if (this.buffer == null) {
/* 157 */       constructBuffer();
/*     */     }
/*     */     
/* 160 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/* 165 */     if (data == null) {
/* 166 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/* 169 */     if (data.remaining((byte)11) != 68) {
/* 170 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 68");
/*     */     }
/*     */     
/* 173 */     if (data.get((byte)11) != (byte)"BitTorrent protocol".length()) {
/* 174 */       throw new MessageException("[" + getID() + "] decode error: payload.get() != (byte)PROTOCOL.length()");
/*     */     }
/*     */     
/* 177 */     byte[] header = new byte["BitTorrent protocol".getBytes().length];
/* 178 */     data.get((byte)11, header);
/*     */     
/* 180 */     if (!"BitTorrent protocol".equals(new String(header))) {
/* 181 */       throw new MessageException("[" + getID() + "] decode error: invalid protocol given: " + new String(header));
/*     */     }
/*     */     
/* 184 */     byte[] reserved = new byte[8];
/* 185 */     data.get((byte)11, reserved);
/*     */     
/* 187 */     byte[] infohash = new byte[20];
/* 188 */     data.get((byte)11, infohash);
/*     */     
/* 190 */     byte[] peerid = new byte[20];
/* 191 */     data.get((byte)11, peerid);
/*     */     
/* 193 */     data.returnToPool();
/*     */     
/* 195 */     if ((peerid[0] == 0) && (peerid[1] == 0)) {
/* 196 */       boolean ok = false;
/* 197 */       for (int i = 2; i < 20; i++) {
/* 198 */         if (peerid[i] != 0)
/*     */         {
/* 200 */           ok = true;
/* 201 */           break;
/*     */         }
/*     */       }
/* 204 */       if (!ok) {
/* 205 */         byte[] x = "-#@0000-".getBytes();
/*     */         
/* 207 */         RandomUtils.nextBytes(peerid);
/*     */         
/* 209 */         System.arraycopy(x, 0, peerid, 0, x.length);
/*     */       }
/*     */     }
/* 212 */     return new BTHandshake(reserved, infohash, peerid, version);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer[] getRawData()
/*     */   {
/* 219 */     if (this.buffer == null) {
/* 220 */       constructBuffer();
/*     */     }
/*     */     
/* 223 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/* 226 */   public int getPriority() { return 2; }
/*     */   
/* 228 */   public boolean isNoDelay() { return true; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 233 */   public Message[] messagesToRemove() { return null; }
/*     */   
/*     */   public void destroy() {
/* 236 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */   
/* 239 */   public Message getBaseMessage() { return this; }
/*     */   
/*     */   public void setNoDelay() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTHandshake.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */