/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERIA5String;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.util.IPAddress;
/*     */ import org.gudy.bouncycastle.util.Strings;
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
/*     */ public class GeneralName
/*     */   extends ASN1Encodable
/*     */   implements ASN1Choice
/*     */ {
/*     */   public static final int otherName = 0;
/*     */   public static final int rfc822Name = 1;
/*     */   public static final int dNSName = 2;
/*     */   public static final int x400Address = 3;
/*     */   public static final int directoryName = 4;
/*     */   public static final int ediPartyName = 5;
/*     */   public static final int uniformResourceIdentifier = 6;
/*     */   public static final int iPAddress = 7;
/*     */   public static final int registeredID = 8;
/*     */   DEREncodable obj;
/*     */   int tag;
/*     */   
/*     */   public GeneralName(X509Name dirName)
/*     */   {
/*  64 */     this.obj = dirName;
/*  65 */     this.tag = 4;
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public GeneralName(DERObject name, int tag)
/*     */   {
/*  74 */     this.obj = name;
/*  75 */     this.tag = tag;
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
/*     */   public GeneralName(int tag, ASN1Encodable name)
/*     */   {
/* 109 */     this.obj = name;
/* 110 */     this.tag = tag;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralName(int tag, String name)
/*     */   {
/* 140 */     this.tag = tag;
/*     */     
/* 142 */     if ((tag == 1) || (tag == 2) || (tag == 6))
/*     */     {
/* 144 */       this.obj = new DERIA5String(name);
/*     */     }
/* 146 */     else if (tag == 8)
/*     */     {
/* 148 */       this.obj = new DERObjectIdentifier(name);
/*     */     }
/* 150 */     else if (tag == 4)
/*     */     {
/* 152 */       this.obj = new X509Name(name);
/*     */     }
/* 154 */     else if (tag == 7)
/*     */     {
/* 156 */       if (IPAddress.isValid(name))
/*     */       {
/* 158 */         this.obj = new DEROctetString(Strings.toUTF8ByteArray(name));
/*     */       }
/*     */       else
/*     */       {
/* 162 */         throw new IllegalArgumentException("IP Address is invalid");
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/* 167 */       throw new IllegalArgumentException("can't process String for tag: " + tag);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static GeneralName getInstance(Object obj)
/*     */   {
/* 174 */     if ((obj == null) || ((obj instanceof GeneralName)))
/*     */     {
/* 176 */       return (GeneralName)obj;
/*     */     }
/*     */     
/* 179 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/* 181 */       ASN1TaggedObject tagObj = (ASN1TaggedObject)obj;
/* 182 */       int tag = tagObj.getTagNo();
/*     */       
/* 184 */       switch (tag)
/*     */       {
/*     */       case 0: 
/* 187 */         return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, false));
/*     */       case 1: 
/* 189 */         return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
/*     */       case 2: 
/* 191 */         return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
/*     */       case 3: 
/* 193 */         throw new IllegalArgumentException("unknown tag: " + tag);
/*     */       case 4: 
/* 195 */         return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, true));
/*     */       case 5: 
/* 197 */         return new GeneralName(tag, ASN1Sequence.getInstance(tagObj, false));
/*     */       case 6: 
/* 199 */         return new GeneralName(tag, DERIA5String.getInstance(tagObj, false));
/*     */       case 7: 
/* 201 */         return new GeneralName(tag, ASN1OctetString.getInstance(tagObj, false));
/*     */       case 8: 
/* 203 */         return new GeneralName(tag, DERObjectIdentifier.getInstance(tagObj, false));
/*     */       }
/*     */       
/*     */     }
/* 207 */     throw new IllegalArgumentException("unknown object in getInstance");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static GeneralName getInstance(ASN1TaggedObject tagObj, boolean explicit)
/*     */   {
/* 214 */     return getInstance(ASN1TaggedObject.getInstance(tagObj, true));
/*     */   }
/*     */   
/*     */   public int getTagNo()
/*     */   {
/* 219 */     return this.tag;
/*     */   }
/*     */   
/*     */   public DEREncodable getName()
/*     */   {
/* 224 */     return this.obj;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 229 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 231 */     buf.append(this.tag);
/* 232 */     buf.append(": ");
/* 233 */     switch (this.tag)
/*     */     {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 6: 
/* 238 */       buf.append(DERIA5String.getInstance(this.obj).getString());
/* 239 */       break;
/*     */     case 4: 
/* 241 */       buf.append(X509Name.getInstance(this.obj).toString());
/* 242 */       break;
/*     */     case 3: case 5: default: 
/* 244 */       buf.append(this.obj.toString());
/*     */     }
/* 246 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 251 */     if (this.tag == 4)
/*     */     {
/* 253 */       return new DERTaggedObject(true, this.tag, this.obj);
/*     */     }
/*     */     
/*     */ 
/* 257 */     return new DERTaggedObject(false, this.tag, this.obj);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/GeneralName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */