/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.security.Signature;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
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
/*     */ public class SubscriptionBodyImpl
/*     */ {
/*     */   private static final int SIMPLE_ID_LENGTH = 10;
/*     */   private SubscriptionManagerImpl manager;
/*     */   private String name;
/*     */   private boolean is_public;
/*     */   private Boolean is_anonymous;
/*     */   private byte[] public_key;
/*     */   private int version;
/*     */   private int az_version;
/*     */   private String json;
/*     */   private Map singleton_details;
/*     */   private byte[] hash;
/*     */   private byte[] sig;
/*     */   private int sig_data_size;
/*     */   private Map map;
/*     */   
/*     */   protected static byte[] encode(byte[] hash, int version, int size)
/*     */   {
/*  50 */     int hash_len = hash.length;
/*     */     
/*  52 */     byte[] result = new byte[hash_len + 4 + 4];
/*     */     
/*  54 */     System.arraycopy(hash, 0, result, 0, hash_len);
/*  55 */     System.arraycopy(SubscriptionImpl.intToBytes(version), 0, result, hash_len, 4);
/*  56 */     System.arraycopy(SubscriptionImpl.intToBytes(size), 0, result, hash_len + 4, 4);
/*     */     
/*  58 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static byte[] sign(byte[] private_key, byte[] hash, int version, int size)
/*     */     throws Exception
/*     */   {
/*  70 */     Signature signature = CryptoECCUtils.getSignature(CryptoECCUtils.rawdataToPrivkey(private_key));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */     signature.update(encode(hash, version, size));
/*     */     
/*  78 */     return signature.sign();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean verify(byte[] public_key, byte[] hash, int version, int size, byte[] sig)
/*     */   {
/*     */     try
/*     */     {
/*  90 */       Signature signature = CryptoECCUtils.getSignature(CryptoECCUtils.rawdataToPubkey(public_key));
/*     */       
/*  92 */       signature.update(encode(hash, version, size));
/*     */       
/*  94 */       return signature.verify(sig);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  98 */       Debug.out(e);
/*     */     }
/* 100 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static byte[] deriveShortID(byte[] public_key, Map singleton_details)
/*     */   {
/* 109 */     if (singleton_details != null)
/*     */     {
/* 111 */       return deriveSingletonShortID(singleton_details);
/*     */     }
/*     */     
/*     */ 
/* 115 */     byte[] hash = new SHA1Simple().calculateHash(public_key);
/*     */     
/* 117 */     byte[] short_id = new byte[10];
/*     */     
/* 119 */     System.arraycopy(hash, 0, short_id, 0, 10);
/*     */     
/* 121 */     return short_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static byte[] deriveSingletonShortID(Map singleton_details)
/*     */   {
/* 129 */     byte[] short_id = new byte[10];
/*     */     
/* 131 */     byte[] explicit_sid = new SHA1Simple().calculateHash((byte[])singleton_details.get("key"));
/*     */     
/* 133 */     System.arraycopy(explicit_sid, 0, short_id, 0, 10);
/*     */     
/* 135 */     return short_id;
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
/*     */   protected SubscriptionBodyImpl(SubscriptionManagerImpl _manager, SubscriptionImpl _subs)
/*     */     throws SubscriptionException
/*     */   {
/* 166 */     this.manager = _manager;
/*     */     try
/*     */     {
/* 169 */       File vuze_file = this.manager.getVuzeFile(_subs);
/*     */       
/* 171 */       VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(vuze_file.getAbsolutePath());
/*     */       
/* 173 */       if (vf == null)
/*     */       {
/* 175 */         throw new IOException("Failed to load vuze file '" + vuze_file + "'");
/*     */       }
/*     */       
/* 178 */       load(vf.getComponents()[0].getContent(), false);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 182 */       rethrow(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SubscriptionBodyImpl(SubscriptionManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/* 195 */     this.manager = _manager;
/*     */     
/* 197 */     load(_map, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void load(Map _map, boolean _verify)
/*     */     throws IOException
/*     */   {
/* 207 */     this.map = _map;
/*     */     
/* 209 */     this.hash = ((byte[])this.map.get("hash"));
/* 210 */     this.sig = ((byte[])this.map.get("sig"));
/* 211 */     Long l_size = (Long)this.map.get("size");
/*     */     
/* 213 */     Map details = (Map)this.map.get("details");
/*     */     
/* 215 */     if ((details == null) || (this.hash == null) || (this.sig == null) || (l_size == null))
/*     */     {
/* 217 */       throw new IOException("Invalid subscription - details missing");
/*     */     }
/*     */     
/* 220 */     this.sig_data_size = l_size.intValue();
/*     */     
/* 222 */     this.name = new String((byte[])details.get("name"), "UTF-8");
/* 223 */     this.public_key = ((byte[])details.get("public_key"));
/* 224 */     this.version = ((Long)details.get("version")).intValue();
/* 225 */     this.is_public = (((Long)details.get("is_public")).intValue() == 1);
/* 226 */     Long anon = (Long)details.get("is_anonymous");
/* 227 */     this.is_anonymous = (anon == null ? null : Boolean.valueOf(anon.longValue() == 1L));
/* 228 */     this.json = new String((byte[])details.get("json"), "UTF-8");
/*     */     
/* 230 */     this.singleton_details = ((Map)details.get("sin_details"));
/*     */     
/* 232 */     Long l_az_version = (Long)details.get("az_version");
/*     */     
/* 234 */     this.az_version = (l_az_version == null ? 1 : l_az_version.intValue());
/*     */     
/* 236 */     if (_verify)
/*     */     {
/*     */ 
/*     */ 
/* 240 */       byte[] contents = BEncoder.encode(details);
/*     */       
/* 242 */       byte[] actual_hash = new SHA1Simple().calculateHash(contents);
/*     */       
/* 244 */       if (!Arrays.equals(actual_hash, this.hash))
/*     */       {
/*     */ 
/*     */ 
/* 248 */         Map details_copy = new HashMap(details);
/*     */         
/* 250 */         details_copy.remove("az_version");
/*     */         
/* 252 */         contents = BEncoder.encode(details_copy);
/*     */         
/* 254 */         actual_hash = new SHA1Simple().calculateHash(contents);
/*     */       }
/*     */       
/* 257 */       if (!Arrays.equals(actual_hash, this.hash))
/*     */       {
/* 259 */         throw new IOException("Hash mismatch");
/*     */       }
/*     */       
/* 262 */       if (this.sig_data_size != contents.length)
/*     */       {
/* 264 */         throw new IOException("Signature data length mismatch");
/*     */       }
/*     */       
/* 267 */       if (!verify(this.public_key, this.hash, this.version, this.sig_data_size, this.sig))
/*     */       {
/* 269 */         throw new IOException("Signature verification failed");
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SubscriptionBodyImpl(SubscriptionManagerImpl _manager, String _name, boolean _is_public, boolean _is_anonymous, String _json_content, byte[] _public_key, int _version, int _az_version, Map _singleton_details)
/*     */     throws IOException
/*     */   {
/* 290 */     this.manager = _manager;
/*     */     
/* 292 */     this.name = _name;
/* 293 */     this.is_public = _is_public;
/* 294 */     this.is_anonymous = Boolean.valueOf(_is_anonymous);
/* 295 */     this.public_key = _public_key;
/* 296 */     this.version = _version;
/* 297 */     this.az_version = _az_version;
/* 298 */     this.json = _json_content;
/*     */     
/* 300 */     this.singleton_details = _singleton_details;
/*     */     
/* 302 */     this.map = new HashMap();
/*     */     
/* 304 */     Map details = new HashMap();
/*     */     
/* 306 */     this.map.put("details", details);
/*     */     
/* 308 */     details.put("name", this.name.getBytes("UTF-8"));
/* 309 */     details.put("is_public", new Long(this.is_public ? 1L : 0L));
/* 310 */     if (this.is_anonymous.booleanValue()) {
/* 311 */       details.put("is_anonymous", new Long(1L));
/*     */     }
/* 313 */     details.put("public_key", this.public_key);
/* 314 */     details.put("version", new Long(this.version));
/* 315 */     details.put("az_version", new Long(this.az_version));
/* 316 */     details.put("json", _json_content.getBytes("UTF-8"));
/*     */     
/* 318 */     if (this.singleton_details != null)
/*     */     {
/* 320 */       details.put("sin_details", this.singleton_details);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateDetails(SubscriptionImpl subs, Map details)
/*     */     throws IOException
/*     */   {
/* 331 */     this.is_public = subs.isPublic();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 337 */     if (this.is_anonymous == null) {
/* 338 */       if (subs.isAnonymous()) {
/* 339 */         this.is_anonymous = Boolean.valueOf(true);
/*     */       }
/*     */     } else {
/* 342 */       this.is_anonymous = Boolean.valueOf(subs.isAnonymous());
/*     */     }
/*     */     
/* 345 */     this.version = subs.getVersion();
/* 346 */     this.az_version = subs.getAZVersion();
/* 347 */     this.name = subs.getName(false);
/*     */     
/* 349 */     details.put("name", this.name.getBytes("UTF-8"));
/* 350 */     details.put("is_public", new Long(this.is_public ? 1L : 0L));
/* 351 */     if (this.is_anonymous != null) {
/* 352 */       details.put("is_anonymous", new Long(this.is_anonymous.booleanValue() ? 1L : 0L));
/*     */     }
/* 354 */     details.put("version", new Long(this.version));
/* 355 */     details.put("az_version", new Long(this.az_version));
/*     */     
/* 357 */     if (this.json != null)
/*     */     {
/* 359 */       details.put("json", this.json.getBytes("UTF-8"));
/*     */     }
/*     */     
/* 362 */     if (this.singleton_details != null)
/*     */     {
/* 364 */       details.put("sin_details", this.singleton_details);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getName()
/*     */   {
/* 371 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getPublicKey()
/*     */   {
/* 377 */     return this.public_key;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getShortID()
/*     */   {
/* 383 */     return deriveShortID(this.public_key, this.singleton_details);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isPublic()
/*     */   {
/* 389 */     return this.is_public;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isAnonymous()
/*     */   {
/* 395 */     return this.is_anonymous == null ? false : this.is_anonymous.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getJSON()
/*     */   {
/* 401 */     return this.json;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map getSingletonDetails()
/*     */   {
/* 407 */     return this.singleton_details;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setJSON(String _json)
/*     */   {
/* 414 */     this.json = _json;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getVersion()
/*     */   {
/* 420 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getAZVersion()
/*     */   {
/* 426 */     return this.az_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] getHash()
/*     */   {
/* 434 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getSig()
/*     */   {
/* 440 */     return this.sig;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getSigDataSize()
/*     */   {
/* 446 */     return this.sig_data_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeVuzeFile(SubscriptionImpl subs)
/*     */     throws SubscriptionException
/*     */   {
/*     */     try
/*     */     {
/* 456 */       File file = this.manager.getVuzeFile(subs);
/*     */       
/* 458 */       Map details = (Map)this.map.get("details");
/*     */       
/* 460 */       updateDetails(subs, details);
/*     */       
/* 462 */       byte[] contents = BEncoder.encode(details);
/*     */       
/* 464 */       byte[] new_hash = new SHA1Simple().calculateHash(contents);
/*     */       
/* 466 */       byte[] old_hash = (byte[])this.map.get("hash");
/*     */       
/*     */ 
/*     */ 
/* 470 */       if ((old_hash != null) && (!Arrays.equals(old_hash, new_hash)))
/*     */       {
/* 472 */         Map details_copy = new HashMap(details);
/*     */         
/* 474 */         details_copy.remove("az_version");
/*     */         
/* 476 */         contents = BEncoder.encode(details_copy);
/*     */         
/* 478 */         new_hash = new SHA1Simple().calculateHash(contents);
/*     */       }
/*     */       
/* 481 */       if ((old_hash == null) || (!Arrays.equals(old_hash, new_hash)))
/*     */       {
/* 483 */         byte[] private_key = subs.getPrivateKey();
/*     */         
/* 485 */         if (private_key == null)
/*     */         {
/* 487 */           throw new SubscriptionException("Only the originator of a subscription can modify it");
/*     */         }
/*     */         
/* 490 */         this.map.put("size", new Long(contents.length));
/*     */         try
/*     */         {
/* 493 */           this.map.put("hash", new_hash);
/* 494 */           this.map.put("sig", sign(private_key, new_hash, this.version, contents.length));
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 498 */           throw new SubscriptionException("Crypto failed: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/*     */       
/* 502 */       File backup_file = null;
/*     */       
/* 504 */       if (file.exists())
/*     */       {
/* 506 */         backup_file = new File(file.getParent(), file.getName() + ".bak");
/*     */         
/* 508 */         backup_file.delete();
/*     */         
/* 510 */         if (!file.renameTo(backup_file))
/*     */         {
/* 512 */           throw new SubscriptionException("Backup failed");
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 517 */         VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*     */         
/* 519 */         vf.addComponent(16, this.map);
/*     */         
/* 521 */         vf.write(file);
/*     */         
/* 523 */         this.hash = new_hash;
/* 524 */         this.sig = ((byte[])this.map.get("sig"));
/* 525 */         this.sig_data_size = contents.length;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 529 */         if (backup_file != null)
/*     */         {
/* 531 */           backup_file.renameTo(file);
/*     */         }
/*     */         
/* 534 */         throw new SubscriptionException("File write failed: " + Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 538 */       rethrow(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void rethrow(Throwable e)
/*     */     throws SubscriptionException
/*     */   {
/* 548 */     if ((e instanceof SubscriptionException))
/*     */     {
/* 550 */       throw ((SubscriptionException)e);
/*     */     }
/*     */     
/* 553 */     throw new SubscriptionException("Operation failed", e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionBodyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */