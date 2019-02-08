/*     */ package com.aelitis.azureus.core.dht.transport.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeNetwork;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPRequestHandler;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface DHTTransportUDP
/*     */   extends DHTTransport
/*     */ {
/*     */   public static final byte PROTOCOL_VERSION_2304 = 8;
/*     */   public static final byte PROTOCOL_VERSION_2306 = 12;
/*     */   public static final byte PROTOCOL_VERSION_2400 = 13;
/*     */   public static final byte PROTOCOL_VERSION_2402 = 14;
/*     */   public static final byte PROTOCOL_VERSION_2500 = 15;
/*     */   public static final byte PROTOCOL_VERSION_2502 = 16;
/*     */   public static final byte PROTOCOL_VERSION_3111 = 17;
/*     */   public static final byte PROTOCOL_VERSION_4204 = 22;
/*     */   public static final byte PROTOCOL_VERSION_4208 = 23;
/*     */   public static final byte PROTOCOL_VERSION_4310 = 26;
/*     */   public static final byte PROTOCOL_VERSION_4407 = 50;
/*     */   public static final byte PROTOCOL_VERSION_4511 = 50;
/*     */   public static final byte PROTOCOL_VERSION_4600 = 50;
/*     */   public static final byte PROTOCOL_VERSION_4720 = 50;
/*     */   public static final byte PROTOCOL_VERSION_4800 = 51;
/*     */   public static final byte PROTOCOL_VERSION_5400 = 52;
/*     */   public static final byte PROTOCOL_VERSION_5500 = 52;
/*     */   public static final byte PROTOCOL_VERSION_DIV_AND_CONT = 6;
/*     */   public static final byte PROTOCOL_VERSION_ANTI_SPOOF = 7;
/*     */   public static final byte PROTOCOL_VERSION_ENCRYPT_TT = 8;
/*     */   public static final byte PROTOCOL_VERSION_ANTI_SPOOF2 = 8;
/*     */   public static final byte PROTOCOL_VERSION_FIX_ORIGINATOR = 9;
/*     */   public static final byte PROTOCOL_VERSION_VIVALDI = 10;
/*     */   public static final byte PROTOCOL_VERSION_REMOVE_DIST_ADD_VER = 11;
/*     */   public static final byte PROTOCOL_VERSION_XFER_STATUS = 12;
/*     */   public static final byte PROTOCOL_VERSION_SIZE_ESTIMATE = 13;
/*     */   public static final byte PROTOCOL_VERSION_VENDOR_ID = 14;
/*     */   public static final byte PROTOCOL_VERSION_BLOCK_KEYS = 14;
/*     */   public static final byte PROTOCOL_VERSION_GENERIC_NETPOS = 15;
/*     */   public static final byte PROTOCOL_VERSION_VIVALDI_FINDVALUE = 16;
/*     */   public static final byte PROTOCOL_VERSION_ANON_VALUES = 17;
/*     */   public static final byte PROTOCOL_VERSION_CVS_FIX_OVERLOAD_V1 = 18;
/*     */   public static final byte PROTOCOL_VERSION_CVS_FIX_OVERLOAD_V2 = 19;
/*     */   public static final byte PROTOCOL_VERSION_MORE_STATS = 20;
/*     */   public static final byte PROTOCOL_VERSION_CVS_FIX_OVERLOAD_V3 = 21;
/*     */   public static final byte PROTOCOL_VERSION_MORE_NODE_STATUS = 22;
/*     */   public static final byte PROTOCOL_VERSION_LONGER_LIFE = 23;
/*     */   public static final byte PROTOCOL_VERSION_REPLICATION_CONTROL = 24;
/*     */   public static final byte PROTOCOL_VERSION_REPLICATION_CONTROL2 = 25;
/*     */   public static final byte PROTOCOL_VERSION_REPLICATION_CONTROL3 = 26;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID_PORTS = 32;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID_PORTS2 = 33;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID_PORTS2X = 34;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID_PORTS2Y = 35;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID_PORTS2Z = 36;
/*     */   public static final byte PROTOCOL_VERSION_RESTRICT_ID3 = 50;
/*     */   public static final byte PROTOCOL_VERSION_VIVALDI_OPTIONAL = 51;
/*     */   public static final byte PROTOCOL_VERSION_PACKET_FLAGS = 51;
/*     */   public static final byte PROTOCOL_VERSION_ALT_CONTACTS = 52;
/*     */   public static final byte PROTOCOL_VERSION_PACKET_FLAGS2 = 53;
/*     */   public static final byte PROTOCOL_VERSION_PROC_TIME = 54;
/*     */   public static final byte PROTOCOL_VERSION_NETWORKS = 9;
/*     */   
/*     */   public abstract DHTTransportUDPContact importContact(InetSocketAddress paramInetSocketAddress, byte paramByte, boolean paramBoolean)
/*     */     throws DHTTransportException;
/*     */   
/*     */   public abstract DHTTransportUDPContact importContact(Map<String, Object> paramMap)
/*     */     throws DHTTransportException;
/*     */   
/*     */   public abstract DHTUDPRequestHandler getRequestHandler();
/*     */   
/*     */   public abstract DHTUDPPacketHandler getPacketHandler();
/*     */   
/*     */   public abstract DHTTransportAlternativeNetwork getAlternativeNetwork(int paramInt);
/*     */   
/*     */   public abstract void registerAlternativeNetwork(DHTTransportAlternativeNetwork paramDHTTransportAlternativeNetwork);
/*     */   
/*     */   public abstract void unregisterAlternativeNetwork(DHTTransportAlternativeNetwork paramDHTTransportAlternativeNetwork);
/*     */   
/*     */   public static class Helper
/*     */   {
/* 116 */     private static final int explicit_min = COConfigurationManager.getIntParameter("DHT.protocol.version.min", -1);
/*     */     
/*     */ 
/*     */ 
/*     */     static byte getVersion(byte min)
/*     */     {
/* 122 */       return (byte)Math.max(explicit_min, min & 0xFF);
/*     */     }
/*     */   }
/*     */   
/* 126 */   public static final byte PROTOCOL_VERSION_MAIN = Helper.getVersion();
/* 127 */   public static final byte PROTOCOL_VERSION_CVS = Helper.getVersion((byte)54);
/*     */   
/* 129 */   public static final byte PROTOCOL_VERSION_MIN = Helper.getVersion((byte)51);
/* 130 */   public static final byte PROTOCOL_VERSION_MIN_CVS = Helper.getVersion((byte)51);
/*     */   public static final byte VENDOR_ID_AELITIS = 0;
/*     */   public static final byte VENDOR_ID_ShareNET = 1;
/*     */   public static final byte VENDOR_ID_NONE = -1;
/*     */   public static final byte VENDOR_ID_ME = 0;
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/DHTTransportUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */