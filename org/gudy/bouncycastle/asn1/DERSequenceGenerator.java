/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class DERSequenceGenerator
/*    */   extends DERGenerator
/*    */ {
/* 10 */   private final ByteArrayOutputStream _bOut = new ByteArrayOutputStream();
/*    */   
/*    */ 
/*    */   public DERSequenceGenerator(OutputStream out)
/*    */     throws IOException
/*    */   {
/* 16 */     super(out);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit)
/*    */     throws IOException
/*    */   {
/* 25 */     super(out, tagNo, isExplicit);
/*    */   }
/*    */   
/*    */ 
/*    */   public void addObject(DEREncodable object)
/*    */     throws IOException
/*    */   {
/* 32 */     object.getDERObject().encode(new DEROutputStream(this._bOut));
/*    */   }
/*    */   
/*    */   public OutputStream getRawOutputStream()
/*    */   {
/* 37 */     return this._bOut;
/*    */   }
/*    */   
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 43 */     writeDEREncoded(48, this._bOut.toByteArray());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERSequenceGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */