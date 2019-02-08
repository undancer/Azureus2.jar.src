/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Enumeration;
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
/*    */ public class DERSequence
/*    */   extends ASN1Sequence
/*    */ {
/*    */   public DERSequence() {}
/*    */   
/*    */   public DERSequence(DEREncodable obj)
/*    */   {
/* 23 */     addObject(obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERSequence(DEREncodableVector v)
/*    */   {
/* 32 */     for (int i = 0; i != v.size(); i++)
/*    */     {
/* 34 */       addObject(v.get(i));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERSequence(ASN1Encodable[] a)
/*    */   {
/* 44 */     for (int i = 0; i != a.length; i++)
/*    */     {
/* 46 */       addObject(a[i]);
/*    */     }
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
/* 62 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 63 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 64 */     Enumeration e = getObjects();
/*    */     
/* 66 */     while (e.hasMoreElements())
/*    */     {
/* 68 */       Object obj = e.nextElement();
/*    */       
/* 70 */       dOut.writeObject(obj);
/*    */     }
/*    */     
/* 73 */     dOut.close();
/*    */     
/* 75 */     byte[] bytes = bOut.toByteArray();
/*    */     
/* 77 */     out.writeEncoded(48, bytes);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */