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
/*     */ public class DERNumericString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERNumericString getInstance(Object obj)
/*     */   {
/*  22 */     if ((obj == null) || ((obj instanceof DERNumericString)))
/*     */     {
/*  24 */       return (DERNumericString)obj;
/*     */     }
/*     */     
/*  27 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  29 */       return new DERNumericString(((ASN1OctetString)obj).getOctets());
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
/*     */   public static DERNumericString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  53 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERNumericString(byte[] string)
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
/*     */   public DERNumericString(String string)
/*     */   {
/*  78 */     this(string, false);
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
/*     */   public DERNumericString(String string, boolean validate)
/*     */   {
/*  93 */     if ((validate) && (!isNumericString(string)))
/*     */     {
/*  95 */       throw new IllegalArgumentException("string contains illegal characters");
/*     */     }
/*     */     
/*  98 */     this.string = string;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/* 103 */     return this.string;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 108 */     return this.string;
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/* 113 */     char[] cs = this.string.toCharArray();
/* 114 */     byte[] bs = new byte[cs.length];
/*     */     
/* 116 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/* 118 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 121 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 128 */     out.writeEncoded(18, getOctets());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 133 */     return getString().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 139 */     if (!(o instanceof DERNumericString))
/*     */     {
/* 141 */       return false;
/*     */     }
/*     */     
/* 144 */     DERNumericString s = (DERNumericString)o;
/*     */     
/* 146 */     return getString().equals(s.getString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isNumericString(String str)
/*     */   {
/* 158 */     for (int i = str.length() - 1; i >= 0; i--)
/*     */     {
/* 160 */       char ch = str.charAt(i);
/*     */       
/* 162 */       if (ch > '')
/*     */       {
/* 164 */         return false;
/*     */       }
/*     */       
/* 167 */       if ((('0' > ch) || (ch > '9')) && (ch != ' '))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 172 */         return false;
/*     */       }
/*     */     }
/* 175 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERNumericString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */