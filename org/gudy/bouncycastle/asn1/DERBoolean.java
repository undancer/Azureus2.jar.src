/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ public class DERBoolean
/*     */   extends ASN1Object
/*     */ {
/*     */   byte value;
/*  10 */   public static final DERBoolean FALSE = new DERBoolean(false);
/*  11 */   public static final DERBoolean TRUE = new DERBoolean(true);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERBoolean getInstance(Object obj)
/*     */   {
/*  21 */     if ((obj == null) || ((obj instanceof DERBoolean)))
/*     */     {
/*  23 */       return (DERBoolean)obj;
/*     */     }
/*     */     
/*  26 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  28 */       return new DERBoolean(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  31 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  33 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  36 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERBoolean getInstance(boolean value)
/*     */   {
/*  45 */     return value ? TRUE : FALSE;
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
/*     */   public static DERBoolean getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  61 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */   public DERBoolean(byte[] value)
/*     */   {
/*  67 */     this.value = value[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public DERBoolean(boolean value)
/*     */   {
/*  73 */     this.value = (value ? -1 : 0);
/*     */   }
/*     */   
/*     */   public boolean isTrue()
/*     */   {
/*  78 */     return this.value != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/*  85 */     byte[] bytes = new byte[1];
/*     */     
/*  87 */     bytes[0] = this.value;
/*     */     
/*  89 */     out.writeEncoded(1, bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean asn1Equals(DERObject o)
/*     */   {
/*  95 */     if ((o == null) || (!(o instanceof DERBoolean)))
/*     */     {
/*  97 */       return false;
/*     */     }
/*     */     
/* 100 */     return this.value == ((DERBoolean)o).value;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 105 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 111 */     return this.value != 0 ? "TRUE" : "FALSE";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */