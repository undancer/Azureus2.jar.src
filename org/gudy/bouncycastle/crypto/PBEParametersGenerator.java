/*     */ package org.gudy.bouncycastle.crypto;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PBEParametersGenerator
/*     */ {
/*     */   protected byte[] password;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] salt;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int iterationCount;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(byte[] password, byte[] salt, int iterationCount)
/*     */   {
/*  34 */     this.password = password;
/*  35 */     this.salt = salt;
/*  36 */     this.iterationCount = iterationCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getPassword()
/*     */   {
/*  46 */     return this.password;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getSalt()
/*     */   {
/*  56 */     return this.salt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIterationCount()
/*     */   {
/*  66 */     return this.iterationCount;
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
/*     */   public abstract CipherParameters generateDerivedParameters(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract CipherParameters generateDerivedParameters(int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract CipherParameters generateDerivedMacParameters(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] PKCS5PasswordToBytes(char[] password)
/*     */   {
/* 106 */     byte[] bytes = new byte[password.length];
/*     */     
/* 108 */     for (int i = 0; i != bytes.length; i++)
/*     */     {
/* 110 */       bytes[i] = ((byte)password[i]);
/*     */     }
/*     */     
/* 113 */     return bytes;
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
/*     */   public static byte[] PKCS12PasswordToBytes(char[] password)
/*     */   {
/* 126 */     if (password.length > 0)
/*     */     {
/*     */ 
/* 129 */       byte[] bytes = new byte[(password.length + 1) * 2];
/*     */       
/* 131 */       for (int i = 0; i != password.length; i++)
/*     */       {
/* 133 */         bytes[(i * 2)] = ((byte)(password[i] >>> '\b'));
/* 134 */         bytes[(i * 2 + 1)] = ((byte)password[i]);
/*     */       }
/*     */       
/* 137 */       return bytes;
/*     */     }
/*     */     
/*     */ 
/* 141 */     return new byte[0];
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/PBEParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */