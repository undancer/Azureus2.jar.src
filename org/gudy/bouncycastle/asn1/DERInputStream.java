/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public class DERInputStream
/*     */   extends FilterInputStream
/*     */   implements DERTags
/*     */ {
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public DERInputStream(InputStream is)
/*     */   {
/*  24 */     super(is);
/*     */   }
/*     */   
/*     */   protected int readLength()
/*     */     throws IOException
/*     */   {
/*  30 */     int length = read();
/*  31 */     if (length < 0)
/*     */     {
/*  33 */       throw new IOException("EOF found when length expected");
/*     */     }
/*     */     
/*  36 */     if (length == 128)
/*     */     {
/*  38 */       return -1;
/*     */     }
/*     */     
/*  41 */     if (length > 127)
/*     */     {
/*  43 */       int size = length & 0x7F;
/*     */       
/*  45 */       if (size > 4)
/*     */       {
/*  47 */         throw new IOException("DER length more than 4 bytes");
/*     */       }
/*     */       
/*  50 */       length = 0;
/*  51 */       for (int i = 0; i < size; i++)
/*     */       {
/*  53 */         int next = read();
/*     */         
/*  55 */         if (next < 0)
/*     */         {
/*  57 */           throw new IOException("EOF found reading length");
/*     */         }
/*     */         
/*  60 */         length = (length << 8) + next;
/*     */       }
/*     */       
/*  63 */       if (length < 0)
/*     */       {
/*  65 */         throw new IOException("corrupted stream - negative length found");
/*     */       }
/*     */     }
/*     */     
/*  69 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void readFully(byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  76 */     int left = bytes.length;
/*     */     
/*  78 */     if (left == 0)
/*     */     {
/*  80 */       return;
/*     */     }
/*     */     
/*  83 */     while (left > 0)
/*     */     {
/*  85 */       int l = read(bytes, bytes.length - left, left);
/*     */       
/*  87 */       if (l < 0)
/*     */       {
/*  89 */         throw new EOFException("unexpected end of stream");
/*     */       }
/*     */       
/*  92 */       left -= l;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected DERObject buildObject(int tag, byte[] bytes)
/*     */     throws IOException
/*     */   {
/*     */     ByteArrayInputStream bIn;
/*     */     
/*     */     BERInputStream dIn;
/*     */     
/*     */     DERConstructedSequence seq;
/* 105 */     switch (tag)
/*     */     {
/*     */     case 5: 
/* 108 */       return null;
/*     */     case 48: 
/* 110 */       bIn = new ByteArrayInputStream(bytes);
/* 111 */       dIn = new BERInputStream(bIn);
/* 112 */       seq = new DERConstructedSequence();
/*     */       
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/* 118 */           DERObject obj = dIn.readObject();
/*     */           
/* 120 */           seq.addObject(obj);
/*     */         }
/*     */       }
/*     */       catch (EOFException ex)
/*     */       {
/* 125 */         return seq;
/*     */       }
/*     */     case 49: 
/* 128 */       bIn = new ByteArrayInputStream(bytes);
/* 129 */       dIn = new BERInputStream(bIn);
/*     */       
/* 131 */       ASN1EncodableVector v = new ASN1EncodableVector();
/*     */       
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/* 137 */           DERObject obj = dIn.readObject();
/*     */           
/* 139 */           v.add(obj);
/*     */         }
/*     */       }
/*     */       catch (EOFException ex)
/*     */       {
/* 144 */         return new DERConstructedSet(v);
/*     */       }
/*     */     case 1: 
/* 147 */       return new DERBoolean(bytes);
/*     */     case 2: 
/* 149 */       return new DERInteger(bytes);
/*     */     case 10: 
/* 151 */       return new DEREnumerated(bytes);
/*     */     case 6: 
/* 153 */       return new DERObjectIdentifier(bytes);
/*     */     case 3: 
/* 155 */       int padBits = bytes[0];
/* 156 */       byte[] data = new byte[bytes.length - 1];
/*     */       
/* 158 */       System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
/*     */       
/* 160 */       return new DERBitString(data, padBits);
/*     */     case 12: 
/* 162 */       return new DERUTF8String(bytes);
/*     */     case 19: 
/* 164 */       return new DERPrintableString(bytes);
/*     */     case 22: 
/* 166 */       return new DERIA5String(bytes);
/*     */     case 20: 
/* 168 */       return new DERT61String(bytes);
/*     */     case 26: 
/* 170 */       return new DERVisibleString(bytes);
/*     */     case 28: 
/* 172 */       return new DERUniversalString(bytes);
/*     */     case 27: 
/* 174 */       return new DERGeneralString(bytes);
/*     */     case 30: 
/* 176 */       return new DERBMPString(bytes);
/*     */     case 4: 
/* 178 */       return new DEROctetString(bytes);
/*     */     case 23: 
/* 180 */       return new DERUTCTime(bytes);
/*     */     case 24: 
/* 182 */       return new DERGeneralizedTime(bytes);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 187 */     if ((tag & 0x80) != 0)
/*     */     {
/* 189 */       if ((tag & 0x1F) == 31)
/*     */       {
/* 191 */         throw new IOException("unsupported high tag encountered");
/*     */       }
/*     */       
/* 194 */       if (bytes.length == 0)
/*     */       {
/* 196 */         if ((tag & 0x20) == 0)
/*     */         {
/* 198 */           return new DERTaggedObject(false, tag & 0x1F, new DERNull());
/*     */         }
/*     */         
/*     */ 
/* 202 */         return new DERTaggedObject(false, tag & 0x1F, new DERConstructedSequence());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */       if ((tag & 0x20) == 0)
/*     */       {
/* 211 */         return new DERTaggedObject(false, tag & 0x1F, new DEROctetString(bytes));
/*     */       }
/*     */       
/* 214 */       bIn = new ByteArrayInputStream(bytes);
/* 215 */       dIn = new BERInputStream(bIn);
/*     */       
/* 217 */       DEREncodable dObj = dIn.readObject();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 223 */       if (dIn.available() == 0)
/*     */       {
/* 225 */         return new DERTaggedObject(tag & 0x1F, dObj);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 231 */       seq = new DERConstructedSequence();
/*     */       
/* 233 */       seq.addObject(dObj);
/*     */       
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/* 239 */           dObj = dIn.readObject();
/*     */           
/* 241 */           seq.addObject(dObj);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (EOFException ex)
/*     */       {
/*     */ 
/* 249 */         return new DERTaggedObject(false, tag & 0x1F, seq);
/*     */       }
/*     */     }
/* 252 */     return new DERUnknownTag(tag, bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public DERObject readObject()
/*     */     throws IOException
/*     */   {
/* 259 */     int tag = read();
/* 260 */     if (tag == -1)
/*     */     {
/* 262 */       throw new EOFException();
/*     */     }
/*     */     
/* 265 */     int length = readLength();
/* 266 */     byte[] bytes = new byte[length];
/*     */     
/* 268 */     readFully(bytes);
/*     */     
/* 270 */     return buildObject(tag, bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */