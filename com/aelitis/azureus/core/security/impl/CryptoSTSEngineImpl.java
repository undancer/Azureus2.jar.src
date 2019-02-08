/*     */ package com.aelitis.azureus.core.security.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerException;
/*     */ import com.aelitis.azureus.core.security.CryptoSTSEngine;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyPair;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import org.gudy.bouncycastle.jce.provider.JCEECDHKeyAgreement.DH;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class CryptoSTSEngineImpl
/*     */   implements CryptoSTSEngine
/*     */ {
/*     */   public static final int VERSION = 1;
/*     */   private KeyPair ephemeralKeyPair;
/*     */   private final PublicKey myPublicKey;
/*     */   private final PrivateKey myPrivateKey;
/*     */   private PublicKey remotePubKey;
/*     */   private byte[] sharedSecret;
/*     */   private InternalDH ecDH;
/*     */   
/*     */   CryptoSTSEngineImpl(PublicKey _myPub, PrivateKey _myPriv)
/*     */     throws CryptoManagerException
/*     */   {
/*  69 */     this.myPublicKey = _myPub;
/*  70 */     this.myPrivateKey = _myPriv;
/*     */     
/*  72 */     this.ephemeralKeyPair = CryptoECCUtils.createKeys();
/*     */     try
/*     */     {
/*  75 */       this.ecDH = new InternalDH();
/*     */       
/*     */ 
/*     */ 
/*  79 */       this.ecDH.init(this.ephemeralKeyPair.getPrivate());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  83 */       throw new CryptoManagerException("Couldn't initialize crypto handshake", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getKeys(ByteBuffer message)
/*     */     throws CryptoManagerException
/*     */   {
/*  93 */     getMessage(message, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putKeys(ByteBuffer message)
/*     */     throws CryptoManagerException
/*     */   {
/* 102 */     putMessage(message, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getAuth(ByteBuffer message)
/*     */     throws CryptoManagerException
/*     */   {
/* 111 */     getMessage(message, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putAuth(ByteBuffer message)
/*     */     throws CryptoManagerException
/*     */   {
/* 120 */     putMessage(message, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putMessage(ByteBuffer message, boolean keys)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 133 */       int version = getInt(message, 255);
/*     */       
/* 135 */       if (version != 1)
/*     */       {
/* 137 */         throw new CryptoManagerException("invalid version (" + version + ")");
/*     */       }
/*     */       
/* 140 */       if (keys)
/*     */       {
/* 142 */         if (this.sharedSecret != null)
/*     */         {
/* 144 */           throw new CryptoManagerException("phase error: keys already received");
/*     */         }
/*     */         
/* 147 */         byte[] rawRemoteOtherPubkey = getBytes(message, 65535);
/*     */         
/* 149 */         byte[] rawRemoteEphemeralPubkey = getBytes(message, 65535);
/*     */         
/* 151 */         byte[] remoteSig = getBytes(message, 65535);
/*     */         
/* 153 */         byte[] pad = getBytes(message, 65535);
/*     */         
/* 155 */         this.remotePubKey = CryptoECCUtils.rawdataToPubkey(rawRemoteOtherPubkey);
/*     */         
/* 157 */         Signature check = CryptoECCUtils.getSignature(this.remotePubKey);
/*     */         
/* 159 */         check.update(rawRemoteOtherPubkey);
/*     */         
/* 161 */         check.update(rawRemoteEphemeralPubkey);
/*     */         
/* 163 */         if (check.verify(remoteSig))
/*     */         {
/* 165 */           this.ecDH.doPhase(CryptoECCUtils.rawdataToPubkey(rawRemoteEphemeralPubkey), true);
/*     */           
/* 167 */           this.sharedSecret = this.ecDH.generateSecret();
/*     */         }
/*     */         else
/*     */         {
/* 171 */           throw new CryptoManagerException("Signature check failed");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 176 */         if (this.sharedSecret == null)
/*     */         {
/* 178 */           throw new CryptoManagerException("phase error: keys not received");
/*     */         }
/*     */         
/* 181 */         byte[] IV = getBytes(message, 65535);
/*     */         
/* 183 */         byte[] remoteSig = getBytes(message, 65535);
/*     */         
/* 185 */         Signature check = CryptoECCUtils.getSignature(this.remotePubKey);
/*     */         
/* 187 */         check.update(IV);
/*     */         
/* 189 */         check.update(this.sharedSecret);
/*     */         
/* 191 */         if (!check.verify(remoteSig))
/*     */         {
/* 193 */           throw new CryptoManagerException("Signature check failed");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (CryptoManagerException e) {
/* 198 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 202 */       throw new CryptoManagerException("Failed to generate message");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getMessage(ByteBuffer buffer, boolean keys)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 216 */       putInt(buffer, 1, 255);
/*     */       
/* 218 */       SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
/*     */       
/* 220 */       Signature sig = CryptoECCUtils.getSignature(this.myPrivateKey);
/*     */       
/* 222 */       if (keys)
/*     */       {
/* 224 */         byte[] rawMyPubkey = CryptoECCUtils.keyToRawdata(this.myPublicKey);
/*     */         
/* 226 */         byte[] rawEphemeralPubkey = CryptoECCUtils.keyToRawdata(this.ephemeralKeyPair.getPublic());
/*     */         
/* 228 */         sig.update(rawMyPubkey);
/*     */         
/* 230 */         sig.update(rawEphemeralPubkey);
/*     */         
/* 232 */         byte[] rawSign = sig.sign();
/*     */         
/* 234 */         byte[] pad = new byte[random.nextInt(32)];
/*     */         
/* 236 */         random.nextBytes(pad);
/*     */         
/* 238 */         putBytes(buffer, rawMyPubkey, 65535);
/*     */         
/* 240 */         putBytes(buffer, rawEphemeralPubkey, 65535);
/*     */         
/* 242 */         putBytes(buffer, rawSign, 65535);
/*     */         
/* 244 */         putBytes(buffer, pad, 65535);
/*     */       }
/*     */       else
/*     */       {
/* 248 */         if (this.sharedSecret == null)
/*     */         {
/* 250 */           throw new CryptoManagerException("phase error: keys not received");
/*     */         }
/*     */         
/* 253 */         byte[] IV = new byte[20 + random.nextInt(32)];
/*     */         
/* 255 */         random.nextBytes(IV);
/*     */         
/* 257 */         sig.update(IV);
/*     */         
/* 259 */         sig.update(this.sharedSecret);
/*     */         
/* 261 */         byte[] rawSig = sig.sign();
/*     */         
/* 263 */         putBytes(buffer, IV, 65535);
/*     */         
/* 265 */         putBytes(buffer, rawSig, 65535);
/*     */       }
/*     */     }
/*     */     catch (CryptoManagerException e) {
/* 269 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 273 */       throw new CryptoManagerException("Failed to generate message");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getSharedSecret()
/*     */     throws CryptoManagerException
/*     */   {
/* 282 */     if (this.sharedSecret == null)
/*     */     {
/* 284 */       throw new CryptoManagerException("secret not yet available");
/*     */     }
/*     */     
/* 287 */     return this.sharedSecret;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getRemotePublicKey()
/*     */     throws CryptoManagerException
/*     */   {
/* 295 */     if (this.remotePubKey == null)
/*     */     {
/* 297 */       throw new CryptoManagerException("key not yet available");
/*     */     }
/*     */     
/* 300 */     return CryptoECCUtils.keyToRawdata(this.remotePubKey);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getInt(ByteBuffer buffer, int max_size)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 311 */       if (max_size < 256)
/*     */       {
/* 313 */         return buffer.get() & 0xFF;
/*     */       }
/* 315 */       if (max_size < 65536)
/*     */       {
/* 317 */         return buffer.getShort() & 0xFFFF;
/*     */       }
/*     */       
/*     */ 
/* 321 */       return buffer.getInt();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 325 */       throw new CryptoManagerException("Failed to get int", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] getBytes(ByteBuffer buffer, int max_size)
/*     */     throws CryptoManagerException
/*     */   {
/* 336 */     int len = getInt(buffer, max_size);
/*     */     
/* 338 */     if (len > max_size)
/*     */     {
/* 340 */       throw new CryptoManagerException("Invalid length");
/*     */     }
/*     */     try
/*     */     {
/* 344 */       byte[] res = new byte[len];
/*     */       
/* 346 */       buffer.get(res);
/*     */       
/* 348 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 352 */       throw new CryptoManagerException("Failed to get byte[]", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void putInt(ByteBuffer buffer, int value, int max_size)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 365 */       if (max_size < 256)
/*     */       {
/* 367 */         buffer.put((byte)value);
/*     */       }
/* 369 */       else if (max_size < 65536)
/*     */       {
/* 371 */         buffer.putShort((short)value);
/*     */       }
/*     */       else
/*     */       {
/* 375 */         buffer.putInt(value);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 379 */       throw new CryptoManagerException("Failed to put int", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void putBytes(ByteBuffer buffer, byte[] value, int max_size)
/*     */     throws CryptoManagerException
/*     */   {
/* 391 */     putInt(buffer, value.length, max_size);
/*     */     try
/*     */     {
/* 394 */       buffer.put(value);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 398 */       throw new CryptoManagerException("Failed to put byte[]", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static class InternalDH
/*     */     extends JCEECDHKeyAgreement.DH
/*     */   {
/*     */     public void init(Key key)
/*     */       throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */     {
/* 414 */       engineInit(key, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Key doPhase(Key key, boolean lastPhase)
/*     */       throws InvalidKeyException, IllegalStateException
/*     */     {
/* 424 */       return engineDoPhase(key, lastPhase);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public byte[] generateSecret()
/*     */       throws IllegalStateException
/*     */     {
/* 432 */       return engineGenerateSecret();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/impl/CryptoSTSEngineImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */