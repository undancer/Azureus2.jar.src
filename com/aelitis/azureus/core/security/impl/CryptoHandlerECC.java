/*     */ package com.aelitis.azureus.core.security.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*     */ import com.aelitis.azureus.core.security.CryptoHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerException;
/*     */ import com.aelitis.azureus.core.security.CryptoSTSEngine;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.Arrays;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.IllegalBlockSizeException;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.bouncycastle.jce.provider.JCEIESCipher.ECIES;
/*     */ import org.gudy.bouncycastle.jce.spec.IEKeySpec;
/*     */ import org.gudy.bouncycastle.jce.spec.IESParameterSpec;
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
/*     */ public class CryptoHandlerECC
/*     */   implements CryptoHandler
/*     */ {
/*     */   private static final String DEFAULT_PASSWORD = "";
/*  59 */   private static final Long DEFAULT_TIMEOUT = Long.valueOf(Long.MAX_VALUE);
/*     */   
/*     */ 
/*     */   private static final int TIMEOUT_DEFAULT_SECS = 3600;
/*     */   
/*     */ 
/*     */   final CryptoManagerImpl manager;
/*     */   
/*  67 */   private String CONFIG_PREFIX = "core.crypto.ecc.";
/*     */   
/*     */ 
/*     */   private PrivateKey use_method_private_key;
/*     */   
/*     */   private PublicKey use_method_public_key;
/*     */   
/*     */   private long last_unlock_time;
/*     */   
/*     */ 
/*     */   protected CryptoHandlerECC(CryptoManagerImpl _manager, int _instance_id)
/*     */   {
/*  79 */     this.manager = _manager;
/*     */     
/*  81 */     this.CONFIG_PREFIX = (this.CONFIG_PREFIX + _instance_id + ".");
/*     */     
/*     */ 
/*     */ 
/*  85 */     if (getDefaultPasswordHandlerType() != 1)
/*     */     {
/*  87 */       COConfigurationManager.setParameter(this.CONFIG_PREFIX + "default_pwtype", 1);
/*     */     }
/*     */     
/*  90 */     if ((getCurrentPasswordType() == 2) || (COConfigurationManager.getByteParameter(this.CONFIG_PREFIX + "publickey", null) == null))
/*     */     {
/*     */       try
/*     */       {
/*  94 */         createAndStoreKeys(this.manager.setPassword(1, 1, "".toCharArray(), DEFAULT_TIMEOUT.longValue()));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */         Debug.outNoStack("Successfully migrated key management");
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 105 */         Debug.out("Failed to migrate key management", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 113 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unlock()
/*     */     throws CryptoManagerException
/*     */   {
/* 121 */     getMyPrivateKey("unlock");
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized boolean isUnlocked()
/*     */   {
/* 127 */     return this.use_method_private_key != null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void lock()
/*     */   {
/* 133 */     boolean changed = false;
/*     */     
/* 135 */     synchronized (this)
/*     */     {
/* 137 */       changed = this.use_method_private_key != null;
/*     */       
/* 139 */       this.use_method_private_key = null;
/*     */     }
/*     */     
/* 142 */     if (changed)
/*     */     {
/* 144 */       this.manager.lockChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUnlockTimeoutSeconds()
/*     */   {
/* 151 */     return COConfigurationManager.getIntParameter(this.CONFIG_PREFIX + "timeout", 3600);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUnlockTimeoutSeconds(int secs)
/*     */   {
/* 158 */     COConfigurationManager.setParameter(this.CONFIG_PREFIX + "timeout", secs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] sign(byte[] data, String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 168 */     PrivateKey priv = getMyPrivateKey(reason);
/*     */     
/* 170 */     Signature sig = CryptoECCUtils.getSignature(priv);
/*     */     try
/*     */     {
/* 173 */       sig.update(data);
/*     */       
/* 175 */       return sig.sign();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 179 */       throw new CryptoManagerException("Signature failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean verify(byte[] public_key, byte[] data, byte[] signature)
/*     */     throws CryptoManagerException
/*     */   {
/* 191 */     PublicKey pub = CryptoECCUtils.rawdataToPubkey(public_key);
/*     */     
/* 193 */     Signature sig = CryptoECCUtils.getSignature(pub);
/*     */     try
/*     */     {
/* 196 */       sig.update(data);
/*     */       
/* 198 */       return sig.verify(signature);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 202 */       throw new CryptoManagerException("Signature failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] encrypt(byte[] other_public_key, byte[] data, String reason)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 215 */       IEKeySpec key_spec = new IEKeySpec(getMyPrivateKey(reason), CryptoECCUtils.rawdataToPubkey(other_public_key));
/*     */       
/* 217 */       byte[] d = new byte[16];
/* 218 */       byte[] e = new byte[16];
/*     */       
/* 220 */       RandomUtils.nextSecureBytes(d);
/* 221 */       RandomUtils.nextSecureBytes(e);
/*     */       
/* 223 */       IESParameterSpec param = new IESParameterSpec(d, e, 128);
/*     */       
/* 225 */       InternalECIES cipher = new InternalECIES();
/*     */       
/* 227 */       cipher.internalEngineInit(1, key_spec, param, null);
/*     */       
/* 229 */       byte[] encrypted = cipher.internalEngineDoFinal(data, 0, data.length);
/*     */       
/* 231 */       byte[] result = new byte[32 + encrypted.length];
/*     */       
/* 233 */       System.arraycopy(d, 0, result, 0, 16);
/* 234 */       System.arraycopy(e, 0, result, 16, 16);
/* 235 */       System.arraycopy(encrypted, 0, result, 32, encrypted.length);
/*     */       
/* 237 */       return result;
/*     */     }
/*     */     catch (CryptoManagerException e)
/*     */     {
/* 241 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 245 */       throw new CryptoManagerException("Encrypt failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] decrypt(byte[] other_public_key, byte[] data, String reason)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 258 */       IEKeySpec key_spec = new IEKeySpec(getMyPrivateKey(reason), CryptoECCUtils.rawdataToPubkey(other_public_key));
/*     */       
/* 260 */       byte[] d = new byte[16];
/* 261 */       byte[] e = new byte[16];
/*     */       
/* 263 */       System.arraycopy(data, 0, d, 0, 16);
/* 264 */       System.arraycopy(data, 16, e, 0, 16);
/*     */       
/* 266 */       IESParameterSpec param = new IESParameterSpec(d, e, 128);
/*     */       
/* 268 */       InternalECIES cipher = new InternalECIES();
/*     */       
/* 270 */       cipher.internalEngineInit(2, key_spec, param, null);
/*     */       
/* 272 */       return cipher.internalEngineDoFinal(data, 32, data.length - 32);
/*     */     }
/*     */     catch (CryptoManagerException e)
/*     */     {
/* 276 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 280 */       throw new CryptoManagerException("Decrypt failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CryptoSTSEngine getSTSEngine(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 290 */     return new CryptoSTSEngineImpl(getMyPublicKey(reason, true), getMyPrivateKey(reason));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CryptoSTSEngine getSTSEngine(PublicKey public_key, PrivateKey private_key)
/*     */     throws CryptoManagerException
/*     */   {
/* 300 */     return new CryptoSTSEngineImpl(public_key, private_key);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] peekPublicKey()
/*     */   {
/*     */     try
/*     */     {
/* 308 */       return CryptoECCUtils.keyToRawdata(getMyPublicKey("peek", false));
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 312 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getPublicKey(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 322 */     return CryptoECCUtils.keyToRawdata(getMyPublicKey(reason, true));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncryptedPrivateKey(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 331 */     getMyPrivateKey(reason);
/*     */     
/* 333 */     byte[] pk = COConfigurationManager.getByteParameter(this.CONFIG_PREFIX + "privatekey", null);
/*     */     
/* 335 */     if (pk == null)
/*     */     {
/* 337 */       throw new CryptoManagerException("Private key unavailable");
/*     */     }
/*     */     
/* 340 */     int pw_type = getCurrentPasswordType();
/*     */     
/* 342 */     byte[] res = new byte[pk.length + 1];
/*     */     
/* 344 */     res[0] = ((byte)pw_type);
/*     */     
/* 346 */     System.arraycopy(pk, 0, res, 1, pk.length);
/*     */     
/* 348 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recoverKeys(byte[] public_key, byte[] encrypted_private_key_and_type)
/*     */     throws CryptoManagerException
/*     */   {
/* 358 */     boolean lock_changed = false;
/*     */     
/* 360 */     synchronized (this)
/*     */     {
/* 362 */       lock_changed = this.use_method_private_key != null;
/*     */       
/* 364 */       this.use_method_private_key = null;
/* 365 */       this.use_method_public_key = null;
/*     */       
/* 367 */       this.manager.clearPassword(1, 3);
/*     */       
/* 369 */       COConfigurationManager.setParameter(this.CONFIG_PREFIX + "publickey", public_key);
/*     */       
/* 371 */       int type = encrypted_private_key_and_type[0] & 0xFF;
/*     */       
/* 373 */       COConfigurationManager.setParameter(this.CONFIG_PREFIX + "pwtype", type);
/*     */       
/* 375 */       byte[] encrypted_private_key = new byte[encrypted_private_key_and_type.length - 1];
/*     */       
/* 377 */       System.arraycopy(encrypted_private_key_and_type, 1, encrypted_private_key, 0, encrypted_private_key.length);
/*     */       
/* 379 */       COConfigurationManager.setParameter(this.CONFIG_PREFIX + "privatekey", encrypted_private_key);
/*     */       
/* 381 */       COConfigurationManager.save();
/*     */     }
/*     */     
/* 384 */     this.manager.keyChanged(this);
/*     */     
/* 386 */     if (lock_changed)
/*     */     {
/* 388 */       this.manager.lockChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetKeys(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 398 */     boolean lock_changed = false;
/*     */     
/* 400 */     synchronized (this)
/*     */     {
/* 402 */       lock_changed = this.use_method_private_key != null;
/*     */       
/* 404 */       this.use_method_private_key = null;
/* 405 */       this.use_method_public_key = null;
/*     */       
/* 407 */       this.manager.clearPassword(1, 3);
/*     */       
/* 409 */       COConfigurationManager.removeParameter(this.CONFIG_PREFIX + "publickey");
/*     */       
/* 411 */       COConfigurationManager.removeParameter(this.CONFIG_PREFIX + "privatekey");
/*     */       
/* 413 */       COConfigurationManager.save();
/*     */     }
/*     */     
/* 416 */     if (lock_changed)
/*     */     {
/* 418 */       this.manager.lockChanged(this);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 423 */       createAndStoreKeys("resetting keys");
/*     */     }
/*     */     catch (CryptoManagerException e)
/*     */     {
/* 427 */       this.manager.keyChanged(this);
/*     */       
/* 429 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected PrivateKey getMyPrivateKey(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: iconst_0
/*     */     //   1: istore_2
/*     */     //   2: aload_0
/*     */     //   3: dup
/*     */     //   4: astore_3
/*     */     //   5: monitorenter
/*     */     //   6: aload_0
/*     */     //   7: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   10: ifnull +40 -> 50
/*     */     //   13: aload_0
/*     */     //   14: invokevirtual 441	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:getUnlockTimeoutSeconds	()I
/*     */     //   17: istore 4
/*     */     //   19: iload 4
/*     */     //   21: ifle +29 -> 50
/*     */     //   24: invokestatic 500	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*     */     //   27: aload_0
/*     */     //   28: getfield 424	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:last_unlock_time	J
/*     */     //   31: lsub
/*     */     //   32: iload 4
/*     */     //   34: sipush 1000
/*     */     //   37: imul
/*     */     //   38: i2l
/*     */     //   39: lcmp
/*     */     //   40: iflt +10 -> 50
/*     */     //   43: iconst_1
/*     */     //   44: istore_2
/*     */     //   45: aload_0
/*     */     //   46: aconst_null
/*     */     //   47: putfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   50: aload_0
/*     */     //   51: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   54: ifnull +26 -> 80
/*     */     //   57: aload_0
/*     */     //   58: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   61: astore 4
/*     */     //   63: aload_3
/*     */     //   64: monitorexit
/*     */     //   65: iload_2
/*     */     //   66: ifeq +11 -> 77
/*     */     //   69: aload_0
/*     */     //   70: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   73: aload_0
/*     */     //   74: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   77: aload 4
/*     */     //   79: areturn
/*     */     //   80: aload_3
/*     */     //   81: monitorexit
/*     */     //   82: goto +10 -> 92
/*     */     //   85: astore 5
/*     */     //   87: aload_3
/*     */     //   88: monitorexit
/*     */     //   89: aload 5
/*     */     //   91: athrow
/*     */     //   92: new 276	java/lang/StringBuilder
/*     */     //   95: dup
/*     */     //   96: invokespecial 478	java/lang/StringBuilder:<init>	()V
/*     */     //   99: aload_0
/*     */     //   100: getfield 427	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:CONFIG_PREFIX	Ljava/lang/String;
/*     */     //   103: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   106: ldc 28
/*     */     //   108: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   111: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   114: aconst_null
/*     */     //   115: invokestatic 494	org/gudy/azureus2/core3/config/COConfigurationManager:getByteParameter	(Ljava/lang/String;[B)[B
/*     */     //   118: astore_3
/*     */     //   119: aload_3
/*     */     //   120: ifnonnull +30 -> 150
/*     */     //   123: aload_0
/*     */     //   124: aload_1
/*     */     //   125: invokevirtual 450	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:createAndStoreKeys	(Ljava/lang/String;)[Ljava/security/Key;
/*     */     //   128: iconst_1
/*     */     //   129: aaload
/*     */     //   130: checkcast 281	java/security/PrivateKey
/*     */     //   133: astore 4
/*     */     //   135: iload_2
/*     */     //   136: ifeq +11 -> 147
/*     */     //   139: aload_0
/*     */     //   140: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   143: aload_0
/*     */     //   144: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   147: aload 4
/*     */     //   149: areturn
/*     */     //   150: aload_0
/*     */     //   151: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   154: iconst_1
/*     */     //   155: iconst_2
/*     */     //   156: aload_1
/*     */     //   157: new 267	com/aelitis/azureus/core/security/impl/CryptoHandlerECC$1
/*     */     //   160: dup
/*     */     //   161: aload_0
/*     */     //   162: aload_3
/*     */     //   163: invokespecial 453	com/aelitis/azureus/core/security/impl/CryptoHandlerECC$1:<init>	(Lcom/aelitis/azureus/core/security/impl/CryptoHandlerECC;[B)V
/*     */     //   166: aload_0
/*     */     //   167: invokevirtual 439	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:getCurrentPasswordType	()I
/*     */     //   170: invokevirtual 465	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:getPassword	(IILjava/lang/String;Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl$passwordTester;I)Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl$passwordDetails;
/*     */     //   173: astore 4
/*     */     //   175: aload_0
/*     */     //   176: dup
/*     */     //   177: astore 5
/*     */     //   179: monitorenter
/*     */     //   180: iconst_0
/*     */     //   181: istore 6
/*     */     //   183: aload_0
/*     */     //   184: aload_0
/*     */     //   185: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   188: aload_3
/*     */     //   189: aload 4
/*     */     //   191: invokevirtual 467	com/aelitis/azureus/core/security/impl/CryptoManagerImpl$passwordDetails:getPassword	()[C
/*     */     //   194: invokevirtual 460	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:decryptWithPBE	([B[C)[B
/*     */     //   197: invokestatic 432	com/aelitis/azureus/core/security/CryptoECCUtils:rawdataToPrivkey	([B)Ljava/security/PrivateKey;
/*     */     //   200: putfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   203: iconst_1
/*     */     //   204: istore_2
/*     */     //   205: aload_0
/*     */     //   206: invokestatic 500	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*     */     //   209: putfield 424	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:last_unlock_time	J
/*     */     //   212: aload_0
/*     */     //   213: aload_1
/*     */     //   214: invokevirtual 445	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:checkKeysOK	(Ljava/lang/String;)Z
/*     */     //   217: ifne +14 -> 231
/*     */     //   220: new 265	com/aelitis/azureus/core/security/CryptoManagerPasswordException
/*     */     //   223: dup
/*     */     //   224: iconst_1
/*     */     //   225: ldc 16
/*     */     //   227: invokespecial 438	com/aelitis/azureus/core/security/CryptoManagerPasswordException:<init>	(ZLjava/lang/String;)V
/*     */     //   230: athrow
/*     */     //   231: iconst_1
/*     */     //   232: istore 6
/*     */     //   234: iload 6
/*     */     //   236: ifne +67 -> 303
/*     */     //   239: aload_0
/*     */     //   240: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   243: iconst_1
/*     */     //   244: iconst_3
/*     */     //   245: invokevirtual 458	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:clearPassword	(II)V
/*     */     //   248: iconst_1
/*     */     //   249: istore_2
/*     */     //   250: aload_0
/*     */     //   251: aconst_null
/*     */     //   252: putfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   255: goto +48 -> 303
/*     */     //   258: astore 7
/*     */     //   260: aload 7
/*     */     //   262: athrow
/*     */     //   263: astore 7
/*     */     //   265: new 264	com/aelitis/azureus/core/security/CryptoManagerException
/*     */     //   268: dup
/*     */     //   269: ldc 16
/*     */     //   271: aload 7
/*     */     //   273: invokespecial 437	com/aelitis/azureus/core/security/CryptoManagerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   276: athrow
/*     */     //   277: astore 8
/*     */     //   279: iload 6
/*     */     //   281: ifne +19 -> 300
/*     */     //   284: aload_0
/*     */     //   285: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   288: iconst_1
/*     */     //   289: iconst_3
/*     */     //   290: invokevirtual 458	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:clearPassword	(II)V
/*     */     //   293: iconst_1
/*     */     //   294: istore_2
/*     */     //   295: aload_0
/*     */     //   296: aconst_null
/*     */     //   297: putfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   300: aload 8
/*     */     //   302: athrow
/*     */     //   303: aload_0
/*     */     //   304: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   307: ifnonnull +13 -> 320
/*     */     //   310: new 264	com/aelitis/azureus/core/security/CryptoManagerException
/*     */     //   313: dup
/*     */     //   314: ldc 9
/*     */     //   316: invokespecial 436	com/aelitis/azureus/core/security/CryptoManagerException:<init>	(Ljava/lang/String;)V
/*     */     //   319: athrow
/*     */     //   320: aload_0
/*     */     //   321: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   324: astore 7
/*     */     //   326: aload 5
/*     */     //   328: monitorexit
/*     */     //   329: iload_2
/*     */     //   330: ifeq +11 -> 341
/*     */     //   333: aload_0
/*     */     //   334: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   337: aload_0
/*     */     //   338: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   341: aload 7
/*     */     //   343: areturn
/*     */     //   344: astore 9
/*     */     //   346: aload 5
/*     */     //   348: monitorexit
/*     */     //   349: aload 9
/*     */     //   351: athrow
/*     */     //   352: astore 10
/*     */     //   354: iload_2
/*     */     //   355: ifeq +11 -> 366
/*     */     //   358: aload_0
/*     */     //   359: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   362: aload_0
/*     */     //   363: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   366: aload 10
/*     */     //   368: athrow
/*     */     // Line number table:
/*     */     //   Java source line #439	-> byte code offset #0
/*     */     //   Java source line #442	-> byte code offset #2
/*     */     //   Java source line #444	-> byte code offset #6
/*     */     //   Java source line #446	-> byte code offset #13
/*     */     //   Java source line #448	-> byte code offset #19
/*     */     //   Java source line #450	-> byte code offset #24
/*     */     //   Java source line #452	-> byte code offset #43
/*     */     //   Java source line #454	-> byte code offset #45
/*     */     //   Java source line #459	-> byte code offset #50
/*     */     //   Java source line #461	-> byte code offset #57
/*     */     //   Java source line #545	-> byte code offset #65
/*     */     //   Java source line #547	-> byte code offset #69
/*     */     //   Java source line #463	-> byte code offset #80
/*     */     //   Java source line #465	-> byte code offset #92
/*     */     //   Java source line #467	-> byte code offset #119
/*     */     //   Java source line #469	-> byte code offset #123
/*     */     //   Java source line #545	-> byte code offset #135
/*     */     //   Java source line #547	-> byte code offset #139
/*     */     //   Java source line #473	-> byte code offset #150
/*     */     //   Java source line #497	-> byte code offset #175
/*     */     //   Java source line #499	-> byte code offset #180
/*     */     //   Java source line #502	-> byte code offset #183
/*     */     //   Java source line #504	-> byte code offset #203
/*     */     //   Java source line #506	-> byte code offset #205
/*     */     //   Java source line #508	-> byte code offset #212
/*     */     //   Java source line #510	-> byte code offset #220
/*     */     //   Java source line #513	-> byte code offset #231
/*     */     //   Java source line #525	-> byte code offset #234
/*     */     //   Java source line #527	-> byte code offset #239
/*     */     //   Java source line #529	-> byte code offset #248
/*     */     //   Java source line #531	-> byte code offset #250
/*     */     //   Java source line #515	-> byte code offset #258
/*     */     //   Java source line #517	-> byte code offset #260
/*     */     //   Java source line #519	-> byte code offset #263
/*     */     //   Java source line #521	-> byte code offset #265
/*     */     //   Java source line #525	-> byte code offset #277
/*     */     //   Java source line #527	-> byte code offset #284
/*     */     //   Java source line #529	-> byte code offset #293
/*     */     //   Java source line #531	-> byte code offset #295
/*     */     //   Java source line #535	-> byte code offset #303
/*     */     //   Java source line #537	-> byte code offset #310
/*     */     //   Java source line #540	-> byte code offset #320
/*     */     //   Java source line #545	-> byte code offset #329
/*     */     //   Java source line #547	-> byte code offset #333
/*     */     //   Java source line #541	-> byte code offset #344
/*     */     //   Java source line #545	-> byte code offset #352
/*     */     //   Java source line #547	-> byte code offset #358
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	369	0	this	CryptoHandlerECC
/*     */     //   0	369	1	reason	String
/*     */     //   1	354	2	lock_change	boolean
/*     */     //   118	71	3	encoded	byte[]
/*     */     //   17	131	4	timeout_secs	int
/*     */     //   173	17	4	password_details	CryptoManagerImpl.passwordDetails
/*     */     //   85	5	5	localObject1	Object
/*     */     //   181	99	6	ok	boolean
/*     */     //   258	3	7	e	CryptoManagerException
/*     */     //   263	79	7	e	Throwable
/*     */     //   277	24	8	localObject2	Object
/*     */     //   344	6	9	localObject3	Object
/*     */     //   352	15	10	localObject4	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	65	85	finally
/*     */     //   80	82	85	finally
/*     */     //   85	89	85	finally
/*     */     //   183	234	258	com/aelitis/azureus/core/security/CryptoManagerException
/*     */     //   183	234	263	java/lang/Throwable
/*     */     //   183	234	277	finally
/*     */     //   258	279	277	finally
/*     */     //   180	329	344	finally
/*     */     //   344	349	344	finally
/*     */     //   2	65	352	finally
/*     */     //   80	135	352	finally
/*     */     //   150	329	352	finally
/*     */     //   344	354	352	finally
/*     */   }
/*     */   
/*     */   protected boolean checkKeysOK(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 558 */     byte[] test_data = "test".getBytes();
/*     */     
/* 560 */     return verify(CryptoECCUtils.keyToRawdata(getMyPublicKey(reason, true)), test_data, sign(test_data, reason));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PublicKey getMyPublicKey(String reason, boolean create_if_needed)
/*     */     throws CryptoManagerException
/*     */   {
/* 570 */     boolean create_new = false;
/*     */     
/* 572 */     synchronized (this)
/*     */     {
/* 574 */       if (this.use_method_public_key == null)
/*     */       {
/* 576 */         byte[] key_bytes = COConfigurationManager.getByteParameter(this.CONFIG_PREFIX + "publickey", null);
/*     */         
/* 578 */         if (key_bytes == null)
/*     */         {
/* 580 */           if (create_if_needed)
/*     */           {
/* 582 */             create_new = true;
/*     */           }
/*     */           else
/*     */           {
/* 586 */             return null;
/*     */           }
/*     */         }
/*     */         else {
/* 590 */           this.use_method_public_key = CryptoECCUtils.rawdataToPubkey(key_bytes);
/*     */         }
/*     */       }
/*     */       
/* 594 */       if (!create_new)
/*     */       {
/* 596 */         if (this.use_method_public_key == null)
/*     */         {
/* 598 */           throw new CryptoManagerException("Failed to get public key");
/*     */         }
/*     */         
/* 601 */         return this.use_method_public_key;
/*     */       }
/*     */     }
/*     */     
/* 605 */     return (PublicKey)createAndStoreKeys(reason)[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDefaultPasswordHandlerType()
/*     */   {
/* 611 */     return COConfigurationManager.getIntParameter(this.CONFIG_PREFIX + "default_pwtype", 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDefaultPasswordHandlerType(int new_type)
/*     */     throws CryptoManagerException
/*     */   {
/* 620 */     String reason = "Changing password handler";
/*     */     
/* 622 */     boolean have_existing_keys = COConfigurationManager.getByteParameter(this.CONFIG_PREFIX + "privatekey", null) != null;
/*     */     
/*     */ 
/*     */ 
/* 626 */     if (have_existing_keys)
/*     */     {
/* 628 */       if (new_type == getCurrentPasswordType())
/*     */       {
/* 630 */         return;
/*     */       }
/*     */       
/* 633 */       getMyPrivateKey(reason);
/*     */       
/* 635 */       CryptoManagerImpl.passwordDetails password_details = this.manager.getPassword(1, 1, reason, null, new_type);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 644 */       synchronized (this)
/*     */       {
/* 646 */         if (this.use_method_private_key == null)
/*     */         {
/* 648 */           throw new CryptoManagerException("Private key not available");
/*     */         }
/*     */         
/* 651 */         byte[] priv_raw = CryptoECCUtils.keyToRawdata(this.use_method_private_key);
/*     */         
/* 653 */         byte[] priv_enc = this.manager.encryptWithPBE(priv_raw, password_details.getPassword());
/*     */         
/* 655 */         COConfigurationManager.setParameter(this.CONFIG_PREFIX + "privatekey", priv_enc);
/*     */         
/* 657 */         COConfigurationManager.setParameter(this.CONFIG_PREFIX + "pwtype", password_details.getHandlerType());
/*     */         
/* 659 */         COConfigurationManager.setParameter(this.CONFIG_PREFIX + "default_pwtype", password_details.getHandlerType());
/*     */         
/* 661 */         COConfigurationManager.save();
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 667 */       synchronized (this)
/*     */       {
/* 669 */         if (COConfigurationManager.getByteParameter(this.CONFIG_PREFIX + "privatekey", null) == null)
/*     */         {
/* 671 */           COConfigurationManager.setParameter(this.CONFIG_PREFIX + "default_pwtype", new_type);
/*     */           
/* 673 */           COConfigurationManager.save();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Key[] createAndStoreKeys(String reason)
/*     */     throws CryptoManagerException
/*     */   {
/* 685 */     CryptoManagerImpl.passwordDetails password_details = this.manager.getPassword(1, 1, reason, null, getDefaultPasswordHandlerType());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 693 */     return createAndStoreKeys(password_details);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected Key[] createAndStoreKeys(CryptoManagerImpl.passwordDetails password_details)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 429	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_public_key	Ljava/security/PublicKey;
/*     */     //   8: ifnull +10 -> 18
/*     */     //   11: aload_0
/*     */     //   12: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   15: ifnonnull +148 -> 163
/*     */     //   18: invokestatic 430	com/aelitis/azureus/core/security/CryptoECCUtils:createKeys	()Ljava/security/KeyPair;
/*     */     //   21: astore_3
/*     */     //   22: aload_0
/*     */     //   23: aload_3
/*     */     //   24: invokevirtual 484	java/security/KeyPair:getPublic	()Ljava/security/PublicKey;
/*     */     //   27: putfield 429	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_public_key	Ljava/security/PublicKey;
/*     */     //   30: aload_0
/*     */     //   31: aload_3
/*     */     //   32: invokevirtual 483	java/security/KeyPair:getPrivate	()Ljava/security/PrivateKey;
/*     */     //   35: putfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   38: aload_0
/*     */     //   39: invokestatic 500	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*     */     //   42: putfield 424	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:last_unlock_time	J
/*     */     //   45: new 276	java/lang/StringBuilder
/*     */     //   48: dup
/*     */     //   49: invokespecial 478	java/lang/StringBuilder:<init>	()V
/*     */     //   52: aload_0
/*     */     //   53: getfield 427	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:CONFIG_PREFIX	Ljava/lang/String;
/*     */     //   56: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   59: ldc 31
/*     */     //   61: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   64: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   67: aload_0
/*     */     //   68: getfield 429	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_public_key	Ljava/security/PublicKey;
/*     */     //   71: invokestatic 433	com/aelitis/azureus/core/security/CryptoECCUtils:keyToRawdata	(Ljava/security/PublicKey;)[B
/*     */     //   74: invokestatic 493	org/gudy/azureus2/core3/config/COConfigurationManager:setParameter	(Ljava/lang/String;[B)Z
/*     */     //   77: pop
/*     */     //   78: aload_0
/*     */     //   79: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   82: invokestatic 431	com/aelitis/azureus/core/security/CryptoECCUtils:keyToRawdata	(Ljava/security/PrivateKey;)[B
/*     */     //   85: astore 4
/*     */     //   87: aload_0
/*     */     //   88: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   91: aload 4
/*     */     //   93: aload_1
/*     */     //   94: invokevirtual 467	com/aelitis/azureus/core/security/impl/CryptoManagerImpl$passwordDetails:getPassword	()[C
/*     */     //   97: invokevirtual 461	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:encryptWithPBE	([B[C)[B
/*     */     //   100: astore 5
/*     */     //   102: new 276	java/lang/StringBuilder
/*     */     //   105: dup
/*     */     //   106: invokespecial 478	java/lang/StringBuilder:<init>	()V
/*     */     //   109: aload_0
/*     */     //   110: getfield 427	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:CONFIG_PREFIX	Ljava/lang/String;
/*     */     //   113: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   116: ldc 28
/*     */     //   118: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   121: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   124: aload 5
/*     */     //   126: invokestatic 493	org/gudy/azureus2/core3/config/COConfigurationManager:setParameter	(Ljava/lang/String;[B)Z
/*     */     //   129: pop
/*     */     //   130: new 276	java/lang/StringBuilder
/*     */     //   133: dup
/*     */     //   134: invokespecial 478	java/lang/StringBuilder:<init>	()V
/*     */     //   137: aload_0
/*     */     //   138: getfield 427	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:CONFIG_PREFIX	Ljava/lang/String;
/*     */     //   141: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   144: ldc 32
/*     */     //   146: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   149: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   152: aload_1
/*     */     //   153: invokevirtual 466	com/aelitis/azureus/core/security/impl/CryptoManagerImpl$passwordDetails:getHandlerType	()I
/*     */     //   156: invokestatic 492	org/gudy/azureus2/core3/config/COConfigurationManager:setParameter	(Ljava/lang/String;I)Z
/*     */     //   159: pop
/*     */     //   160: invokestatic 489	org/gudy/azureus2/core3/config/COConfigurationManager:save	()V
/*     */     //   163: iconst_2
/*     */     //   164: anewarray 279	java/security/Key
/*     */     //   167: dup
/*     */     //   168: iconst_0
/*     */     //   169: aload_0
/*     */     //   170: getfield 429	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_public_key	Ljava/security/PublicKey;
/*     */     //   173: aastore
/*     */     //   174: dup
/*     */     //   175: iconst_1
/*     */     //   176: aload_0
/*     */     //   177: getfield 428	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:use_method_private_key	Ljava/security/PrivateKey;
/*     */     //   180: aastore
/*     */     //   181: astore_3
/*     */     //   182: aload_2
/*     */     //   183: monitorexit
/*     */     //   184: aload_0
/*     */     //   185: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   188: aload_0
/*     */     //   189: invokevirtual 462	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:keyChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   192: aload_0
/*     */     //   193: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   196: aload_0
/*     */     //   197: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   200: aload_3
/*     */     //   201: areturn
/*     */     //   202: astore 6
/*     */     //   204: aload_2
/*     */     //   205: monitorexit
/*     */     //   206: aload 6
/*     */     //   208: athrow
/*     */     //   209: astore 7
/*     */     //   211: aload_0
/*     */     //   212: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   215: aload_0
/*     */     //   216: invokevirtual 462	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:keyChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   219: aload_0
/*     */     //   220: getfield 425	com/aelitis/azureus/core/security/impl/CryptoHandlerECC:manager	Lcom/aelitis/azureus/core/security/impl/CryptoManagerImpl;
/*     */     //   223: aload_0
/*     */     //   224: invokevirtual 463	com/aelitis/azureus/core/security/impl/CryptoManagerImpl:lockChanged	(Lcom/aelitis/azureus/core/security/CryptoHandler;)V
/*     */     //   227: aload 7
/*     */     //   229: athrow
/*     */     // Line number table:
/*     */     //   Java source line #703	-> byte code offset #0
/*     */     //   Java source line #705	-> byte code offset #4
/*     */     //   Java source line #707	-> byte code offset #18
/*     */     //   Java source line #709	-> byte code offset #22
/*     */     //   Java source line #711	-> byte code offset #30
/*     */     //   Java source line #713	-> byte code offset #38
/*     */     //   Java source line #715	-> byte code offset #45
/*     */     //   Java source line #717	-> byte code offset #78
/*     */     //   Java source line #719	-> byte code offset #87
/*     */     //   Java source line #721	-> byte code offset #102
/*     */     //   Java source line #723	-> byte code offset #130
/*     */     //   Java source line #725	-> byte code offset #160
/*     */     //   Java source line #728	-> byte code offset #163
/*     */     //   Java source line #732	-> byte code offset #184
/*     */     //   Java source line #734	-> byte code offset #192
/*     */     //   Java source line #729	-> byte code offset #202
/*     */     //   Java source line #732	-> byte code offset #209
/*     */     //   Java source line #734	-> byte code offset #219
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	230	0	this	CryptoHandlerECC
/*     */     //   0	230	1	password_details	CryptoManagerImpl.passwordDetails
/*     */     //   21	180	3	keys	java.security.KeyPair
/*     */     //   85	7	4	priv_raw	byte[]
/*     */     //   100	25	5	priv_enc	byte[]
/*     */     //   202	5	6	localObject1	Object
/*     */     //   209	19	7	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	184	202	finally
/*     */     //   202	206	202	finally
/*     */     //   0	184	209	finally
/*     */     //   202	211	209	finally
/*     */   }
/*     */   
/*     */   public boolean verifyPublicKey(byte[] encoded)
/*     */   {
/*     */     try
/*     */     {
/* 743 */       CryptoECCUtils.rawdataToPubkey(encoded);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 748 */       return true;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 752 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String exportKeys()
/*     */     throws CryptoManagerException
/*     */   {
/* 761 */     return "id:      " + Base32.encode(this.manager.getSecureID()) + "\r\n" + "public:  " + Base32.encode(getPublicKey("Key export")) + "\r\n" + "private: " + Base32.encode(getEncryptedPrivateKey("Key export"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean importKeys(String str)
/*     */     throws CryptoManagerException
/*     */   {
/* 772 */     String reason = "Key import";
/*     */     
/* 774 */     byte[] existing_id = this.manager.getSecureID();
/* 775 */     byte[] existing_public_key = peekPublicKey();
/* 776 */     byte[] existing_private_key = existing_public_key == null ? null : getEncryptedPrivateKey(reason);
/*     */     
/* 778 */     byte[] recovered_id = null;
/* 779 */     byte[] recovered_public_key = null;
/* 780 */     byte[] recovered_private_key = null;
/*     */     
/* 782 */     String[] bits = str.split("\n");
/*     */     
/* 784 */     for (int i = 0; i < bits.length; i++)
/*     */     {
/* 786 */       String bit = bits[i].trim();
/*     */       
/* 788 */       if (bit.length() != 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 793 */         String[] x = bit.split(":");
/*     */         
/* 795 */         if (x.length == 2)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 800 */           String lhs = x[0].trim();
/* 801 */           String rhs = x[1].trim();
/*     */           
/* 803 */           byte[] rhs_val = Base32.decode(rhs);
/*     */           
/* 805 */           if (lhs.equals("id"))
/*     */           {
/* 807 */             recovered_id = rhs_val;
/*     */           }
/* 809 */           else if (lhs.equals("public"))
/*     */           {
/* 811 */             recovered_public_key = rhs_val;
/*     */           }
/* 813 */           else if (lhs.equals("private"))
/*     */           {
/* 815 */             recovered_private_key = rhs_val; }
/*     */         }
/*     */       }
/*     */     }
/* 819 */     if ((recovered_id == null) || (recovered_public_key == null) || (recovered_private_key == null))
/*     */     {
/* 821 */       throw new CryptoManagerException("Invalid input file");
/*     */     }
/*     */     
/* 824 */     boolean ok = false;
/*     */     
/* 826 */     boolean result = false;
/*     */     
/*     */     try
/*     */     {
/* 830 */       result = !Arrays.equals(existing_id, recovered_id);
/*     */       
/* 832 */       if (result)
/*     */       {
/* 834 */         this.manager.setSecureID(recovered_id);
/*     */       }
/*     */       
/* 837 */       recoverKeys(recovered_public_key, recovered_private_key);
/*     */       
/* 839 */       if (!checkKeysOK(reason))
/*     */       {
/* 841 */         throw new CryptoManagerException("Invalid key pair");
/*     */       }
/*     */       
/* 844 */       ok = true;
/*     */     }
/*     */     finally
/*     */     {
/* 848 */       if (!ok)
/*     */       {
/* 850 */         result = false;
/*     */         
/* 852 */         this.manager.setSecureID(existing_id);
/*     */         
/* 854 */         if (existing_public_key != null)
/*     */         {
/* 856 */           recoverKeys(existing_public_key, existing_private_key);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 861 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getCurrentPasswordType()
/*     */   {
/* 867 */     return COConfigurationManager.getIntParameter(this.CONFIG_PREFIX + "pwtype", 1);
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
/*     */   static class InternalECIES
/*     */     extends JCEIESCipher.ECIES
/*     */   {
/*     */     public void internalEngineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
/*     */       throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */     {
/* 884 */       engineInit(opmode, key, params, random);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected byte[] internalEngineDoFinal(byte[] input, int inputOffset, int inputLen)
/*     */       throws IllegalBlockSizeException, BadPaddingException
/*     */     {
/* 895 */       return engineDoFinal(input, inputOffset, inputLen);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/impl/CryptoHandlerECC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */