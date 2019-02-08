/*     */ package org.gudy.bouncycastle.crypto.engines;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.StreamCipher;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RC4Engine
/*     */   implements StreamCipher
/*     */ {
/*     */   private static final int STATE_LENGTH = 256;
/*  16 */   private byte[] engineState = null;
/*  17 */   private int x = 0;
/*  18 */   private int y = 0;
/*  19 */   private byte[] workingKey = null;
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
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */   {
/*  34 */     if ((params instanceof KeyParameter))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  41 */       this.workingKey = ((KeyParameter)params).getKey();
/*  42 */       setKey(this.workingKey);
/*     */       
/*  44 */       return;
/*     */     }
/*     */     
/*  47 */     throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + params.getClass().getName());
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  52 */     return "RC4";
/*     */   }
/*     */   
/*     */   public byte returnByte(byte in)
/*     */   {
/*  57 */     this.x = (this.x + 1 & 0xFF);
/*  58 */     this.y = (this.engineState[this.x] + this.y & 0xFF);
/*     */     
/*     */ 
/*  61 */     byte tmp = this.engineState[this.x];
/*  62 */     this.engineState[this.x] = this.engineState[this.y];
/*  63 */     this.engineState[this.y] = tmp;
/*     */     
/*     */ 
/*  66 */     return (byte)(in ^ this.engineState[(this.engineState[this.x] + this.engineState[this.y] & 0xFF)]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
/*     */   {
/*  76 */     for (int i = 0; i < len; i++)
/*     */     {
/*  78 */       this.x = (this.x + 1 & 0xFF);
/*  79 */       this.y = (this.engineState[this.x] + this.y & 0xFF);
/*     */       
/*     */ 
/*  82 */       byte tmp = this.engineState[this.x];
/*  83 */       this.engineState[this.x] = this.engineState[this.y];
/*  84 */       this.engineState[this.y] = tmp;
/*     */       
/*     */ 
/*  87 */       out[(i + outOff)] = ((byte)(in[(i + inOff)] ^ this.engineState[(this.engineState[this.x] + this.engineState[this.y] & 0xFF)]));
/*     */     }
/*     */   }
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
/*     */   public void reset()
/*     */   {
/* 118 */     setKey(this.workingKey);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setKey(byte[] keyBytes)
/*     */   {
/* 125 */     this.workingKey = keyBytes;
/*     */     
/*     */ 
/*     */ 
/* 129 */     this.x = 0;
/* 130 */     this.y = 0;
/*     */     
/* 132 */     if (this.engineState == null)
/*     */     {
/* 134 */       this.engineState = new byte['Ā'];
/*     */     }
/*     */     
/*     */ 
/* 138 */     for (int i = 0; i < 256; i++)
/*     */     {
/* 140 */       this.engineState[i] = ((byte)i);
/*     */     }
/*     */     
/* 143 */     int i1 = 0;
/* 144 */     int i2 = 0;
/*     */     
/* 146 */     for (int i = 0; i < 256; i++)
/*     */     {
/* 148 */       i2 = (keyBytes[i1] & 0xFF) + this.engineState[i] + i2 & 0xFF;
/*     */       
/* 150 */       byte tmp = this.engineState[i];
/* 151 */       this.engineState[i] = this.engineState[i2];
/* 152 */       this.engineState[i2] = tmp;
/* 153 */       i1 = (i1 + 1) % keyBytes.length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/engines/RC4Engine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */