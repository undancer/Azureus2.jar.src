/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.Signature;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.RSAPublicKeySpec;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ public class AEVerifier
/*     */ {
/*     */   private static final String pub_exp = "10001";
/*     */   private static final String modulus = "9a68296f49bf47b2a83ae4ba3cdb5a840a2689e5b36a6f2bfc27b916fc4dc9437f9087c4f0b5ae2fc5127a901b3c048753aa63d29cd7f9da7c81d475380de68236bd919230b0074aa6f40f29a78ac4a14e84fb8946cbcb5a840d1c2f77d83c795c289e37135843b8da008e082654a83b8bd3341b9f2ff6064e20b6c7ba89a707a1f3e1d8b2e0035dae539b04e49775eba23e5cbe89e22290da6c84ec3f450d07";
/*     */   
/*     */   public static void verifyData(File file)
/*     */     throws AEVerifierException, Exception
/*     */   {
/*  47 */     KeyFactory key_factory = KeyFactory.getInstance("RSA");
/*     */     
/*  49 */     RSAPublicKeySpec public_key_spec = new RSAPublicKeySpec(new BigInteger("9a68296f49bf47b2a83ae4ba3cdb5a840a2689e5b36a6f2bfc27b916fc4dc9437f9087c4f0b5ae2fc5127a901b3c048753aa63d29cd7f9da7c81d475380de68236bd919230b0074aa6f40f29a78ac4a14e84fb8946cbcb5a840d1c2f77d83c795c289e37135843b8da008e082654a83b8bd3341b9f2ff6064e20b6c7ba89a707a1f3e1d8b2e0035dae539b04e49775eba23e5cbe89e22290da6c84ec3f450d07", 16), new BigInteger("10001", 16));
/*     */     
/*     */ 
/*  52 */     RSAPublicKey public_key = (RSAPublicKey)key_factory.generatePublic(public_key_spec);
/*     */     
/*  54 */     verifyData(file, public_key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void verifyData(File file, RSAPublicKey key)
/*     */     throws AEVerifierException, Exception
/*     */   {
/*  64 */     ZipInputStream zis = null;
/*     */     try
/*     */     {
/*  67 */       zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
/*     */       
/*     */ 
/*  70 */       byte[] signature = null;
/*     */       
/*  72 */       Signature sig = Signature.getInstance("MD5withRSA");
/*     */       
/*  74 */       sig.initVerify(key);
/*     */       
/*     */       for (;;)
/*     */       {
/*  78 */         ZipEntry entry = zis.getNextEntry();
/*     */         
/*  80 */         if (entry == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*  85 */         if (!entry.isDirectory())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  90 */           String name = entry.getName();
/*     */           
/*  92 */           ByteArrayOutputStream output = null;
/*     */           
/*  94 */           if (name.equalsIgnoreCase("azureus.sig"))
/*     */           {
/*  96 */             output = new ByteArrayOutputStream();
/*     */           }
/*     */           
/*  99 */           byte[] buffer = new byte[65536];
/*     */           
/*     */           for (;;)
/*     */           {
/* 103 */             int len = zis.read(buffer);
/*     */             
/* 105 */             if (len <= 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 110 */             if (output == null)
/*     */             {
/* 112 */               sig.update(buffer, 0, len);
/*     */             }
/*     */             else
/*     */             {
/* 116 */               output.write(buffer, 0, len);
/*     */             }
/*     */           }
/*     */           
/* 120 */           if (output != null)
/*     */           {
/* 122 */             signature = output.toByteArray();
/*     */           }
/*     */         }
/*     */       }
/* 126 */       if (signature == null)
/*     */       {
/* 128 */         throw new AEVerifierException(1, "Signature missing from file");
/*     */       }
/*     */       
/* 131 */       if (!sig.verify(signature))
/*     */       {
/* 133 */         throw new AEVerifierException(2, "Signature doesn't match data");
/*     */       }
/*     */     }
/*     */     finally {
/* 137 */       if (zis != null)
/*     */       {
/* 139 */         zis.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void verifyData(String data, byte[] signature)
/*     */     throws AEVerifierException, Exception
/*     */   {
/* 151 */     KeyFactory key_factory = KeyFactory.getInstance("RSA");
/*     */     
/* 153 */     RSAPublicKeySpec public_key_spec = new RSAPublicKeySpec(new BigInteger("9a68296f49bf47b2a83ae4ba3cdb5a840a2689e5b36a6f2bfc27b916fc4dc9437f9087c4f0b5ae2fc5127a901b3c048753aa63d29cd7f9da7c81d475380de68236bd919230b0074aa6f40f29a78ac4a14e84fb8946cbcb5a840d1c2f77d83c795c289e37135843b8da008e082654a83b8bd3341b9f2ff6064e20b6c7ba89a707a1f3e1d8b2e0035dae539b04e49775eba23e5cbe89e22290da6c84ec3f450d07", 16), new BigInteger("10001", 16));
/*     */     
/*     */ 
/* 156 */     RSAPublicKey public_key = (RSAPublicKey)key_factory.generatePublic(public_key_spec);
/*     */     
/* 158 */     Signature sig = Signature.getInstance("MD5withRSA");
/*     */     
/* 160 */     sig.initVerify(public_key);
/*     */     
/* 162 */     sig.update(data.getBytes("UTF-8"));
/*     */     
/* 164 */     if (!sig.verify(signature))
/*     */     {
/* 166 */       throw new AEVerifierException(2, "Data verification failed, signature doesn't match data");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */