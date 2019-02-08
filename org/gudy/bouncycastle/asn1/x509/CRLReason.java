/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.DEREnumerated;
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
/*     */ public class CRLReason
/*     */   extends DEREnumerated
/*     */ {
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int UNSPECIFIED = 0;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int KEY_COMPROMISE = 1;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int CA_COMPROMISE = 2;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int AFFILIATION_CHANGED = 3;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int SUPERSEDED = 4;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int CESSATION_OF_OPERATION = 5;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int CERTIFICATE_HOLD = 6;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int REMOVE_FROM_CRL = 8;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int PRIVILEGE_WITHDRAWN = 9;
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static final int AA_COMPROMISE = 10;
/*     */   public static final int unspecified = 0;
/*     */   public static final int keyCompromise = 1;
/*     */   public static final int cACompromise = 2;
/*     */   public static final int affiliationChanged = 3;
/*     */   public static final int superseded = 4;
/*     */   public static final int cessationOfOperation = 5;
/*     */   public static final int certificateHold = 6;
/*     */   public static final int removeFromCRL = 8;
/*     */   public static final int privilegeWithdrawn = 9;
/*     */   public static final int aACompromise = 10;
/*  78 */   private static final String[] reasonString = { "unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLReason(int reason)
/*     */   {
/*  88 */     super(reason);
/*     */   }
/*     */   
/*     */ 
/*     */   public CRLReason(DEREnumerated reason)
/*     */   {
/*  94 */     super(reason.getValue().intValue());
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 100 */     int reason = getValue().intValue();
/* 101 */     String str; String str; if ((reason < 0) || (reason > 10))
/*     */     {
/* 103 */       str = "invalid";
/*     */     }
/*     */     else
/*     */     {
/* 107 */       str = reasonString[reason];
/*     */     }
/* 109 */     return "CRLReason: " + str;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CRLReason.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */