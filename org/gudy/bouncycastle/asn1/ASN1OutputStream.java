/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ public class ASN1OutputStream
/*    */   extends DEROutputStream
/*    */ {
/*    */   public ASN1OutputStream(OutputStream os)
/*    */   {
/* 12 */     super(os);
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeObject(Object obj)
/*    */     throws IOException
/*    */   {
/* 19 */     if (obj == null)
/*    */     {
/* 21 */       writeNull();
/*    */     }
/* 23 */     else if ((obj instanceof DERObject))
/*    */     {
/* 25 */       ((DERObject)obj).encode(this);
/*    */     }
/* 27 */     else if ((obj instanceof DEREncodable))
/*    */     {
/* 29 */       ((DEREncodable)obj).getDERObject().encode(this);
/*    */     }
/*    */     else
/*    */     {
/* 33 */       throw new IOException("object not ASN1Encodable");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1OutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */