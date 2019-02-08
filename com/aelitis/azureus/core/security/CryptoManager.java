/*    */ package com.aelitis.azureus.core.security;
/*    */ 
/*    */ import java.math.BigInteger;
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
/*    */ public abstract interface CryptoManager
/*    */ {
/*    */   public static final String CRYPTO_CONFIG_PREFIX = "core.crypto.";
/*    */   public static final int HANDLER_ECC = 1;
/* 33 */   public static final int[] HANDLERS = { 1 };
/*    */   
/*    */   public abstract byte[] getSecureID();
/*    */   
/*    */   public abstract CryptoHandler getECCHandler();
/*    */   
/*    */   public abstract byte[] obfuscate(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract byte[] deobfuscate(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract void clearPasswords();
/*    */   
/*    */   public abstract void clearPasswords(int paramInt);
/*    */   
/*    */   public abstract void addPasswordHandler(CryptoManagerPasswordHandler paramCryptoManagerPasswordHandler);
/*    */   
/*    */   public abstract void removePasswordHandler(CryptoManagerPasswordHandler paramCryptoManagerPasswordHandler);
/*    */   
/*    */   public abstract void addKeyListener(CryptoManagerKeyListener paramCryptoManagerKeyListener);
/*    */   
/*    */   public abstract void removeKeyListener(CryptoManagerKeyListener paramCryptoManagerKeyListener);
/*    */   
/*    */   public abstract void setSRPParameters(byte[] paramArrayOfByte, BigInteger paramBigInteger);
/*    */   
/*    */   public abstract SRPParameters getSRPParameters();
/*    */   
/*    */   public static abstract interface SRPParameters
/*    */   {
/*    */     public abstract byte[] getSalt();
/*    */     
/*    */     public abstract BigInteger getVerifier();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */