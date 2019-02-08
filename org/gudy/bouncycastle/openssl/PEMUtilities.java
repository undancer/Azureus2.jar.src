/*     */ package org.gudy.bouncycastle.openssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.Key;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.RC2ParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.bouncycastle.crypto.PBEParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PEMUtilities
/*     */ {
/*     */   static byte[] crypt(boolean encrypt, String provider, byte[] bytes, char[] password, String dekAlgName, byte[] iv)
/*     */     throws IOException
/*     */   {
/*  27 */     AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
/*     */     
/*  29 */     String blockMode = "CBC";
/*  30 */     String padding = "PKCS5Padding";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  35 */     if (dekAlgName.endsWith("-CFB"))
/*     */     {
/*  37 */       blockMode = "CFB";
/*  38 */       padding = "NoPadding";
/*     */     }
/*  40 */     if ((dekAlgName.endsWith("-ECB")) || ("DES-EDE".equals(dekAlgName)) || ("DES-EDE3".equals(dekAlgName)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  46 */       blockMode = "ECB";
/*  47 */       paramSpec = null;
/*     */     }
/*  49 */     if (dekAlgName.endsWith("-OFB"))
/*     */     {
/*  51 */       blockMode = "OFB";
/*  52 */       padding = "NoPadding";
/*     */     }
/*     */     
/*     */     Key sKey;
/*     */     
/*  57 */     if (dekAlgName.startsWith("DES-EDE"))
/*     */     {
/*  59 */       String alg = "DESede";
/*     */       
/*     */ 
/*  62 */       boolean des2 = !dekAlgName.startsWith("DES-EDE3");
/*  63 */       sKey = getKey(password, alg, 24, iv, des2);
/*     */     } else { Key sKey;
/*  65 */       if (dekAlgName.startsWith("DES-"))
/*     */       {
/*  67 */         String alg = "DES";
/*  68 */         sKey = getKey(password, alg, 8, iv);
/*     */       } else { Key sKey;
/*  70 */         if (dekAlgName.startsWith("BF-"))
/*     */         {
/*  72 */           String alg = "Blowfish";
/*  73 */           sKey = getKey(password, alg, 16, iv);
/*     */         }
/*  75 */         else if (dekAlgName.startsWith("RC2-"))
/*     */         {
/*  77 */           String alg = "RC2";
/*  78 */           int keyBits = 128;
/*  79 */           if (dekAlgName.startsWith("RC2-40-"))
/*     */           {
/*  81 */             keyBits = 40;
/*     */           }
/*  83 */           else if (dekAlgName.startsWith("RC2-64-"))
/*     */           {
/*  85 */             keyBits = 64;
/*     */           }
/*  87 */           Key sKey = getKey(password, alg, keyBits / 8, iv);
/*  88 */           if (paramSpec == null)
/*     */           {
/*  90 */             paramSpec = new RC2ParameterSpec(keyBits);
/*     */           }
/*     */           else
/*     */           {
/*  94 */             paramSpec = new RC2ParameterSpec(keyBits, iv); }
/*     */         } else {
/*     */           Key sKey;
/*  97 */           if (dekAlgName.startsWith("AES-"))
/*     */           {
/*  99 */             String alg = "AES";
/* 100 */             byte[] salt = iv;
/* 101 */             if (salt.length > 8)
/*     */             {
/* 103 */               salt = new byte[8];
/* 104 */               System.arraycopy(iv, 0, salt, 0, 8);
/*     */             }
/*     */             
/*     */             int keyBits;
/* 108 */             if (dekAlgName.startsWith("AES-128-"))
/*     */             {
/* 110 */               keyBits = 128;
/*     */             } else { int keyBits;
/* 112 */               if (dekAlgName.startsWith("AES-192-"))
/*     */               {
/* 114 */                 keyBits = 192;
/*     */               } else { int keyBits;
/* 116 */                 if (dekAlgName.startsWith("AES-256-"))
/*     */                 {
/* 118 */                   keyBits = 256;
/*     */                 }
/*     */                 else
/*     */                 {
/* 122 */                   throw new IOException("unknown AES encryption with private key"); } } }
/*     */             int keyBits;
/* 124 */             sKey = getKey(password, "AES", keyBits / 8, salt);
/*     */           }
/*     */           else
/*     */           {
/* 128 */             throw new IOException("unknown encryption with private key"); } } } }
/*     */     Key sKey;
/*     */     String alg;
/* 131 */     String transformation = alg + "/" + blockMode + "/" + padding;
/*     */     
/*     */     try
/*     */     {
/* 135 */       Cipher c = Cipher.getInstance(transformation, provider);
/* 136 */       int mode = encrypt ? 1 : 2;
/*     */       
/* 138 */       if (paramSpec == null)
/*     */       {
/* 140 */         c.init(mode, sKey);
/*     */       }
/*     */       else
/*     */       {
/* 144 */         c.init(mode, sKey, paramSpec);
/*     */       }
/* 146 */       return c.doFinal(bytes);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 150 */       throw new IOException("exception using cipher: " + e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static SecretKey getKey(char[] password, String algorithm, int keyLength, byte[] salt)
/*     */     throws IOException
/*     */   {
/* 161 */     return getKey(password, algorithm, keyLength, salt, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static SecretKey getKey(char[] password, String algorithm, int keyLength, byte[] salt, boolean des2)
/*     */     throws IOException
/*     */   {
/* 172 */     OpenSSLPBEParametersGenerator pGen = new OpenSSLPBEParametersGenerator();
/*     */     
/* 174 */     pGen.init(PBEParametersGenerator.PKCS5PasswordToBytes(password), salt);
/*     */     
/*     */ 
/* 177 */     KeyParameter keyParam = (KeyParameter)pGen.generateDerivedParameters(keyLength * 8);
/* 178 */     byte[] key = keyParam.getKey();
/* 179 */     if ((des2) && (key.length >= 24))
/*     */     {
/*     */ 
/* 182 */       System.arraycopy(key, 0, key, 16, 8);
/*     */     }
/* 184 */     return new SecretKeySpec(key, algorithm);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/openssl/PEMUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */