/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERBitString;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.crypto.Digest;
/*    */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
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
/*    */ public class SubjectKeyIdentifier
/*    */   extends ASN1Encodable
/*    */ {
/*    */   private byte[] keyidentifier;
/*    */   
/*    */   public static SubjectKeyIdentifier getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 29 */     return getInstance(ASN1OctetString.getInstance(obj, explicit));
/*    */   }
/*    */   
/*    */ 
/*    */   public static SubjectKeyIdentifier getInstance(Object obj)
/*    */   {
/* 35 */     if ((obj instanceof SubjectKeyIdentifier))
/*    */     {
/* 37 */       return (SubjectKeyIdentifier)obj;
/*    */     }
/*    */     
/* 40 */     if ((obj instanceof SubjectPublicKeyInfo))
/*    */     {
/* 42 */       return new SubjectKeyIdentifier((SubjectPublicKeyInfo)obj);
/*    */     }
/*    */     
/* 45 */     if ((obj instanceof ASN1OctetString))
/*    */     {
/* 47 */       return new SubjectKeyIdentifier((ASN1OctetString)obj);
/*    */     }
/*    */     
/* 50 */     if ((obj instanceof X509Extension))
/*    */     {
/* 52 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*    */     }
/*    */     
/* 55 */     throw new IllegalArgumentException("Invalid SubjectKeyIdentifier: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */   public SubjectKeyIdentifier(byte[] keyid)
/*    */   {
/* 61 */     this.keyidentifier = keyid;
/*    */   }
/*    */   
/*    */ 
/*    */   public SubjectKeyIdentifier(ASN1OctetString keyid)
/*    */   {
/* 67 */     this.keyidentifier = keyid.getOctets();
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
/*    */   public SubjectKeyIdentifier(SubjectPublicKeyInfo spki)
/*    */   {
/* 80 */     Digest digest = new SHA1Digest();
/* 81 */     byte[] resBuf = new byte[digest.getDigestSize()];
/*    */     
/* 83 */     byte[] bytes = spki.getPublicKeyData().getBytes();
/* 84 */     digest.update(bytes, 0, bytes.length);
/* 85 */     digest.doFinal(resBuf, 0);
/* 86 */     this.keyidentifier = resBuf;
/*    */   }
/*    */   
/*    */   public byte[] getKeyIdentifier()
/*    */   {
/* 91 */     return this.keyidentifier;
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 96 */     return new DEROctetString(this.keyidentifier);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/SubjectKeyIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */