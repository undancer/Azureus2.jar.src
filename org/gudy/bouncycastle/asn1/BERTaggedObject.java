/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
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
/*     */ public class BERTaggedObject
/*     */   extends DERTaggedObject
/*     */ {
/*     */   public BERTaggedObject(int tagNo, DEREncodable obj)
/*     */   {
/*  22 */     super(tagNo, obj);
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
/*     */   public BERTaggedObject(boolean explicit, int tagNo, DEREncodable obj)
/*     */   {
/*  35 */     super(explicit, tagNo, obj);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BERTaggedObject(int tagNo)
/*     */   {
/*  45 */     super(false, tagNo, new BERSequence());
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/*  52 */     if (((out instanceof ASN1OutputStream)) || ((out instanceof BEROutputStream)))
/*     */     {
/*  54 */       out.write(0xA0 | this.tagNo);
/*  55 */       out.write(128);
/*     */       
/*  57 */       if (!this.empty)
/*     */       {
/*  59 */         if (!this.explicit)
/*     */         {
/*  61 */           if ((this.obj instanceof ASN1OctetString))
/*     */           {
/*     */             Enumeration e;
/*     */             Enumeration e;
/*  65 */             if ((this.obj instanceof BERConstructedOctetString))
/*     */             {
/*  67 */               e = ((BERConstructedOctetString)this.obj).getObjects();
/*     */             }
/*     */             else
/*     */             {
/*  71 */               ASN1OctetString octs = (ASN1OctetString)this.obj;
/*  72 */               BERConstructedOctetString berO = new BERConstructedOctetString(octs.getOctets());
/*     */               
/*  74 */               e = berO.getObjects();
/*     */             }
/*     */             
/*  77 */             while (e.hasMoreElements())
/*     */             {
/*  79 */               out.writeObject(e.nextElement());
/*     */             }
/*     */           }
/*  82 */           else if ((this.obj instanceof ASN1Sequence))
/*     */           {
/*  84 */             Enumeration e = ((ASN1Sequence)this.obj).getObjects();
/*     */             
/*  86 */             while (e.hasMoreElements())
/*     */             {
/*  88 */               out.writeObject(e.nextElement());
/*     */             }
/*     */           }
/*  91 */           else if ((this.obj instanceof ASN1Set))
/*     */           {
/*  93 */             Enumeration e = ((ASN1Set)this.obj).getObjects();
/*     */             
/*  95 */             while (e.hasMoreElements())
/*     */             {
/*  97 */               out.writeObject(e.nextElement());
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 102 */             throw new RuntimeException("not implemented: " + this.obj.getClass().getName());
/*     */           }
/*     */           
/*     */         }
/*     */         else {
/* 107 */           out.writeObject(this.obj);
/*     */         }
/*     */       }
/*     */       
/* 111 */       out.write(0);
/* 112 */       out.write(0);
/*     */     }
/*     */     else
/*     */     {
/* 116 */       super.encode(out);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERTaggedObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */