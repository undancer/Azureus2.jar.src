/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReplyDecoder;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequestDecoder;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
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
/*     */ public class DHTUDPPacketHelper
/*     */ {
/*     */   public static final int PACKET_MAX_BYTES = 1400;
/*     */   public static final int ACT_REQUEST_PING = 1024;
/*     */   public static final int ACT_REPLY_PING = 1025;
/*     */   public static final int ACT_REQUEST_STORE = 1026;
/*     */   public static final int ACT_REPLY_STORE = 1027;
/*     */   public static final int ACT_REQUEST_FIND_NODE = 1028;
/*     */   public static final int ACT_REPLY_FIND_NODE = 1029;
/*     */   public static final int ACT_REQUEST_FIND_VALUE = 1030;
/*     */   public static final int ACT_REPLY_FIND_VALUE = 1031;
/*     */   public static final int ACT_REPLY_ERROR = 1032;
/*     */   public static final int ACT_REPLY_STATS = 1033;
/*     */   public static final int ACT_REQUEST_STATS = 1034;
/*     */   public static final int ACT_DATA = 1035;
/*     */   public static final int ACT_REQUEST_KEY_BLOCK = 1036;
/*     */   public static final int ACT_REPLY_KEY_BLOCK = 1037;
/*     */   public static final int ACT_REQUEST_QUERY_STORE = 1038;
/*     */   public static final int ACT_REPLY_QUERY_STORE = 1039;
/*  66 */   private static boolean registered = false;
/*     */   
/*     */ 
/*     */   protected static void registerCodecs()
/*     */   {
/*  71 */     if (registered)
/*     */     {
/*  73 */       return;
/*     */     }
/*     */     
/*  76 */     registered = true;
/*     */     
/*  78 */     PRUDPPacketRequestDecoder request_decoder = new PRUDPPacketRequestDecoder()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public PRUDPPacketRequest decode(PRUDPPacketHandler handler, DataInputStream is, long connection_id, int action, int transaction_id)
/*     */         throws IOException
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */         if (handler == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  96 */           throw new IOException("No handler available for DHT packet decode");
/*     */         }
/*     */         
/*  99 */         DHTUDPPacketNetworkHandler network_handler = (DHTUDPPacketNetworkHandler)handler.getRequestHandler();
/*     */         
/* 101 */         if (network_handler == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 106 */           throw new IOException("No network handler available for DHT packet decode");
/*     */         }
/*     */         
/* 109 */         switch (action)
/*     */         {
/*     */         case 1024: 
/* 112 */           return new DHTUDPPacketRequestPing(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1026: 
/* 116 */           return new DHTUDPPacketRequestStore(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1028: 
/* 120 */           return new DHTUDPPacketRequestFindNode(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1030: 
/* 124 */           return new DHTUDPPacketRequestFindValue(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1034: 
/* 128 */           return new DHTUDPPacketRequestStats(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1035: 
/* 132 */           return new DHTUDPPacketData(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1036: 
/* 136 */           return new DHTUDPPacketRequestKeyBlock(network_handler, is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1038: 
/* 140 */           return new DHTUDPPacketRequestQueryStorage(network_handler, is, connection_id, transaction_id);
/*     */         }
/*     */         
/*     */         
/* 144 */         throw new IOException("Unknown action type");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 149 */     };
/* 150 */     Map request_decoders = new HashMap();
/*     */     
/* 152 */     request_decoders.put(new Integer(1024), request_decoder);
/* 153 */     request_decoders.put(new Integer(1026), request_decoder);
/* 154 */     request_decoders.put(new Integer(1028), request_decoder);
/* 155 */     request_decoders.put(new Integer(1030), request_decoder);
/* 156 */     request_decoders.put(new Integer(1034), request_decoder);
/* 157 */     request_decoders.put(new Integer(1035), request_decoder);
/* 158 */     request_decoders.put(new Integer(1036), request_decoder);
/* 159 */     request_decoders.put(new Integer(1038), request_decoder);
/*     */     
/* 161 */     PRUDPPacketRequest.registerDecoders(request_decoders);
/*     */     
/*     */ 
/*     */ 
/* 165 */     PRUDPPacketReplyDecoder reply_decoder = new PRUDPPacketReplyDecoder()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public PRUDPPacketReply decode(PRUDPPacketHandler handler, InetSocketAddress originator, DataInputStream is, int action, int transaction_id)
/*     */         throws IOException
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 178 */         if (handler == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 183 */           throw new IOException("No handler available for DHT packet decode");
/*     */         }
/*     */         
/* 186 */         DHTUDPPacketNetworkHandler network_handler = (DHTUDPPacketNetworkHandler)handler.getRequestHandler();
/*     */         
/* 188 */         if (network_handler == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 193 */           throw new IOException("No network handler available for DHT packet decode");
/*     */         }
/*     */         
/* 196 */         switch (action)
/*     */         {
/*     */ 
/*     */         case 1025: 
/* 200 */           return new DHTUDPPacketReplyPing(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1027: 
/* 204 */           return new DHTUDPPacketReplyStore(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1029: 
/* 208 */           return new DHTUDPPacketReplyFindNode(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1031: 
/* 212 */           return new DHTUDPPacketReplyFindValue(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1032: 
/* 216 */           return new DHTUDPPacketReplyError(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1033: 
/* 220 */           return new DHTUDPPacketReplyStats(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1037: 
/* 224 */           return new DHTUDPPacketReplyKeyBlock(network_handler, originator, is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1039: 
/* 228 */           return new DHTUDPPacketReplyQueryStorage(network_handler, originator, is, transaction_id);
/*     */         }
/*     */         
/*     */         
/* 232 */         throw new IOException("Unknown action type");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 237 */     };
/* 238 */     Map reply_decoders = new HashMap();
/*     */     
/* 240 */     reply_decoders.put(new Integer(1025), reply_decoder);
/* 241 */     reply_decoders.put(new Integer(1027), reply_decoder);
/* 242 */     reply_decoders.put(new Integer(1029), reply_decoder);
/* 243 */     reply_decoders.put(new Integer(1031), reply_decoder);
/* 244 */     reply_decoders.put(new Integer(1032), reply_decoder);
/* 245 */     reply_decoders.put(new Integer(1033), reply_decoder);
/* 246 */     reply_decoders.put(new Integer(1037), reply_decoder);
/* 247 */     reply_decoders.put(new Integer(1039), reply_decoder);
/*     */     
/* 249 */     PRUDPPacketReply.registerDecoders(reply_decoders);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */