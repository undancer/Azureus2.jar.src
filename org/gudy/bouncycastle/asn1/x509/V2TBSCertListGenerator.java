/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERUTCTime;
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
/*     */ 
/*     */ 
/*     */ public class V2TBSCertListGenerator
/*     */ {
/*  48 */   DERInteger version = new DERInteger(1);
/*     */   AlgorithmIdentifier signature;
/*     */   X509Name issuer;
/*     */   Time thisUpdate;
/*  52 */   Time nextUpdate = null;
/*  53 */   X509Extensions extensions = null;
/*  54 */   private Vector crlentries = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSignature(AlgorithmIdentifier signature)
/*     */   {
/*  64 */     this.signature = signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIssuer(X509Name issuer)
/*     */   {
/*  70 */     this.issuer = issuer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setThisUpdate(DERUTCTime thisUpdate)
/*     */   {
/*  76 */     this.thisUpdate = new Time(thisUpdate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNextUpdate(DERUTCTime nextUpdate)
/*     */   {
/*  82 */     this.nextUpdate = new Time(nextUpdate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setThisUpdate(Time thisUpdate)
/*     */   {
/*  88 */     this.thisUpdate = thisUpdate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNextUpdate(Time nextUpdate)
/*     */   {
/*  94 */     this.nextUpdate = nextUpdate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addCRLEntry(ASN1Sequence crlEntry)
/*     */   {
/* 100 */     if (this.crlentries == null)
/*     */     {
/* 102 */       this.crlentries = new Vector();
/*     */     }
/*     */     
/* 105 */     this.crlentries.addElement(crlEntry);
/*     */   }
/*     */   
/*     */   public void addCRLEntry(DERInteger userCertificate, DERUTCTime revocationDate, int reason)
/*     */   {
/* 110 */     addCRLEntry(userCertificate, new Time(revocationDate), reason);
/*     */   }
/*     */   
/*     */   public void addCRLEntry(DERInteger userCertificate, Time revocationDate, int reason)
/*     */   {
/* 115 */     addCRLEntry(userCertificate, revocationDate, reason, null);
/*     */   }
/*     */   
/*     */   public void addCRLEntry(DERInteger userCertificate, Time revocationDate, int reason, DERGeneralizedTime invalidityDate)
/*     */   {
/* 120 */     Vector extOids = new Vector();
/* 121 */     Vector extValues = new Vector();
/*     */     
/* 123 */     if (reason != 0)
/*     */     {
/* 125 */       CRLReason crlReason = new CRLReason(reason);
/*     */       
/*     */       try
/*     */       {
/* 129 */         extOids.addElement(X509Extensions.ReasonCode);
/* 130 */         extValues.addElement(new X509Extension(false, new DEROctetString(crlReason.getEncoded())));
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 134 */         throw new IllegalArgumentException("error encoding reason: " + e);
/*     */       }
/*     */     }
/*     */     
/* 138 */     if (invalidityDate != null)
/*     */     {
/*     */       try
/*     */       {
/* 142 */         extOids.addElement(X509Extensions.InvalidityDate);
/* 143 */         extValues.addElement(new X509Extension(false, new DEROctetString(invalidityDate.getEncoded())));
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 147 */         throw new IllegalArgumentException("error encoding invalidityDate: " + e);
/*     */       }
/*     */     }
/*     */     
/* 151 */     if (extOids.size() != 0)
/*     */     {
/* 153 */       addCRLEntry(userCertificate, revocationDate, new X509Extensions(extOids, extValues));
/*     */     }
/*     */     else
/*     */     {
/* 157 */       addCRLEntry(userCertificate, revocationDate, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addCRLEntry(DERInteger userCertificate, Time revocationDate, X509Extensions extensions)
/*     */   {
/* 163 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 165 */     v.add(userCertificate);
/* 166 */     v.add(revocationDate);
/*     */     
/* 168 */     if (extensions != null)
/*     */     {
/* 170 */       v.add(extensions);
/*     */     }
/*     */     
/* 173 */     addCRLEntry(new DERSequence(v));
/*     */   }
/*     */   
/*     */ 
/*     */   public void setExtensions(X509Extensions extensions)
/*     */   {
/* 179 */     this.extensions = extensions;
/*     */   }
/*     */   
/*     */   public TBSCertList generateTBSCertList()
/*     */   {
/* 184 */     if ((this.signature == null) || (this.issuer == null) || (this.thisUpdate == null))
/*     */     {
/* 186 */       throw new IllegalStateException("Not all mandatory fields set in V2 TBSCertList generator.");
/*     */     }
/*     */     
/* 189 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 191 */     v.add(this.version);
/* 192 */     v.add(this.signature);
/* 193 */     v.add(this.issuer);
/*     */     
/* 195 */     v.add(this.thisUpdate);
/* 196 */     if (this.nextUpdate != null)
/*     */     {
/* 198 */       v.add(this.nextUpdate);
/*     */     }
/*     */     
/*     */ 
/* 202 */     if (this.crlentries != null)
/*     */     {
/* 204 */       ASN1EncodableVector certs = new ASN1EncodableVector();
/* 205 */       Enumeration it = this.crlentries.elements();
/* 206 */       while (it.hasMoreElements())
/*     */       {
/* 208 */         certs.add((ASN1Sequence)it.nextElement());
/*     */       }
/* 210 */       v.add(new DERSequence(certs));
/*     */     }
/*     */     
/* 213 */     if (this.extensions != null)
/*     */     {
/* 215 */       v.add(new DERTaggedObject(0, this.extensions));
/*     */     }
/*     */     
/* 218 */     return new TBSCertList(new DERSequence(v));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/V2TBSCertListGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */