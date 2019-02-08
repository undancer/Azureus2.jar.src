/*     */ package org.gudy.azureus2.core3.tracker.protocol.udp;
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
/*     */ 
/*     */ 
/*     */ public class PRUDPTrackerCodecs
/*     */ {
/*  41 */   private static boolean registered = false;
/*     */   
/*     */ 
/*     */   public static void registerCodecs()
/*     */   {
/*  46 */     if (registered)
/*     */     {
/*  48 */       return;
/*     */     }
/*     */     
/*  51 */     registered = true;
/*     */     
/*  53 */     PRUDPPacketReplyDecoder reply_decoder = new PRUDPPacketReplyDecoder()
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
/*  66 */         switch (action)
/*     */         {
/*     */ 
/*     */         case 0: 
/*  70 */           return new PRUDPPacketReplyConnect(is, transaction_id);
/*     */         
/*     */ 
/*     */         case 1: 
/*  74 */           if (PRUDPPacketTracker.VERSION == 1) {
/*  75 */             return new PRUDPPacketReplyAnnounce(is, transaction_id);
/*     */           }
/*  77 */           return new PRUDPPacketReplyAnnounce2(is, transaction_id);
/*     */         
/*     */ 
/*     */ 
/*     */         case 2: 
/*  82 */           if (PRUDPPacketTracker.VERSION == 1) {
/*  83 */             return new PRUDPPacketReplyScrape(is, transaction_id);
/*     */           }
/*  85 */           return new PRUDPPacketReplyScrape2(is, transaction_id);
/*     */         
/*     */ 
/*     */ 
/*     */         case 3: 
/*  90 */           return new PRUDPPacketReplyError(is, transaction_id);
/*     */         }
/*     */         
/*     */         
/*  94 */         throw new IOException("Unrecognised action '" + action + "'");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  99 */     };
/* 100 */     Map reply_decoders = new HashMap();
/*     */     
/* 102 */     reply_decoders.put(new Integer(0), reply_decoder);
/* 103 */     reply_decoders.put(new Integer(1), reply_decoder);
/* 104 */     reply_decoders.put(new Integer(2), reply_decoder);
/* 105 */     reply_decoders.put(new Integer(3), reply_decoder);
/*     */     
/* 107 */     PRUDPPacketReply.registerDecoders(reply_decoders);
/*     */     
/* 109 */     PRUDPPacketRequestDecoder request_decoder = new PRUDPPacketRequestDecoder()
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
/* 122 */         switch (action)
/*     */         {
/*     */         case 0: 
/* 125 */           return new PRUDPPacketRequestConnect(is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */         case 1: 
/* 129 */           if (PRUDPPacketTracker.VERSION == 1) {
/* 130 */             return new PRUDPPacketRequestAnnounce(is, connection_id, transaction_id);
/*     */           }
/* 132 */           return new PRUDPPacketRequestAnnounce2(is, connection_id, transaction_id);
/*     */         
/*     */ 
/*     */ 
/*     */         case 2: 
/* 137 */           return new PRUDPPacketRequestScrape(is, connection_id, transaction_id);
/*     */         }
/*     */         
/*     */         
/* 141 */         throw new IOException("unsupported request type");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 146 */     };
/* 147 */     Map request_decoders = new HashMap();
/*     */     
/* 149 */     request_decoders.put(new Integer(0), request_decoder);
/* 150 */     request_decoders.put(new Integer(1), request_decoder);
/* 151 */     request_decoders.put(new Integer(2), request_decoder);
/*     */     
/* 153 */     PRUDPPacketRequest.registerDecoders(request_decoders);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPTrackerCodecs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */