/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class DERConstructedSequence
/*    */   extends ASN1Sequence
/*    */ {
/*    */   public void addObject(DEREncodable obj)
/*    */   {
/* 16 */     super.addObject(obj);
/*    */   }
/*    */   
/*    */   public int getSize()
/*    */   {
/* 21 */     return size();
/*    */   }
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
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 36 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 37 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 38 */     Enumeration e = getObjects();
/*    */     
/* 40 */     while (e.hasMoreElements())
/*    */     {
/* 42 */       Object obj = e.nextElement();
/*    */       
/* 44 */       dOut.writeObject(obj);
/*    */     }
/*    */     
/* 47 */     dOut.close();
/*    */     
/* 49 */     byte[] bytes = bOut.toByteArray();
/*    */     
/* 51 */     out.writeEncoded(48, bytes);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERConstructedSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */