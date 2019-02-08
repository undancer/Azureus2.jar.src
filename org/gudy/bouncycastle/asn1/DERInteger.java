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
/*     */ public class DERInteger
/*     */   extends ASN1Object
/*     */ {
/*     */   byte[] bytes;
/*     */   
/*     */   public static DERInteger getInstance(Object obj)
/*     */   {
/*  19 */     if ((obj == null) || ((obj instanceof DERInteger)))
/*     */     {
/*  21 */       return (DERInteger)obj;
/*     */     }
/*     */     
/*  24 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  26 */       return new DERInteger(((ASN1OctetString)obj).getOctets());
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
/*     */   public static DERInteger getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  50 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */   public DERInteger(int value)
/*     */   {
/*  56 */     this.bytes = BigInteger.valueOf(value).toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public DERInteger(BigInteger value)
/*     */   {
/*  62 */     this.bytes = value.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public DERInteger(byte[] bytes)
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getPositiveValue()
/*     */   {
/*  82 */     return new BigInteger(1, this.bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/*  89 */     out.writeEncoded(2, this.bytes);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/*  94 */     int value = 0;
/*     */     
/*  96 */     for (int i = 0; i != this.bytes.length; i++)
/*     */     {
/*  98 */       value ^= (this.bytes[i] & 0xFF) << i % 4;
/*     */     }
/*     */     
/* 101 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 107 */     if (!(o instanceof DERInteger))
/*     */     {
/* 109 */       return false;
/*     */     }
/*     */     
/* 112 */     DERInteger other = (DERInteger)o;
/*     */     
/* 114 */     if (this.bytes.length != other.bytes.length)
/*     */     {
/* 116 */       return false;
/*     */     }
/*     */     
/* 119 */     for (int i = 0; i != this.bytes.length; i++)
/*     */     {
/* 121 */       if (this.bytes[i] != other.bytes[i])
/*     */       {
/* 123 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 127 */     return true;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 132 */     return getValue().toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERInteger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */