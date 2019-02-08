/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x9.X962NamedCurves;
/*     */ import org.gudy.bouncycastle.asn1.x9.X962Parameters;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ECParameters;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ECPoint;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPublicKey;
/*     */ import org.gudy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECParameterSpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPublicKeySpec;
/*     */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*     */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*     */ 
/*     */ public class JCEECPublicKey implements ECPublicKey
/*     */ {
/*  33 */   private String algorithm = "EC";
/*     */   
/*     */   private ECPoint q;
/*     */   
/*     */   private ECParameterSpec ecSpec;
/*     */   
/*     */   JCEECPublicKey(String algorithm, ECPublicKeySpec spec)
/*     */   {
/*  41 */     this.algorithm = algorithm;
/*  42 */     this.q = spec.getQ();
/*  43 */     this.ecSpec = spec.getParams();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   JCEECPublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec)
/*     */   {
/*  51 */     ECDomainParameters dp = params.getParameters();
/*     */     
/*  53 */     this.algorithm = algorithm;
/*  54 */     this.q = params.getQ();
/*     */     
/*  56 */     if (spec == null)
/*     */     {
/*  58 */       this.ecSpec = new ECParameterSpec(dp.getCurve(), dp.getG(), dp.getN(), dp.getH(), dp.getSeed());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*  67 */       this.ecSpec = spec;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   JCEECPublicKey(String algorithm, ECPublicKey key)
/*     */   {
/*  75 */     this.q = key.getQ();
/*  76 */     this.algorithm = key.getAlgorithm();
/*  77 */     this.ecSpec = key.getParams();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   JCEECPublicKey(String algorithm, ECPoint q, ECParameterSpec ecSpec)
/*     */   {
/*  85 */     this.algorithm = algorithm;
/*  86 */     this.q = q;
/*  87 */     this.ecSpec = ecSpec;
/*     */   }
/*     */   
/*     */ 
/*     */   JCEECPublicKey(SubjectPublicKeyInfo info)
/*     */   {
/*  93 */     X962Parameters params = new X962Parameters((DERObject)info.getAlgorithmId().getParameters());
/*     */     
/*  95 */     if (params.isNamedCurve())
/*     */     {
/*  97 */       DERObjectIdentifier oid = (DERObjectIdentifier)params.getParameters();
/*  98 */       X9ECParameters ecP = X962NamedCurves.getByOID(oid);
/*     */       
/* 100 */       this.ecSpec = new ECNamedCurveParameterSpec(X962NamedCurves.getName(oid), ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 110 */       X9ECParameters ecP = new X9ECParameters((ASN1Sequence)params.getParameters());
/*     */       
/* 112 */       this.ecSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     DERBitString bits = info.getPublicKeyData();
/* 121 */     byte[] data = bits.getBytes();
/* 122 */     ASN1OctetString key = new DEROctetString(data);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 127 */     if ((data[0] == 4) && (data[1] == data.length - 2) && ((data[2] == 2) || (data[2] == 3)))
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 132 */         ByteArrayInputStream bIn = new ByteArrayInputStream(data);
/* 133 */         DERInputStream dIn = new DERInputStream(bIn);
/*     */         
/* 135 */         key = (ASN1OctetString)dIn.readObject();
/*     */       }
/*     */       catch (IOException ex)
/*     */       {
/* 139 */         throw new IllegalArgumentException("error recovering public key");
/*     */       }
/*     */     }
/*     */     
/* 143 */     X9ECPoint derQ = new X9ECPoint(this.ecSpec.getCurve(), key);
/*     */     
/* 145 */     this.q = derQ.getPoint();
/*     */   }
/*     */   
/*     */   public String getAlgorithm()
/*     */   {
/* 150 */     return this.algorithm;
/*     */   }
/*     */   
/*     */   public String getFormat()
/*     */   {
/* 155 */     return "X.509";
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */   {
/* 160 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 161 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 162 */     X962Parameters params = null;
/*     */     
/* 164 */     if ((this.ecSpec instanceof ECNamedCurveParameterSpec))
/*     */     {
/* 166 */       params = new X962Parameters(X962NamedCurves.getOID(((ECNamedCurveParameterSpec)this.ecSpec).getName()));
/*     */     }
/*     */     else
/*     */     {
/* 170 */       X9ECParameters ecP = new X9ECParameters(this.ecSpec.getCurve(), this.ecSpec.getG(), this.ecSpec.getN(), this.ecSpec.getH(), this.ecSpec.getSeed());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 176 */       params = new X962Parameters(ecP);
/*     */     }
/*     */     
/* 179 */     ASN1OctetString p = (ASN1OctetString)new X9ECPoint(getQ()).getDERObject();
/*     */     
/* 181 */     SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params.getDERObject()), p.getOctets());
/*     */     
/*     */     try
/*     */     {
/* 185 */       dOut.writeObject(info);
/* 186 */       dOut.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 190 */       throw new RuntimeException("Error encoding EC public key");
/*     */     }
/*     */     
/* 193 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */   public ECParameterSpec getParams()
/*     */   {
/* 198 */     return this.ecSpec;
/*     */   }
/*     */   
/*     */   public ECPoint getQ()
/*     */   {
/* 203 */     return this.q;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 208 */     StringBuilder buf = new StringBuilder();
/* 209 */     String nl = System.getProperty("line.separator");
/*     */     
/* 211 */     buf.append("EC Public Key").append(nl);
/* 212 */     buf.append("            X: ").append(getQ().getX().toBigInteger().toString(16)).append(nl);
/* 213 */     buf.append("            Y: ").append(getQ().getY().toBigInteger().toString(16)).append(nl);
/*     */     
/* 215 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JCEECPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */