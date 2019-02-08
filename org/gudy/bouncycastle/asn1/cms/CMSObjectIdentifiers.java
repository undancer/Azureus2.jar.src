/*    */ package org.gudy.bouncycastle.asn1.cms;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*    */ 
/*    */ public abstract interface CMSObjectIdentifiers
/*    */ {
/*  8 */   public static final DERObjectIdentifier data = PKCSObjectIdentifiers.data;
/*  9 */   public static final DERObjectIdentifier signedData = PKCSObjectIdentifiers.signedData;
/* 10 */   public static final DERObjectIdentifier envelopedData = PKCSObjectIdentifiers.envelopedData;
/* 11 */   public static final DERObjectIdentifier signedAndEnvelopedData = PKCSObjectIdentifiers.signedAndEnvelopedData;
/* 12 */   public static final DERObjectIdentifier digestedData = PKCSObjectIdentifiers.digestedData;
/* 13 */   public static final DERObjectIdentifier encryptedData = PKCSObjectIdentifiers.encryptedData;
/* 14 */   public static final DERObjectIdentifier compressedData = PKCSObjectIdentifiers.id_ct_compressedData;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/cms/CMSObjectIdentifiers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */