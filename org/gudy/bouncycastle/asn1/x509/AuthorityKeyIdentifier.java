/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AuthorityKeyIdentifier
/*     */   extends ASN1Encodable
/*     */ {
/*  40 */   ASN1OctetString keyidentifier = null;
/*  41 */   GeneralNames certissuer = null;
/*  42 */   DERInteger certserno = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static AuthorityKeyIdentifier getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  48 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static AuthorityKeyIdentifier getInstance(Object obj)
/*     */   {
/*  54 */     if ((obj instanceof AuthorityKeyIdentifier))
/*     */     {
/*  56 */       return (AuthorityKeyIdentifier)obj;
/*     */     }
/*  58 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  60 */       return new AuthorityKeyIdentifier((ASN1Sequence)obj);
/*     */     }
/*  62 */     if ((obj instanceof X509Extension))
/*     */     {
/*  64 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*     */     }
/*     */     
/*  67 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public AuthorityKeyIdentifier(ASN1Sequence seq)
/*     */   {
/*  73 */     Enumeration e = seq.getObjects();
/*     */     
/*  75 */     while (e.hasMoreElements())
/*     */     {
/*  77 */       ASN1TaggedObject o = DERTaggedObject.getInstance(e.nextElement());
/*     */       
/*  79 */       switch (o.getTagNo())
/*     */       {
/*     */       case 0: 
/*  82 */         this.keyidentifier = ASN1OctetString.getInstance(o, false);
/*  83 */         break;
/*     */       case 1: 
/*  85 */         this.certissuer = GeneralNames.getInstance(o, false);
/*  86 */         break;
/*     */       case 2: 
/*  88 */         this.certserno = DERInteger.getInstance(o, false);
/*  89 */         break;
/*     */       default: 
/*  91 */         throw new IllegalArgumentException("illegal tag");
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityKeyIdentifier(SubjectPublicKeyInfo spki)
/*     */   {
/* 112 */     Digest digest = new SHA1Digest();
/* 113 */     byte[] resBuf = new byte[digest.getDigestSize()];
/*     */     
/* 115 */     byte[] bytes = spki.getPublicKeyData().getBytes();
/* 116 */     digest.update(bytes, 0, bytes.length);
/* 117 */     digest.doFinal(resBuf, 0);
/* 118 */     this.keyidentifier = new DEROctetString(resBuf);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityKeyIdentifier(SubjectPublicKeyInfo spki, GeneralNames name, BigInteger serialNumber)
/*     */   {
/* 130 */     Digest digest = new SHA1Digest();
/* 131 */     byte[] resBuf = new byte[digest.getDigestSize()];
/*     */     
/* 133 */     byte[] bytes = spki.getPublicKeyData().getBytes();
/* 134 */     digest.update(bytes, 0, bytes.length);
/* 135 */     digest.doFinal(resBuf, 0);
/*     */     
/* 137 */     this.keyidentifier = new DEROctetString(resBuf);
/* 138 */     this.certissuer = GeneralNames.getInstance(name.toASN1Object());
/* 139 */     this.certserno = new DERInteger(serialNumber);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityKeyIdentifier(GeneralNames name, BigInteger serialNumber)
/*     */   {
/* 150 */     this.keyidentifier = null;
/* 151 */     this.certissuer = GeneralNames.getInstance(name.toASN1Object());
/* 152 */     this.certserno = new DERInteger(serialNumber);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityKeyIdentifier(byte[] keyIdentifier)
/*     */   {
/* 161 */     this.keyidentifier = new DEROctetString(keyIdentifier);
/* 162 */     this.certissuer = null;
/* 163 */     this.certserno = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityKeyIdentifier(byte[] keyIdentifier, GeneralNames name, BigInteger serialNumber)
/*     */   {
/* 175 */     this.keyidentifier = new DEROctetString(keyIdentifier);
/* 176 */     this.certissuer = GeneralNames.getInstance(name.toASN1Object());
/* 177 */     this.certserno = new DERInteger(serialNumber);
/*     */   }
/*     */   
/*     */   public byte[] getKeyIdentifier()
/*     */   {
/* 182 */     if (this.keyidentifier != null)
/*     */     {
/* 184 */       return this.keyidentifier.getOctets();
/*     */     }
/*     */     
/* 187 */     return null;
/*     */   }
/*     */   
/*     */   public GeneralNames getAuthorityCertIssuer()
/*     */   {
/* 192 */     return this.certissuer;
/*     */   }
/*     */   
/*     */   public BigInteger getAuthorityCertSerialNumber()
/*     */   {
/* 197 */     if (this.certserno != null)
/*     */     {
/* 199 */       return this.certserno.getValue();
/*     */     }
/*     */     
/* 202 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 210 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 212 */     if (this.keyidentifier != null)
/*     */     {
/* 214 */       v.add(new DERTaggedObject(false, 0, this.keyidentifier));
/*     */     }
/*     */     
/* 217 */     if (this.certissuer != null)
/*     */     {
/* 219 */       v.add(new DERTaggedObject(false, 1, this.certissuer));
/*     */     }
/*     */     
/* 222 */     if (this.certserno != null)
/*     */     {
/* 224 */       v.add(new DERTaggedObject(false, 2, this.certserno));
/*     */     }
/*     */     
/*     */ 
/* 228 */     return new DERSequence(v);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 233 */     return "AuthorityKeyIdentifier: KeyID(" + this.keyidentifier.getOctets() + ")";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AuthorityKeyIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */