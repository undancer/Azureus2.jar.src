/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBoolean;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IssuingDistributionPoint
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DistributionPointName distributionPoint;
/*     */   private boolean onlyContainsUserCerts;
/*     */   private boolean onlyContainsCACerts;
/*     */   private ReasonFlags onlySomeReasons;
/*     */   private boolean indirectCRL;
/*     */   private boolean onlyContainsAttributeCerts;
/*     */   private ASN1Sequence seq;
/*     */   
/*     */   public static IssuingDistributionPoint getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  47 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static IssuingDistributionPoint getInstance(Object obj)
/*     */   {
/*  53 */     if ((obj == null) || ((obj instanceof IssuingDistributionPoint)))
/*     */     {
/*  55 */       return (IssuingDistributionPoint)obj;
/*     */     }
/*  57 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  59 */       return new IssuingDistributionPoint((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  62 */     throw new IllegalArgumentException("unknown object in factory");
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
/*     */   public IssuingDistributionPoint(DistributionPointName distributionPoint, boolean onlyContainsUserCerts, boolean onlyContainsCACerts, ReasonFlags onlySomeReasons, boolean indirectCRL, boolean onlyContainsAttributeCerts)
/*     */   {
/*  88 */     this.distributionPoint = distributionPoint;
/*  89 */     this.indirectCRL = indirectCRL;
/*  90 */     this.onlyContainsAttributeCerts = onlyContainsAttributeCerts;
/*  91 */     this.onlyContainsCACerts = onlyContainsCACerts;
/*  92 */     this.onlyContainsUserCerts = onlyContainsUserCerts;
/*  93 */     this.onlySomeReasons = onlySomeReasons;
/*     */     
/*  95 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/*  96 */     if (distributionPoint != null)
/*     */     {
/*  98 */       vec.add(new DERTaggedObject(true, 0, distributionPoint));
/*     */     }
/* 100 */     if (!onlyContainsUserCerts)
/*     */     {
/* 102 */       vec.add(new DERTaggedObject(false, 1, new DERBoolean(true)));
/*     */     }
/* 104 */     if (!onlyContainsCACerts)
/*     */     {
/* 106 */       vec.add(new DERTaggedObject(false, 2, new DERBoolean(true)));
/*     */     }
/* 108 */     if (onlySomeReasons != null)
/*     */     {
/* 110 */       vec.add(new DERTaggedObject(false, 3, onlySomeReasons));
/*     */     }
/* 112 */     if (!indirectCRL)
/*     */     {
/* 114 */       vec.add(new DERTaggedObject(false, 4, new DERBoolean(true)));
/*     */     }
/* 116 */     if (!onlyContainsAttributeCerts)
/*     */     {
/* 118 */       vec.add(new DERTaggedObject(false, 5, new DERBoolean(true)));
/*     */     }
/*     */     
/* 121 */     this.seq = new DERSequence(vec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IssuingDistributionPoint(ASN1Sequence seq)
/*     */   {
/* 130 */     this.seq = seq;
/*     */     
/* 132 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/* 134 */       ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
/*     */       
/* 136 */       switch (o.getTagNo())
/*     */       {
/*     */ 
/*     */       case 0: 
/* 140 */         this.distributionPoint = DistributionPointName.getInstance(o, true);
/* 141 */         break;
/*     */       case 1: 
/* 143 */         this.onlyContainsUserCerts = DERBoolean.getInstance(o, false).isTrue();
/* 144 */         break;
/*     */       case 2: 
/* 146 */         this.onlyContainsCACerts = DERBoolean.getInstance(o, false).isTrue();
/* 147 */         break;
/*     */       case 3: 
/* 149 */         this.onlySomeReasons = new ReasonFlags(ReasonFlags.getInstance(o, false));
/* 150 */         break;
/*     */       case 4: 
/* 152 */         this.indirectCRL = DERBoolean.getInstance(o, false).isTrue();
/* 153 */         break;
/*     */       case 5: 
/* 155 */         this.onlyContainsAttributeCerts = DERBoolean.getInstance(o, false).isTrue();
/* 156 */         break;
/*     */       default: 
/* 158 */         throw new IllegalArgumentException("unknown tag in IssuingDistributionPoint");
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean onlyContainsUserCerts()
/*     */   {
/* 166 */     return this.onlyContainsUserCerts;
/*     */   }
/*     */   
/*     */   public boolean onlyContainsCACerts()
/*     */   {
/* 171 */     return this.onlyContainsCACerts;
/*     */   }
/*     */   
/*     */   public boolean isIndirectCRL()
/*     */   {
/* 176 */     return this.indirectCRL;
/*     */   }
/*     */   
/*     */   public boolean onlyContainsAttributeCerts()
/*     */   {
/* 181 */     return this.onlyContainsAttributeCerts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPointName getDistributionPoint()
/*     */   {
/* 189 */     return this.distributionPoint;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ReasonFlags getOnlySomeReasons()
/*     */   {
/* 197 */     return this.onlySomeReasons;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 202 */     return this.seq;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 207 */     String sep = System.getProperty("line.separator");
/* 208 */     StringBuffer buf = new StringBuffer();
/*     */     
/* 210 */     buf.append("IssuingDistributionPoint: [");
/* 211 */     buf.append(sep);
/* 212 */     if (this.distributionPoint != null)
/*     */     {
/* 214 */       appendObject(buf, sep, "distributionPoint", this.distributionPoint.toString());
/*     */     }
/* 216 */     if (this.onlyContainsUserCerts)
/*     */     {
/* 218 */       appendObject(buf, sep, "onlyContainsUserCerts", booleanToString(this.onlyContainsUserCerts));
/*     */     }
/* 220 */     if (this.onlyContainsCACerts)
/*     */     {
/* 222 */       appendObject(buf, sep, "onlyContainsCACerts", booleanToString(this.onlyContainsCACerts));
/*     */     }
/* 224 */     if (this.onlySomeReasons != null)
/*     */     {
/* 226 */       appendObject(buf, sep, "onlySomeReasons", this.onlySomeReasons.toString());
/*     */     }
/* 228 */     if (this.onlyContainsAttributeCerts)
/*     */     {
/* 230 */       appendObject(buf, sep, "onlyContainsAttributeCerts", booleanToString(this.onlyContainsAttributeCerts));
/*     */     }
/* 232 */     if (this.indirectCRL)
/*     */     {
/* 234 */       appendObject(buf, sep, "indirectCRL", booleanToString(this.indirectCRL));
/*     */     }
/* 236 */     buf.append("]");
/* 237 */     buf.append(sep);
/* 238 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void appendObject(StringBuffer buf, String sep, String name, String value)
/*     */   {
/* 243 */     String indent = "    ";
/*     */     
/* 245 */     buf.append(indent);
/* 246 */     buf.append(name);
/* 247 */     buf.append(":");
/* 248 */     buf.append(sep);
/* 249 */     buf.append(indent);
/* 250 */     buf.append(indent);
/* 251 */     buf.append(value);
/* 252 */     buf.append(sep);
/*     */   }
/*     */   
/*     */   private String booleanToString(boolean value)
/*     */   {
/* 257 */     return value ? "true" : "false";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/IssuingDistributionPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */