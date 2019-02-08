/*     */ package org.gudy.bouncycastle.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.x509.AttCertIssuer;
/*     */ import org.gudy.bouncycastle.asn1.x509.GeneralName;
/*     */ import org.gudy.bouncycastle.asn1.x509.GeneralNames;
/*     */ import org.gudy.bouncycastle.asn1.x509.IssuerSerial;
/*     */ import org.gudy.bouncycastle.asn1.x509.V2Form;
/*     */ import org.gudy.bouncycastle.jce.X509Principal;
/*     */ import org.gudy.bouncycastle.util.Selector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AttributeCertificateIssuer
/*     */   implements CertSelector, Selector
/*     */ {
/*     */   final ASN1Encodable form;
/*     */   
/*     */   AttributeCertificateIssuer(AttCertIssuer issuer)
/*     */   {
/*  37 */     this.form = issuer.getIssuer();
/*     */   }
/*     */   
/*     */   public AttributeCertificateIssuer(X500Principal principal)
/*     */     throws IOException
/*     */   {
/*  43 */     this(new X509Principal(principal.getEncoded()));
/*     */   }
/*     */   
/*     */   public AttributeCertificateIssuer(X509Principal principal)
/*     */   {
/*  48 */     this.form = new V2Form(new GeneralNames(new DERSequence(new GeneralName(principal))));
/*     */   }
/*     */   
/*     */   private Object[] getNames()
/*     */   {
/*     */     GeneralNames name;
/*     */     GeneralNames name;
/*  55 */     if ((this.form instanceof V2Form))
/*     */     {
/*  57 */       name = ((V2Form)this.form).getIssuerName();
/*     */     }
/*     */     else
/*     */     {
/*  61 */       name = (GeneralNames)this.form;
/*     */     }
/*     */     
/*  64 */     GeneralName[] names = name.getNames();
/*     */     
/*  66 */     List l = new ArrayList(names.length);
/*     */     
/*  68 */     for (int i = 0; i != names.length; i++)
/*     */     {
/*  70 */       if (names[i].getTagNo() == 4)
/*     */       {
/*     */         try
/*     */         {
/*  74 */           l.add(new X500Principal(((ASN1Encodable)names[i].getName()).getEncoded()));
/*     */ 
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*  79 */           throw new RuntimeException("badly formed Name object");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  84 */     return l.toArray(new Object[l.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal[] getPrincipals()
/*     */   {
/*  95 */     Object[] p = getNames();
/*  96 */     List l = new ArrayList();
/*     */     
/*  98 */     for (int i = 0; i != p.length; i++)
/*     */     {
/* 100 */       if ((p[i] instanceof Principal))
/*     */       {
/* 102 */         l.add(p[i]);
/*     */       }
/*     */     }
/*     */     
/* 106 */     return (Principal[])l.toArray(new Principal[l.size()]);
/*     */   }
/*     */   
/*     */   private boolean matchesDN(X500Principal subject, GeneralNames targets)
/*     */   {
/* 111 */     GeneralName[] names = targets.getNames();
/*     */     
/* 113 */     for (int i = 0; i != names.length; i++)
/*     */     {
/* 115 */       GeneralName gn = names[i];
/*     */       
/* 117 */       if (gn.getTagNo() == 4)
/*     */       {
/*     */         try
/*     */         {
/* 121 */           if (new X500Principal(((ASN1Encodable)gn.getName()).getEncoded()).equals(subject))
/*     */           {
/* 123 */             return true;
/*     */           }
/*     */         }
/*     */         catch (IOException e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 132 */     return false;
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/* 137 */     return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
/*     */   }
/*     */   
/*     */   public boolean match(Certificate cert)
/*     */   {
/* 142 */     if (!(cert instanceof X509Certificate))
/*     */     {
/* 144 */       return false;
/*     */     }
/*     */     
/* 147 */     X509Certificate x509Cert = (X509Certificate)cert;
/*     */     
/* 149 */     if ((this.form instanceof V2Form))
/*     */     {
/* 151 */       V2Form issuer = (V2Form)this.form;
/* 152 */       if (issuer.getBaseCertificateID() != null)
/*     */       {
/* 154 */         return (issuer.getBaseCertificateID().getSerial().getValue().equals(x509Cert.getSerialNumber())) && (matchesDN(x509Cert.getIssuerX500Principal(), issuer.getBaseCertificateID().getIssuer()));
/*     */       }
/*     */       
/*     */ 
/* 158 */       GeneralNames name = issuer.getIssuerName();
/* 159 */       if (matchesDN(x509Cert.getSubjectX500Principal(), name))
/*     */       {
/* 161 */         return true;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 166 */       GeneralNames name = (GeneralNames)this.form;
/* 167 */       if (matchesDN(x509Cert.getSubjectX500Principal(), name))
/*     */       {
/* 169 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 173 */     return false;
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 178 */     if (obj == this)
/*     */     {
/* 180 */       return true;
/*     */     }
/*     */     
/* 183 */     if (!(obj instanceof AttributeCertificateIssuer))
/*     */     {
/* 185 */       return false;
/*     */     }
/*     */     
/* 188 */     AttributeCertificateIssuer other = (AttributeCertificateIssuer)obj;
/*     */     
/* 190 */     return this.form.equals(other.form);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 195 */     return this.form.hashCode();
/*     */   }
/*     */   
/*     */   public boolean match(Object obj)
/*     */   {
/* 200 */     if (!(obj instanceof X509Certificate))
/*     */     {
/* 202 */       return false;
/*     */     }
/*     */     
/* 205 */     return match((Certificate)obj);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/x509/AttributeCertificateIssuer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */