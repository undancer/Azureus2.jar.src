/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ public class BEROctetStringParser
/*    */   implements ASN1OctetStringParser
/*    */ {
/*    */   private ASN1ObjectParser _parser;
/*    */   
/*    */   protected BEROctetStringParser(ASN1ObjectParser parser)
/*    */   {
/* 15 */     this._parser = parser;
/*    */   }
/*    */   
/*    */   public InputStream getOctetStream()
/*    */   {
/* 20 */     return new ConstructedOctetStream(this._parser);
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 25 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 26 */     InputStream in = getOctetStream();
/*    */     
/*    */     try
/*    */     {
/*    */       int ch;
/* 31 */       while ((ch = in.read()) >= 0)
/*    */       {
/* 33 */         bOut.write(ch);
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 38 */       throw new IllegalStateException("IOException converting stream to byte array: " + e.getMessage());
/*    */     }
/*    */     
/* 41 */     return new BERConstructedOctetString(bOut.toByteArray());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BEROctetStringParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */