/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERUTF8String;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IetfAttrSyntax
/*     */   extends ASN1Encodable
/*     */ {
/*     */   public static final int VALUE_OCTETS = 1;
/*     */   public static final int VALUE_OID = 2;
/*     */   public static final int VALUE_UTF8 = 3;
/*  28 */   GeneralNames policyAuthority = null;
/*  29 */   Vector values = new Vector();
/*  30 */   int valueChoice = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IetfAttrSyntax(ASN1Sequence seq)
/*     */   {
/*  37 */     int i = 0;
/*     */     
/*  39 */     if ((seq.getObjectAt(0) instanceof ASN1TaggedObject))
/*     */     {
/*  41 */       this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject)seq.getObjectAt(0), false);
/*  42 */       i++;
/*     */     }
/*  44 */     else if (seq.size() == 2)
/*     */     {
/*  46 */       this.policyAuthority = GeneralNames.getInstance(seq.getObjectAt(0));
/*  47 */       i++;
/*     */     }
/*     */     
/*  50 */     if (!(seq.getObjectAt(i) instanceof ASN1Sequence))
/*     */     {
/*  52 */       throw new IllegalArgumentException("Non-IetfAttrSyntax encoding");
/*     */     }
/*     */     
/*  55 */     seq = (ASN1Sequence)seq.getObjectAt(i);
/*     */     
/*  57 */     for (Enumeration e = seq.getObjects(); e.hasMoreElements();)
/*     */     {
/*  59 */       DERObject obj = (DERObject)e.nextElement();
/*     */       
/*     */       int type;
/*  62 */       if ((obj instanceof DERObjectIdentifier))
/*     */       {
/*  64 */         type = 2;
/*     */       } else { int type;
/*  66 */         if ((obj instanceof DERUTF8String))
/*     */         {
/*  68 */           type = 3;
/*     */         } else { int type;
/*  70 */           if ((obj instanceof DEROctetString))
/*     */           {
/*  72 */             type = 1;
/*     */           }
/*     */           else
/*     */           {
/*  76 */             throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax"); }
/*     */         } }
/*     */       int type;
/*  79 */       if (this.valueChoice < 0)
/*     */       {
/*  81 */         this.valueChoice = type;
/*     */       }
/*     */       
/*  84 */       if (type != this.valueChoice)
/*     */       {
/*  86 */         throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax");
/*     */       }
/*     */       
/*  89 */       this.values.addElement(obj);
/*     */     }
/*     */   }
/*     */   
/*     */   public GeneralNames getPolicyAuthority()
/*     */   {
/*  95 */     return this.policyAuthority;
/*     */   }
/*     */   
/*     */   public int getValueType()
/*     */   {
/* 100 */     return this.valueChoice;
/*     */   }
/*     */   
/*     */   public Object[] getValues()
/*     */   {
/* 105 */     if (getValueType() == 1)
/*     */     {
/* 107 */       ASN1OctetString[] tmp = new ASN1OctetString[this.values.size()];
/*     */       
/* 109 */       for (int i = 0; i != tmp.length; i++)
/*     */       {
/* 111 */         tmp[i] = ((ASN1OctetString)this.values.elementAt(i));
/*     */       }
/*     */       
/* 114 */       return tmp;
/*     */     }
/* 116 */     if (getValueType() == 2)
/*     */     {
/* 118 */       DERObjectIdentifier[] tmp = new DERObjectIdentifier[this.values.size()];
/*     */       
/* 120 */       for (int i = 0; i != tmp.length; i++)
/*     */       {
/* 122 */         tmp[i] = ((DERObjectIdentifier)this.values.elementAt(i));
/*     */       }
/*     */       
/* 125 */       return tmp;
/*     */     }
/*     */     
/*     */ 
/* 129 */     DERUTF8String[] tmp = new DERUTF8String[this.values.size()];
/*     */     
/* 131 */     for (int i = 0; i != tmp.length; i++)
/*     */     {
/* 133 */       tmp[i] = ((DERUTF8String)this.values.elementAt(i));
/*     */     }
/*     */     
/* 136 */     return tmp;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 157 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 159 */     if (this.policyAuthority != null)
/*     */     {
/* 161 */       v.add(new DERTaggedObject(0, this.policyAuthority));
/*     */     }
/*     */     
/* 164 */     ASN1EncodableVector v2 = new ASN1EncodableVector();
/*     */     
/* 166 */     for (Enumeration i = this.values.elements(); i.hasMoreElements();)
/*     */     {
/* 168 */       v2.add((ASN1Encodable)i.nextElement());
/*     */     }
/*     */     
/* 171 */     v.add(new DERSequence(v2));
/*     */     
/* 173 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/IetfAttrSyntax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */