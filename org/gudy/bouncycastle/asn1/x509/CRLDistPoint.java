/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CRLDistPoint
/*     */   extends ASN1Encodable
/*     */ {
/*  15 */   ASN1Sequence seq = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static CRLDistPoint getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  21 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static CRLDistPoint getInstance(Object obj)
/*     */   {
/*  27 */     if (((obj instanceof CRLDistPoint)) || (obj == null))
/*     */     {
/*  29 */       return (CRLDistPoint)obj;
/*     */     }
/*  31 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  33 */       return new CRLDistPoint((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  36 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public CRLDistPoint(ASN1Sequence seq)
/*     */   {
/*  42 */     this.seq = seq;
/*     */   }
/*     */   
/*     */ 
/*     */   public CRLDistPoint(DistributionPoint[] points)
/*     */   {
/*  48 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  50 */     for (int i = 0; i != points.length; i++)
/*     */     {
/*  52 */       v.add(points[i]);
/*     */     }
/*     */     
/*  55 */     this.seq = new DERSequence(v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPoint[] getDistributionPoints()
/*     */   {
/*  65 */     DistributionPoint[] dp = new DistributionPoint[this.seq.size()];
/*     */     
/*  67 */     for (int i = 0; i != this.seq.size(); i++)
/*     */     {
/*  69 */       dp[i] = DistributionPoint.getInstance(this.seq.getObjectAt(i));
/*     */     }
/*     */     
/*  72 */     return dp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/*  83 */     return this.seq;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  88 */     StringBuilder buf = new StringBuilder();
/*  89 */     String sep = System.getProperty("line.separator");
/*     */     
/*  91 */     buf.append("CRLDistPoint:");
/*  92 */     buf.append(sep);
/*  93 */     DistributionPoint[] dp = getDistributionPoints();
/*  94 */     for (int i = 0; i != dp.length; i++)
/*     */     {
/*  96 */       buf.append("    ");
/*  97 */       buf.append(dp[i]);
/*  98 */       buf.append(sep);
/*     */     }
/* 100 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CRLDistPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */