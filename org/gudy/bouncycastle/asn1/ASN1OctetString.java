/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.util.encoders.Hex;
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
/*     */ public abstract class ASN1OctetString
/*     */   extends ASN1Object
/*     */   implements ASN1OctetStringParser
/*     */ {
/*     */   byte[] string;
/*     */   
/*     */   public static ASN1OctetString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  31 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ASN1OctetString getInstance(Object obj)
/*     */   {
/*  43 */     if ((obj == null) || ((obj instanceof ASN1OctetString)))
/*     */     {
/*  45 */       return (ASN1OctetString)obj;
/*     */     }
/*     */     
/*  48 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  50 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/*  53 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  55 */       Vector v = new Vector();
/*  56 */       Enumeration e = ((ASN1Sequence)obj).getObjects();
/*     */       
/*  58 */       while (e.hasMoreElements())
/*     */       {
/*  60 */         v.addElement(e.nextElement());
/*     */       }
/*     */       
/*  63 */       return new BERConstructedOctetString(v);
/*     */     }
/*     */     
/*  66 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ASN1OctetString(byte[] string)
/*     */   {
/*  75 */     this.string = string;
/*     */   }
/*     */   
/*     */ 
/*     */   public ASN1OctetString(DEREncodable obj)
/*     */   {
/*     */     try
/*     */     {
/*  83 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*  84 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/*  86 */       dOut.writeObject(obj);
/*  87 */       dOut.close();
/*     */       
/*  89 */       this.string = bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  93 */       throw new IllegalArgumentException("Error processing object : " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public InputStream getOctetStream()
/*     */   {
/*  99 */     return new ByteArrayInputStream(this.string);
/*     */   }
/*     */   
/*     */   public ASN1OctetStringParser parser()
/*     */   {
/* 104 */     return this;
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/* 109 */     return this.string;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 114 */     byte[] b = getOctets();
/* 115 */     int value = 0;
/*     */     
/* 117 */     for (int i = 0; i != b.length; i++)
/*     */     {
/* 119 */       value ^= (b[i] & 0xFF) << i % 4;
/*     */     }
/*     */     
/* 122 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 128 */     if (!(o instanceof ASN1OctetString))
/*     */     {
/* 130 */       return false;
/*     */     }
/*     */     
/* 133 */     ASN1OctetString other = (ASN1OctetString)o;
/*     */     
/* 135 */     byte[] b1 = other.string;
/* 136 */     byte[] b2 = this.string;
/*     */     
/* 138 */     if (b1.length != b2.length)
/*     */     {
/* 140 */       return false;
/*     */     }
/*     */     
/* 143 */     for (int i = 0; i != b1.length; i++)
/*     */     {
/* 145 */       if (b1[i] != b2[i])
/*     */       {
/* 147 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 151 */     return true;
/*     */   }
/*     */   
/*     */   abstract void encode(DEROutputStream paramDEROutputStream)
/*     */     throws IOException;
/*     */   
/*     */   public String toString()
/*     */   {
/* 159 */     return "#" + new String(Hex.encode(this.string));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1OctetString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */