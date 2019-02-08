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
/*     */ public class Targets
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private ASN1Sequence targets;
/*     */   
/*     */   public static Targets getInstance(Object obj)
/*     */   {
/*  50 */     if ((obj instanceof Targets))
/*     */     {
/*  52 */       return (Targets)obj;
/*     */     }
/*  54 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  56 */       return new Targets((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  59 */     throw new IllegalArgumentException("unknown object in factory: " + obj.getClass());
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
/*     */   private Targets(ASN1Sequence targets)
/*     */   {
/*  72 */     this.targets = targets;
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
/*     */   public Targets(Target[] targets)
/*     */   {
/*  86 */     this.targets = new DERSequence(targets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Target[] getTargets()
/*     */   {
/*  98 */     Target[] targs = new Target[this.targets.size()];
/*  99 */     int count = 0;
/* 100 */     for (Enumeration e = this.targets.getObjects(); e.hasMoreElements();)
/*     */     {
/* 102 */       targs[(count++)] = Target.getInstance(e.nextElement());
/*     */     }
/* 104 */     return targs;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 120 */     return this.targets;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/Targets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */