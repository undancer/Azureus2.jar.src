/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class BERConstructedSequence
/*    */   extends DERConstructedSequence
/*    */ {
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 18 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*    */     {
/* 20 */       out.write(48);
/* 21 */       out.write(128);
/*    */       
/* 23 */       Enumeration e = getObjects();
/* 24 */       while (e.hasMoreElements())
/*    */       {
/* 26 */         out.writeObject(e.nextElement());
/*    */       }
/*    */       
/* 29 */       out.write(0);
/* 30 */       out.write(0);
/*    */     }
/*    */     else
/*    */     {
/* 34 */       super.encode(out);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERConstructedSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */