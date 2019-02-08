/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public class BERInputStream
/*     */   extends DERInputStream
/*     */ {
/*  15 */   private static final DERObject END_OF_STREAM = new DERObject()
/*     */   {
/*     */ 
/*     */     void encode(DEROutputStream out)
/*     */       throws IOException
/*     */     {
/*  21 */       throw new IOException("Eeek!");
/*     */     }
/*     */     
/*     */     public int hashCode() {
/*  25 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/*  30 */       return o == this;
/*     */     }
/*     */   };
/*     */   
/*     */   public BERInputStream(InputStream is)
/*     */   {
/*  36 */     super(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] readIndefiniteLengthFully()
/*     */     throws IOException
/*     */   {
/*  45 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/*     */ 
/*  48 */     int b1 = read();
/*     */     int b;
/*  50 */     while ((b = read()) >= 0)
/*     */     {
/*  52 */       if ((b1 == 0) && (b == 0)) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  57 */       bOut.write(b1);
/*  58 */       b1 = b;
/*     */     }
/*     */     
/*  61 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */   private BERConstructedOctetString buildConstructedOctetString()
/*     */     throws IOException
/*     */   {
/*  67 */     Vector octs = new Vector();
/*     */     
/*     */     for (;;)
/*     */     {
/*  71 */       DERObject o = readObject();
/*     */       
/*  73 */       if (o == END_OF_STREAM) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  78 */       octs.addElement(o);
/*     */     }
/*     */     
/*  81 */     return new BERConstructedOctetString(octs);
/*     */   }
/*     */   
/*     */   public DERObject readObject()
/*     */     throws IOException
/*     */   {
/*  87 */     int tag = read();
/*  88 */     if (tag == -1)
/*     */     {
/*  90 */       throw new EOFException();
/*     */     }
/*     */     
/*  93 */     int length = readLength();
/*     */     
/*  95 */     if (length < 0) {
/*     */       BERConstructedSequence seq;
/*  97 */       switch (tag)
/*     */       {
/*     */       case 5: 
/* 100 */         return null;
/*     */       case 48: 
/* 102 */         seq = new BERConstructedSequence();
/*     */         
/*     */         for (;;)
/*     */         {
/* 106 */           DERObject obj = readObject();
/*     */           
/* 108 */           if (obj == END_OF_STREAM) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 113 */           seq.addObject(obj);
/*     */         }
/* 115 */         return seq;
/*     */       case 36: 
/* 117 */         return buildConstructedOctetString();
/*     */       case 49: 
/* 119 */         ASN1EncodableVector v = new ASN1EncodableVector();
/*     */         
/*     */         for (;;)
/*     */         {
/* 123 */           DERObject obj = readObject();
/*     */           
/* 125 */           if (obj == END_OF_STREAM) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 130 */           v.add(obj);
/*     */         }
/* 132 */         return new BERSet(v);
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 137 */       if ((tag & 0x80) != 0)
/*     */       {
/* 139 */         if ((tag & 0x1F) == 31)
/*     */         {
/* 141 */           throw new IOException("unsupported high tag encountered");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 147 */         if ((tag & 0x20) == 0)
/*     */         {
/* 149 */           byte[] bytes = readIndefiniteLengthFully();
/*     */           
/* 151 */           return new BERTaggedObject(false, tag & 0x1F, new DEROctetString(bytes));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 157 */         DERObject dObj = readObject();
/*     */         
/* 159 */         if (dObj == END_OF_STREAM)
/*     */         {
/* 161 */           return new DERTaggedObject(tag & 0x1F);
/*     */         }
/*     */         
/* 164 */         DERObject next = readObject();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */         if (next == END_OF_STREAM)
/*     */         {
/* 172 */           return new BERTaggedObject(tag & 0x1F, dObj);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 178 */         seq = new BERConstructedSequence();
/*     */         
/* 180 */         seq.addObject(dObj);
/*     */         
/*     */         do
/*     */         {
/* 184 */           seq.addObject(next);
/* 185 */           next = readObject();
/*     */         }
/* 187 */         while (next != END_OF_STREAM);
/*     */         
/* 189 */         return new BERTaggedObject(false, tag & 0x1F, seq);
/*     */       }
/*     */       
/* 192 */       throw new IOException("unknown BER object encountered");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 197 */     if ((tag == 0) && (length == 0))
/*     */     {
/* 199 */       return END_OF_STREAM;
/*     */     }
/*     */     
/* 202 */     byte[] bytes = new byte[length];
/*     */     
/* 204 */     readFully(bytes);
/*     */     
/* 206 */     return buildObject(tag, bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */