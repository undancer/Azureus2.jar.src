/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GeneralNames
/*    */   extends ASN1Encodable
/*    */ {
/*    */   ASN1Sequence seq;
/*    */   
/*    */   public static GeneralNames getInstance(Object obj)
/*    */   {
/* 19 */     if ((obj == null) || ((obj instanceof GeneralNames)))
/*    */     {
/* 21 */       return (GeneralNames)obj;
/*    */     }
/*    */     
/* 24 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 26 */       return new GeneralNames((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 29 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static GeneralNames getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 36 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public GeneralNames(GeneralName name)
/*    */   {
/* 47 */     this.seq = new DERSequence(name);
/*    */   }
/*    */   
/*    */ 
/*    */   public GeneralNames(ASN1Sequence seq)
/*    */   {
/* 53 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public GeneralName[] getNames()
/*    */   {
/* 58 */     GeneralName[] names = new GeneralName[this.seq.size()];
/*    */     
/* 60 */     for (int i = 0; i != this.seq.size(); i++)
/*    */     {
/* 62 */       names[i] = GeneralName.getInstance(this.seq.getObjectAt(i));
/*    */     }
/*    */     
/* 65 */     return names;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObject toASN1Object()
/*    */   {
/* 76 */     return this.seq;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 81 */     StringBuilder buf = new StringBuilder();
/* 82 */     String sep = System.getProperty("line.separator");
/* 83 */     GeneralName[] names = getNames();
/*    */     
/* 85 */     buf.append("GeneralNames:");
/* 86 */     buf.append(sep);
/*    */     
/* 88 */     for (int i = 0; i != names.length; i++)
/*    */     {
/* 90 */       buf.append("    ");
/* 91 */       buf.append(names[i]);
/* 92 */       buf.append(sep);
/*    */     }
/* 94 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/GeneralNames.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */