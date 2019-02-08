/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class BERSetParser
/*    */   implements ASN1SetParser
/*    */ {
/*    */   private ASN1ObjectParser _parser;
/*    */   
/*    */   BERSetParser(ASN1ObjectParser parser)
/*    */   {
/* 12 */     this._parser = parser;
/*    */   }
/*    */   
/*    */   public DEREncodable readObject()
/*    */     throws IOException
/*    */   {
/* 18 */     return this._parser.readObject();
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 23 */     return new BERSet(this._parser.readVector());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERSetParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */