/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BERTaggedObjectParser
/*     */   implements ASN1TaggedObjectParser
/*     */ {
/*     */   private int _baseTag;
/*     */   private int _tagNumber;
/*     */   private InputStream _contentStream;
/*     */   private boolean _indefiniteLength;
/*     */   
/*     */   protected BERTaggedObjectParser(int baseTag, int tagNumber, InputStream contentStream)
/*     */   {
/*  20 */     this._baseTag = baseTag;
/*  21 */     this._tagNumber = tagNumber;
/*  22 */     this._contentStream = contentStream;
/*  23 */     this._indefiniteLength = (contentStream instanceof IndefiniteLengthInputStream);
/*     */   }
/*     */   
/*     */   public boolean isConstructed()
/*     */   {
/*  28 */     return (this._baseTag & 0x20) != 0;
/*     */   }
/*     */   
/*     */   public int getTagNo()
/*     */   {
/*  33 */     return this._tagNumber;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DEREncodable getObjectParser(int tag, boolean isExplicit)
/*     */     throws IOException
/*     */   {
/*  41 */     if (isExplicit)
/*     */     {
/*  43 */       return new ASN1StreamParser(this._contentStream).readObject();
/*     */     }
/*     */     
/*     */ 
/*  47 */     switch (tag)
/*     */     {
/*     */     case 17: 
/*  50 */       if (this._indefiniteLength)
/*     */       {
/*  52 */         return new BERSetParser(new ASN1ObjectParser(this._baseTag, this._tagNumber, this._contentStream));
/*     */       }
/*     */       
/*     */ 
/*  56 */       return new DERSet(loadVector(this._contentStream)).parser();
/*     */     
/*     */     case 16: 
/*  59 */       if (this._indefiniteLength)
/*     */       {
/*  61 */         return new BERSequenceParser(new ASN1ObjectParser(this._baseTag, this._tagNumber, this._contentStream));
/*     */       }
/*     */       
/*     */ 
/*  65 */       return new DERSequence(loadVector(this._contentStream)).parser();
/*     */     
/*     */     case 4: 
/*  68 */       if ((this._indefiniteLength) || (isConstructed()))
/*     */       {
/*  70 */         return new BEROctetStringParser(new ASN1ObjectParser(this._baseTag, this._tagNumber, this._contentStream));
/*     */       }
/*     */       
/*     */ 
/*  74 */       return new DEROctetString(((DefiniteLengthInputStream)this._contentStream).toByteArray()).parser();
/*     */     }
/*     */     
/*     */     
/*     */ 
/*  79 */     throw new RuntimeException("implicit tagging not implemented");
/*     */   }
/*     */   
/*     */   private ASN1EncodableVector loadVector(InputStream in)
/*     */     throws IOException
/*     */   {
/*  85 */     ASN1StreamParser aIn = new ASN1StreamParser(in);
/*  86 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*  87 */     DEREncodable obj = aIn.readObject();
/*     */     
/*  89 */     while (obj != null)
/*     */     {
/*  91 */       v.add(obj.getDERObject());
/*  92 */       obj = aIn.readObject();
/*     */     }
/*     */     
/*  95 */     return v;
/*     */   }
/*     */   
/*     */   private ASN1EncodableVector rLoadVector(InputStream in)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return loadVector(in);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 106 */       throw new IllegalStateException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public DERObject getDERObject()
/*     */   {
/* 112 */     if (this._indefiniteLength)
/*     */     {
/* 114 */       ASN1EncodableVector v = rLoadVector(this._contentStream);
/*     */       
/* 116 */       if (v.size() > 1)
/*     */       {
/* 118 */         return new BERTaggedObject(false, this._tagNumber, new BERSequence(v));
/*     */       }
/* 120 */       if (v.size() == 1)
/*     */       {
/* 122 */         return new BERTaggedObject(true, this._tagNumber, v.get(0));
/*     */       }
/*     */       
/*     */ 
/* 126 */       return new BERTaggedObject(false, this._tagNumber, new BERSequence());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 131 */     if (isConstructed())
/*     */     {
/* 133 */       ASN1EncodableVector v = rLoadVector(this._contentStream);
/*     */       
/* 135 */       if (v.size() == 1)
/*     */       {
/* 137 */         return new DERTaggedObject(true, this._tagNumber, v.get(0));
/*     */       }
/*     */       
/* 140 */       return new DERTaggedObject(false, this._tagNumber, new DERSequence(v));
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 145 */       return new DERTaggedObject(false, this._tagNumber, new DEROctetString(((DefiniteLengthInputStream)this._contentStream).toByteArray()));
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 149 */       throw new IllegalStateException(e.getMessage());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERTaggedObjectParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */