/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BERConstructedOctetString
/*     */   extends DEROctetString
/*     */ {
/*     */   private static final int MAX_LENGTH = 1000;
/*     */   private Vector octs;
/*     */   
/*     */   private static byte[] toBytes(Vector octs)
/*     */   {
/*  19 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/*  21 */     for (int i = 0; i != octs.size(); i++)
/*     */     {
/*     */       try
/*     */       {
/*  25 */         DEROctetString o = (DEROctetString)octs.elementAt(i);
/*     */         
/*  27 */         bOut.write(o.getOctets());
/*     */       }
/*     */       catch (ClassCastException e)
/*     */       {
/*  31 */         throw new IllegalArgumentException(octs.elementAt(i).getClass().getName() + " found in input should only contain DEROctetString");
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*  35 */         throw new IllegalArgumentException("exception converting octets " + e.toString());
/*     */       }
/*     */     }
/*     */     
/*  39 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BERConstructedOctetString(byte[] string)
/*     */   {
/*  50 */     super(string);
/*     */   }
/*     */   
/*     */ 
/*     */   public BERConstructedOctetString(Vector octs)
/*     */   {
/*  56 */     super(toBytes(octs));
/*     */     
/*  58 */     this.octs = octs;
/*     */   }
/*     */   
/*     */ 
/*     */   public BERConstructedOctetString(DERObject obj)
/*     */   {
/*  64 */     super(obj);
/*     */   }
/*     */   
/*     */ 
/*     */   public BERConstructedOctetString(DEREncodable obj)
/*     */   {
/*  70 */     super(obj.getDERObject());
/*     */   }
/*     */   
/*     */   public byte[] getOctets()
/*     */   {
/*  75 */     return this.string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration getObjects()
/*     */   {
/*  83 */     if (this.octs == null)
/*     */     {
/*  85 */       return generateOcts().elements();
/*     */     }
/*     */     
/*  88 */     return this.octs.elements();
/*     */   }
/*     */   
/*     */   private Vector generateOcts()
/*     */   {
/*  93 */     int start = 0;
/*  94 */     int end = 0;
/*  95 */     Vector vec = new Vector();
/*     */     
/*  97 */     while (end + 1 < this.string.length)
/*     */     {
/*  99 */       if ((this.string[end] == 0) && (this.string[(end + 1)] == 0))
/*     */       {
/* 101 */         byte[] nStr = new byte[end - start + 1];
/*     */         
/* 103 */         System.arraycopy(this.string, start, nStr, 0, nStr.length);
/*     */         
/* 105 */         vec.addElement(new DEROctetString(nStr));
/* 106 */         start = end + 1;
/*     */       }
/* 108 */       end++;
/*     */     }
/*     */     
/* 111 */     byte[] nStr = new byte[this.string.length - start];
/*     */     
/* 113 */     System.arraycopy(this.string, start, nStr, 0, nStr.length);
/*     */     
/* 115 */     vec.addElement(new DEROctetString(nStr));
/*     */     
/* 117 */     return vec;
/*     */   }
/*     */   
/*     */ 
/*     */   public void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 124 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*     */     {
/* 126 */       out.write(36);
/*     */       
/* 128 */       out.write(128);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 133 */       if (this.octs != null)
/*     */       {
/* 135 */         for (int i = 0; i != this.octs.size(); i++)
/*     */         {
/* 137 */           out.writeObject(this.octs.elementAt(i));
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 142 */         for (int i = 0; i < this.string.length; i += 1000)
/*     */         {
/*     */           int end;
/*     */           int end;
/* 146 */           if (i + 1000 > this.string.length)
/*     */           {
/* 148 */             end = this.string.length;
/*     */           }
/*     */           else
/*     */           {
/* 152 */             end = i + 1000;
/*     */           }
/*     */           
/* 155 */           byte[] nStr = new byte[end - i];
/*     */           
/* 157 */           System.arraycopy(this.string, i, nStr, 0, nStr.length);
/*     */           
/* 159 */           out.writeObject(new DEROctetString(nStr));
/*     */         }
/*     */       }
/*     */       
/* 163 */       out.write(0);
/* 164 */       out.write(0);
/*     */     }
/*     */     else
/*     */     {
/* 168 */       super.encode(out);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERConstructedOctetString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */