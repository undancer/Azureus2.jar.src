/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public abstract class ASN1Set
/*     */   extends ASN1Object
/*     */ {
/*  11 */   protected Vector set = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ASN1Set getInstance(Object obj)
/*     */   {
/*  22 */     if ((obj == null) || ((obj instanceof ASN1Set)))
/*     */     {
/*  24 */       return (ASN1Set)obj;
/*     */     }
/*     */     
/*  27 */     throw new IllegalArgumentException("unknown object in getInstance");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ASN1Set getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  50 */     if (explicit)
/*     */     {
/*  52 */       if (!obj.isExplicit())
/*     */       {
/*  54 */         throw new IllegalArgumentException("object implicit - explicit expected.");
/*     */       }
/*     */       
/*  57 */       return (ASN1Set)obj.getObject();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  66 */     if (obj.isExplicit())
/*     */     {
/*  68 */       ASN1Set set = new DERSet(obj.getObject());
/*     */       
/*  70 */       return set;
/*     */     }
/*     */     
/*     */ 
/*  74 */     if ((obj.getObject() instanceof ASN1Set))
/*     */     {
/*  76 */       return (ASN1Set)obj.getObject();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/*  85 */     if ((obj.getObject() instanceof ASN1Sequence))
/*     */     {
/*  87 */       ASN1Sequence s = (ASN1Sequence)obj.getObject();
/*  88 */       Enumeration e = s.getObjects();
/*     */       
/*  90 */       while (e.hasMoreElements())
/*     */       {
/*  92 */         v.add((DEREncodable)e.nextElement());
/*     */       }
/*     */       
/*  95 */       return new DERSet(v, false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 100 */     throw new IllegalArgumentException("unknown object in getInstanceFromTagged");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration getObjects()
/*     */   {
/* 110 */     return this.set.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DEREncodable getObjectAt(int index)
/*     */   {
/* 122 */     return (DEREncodable)this.set.elementAt(index);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 132 */     return this.set.size();
/*     */   }
/*     */   
/*     */   public ASN1SetParser parser()
/*     */   {
/* 137 */     final ASN1Set outer = this;
/*     */     
/* 139 */     new ASN1SetParser()
/*     */     {
/* 141 */       private final int max = ASN1Set.this.size();
/*     */       private int index;
/*     */       
/*     */       public DEREncodable readObject()
/*     */         throws IOException
/*     */       {
/* 147 */         if (this.index == this.max)
/*     */         {
/* 149 */           return null;
/*     */         }
/*     */         
/* 152 */         DEREncodable obj = ASN1Set.this.getObjectAt(this.index++);
/* 153 */         if ((obj instanceof ASN1Sequence))
/*     */         {
/* 155 */           return ((ASN1Sequence)obj).parser();
/*     */         }
/* 157 */         if ((obj instanceof ASN1Set))
/*     */         {
/* 159 */           return ((ASN1Set)obj).parser();
/*     */         }
/*     */         
/* 162 */         return obj;
/*     */       }
/*     */       
/*     */       public DERObject getDERObject()
/*     */       {
/* 167 */         return outer;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 174 */     Enumeration e = getObjects();
/* 175 */     int hashCode = 0;
/*     */     
/* 177 */     while (e.hasMoreElements())
/*     */     {
/* 179 */       hashCode ^= e.nextElement().hashCode();
/*     */     }
/*     */     
/* 182 */     return hashCode;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 188 */     if (!(o instanceof ASN1Set))
/*     */     {
/* 190 */       return false;
/*     */     }
/*     */     
/* 193 */     ASN1Set other = (ASN1Set)o;
/*     */     
/* 195 */     if (size() != other.size())
/*     */     {
/* 197 */       return false;
/*     */     }
/*     */     
/* 200 */     Enumeration s1 = getObjects();
/* 201 */     Enumeration s2 = other.getObjects();
/*     */     
/* 203 */     while (s1.hasMoreElements())
/*     */     {
/* 205 */       DERObject o1 = ((DEREncodable)s1.nextElement()).getDERObject();
/* 206 */       DERObject o2 = ((DEREncodable)s2.nextElement()).getDERObject();
/*     */       
/* 208 */       if ((o1 != o2) && ((o1 == null) || (!o1.equals(o2))))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 213 */         return false;
/*     */       }
/*     */     }
/* 216 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean lessThanOrEqual(byte[] a, byte[] b)
/*     */   {
/* 226 */     if (a.length <= b.length)
/*     */     {
/* 228 */       for (int i = 0; i != a.length; i++)
/*     */       {
/* 230 */         int l = a[i] & 0xFF;
/* 231 */         int r = b[i] & 0xFF;
/*     */         
/* 233 */         if (r > l)
/*     */         {
/* 235 */           return true;
/*     */         }
/* 237 */         if (l > r)
/*     */         {
/* 239 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 243 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 247 */     for (int i = 0; i != b.length; i++)
/*     */     {
/* 249 */       int l = a[i] & 0xFF;
/* 250 */       int r = b[i] & 0xFF;
/*     */       
/* 252 */       if (r > l)
/*     */       {
/* 254 */         return true;
/*     */       }
/* 256 */       if (l > r)
/*     */       {
/* 258 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 262 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] getEncoded(DEREncodable obj)
/*     */   {
/* 269 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 270 */     ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 274 */       aOut.writeObject(obj);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 278 */       throw new IllegalArgumentException("cannot encode object added to SET");
/*     */     }
/*     */     
/* 281 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */   protected void sort()
/*     */   {
/* 286 */     if (this.set.size() > 1)
/*     */     {
/* 288 */       boolean swapped = true;
/* 289 */       int lastSwap = this.set.size() - 1;
/*     */       
/* 291 */       while (swapped)
/*     */       {
/* 293 */         int index = 0;
/* 294 */         int swapIndex = 0;
/* 295 */         byte[] a = getEncoded((DEREncodable)this.set.elementAt(0));
/*     */         
/* 297 */         swapped = false;
/*     */         
/* 299 */         while (index != lastSwap)
/*     */         {
/* 301 */           byte[] b = getEncoded((DEREncodable)this.set.elementAt(index + 1));
/*     */           
/* 303 */           if (lessThanOrEqual(a, b))
/*     */           {
/* 305 */             a = b;
/*     */           }
/*     */           else
/*     */           {
/* 309 */             Object o = this.set.elementAt(index);
/*     */             
/* 311 */             this.set.setElementAt(this.set.elementAt(index + 1), index);
/* 312 */             this.set.setElementAt(o, index + 1);
/*     */             
/* 314 */             swapped = true;
/* 315 */             swapIndex = index;
/*     */           }
/*     */           
/* 318 */           index++;
/*     */         }
/*     */         
/* 321 */         lastSwap = swapIndex;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addObject(DEREncodable obj)
/*     */   {
/* 329 */     this.set.addElement(obj);
/*     */   }
/*     */   
/*     */   abstract void encode(DEROutputStream paramDEROutputStream)
/*     */     throws IOException;
/*     */   
/*     */   public String toString()
/*     */   {
/* 337 */     return this.set.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Set.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */