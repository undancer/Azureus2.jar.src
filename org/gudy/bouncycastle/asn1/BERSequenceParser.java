/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class BERSequenceParser
/*    */   implements ASN1SequenceParser
/*    */ {
/*    */   private ASN1ObjectParser _parser;
/*    */   
/*    */   BERSequenceParser(ASN1ObjectParser parser)
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
/* 23 */     return new BERSequence(this._parser.readVector());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERSequenceParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */