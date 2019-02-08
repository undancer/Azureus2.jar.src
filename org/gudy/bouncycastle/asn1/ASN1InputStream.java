/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ASN1InputStream
/*     */   extends FilterInputStream
/*     */   implements DERTags
/*     */ {
/*  21 */   private static final DERObject END_OF_STREAM = new DERObject()
/*     */   {
/*     */ 
/*     */     void encode(DEROutputStream out)
/*     */       throws IOException
/*     */     {
/*  27 */       throw new IOException("Eeek!");
/*     */     }
/*     */     
/*     */     public int hashCode() {
/*  31 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/*  36 */       return o == this;
/*     */     }
/*     */   };
/*     */   
/*  40 */   boolean eofFound = false;
/*  41 */   int limit = Integer.MAX_VALUE;
/*     */   
/*     */ 
/*     */   public ASN1InputStream(InputStream is)
/*     */   {
/*  46 */     super(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ASN1InputStream(byte[] input)
/*     */   {
/*  58 */     this(new ByteArrayInputStream(input), input.length);
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
/*     */   public ASN1InputStream(InputStream input, int limit)
/*     */   {
/*  71 */     super(input);
/*  72 */     this.limit = limit;
/*     */   }
/*     */   
/*     */   protected int readLength()
/*     */     throws IOException
/*     */   {
/*  78 */     int length = read();
/*  79 */     if (length < 0)
/*     */     {
/*  81 */       throw new IOException("EOF found when length expected");
/*     */     }
/*     */     
/*  84 */     if (length == 128)
/*     */     {
/*  86 */       return -1;
/*     */     }
/*     */     
/*  89 */     if (length > 127)
/*     */     {
/*  91 */       int size = length & 0x7F;
/*     */       
/*  93 */       if (size > 4)
/*     */       {
/*  95 */         throw new IOException("DER length more than 4 bytes");
/*     */       }
/*     */       
/*  98 */       length = 0;
/*  99 */       for (int i = 0; i < size; i++)
/*     */       {
/* 101 */         int next = read();
/*     */         
/* 103 */         if (next < 0)
/*     */         {
/* 105 */           throw new IOException("EOF found reading length");
/*     */         }
/*     */         
/* 108 */         length = (length << 8) + next;
/*     */       }
/*     */       
/* 111 */       if (length < 0)
/*     */       {
/* 113 */         throw new IOException("corrupted stream - negative length found");
/*     */       }
/*     */       
/* 116 */       if (length >= this.limit)
/*     */       {
/* 118 */         throw new IOException("corrupted stream - out of bounds length found");
/*     */       }
/*     */     }
/*     */     
/* 122 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void readFully(byte[] bytes)
/*     */     throws IOException
/*     */   {
/* 129 */     int left = bytes.length;
/*     */     
/*     */ 
/* 132 */     if (left == 0) {
/*     */       return;
/*     */     }
/*     */     
/*     */     int len;
/* 137 */     while ((len = read(bytes, bytes.length - left, left)) > 0)
/*     */     {
/* 139 */       if (left -= len == 0)
/*     */       {
/* 141 */         return;
/*     */       }
/*     */     }
/*     */     
/* 145 */     if (left != 0)
/*     */     {
/* 147 */       throw new EOFException("EOF encountered in middle of object");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DERObject buildObject(int tag, int tagNo, int length)
/*     */     throws IOException
/*     */   {
/* 160 */     if ((tag & 0x40) != 0)
/*     */     {
/* 162 */       return new DERApplicationSpecific(tagNo, readDefiniteLengthFully(length));
/*     */     }
/*     */     
/* 165 */     boolean isConstructed = (tag & 0x20) != 0;
/*     */     
/* 167 */     if (isConstructed)
/*     */     {
/* 169 */       switch (tag)
/*     */       {
/*     */       case 48: 
/* 172 */         return new DERSequence(buildDerEncodableVector(length));
/*     */       case 49: 
/* 174 */         return new DERSet(buildDerEncodableVector(length), false);
/*     */       case 36: 
/* 176 */         return buildDerConstructedOctetString(length);
/*     */       }
/*     */       
/*     */       
/*     */ 
/*     */ 
/* 182 */       if ((tag & 0x80) != 0)
/*     */       {
/* 184 */         if (length == 0)
/*     */         {
/* 186 */           return new DERTaggedObject(false, tagNo, new DERSequence());
/*     */         }
/*     */         
/* 189 */         ASN1EncodableVector v = buildDerEncodableVector(length);
/*     */         
/* 191 */         if (v.size() == 1)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */           return new DERTaggedObject(tagNo, v.get(0));
/*     */         }
/*     */         
/* 200 */         return new DERTaggedObject(false, tagNo, new DERSequence(v));
/*     */       }
/*     */       
/* 203 */       return new DERUnknownTag(tag, readDefiniteLengthFully(length));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 208 */     byte[] bytes = readDefiniteLengthFully(length);
/*     */     
/* 210 */     switch (tag)
/*     */     {
/*     */     case 5: 
/* 213 */       return DERNull.INSTANCE;
/*     */     case 1: 
/* 215 */       return new DERBoolean(bytes);
/*     */     case 2: 
/* 217 */       return new DERInteger(bytes);
/*     */     case 10: 
/* 219 */       return new DEREnumerated(bytes);
/*     */     case 6: 
/* 221 */       return new DERObjectIdentifier(bytes);
/*     */     
/*     */     case 3: 
/* 224 */       int padBits = bytes[0];
/* 225 */       byte[] data = new byte[bytes.length - 1];
/*     */       
/* 227 */       System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
/*     */       
/* 229 */       return new DERBitString(data, padBits);
/*     */     
/*     */     case 18: 
/* 232 */       return new DERNumericString(bytes);
/*     */     case 12: 
/* 234 */       return new DERUTF8String(bytes);
/*     */     case 19: 
/* 236 */       return new DERPrintableString(bytes);
/*     */     case 22: 
/* 238 */       return new DERIA5String(bytes);
/*     */     case 20: 
/* 240 */       return new DERT61String(bytes);
/*     */     case 26: 
/* 242 */       return new DERVisibleString(bytes);
/*     */     case 27: 
/* 244 */       return new DERGeneralString(bytes);
/*     */     case 28: 
/* 246 */       return new DERUniversalString(bytes);
/*     */     case 30: 
/* 248 */       return new DERBMPString(bytes);
/*     */     case 4: 
/* 250 */       return new DEROctetString(bytes);
/*     */     case 23: 
/* 252 */       return new DERUTCTime(bytes);
/*     */     case 24: 
/* 254 */       return new DERGeneralizedTime(bytes);
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 260 */     if ((tag & 0x80) != 0)
/*     */     {
/* 262 */       if (bytes.length == 0)
/*     */       {
/* 264 */         return new DERTaggedObject(false, tagNo, DERNull.INSTANCE);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 270 */       return new DERTaggedObject(false, tagNo, new DEROctetString(bytes));
/*     */     }
/*     */     
/* 273 */     return new DERUnknownTag(tag, bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] readDefiniteLengthFully(int length)
/*     */     throws IOException
/*     */   {
/* 281 */     byte[] bytes = new byte[length];
/* 282 */     readFully(bytes);
/* 283 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] readIndefiniteLengthFully()
/*     */     throws IOException
/*     */   {
/* 292 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/*     */ 
/* 295 */     int b1 = read();
/*     */     int b;
/* 297 */     while ((b = read()) >= 0)
/*     */     {
/* 299 */       if ((b1 == 0) && (b == 0)) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 304 */       bOut.write(b1);
/* 305 */       b1 = b;
/*     */     }
/*     */     
/* 308 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */   private BERConstructedOctetString buildConstructedOctetString(DERObject sentinel)
/*     */     throws IOException
/*     */   {
/* 314 */     Vector octs = new Vector();
/*     */     
/*     */     DERObject o;
/* 317 */     while ((o = readObject()) != sentinel)
/*     */     {
/* 319 */       octs.addElement(o);
/*     */     }
/*     */     
/* 322 */     return new BERConstructedOctetString(octs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private BERConstructedOctetString buildDerConstructedOctetString(int length)
/*     */     throws IOException
/*     */   {
/* 331 */     DefiniteLengthInputStream dIn = new DefiniteLengthInputStream(this, length);
/* 332 */     ASN1InputStream aIn = new ASN1InputStream(dIn, length);
/*     */     
/* 334 */     return aIn.buildConstructedOctetString(null);
/*     */   }
/*     */   
/*     */   private ASN1EncodableVector buildEncodableVector(DERObject sentinel)
/*     */     throws IOException
/*     */   {
/* 340 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*     */     DERObject o;
/* 343 */     while ((o = readObject()) != sentinel)
/*     */     {
/* 345 */       v.add(o);
/*     */     }
/*     */     
/* 348 */     return v;
/*     */   }
/*     */   
/*     */   private ASN1EncodableVector buildDerEncodableVector(int length)
/*     */     throws IOException
/*     */   {
/* 354 */     DefiniteLengthInputStream dIn = new DefiniteLengthInputStream(this, length);
/* 355 */     ASN1InputStream aIn = new ASN1InputStream(dIn, length);
/*     */     
/* 357 */     return aIn.buildEncodableVector(null);
/*     */   }
/*     */   
/*     */   public DERObject readObject()
/*     */     throws IOException
/*     */   {
/* 363 */     int tag = read();
/* 364 */     if (tag == -1)
/*     */     {
/* 366 */       if (this.eofFound)
/*     */       {
/* 368 */         throw new EOFException("attempt to read past end of file.");
/*     */       }
/*     */       
/* 371 */       this.eofFound = true;
/*     */       
/* 373 */       return null;
/*     */     }
/*     */     
/* 376 */     int tagNo = 0;
/*     */     
/* 378 */     if (((tag & 0x80) != 0) || ((tag & 0x40) != 0))
/*     */     {
/* 380 */       tagNo = readTagNumber(tag);
/*     */     }
/*     */     
/* 383 */     int length = readLength();
/*     */     
/* 385 */     if (length < 0)
/*     */     {
/* 387 */       switch (tag)
/*     */       {
/*     */       case 5: 
/* 390 */         return BERNull.INSTANCE;
/*     */       case 48: 
/* 392 */         return new BERSequence(buildEncodableVector(END_OF_STREAM));
/*     */       case 49: 
/* 394 */         return new BERSet(buildEncodableVector(END_OF_STREAM), false);
/*     */       case 36: 
/* 396 */         return buildConstructedOctetString(END_OF_STREAM);
/*     */       }
/*     */       
/*     */       
/*     */ 
/*     */ 
/* 402 */       if ((tag & 0x80) != 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 407 */         if ((tag & 0x20) == 0)
/*     */         {
/* 409 */           byte[] bytes = readIndefiniteLengthFully();
/*     */           
/* 411 */           return new BERTaggedObject(false, tagNo, new DEROctetString(bytes));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 417 */         ASN1EncodableVector v = buildEncodableVector(END_OF_STREAM);
/*     */         
/* 419 */         if (v.size() == 0)
/*     */         {
/* 421 */           return new DERTaggedObject(tagNo);
/*     */         }
/*     */         
/* 424 */         if (v.size() == 1)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 430 */           return new BERTaggedObject(tagNo, v.get(0));
/*     */         }
/*     */         
/* 433 */         return new BERTaggedObject(false, tagNo, new BERSequence(v));
/*     */       }
/*     */       
/* 436 */       throw new IOException("unknown BER object encountered");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 442 */     if ((tag == 0) && (length == 0))
/*     */     {
/* 444 */       return END_OF_STREAM;
/*     */     }
/*     */     
/* 447 */     return buildObject(tag, tagNo, length);
/*     */   }
/*     */   
/*     */ 
/*     */   private int readTagNumber(int tag)
/*     */     throws IOException
/*     */   {
/* 454 */     int tagNo = tag & 0x1F;
/*     */     
/* 456 */     if (tagNo == 31)
/*     */     {
/* 458 */       int b = read();
/*     */       
/* 460 */       tagNo = 0;
/*     */       
/* 462 */       while ((b >= 0) && ((b & 0x80) != 0))
/*     */       {
/* 464 */         tagNo |= b & 0x7F;
/* 465 */         tagNo <<= 7;
/* 466 */         b = read();
/*     */       }
/*     */       
/* 469 */       if (b < 0)
/*     */       {
/* 471 */         this.eofFound = true;
/* 472 */         throw new EOFException("EOF found inside tag value.");
/*     */       }
/*     */       
/* 475 */       tagNo |= b & 0x7F;
/*     */     }
/*     */     
/* 478 */     return tagNo;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1InputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */