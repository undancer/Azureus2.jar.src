/*     */ package org.gudy.bouncycastle.openssl;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.math.BigInteger;
/*     */ import java.security.Key;
/*     */ import java.security.KeyPair;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.interfaces.DSAPrivateKey;
/*     */ import java.security.interfaces.RSAPrivateCrtKey;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Object;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.cms.ContentInfo;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.DSAParameter;
/*     */ import org.gudy.bouncycastle.jce.PKCS10CertificationRequest;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.util.Strings;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
/*     */ import org.gudy.bouncycastle.util.encoders.Hex;
/*     */ import org.gudy.bouncycastle.x509.X509AttributeCertificate;
/*     */ import org.gudy.bouncycastle.x509.X509V2AttributeCertificate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PEMWriter
/*     */   extends BufferedWriter
/*     */ {
/*     */   private String provider;
/*     */   
/*     */   public PEMWriter(Writer out)
/*     */   {
/*  53 */     this(out, BouncyCastleProvider.PROVIDER_NAME);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PEMWriter(Writer out, String provider)
/*     */   {
/*  60 */     super(out);
/*     */     
/*  62 */     this.provider = provider;
/*     */   }
/*     */   
/*     */   private void writeHexEncoded(byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  68 */     bytes = Hex.encode(bytes);
/*     */     
/*  70 */     for (int i = 0; i != bytes.length; i++)
/*     */     {
/*  72 */       write((char)bytes[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeEncoded(byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  79 */     char[] buf = new char[64];
/*     */     
/*  81 */     bytes = Base64.encode(bytes);
/*     */     
/*  83 */     for (int i = 0; i < bytes.length; i += buf.length)
/*     */     {
/*  85 */       int index = 0;
/*     */       
/*  87 */       while (index != buf.length)
/*     */       {
/*  89 */         if (i + index >= bytes.length) {
/*     */           break;
/*     */         }
/*     */         
/*  93 */         buf[index] = ((char)bytes[(i + index)]);
/*  94 */         index++;
/*     */       }
/*  96 */       write(buf, 0, index);
/*  97 */       newLine();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeObject(Object o)
/*     */     throws IOException
/*     */   {
/*     */     byte[] encoding;
/*     */     
/* 108 */     if ((o instanceof X509Certificate))
/*     */     {
/* 110 */       String type = "CERTIFICATE";
/*     */       try
/*     */       {
/* 113 */         encoding = ((X509Certificate)o).getEncoded();
/*     */       }
/*     */       catch (CertificateEncodingException e)
/*     */       {
/* 117 */         throw new IOException("Cannot encode object: " + e.toString());
/*     */       }
/*     */     }
/* 120 */     else if ((o instanceof X509CRL))
/*     */     {
/* 122 */       String type = "X509 CRL";
/*     */       try
/*     */       {
/* 125 */         encoding = ((X509CRL)o).getEncoded();
/*     */       }
/*     */       catch (CRLException e)
/*     */       {
/* 129 */         throw new IOException("Cannot encode object: " + e.toString());
/*     */       }
/*     */     } else {
/* 132 */       if ((o instanceof KeyPair))
/*     */       {
/* 134 */         writeObject(((KeyPair)o).getPrivate()); return; }
/*     */       byte[] encoding;
/*     */       String type;
/* 137 */       if ((o instanceof PrivateKey))
/*     */       {
/* 139 */         PrivateKeyInfo info = new PrivateKeyInfo((ASN1Sequence)ASN1Object.fromByteArray(((Key)o).getEncoded()));
/*     */         
/*     */ 
/* 142 */         if ((o instanceof RSAPrivateKey))
/*     */         {
/* 144 */           String type = "RSA PRIVATE KEY";
/*     */           
/* 146 */           encoding = info.getPrivateKey().getEncoded();
/*     */         } else { byte[] encoding;
/* 148 */           if ((o instanceof DSAPrivateKey))
/*     */           {
/* 150 */             String type = "DSA PRIVATE KEY";
/*     */             
/* 152 */             DSAParameter p = DSAParameter.getInstance(info.getAlgorithmId().getParameters());
/* 153 */             ASN1EncodableVector v = new ASN1EncodableVector();
/*     */             
/* 155 */             v.add(new DERInteger(0));
/* 156 */             v.add(new DERInteger(p.getP()));
/* 157 */             v.add(new DERInteger(p.getQ()));
/* 158 */             v.add(new DERInteger(p.getG()));
/*     */             
/* 160 */             BigInteger x = ((DSAPrivateKey)o).getX();
/* 161 */             BigInteger y = p.getG().modPow(x, p.getP());
/*     */             
/* 163 */             v.add(new DERInteger(y));
/* 164 */             v.add(new DERInteger(x));
/*     */             
/* 166 */             encoding = new DERSequence(v).getEncoded();
/*     */           }
/*     */           else
/*     */           {
/* 170 */             throw new IOException("Cannot identify private key");
/*     */           }
/*     */         } } else { byte[] encoding;
/* 173 */         if ((o instanceof PublicKey))
/*     */         {
/* 175 */           String type = "PUBLIC KEY";
/*     */           
/* 177 */           encoding = ((PublicKey)o).getEncoded();
/*     */         } else { byte[] encoding;
/* 179 */           if ((o instanceof X509AttributeCertificate))
/*     */           {
/* 181 */             String type = "ATTRIBUTE CERTIFICATE";
/* 182 */             encoding = ((X509V2AttributeCertificate)o).getEncoded();
/*     */           } else { byte[] encoding;
/* 184 */             if ((o instanceof PKCS10CertificationRequest))
/*     */             {
/* 186 */               String type = "CERTIFICATE REQUEST";
/* 187 */               encoding = ((PKCS10CertificationRequest)o).getEncoded();
/*     */             } else { byte[] encoding;
/* 189 */               if ((o instanceof ContentInfo))
/*     */               {
/* 191 */                 String type = "PKCS7";
/* 192 */                 encoding = ((ContentInfo)o).getEncoded();
/*     */               }
/*     */               else
/*     */               {
/* 196 */                 throw new IOException("unknown object passed - can't encode."); } } } } } }
/*     */     byte[] encoding;
/*     */     String type;
/* 199 */     writeHeader(type);
/* 200 */     writeEncoded(encoding);
/* 201 */     writeFooter(type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeObject(Object obj, String algorithm, char[] password, SecureRandom random)
/*     */     throws IOException
/*     */   {
/* 211 */     if ((obj instanceof KeyPair))
/*     */     {
/* 213 */       writeObject(((KeyPair)obj).getPrivate());
/* 214 */       return;
/*     */     }
/*     */     
/*     */ 
/* 218 */     String type = null;
/* 219 */     byte[] keyData = null;
/*     */     
/* 221 */     if ((obj instanceof RSAPrivateCrtKey))
/*     */     {
/* 223 */       type = "RSA PRIVATE KEY";
/*     */       
/* 225 */       RSAPrivateCrtKey k = (RSAPrivateCrtKey)obj;
/*     */       
/* 227 */       RSAPrivateKeyStructure keyStruct = new RSAPrivateKeyStructure(k.getModulus(), k.getPublicExponent(), k.getPrivateExponent(), k.getPrimeP(), k.getPrimeQ(), k.getPrimeExponentP(), k.getPrimeExponentQ(), k.getCrtCoefficient());
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
/* 238 */       keyData = keyStruct.getEncoded();
/*     */     }
/* 240 */     else if ((obj instanceof DSAPrivateKey))
/*     */     {
/* 242 */       type = "DSA PRIVATE KEY";
/*     */       
/* 244 */       DSAPrivateKey k = (DSAPrivateKey)obj;
/* 245 */       DSAParams p = k.getParams();
/* 246 */       ASN1EncodableVector v = new ASN1EncodableVector();
/*     */       
/* 248 */       v.add(new DERInteger(0));
/* 249 */       v.add(new DERInteger(p.getP()));
/* 250 */       v.add(new DERInteger(p.getQ()));
/* 251 */       v.add(new DERInteger(p.getG()));
/*     */       
/* 253 */       BigInteger x = k.getX();
/* 254 */       BigInteger y = p.getG().modPow(x, p.getP());
/*     */       
/* 256 */       v.add(new DERInteger(y));
/* 257 */       v.add(new DERInteger(x));
/*     */       
/* 259 */       keyData = new DERSequence(v).getEncoded();
/*     */     }
/*     */     
/* 262 */     if ((type == null) || (keyData == null))
/*     */     {
/*     */ 
/* 265 */       throw new IllegalArgumentException("Object type not supported: " + obj.getClass().getName());
/*     */     }
/*     */     
/*     */ 
/* 269 */     String dekAlgName = Strings.toUpperCase(algorithm);
/*     */     
/*     */ 
/* 272 */     if (dekAlgName.equals("DESEDE"))
/*     */     {
/* 274 */       dekAlgName = "DES-EDE3-CBC";
/*     */     }
/*     */     
/* 277 */     int ivLength = dekAlgName.startsWith("AES-") ? 16 : 8;
/*     */     
/* 279 */     byte[] iv = new byte[ivLength];
/* 280 */     random.nextBytes(iv);
/*     */     
/* 282 */     byte[] encData = PEMUtilities.crypt(true, this.provider, keyData, password, dekAlgName, iv);
/*     */     
/*     */ 
/*     */ 
/* 286 */     writeHeader(type);
/* 287 */     write("Proc-Type: 4,ENCRYPTED");
/* 288 */     newLine();
/* 289 */     write("DEK-Info: " + dekAlgName + ",");
/* 290 */     writeHexEncoded(iv);
/* 291 */     newLine();
/* 292 */     newLine();
/* 293 */     writeEncoded(encData);
/* 294 */     writeFooter(type);
/*     */   }
/*     */   
/*     */ 
/*     */   private void writeHeader(String type)
/*     */     throws IOException
/*     */   {
/* 301 */     write("-----BEGIN " + type + "-----");
/* 302 */     newLine();
/*     */   }
/*     */   
/*     */ 
/*     */   private void writeFooter(String type)
/*     */     throws IOException
/*     */   {
/* 309 */     write("-----END " + type + "-----");
/* 310 */     newLine();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/openssl/PEMWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */