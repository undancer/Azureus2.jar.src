/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ASN1ObjectParser
/*    */ {
/*    */   private int _baseTag;
/*    */   private int _tagNumber;
/*    */   private ASN1StreamParser _aIn;
/*    */   
/*    */   protected ASN1ObjectParser(int baseTag, int tagNumber, InputStream contentStream)
/*    */   {
/* 18 */     this._baseTag = baseTag;
/* 19 */     this._tagNumber = tagNumber;
/* 20 */     this._aIn = new ASN1StreamParser(contentStream);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   int getTagNumber()
/*    */   {
/* 30 */     return this._tagNumber;
/*    */   }
/*    */   
/*    */   int getBaseTag()
/*    */   {
/* 35 */     return this._baseTag;
/*    */   }
/*    */   
/*    */   DEREncodable readObject()
/*    */     throws IOException
/*    */   {
/* 41 */     return this._aIn.readObject();
/*    */   }
/*    */   
/*    */   ASN1EncodableVector readVector()
/*    */     throws IllegalStateException
/*    */   {
/* 47 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/*    */     try
/*    */     {
/*    */       DEREncodable obj;
/* 52 */       while ((obj = readObject()) != null)
/*    */       {
/* 54 */         v.add(obj.getDERObject());
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 59 */       throw new IllegalStateException(e.getMessage());
/*    */     }
/*    */     
/* 62 */     return v;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1ObjectParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */