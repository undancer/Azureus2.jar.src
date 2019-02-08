/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.sec.ECPrivateKeyStructure;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x9.X962NamedCurves;
/*     */ import org.gudy.bouncycastle.asn1.x9.X962Parameters;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ECParameters;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPrivateKey;
/*     */ import org.gudy.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
/*     */ import org.gudy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECParameterSpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPrivateKeySpec;
/*     */ 
/*     */ 
/*     */ public class JCEECPrivateKey
/*     */   implements ECPrivateKey, PKCS12BagAttributeCarrier
/*     */ {
/*  34 */   private String algorithm = "EC";
/*     */   
/*     */   private BigInteger d;
/*     */   private ECParameterSpec ecSpec;
/*  38 */   private Hashtable pkcs12Attributes = new Hashtable();
/*  39 */   private Vector pkcs12Ordering = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */   protected JCEECPrivateKey() {}
/*     */   
/*     */ 
/*     */   JCEECPrivateKey(ECPrivateKey key)
/*     */   {
/*  48 */     this.d = key.getD();
/*  49 */     this.algorithm = key.getAlgorithm();
/*  50 */     this.ecSpec = key.getParams();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   JCEECPrivateKey(String algorithm, ECPrivateKeySpec spec)
/*     */   {
/*  57 */     this.algorithm = algorithm;
/*  58 */     this.d = spec.getD();
/*  59 */     this.ecSpec = spec.getParams();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   JCEECPrivateKey(String algorithm, ECPrivateKeyParameters params, ECParameterSpec spec)
/*     */   {
/*  67 */     ECDomainParameters dp = params.getParameters();
/*     */     
/*  69 */     this.algorithm = algorithm;
/*  70 */     this.d = params.getD();
/*     */     
/*  72 */     if (spec == null)
/*     */     {
/*  74 */       this.ecSpec = new ECParameterSpec(dp.getCurve(), dp.getG(), dp.getN(), dp.getH(), dp.getSeed());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*  83 */       this.ecSpec = spec;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   JCEECPrivateKey(PrivateKeyInfo info)
/*     */   {
/*  90 */     X962Parameters params = new X962Parameters((DERObject)info.getAlgorithmId().getParameters());
/*     */     
/*  92 */     if (params.isNamedCurve())
/*     */     {
/*  94 */       DERObjectIdentifier oid = (DERObjectIdentifier)params.getParameters();
/*  95 */       X9ECParameters ecP = X962NamedCurves.getByOID(oid);
/*     */       
/*  97 */       this.ecSpec = new ECNamedCurveParameterSpec(X962NamedCurves.getName(oid), ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 107 */       X9ECParameters ecP = new X9ECParameters((ASN1Sequence)params.getParameters());
/* 108 */       this.ecSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 115 */     if ((info.getPrivateKey() instanceof DERInteger))
/*     */     {
/* 117 */       DERInteger derD = (DERInteger)info.getPrivateKey();
/*     */       
/* 119 */       this.d = derD.getValue();
/*     */     }
/*     */     else
/*     */     {
/* 123 */       ECPrivateKeyStructure ec = new ECPrivateKeyStructure((ASN1Sequence)info.getPrivateKey());
/*     */       
/* 125 */       this.d = ec.getKey();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getAlgorithm()
/*     */   {
/* 131 */     return this.algorithm;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getFormat()
/*     */   {
/* 141 */     return "PKCS#8";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/* 152 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 153 */     DEROutputStream dOut = new DEROutputStream(bOut);
/* 154 */     X962Parameters params = null;
/*     */     
/* 156 */     if ((this.ecSpec instanceof ECNamedCurveParameterSpec))
/*     */     {
/* 158 */       params = new X962Parameters(X962NamedCurves.getOID(((ECNamedCurveParameterSpec)this.ecSpec).getName()));
/*     */     }
/*     */     else
/*     */     {
/* 162 */       X9ECParameters ecP = new X9ECParameters(this.ecSpec.getCurve(), this.ecSpec.getG(), this.ecSpec.getN(), this.ecSpec.getH(), this.ecSpec.getSeed());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 168 */       params = new X962Parameters(ecP);
/*     */     }
/*     */     
/* 171 */     PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params.getDERObject()), new ECPrivateKeyStructure(getD()).getDERObject());
/*     */     
/*     */     try
/*     */     {
/* 175 */       dOut.writeObject(info);
/* 176 */       dOut.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 180 */       throw new RuntimeException("Error encoding EC private key");
/*     */     }
/*     */     
/* 183 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */   public ECParameterSpec getParams()
/*     */   {
/* 188 */     return this.ecSpec;
/*     */   }
/*     */   
/*     */   public BigInteger getD()
/*     */   {
/* 193 */     return this.d;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBagAttribute(DERObjectIdentifier oid, DEREncodable attribute)
/*     */   {
/* 256 */     this.pkcs12Attributes.put(oid, attribute);
/* 257 */     this.pkcs12Ordering.addElement(oid);
/*     */   }
/*     */   
/*     */ 
/*     */   public DEREncodable getBagAttribute(DERObjectIdentifier oid)
/*     */   {
/* 263 */     return (DEREncodable)this.pkcs12Attributes.get(oid);
/*     */   }
/*     */   
/*     */   public Enumeration getBagAttributeKeys()
/*     */   {
/* 268 */     return this.pkcs12Ordering.elements();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JCEECPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */