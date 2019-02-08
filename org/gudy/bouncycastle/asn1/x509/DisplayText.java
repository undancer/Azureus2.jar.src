/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBMPString;
/*     */ import org.gudy.bouncycastle.asn1.DERIA5String;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERString;
/*     */ import org.gudy.bouncycastle.asn1.DERUTF8String;
/*     */ import org.gudy.bouncycastle.asn1.DERVisibleString;
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
/*     */ public class DisplayText
/*     */   extends ASN1Encodable
/*     */   implements ASN1Choice
/*     */ {
/*     */   public static final int CONTENT_TYPE_IA5STRING = 0;
/*     */   public static final int CONTENT_TYPE_BMPSTRING = 1;
/*     */   public static final int CONTENT_TYPE_UTF8STRING = 2;
/*     */   public static final int CONTENT_TYPE_VISIBLESTRING = 3;
/*     */   public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
/*     */   int contentType;
/*     */   DERString contents;
/*     */   
/*     */   public DisplayText(int type, String text)
/*     */   {
/*  73 */     if (text.length() > 200)
/*     */     {
/*     */ 
/*     */ 
/*  77 */       text = text.substring(0, 200);
/*     */     }
/*     */     
/*  80 */     this.contentType = type;
/*  81 */     switch (type)
/*     */     {
/*     */     case 0: 
/*  84 */       this.contents = new DERIA5String(text);
/*  85 */       break;
/*     */     case 2: 
/*  87 */       this.contents = new DERUTF8String(text);
/*  88 */       break;
/*     */     case 3: 
/*  90 */       this.contents = new DERVisibleString(text);
/*  91 */       break;
/*     */     case 1: 
/*  93 */       this.contents = new DERBMPString(text);
/*  94 */       break;
/*     */     default: 
/*  96 */       this.contents = new DERUTF8String(text);
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
/*     */   public DisplayText(String text)
/*     */   {
/* 110 */     if (text.length() > 200)
/*     */     {
/* 112 */       text = text.substring(0, 200);
/*     */     }
/*     */     
/* 115 */     this.contentType = 2;
/* 116 */     this.contents = new DERUTF8String(text);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DisplayText(DERString de)
/*     */   {
/* 128 */     this.contents = de;
/*     */   }
/*     */   
/*     */   public static DisplayText getInstance(Object de)
/*     */   {
/* 133 */     if ((de instanceof DERString))
/*     */     {
/* 135 */       return new DisplayText((DERString)de);
/*     */     }
/* 137 */     if ((de instanceof DisplayText))
/*     */     {
/* 139 */       return (DisplayText)de;
/*     */     }
/*     */     
/* 142 */     throw new IllegalArgumentException("illegal object in getInstance");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static DisplayText getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/* 149 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 154 */     return (DERObject)this.contents;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getString()
/*     */   {
/* 164 */     return this.contents.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/DisplayText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */