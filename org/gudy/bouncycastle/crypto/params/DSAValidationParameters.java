/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAValidationParameters
/*    */ {
/*    */   private byte[] seed;
/*    */   
/*    */   private int counter;
/*    */   
/*    */ 
/*    */   public DSAValidationParameters(byte[] seed, int counter)
/*    */   {
/* 14 */     this.seed = seed;
/* 15 */     this.counter = counter;
/*    */   }
/*    */   
/*    */   public int getCounter()
/*    */   {
/* 20 */     return this.counter;
/*    */   }
/*    */   
/*    */   public byte[] getSeed()
/*    */   {
/* 25 */     return this.seed;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 31 */     if ((o == null) || (!(o instanceof DSAValidationParameters)))
/*    */     {
/* 33 */       return false;
/*    */     }
/*    */     
/* 36 */     DSAValidationParameters other = (DSAValidationParameters)o;
/*    */     
/* 38 */     if (other.counter != this.counter)
/*    */     {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     if (other.seed.length != this.seed.length)
/*    */     {
/* 45 */       return false;
/*    */     }
/*    */     
/* 48 */     for (int i = 0; i != other.seed.length; i++)
/*    */     {
/* 50 */       if (other.seed[i] != this.seed[i])
/*    */       {
/* 52 */         return false;
/*    */       }
/*    */     }
/*    */     
/* 56 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DSAValidationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */