/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
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
/*     */ public class NetworkAdminNATUDPCodecs
/*     */ {
/*     */   public static final int ACT_NAT_REQUEST = 40;
/*     */   public static final int ACT_NAT_REPLY = 41;
/*  41 */   private static boolean registered = false;
/*     */   
/*     */   static {
/*  44 */     registerCodecs();
/*     */   }
/*     */   
/*     */ 
/*     */   public static void registerCodecs()
/*     */   {
/*  50 */     if (registered)
/*     */     {
/*  52 */       return;
/*     */     }
/*     */     
/*  55 */     registered = true;
/*     */     
/*  57 */     PRUDPPacketReplyDecoder reply_decoder = new PRUDPPacketReplyDecoder()
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
/*  70 */         switch (action)
/*     */         {
/*     */ 
/*     */         case 41: 
/*  74 */           return new NetworkAdminNATUDPReply(is, transaction_id);
/*     */         }
/*     */         
/*     */         
/*  78 */         throw new IOException("Unrecognised action '" + action + "'");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  83 */     };
/*  84 */     Map reply_decoders = new HashMap();
/*     */     
/*  86 */     reply_decoders.put(new Integer(41), reply_decoder);
/*     */     
/*  88 */     PRUDPPacketReply.registerDecoders(reply_decoders);
/*     */     
/*  90 */     PRUDPPacketRequestDecoder request_decoder = new PRUDPPacketRequestDecoder()
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
/* 103 */         switch (action)
/*     */         {
/*     */         case 40: 
/* 106 */           return new NetworkAdminNATUDPRequest(is, connection_id, transaction_id);
/*     */         }
/*     */         
/*     */         
/* 110 */         throw new IOException("unsupported request type");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 115 */     };
/* 116 */     Map request_decoders = new HashMap();
/*     */     
/* 118 */     request_decoders.put(new Integer(40), request_decoder);
/*     */     
/* 120 */     PRUDPPacketRequest.registerDecoders(request_decoders);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPCodecs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */