/*    */ package com.aelitis.azureus.core.peermanager.messaging;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.util.BDecoder;
/*    */ import org.gudy.azureus2.core3.util.BEncoder;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*    */ public class MessagingUtil
/*    */ {
/*    */   public static DirectByteBuffer convertPayloadToBencodedByteStream(Map payload, byte alloc_id)
/*    */   {
/*    */     byte[] raw_payload;
/*    */     try
/*    */     {
/* 39 */       raw_payload = BEncoder.encode(payload);
/*    */       
/* 41 */       if ((raw_payload == null) || (raw_payload.length == 0))
/*    */       {
/* 43 */         throw new Exception("Encoding failed");
/*    */       }
/*    */     }
/*    */     catch (Throwable t) {
/* 47 */       System.err.println("Payload encoding failed: " + payload);
/* 48 */       Debug.out(t);
/* 49 */       raw_payload = new byte[0];
/*    */     }
/*    */     
/* 52 */     DirectByteBuffer buffer = DirectByteBufferPool.getBuffer(alloc_id, raw_payload.length);
/* 53 */     buffer.put((byte)11, raw_payload);
/* 54 */     buffer.flip((byte)11);
/*    */     
/* 56 */     return buffer;
/*    */   }
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
/*    */   public static Map convertBencodedByteStreamToPayload(DirectByteBuffer stream, int min_size, String id)
/*    */     throws MessageException
/*    */   {
/* 71 */     if (stream == null) {
/* 72 */       throw new MessageException("[" + id + "] decode error: stream == null");
/*    */     }
/*    */     
/* 75 */     if (stream.remaining((byte)11) < min_size) {
/* 76 */       throw new MessageException("[" + id + "] decode error: stream.remaining[" + stream.remaining((byte)11) + "] < " + min_size);
/*    */     }
/*    */     
/* 79 */     byte[] raw = new byte[stream.remaining((byte)11)];
/*    */     
/* 81 */     stream.get((byte)11, raw);
/*    */     try
/*    */     {
/* 84 */       Map result = BDecoder.decode(raw);
/*    */       
/* 86 */       stream.returnToPool();
/*    */       
/* 88 */       return result;
/*    */     }
/*    */     catch (Throwable t) {
/* 91 */       throw new MessageException("[" + id + "] payload stream b-decode error: " + t.getMessage());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessagingUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */