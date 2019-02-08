/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ public class BERSequenceGenerator
/*    */   extends BERGenerator
/*    */ {
/*    */   public BERSequenceGenerator(OutputStream out)
/*    */     throws IOException
/*    */   {
/* 13 */     super(out);
/*    */     
/* 15 */     writeBERHeader(48);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit)
/*    */     throws IOException
/*    */   {
/* 24 */     super(out, tagNo, isExplicit);
/*    */     
/* 26 */     writeBERHeader(48);
/*    */   }
/*    */   
/*    */ 
/*    */   public void addObject(DEREncodable object)
/*    */     throws IOException
/*    */   {
/* 33 */     object.getDERObject().encode(new DEROutputStream(this._out));
/*    */   }
/*    */   
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 39 */     writeBEREnd();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERSequenceGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */