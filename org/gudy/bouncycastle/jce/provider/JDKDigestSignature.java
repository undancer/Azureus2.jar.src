/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.DigestInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD2Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD5Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD128Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD160Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD256Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ import org.gudy.bouncycastle.crypto.encodings.PKCS1Encoding;
/*     */ import org.gudy.bouncycastle.crypto.engines.RSAEngine;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDKDigestSignature
/*     */   extends Signature
/*     */   implements PKCSObjectIdentifiers, X509ObjectIdentifiers
/*     */ {
/*     */   private Digest digest;
/*     */   private AsymmetricBlockCipher cipher;
/*     */   private AlgorithmIdentifier algId;
/*     */   
/*     */   protected JDKDigestSignature(String name, DERObjectIdentifier objId, Digest digest, AsymmetricBlockCipher cipher)
/*     */   {
/*  51 */     super(name);
/*     */     
/*  53 */     this.digest = digest;
/*  54 */     this.cipher = cipher;
/*  55 */     this.algId = new AlgorithmIdentifier(objId, null);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInitVerify(PublicKey publicKey)
/*     */     throws InvalidKeyException
/*     */   {
/*  62 */     if (!(publicKey instanceof RSAPublicKey))
/*     */     {
/*  64 */       throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
/*     */     }
/*     */     
/*  67 */     CipherParameters param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);
/*     */     
/*  69 */     this.digest.reset();
/*  70 */     this.cipher.init(false, param);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInitSign(PrivateKey privateKey)
/*     */     throws InvalidKeyException
/*     */   {
/*  77 */     if (!(privateKey instanceof RSAPrivateKey))
/*     */     {
/*  79 */       throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
/*     */     }
/*     */     
/*  82 */     CipherParameters param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);
/*     */     
/*  84 */     this.digest.reset();
/*     */     
/*  86 */     this.cipher.init(true, param);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineUpdate(byte b)
/*     */     throws SignatureException
/*     */   {
/*  93 */     this.digest.update(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineUpdate(byte[] b, int off, int len)
/*     */     throws SignatureException
/*     */   {
/* 102 */     this.digest.update(b, off, len);
/*     */   }
/*     */   
/*     */   protected byte[] engineSign()
/*     */     throws SignatureException
/*     */   {
/* 108 */     byte[] hash = new byte[this.digest.getDigestSize()];
/*     */     
/* 110 */     this.digest.doFinal(hash, 0);
/*     */     
/*     */     try
/*     */     {
/* 114 */       byte[] bytes = derEncode(hash);
/*     */       
/* 116 */       return this.cipher.processBlock(bytes, 0, bytes.length);
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e)
/*     */     {
/* 120 */       throw new SignatureException("key too small for signature type");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 124 */       throw new SignatureException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean engineVerify(byte[] sigBytes)
/*     */     throws SignatureException
/*     */   {
/* 132 */     byte[] hash = new byte[this.digest.getDigestSize()];
/*     */     
/* 134 */     this.digest.doFinal(hash, 0);
/*     */     
/*     */ 
/*     */     DigestInfo digInfo;
/*     */     
/*     */     try
/*     */     {
/* 141 */       byte[] sig = this.cipher.processBlock(sigBytes, 0, sigBytes.length);
/* 142 */       digInfo = derDecode(sig);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 146 */       return false;
/*     */     }
/*     */     
/* 149 */     if (!digInfo.getAlgorithmId().equals(this.algId))
/*     */     {
/* 151 */       return false;
/*     */     }
/*     */     
/* 154 */     byte[] sigHash = digInfo.getDigest();
/*     */     
/* 156 */     if (hash.length != sigHash.length)
/*     */     {
/* 158 */       return false;
/*     */     }
/*     */     
/* 161 */     for (int i = 0; i < hash.length; i++)
/*     */     {
/* 163 */       if (sigHash[i] != hash[i])
/*     */       {
/* 165 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 169 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineSetParameter(AlgorithmParameterSpec params)
/*     */   {
/* 175 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   protected void engineSetParameter(String param, Object value)
/*     */   {
/* 185 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   protected Object engineGetParameter(String param)
/*     */   {
/* 194 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] derEncode(byte[] hash)
/*     */     throws IOException
/*     */   {
/* 201 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 202 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 203 */     DigestInfo dInfo = new DigestInfo(this.algId, hash);
/*     */     
/* 205 */     dOut.writeObject(dInfo);
/*     */     
/* 207 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   private DigestInfo derDecode(byte[] encoding)
/*     */     throws IOException
/*     */   {
/* 214 */     ByteArrayInputStream bIn = new ByteArrayInputStream(encoding);
/* 215 */     DERInputStream dIn = new DERInputStream(bIn);
/*     */     
/* 217 */     return new DigestInfo((ASN1Sequence)dIn.readObject());
/*     */   }
/*     */   
/*     */   public static class SHA1WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public SHA1WithRSAEncryption()
/*     */     {
/* 225 */       super(id_SHA1, new SHA1Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MD2WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public MD2WithRSAEncryption()
/*     */     {
/* 234 */       super(md2, new MD2Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MD5WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public MD5WithRSAEncryption()
/*     */     {
/* 243 */       super(md5, new MD5Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD160WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public RIPEMD160WithRSAEncryption()
/*     */     {
/* 252 */       super(TeleTrusTObjectIdentifiers.ripemd160, new RIPEMD160Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD128WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public RIPEMD128WithRSAEncryption()
/*     */     {
/* 261 */       super(TeleTrusTObjectIdentifiers.ripemd128, new RIPEMD128Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD256WithRSAEncryption
/*     */     extends JDKDigestSignature
/*     */   {
/*     */     public RIPEMD256WithRSAEncryption()
/*     */     {
/* 270 */       super(TeleTrusTObjectIdentifiers.ripemd256, new RIPEMD256Digest(), new PKCS1Encoding(new RSAEngine()));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKDigestSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */