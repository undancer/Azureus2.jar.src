/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class DERBitString
/*     */   extends ASN1Object
/*     */   implements DERString
/*     */ {
/*  10 */   private static final char[] table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   
/*     */ 
/*     */   protected byte[] data;
/*     */   
/*     */ 
/*     */   protected int padBits;
/*     */   
/*     */ 
/*     */ 
/*     */   protected static int getPadBits(int bitString)
/*     */   {
/*  22 */     int val = 0;
/*  23 */     for (int i = 3; i >= 0; i--)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  29 */       if (i != 0)
/*     */       {
/*  31 */         if (bitString >> i * 8 != 0)
/*     */         {
/*  33 */           val = bitString >> i * 8 & 0xFF;
/*  34 */           break;
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*  39 */       else if (bitString != 0)
/*     */       {
/*  41 */         val = bitString & 0xFF;
/*  42 */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  47 */     if (val == 0)
/*     */     {
/*  49 */       return 7;
/*     */     }
/*     */     
/*     */ 
/*  53 */     int bits = 1;
/*     */     
/*  55 */     while ((val <<= 1 & 0xFF) != 0)
/*     */     {
/*  57 */       bits++;
/*     */     }
/*     */     
/*  60 */     return 8 - bits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static byte[] getBytes(int bitString)
/*     */   {
/*  69 */     int bytes = 4;
/*  70 */     for (int i = 3; i >= 1; i--)
/*     */     {
/*  72 */       if ((bitString & 255 << i * 8) != 0) {
/*     */         break;
/*     */       }
/*     */       
/*  76 */       bytes--;
/*     */     }
/*     */     
/*  79 */     byte[] result = new byte[bytes];
/*  80 */     for (int i = 0; i < bytes; i++)
/*     */     {
/*  82 */       result[i] = ((byte)(bitString >> i * 8 & 0xFF));
/*     */     }
/*     */     
/*  85 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DERBitString getInstance(Object obj)
/*     */   {
/*  96 */     if ((obj == null) || ((obj instanceof DERBitString)))
/*     */     {
/*  98 */       return (DERBitString)obj;
/*     */     }
/*     */     
/* 101 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/* 103 */       byte[] bytes = ((ASN1OctetString)obj).getOctets();
/* 104 */       int padBits = bytes[0];
/* 105 */       byte[] data = new byte[bytes.length - 1];
/*     */       
/* 107 */       System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
/*     */       
/* 109 */       return new DERBitString(data, padBits);
/*     */     }
/*     */     
/* 112 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/* 114 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*     */     }
/*     */     
/* 117 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DERBitString getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/* 133 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected DERBitString(byte data, int padBits)
/*     */   {
/* 140 */     this.data = new byte[1];
/* 141 */     this.data[0] = data;
/* 142 */     this.padBits = padBits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERBitString(byte[] data, int padBits)
/*     */   {
/* 153 */     this.data = data;
/* 154 */     this.padBits = padBits;
/*     */   }
/*     */   
/*     */ 
/*     */   public DERBitString(byte[] data)
/*     */   {
/* 160 */     this(data, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public DERBitString(DEREncodable obj)
/*     */   {
/*     */     try
/*     */     {
/* 168 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 169 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*     */       
/* 171 */       dOut.writeObject(obj);
/* 172 */       dOut.close();
/*     */       
/* 174 */       this.data = bOut.toByteArray();
/* 175 */       this.padBits = 0;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 179 */       throw new IllegalArgumentException("Error processing object : " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] getBytes()
/*     */   {
/* 185 */     return this.data;
/*     */   }
/*     */   
/*     */   public int getPadBits()
/*     */   {
/* 190 */     return this.padBits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 199 */     int value = 0;
/*     */     
/* 201 */     for (int i = 0; (i != this.data.length) && (i != 4); i++)
/*     */     {
/* 203 */       value |= (this.data[i] & 0xFF) << 8 * i;
/*     */     }
/*     */     
/* 206 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 213 */     byte[] bytes = new byte[getBytes().length + 1];
/*     */     
/* 215 */     bytes[0] = ((byte)getPadBits());
/* 216 */     System.arraycopy(getBytes(), 0, bytes, 1, bytes.length - 1);
/*     */     
/* 218 */     out.writeEncoded(3, bytes);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 223 */     int value = 0;
/*     */     
/* 225 */     for (int i = 0; i != this.data.length; i++)
/*     */     {
/* 227 */       value ^= (this.data[i] & 0xFF) << i % 4;
/*     */     }
/*     */     
/* 230 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean asn1Equals(DERObject o)
/*     */   {
/* 236 */     if (!(o instanceof DERBitString))
/*     */     {
/* 238 */       return false;
/*     */     }
/*     */     
/* 241 */     DERBitString other = (DERBitString)o;
/*     */     
/* 243 */     if (this.data.length != other.data.length)
/*     */     {
/* 245 */       return false;
/*     */     }
/*     */     
/* 248 */     for (int i = 0; i != this.data.length; i++)
/*     */     {
/* 250 */       if (this.data[i] != other.data[i])
/*     */       {
/* 252 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 256 */     return this.padBits == other.padBits;
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/* 261 */     StringBuilder buf = new StringBuilder("#");
/* 262 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 263 */     ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 267 */       aOut.writeObject(this);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 271 */       throw new RuntimeException("internal error encoding BitString");
/*     */     }
/*     */     
/* 274 */     byte[] string = bOut.toByteArray();
/*     */     
/* 276 */     for (int i = 0; i != string.length; i++)
/*     */     {
/* 278 */       buf.append(table[(string[i] >>> 4 & 0xF)]);
/* 279 */       buf.append(table[(string[i] & 0xF)]);
/*     */     }
/*     */     
/* 282 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 287 */     return getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERBitString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */