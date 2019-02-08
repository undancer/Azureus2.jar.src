/*    */ package com.aelitis.azureus.core.peermanager.peerdb;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.StringInterner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PeerItemFactory
/*    */ {
/*    */   public static final byte PEER_SOURCE_TRACKER = 0;
/*    */   public static final byte PEER_SOURCE_DHT = 1;
/*    */   public static final byte PEER_SOURCE_PEER_EXCHANGE = 2;
/*    */   public static final byte PEER_SOURCE_PLUGIN = 3;
/*    */   public static final byte PEER_SOURCE_INCOMING = 4;
/*    */   public static final byte HANDSHAKE_TYPE_PLAIN = 0;
/*    */   public static final byte HANDSHAKE_TYPE_CRYPTO = 1;
/*    */   public static final byte CRYPTO_LEVEL_1 = 1;
/*    */   public static final byte CRYPTO_LEVEL_2 = 2;
/*    */   public static final byte CRYPTO_LEVEL_CURRENT = 2;
/*    */   
/*    */   public static PeerItem createPeerItem(String address, int tcp_port, byte source, byte handshake_type, int udp_port, byte crypto_level, int up_speed)
/*    */   {
/* 61 */     return (PeerItem)StringInterner.internObject(new PeerItem(address, tcp_port, source, handshake_type, udp_port, crypto_level, up_speed));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PeerItem createPeerItem(byte[] serialization, byte source, byte handshake_type, int udp_port, String network)
/*    */     throws Exception
/*    */   {
/* 71 */     return (PeerItem)StringInterner.internObject(new PeerItem(serialization, source, handshake_type, udp_port, network));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/peerdb/PeerItemFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */