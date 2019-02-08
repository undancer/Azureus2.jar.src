/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TargetInformation
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private ASN1Sequence targets;
/*     */   
/*     */   public static TargetInformation getInstance(Object obj)
/*     */   {
/*  36 */     if ((obj instanceof TargetInformation))
/*     */     {
/*  38 */       return (TargetInformation)obj;
/*     */     }
/*  40 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  42 */       return new TargetInformation((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  45 */     throw new IllegalArgumentException("unknown object in factory: " + obj.getClass());
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
/*     */   private TargetInformation(ASN1Sequence seq)
/*     */   {
/*  58 */     this.targets = seq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Targets[] getTargetsObjects()
/*     */   {
/*  68 */     Targets[] copy = new Targets[this.targets.size()];
/*  69 */     int count = 0;
/*  70 */     for (Enumeration e = this.targets.getObjects(); e.hasMoreElements();)
/*     */     {
/*  72 */       copy[(count++)] = Targets.getInstance(e.nextElement());
/*     */     }
/*  74 */     return copy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TargetInformation(Targets targets)
/*     */   {
/*  85 */     this.targets = new DERSequence(targets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TargetInformation(Target[] targets)
/*     */   {
/*  97 */     this(new Targets(targets));
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
/* 119 */     return this.targets;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/TargetInformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */