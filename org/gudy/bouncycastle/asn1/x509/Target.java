/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Target
/*     */   extends ASN1Encodable
/*     */   implements ASN1Choice
/*     */ {
/*     */   public static final int targetName = 0;
/*     */   public static final int targetGroup = 1;
/*     */   private GeneralName targName;
/*     */   private GeneralName targGroup;
/*     */   
/*     */   public static Target getInstance(Object obj)
/*     */   {
/*  48 */     if ((obj instanceof Target))
/*     */     {
/*  50 */       return (Target)obj;
/*     */     }
/*  52 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  54 */       return new Target((ASN1TaggedObject)obj);
/*     */     }
/*     */     
/*  57 */     throw new IllegalArgumentException("unknown object in factory: " + obj.getClass());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Target(ASN1TaggedObject tagObj)
/*     */   {
/*  69 */     switch (tagObj.getTagNo())
/*     */     {
/*     */     case 0: 
/*  72 */       this.targName = GeneralName.getInstance(tagObj, true);
/*  73 */       break;
/*     */     case 1: 
/*  75 */       this.targGroup = GeneralName.getInstance(tagObj, true);
/*  76 */       break;
/*     */     default: 
/*  78 */       throw new IllegalArgumentException("unknown tag: " + tagObj.getTagNo());
/*     */     }
/*     */     
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
/*     */   public Target(int type, GeneralName name)
/*     */   {
/*  93 */     this(new DERTaggedObject(type, name));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralName getTargetGroup()
/*     */   {
/* 101 */     return this.targGroup;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralName getTargetName()
/*     */   {
/* 109 */     return this.targName;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 130 */     if (this.targName != null)
/*     */     {
/* 132 */       return new DERTaggedObject(true, 0, this.targName);
/*     */     }
/*     */     
/*     */ 
/* 136 */     return new DERTaggedObject(true, 1, this.targGroup);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/Target.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */