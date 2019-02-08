/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ASN1StreamParser
/*     */ {
/*     */   InputStream _in;
/*     */   private int _limit;
/*     */   private boolean _eofFound;
/*     */   
/*     */   public ASN1StreamParser(InputStream in)
/*     */   {
/*  18 */     this(in, Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ASN1StreamParser(InputStream in, int limit)
/*     */   {
/*  25 */     this._in = in;
/*  26 */     this._limit = limit;
/*     */   }
/*     */   
/*     */ 
/*     */   public ASN1StreamParser(byte[] encoding)
/*     */   {
/*  32 */     this(new ByteArrayInputStream(encoding), encoding.length);
/*     */   }
/*     */   
/*     */   InputStream getParentStream()
/*     */   {
/*  37 */     return this._in;
/*     */   }
/*     */   
/*     */   private int readLength()
/*     */     throws IOException
/*     */   {
/*  43 */     int length = this._in.read();
/*  44 */     if (length < 0)
/*     */     {
/*  46 */       throw new EOFException("EOF found when length expected");
/*     */     }
/*     */     
/*  49 */     if (length == 128)
/*     */     {
/*  51 */       return -1;
/*     */     }
/*     */     
/*  54 */     if (length > 127)
/*     */     {
/*  56 */       int size = length & 0x7F;
/*     */       
/*  58 */       if (size > 4)
/*     */       {
/*  60 */         throw new IOException("DER length more than 4 bytes");
/*     */       }
/*     */       
/*  63 */       length = 0;
/*  64 */       for (int i = 0; i < size; i++)
/*     */       {
/*  66 */         int next = this._in.read();
/*     */         
/*  68 */         if (next < 0)
/*     */         {
/*  70 */           throw new EOFException("EOF found reading length");
/*     */         }
/*     */         
/*  73 */         length = (length << 8) + next;
/*     */       }
/*     */       
/*  76 */       if (length < 0)
/*     */       {
/*  78 */         throw new IOException("corrupted stream - negative length found");
/*     */       }
/*     */       
/*  81 */       if (length >= this._limit)
/*     */       {
/*  83 */         throw new IOException("corrupted stream - out of bounds length found");
/*     */       }
/*     */     }
/*     */     
/*  87 */     return length;
/*     */   }
/*     */   
/*     */   public DEREncodable readObject()
/*     */     throws IOException
/*     */   {
/*  93 */     int tag = this._in.read();
/*  94 */     if (tag == -1)
/*     */     {
/*  96 */       if (this._eofFound)
/*     */       {
/*  98 */         throw new EOFException("attempt to read past end of file.");
/*     */       }
/*     */       
/* 101 */       this._eofFound = true;
/*     */       
/* 103 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 109 */     set00Check(false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 114 */     int baseTagNo = tag & 0xFFFFFFDF;
/* 115 */     int tagNo = baseTagNo;
/*     */     
/* 117 */     if ((tag & 0x80) != 0)
/*     */     {
/* 119 */       tagNo = tag & 0x1F;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 124 */       if (tagNo == 31)
/*     */       {
/* 126 */         tagNo = 0;
/*     */         
/* 128 */         int b = this._in.read();
/*     */         
/* 130 */         while ((b >= 0) && ((b & 0x80) != 0))
/*     */         {
/* 132 */           tagNo |= b & 0x7F;
/* 133 */           tagNo <<= 7;
/* 134 */           b = this._in.read();
/*     */         }
/*     */         
/* 137 */         if (b < 0)
/*     */         {
/* 139 */           this._eofFound = true;
/*     */           
/* 141 */           throw new EOFException("EOF encountered inside tag value.");
/*     */         }
/*     */         
/* 144 */         tagNo |= b & 0x7F;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 151 */     int length = readLength();
/*     */     
/* 153 */     if (length < 0)
/*     */     {
/* 155 */       IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this._in);
/*     */       
/* 157 */       switch (baseTagNo)
/*     */       {
/*     */       case 5: 
/* 160 */         while (indIn.read() >= 0) {}
/*     */         
/*     */ 
/*     */ 
/* 164 */         return BERNull.INSTANCE;
/*     */       case 4: 
/* 166 */         return new BEROctetStringParser(new ASN1ObjectParser(tag, tagNo, indIn));
/*     */       case 16: 
/* 168 */         return new BERSequenceParser(new ASN1ObjectParser(tag, tagNo, indIn));
/*     */       case 17: 
/* 170 */         return new BERSetParser(new ASN1ObjectParser(tag, tagNo, indIn));
/*     */       }
/* 172 */       return new BERTaggedObjectParser(tag, tagNo, indIn);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 177 */     DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this._in, length);
/*     */     
/* 179 */     switch (baseTagNo)
/*     */     {
/*     */     case 2: 
/* 182 */       return new DERInteger(defIn.toByteArray());
/*     */     case 5: 
/* 184 */       defIn.toByteArray();
/* 185 */       return DERNull.INSTANCE;
/*     */     case 6: 
/* 187 */       return new DERObjectIdentifier(defIn.toByteArray());
/*     */     case 4: 
/* 189 */       return new DEROctetString(defIn.toByteArray());
/*     */     case 16: 
/* 191 */       return new DERSequence(loadVector(defIn, length)).parser();
/*     */     case 17: 
/* 193 */       return new DERSet(loadVector(defIn, length)).parser();
/*     */     }
/* 195 */     return new BERTaggedObjectParser(tag, tagNo, defIn);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void set00Check(boolean enabled)
/*     */   {
/* 202 */     if ((this._in instanceof IndefiniteLengthInputStream))
/*     */     {
/* 204 */       ((IndefiniteLengthInputStream)this._in).setEofOn00(enabled);
/*     */     }
/*     */   }
/*     */   
/*     */   private ASN1EncodableVector loadVector(InputStream in, int length)
/*     */     throws IOException
/*     */   {
/* 211 */     ASN1InputStream aIn = new ASN1InputStream(in, length);
/* 212 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*     */     DERObject obj;
/* 215 */     while ((obj = aIn.readObject()) != null)
/*     */     {
/* 217 */       v.add(obj);
/*     */     }
/*     */     
/* 220 */     return v;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1StreamParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */