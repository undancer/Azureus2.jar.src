/*     */ package com.aelitis.azureus.core.versioncheck;
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
/*     */ public class VersionCheckClientUDPCodecs
/*     */ {
/*     */   public static final int ACT_VERSION_REQUEST = 32;
/*     */   public static final int ACT_VERSION_REPLY = 33;
/*  36 */   private static boolean registered = false;
/*     */   
/*     */ 
/*     */   public static void registerCodecs()
/*     */   {
/*  41 */     if (registered)
/*     */     {
/*  43 */       return;
/*     */     }
/*     */     
/*  46 */     registered = true;
/*     */     
/*  48 */     PRUDPPacketReplyDecoder reply_decoder = new PRUDPPacketReplyDecoder()
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
/*  61 */         switch (action)
/*     */         {
/*     */ 
/*     */         case 33: 
/*  65 */           return new VersionCheckClientUDPReply(is, transaction_id);
/*     */         }
/*     */         
/*     */         
/*  69 */         throw new IOException("Unrecognised action '" + action + "'");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  74 */     };
/*  75 */     Map<Integer, PRUDPPacketReplyDecoder> reply_decoders = new HashMap();
/*     */     
/*  77 */     reply_decoders.put(new Integer(33), reply_decoder);
/*     */     
/*  79 */     PRUDPPacketReply.registerDecoders(reply_decoders);
/*     */     
/*  81 */     PRUDPPacketRequestDecoder request_decoder = new PRUDPPacketRequestDecoder()
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
/*  94 */         switch (action)
/*     */         {
/*     */ 
/*     */         case 32: 
/*  98 */           return new VersionCheckClientUDPRequest(is, connection_id, transaction_id);
/*     */         }
/*     */         
/*     */         
/* 102 */         throw new IOException("unsupported request type");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 107 */     };
/* 108 */     Map<Integer, PRUDPPacketRequestDecoder> request_decoders = new HashMap();
/*     */     
/* 110 */     request_decoders.put(new Integer(32), request_decoder);
/*     */     
/* 112 */     PRUDPPacketRequest.registerDecoders(request_decoders);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/versioncheck/VersionCheckClientUDPCodecs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */