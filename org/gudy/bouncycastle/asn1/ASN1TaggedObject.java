/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ASN1TaggedObject
/*     */   extends ASN1Object
/*     */   implements ASN1TaggedObjectParser
/*     */ {
/*     */   int tagNo;
/*  15 */   boolean empty = false;
/*  16 */   boolean explicit = true;
/*  17 */   DEREncodable obj = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static ASN1TaggedObject getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  23 */     if (explicit)
/*     */     {
/*  25 */       return (ASN1TaggedObject)obj.getObject();
/*     */     }
/*     */     
/*  28 */     throw new IllegalArgumentException("implicitly tagged tagged object");
/*     */   }
/*     */   
/*     */ 
/*     */   public static ASN1TaggedObject getInstance(Object obj)
/*     */   {
/*  34 */     if ((obj == null) || ((obj instanceof ASN1TaggedObject)))
/*     */     {
/*  36 */       return (ASN1TaggedObject)obj;
/*     */     }
/*     */     
/*  39 */     throw new IllegalArgumentException("unknown object in getInstance");
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
/*     */   public ASN1TaggedObject(int tagNo, DEREncodable obj)
/*     */   {
/*  52 */     this.explicit = true;
/*  53 */     this.tagNo = tagNo;
/*  54 */     this.obj = obj;
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
/*     */   public ASN1TaggedObject(boolean explicit, int tagNo, DEREncodable obj)
/*     */   {
/*  72 */     if ((obj instanceof ASN1Choice))
/*     */     {
/*  74 */       this.explicit = true;
/*     */     }
/*     */     else
/*     */     {
/*  78 */       this.explicit = explicit;
/*     */     }
/*     */     
/*  81 */     this.tagNo = tagNo;
/*  82 */     this.obj = obj;
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/*  88 */     if (!(o instanceof ASN1TaggedObject))
/*     */     {
/*  90 */       return false;
/*     */     }
/*     */     
/*  93 */     ASN1TaggedObject other = (ASN1TaggedObject)o;
/*     */     
/*  95 */     if ((this.tagNo != other.tagNo) || (this.empty != other.empty) || (this.explicit != other.explicit))
/*     */     {
/*  97 */       return false;
/*     */     }
/*     */     
/* 100 */     if (this.obj == null)
/*     */     {
/* 102 */       if (other.obj != null)
/*     */       {
/* 104 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 109 */     else if (!this.obj.getDERObject().equals(other.obj.getDERObject()))
/*     */     {
/* 111 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 115 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 120 */     int code = this.tagNo;
/*     */     
/* 122 */     if (this.obj != null)
/*     */     {
/* 124 */       code ^= this.obj.hashCode();
/*     */     }
/*     */     
/* 127 */     return code;
/*     */   }
/*     */   
/*     */   public int getTagNo()
/*     */   {
/* 132 */     return this.tagNo;
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
/*     */   public boolean isExplicit()
/*     */   {
/* 146 */     return this.explicit;
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/* 151 */     return this.empty;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject getObject()
/*     */   {
/* 163 */     if (this.obj != null)
/*     */     {
/* 165 */       return this.obj.getDERObject();
/*     */     }
/*     */     
/* 168 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DEREncodable getObjectParser(int tag, boolean isExplicit)
/*     */   {
/* 180 */     switch (tag)
/*     */     {
/*     */     case 17: 
/* 183 */       return ASN1Set.getInstance(this, isExplicit).parser();
/*     */     case 16: 
/* 185 */       return ASN1Sequence.getInstance(this, isExplicit).parser();
/*     */     case 4: 
/* 187 */       return ASN1OctetString.getInstance(this, isExplicit).parser();
/*     */     }
/*     */     
/* 190 */     if (isExplicit)
/*     */     {
/* 192 */       return getObject();
/*     */     }
/*     */     
/* 195 */     throw new RuntimeException("implicit tagging not implemented for tag: " + tag);
/*     */   }
/*     */   
/*     */   abstract void encode(DEROutputStream paramDEROutputStream)
/*     */     throws IOException;
/*     */   
/*     */   public String toString()
/*     */   {
/* 203 */     return "[" + this.tagNo + "]" + this.obj;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1TaggedObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */