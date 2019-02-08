/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public abstract class ASN1Sequence
/*     */   extends ASN1Object
/*     */ {
/*  10 */   private Vector seq = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ASN1Sequence getInstance(Object obj)
/*     */   {
/*  21 */     if ((obj == null) || ((obj instanceof ASN1Sequence)))
/*     */     {
/*  23 */       return (ASN1Sequence)obj;
/*     */     }
/*     */     
/*  26 */     throw new IllegalArgumentException("unknown object in getInstance");
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
/*     */   public static ASN1Sequence getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  49 */     if (explicit)
/*     */     {
/*  51 */       if (!obj.isExplicit())
/*     */       {
/*  53 */         throw new IllegalArgumentException("object implicit - explicit expected.");
/*     */       }
/*     */       
/*  56 */       return (ASN1Sequence)obj.getObject();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */     if (obj.isExplicit())
/*     */     {
/*  67 */       if ((obj instanceof BERTaggedObject))
/*     */       {
/*  69 */         return new BERSequence(obj.getObject());
/*     */       }
/*     */       
/*     */ 
/*  73 */       return new DERSequence(obj.getObject());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  78 */     if ((obj.getObject() instanceof ASN1Sequence))
/*     */     {
/*  80 */       return (ASN1Sequence)obj.getObject();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  85 */     throw new IllegalArgumentException("unknown object in getInstanceFromTagged");
/*     */   }
/*     */   
/*     */ 
/*     */   public Enumeration getObjects()
/*     */   {
/*  91 */     return this.seq.elements();
/*     */   }
/*     */   
/*     */   public ASN1SequenceParser parser()
/*     */   {
/*  96 */     final ASN1Sequence outer = this;
/*     */     
/*  98 */     new ASN1SequenceParser()
/*     */     {
/* 100 */       private final int max = ASN1Sequence.this.size();
/*     */       private int index;
/*     */       
/*     */       public DEREncodable readObject()
/*     */         throws IOException
/*     */       {
/* 106 */         if (this.index == this.max)
/*     */         {
/* 108 */           return null;
/*     */         }
/*     */         
/* 111 */         DEREncodable obj = ASN1Sequence.this.getObjectAt(this.index++);
/* 112 */         if ((obj instanceof ASN1Sequence))
/*     */         {
/* 114 */           return ((ASN1Sequence)obj).parser();
/*     */         }
/* 116 */         if ((obj instanceof ASN1Set))
/*     */         {
/* 118 */           return ((ASN1Set)obj).parser();
/*     */         }
/*     */         
/* 121 */         return obj;
/*     */       }
/*     */       
/*     */       public DERObject getDERObject()
/*     */       {
/* 126 */         return outer;
/*     */       }
/*     */     };
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
/* 140 */     return (DEREncodable)this.seq.elementAt(index);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 150 */     return this.seq.size();
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 155 */     Enumeration e = getObjects();
/* 156 */     int hashCode = 0;
/*     */     
/* 158 */     while (e.hasMoreElements())
/*     */     {
/* 160 */       Object o = e.nextElement();
/*     */       
/* 162 */       if (o != null)
/*     */       {
/* 164 */         hashCode ^= o.hashCode();
/*     */       }
/*     */     }
/*     */     
/* 168 */     return hashCode;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 174 */     if (!(o instanceof ASN1Sequence))
/*     */     {
/* 176 */       return false;
/*     */     }
/*     */     
/* 179 */     ASN1Sequence other = (ASN1Sequence)o;
/*     */     
/* 181 */     if (size() != other.size())
/*     */     {
/* 183 */       return false;
/*     */     }
/*     */     
/* 186 */     Enumeration s1 = getObjects();
/* 187 */     Enumeration s2 = other.getObjects();
/*     */     
/* 189 */     while (s1.hasMoreElements())
/*     */     {
/* 191 */       DERObject o1 = ((DEREncodable)s1.nextElement()).getDERObject();
/* 192 */       DERObject o2 = ((DEREncodable)s2.nextElement()).getDERObject();
/*     */       
/* 194 */       if ((o1 != o2) && ((o1 == null) || (!o1.equals(o2))))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 199 */         return false;
/*     */       }
/*     */     }
/* 202 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addObject(DEREncodable obj)
/*     */   {
/* 208 */     this.seq.addElement(obj);
/*     */   }
/*     */   
/*     */   abstract void encode(DEROutputStream paramDEROutputStream)
/*     */     throws IOException;
/*     */   
/*     */   public String toString()
/*     */   {
/* 216 */     return this.seq.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Sequence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */