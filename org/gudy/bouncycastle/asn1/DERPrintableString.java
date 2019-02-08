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
/*     */ public class DERPrintableString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*     */   String string;
/*     */   
/*     */   public static DERPrintableString getInstance(Object obj)
/*     */   {
/*  22 */     if ((obj == null) || ((obj instanceof DERPrintableString)))
/*     */     {
/*  24 */       return (DERPrintableString)obj;
/*     */     }
/*     */     
/*  27 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  29 */       return new DERPrintableString(((ASN1OctetString)obj).getOctets());
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
/*     */   public static DERPrintableString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  53 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERPrintableString(byte[] string)
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
/*     */   public DERPrintableString(String string)
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
/*     */   public DERPrintableString(String string, boolean validate)
/*     */   {
/*  93 */     if ((validate) && (!isPrintableString(string)))
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
/*     */   public byte[] getOctets()
/*     */   {
/* 108 */     char[] cs = this.string.toCharArray();
/* 109 */     byte[] bs = new byte[cs.length];
/*     */     
/* 111 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/* 113 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 116 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 123 */     out.writeEncoded(19, getOctets());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 128 */     return getString().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 134 */     if (!(o instanceof DERPrintableString))
/*     */     {
/* 136 */       return false;
/*     */     }
/*     */     
/* 139 */     DERPrintableString s = (DERPrintableString)o;
/*     */     
/* 141 */     return getString().equals(s.getString());
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 146 */     return this.string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isPrintableString(String str)
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
/* 167 */       if (('a' > ch) || (ch > 'z'))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 172 */         if (('A' > ch) || (ch > 'Z'))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 177 */           if (('0' > ch) || (ch > '9'))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 182 */             switch (ch) {
/*     */             case ' ': case '\'': case '(': case ')': 
/*     */             case '+': case ',': case '-': case '.': 
/*     */             case '/': case ':': case '=': case '?': 
/*     */               break;
/*     */             case '!': case '"': 
/*     */             case '#': case '$': 
/*     */             case '%': case '&': 
/*     */             case '*': case '0': 
/*     */             case '1': case '2': 
/*     */             case '3': case '4': 
/*     */             case '5': case '6': 
/*     */             case '7': case '8': 
/*     */             case '9': case ';': 
/*     */             case '<': 
/*     */             case '>': 
/*     */             default: 
/* 199 */               return false;
/*     */             } } } }
/*     */     }
/* 202 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERPrintableString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */