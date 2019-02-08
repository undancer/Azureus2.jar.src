/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DEROctetString
/*    */   extends ASN1OctetString
/*    */ {
/*    */   public DEROctetString(byte[] string)
/*    */   {
/* 14 */     super(string);
/*    */   }
/*    */   
/*    */ 
/*    */   public DEROctetString(DEREncodable obj)
/*    */   {
/* 20 */     super(obj);
/*    */   }
/*    */   
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 27 */     out.writeEncoded(4, this.string);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DEROctetString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */