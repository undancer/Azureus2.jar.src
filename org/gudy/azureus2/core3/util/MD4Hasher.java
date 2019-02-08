/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.security.MessageDigest;
/*    */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
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
/*    */ 
/*    */ public class MD4Hasher
/*    */ {
/*    */   protected MessageDigest md4;
/*    */   
/*    */   public MD4Hasher()
/*    */   {
/*    */     try
/*    */     {
/* 41 */       this.md4 = MessageDigest.getInstance("MD4", BouncyCastleProvider.PROVIDER_NAME);
/*    */ 
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/*    */ 
/* 47 */       Debug.printStackTrace(e);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void reset()
/*    */   {
/* 54 */     this.md4.reset();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void update(byte[] data, int pos, int len)
/*    */   {
/* 63 */     this.md4.update(data, pos, len);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void update(byte[] data)
/*    */   {
/* 70 */     update(data, 0, data.length);
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getDigest()
/*    */   {
/* 76 */     return this.md4.digest();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/MD4Hasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */