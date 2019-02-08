/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrivateKeyInfo
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DERObject privKey;
/*     */   private AlgorithmIdentifier algId;
/*     */   private ASN1Set attributes;
/*     */   
/*     */   public static PrivateKeyInfo getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  32 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static PrivateKeyInfo getInstance(Object obj)
/*     */   {
/*  38 */     if ((obj instanceof PrivateKeyInfo))
/*     */     {
/*  40 */       return (PrivateKeyInfo)obj;
/*     */     }
/*  42 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  44 */       return new PrivateKeyInfo((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  47 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PrivateKeyInfo(AlgorithmIdentifier algId, DERObject privateKey)
/*     */   {
/*  54 */     this(algId, privateKey, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrivateKeyInfo(AlgorithmIdentifier algId, DERObject privateKey, ASN1Set attributes)
/*     */   {
/*  62 */     this.privKey = privateKey;
/*  63 */     this.algId = algId;
/*  64 */     this.attributes = attributes;
/*     */   }
/*     */   
/*     */ 
/*     */   public PrivateKeyInfo(ASN1Sequence seq)
/*     */   {
/*  70 */     Enumeration e = seq.getObjects();
/*     */     
/*  72 */     BigInteger version = ((DERInteger)e.nextElement()).getValue();
/*  73 */     if (version.intValue() != 0)
/*     */     {
/*  75 */       throw new IllegalArgumentException("wrong version for private key info");
/*     */     }
/*     */     
/*  78 */     this.algId = new AlgorithmIdentifier((ASN1Sequence)e.nextElement());
/*     */     
/*     */     try
/*     */     {
/*  82 */       ASN1InputStream aIn = new ASN1InputStream(((ASN1OctetString)e.nextElement()).getOctets());
/*     */       
/*  84 */       this.privKey = aIn.readObject();
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/*  88 */       throw new IllegalArgumentException("Error recoverying private key from sequence");
/*     */     }
/*     */     
/*  91 */     if (e.hasMoreElements())
/*     */     {
/*  93 */       this.attributes = ASN1Set.getInstance((ASN1TaggedObject)e.nextElement(), false);
/*     */     }
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getAlgorithmId()
/*     */   {
/*  99 */     return this.algId;
/*     */   }
/*     */   
/*     */   public DERObject getPrivateKey()
/*     */   {
/* 104 */     return this.privKey;
/*     */   }
/*     */   
/*     */   public ASN1Set getAttributes()
/*     */   {
/* 109 */     return this.attributes;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 131 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 133 */     v.add(new DERInteger(0));
/* 134 */     v.add(this.algId);
/* 135 */     v.add(new DEROctetString(this.privKey));
/*     */     
/* 137 */     if (this.attributes != null)
/*     */     {
/* 139 */       v.add(new DERTaggedObject(false, 0, this.attributes));
/*     */     }
/*     */     
/* 142 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PrivateKeyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */