/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.X509CRLEntry;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.TBSCertList.CRLEntry;
/*     */ import org.gudy.bouncycastle.asn1.x509.Time;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extension;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Extensions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509CRLEntryObject
/*     */   extends X509CRLEntry
/*     */ {
/*     */   private TBSCertList.CRLEntry c;
/*     */   
/*     */   public X509CRLEntryObject(TBSCertList.CRLEntry c)
/*     */   {
/*  35 */     this.c = c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/*  44 */     Set extns = getCriticalExtensionOIDs();
/*  45 */     if ((extns != null) && (!extns.isEmpty()))
/*     */     {
/*  47 */       return true;
/*     */     }
/*     */     
/*  50 */     return false;
/*     */   }
/*     */   
/*     */   private Set getExtensionOIDs(boolean critical)
/*     */   {
/*  55 */     X509Extensions extensions = this.c.getExtensions();
/*     */     
/*  57 */     if (extensions != null)
/*     */     {
/*  59 */       HashSet set = new HashSet();
/*  60 */       Enumeration e = extensions.oids();
/*     */       
/*  62 */       while (e.hasMoreElements())
/*     */       {
/*  64 */         DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/*  65 */         X509Extension ext = extensions.getExtension(oid);
/*     */         
/*  67 */         if (critical == ext.isCritical())
/*     */         {
/*  69 */           set.add(oid.getId());
/*     */         }
/*     */       }
/*     */       
/*  73 */       return set;
/*     */     }
/*     */     
/*  76 */     return null;
/*     */   }
/*     */   
/*     */   public Set getCriticalExtensionOIDs()
/*     */   {
/*  81 */     return getExtensionOIDs(true);
/*     */   }
/*     */   
/*     */   public Set getNonCriticalExtensionOIDs()
/*     */   {
/*  86 */     return getExtensionOIDs(false);
/*     */   }
/*     */   
/*     */   public byte[] getExtensionValue(String oid)
/*     */   {
/*  91 */     X509Extensions exts = this.c.getExtensions();
/*     */     
/*  93 */     if (exts != null)
/*     */     {
/*  95 */       X509Extension ext = exts.getExtension(new DERObjectIdentifier(oid));
/*     */       
/*  97 */       if (ext != null)
/*     */       {
/*  99 */         return ext.getValue().getOctets();
/*     */       }
/*     */     }
/*     */     
/* 103 */     return null;
/*     */   }
/*     */   
/*     */   public byte[] getEncoded()
/*     */     throws CRLException
/*     */   {
/* 109 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 110 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 114 */       dOut.writeObject(this.c);
/*     */       
/* 116 */       return bOut.toByteArray();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 120 */       throw new CRLException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public BigInteger getSerialNumber()
/*     */   {
/* 126 */     return this.c.getUserCertificate().getValue();
/*     */   }
/*     */   
/*     */   public Date getRevocationDate()
/*     */   {
/* 131 */     return this.c.getRevocationDate().getDate();
/*     */   }
/*     */   
/*     */   public boolean hasExtensions()
/*     */   {
/* 136 */     return this.c.getExtensions() != null;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 141 */     StringBuilder buf = new StringBuilder();
/* 142 */     String nl = System.getProperty("line.separator");
/*     */     
/* 144 */     buf.append("      userCertificate: ").append(getSerialNumber()).append(nl);
/* 145 */     buf.append("       revocationDate: ").append(getRevocationDate()).append(nl);
/*     */     
/*     */ 
/* 148 */     X509Extensions extensions = this.c.getExtensions();
/*     */     
/* 150 */     if (extensions != null)
/*     */     {
/* 152 */       Enumeration e = extensions.oids();
/* 153 */       if (e.hasMoreElements())
/*     */       {
/* 155 */         buf.append("   crlEntryExtensions:").append(nl);
/*     */         
/* 157 */         while (e.hasMoreElements())
/*     */         {
/* 159 */           DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
/* 160 */           X509Extension ext = extensions.getExtension(oid);
/* 161 */           buf.append(ext);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 166 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/X509CRLEntryObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */