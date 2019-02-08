/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.interfaces.DSAKey;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DSA;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ import org.gudy.bouncycastle.crypto.signers.ECDSASigner;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECKey;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDKDSASigner
/*     */   extends Signature
/*     */   implements PKCSObjectIdentifiers, X509ObjectIdentifiers
/*     */ {
/*     */   private Digest digest;
/*     */   private DSA signer;
/*     */   private SecureRandom random;
/*     */   
/*     */   protected JDKDSASigner(String name, Digest digest, DSA signer)
/*     */   {
/*  48 */     super(name);
/*     */     
/*  50 */     this.digest = digest;
/*  51 */     this.signer = signer;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInitVerify(PublicKey publicKey)
/*     */     throws InvalidKeyException
/*     */   {
/*  58 */     CipherParameters param = null;
/*     */     
/*  60 */     if ((publicKey instanceof ECKey))
/*     */     {
/*  62 */       param = ECUtil.generatePublicKeyParameter(publicKey);
/*     */     }
/*  64 */     else if ((publicKey instanceof DSAKey))
/*     */     {
/*  66 */       param = DSAUtil.generatePublicKeyParameter(publicKey);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */         throw new InvalidKeyException("can't recognise key type in DSA based signer");
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  94 */         throw new InvalidKeyException("can't recognise key type in DSA based signer");
/*     */       }
/*     */     }
/*     */     
/*  98 */     this.digest.reset();
/*  99 */     this.signer.init(false, param);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void engineInitSign(PrivateKey privateKey, SecureRandom random)
/*     */     throws InvalidKeyException
/*     */   {
/* 107 */     this.random = random;
/* 108 */     engineInitSign(privateKey);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInitSign(PrivateKey privateKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 115 */     CipherParameters param = null;
/*     */     
/* 117 */     if ((privateKey instanceof ECKey))
/*     */     {
/* 119 */       param = ECUtil.generatePrivateKeyParameter(privateKey);
/*     */     }
/*     */     else
/*     */     {
/* 123 */       param = DSAUtil.generatePrivateKeyParameter(privateKey);
/*     */     }
/*     */     
/* 126 */     this.digest.reset();
/*     */     
/* 128 */     if (this.random != null)
/*     */     {
/* 130 */       this.signer.init(true, new ParametersWithRandom(param, this.random));
/*     */     }
/*     */     else
/*     */     {
/* 134 */       this.signer.init(true, param);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineUpdate(byte b)
/*     */     throws SignatureException
/*     */   {
/* 142 */     this.digest.update(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineUpdate(byte[] b, int off, int len)
/*     */     throws SignatureException
/*     */   {
/* 151 */     this.digest.update(b, off, len);
/*     */   }
/*     */   
/*     */   protected byte[] engineSign()
/*     */     throws SignatureException
/*     */   {
/* 157 */     byte[] hash = new byte[this.digest.getDigestSize()];
/*     */     
/* 159 */     this.digest.doFinal(hash, 0);
/*     */     
/*     */     try
/*     */     {
/* 163 */       BigInteger[] sig = this.signer.generateSignature(hash);
/*     */       
/* 165 */       return derEncode(sig[0], sig[1]);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 169 */       throw new SignatureException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean engineVerify(byte[] sigBytes)
/*     */     throws SignatureException
/*     */   {
/* 177 */     byte[] hash = new byte[this.digest.getDigestSize()];
/*     */     
/* 179 */     this.digest.doFinal(hash, 0);
/*     */     
/*     */     BigInteger[] sig;
/*     */     
/*     */     try
/*     */     {
/* 185 */       sig = derDecode(sigBytes);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 189 */       throw new SignatureException("error decoding signature bytes.");
/*     */     }
/*     */     
/* 192 */     return this.signer.verifySignature(hash, sig[0], sig[1]);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineSetParameter(AlgorithmParameterSpec params)
/*     */   {
/* 198 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   protected void engineSetParameter(String param, Object value)
/*     */   {
/* 208 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   protected Object engineGetParameter(String param)
/*     */   {
/* 217 */     throw new UnsupportedOperationException("engineSetParameter unsupported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] derEncode(BigInteger r, BigInteger s)
/*     */     throws IOException
/*     */   {
/* 225 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 226 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 227 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 229 */     v.add(new DERInteger(r));
/* 230 */     v.add(new DERInteger(s));
/*     */     
/* 232 */     dOut.writeObject(new DERSequence(v));
/*     */     
/* 234 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   private BigInteger[] derDecode(byte[] encoding)
/*     */     throws IOException
/*     */   {
/* 241 */     ByteArrayInputStream bIn = new ByteArrayInputStream(encoding);
/* 242 */     DERInputStream dIn = new DERInputStream(bIn);
/* 243 */     ASN1Sequence s = (ASN1Sequence)dIn.readObject();
/*     */     
/* 245 */     BigInteger[] sig = new BigInteger[2];
/*     */     
/* 247 */     sig[0] = ((DERInteger)s.getObjectAt(0)).getValue();
/* 248 */     sig[1] = ((DERInteger)s.getObjectAt(1)).getValue();
/*     */     
/* 250 */     return sig;
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
/*     */   public static class ecDSA
/*     */     extends JDKDSASigner
/*     */   {
/*     */     public ecDSA()
/*     */     {
/* 269 */       super(new SHA1Digest(), new ECDSASigner());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKDSASigner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */