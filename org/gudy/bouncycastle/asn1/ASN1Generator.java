/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public abstract class ASN1Generator
/*    */ {
/*    */   protected OutputStream _out;
/*    */   
/*    */   public ASN1Generator(OutputStream out)
/*    */   {
/* 11 */     this._out = out;
/*    */   }
/*    */   
/*    */   public abstract OutputStream getRawOutputStream();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Generator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */