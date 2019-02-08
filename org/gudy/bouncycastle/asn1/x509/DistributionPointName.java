/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
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
/*     */ public class DistributionPointName
/*     */   extends ASN1Encodable
/*     */   implements ASN1Choice
/*     */ {
/*     */   DEREncodable name;
/*     */   int type;
/*     */   public static final int FULL_NAME = 0;
/*     */   public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
/*     */   
/*     */   public static DistributionPointName getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  36 */     return getInstance(ASN1TaggedObject.getInstance(obj, true));
/*     */   }
/*     */   
/*     */ 
/*     */   public static DistributionPointName getInstance(Object obj)
/*     */   {
/*  42 */     if ((obj == null) || ((obj instanceof DistributionPointName)))
/*     */     {
/*  44 */       return (DistributionPointName)obj;
/*     */     }
/*  46 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  48 */       return new DistributionPointName((ASN1TaggedObject)obj);
/*     */     }
/*     */     
/*  51 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPointName(int type, DEREncodable name)
/*     */   {
/*  61 */     this.type = type;
/*  62 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DistributionPointName(int type, ASN1Encodable name)
/*     */   {
/*  69 */     this.type = type;
/*  70 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getType()
/*     */   {
/*  80 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ASN1Encodable getName()
/*     */   {
/*  90 */     return (ASN1Encodable)this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public DistributionPointName(ASN1TaggedObject obj)
/*     */   {
/*  96 */     this.type = obj.getTagNo();
/*     */     
/*  98 */     if (this.type == 0)
/*     */     {
/* 100 */       this.name = GeneralNames.getInstance(obj, false);
/*     */     }
/*     */     else
/*     */     {
/* 104 */       this.name = ASN1Set.getInstance(obj, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 110 */     return new DERTaggedObject(false, this.type, this.name);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 115 */     String sep = System.getProperty("line.separator");
/* 116 */     StringBuffer buf = new StringBuffer();
/* 117 */     buf.append("DistributionPointName: [");
/* 118 */     buf.append(sep);
/* 119 */     if (this.type == 0)
/*     */     {
/* 121 */       appendObject(buf, sep, "fullName", this.name.toString());
/*     */     }
/*     */     else
/*     */     {
/* 125 */       appendObject(buf, sep, "nameRelativeToCRLIssuer", this.name.toString());
/*     */     }
/* 127 */     buf.append("]");
/* 128 */     buf.append(sep);
/* 129 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void appendObject(StringBuffer buf, String sep, String name, String value)
/*     */   {
/* 134 */     String indent = "    ";
/*     */     
/* 136 */     buf.append(indent);
/* 137 */     buf.append(name);
/* 138 */     buf.append(":");
/* 139 */     buf.append(sep);
/* 140 */     buf.append(indent);
/* 141 */     buf.append(indent);
/* 142 */     buf.append(value);
/* 143 */     buf.append(sep);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/DistributionPointName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */