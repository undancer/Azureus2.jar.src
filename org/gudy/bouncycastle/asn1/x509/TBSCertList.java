/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERUTCTime;
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
/*     */ public class TBSCertList
/*     */   extends ASN1Encodable
/*     */ {
/*     */   ASN1Sequence seq;
/*     */   DERInteger version;
/*     */   AlgorithmIdentifier signature;
/*     */   X509Name issuer;
/*     */   Time thisUpdate;
/*     */   Time nextUpdate;
/*     */   CRLEntry[] revokedCertificates;
/*     */   X509Extensions crlExtensions;
/*     */   
/*     */   public static class CRLEntry
/*     */     extends ASN1Encodable
/*     */   {
/*     */     ASN1Sequence seq;
/*     */     DERInteger userCertificate;
/*     */     Time revocationDate;
/*     */     X509Extensions crlEntryExtensions;
/*     */     
/*     */     public CRLEntry(ASN1Sequence seq)
/*     */     {
/*  53 */       if ((seq.size() < 2) || (seq.size() > 3))
/*     */       {
/*  55 */         throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */       }
/*     */       
/*  58 */       this.seq = seq;
/*     */       
/*  60 */       this.userCertificate = DERInteger.getInstance(seq.getObjectAt(0));
/*  61 */       this.revocationDate = Time.getInstance(seq.getObjectAt(1));
/*  62 */       if (seq.size() == 3)
/*     */       {
/*  64 */         this.crlEntryExtensions = X509Extensions.getInstance(seq.getObjectAt(2));
/*     */       }
/*     */     }
/*     */     
/*     */     public DERInteger getUserCertificate()
/*     */     {
/*  70 */       return this.userCertificate;
/*     */     }
/*     */     
/*     */     public Time getRevocationDate()
/*     */     {
/*  75 */       return this.revocationDate;
/*     */     }
/*     */     
/*     */     public X509Extensions getExtensions()
/*     */     {
/*  80 */       return this.crlEntryExtensions;
/*     */     }
/*     */     
/*     */     public DERObject toASN1Object()
/*     */     {
/*  85 */       return this.seq;
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
/*     */   public static TBSCertList getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/* 103 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static TBSCertList getInstance(Object obj)
/*     */   {
/* 109 */     if ((obj instanceof TBSCertList))
/*     */     {
/* 111 */       return (TBSCertList)obj;
/*     */     }
/* 113 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/* 115 */       return new TBSCertList((ASN1Sequence)obj);
/*     */     }
/*     */     
/* 118 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public TBSCertList(ASN1Sequence seq)
/*     */   {
/* 124 */     if ((seq.size() < 3) || (seq.size() > 7))
/*     */     {
/* 126 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/* 129 */     int seqPos = 0;
/*     */     
/* 131 */     this.seq = seq;
/*     */     
/* 133 */     if ((seq.getObjectAt(seqPos) instanceof DERInteger))
/*     */     {
/* 135 */       this.version = DERInteger.getInstance(seq.getObjectAt(seqPos++));
/*     */     }
/*     */     else
/*     */     {
/* 139 */       this.version = new DERInteger(0);
/*     */     }
/*     */     
/* 142 */     this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqPos++));
/* 143 */     this.issuer = X509Name.getInstance(seq.getObjectAt(seqPos++));
/* 144 */     this.thisUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
/*     */     
/* 146 */     if ((seqPos < seq.size()) && (((seq.getObjectAt(seqPos) instanceof DERUTCTime)) || ((seq.getObjectAt(seqPos) instanceof DERGeneralizedTime)) || ((seq.getObjectAt(seqPos) instanceof Time))))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 151 */       this.nextUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
/*     */     }
/*     */     
/* 154 */     if ((seqPos < seq.size()) && (!(seq.getObjectAt(seqPos) instanceof DERTaggedObject)))
/*     */     {
/*     */ 
/* 157 */       ASN1Sequence certs = ASN1Sequence.getInstance(seq.getObjectAt(seqPos++));
/* 158 */       this.revokedCertificates = new CRLEntry[certs.size()];
/*     */       
/* 160 */       for (int i = 0; i < this.revokedCertificates.length; i++)
/*     */       {
/* 162 */         this.revokedCertificates[i] = new CRLEntry(ASN1Sequence.getInstance(certs.getObjectAt(i)));
/*     */       }
/*     */     }
/*     */     
/* 166 */     if ((seqPos < seq.size()) && ((seq.getObjectAt(seqPos) instanceof DERTaggedObject)))
/*     */     {
/*     */ 
/* 169 */       this.crlExtensions = X509Extensions.getInstance(seq.getObjectAt(seqPos++));
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/* 175 */     return this.version.getValue().intValue() + 1;
/*     */   }
/*     */   
/*     */   public DERInteger getVersionNumber()
/*     */   {
/* 180 */     return this.version;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getSignature()
/*     */   {
/* 185 */     return this.signature;
/*     */   }
/*     */   
/*     */   public X509Name getIssuer()
/*     */   {
/* 190 */     return this.issuer;
/*     */   }
/*     */   
/*     */   public Time getThisUpdate()
/*     */   {
/* 195 */     return this.thisUpdate;
/*     */   }
/*     */   
/*     */   public Time getNextUpdate()
/*     */   {
/* 200 */     return this.nextUpdate;
/*     */   }
/*     */   
/*     */   public CRLEntry[] getRevokedCertificates()
/*     */   {
/* 205 */     return this.revokedCertificates;
/*     */   }
/*     */   
/*     */   public X509Extensions getExtensions()
/*     */   {
/* 210 */     return this.crlExtensions;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 215 */     return this.seq;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/TBSCertList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */