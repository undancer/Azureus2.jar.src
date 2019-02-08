/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DERApplicationSpecific
/*     */   extends ASN1Object
/*     */ {
/*     */   private int tag;
/*     */   private byte[] octets;
/*     */   
/*     */   public DERApplicationSpecific(int tag, byte[] octets)
/*     */   {
/*  19 */     this.tag = tag;
/*  20 */     this.octets = octets;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DERApplicationSpecific(int tag, DEREncodable object)
/*     */     throws IOException
/*     */   {
/*  28 */     this(true, tag, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERApplicationSpecific(boolean explicit, int tag, DEREncodable object)
/*     */     throws IOException
/*     */   {
/*  37 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*  38 */     DEROutputStream dos = new DEROutputStream(bOut);
/*     */     
/*  40 */     dos.writeObject(object);
/*     */     
/*  42 */     byte[] data = bOut.toByteArray();
/*     */     
/*  44 */     if (tag >= 31)
/*     */     {
/*  46 */       throw new IOException("unsupported tag number");
/*     */     }
/*     */     
/*  49 */     if (explicit)
/*     */     {
/*  51 */       this.tag = (tag | 0x20);
/*  52 */       this.octets = data;
/*     */     }
/*     */     else
/*     */     {
/*  56 */       this.tag = tag;
/*  57 */       int lenBytes = getLengthOfLength(data);
/*  58 */       byte[] tmp = new byte[data.length - lenBytes];
/*  59 */       System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
/*  60 */       this.octets = tmp;
/*     */     }
/*     */   }
/*     */   
/*     */   private int getLengthOfLength(byte[] data)
/*     */   {
/*  66 */     int count = 2;
/*     */     
/*  68 */     while ((data[(count - 1)] & 0x80) != 0)
/*     */     {
/*  70 */       count++;
/*     */     }
/*     */     
/*  73 */     return count;
/*     */   }
/*     */   
/*     */   public boolean isConstructed()
/*     */   {
/*  78 */     return (this.tag & 0x20) != 0;
/*     */   }
/*     */   
/*     */   public byte[] getContents()
/*     */   {
/*  83 */     return this.octets;
/*     */   }
/*     */   
/*     */   public int getApplicationTag()
/*     */   {
/*  88 */     return this.tag;
/*     */   }
/*     */   
/*     */   public DERObject getObject()
/*     */     throws IOException
/*     */   {
/*  94 */     return new ASN1InputStream(getContents()).readObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject getObject(int derTagNo)
/*     */     throws IOException
/*     */   {
/* 107 */     if (this.tag >= 31)
/*     */     {
/* 109 */       throw new IOException("unsupported tag number");
/*     */     }
/*     */     
/* 112 */     byte[] tmp = getEncoded();
/*     */     
/* 114 */     tmp[0] = ((byte)derTagNo);
/*     */     
/* 116 */     return new ASN1InputStream(tmp).readObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 124 */     out.writeEncoded(0x40 | this.tag, this.octets);
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 130 */     if (!(o instanceof DERApplicationSpecific))
/*     */     {
/* 132 */       return false;
/*     */     }
/*     */     
/* 135 */     DERApplicationSpecific other = (DERApplicationSpecific)o;
/*     */     
/* 137 */     if (this.tag != other.tag)
/*     */     {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     if (this.octets.length != other.octets.length)
/*     */     {
/* 144 */       return false;
/*     */     }
/*     */     
/* 147 */     for (int i = 0; i < this.octets.length; i++)
/*     */     {
/* 149 */       if (this.octets[i] != other.octets[i])
/*     */       {
/* 151 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 155 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 160 */     byte[] b = getContents();
/* 161 */     int value = 0;
/*     */     
/* 163 */     for (int i = 0; i != b.length; i++)
/*     */     {
/* 165 */       value ^= (b[i] & 0xFF) << i % 4;
/*     */     }
/*     */     
/* 168 */     return value ^ getApplicationTag();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERApplicationSpecific.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */