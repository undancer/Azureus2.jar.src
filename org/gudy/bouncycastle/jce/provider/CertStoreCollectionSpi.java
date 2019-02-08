/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLSelector;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertStoreParameters;
/*     */ import java.security.cert.CertStoreSpi;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CertStoreCollectionSpi
/*     */   extends CertStoreSpi
/*     */ {
/*     */   private CollectionCertStoreParameters params;
/*     */   
/*     */   public CertStoreCollectionSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException
/*     */   {
/*  24 */     super(params);
/*     */     
/*  26 */     if (!(params instanceof CollectionCertStoreParameters))
/*     */     {
/*  28 */       throw new InvalidAlgorithmParameterException("org.gudy.bouncycastle.jce.provider.CertStoreCollectionSpi: parameter must be a CollectionCertStoreParameters object\n" + params.toString());
/*     */     }
/*     */     
/*  31 */     this.params = ((CollectionCertStoreParameters)params);
/*     */   }
/*     */   
/*     */ 
/*     */   public Collection engineGetCertificates(CertSelector selector)
/*     */     throws CertStoreException
/*     */   {
/*  38 */     Set col = new HashSet();
/*  39 */     Iterator iter = this.params.getCollection().iterator();
/*     */     
/*  41 */     if (selector == null)
/*     */     {
/*  43 */       while (iter.hasNext())
/*     */       {
/*  45 */         Object obj = iter.next();
/*     */         
/*  47 */         if ((obj instanceof Certificate))
/*     */         {
/*  49 */           col.add(obj);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  55 */     while (iter.hasNext())
/*     */     {
/*  57 */       Object obj = iter.next();
/*     */       
/*  59 */       if (((obj instanceof Certificate)) && (selector.match((Certificate)obj)))
/*     */       {
/*  61 */         col.add(obj);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  66 */     return col;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection engineGetCRLs(CRLSelector selector)
/*     */     throws CertStoreException
/*     */   {
/*  74 */     Set col = new HashSet();
/*  75 */     Iterator iter = this.params.getCollection().iterator();
/*     */     
/*  77 */     if (selector == null)
/*     */     {
/*  79 */       while (iter.hasNext())
/*     */       {
/*  81 */         Object obj = iter.next();
/*     */         
/*  83 */         if ((obj instanceof CRL))
/*     */         {
/*  85 */           col.add(obj);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  91 */     while (iter.hasNext())
/*     */     {
/*  93 */       Object obj = iter.next();
/*     */       
/*  95 */       if (((obj instanceof CRL)) && (selector.match((CRL)obj)))
/*     */       {
/*  97 */         col.add(obj);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 102 */     return col;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/CertStoreCollectionSpi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */