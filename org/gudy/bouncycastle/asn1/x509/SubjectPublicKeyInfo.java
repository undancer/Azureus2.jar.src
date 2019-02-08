/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
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
/*     */ public class SubjectPublicKeyInfo
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private AlgorithmIdentifier algId;
/*     */   private DERBitString keyData;
/*     */   
/*     */   public static SubjectPublicKeyInfo getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  34 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static SubjectPublicKeyInfo getInstance(Object obj)
/*     */   {
/*  40 */     if ((obj instanceof SubjectPublicKeyInfo))
/*     */     {
/*  42 */       return (SubjectPublicKeyInfo)obj;
/*     */     }
/*  44 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  46 */       return new SubjectPublicKeyInfo((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  49 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SubjectPublicKeyInfo(AlgorithmIdentifier algId, DEREncodable publicKey)
/*     */   {
/*  56 */     this.keyData = new DERBitString(publicKey);
/*  57 */     this.algId = algId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SubjectPublicKeyInfo(AlgorithmIdentifier algId, byte[] publicKey)
/*     */   {
/*  64 */     this.keyData = new DERBitString(publicKey);
/*  65 */     this.algId = algId;
/*     */   }
/*     */   
/*     */ 
/*     */   public SubjectPublicKeyInfo(ASN1Sequence seq)
/*     */   {
/*  71 */     if (seq.size() != 2)
/*     */     {
/*  73 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/*  77 */     Enumeration e = seq.getObjects();
/*     */     
/*  79 */     this.algId = AlgorithmIdentifier.getInstance(e.nextElement());
/*  80 */     this.keyData = DERBitString.getInstance(e.nextElement());
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getAlgorithmId()
/*     */   {
/*  85 */     return this.algId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject getPublicKey()
/*     */     throws IOException
/*     */   {
/*  98 */     ASN1InputStream aIn = new ASN1InputStream(this.keyData.getBytes());
/*     */     
/* 100 */     return aIn.readObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERBitString getPublicKeyData()
/*     */   {
/* 108 */     return this.keyData;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 121 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 123 */     v.add(this.algId);
/* 124 */     v.add(this.keyData);
/*     */     
/* 126 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/SubjectPublicKeyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */