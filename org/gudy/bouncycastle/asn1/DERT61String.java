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
/*     */ public class DERT61String
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERT61String getInstance(Object obj)
/*     */   {
/*  22 */     if ((obj == null) || ((obj instanceof DERT61String)))
/*     */     {
/*  24 */       return (DERT61String)obj;
/*     */     }
/*     */     
/*  27 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  29 */       return new DERT61String(((ASN1OctetString)obj).getOctets());
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
/*     */   public static DERT61String getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  53 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERT61String(byte[] string)
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
/*     */   public DERT61String(String string)
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
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/*  95 */     out.writeEncoded(20, getOctets());
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/* 100 */     char[] cs = this.string.toCharArray();
/* 101 */     byte[] bs = new byte[cs.length];
/*     */     
/* 103 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/* 105 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 108 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 114 */     if (!(o instanceof DERT61String))
/*     */     {
/* 116 */       return false;
/*     */     }
/*     */     
/* 119 */     return getString().equals(((DERT61String)o).getString());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 124 */     return getString().hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERT61String.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */