/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHValidationParameters
/*    */ {
/*    */   private byte[] seed;
/*    */   
/*    */   private int counter;
/*    */   
/*    */ 
/*    */   public DHValidationParameters(byte[] seed, int counter)
/*    */   {
/* 14 */     this.seed = seed;
/* 15 */     this.counter = counter;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 21 */     if ((o == null) || (!(o instanceof DHValidationParameters)))
/*    */     {
/* 23 */       return false;
/*    */     }
/*    */     
/* 26 */     DHValidationParameters other = (DHValidationParameters)o;
/*    */     
/* 28 */     if (other.counter != this.counter)
/*    */     {
/* 30 */       return false;
/*    */     }
/*    */     
/* 33 */     if (other.seed.length != this.seed.length)
/*    */     {
/* 35 */       return false;
/*    */     }
/*    */     
/* 38 */     for (int i = 0; i != other.seed.length; i++)
/*    */     {
/* 40 */       if (other.seed[i] != this.seed[i])
/*    */       {
/* 42 */         return false;
/*    */       }
/*    */     }
/*    */     
/* 46 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHValidationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */