/*    */ package com.aelitis.azureus.core.networkmanager.impl;
/*    */ 
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class TransportCryptoManager
/*    */ {
/* 29 */   private static final TransportCryptoManager instance = new TransportCryptoManager();
/*    */   
/*    */   public static TransportCryptoManager getSingleton() {
/* 32 */     return instance;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void manageCrypto(TransportHelper transport, byte[][] shared_secrets, boolean is_incoming, ByteBuffer initial_data, final HandshakeListener listener)
/*    */   {
/*    */     try
/*    */     {
/* 45 */       new ProtocolDecoderInitial(transport, shared_secrets, !is_incoming, initial_data, new ProtocolDecoderAdapter()
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */         public int getMaximumPlainHeaderLength()
/*    */         {
/*    */ 
/*    */ 
/*    */ 
/* 55 */           return listener.getMaximumPlainHeaderLength();
/*    */         }
/*    */         
/*    */ 
/*    */ 
/*    */         public int matchPlainHeader(ByteBuffer buffer)
/*    */         {
/* 62 */           return listener.matchPlainHeader(buffer);
/*    */         }
/*    */         
/*    */ 
/*    */ 
/*    */         public void gotSecret(byte[] session_secret)
/*    */         {
/* 69 */           listener.gotSecret(session_secret);
/*    */         }
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */         public void decodeComplete(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*    */         {
/* 77 */           listener.handshakeSuccess(decoder, remaining_initial_data);
/*    */         }
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */         public void decodeFailed(ProtocolDecoder decoder, Throwable cause)
/*    */         {
/* 85 */           listener.handshakeFailure(cause);
/*    */         }
/*    */       });
/*    */     }
/*    */     catch (Throwable e) {
/* 90 */       listener.handshakeFailure(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public static abstract interface HandshakeListener
/*    */   {
/*    */     public static final int MATCH_NONE = 1;
/*    */     public static final int MATCH_CRYPTO_NO_AUTO_FALLBACK = 2;
/*    */     public static final int MATCH_CRYPTO_AUTO_FALLBACK = 3;
/*    */     
/*    */     public abstract void handshakeSuccess(ProtocolDecoder paramProtocolDecoder, ByteBuffer paramByteBuffer);
/*    */     
/*    */     public abstract void handshakeFailure(Throwable paramThrowable);
/*    */     
/*    */     public abstract void gotSecret(byte[] paramArrayOfByte);
/*    */     
/*    */     public abstract int getMaximumPlainHeaderLength();
/*    */     
/*    */     public abstract int matchPlainHeader(ByteBuffer paramByteBuffer);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportCryptoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */