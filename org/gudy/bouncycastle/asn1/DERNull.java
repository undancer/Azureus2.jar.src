/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DERNull
/*    */   extends ASN1Null
/*    */ {
/* 11 */   public static final DERNull INSTANCE = new DERNull();
/*    */   
/* 13 */   byte[] zeroBytes = new byte[0];
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 23 */     out.writeEncoded(5, this.zeroBytes);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERNull.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */