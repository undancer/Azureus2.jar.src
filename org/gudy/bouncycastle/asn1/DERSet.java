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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DERSet
/*    */   extends ASN1Set
/*    */ {
/*    */   public DERSet() {}
/*    */   
/*    */   public DERSet(DEREncodable obj)
/*    */   {
/* 26 */     addObject(obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERSet(DEREncodableVector v)
/*    */   {
/* 35 */     this(v, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERSet(ASN1Encodable[] a)
/*    */   {
/* 44 */     for (int i = 0; i != a.length; i++)
/*    */     {
/* 46 */       addObject(a[i]);
/*    */     }
/*    */     
/* 49 */     sort();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   DERSet(DEREncodableVector v, boolean needsSorting)
/*    */   {
/* 59 */     for (int i = 0; i != v.size(); i++)
/*    */     {
/* 61 */       addObject(v.get(i));
/*    */     }
/*    */     
/* 64 */     if (needsSorting)
/*    */     {
/* 66 */       sort();
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
/* 82 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 83 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 84 */     Enumeration e = getObjects();
/*    */     
/* 86 */     while (e.hasMoreElements())
/*    */     {
/* 88 */       Object obj = e.nextElement();
/*    */       
/* 90 */       dOut.writeObject(obj);
/*    */     }
/*    */     
/* 93 */     dOut.close();
/*    */     
/* 95 */     byte[] bytes = bOut.toByteArray();
/*    */     
/* 97 */     out.writeEncoded(49, bytes);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */