/*    */ package org.gudy.bouncycastle.jce;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.security.cert.CRLException;
/*    */ import java.security.cert.CertificateEncodingException;
/*    */ import java.security.cert.X509CRL;
/*    */ import java.security.cert.X509Certificate;
/*    */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.TBSCertList;
/*    */ import org.gudy.bouncycastle.asn1.x509.TBSCertificateStructure;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrincipalUtil
/*    */ {
/*    */   public static X509Principal getIssuerX509Principal(X509Certificate cert)
/*    */     throws CertificateEncodingException
/*    */   {
/*    */     try
/*    */     {
/* 24 */       ByteArrayInputStream bIn = new ByteArrayInputStream(cert.getTBSCertificate());
/*    */       
/* 26 */       ASN1InputStream aIn = new ASN1InputStream(bIn);
/* 27 */       TBSCertificateStructure tbsCert = new TBSCertificateStructure((ASN1Sequence)aIn.readObject());
/*    */       
/*    */ 
/* 30 */       return new X509Principal(tbsCert.getIssuer());
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 34 */       throw new CertificateEncodingException(e.toString());
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static X509Principal getSubjectX509Principal(X509Certificate cert)
/*    */     throws CertificateEncodingException
/*    */   {
/*    */     try
/*    */     {
/* 47 */       ByteArrayInputStream bIn = new ByteArrayInputStream(cert.getTBSCertificate());
/*    */       
/* 49 */       ASN1InputStream aIn = new ASN1InputStream(bIn);
/* 50 */       TBSCertificateStructure tbsCert = new TBSCertificateStructure((ASN1Sequence)aIn.readObject());
/*    */       
/*    */ 
/* 53 */       return new X509Principal(tbsCert.getSubject());
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 57 */       throw new CertificateEncodingException(e.toString());
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static X509Principal getIssuerX509Principal(X509CRL crl)
/*    */     throws CRLException
/*    */   {
/*    */     try
/*    */     {
/* 70 */       ByteArrayInputStream bIn = new ByteArrayInputStream(crl.getTBSCertList());
/*    */       
/* 72 */       ASN1InputStream aIn = new ASN1InputStream(bIn);
/* 73 */       TBSCertList tbsCertList = new TBSCertList((ASN1Sequence)aIn.readObject());
/*    */       
/*    */ 
/* 76 */       return new X509Principal(tbsCertList.getIssuer());
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 80 */       throw new CRLException(e.toString());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/PrincipalUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */