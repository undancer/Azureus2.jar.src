/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*    */ import org.gudy.bouncycastle.asn1.DERIA5String;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERPrintableString;
/*    */ import org.gudy.bouncycastle.asn1.DERUTF8String;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X509DefaultEntryConverter
/*    */   extends X509NameEntryConverter
/*    */ {
/*    */   public DERObject getConvertedValue(DERObjectIdentifier oid, String value)
/*    */   {
/* 32 */     if ((value.length() != 0) && (value.charAt(0) == '#'))
/*    */     {
/*    */       try
/*    */       {
/* 36 */         return convertHexEncoded(value, 1);
/*    */       }
/*    */       catch (IOException e)
/*    */       {
/* 40 */         throw new RuntimeException("can't recode value for oid " + oid.getId());
/*    */       }
/*    */     }
/* 43 */     if ((oid.equals(X509Name.EmailAddress)) || (oid.equals(X509Name.DC)))
/*    */     {
/* 45 */       return new DERIA5String(value);
/*    */     }
/* 47 */     if (oid.equals(X509Name.DATE_OF_BIRTH))
/*    */     {
/* 49 */       return new DERGeneralizedTime(value);
/*    */     }
/* 51 */     if ((oid.equals(X509Name.C)) || (oid.equals(X509Name.SN)) || (oid.equals(X509Name.DN_QUALIFIER)))
/*    */     {
/* 53 */       return new DERPrintableString(value);
/*    */     }
/*    */     
/* 56 */     return new DERUTF8String(value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509DefaultEntryConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */