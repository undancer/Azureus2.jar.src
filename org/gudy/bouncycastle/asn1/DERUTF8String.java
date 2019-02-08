/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.gudy.bouncycastle.util.Strings;
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
/*     */ public class DERUTF8String
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERUTF8String getInstance(Object obj)
/*     */   {
/*  24 */     if ((obj == null) || ((obj instanceof DERUTF8String)))
/*     */     {
/*  26 */       return (DERUTF8String)obj;
/*     */     }
/*     */     
/*  29 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  31 */       return new DERUTF8String(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  34 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  36 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  39 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERUTF8String getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  58 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   DERUTF8String(byte[] string)
/*     */   {
/*  66 */     this.string = Strings.fromUTF8ByteArray(string);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERUTF8String(String string)
/*     */   {
/*  74 */     this.string = string;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/*  79 */     return this.string;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  84 */     return this.string;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/*  89 */     return getString().hashCode();
/*     */   }
/*     */   
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/*  94 */     if (!(o instanceof DERUTF8String))
/*     */     {
/*  96 */       return false;
/*     */     }
/*     */     
/*  99 */     DERUTF8String s = (DERUTF8String)o;
/*     */     
/* 101 */     return getString().equals(s.getString());
/*     */   }
/*     */   
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 107 */     out.writeEncoded(12, Strings.toUTF8ByteArray(this.string));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERUTF8String.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */