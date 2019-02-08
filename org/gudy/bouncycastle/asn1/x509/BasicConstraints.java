/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBoolean;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicConstraints
/*     */   extends ASN1Encodable
/*     */ {
/*  19 */   DERBoolean cA = new DERBoolean(false);
/*  20 */   DERInteger pathLenConstraint = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static BasicConstraints getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  26 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static BasicConstraints getInstance(Object obj)
/*     */   {
/*  32 */     if ((obj == null) || ((obj instanceof BasicConstraints)))
/*     */     {
/*  34 */       return (BasicConstraints)obj;
/*     */     }
/*     */     
/*  37 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  39 */       return new BasicConstraints((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  42 */     if ((obj instanceof X509Extension))
/*     */     {
/*  44 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*     */     }
/*     */     
/*  47 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicConstraints(ASN1Sequence seq)
/*     */   {
/*  53 */     if (seq.size() == 0)
/*     */     {
/*  55 */       this.cA = null;
/*  56 */       this.pathLenConstraint = null;
/*     */     }
/*     */     else
/*     */     {
/*  60 */       if ((seq.getObjectAt(0) instanceof DERBoolean))
/*     */       {
/*  62 */         this.cA = DERBoolean.getInstance(seq.getObjectAt(0));
/*     */       }
/*     */       else
/*     */       {
/*  66 */         this.cA = null;
/*  67 */         this.pathLenConstraint = DERInteger.getInstance(seq.getObjectAt(0));
/*     */       }
/*  69 */       if (seq.size() > 1)
/*     */       {
/*  71 */         if (this.cA != null)
/*     */         {
/*  73 */           this.pathLenConstraint = DERInteger.getInstance(seq.getObjectAt(1));
/*     */         }
/*     */         else
/*     */         {
/*  77 */           throw new IllegalArgumentException("wrong sequence in constructor");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public BasicConstraints(boolean cA, int pathLenConstraint)
/*     */   {
/*  92 */     if (cA)
/*     */     {
/*  94 */       this.cA = new DERBoolean(cA);
/*  95 */       this.pathLenConstraint = new DERInteger(pathLenConstraint);
/*     */     }
/*     */     else
/*     */     {
/*  99 */       this.cA = null;
/* 100 */       this.pathLenConstraint = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicConstraints(boolean cA)
/*     */   {
/* 107 */     if (cA)
/*     */     {
/* 109 */       this.cA = new DERBoolean(true);
/*     */     }
/*     */     else
/*     */     {
/* 113 */       this.cA = null;
/*     */     }
/* 115 */     this.pathLenConstraint = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BasicConstraints(int pathLenConstraint)
/*     */   {
/* 126 */     this.cA = new DERBoolean(true);
/* 127 */     this.pathLenConstraint = new DERInteger(pathLenConstraint);
/*     */   }
/*     */   
/*     */   public boolean isCA()
/*     */   {
/* 132 */     return (this.cA != null) && (this.cA.isTrue());
/*     */   }
/*     */   
/*     */   public BigInteger getPathLenConstraint()
/*     */   {
/* 137 */     if (this.pathLenConstraint != null)
/*     */     {
/* 139 */       return this.pathLenConstraint.getValue();
/*     */     }
/*     */     
/* 142 */     return null;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 156 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 158 */     if (this.cA != null)
/*     */     {
/* 160 */       v.add(this.cA);
/*     */     }
/*     */     
/* 163 */     if (this.pathLenConstraint != null)
/*     */     {
/* 165 */       v.add(this.pathLenConstraint);
/*     */     }
/*     */     
/* 168 */     return new DERSequence(v);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 173 */     if (this.pathLenConstraint == null)
/*     */     {
/* 175 */       if (this.cA == null)
/*     */       {
/* 177 */         return "BasicConstraints: isCa(false)";
/*     */       }
/* 179 */       return "BasicConstraints: isCa(" + isCA() + ")";
/*     */     }
/* 181 */     return "BasicConstraints: isCa(" + isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/BasicConstraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */