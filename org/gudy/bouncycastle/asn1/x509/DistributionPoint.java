/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
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
/*     */ public class DistributionPoint
/*     */   extends ASN1Encodable
/*     */ {
/*     */   DistributionPointName distributionPoint;
/*     */   ReasonFlags reasons;
/*     */   GeneralNames cRLIssuer;
/*     */   
/*     */   public static DistributionPoint getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  37 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static DistributionPoint getInstance(Object obj)
/*     */   {
/*  43 */     if ((obj == null) || ((obj instanceof DistributionPoint)))
/*     */     {
/*  45 */       return (DistributionPoint)obj;
/*     */     }
/*     */     
/*  48 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  50 */       return new DistributionPoint((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  53 */     throw new IllegalArgumentException("Invalid DistributionPoint: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */   public DistributionPoint(ASN1Sequence seq)
/*     */   {
/*  59 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/*  61 */       ASN1TaggedObject t = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
/*  62 */       switch (t.getTagNo())
/*     */       {
/*     */       case 0: 
/*  65 */         this.distributionPoint = DistributionPointName.getInstance(t, true);
/*  66 */         break;
/*     */       case 1: 
/*  68 */         this.reasons = new ReasonFlags(DERBitString.getInstance(t, false));
/*  69 */         break;
/*     */       case 2: 
/*  71 */         this.cRLIssuer = GeneralNames.getInstance(t, false);
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DistributionPoint(DistributionPointName distributionPoint, ReasonFlags reasons, GeneralNames cRLIssuer)
/*     */   {
/*  81 */     this.distributionPoint = distributionPoint;
/*  82 */     this.reasons = reasons;
/*  83 */     this.cRLIssuer = cRLIssuer;
/*     */   }
/*     */   
/*     */   public DistributionPointName getDistributionPoint()
/*     */   {
/*  88 */     return this.distributionPoint;
/*     */   }
/*     */   
/*     */   public ReasonFlags getReasons()
/*     */   {
/*  93 */     return this.reasons;
/*     */   }
/*     */   
/*     */   public GeneralNames getCRLIssuer()
/*     */   {
/*  98 */     return this.cRLIssuer;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 103 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 105 */     if (this.distributionPoint != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 110 */       v.add(new DERTaggedObject(0, this.distributionPoint));
/*     */     }
/*     */     
/* 113 */     if (this.reasons != null)
/*     */     {
/* 115 */       v.add(new DERTaggedObject(false, 1, this.reasons));
/*     */     }
/*     */     
/* 118 */     if (this.cRLIssuer != null)
/*     */     {
/* 120 */       v.add(new DERTaggedObject(false, 2, this.cRLIssuer));
/*     */     }
/*     */     
/* 123 */     return new DERSequence(v);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 128 */     String sep = System.getProperty("line.separator");
/* 129 */     StringBuffer buf = new StringBuffer();
/* 130 */     buf.append("DistributionPoint: [");
/* 131 */     buf.append(sep);
/* 132 */     if (this.distributionPoint != null)
/*     */     {
/* 134 */       appendObject(buf, sep, "distributionPoint", this.distributionPoint.toString());
/*     */     }
/* 136 */     if (this.reasons != null)
/*     */     {
/* 138 */       appendObject(buf, sep, "reasons", this.reasons.toString());
/*     */     }
/* 140 */     if (this.cRLIssuer != null)
/*     */     {
/* 142 */       appendObject(buf, sep, "cRLIssuer", this.cRLIssuer.toString());
/*     */     }
/* 144 */     buf.append("]");
/* 145 */     buf.append(sep);
/* 146 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void appendObject(StringBuffer buf, String sep, String name, String value)
/*     */   {
/* 151 */     String indent = "    ";
/*     */     
/* 153 */     buf.append(indent);
/* 154 */     buf.append(name);
/* 155 */     buf.append(":");
/* 156 */     buf.append(sep);
/* 157 */     buf.append(indent);
/* 158 */     buf.append(indent);
/* 159 */     buf.append(value);
/* 160 */     buf.append(sep);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/DistributionPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */