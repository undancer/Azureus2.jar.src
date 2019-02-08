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
/*    */ public class BERSequence
/*    */   extends DERSequence
/*    */ {
/*    */   public BERSequence() {}
/*    */   
/*    */   public BERSequence(DEREncodable obj)
/*    */   {
/* 22 */     super(obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BERSequence(DEREncodableVector v)
/*    */   {
/* 31 */     super(v);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 40 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*    */     {
/* 42 */       out.write(48);
/* 43 */       out.write(128);
/*    */       
/* 45 */       Enumeration e = getObjects();
/* 46 */       while (e.hasMoreElements())
/*    */       {
/* 48 */         out.writeObject(e.nextElement());
/*    */       }
/*    */       
/* 51 */       out.write(0);
/* 52 */       out.write(0);
/*    */     }
/*    */     else
/*    */     {
/* 56 */       super.encode(out);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERSequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */