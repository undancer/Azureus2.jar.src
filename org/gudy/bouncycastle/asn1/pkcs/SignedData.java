/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.BERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignedData
/*     */   implements DEREncodable, PKCSObjectIdentifiers
/*     */ {
/*     */   private DERInteger version;
/*     */   private ASN1Set digestAlgorithms;
/*     */   private ContentInfo contentInfo;
/*     */   private ASN1Set certificates;
/*     */   private ASN1Set crls;
/*     */   private ASN1Set signerInfos;
/*     */   
/*     */   public static SignedData getInstance(Object o)
/*     */   {
/*  30 */     if ((o instanceof SignedData))
/*     */     {
/*  32 */       return (SignedData)o;
/*     */     }
/*  34 */     if ((o instanceof ASN1Sequence))
/*     */     {
/*  36 */       return new SignedData((ASN1Sequence)o);
/*     */     }
/*     */     
/*  39 */     throw new IllegalArgumentException("unknown object in factory: " + o);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignedData(DERInteger _version, ASN1Set _digestAlgorithms, ContentInfo _contentInfo, ASN1Set _certificates, ASN1Set _crls, ASN1Set _signerInfos)
/*     */   {
/*  50 */     this.version = _version;
/*  51 */     this.digestAlgorithms = _digestAlgorithms;
/*  52 */     this.contentInfo = _contentInfo;
/*  53 */     this.certificates = _certificates;
/*  54 */     this.crls = _crls;
/*  55 */     this.signerInfos = _signerInfos;
/*     */   }
/*     */   
/*     */ 
/*     */   public SignedData(ASN1Sequence seq)
/*     */   {
/*  61 */     Enumeration e = seq.getObjects();
/*     */     
/*  63 */     this.version = ((DERInteger)e.nextElement());
/*  64 */     this.digestAlgorithms = ((ASN1Set)e.nextElement());
/*  65 */     this.contentInfo = ContentInfo.getInstance(e.nextElement());
/*     */     
/*  67 */     while (e.hasMoreElements())
/*     */     {
/*  69 */       DERObject o = (DERObject)e.nextElement();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */       if ((o instanceof DERTaggedObject))
/*     */       {
/*  77 */         DERTaggedObject tagged = (DERTaggedObject)o;
/*     */         
/*  79 */         switch (tagged.getTagNo())
/*     */         {
/*     */         case 0: 
/*  82 */           this.certificates = ASN1Set.getInstance(tagged, false);
/*  83 */           break;
/*     */         case 1: 
/*  85 */           this.crls = ASN1Set.getInstance(tagged, false);
/*  86 */           break;
/*     */         default: 
/*  88 */           throw new IllegalArgumentException("unknown tag value " + tagged.getTagNo());
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  93 */         this.signerInfos = ((ASN1Set)o);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public DERInteger getVersion()
/*     */   {
/* 100 */     return this.version;
/*     */   }
/*     */   
/*     */   public ASN1Set getDigestAlgorithms()
/*     */   {
/* 105 */     return this.digestAlgorithms;
/*     */   }
/*     */   
/*     */   public ContentInfo getContentInfo()
/*     */   {
/* 110 */     return this.contentInfo;
/*     */   }
/*     */   
/*     */   public ASN1Set getCertificates()
/*     */   {
/* 115 */     return this.certificates;
/*     */   }
/*     */   
/*     */   public ASN1Set getCRLs()
/*     */   {
/* 120 */     return this.crls;
/*     */   }
/*     */   
/*     */   public ASN1Set getSignerInfos()
/*     */   {
/* 125 */     return this.signerInfos;
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
/*     */   public DERObject getDERObject()
/*     */   {
/* 145 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 147 */     v.add(this.version);
/* 148 */     v.add(this.digestAlgorithms);
/* 149 */     v.add(this.contentInfo);
/*     */     
/* 151 */     if (this.certificates != null)
/*     */     {
/* 153 */       v.add(new DERTaggedObject(false, 0, this.certificates));
/*     */     }
/*     */     
/* 156 */     if (this.crls != null)
/*     */     {
/* 158 */       v.add(new DERTaggedObject(false, 1, this.crls));
/*     */     }
/*     */     
/* 161 */     v.add(this.signerInfos);
/*     */     
/* 163 */     return new BERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/SignedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */