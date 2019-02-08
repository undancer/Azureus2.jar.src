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
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class DERConstructedSet
/*    */   extends ASN1Set
/*    */ {
/*    */   public DERConstructedSet() {}
/*    */   
/*    */   public DERConstructedSet(DEREncodable obj)
/*    */   {
/* 24 */     addObject(obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERConstructedSet(DEREncodableVector v)
/*    */   {
/* 33 */     for (int i = 0; i != v.size(); i++)
/*    */     {
/* 35 */       addObject(v.get(i));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void addObject(DEREncodable obj)
/*    */   {
/* 42 */     super.addObject(obj);
/*    */   }
/*    */   
/*    */   public int getSize()
/*    */   {
/* 47 */     return size();
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
/* 77 */     out.writeEncoded(49, bytes);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERConstructedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */