/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
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
/*    */ public class BERSet
/*    */   extends DERSet
/*    */ {
/*    */   public BERSet() {}
/*    */   
/*    */   public BERSet(DEREncodable obj)
/*    */   {
/* 22 */     super(obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BERSet(DEREncodableVector v)
/*    */   {
/* 31 */     super(v, false);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   BERSet(DEREncodableVector v, boolean needsSorting)
/*    */   {
/* 41 */     super(v, needsSorting);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 50 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*    */     {
/* 52 */       out.write(49);
/* 53 */       out.write(128);
/*    */       
/* 55 */       Enumeration e = getObjects();
/* 56 */       while (e.hasMoreElements())
/*    */       {
/* 58 */         out.writeObject(e.nextElement());
/*    */       }
/*    */       
/* 61 */       out.write(0);
/* 62 */       out.write(0);
/*    */     }
/*    */     else
/*    */     {
/* 66 */       super.encode(out);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */