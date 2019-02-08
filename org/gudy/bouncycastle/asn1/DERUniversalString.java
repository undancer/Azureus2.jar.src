/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DERUniversalString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*  13 */   private static final char[] table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] string;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERUniversalString getInstance(Object obj)
/*     */   {
/*  24 */     if ((obj == null) || ((obj instanceof DERUniversalString)))
/*     */     {
/*  26 */       return (DERUniversalString)obj;
/*     */     }
/*     */     
/*  29 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  31 */       return new DERUniversalString(((ASN1OctetString)obj).getOctets());
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
/*     */   public static DERUniversalString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  50 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERUniversalString(byte[] string)
/*     */   {
/*  59 */     this.string = string;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/*  64 */     StringBuilder buf = new StringBuilder("#");
/*  65 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*  66 */     ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/*  70 */       aOut.writeObject(this);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  74 */       throw new RuntimeException("internal error encoding BitString");
/*     */     }
/*     */     
/*  77 */     byte[] string = bOut.toByteArray();
/*     */     
/*  79 */     for (int i = 0; i != string.length; i++)
/*     */     {
/*  81 */       buf.append(table[(string[i] >>> 4 & 0xF)]);
/*  82 */       buf.append(table[(string[i] & 0xF)]);
/*     */     }
/*     */     
/*  85 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  90 */     return getString();
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/*  95 */     return this.string;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 102 */     out.writeEncoded(28, getOctets());
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 108 */     if (!(o instanceof DERUniversalString))
/*     */     {
/* 110 */       return false;
/*     */     }
/*     */     
/* 113 */     return getString().equals(((DERUniversalString)o).getString());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 118 */     return getString().hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERUniversalString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */