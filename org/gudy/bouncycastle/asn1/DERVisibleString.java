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
/*     */ public class DERVisibleString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERVisibleString getInstance(Object obj)
/*     */   {
/*  22 */     if ((obj == null) || ((obj instanceof DERVisibleString)))
/*     */     {
/*  24 */       return (DERVisibleString)obj;
/*     */     }
/*     */     
/*  27 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  29 */       return new DERVisibleString(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  32 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  34 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  37 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DERVisibleString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  53 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERVisibleString(byte[] string)
/*     */   {
/*  62 */     char[] cs = new char[string.length];
/*     */     
/*  64 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/*  66 */       cs[i] = ((char)(string[i] & 0xFF));
/*     */     }
/*     */     
/*  69 */     this.string = new String(cs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERVisibleString(String string)
/*     */   {
/*  78 */     this.string = string;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/*  83 */     return this.string;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  88 */     return this.string;
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/*  93 */     char[] cs = this.string.toCharArray();
/*  94 */     byte[] bs = new byte[cs.length];
/*     */     
/*  96 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/*  98 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 101 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 108 */     out.writeEncoded(26, getOctets());
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 114 */     if (!(o instanceof DERVisibleString))
/*     */     {
/* 116 */       return false;
/*     */     }
/*     */     
/* 119 */     return getString().equals(((DERVisibleString)o).getString());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 124 */     return getString().hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERVisibleString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */