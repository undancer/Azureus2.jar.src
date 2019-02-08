/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class TransportHelperFilterStreamXOR
/*     */   extends TransportHelperFilterStream
/*     */ {
/*     */   private final byte[] mask;
/*     */   private int read_position;
/*     */   private int write_position;
/*     */   
/*     */   protected TransportHelperFilterStreamXOR(TransportHelper _transport, byte[] _mask)
/*     */   {
/*  39 */     super(_transport);
/*     */     
/*  41 */     this.mask = _mask;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void cryptoOut(ByteBuffer source_buffer, ByteBuffer target_buffer)
/*     */     throws IOException
/*     */   {
/*  51 */     int rem = source_buffer.remaining();
/*     */     
/*  53 */     for (int i = 0; i < rem; i++)
/*     */     {
/*  55 */       byte b = source_buffer.get();
/*     */       
/*  57 */       b = (byte)(b ^ this.mask[(this.write_position++)]);
/*     */       
/*  59 */       target_buffer.put(b);
/*     */       
/*  61 */       if (this.write_position == this.mask.length)
/*     */       {
/*  63 */         this.write_position = 0;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void cryptoIn(ByteBuffer source_buffer, ByteBuffer target_buffer)
/*     */     throws IOException
/*     */   {
/*  75 */     int rem = source_buffer.remaining();
/*     */     
/*  77 */     for (int i = 0; i < rem; i++)
/*     */     {
/*  79 */       byte b = source_buffer.get();
/*     */       
/*  81 */       b = (byte)(b ^ this.mask[(this.read_position++)]);
/*     */       
/*  83 */       target_buffer.put(b);
/*     */       
/*  85 */       if (this.read_position == this.mask.length)
/*     */       {
/*  87 */         this.read_position = 0;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEncrypted()
/*     */   {
/*  95 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName(boolean verbose)
/*     */   {
/* 101 */     String proto_str = getHelper().getName(verbose);
/*     */     
/* 103 */     if (proto_str.length() > 0)
/*     */     {
/* 105 */       proto_str = " (" + proto_str + ")";
/*     */     }
/*     */     
/* 108 */     return "XOR-" + this.mask.length * 8 + proto_str;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilterStreamXOR.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */