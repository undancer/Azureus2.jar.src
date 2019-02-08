/*     */ package com.aelitis.azureus.core.security.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import com.aelitis.azureus.core.security.CryptoManager.SRPParameters;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerException;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerKeyListener;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordException;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordHandler.passwordDetails;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.SecretKeyFactory;
/*     */ import javax.crypto.spec.PBEKeySpec;
/*     */ import javax.crypto.spec.PBEParameterSpec;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SHA1;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.engines.RC4Engine;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
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
/*     */ public class CryptoManagerImpl
/*     */   implements CryptoManager
/*     */ {
/*     */   private static final int PBE_ITERATIONS = 100;
/*     */   private static final String PBE_ALG = "PBEWithMD5AndDES";
/*     */   private static CryptoManagerImpl singleton;
/*     */   private byte[] secure_id;
/*     */   private final CryptoHandler ecc_handler;
/*     */   
/*     */   public static synchronized CryptoManager getSingleton()
/*     */   {
/*  69 */     if (singleton == null)
/*     */     {
/*  71 */       singleton = new CryptoManagerImpl();
/*     */     }
/*     */     
/*  74 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  79 */   private final CopyOnWriteList password_handlers = new CopyOnWriteList();
/*  80 */   private final CopyOnWriteList keychange_listeners = new CopyOnWriteList();
/*     */   
/*  82 */   private final Map session_passwords = Collections.synchronizedMap(new HashMap());
/*     */   
/*     */ 
/*     */   protected CryptoManagerImpl()
/*     */   {
/*  87 */     SESecurityManager.initialise();
/*     */     
/*  89 */     long now = SystemTime.getCurrentTime();
/*     */     
/*  91 */     for (int i = 0; i < CryptoManager.HANDLERS.length; i++)
/*     */     {
/*  93 */       int handler = CryptoManager.HANDLERS[i];
/*     */       
/*  95 */       String persist_timeout_key = "core.crypto.pw." + handler + ".persist_timeout";
/*  96 */       String persist_pw_key = "core.crypto.pw." + handler + ".persist_value";
/*     */       
/*  98 */       long timeout = COConfigurationManager.getLongParameter(persist_timeout_key, 0L);
/*     */       
/* 100 */       if (now > timeout)
/*     */       {
/* 102 */         COConfigurationManager.setParameter(persist_timeout_key, 0);
/* 103 */         COConfigurationManager.setParameter(persist_pw_key, "");
/*     */       }
/*     */       else
/*     */       {
/* 107 */         addPasswordTimer(persist_timeout_key, persist_pw_key, timeout);
/*     */       }
/*     */     }
/*     */     
/* 111 */     this.ecc_handler = new CryptoHandlerECC(this, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addPasswordTimer(final String timeout_key, String pw_key, final long timeout)
/*     */   {
/* 120 */     if (timeout == Long.MAX_VALUE)
/*     */     {
/* 122 */       return;
/*     */     }
/*     */     
/* 125 */     SimpleTimer.addEvent("CryptoManager:pw_timeout", timeout, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 134 */         synchronized (CryptoManagerImpl.this)
/*     */         {
/* 136 */           if (COConfigurationManager.getLongParameter(timeout_key, 0L) == timeout)
/*     */           {
/* 138 */             COConfigurationManager.removeParameter(timeout_key);
/* 139 */             COConfigurationManager.removeParameter(this.val$pw_key);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getSecureID()
/*     */   {
/* 149 */     String key = "core.crypto.id";
/*     */     
/* 151 */     if (this.secure_id == null)
/*     */     {
/* 153 */       this.secure_id = COConfigurationManager.getByteParameter(key, null);
/*     */     }
/*     */     
/* 156 */     if (this.secure_id == null)
/*     */     {
/* 158 */       this.secure_id = new byte[20];
/*     */       
/* 160 */       RandomUtils.SECURE_RANDOM.nextBytes(this.secure_id);
/*     */       
/* 162 */       COConfigurationManager.setParameter(key, this.secure_id);
/*     */       
/* 164 */       COConfigurationManager.save();
/*     */     }
/*     */     
/* 167 */     return this.secure_id;
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] getOBSID()
/*     */   {
/* 173 */     String key = "core.crypto.obs.id";
/*     */     
/* 175 */     byte[] obs_id = COConfigurationManager.getByteParameter(key, null);
/*     */     
/* 177 */     if (obs_id == null)
/*     */     {
/* 179 */       obs_id = new byte[20];
/*     */       
/* 181 */       RandomUtils.SECURE_RANDOM.nextBytes(obs_id);
/*     */       
/* 183 */       COConfigurationManager.setParameter(key, obs_id);
/*     */       
/* 185 */       COConfigurationManager.save();
/*     */     }
/*     */     
/* 188 */     return obs_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] obfuscate(byte[] data)
/*     */   {
/* 195 */     RC4Engine engine = new RC4Engine();
/*     */     
/* 197 */     CipherParameters params = new KeyParameter(new SHA1Simple().calculateHash(getOBSID()));
/*     */     
/* 199 */     engine.init(true, params);
/*     */     
/* 201 */     byte[] temp = new byte['Ѐ'];
/*     */     
/* 203 */     engine.processBytes(temp, 0, 1024, temp, 0);
/*     */     
/* 205 */     byte[] obs_value = new byte[data.length];
/*     */     
/* 207 */     engine.processBytes(data, 0, data.length, obs_value, 0);
/*     */     
/* 209 */     return obs_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] deobfuscate(byte[] data)
/*     */   {
/* 216 */     return obfuscate(data);
/*     */   }
/*     */   
/*     */ 
/*     */   public CryptoHandler getECCHandler()
/*     */   {
/* 222 */     return this.ecc_handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] encryptWithPBE(byte[] data, char[] password)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 233 */       byte[] salt = new byte[8];
/*     */       
/* 235 */       RandomUtils.SECURE_RANDOM.nextBytes(salt);
/*     */       
/* 237 */       PBEKeySpec keySpec = new PBEKeySpec(password);
/*     */       
/* 239 */       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
/*     */       
/* 241 */       SecretKey key = keyFactory.generateSecret(keySpec);
/*     */       
/* 243 */       PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
/*     */       
/* 245 */       Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
/*     */       
/* 247 */       cipher.init(1, key, paramSpec);
/*     */       
/* 249 */       byte[] enc = cipher.doFinal(data);
/*     */       
/* 251 */       byte[] res = new byte[salt.length + enc.length];
/*     */       
/* 253 */       System.arraycopy(salt, 0, res, 0, salt.length);
/*     */       
/* 255 */       System.arraycopy(enc, 0, res, salt.length, enc.length);
/*     */       
/* 257 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 261 */       throw new CryptoManagerException("PBE encryption failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] decryptWithPBE(byte[] data, char[] password)
/*     */     throws CryptoManagerException
/*     */   {
/* 272 */     boolean fail_is_pw_error = false;
/*     */     try
/*     */     {
/* 275 */       byte[] salt = new byte[8];
/*     */       
/* 277 */       System.arraycopy(data, 0, salt, 0, 8);
/*     */       
/* 279 */       PBEKeySpec keySpec = new PBEKeySpec(password);
/*     */       
/* 281 */       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
/*     */       
/* 283 */       SecretKey key = keyFactory.generateSecret(keySpec);
/*     */       
/* 285 */       PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
/*     */       
/* 287 */       Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
/*     */       
/* 289 */       cipher.init(2, key, paramSpec);
/*     */       
/* 291 */       fail_is_pw_error = true;
/*     */       
/* 293 */       return cipher.doFinal(data, 8, data.length - 8);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 297 */       if (fail_is_pw_error)
/*     */       {
/* 299 */         throw new CryptoManagerPasswordException(true, "Password incorrect", e);
/*     */       }
/*     */       
/* 302 */       throw new CryptoManagerException("PBE decryption failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearPasswords()
/*     */   {
/* 310 */     clearPasswords(3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearPasswords(int password_handler_type)
/*     */   {
/* 317 */     this.session_passwords.clear();
/*     */     
/* 319 */     for (int i = 0; i < CryptoManager.HANDLERS.length; i++)
/*     */     {
/* 321 */       clearPassword(CryptoManager.HANDLERS[i], password_handler_type);
/*     */     }
/*     */     
/* 324 */     this.ecc_handler.lock();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void clearPassword(int handler, int password_handler_type)
/*     */   {
/* 332 */     String persist_timeout_key = "core.crypto.pw." + handler + ".persist_timeout";
/* 333 */     String persist_pw_key = "core.crypto.pw." + handler + ".persist_value";
/* 334 */     String persist_pw_key_type = "core.crypto.pw." + handler + ".persist_type";
/*     */     
/* 336 */     int pw_type = (int)COConfigurationManager.getLongParameter(persist_pw_key_type, 1L);
/*     */     
/* 338 */     if ((password_handler_type == 3) || (password_handler_type == pw_type))
/*     */     {
/*     */ 
/* 341 */       COConfigurationManager.removeParameter(persist_timeout_key);
/* 342 */       COConfigurationManager.removeParameter(persist_pw_key);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected passwordDetails setPassword(int handler, int pw_type, char[] pw_chars, long timeout)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/* 356 */       String persist_timeout_key = "core.crypto.pw." + handler + ".persist_timeout";
/* 357 */       String persist_pw_key = "core.crypto.pw." + handler + ".persist_value";
/* 358 */       String persist_pw_key_type = "core.crypto.pw." + handler + ".persist_type";
/*     */       
/* 360 */       byte[] salt = getPasswordSalt();
/* 361 */       byte[] pw_bytes = new String(pw_chars).getBytes("UTF8");
/*     */       
/* 363 */       SHA1 sha1 = new SHA1();
/*     */       
/* 365 */       sha1.update(ByteBuffer.wrap(salt));
/* 366 */       sha1.update(ByteBuffer.wrap(pw_bytes));
/*     */       
/* 368 */       String encoded_pw = ByteFormatter.encodeString(sha1.digest());
/*     */       
/* 370 */       COConfigurationManager.setParameter(persist_timeout_key, timeout);
/* 371 */       COConfigurationManager.setParameter(persist_pw_key_type, pw_type);
/* 372 */       COConfigurationManager.setParameter(persist_pw_key, encoded_pw);
/*     */       
/* 374 */       return new passwordDetails(encoded_pw.toCharArray(), pw_type);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 380 */       throw new CryptoManagerException("setPassword failed", e);
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
/*     */   protected passwordDetails getPassword(int handler, int action, String reason, passwordTester tester, int pw_type)
/*     */     throws CryptoManagerException
/*     */   {
/* 394 */     String persist_timeout_key = "core.crypto.pw." + handler + ".persist_timeout";
/* 395 */     String persist_pw_key = "core.crypto.pw." + handler + ".persist_value";
/* 396 */     String persist_pw_key_type = "core.crypto.pw." + handler + ".persist_type";
/*     */     
/* 398 */     long current_timeout = COConfigurationManager.getLongParameter(persist_timeout_key, 0L);
/*     */     
/*     */ 
/*     */ 
/* 402 */     if (current_timeout < 0L)
/*     */     {
/* 404 */       passwordDetails pw = (passwordDetails)this.session_passwords.get(persist_pw_key);
/*     */       
/* 406 */       if ((pw != null) && (pw.getHandlerType() == pw_type))
/*     */       {
/* 408 */         return pw;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 414 */     if (current_timeout > SystemTime.getCurrentTime())
/*     */     {
/* 416 */       String current_pw = COConfigurationManager.getStringParameter(persist_pw_key, "");
/*     */       
/* 418 */       if (current_pw.length() > 0)
/*     */       {
/* 420 */         int type = (int)COConfigurationManager.getLongParameter(persist_pw_key_type, 1L);
/*     */         
/* 422 */         if (type == pw_type)
/*     */         {
/* 424 */           return new passwordDetails(current_pw.toCharArray(), type);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 429 */     Iterator it = this.password_handlers.iterator();
/*     */     
/* 431 */     while (it.hasNext())
/*     */     {
/* 433 */       int retry_count = 0;
/*     */       
/* 435 */       char[] last_pw_chars = null;
/*     */       
/* 437 */       CryptoManagerPasswordHandler provider = (CryptoManagerPasswordHandler)it.next();
/*     */       
/* 439 */       if ((pw_type == 0) || (pw_type == provider.getHandlerType()))
/*     */       {
/*     */ 
/*     */         for (;;)
/*     */         {
/*     */ 
/* 445 */           if (retry_count < 64) {
/*     */             try
/*     */             {
/* 448 */               CryptoManagerPasswordHandler.passwordDetails details = provider.getPassword(handler, action, retry_count > 0, reason);
/*     */               
/* 450 */               if (details != null)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 457 */                 char[] pw_chars = details.getPassword();
/*     */                 
/* 459 */                 if ((last_pw_chars != null) && (Arrays.equals(last_pw_chars, pw_chars)))
/*     */                 {
/*     */ 
/*     */ 
/* 463 */                   retry_count++;
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/* 468 */                   last_pw_chars = pw_chars;
/*     */                   
/*     */ 
/*     */ 
/* 472 */                   byte[] salt = getPasswordSalt();
/* 473 */                   byte[] pw_bytes = new String(pw_chars).getBytes("UTF8");
/*     */                   
/* 475 */                   SHA1 sha1 = new SHA1();
/*     */                   
/* 477 */                   sha1.update(ByteBuffer.wrap(salt));
/* 478 */                   sha1.update(ByteBuffer.wrap(pw_bytes));
/*     */                   
/* 480 */                   String encoded_pw = ByteFormatter.encodeString(sha1.digest());
/*     */                   
/* 482 */                   if ((tester != null) && (!tester.testPassword(encoded_pw.toCharArray())))
/*     */                   {
/*     */ 
/*     */ 
/* 486 */                     retry_count++;
/*     */ 
/*     */                   }
/*     */                   else
/*     */                   {
/* 491 */                     int persist_secs = details.getPersistForSeconds();
/*     */                     
/*     */                     long timeout;
/*     */                     long timeout;
/* 495 */                     if (persist_secs == 0)
/*     */                     {
/* 497 */                       timeout = 0L;
/*     */                     } else { long timeout;
/* 499 */                       if (persist_secs == Integer.MAX_VALUE)
/*     */                       {
/* 501 */                         timeout = Long.MAX_VALUE;
/*     */                       } else { long timeout;
/* 503 */                         if (persist_secs < 0)
/*     */                         {
/*     */ 
/*     */ 
/* 507 */                           timeout = -1L;
/*     */                         }
/*     */                         else
/*     */                         {
/* 511 */                           timeout = SystemTime.getCurrentTime() + persist_secs * 1000L; }
/*     */                       }
/*     */                     }
/* 514 */                     passwordDetails result = new passwordDetails(encoded_pw.toCharArray(), provider.getHandlerType());
/*     */                     
/* 516 */                     synchronized (this)
/*     */                     {
/* 518 */                       COConfigurationManager.setParameter(persist_timeout_key, timeout);
/* 519 */                       COConfigurationManager.setParameter(persist_pw_key_type, provider.getHandlerType());
/*     */                       
/* 521 */                       this.session_passwords.remove(persist_pw_key);
/*     */                       
/* 523 */                       COConfigurationManager.removeParameter(persist_pw_key);
/*     */                       
/* 525 */                       if (timeout < 0L)
/*     */                       {
/* 527 */                         this.session_passwords.put(persist_pw_key, result);
/*     */                       }
/* 529 */                       else if (timeout > 0L)
/*     */                       {
/* 531 */                         COConfigurationManager.setParameter(persist_pw_key, encoded_pw);
/*     */                         
/* 533 */                         addPasswordTimer(persist_timeout_key, persist_pw_key, timeout);
/*     */                       }
/*     */                     }
/*     */                     
/* 537 */                     provider.passwordOK(handler, details);
/*     */                     
/* 539 */                     return result;
/*     */                   }
/*     */                 }
/*     */               }
/* 543 */             } catch (Throwable e) { Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 552 */     throw new CryptoManagerPasswordException(false, "No password handlers returned a password");
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getPasswordSalt()
/*     */   {
/* 558 */     return getSecureID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSecureID(byte[] id)
/*     */   {
/* 565 */     String key = "core.crypto.id";
/*     */     
/* 567 */     COConfigurationManager.setParameter(key, id);
/*     */     
/* 569 */     COConfigurationManager.save();
/*     */     
/* 571 */     this.secure_id = id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void keyChanged(CryptoHandler handler)
/*     */   {
/* 578 */     Iterator it = this.keychange_listeners.iterator();
/*     */     
/* 580 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 583 */         ((CryptoManagerKeyListener)it.next()).keyChanged(handler);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 587 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void lockChanged(CryptoHandler handler)
/*     */   {
/* 596 */     Iterator it = this.keychange_listeners.iterator();
/*     */     
/* 598 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 601 */         ((CryptoManagerKeyListener)it.next()).keyLockStatusChanged(handler);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 605 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPasswordHandler(CryptoManagerPasswordHandler handler)
/*     */   {
/* 614 */     this.password_handlers.add(handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePasswordHandler(CryptoManagerPasswordHandler handler)
/*     */   {
/* 621 */     this.password_handlers.remove(handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addKeyListener(CryptoManagerKeyListener listener)
/*     */   {
/* 628 */     this.keychange_listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeKeyListener(CryptoManagerKeyListener listener)
/*     */   {
/* 635 */     this.keychange_listeners.remove(listener);
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
/*     */   public void setSRPParameters(byte[] salt, BigInteger verifier)
/*     */   {
/* 651 */     if (salt == null)
/*     */     {
/* 653 */       COConfigurationManager.removeParameter("core.crypto.srp.def.salt");
/* 654 */       COConfigurationManager.removeParameter("core.crypto.srp.def.verifier");
/*     */     }
/*     */     else
/*     */     {
/* 658 */       COConfigurationManager.setParameter("core.crypto.srp.def.salt", salt);
/* 659 */       COConfigurationManager.setParameter("core.crypto.srp.def.verifier", verifier.toByteArray());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public CryptoManager.SRPParameters getSRPParameters()
/*     */   {
/* 666 */     byte[] salt = COConfigurationManager.getByteParameter("core.crypto.srp.def.salt", null);
/* 667 */     byte[] verifier = COConfigurationManager.getByteParameter("core.crypto.srp.def.verifier", null);
/*     */     
/* 669 */     if ((salt != null) && (verifier != null))
/*     */     {
/* 671 */       return new SRPParametersImpl(salt, new BigInteger(verifier), null);
/*     */     }
/*     */     
/* 674 */     return null;
/*     */   }
/*     */   
/*     */   public static abstract interface passwordTester
/*     */   {
/*     */     public abstract boolean testPassword(char[] paramArrayOfChar);
/*     */   }
/*     */   
/*     */   protected static class passwordDetails
/*     */   {
/*     */     private final char[] password;
/*     */     private final int type;
/*     */     
/*     */     protected passwordDetails(char[] _password, int _type) {
/* 688 */       this.password = _password;
/* 689 */       this.type = _type;
/*     */     }
/*     */     
/*     */ 
/*     */     public char[] getPassword()
/*     */     {
/* 695 */       return this.password;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getHandlerType()
/*     */     {
/* 701 */       return this.type;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class SRPParametersImpl
/*     */     implements CryptoManager.SRPParameters
/*     */   {
/*     */     private final byte[] salt;
/*     */     
/*     */     private final BigInteger verifier;
/*     */     
/*     */ 
/*     */     private SRPParametersImpl(byte[] _salt, BigInteger _verifier)
/*     */     {
/* 717 */       this.salt = _salt;
/* 718 */       this.verifier = _verifier;
/*     */     }
/*     */     
/*     */ 
/*     */     public byte[] getSalt()
/*     */     {
/* 724 */       return this.salt;
/*     */     }
/*     */     
/*     */ 
/*     */     public BigInteger getVerifier()
/*     */     {
/* 730 */       return this.verifier;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 740 */       String stuff = "12345";
/*     */       
/* 742 */       CryptoManagerImpl man = (CryptoManagerImpl)getSingleton();
/*     */       
/* 744 */       man.addPasswordHandler(new CryptoManagerPasswordHandler()
/*     */       {
/*     */ 
/*     */         public int getHandlerType()
/*     */         {
/*     */ 
/* 750 */           return 1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public CryptoManagerPasswordHandler.passwordDetails getPassword(int handler_type, int action_type, boolean last_pw_incorrect, String reason)
/*     */         {
/* 760 */           new CryptoManagerPasswordHandler.passwordDetails()
/*     */           {
/*     */ 
/*     */             public char[] getPassword()
/*     */             {
/*     */ 
/* 766 */               return "trout".toCharArray();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getPersistForSeconds()
/*     */             {
/* 772 */               return 10;
/*     */             }
/*     */           };
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void passwordOK(int handler_type, CryptoManagerPasswordHandler.passwordDetails details) {}
/* 784 */       });
/* 785 */       CryptoHandler handler1 = man.getECCHandler();
/*     */       
/* 787 */       CryptoHandler handler2 = new CryptoHandlerECC(man, 2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 793 */       byte[] sig = handler1.sign(stuff.getBytes(), "h1: sign");
/*     */       
/* 795 */       System.out.println(handler1.verify(handler1.getPublicKey("h1: Test verify"), stuff.getBytes(), sig));
/*     */       
/* 797 */       handler1.lock();
/*     */       
/* 799 */       byte[] enc = handler1.encrypt(handler2.getPublicKey("h2: getPublic"), stuff.getBytes(), "h1: encrypt");
/*     */       
/* 801 */       System.out.println("pk1 = " + ByteFormatter.encodeString(handler1.getPublicKey("h1: getPublic")));
/* 802 */       System.out.println("pk2 = " + ByteFormatter.encodeString(handler2.getPublicKey("h2: getPublic")));
/*     */       
/* 804 */       System.out.println("dec: " + new String(handler2.decrypt(handler1.getPublicKey("h1: getPublic"), enc, "h2: decrypt")));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 808 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/impl/CryptoManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */