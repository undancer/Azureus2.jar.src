/*     */ package org.gudy.azureus2.ui.console.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.spec.KeySpec;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.NoSuchPaddingException;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.SecretKeyFactory;
/*     */ import javax.crypto.spec.DESKeySpec;
/*     */ import javax.crypto.spec.DESedeKeySpec;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
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
/*     */ public class StringEncrypter
/*     */ {
/*     */   public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
/*     */   public static final String DES_ENCRYPTION_SCHEME = "DES";
/*     */   public static final String DEFAULT_ENCRYPTION_KEY = "Azureus users love their sensitive information to be encrypted";
/*     */   private KeySpec keySpec;
/*     */   private SecretKeyFactory keyFactory;
/*     */   private Cipher cipher;
/*     */   private static final String UNICODE_FORMAT = "UTF8";
/*     */   
/*     */   public StringEncrypter(String encryptionScheme)
/*     */     throws StringEncrypter.EncryptionException
/*     */   {
/*  52 */     this(encryptionScheme, "Azureus users love their sensitive information to be encrypted");
/*     */   }
/*     */   
/*     */   public StringEncrypter(String encryptionScheme, String encryptionKey) throws StringEncrypter.EncryptionException
/*     */   {
/*  57 */     if (encryptionKey == null)
/*  58 */       throw new IllegalArgumentException("encryption key was null");
/*  59 */     if (encryptionKey.trim().length() < 24) {
/*  60 */       throw new IllegalArgumentException("encryption key was less than 24 characters");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  65 */       byte[] keyAsBytes = encryptionKey.getBytes("UTF8");
/*     */       
/*  67 */       if (encryptionScheme.equals("DESede"))
/*     */       {
/*  69 */         this.keySpec = new DESedeKeySpec(keyAsBytes);
/*     */       }
/*  71 */       else if (encryptionScheme.equals("DES"))
/*     */       {
/*  73 */         this.keySpec = new DESKeySpec(keyAsBytes);
/*     */       }
/*     */       else
/*     */       {
/*  77 */         throw new IllegalArgumentException("Encryption scheme not supported: " + encryptionScheme);
/*     */       }
/*     */       
/*     */ 
/*  81 */       this.keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
/*  82 */       this.cipher = Cipher.getInstance(encryptionScheme);
/*     */ 
/*     */     }
/*     */     catch (InvalidKeyException e)
/*     */     {
/*  87 */       throw new EncryptionException(e);
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*  91 */       throw new EncryptionException(e);
/*     */     }
/*     */     catch (NoSuchAlgorithmException e)
/*     */     {
/*  95 */       throw new EncryptionException(e);
/*     */     }
/*     */     catch (NoSuchPaddingException e)
/*     */     {
/*  99 */       throw new EncryptionException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public String encrypt(String unencryptedString)
/*     */     throws StringEncrypter.EncryptionException
/*     */   {
/* 106 */     if ((unencryptedString == null) || (unencryptedString.trim().length() == 0)) {
/* 107 */       throw new IllegalArgumentException("unencrypted string was null or empty");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 112 */       SecretKey key = this.keyFactory.generateSecret(this.keySpec);
/* 113 */       this.cipher.init(1, key);
/* 114 */       byte[] cleartext = unencryptedString.getBytes("UTF8");
/* 115 */       byte[] ciphertext = this.cipher.doFinal(cleartext);
/*     */       
/* 117 */       return new String(Base64.encode(ciphertext));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 121 */       throw new EncryptionException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public String decrypt(String encryptedString) throws StringEncrypter.EncryptionException
/*     */   {
/* 127 */     if ((encryptedString == null) || (encryptedString.trim().length() <= 0)) {
/* 128 */       throw new IllegalArgumentException("encrypted string was null or empty");
/*     */     }
/*     */     try
/*     */     {
/* 132 */       SecretKey key = this.keyFactory.generateSecret(this.keySpec);
/* 133 */       this.cipher.init(2, key);
/* 134 */       byte[] cleartext = Base64.decode(encryptedString);
/* 135 */       byte[] ciphertext = this.cipher.doFinal(cleartext);
/*     */       
/* 137 */       return bytes2String(ciphertext);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 141 */       throw new EncryptionException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String bytes2String(byte[] bytes)
/*     */   {
/* 147 */     StringBuilder stringBuffer = new StringBuilder();
/* 148 */     for (int i = 0; i < bytes.length; i++)
/*     */     {
/* 150 */       stringBuffer.append((char)bytes[i]);
/*     */     }
/* 152 */     return stringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public static class EncryptionException
/*     */     extends Exception
/*     */   {
/*     */     private static final long serialVersionUID = -8767982102667004210L;
/*     */     
/*     */ 
/*     */     public EncryptionException(Throwable t)
/*     */     {
/* 164 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/util/StringEncrypter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */