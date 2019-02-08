/*    */ package com.aelitis.azureus.core.networkmanager.impl;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TransportHelperFilterStreamCipher
/*    */   extends TransportHelperFilterStream
/*    */ {
/*    */   private final TransportCipher read_cipher;
/*    */   private final TransportCipher write_cipher;
/*    */   
/*    */   public TransportHelperFilterStreamCipher(TransportHelper _transport, TransportCipher _read_cipher, TransportCipher _write_cipher)
/*    */   {
/* 40 */     super(_transport);
/*    */     
/* 42 */     this.read_cipher = _read_cipher;
/* 43 */     this.write_cipher = _write_cipher;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void cryptoOut(ByteBuffer source_buffer, ByteBuffer target_buffer)
/*    */     throws IOException
/*    */   {
/* 53 */     this.write_cipher.update(source_buffer, target_buffer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void cryptoIn(ByteBuffer source_buffer, ByteBuffer target_buffer)
/*    */     throws IOException
/*    */   {
/* 63 */     this.read_cipher.update(source_buffer, target_buffer);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isEncrypted()
/*    */   {
/* 69 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName(boolean verbose)
/*    */   {
/* 75 */     String proto_str = getHelper().getName(verbose);
/*    */     
/* 77 */     if (proto_str.length() > 0)
/*    */     {
/* 79 */       proto_str = " (" + proto_str + ")";
/*    */     }
/*    */     
/* 82 */     return this.read_cipher.getName() + proto_str;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilterStreamCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */