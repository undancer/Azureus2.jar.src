/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class DERBMPString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERBMPString getInstance(Object obj)
/*     */   {
/*  23 */     if ((obj == null) || ((obj instanceof DERBMPString)))
/*     */     {
/*  25 */       return (DERBMPString)obj;
/*     */     }
/*     */     
/*  28 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  30 */       return new DERBMPString(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  33 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  35 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  38 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DERBMPString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  54 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERBMPString(byte[] string)
/*     */   {
/*  64 */     char[] cs = new char[string.length / 2];
/*     */     
/*  66 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/*  68 */       cs[i] = ((char)(string[(2 * i)] << 8 | string[(2 * i + 1)] & 0xFF));
/*     */     }
/*     */     
/*  71 */     this.string = new String(cs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERBMPString(String string)
/*     */   {
/*  80 */     this.string = string;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/*  85 */     return this.string;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  90 */     return this.string;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/*  95 */     return getString().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean asn1Equals(DERObject o)
/*     */   {
/* 101 */     if (!(o instanceof DERBMPString))
/*     */     {
/* 103 */       return false;
/*     */     }
/*     */     
/* 106 */     DERBMPString s = (DERBMPString)o;
/*     */     
/* 108 */     return getString().equals(s.getString());
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 115 */     char[] c = this.string.toCharArray();
/* 116 */     byte[] b = new byte[c.length * 2];
/*     */     
/* 118 */     for (int i = 0; i != c.length; i++)
/*     */     {
/* 120 */       b[(2 * i)] = ((byte)(c[i] >> '\b'));
/* 121 */       b[(2 * i + 1)] = ((byte)c[i]);
/*     */     }
/*     */     
/* 124 */     out.writeEncoded(30, b);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERBMPString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */