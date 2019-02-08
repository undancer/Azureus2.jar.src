/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.RSAPublicKeySpec;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERNull;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.RSAPublicKeyStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAKeyParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JCERSAPublicKey
/*     */   implements RSAPublicKey
/*     */ {
/*     */   private BigInteger modulus;
/*     */   private BigInteger publicExponent;
/*     */   
/*     */   JCERSAPublicKey(RSAKeyParameters key)
/*     */   {
/*  27 */     this.modulus = key.getModulus();
/*  28 */     this.publicExponent = key.getExponent();
/*     */   }
/*     */   
/*     */ 
/*     */   JCERSAPublicKey(RSAPublicKeySpec spec)
/*     */   {
/*  34 */     this.modulus = spec.getModulus();
/*  35 */     this.publicExponent = spec.getPublicExponent();
/*     */   }
/*     */   
/*     */ 
/*     */   JCERSAPublicKey(RSAPublicKey key)
/*     */   {
/*  41 */     this.modulus = key.getModulus();
/*  42 */     this.publicExponent = key.getPublicExponent();
/*     */   }
/*     */   
/*     */ 
/*     */   JCERSAPublicKey(SubjectPublicKeyInfo info)
/*     */   {
/*     */     try
/*     */     {
/*  50 */       RSAPublicKeyStructure pubKey = new RSAPublicKeyStructure((ASN1Sequence)info.getPublicKey());
/*     */       
/*  52 */       this.modulus = pubKey.getModulus();
/*  53 */       this.publicExponent = pubKey.getPublicExponent();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  57 */       throw new IllegalArgumentException("invalid info structure in RSA public key");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getModulus()
/*     */   {
/*  68 */     return this.modulus;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getPublicExponent()
/*     */   {
/*  78 */     return this.publicExponent;
/*     */   }
/*     */   
/*     */   public String getAlgorithm()
/*     */   {
/*  83 */     return "RSA";
/*     */   }
/*     */   
/*     */   public String getFormat()
/*     */   {
/*  88 */     return "X.509";
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */   {
/*  93 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*  94 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*  95 */     SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()), new RSAPublicKeyStructure(getModulus(), getPublicExponent()).getDERObject());
/*     */     
/*     */     try
/*     */     {
/*  99 */       dOut.writeObject(info);
/* 100 */       dOut.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 104 */       throw new RuntimeException("Error encoding RSA public key");
/*     */     }
/*     */     
/* 107 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 113 */     if (!(o instanceof RSAPublicKey))
/*     */     {
/* 115 */       return false;
/*     */     }
/*     */     
/* 118 */     if (o == this)
/*     */     {
/* 120 */       return true;
/*     */     }
/*     */     
/* 123 */     RSAPublicKey key = (RSAPublicKey)o;
/*     */     
/* 125 */     return (getModulus().equals(key.getModulus())) && (getPublicExponent().equals(key.getPublicExponent()));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 131 */     StringBuilder buf = new StringBuilder();
/* 132 */     String nl = System.getProperty("line.separator");
/*     */     
/* 134 */     buf.append("RSA Public Key").append(nl);
/* 135 */     buf.append("            modulus: ").append(getModulus().toString(16)).append(nl);
/* 136 */     buf.append("    public exponent: ").append(getPublicExponent().toString(16)).append(nl);
/*     */     
/* 138 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JCERSAPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */