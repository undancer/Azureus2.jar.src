/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.engines.RC4Engine;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
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
/*     */ public class TransportCipher
/*     */ {
/*  37 */   private static boolean internal_rc4 = true;
/*     */   
/*     */ 
/*     */ 
/*     */   private Cipher cipher;
/*     */   
/*     */ 
/*     */   private RC4Engine rc4_engine;
/*     */   
/*     */ 
/*     */ 
/*     */   public TransportCipher(String algorithm, int mode, SecretKeySpec key_spec, AlgorithmParameterSpec params)
/*     */     throws Exception
/*     */   {
/*  51 */     this.cipher = Cipher.getInstance(algorithm);
/*     */     
/*  53 */     this.cipher.init(mode, key_spec, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   TransportCipher(String algorithm, int mode, SecretKeySpec key_spec)
/*     */     throws Exception
/*     */   {
/*  63 */     if (algorithm.equals("RC4"))
/*     */     {
/*  65 */       if (!internal_rc4) {
/*     */         try
/*     */         {
/*  68 */           this.cipher = Cipher.getInstance(algorithm);
/*     */           
/*  70 */           this.cipher.init(mode, key_spec);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  74 */           internal_rc4 = true;
/*     */         }
/*     */       }
/*     */       
/*  78 */       if (internal_rc4)
/*     */       {
/*  80 */         this.rc4_engine = new RC4Engine();
/*     */         
/*  82 */         CipherParameters params = new KeyParameter(key_spec.getEncoded());
/*     */         
/*  84 */         this.rc4_engine.init(mode == 1, params);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */       byte[] temp = new byte['Ѐ'];
/*     */       
/*  93 */       temp = update(temp);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  98 */       this.cipher = Cipher.getInstance(algorithm);
/*     */       
/* 100 */       this.cipher.init(mode, key_spec);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected byte[] update(byte[] data)
/*     */   {
/* 108 */     return update(data, 0, data.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected byte[] update(byte[] data, int offset, int length)
/*     */   {
/*     */     byte[] result;
/*     */     
/*     */     byte[] result;
/*     */     
/* 119 */     if (length == 0)
/*     */     {
/*     */ 
/*     */ 
/* 123 */       result = new byte[0];
/*     */     } else { byte[] result;
/* 125 */       if (this.cipher != null)
/*     */       {
/* 127 */         result = this.cipher.update(data, offset, length);
/*     */       }
/*     */       else
/*     */       {
/* 131 */         result = new byte[length];
/*     */         
/* 133 */         this.rc4_engine.processBytes(data, offset, length, result, 0);
/*     */       }
/*     */     }
/* 136 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void update(ByteBuffer source_buffer, ByteBuffer target_buffer)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 151 */       int length = source_buffer.remaining();
/*     */       int offset;
/* 153 */       byte[] source_bytes; int offset; if (source_buffer.hasArray())
/*     */       {
/* 155 */         byte[] source_bytes = source_buffer.array();
/*     */         
/* 157 */         offset = source_buffer.arrayOffset() + source_buffer.position();
/*     */       }
/*     */       else
/*     */       {
/* 161 */         source_bytes = new byte[length];
/*     */         
/* 163 */         offset = 0;
/*     */         
/* 165 */         source_buffer.get(source_bytes);
/*     */       }
/*     */       
/* 168 */       byte[] target_bytes = update(source_bytes, offset, length);
/*     */       
/* 170 */       source_buffer.position(source_buffer.limit());
/*     */       
/* 172 */       target_buffer.put(target_bytes);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 176 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 183 */     if (this.cipher != null)
/*     */     {
/* 185 */       String s = this.cipher.getAlgorithm();
/*     */       
/* 187 */       int pos = s.indexOf("/");
/*     */       
/* 189 */       if (pos != -1)
/*     */       {
/* 191 */         s = s.substring(0, pos);
/*     */       }
/*     */       
/* 194 */       if (s.equals("RC4"))
/*     */       {
/* 196 */         s = "RC4-160";
/*     */       }
/*     */       else
/*     */       {
/* 200 */         s = s + "-" + this.cipher.getBlockSize() * 8;
/*     */       }
/*     */       
/* 203 */       return s;
/*     */     }
/*     */     
/* 206 */     return "RC4-160";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */