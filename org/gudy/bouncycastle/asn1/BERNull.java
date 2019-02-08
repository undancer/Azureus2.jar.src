/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BERNull
/*    */   extends DERNull
/*    */ {
/* 11 */   public static final BERNull INSTANCE = new BERNull();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 21 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*    */     {
/* 23 */       out.write(5);
/*    */     }
/*    */     else
/*    */     {
/* 27 */       super.encode(out);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERNull.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */