/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DERObjectIdentifier
/*     */   extends ASN1Object
/*     */ {
/*     */   String identifier;
/*     */   
/*     */   public static DERObjectIdentifier getInstance(Object obj)
/*     */   {
/*  21 */     if ((obj == null) || ((obj instanceof DERObjectIdentifier)))
/*     */     {
/*  23 */       return (DERObjectIdentifier)obj;
/*     */     }
/*     */     
/*  26 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  28 */       return new DERObjectIdentifier(((ASN1OctetString)obj).getOctets());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERObjectIdentifier getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  52 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   DERObjectIdentifier(byte[] bytes)
/*     */   {
/*  59 */     StringBuilder objId = new StringBuilder();
/*  60 */     long value = 0L;
/*  61 */     BigInteger bigValue = null;
/*  62 */     boolean first = true;
/*     */     
/*  64 */     for (int i = 0; i != bytes.length; i++)
/*     */     {
/*  66 */       int b = bytes[i] & 0xFF;
/*     */       
/*  68 */       if (value < 36028797018963968L)
/*     */       {
/*  70 */         value = value * 128L + (b & 0x7F);
/*  71 */         if ((b & 0x80) == 0)
/*     */         {
/*  73 */           if (first)
/*     */           {
/*  75 */             switch ((int)value / 40)
/*     */             {
/*     */             case 0: 
/*  78 */               objId.append('0');
/*  79 */               break;
/*     */             case 1: 
/*  81 */               objId.append('1');
/*  82 */               value -= 40L;
/*  83 */               break;
/*     */             default: 
/*  85 */               objId.append('2');
/*  86 */               value -= 80L;
/*     */             }
/*  88 */             first = false;
/*     */           }
/*     */           
/*  91 */           objId.append('.');
/*  92 */           objId.append(value);
/*  93 */           value = 0L;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  98 */         if (bigValue == null)
/*     */         {
/* 100 */           bigValue = BigInteger.valueOf(value);
/*     */         }
/* 102 */         bigValue = bigValue.shiftLeft(7);
/* 103 */         bigValue = bigValue.or(BigInteger.valueOf(b & 0x7F));
/* 104 */         if ((b & 0x80) == 0)
/*     */         {
/* 106 */           objId.append('.');
/* 107 */           objId.append(bigValue);
/* 108 */           bigValue = null;
/* 109 */           value = 0L;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 114 */     this.identifier = objId.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public DERObjectIdentifier(String identifier)
/*     */   {
/* 120 */     if (!isValidIdentifier(identifier))
/*     */     {
/* 122 */       throw new IllegalArgumentException("string " + identifier + " not an OID");
/*     */     }
/*     */     
/* 125 */     this.identifier = identifier;
/*     */   }
/*     */   
/*     */   public String getId()
/*     */   {
/* 130 */     return this.identifier;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void writeField(OutputStream out, long fieldValue)
/*     */     throws IOException
/*     */   {
/* 138 */     if (fieldValue >= 128L)
/*     */     {
/* 140 */       if (fieldValue >= 16384L)
/*     */       {
/* 142 */         if (fieldValue >= 2097152L)
/*     */         {
/* 144 */           if (fieldValue >= 268435456L)
/*     */           {
/* 146 */             if (fieldValue >= 34359738368L)
/*     */             {
/* 148 */               if (fieldValue >= 4398046511104L)
/*     */               {
/* 150 */                 if (fieldValue >= 562949953421312L)
/*     */                 {
/* 152 */                   if (fieldValue >= 72057594037927936L)
/*     */                   {
/* 154 */                     out.write((int)(fieldValue >> 56) | 0x80);
/*     */                   }
/* 156 */                   out.write((int)(fieldValue >> 49) | 0x80);
/*     */                 }
/* 158 */                 out.write((int)(fieldValue >> 42) | 0x80);
/*     */               }
/* 160 */               out.write((int)(fieldValue >> 35) | 0x80);
/*     */             }
/* 162 */             out.write((int)(fieldValue >> 28) | 0x80);
/*     */           }
/* 164 */           out.write((int)(fieldValue >> 21) | 0x80);
/*     */         }
/* 166 */         out.write((int)(fieldValue >> 14) | 0x80);
/*     */       }
/* 168 */       out.write((int)(fieldValue >> 7) | 0x80);
/*     */     }
/* 170 */     out.write((int)fieldValue & 0x7F);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void writeField(OutputStream out, BigInteger fieldValue)
/*     */     throws IOException
/*     */   {
/* 178 */     int byteCount = (fieldValue.bitLength() + 6) / 7;
/* 179 */     if (byteCount == 0)
/*     */     {
/* 181 */       out.write(0);
/*     */     }
/*     */     else
/*     */     {
/* 185 */       BigInteger tmpValue = fieldValue;
/* 186 */       byte[] tmp = new byte[byteCount];
/* 187 */       for (int i = byteCount - 1; i >= 0; i--)
/*     */       {
/* 189 */         tmp[i] = ((byte)(tmpValue.intValue() & 0x7F | 0x80));
/* 190 */         tmpValue = tmpValue.shiftRight(7);
/*     */       }
/* 192 */       int tmp79_78 = (byteCount - 1); byte[] tmp79_74 = tmp;tmp79_74[tmp79_78] = ((byte)(tmp79_74[tmp79_78] & 0x7F));
/* 193 */       out.write(tmp);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 202 */     OIDTokenizer tok = new OIDTokenizer(this.identifier);
/* 203 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 204 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/* 206 */     writeField(bOut, Integer.parseInt(tok.nextToken()) * 40 + Integer.parseInt(tok.nextToken()));
/*     */     
/*     */ 
/*     */ 
/* 210 */     while (tok.hasMoreTokens())
/*     */     {
/* 212 */       String token = tok.nextToken();
/* 213 */       if (token.length() < 18)
/*     */       {
/* 215 */         writeField(bOut, Long.parseLong(token));
/*     */       }
/*     */       else
/*     */       {
/* 219 */         writeField(bOut, new BigInteger(token));
/*     */       }
/*     */     }
/*     */     
/* 223 */     dOut.close();
/*     */     
/* 225 */     byte[] bytes = bOut.toByteArray();
/*     */     
/* 227 */     out.writeEncoded(6, bytes);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 232 */     return this.identifier.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 238 */     if (!(o instanceof DERObjectIdentifier))
/*     */     {
/* 240 */       return false;
/*     */     }
/*     */     
/* 243 */     return this.identifier.equals(((DERObjectIdentifier)o).identifier);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 248 */     return getId();
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean isValidIdentifier(String identifier)
/*     */   {
/* 254 */     if ((identifier.length() < 3) || (identifier.charAt(1) != '.'))
/*     */     {
/*     */ 
/* 257 */       return false;
/*     */     }
/*     */     
/* 260 */     char first = identifier.charAt(0);
/* 261 */     if ((first < '0') || (first > '2'))
/*     */     {
/* 263 */       return false;
/*     */     }
/*     */     
/* 266 */     boolean periodAllowed = false;
/* 267 */     for (int i = identifier.length() - 1; i >= 2; i--)
/*     */     {
/* 269 */       char ch = identifier.charAt(i);
/*     */       
/* 271 */       if (('0' <= ch) && (ch <= '9'))
/*     */       {
/* 273 */         periodAllowed = true;
/*     */ 
/*     */ 
/*     */       }
/* 277 */       else if (ch == '.')
/*     */       {
/* 279 */         if (!periodAllowed)
/*     */         {
/* 281 */           return false;
/*     */         }
/*     */         
/* 284 */         periodAllowed = false;
/*     */       }
/*     */       else
/*     */       {
/* 288 */         return false;
/*     */       }
/*     */     }
/* 291 */     return periodAllowed;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERObjectIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */