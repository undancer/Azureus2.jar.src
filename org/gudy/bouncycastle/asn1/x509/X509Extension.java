/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Object;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.DERBoolean;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X509Extension
/*    */ {
/*    */   boolean critical;
/*    */   ASN1OctetString value;
/*    */   
/*    */   public X509Extension(DERBoolean critical, ASN1OctetString value)
/*    */   {
/* 22 */     this.critical = critical.isTrue();
/* 23 */     this.value = value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public X509Extension(boolean critical, ASN1OctetString value)
/*    */   {
/* 30 */     this.critical = critical;
/* 31 */     this.value = value;
/*    */   }
/*    */   
/*    */   public boolean isCritical()
/*    */   {
/* 36 */     return this.critical;
/*    */   }
/*    */   
/*    */   public ASN1OctetString getValue()
/*    */   {
/* 41 */     return this.value;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 46 */     if (isCritical())
/*    */     {
/* 48 */       return getValue().hashCode();
/*    */     }
/*    */     
/*    */ 
/* 52 */     return getValue().hashCode() ^ 0xFFFFFFFF;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 58 */     if (!(o instanceof X509Extension))
/*    */     {
/* 60 */       return false;
/*    */     }
/*    */     
/* 63 */     X509Extension other = (X509Extension)o;
/*    */     
/* 65 */     return (other.getValue().equals(getValue())) && (other.isCritical() == isCritical());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ASN1Object convertValueToObject(X509Extension ext)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 81 */       return ASN1Object.fromByteArray(ext.getValue().getOctets());
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 85 */       throw new IllegalArgumentException("can't convert extension: " + e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509Extension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */