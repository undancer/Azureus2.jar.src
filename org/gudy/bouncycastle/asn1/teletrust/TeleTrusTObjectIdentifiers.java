/*    */ package org.gudy.bouncycastle.asn1.teletrust;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ 
/*    */ 
/*    */ public abstract interface TeleTrusTObjectIdentifiers
/*    */ {
/*    */   public static final String teleTrusTAlgorithm = "1.3.36.3";
/*  9 */   public static final DERObjectIdentifier ripemd160 = new DERObjectIdentifier("1.3.36.3.2.1");
/* 10 */   public static final DERObjectIdentifier ripemd128 = new DERObjectIdentifier("1.3.36.3.2.2");
/* 11 */   public static final DERObjectIdentifier ripemd256 = new DERObjectIdentifier("1.3.36.3.2.3");
/*    */   
/*    */   public static final String teleTrusTRSAsignatureAlgorithm = "1.3.36.3.3.1";
/*    */   
/* 15 */   public static final DERObjectIdentifier rsaSignatureWithripemd160 = new DERObjectIdentifier("1.3.36.3.3.1.2");
/* 16 */   public static final DERObjectIdentifier rsaSignatureWithripemd128 = new DERObjectIdentifier("1.3.36.3.3.1.3");
/* 17 */   public static final DERObjectIdentifier rsaSignatureWithripemd256 = new DERObjectIdentifier("1.3.36.3.3.1.4");
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/teletrust/TeleTrusTObjectIdentifiers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */