/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
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
/*     */ public class AuthorityInformationAccess
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private AccessDescription[] descriptions;
/*     */   
/*     */   public static AuthorityInformationAccess getInstance(Object obj)
/*     */   {
/*  36 */     if ((obj instanceof AuthorityInformationAccess))
/*     */     {
/*  38 */       return (AuthorityInformationAccess)obj;
/*     */     }
/*     */     
/*  41 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  43 */       return new AuthorityInformationAccess((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  46 */     if ((obj instanceof X509Extension))
/*     */     {
/*  48 */       return getInstance(X509Extension.convertValueToObject((X509Extension)obj));
/*     */     }
/*     */     
/*  51 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public AuthorityInformationAccess(ASN1Sequence seq)
/*     */   {
/*  57 */     this.descriptions = new AccessDescription[seq.size()];
/*     */     
/*  59 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/*  61 */       this.descriptions[i] = AccessDescription.getInstance(seq.getObjectAt(i));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthorityInformationAccess(DERObjectIdentifier oid, GeneralName location)
/*     */   {
/*  72 */     this.descriptions = new AccessDescription[1];
/*     */     
/*  74 */     this.descriptions[0] = new AccessDescription(oid, location);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AccessDescription[] getAccessDescriptions()
/*     */   {
/*  84 */     return this.descriptions;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/*  89 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/*     */     
/*  91 */     for (int i = 0; i != this.descriptions.length; i++)
/*     */     {
/*  93 */       vec.add(this.descriptions[i]);
/*     */     }
/*     */     
/*  96 */     return new DERSequence(vec);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 101 */     return "AuthorityInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AuthorityInformationAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */