/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DEREnumerated
/*     */   extends ASN1Object
/*     */ {
/*     */   byte[] bytes;
/*     */   
/*     */   public static DEREnumerated getInstance(Object obj)
/*     */   {
/*  19 */     if ((obj == null) || ((obj instanceof DEREnumerated)))
/*     */     {
/*  21 */       return (DEREnumerated)obj;
/*     */     }
/*     */     
/*  24 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  26 */       return new DEREnumerated(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  29 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  31 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  34 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DEREnumerated getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  50 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */   public DEREnumerated(int value)
/*     */   {
/*  56 */     this.bytes = BigInteger.valueOf(value).toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public DEREnumerated(BigInteger value)
/*     */   {
/*  62 */     this.bytes = value.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public DEREnumerated(byte[] bytes)
/*     */   {
/*  68 */     this.bytes = bytes;
/*     */   }
/*     */   
/*     */   public BigInteger getValue()
/*     */   {
/*  73 */     return new BigInteger(this.bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/*  80 */     out.writeEncoded(10, this.bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/*  86 */     if (!(o instanceof DEREnumerated))
/*     */     {
/*  88 */       return false;
/*     */     }
/*     */     
/*  91 */     DEREnumerated other = (DEREnumerated)o;
/*     */     
/*  93 */     if (this.bytes.length != other.bytes.length)
/*     */     {
/*  95 */       return false;
/*     */     }
/*     */     
/*  98 */     for (int i = 0; i != this.bytes.length; i++)
/*     */     {
/* 100 */       if (this.bytes[i] != other.bytes[i])
/*     */       {
/* 102 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 106 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 111 */     return getValue().hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DEREnumerated.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */