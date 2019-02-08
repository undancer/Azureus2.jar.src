/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKIXCertPath
/*     */   extends CertPath
/*     */ {
/*     */   static final List certPathEncodings;
/*     */   private List certificates;
/*     */   
/*     */   static
/*     */   {
/*  40 */     List encodings = new ArrayList();
/*  41 */     encodings.add("PkiPath");
/*  42 */     certPathEncodings = Collections.unmodifiableList(encodings);
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
/*     */   PKIXCertPath(List certificates)
/*     */   {
/*  55 */     super("X.509");
/*  56 */     this.certificates = new ArrayList(certificates);
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
/*     */   PKIXCertPath(InputStream inStream, String encoding)
/*     */     throws CertificateException
/*     */   {
/*  71 */     super("X.509");
/*     */     try {
/*  73 */       if (encoding.equals("PkiPath"))
/*     */       {
/*  75 */         DERInputStream derInStream = new DERInputStream(inStream);
/*  76 */         DERObject derObject = derInStream.readObject();
/*  77 */         if ((derObject == null) || (!(derObject instanceof ASN1Sequence)))
/*     */         {
/*  79 */           throw new CertificateException("input stream does not contain a ASN1 SEQUENCE while reading PkiPath encoded data to load CertPath");
/*     */         }
/*  81 */         Enumeration enumx = ((ASN1Sequence)derObject).getObjects();
/*     */         
/*     */ 
/*     */ 
/*  85 */         this.certificates = new ArrayList();
/*  86 */         CertificateFactory certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
/*  87 */         while (enumx.hasMoreElements()) {
/*  88 */           ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/*  89 */           DEROutputStream derOutStream = new DEROutputStream(outStream);
/*     */           
/*  91 */           derOutStream.writeObject(enumx.nextElement());
/*  92 */           derOutStream.close();
/*     */           
/*  94 */           InputStream certInStream = new ByteArrayInputStream(outStream.toByteArray());
/*  95 */           this.certificates.add(0, certFactory.generateCertificate(certInStream));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 100 */         throw new CertificateException("unsupported encoding");
/*     */       }
/*     */     } catch (IOException ex) {
/* 103 */       throw new CertificateException("IOException throw while decoding CertPath:\n" + ex.toString());
/*     */     } catch (NoSuchProviderException ex) {
/* 105 */       throw new CertificateException("BouncyCastle provider not found while trying to get a CertificateFactory:\n" + ex.toString());
/*     */     }
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
/*     */   public Iterator getEncodings()
/*     */   {
/* 119 */     return certPathEncodings.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */     throws CertificateEncodingException
/*     */   {
/* 132 */     Iterator iter = getEncodings();
/* 133 */     if (iter.hasNext())
/*     */     {
/* 135 */       Object enc = iter.next();
/* 136 */       if ((enc instanceof String))
/*     */       {
/* 138 */         return getEncoded((String)enc);
/*     */       }
/*     */     }
/* 141 */     return null;
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
/*     */   public byte[] getEncoded(String encoding)
/*     */     throws CertificateEncodingException
/*     */   {
/* 159 */     DERObject encoded = null;
/* 160 */     if (encoding.equals("PkiPath"))
/*     */     {
/* 162 */       ASN1EncodableVector v = new ASN1EncodableVector();
/*     */       
/*     */ 
/* 165 */       ListIterator iter = this.certificates.listIterator(this.certificates.size());
/* 166 */       while (iter.hasPrevious())
/*     */       {
/* 168 */         v.add(getEncodedX509Certificate((X509Certificate)iter.previous()));
/*     */       }
/*     */       
/* 171 */       encoded = new DERSequence(v);
/*     */     }
/*     */     else {
/* 174 */       throw new CertificateEncodingException("unsupported encoding");
/*     */     }
/* 176 */     if (encoded == null) {
/* 177 */       return null;
/*     */     }
/* 179 */     ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 180 */     DEROutputStream derOutStream = new DEROutputStream(outStream);
/*     */     try
/*     */     {
/* 183 */       derOutStream.writeObject(encoded);
/* 184 */       derOutStream.close();
/*     */     } catch (IOException ex) {
/* 186 */       throw new CertificateEncodingException("IOExeption thrown: " + ex.toString());
/*     */     }
/*     */     
/* 189 */     return outStream.toByteArray();
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
/*     */   public List getCertificates()
/*     */   {
/* 202 */     return new ArrayList(this.certificates);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DERObject getEncodedX509Certificate(X509Certificate cert)
/*     */     throws CertificateEncodingException
/*     */   {
/*     */     try
/*     */     {
/* 216 */       ByteArrayInputStream inStream = new ByteArrayInputStream(cert.getEncoded());
/* 217 */       DERInputStream derInStream = new DERInputStream(inStream);
/* 218 */       return derInStream.readObject();
/*     */     } catch (IOException ex) {
/* 220 */       throw new CertificateEncodingException("IOException caught while encoding certificate\n" + ex.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/PKIXCertPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */