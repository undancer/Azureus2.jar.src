/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.security;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.security.CryptoHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerException;
/*     */ import com.aelitis.azureus.core.security.CryptoSTSEngine;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageException;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnectionListener;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.plugins.utils.security.SEPublicKey;
/*     */ import org.gudy.azureus2.plugins.utils.security.SEPublicKeyLocator;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.GenericMessageConnectionImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ public class SESTSConnectionImpl
/*     */   implements GenericMessageConnection
/*     */ {
/*     */   private static final int CRYPTO_SETUP_TIMEOUT = 60000;
/*  69 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*  71 */   private static final byte[] AES_IV1 = { 21, -32, 107, 126, -104, 89, -28, -89, 52, 102, -83, 72, 53, -30, -48, 36 };
/*     */   
/*     */ 
/*     */ 
/*  75 */   private static final byte[] AES_IV2 = { -60, -17, 6, 60, -104, 35, -24, -76, 38, 88, -82, -71, 44, 36, -74, 17 };
/*     */   
/*     */ 
/*     */ 
/*  79 */   private final int AES_KEY_SIZE_BYTES = AES_IV1.length;
/*     */   
/*     */ 
/*     */   private static long last_incoming_sts_create;
/*     */   
/*  84 */   private static List connections = new ArrayList();
/*     */   private static final int BLOOM_RECREATE = 30000;
/*     */   private static final int BLOOM_INCREASE = 500;
/*     */   
/*  88 */   static { SimpleTimer.addPeriodicEvent("SESTSConnectionTimer", 15000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*  97 */         List to_close = new ArrayList();
/*     */         
/*  99 */         synchronized (SESTSConnectionImpl.connections)
/*     */         {
/* 101 */           for (int i = 0; i < SESTSConnectionImpl.connections.size(); i++)
/*     */           {
/* 103 */             SESTSConnectionImpl connection = (SESTSConnectionImpl)SESTSConnectionImpl.connections.get(i);
/*     */             
/* 105 */             if (!connection.crypto_complete.isReleasedForever())
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 110 */               long now = SystemTime.getCurrentTime();
/*     */               
/* 112 */               if (connection.create_time > now)
/*     */               {
/* 114 */                 connection.create_time = now;
/*     */               }
/*     */               else
/*     */               {
/* 118 */                 int time_allowed = connection.getConnectMethodCount() * 60000;
/*     */                 
/* 120 */                 if (now - connection.create_time > time_allowed)
/*     */                 {
/* 122 */                   to_close.add(connection);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 128 */         for (int i = 0; i < to_close.size(); i++)
/*     */         {
/* 130 */           ((SESTSConnectionImpl)to_close.get(i)).reportFailed(new Exception("Timeout during crypto setup"));
/*     */         }
/*     */       }
/*     */     }); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */   private static BloomFilter generate_bloom = BloomFilterFactory.createAddRemove4Bit(500);
/* 140 */   private static long generate_bloom_create_time = SystemTime.getCurrentTime();
/*     */   
/*     */   private AzureusCore core;
/*     */   
/*     */   private GenericMessageConnectionImpl connection;
/*     */   
/*     */   private SEPublicKey my_public_key;
/*     */   
/*     */   private SEPublicKeyLocator key_locator;
/*     */   
/*     */   private String reason;
/*     */   private int block_crypto;
/*     */   private long create_time;
/*     */   private CryptoSTSEngine sts_engine;
/* 154 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */   private boolean sent_keys;
/*     */   
/*     */   private boolean sent_auth;
/*     */   
/*     */   private PooledByteBuffer pending_message;
/* 161 */   private AESemaphore crypto_complete = new AESemaphore("SESTSConnection:send");
/*     */   
/*     */ 
/*     */ 
/*     */   private Cipher outgoing_cipher;
/*     */   
/*     */ 
/*     */ 
/*     */   private Cipher incoming_cipher;
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile boolean failed;
/*     */   
/*     */ 
/*     */ 
/*     */   protected SESTSConnectionImpl(AzureusCore _core, GenericMessageConnectionImpl _connection, SEPublicKey _my_public_key, SEPublicKeyLocator _key_locator, String _reason, int _block_crypto)
/*     */     throws Exception
/*     */   {
/* 180 */     this.core = _core;
/* 181 */     this.connection = _connection;
/* 182 */     this.my_public_key = _my_public_key;
/* 183 */     this.key_locator = _key_locator;
/* 184 */     this.reason = _reason;
/* 185 */     this.block_crypto = _block_crypto;
/*     */     
/* 187 */     this.create_time = SystemTime.getCurrentTime();
/*     */     
/* 189 */     synchronized (connections)
/*     */     {
/* 191 */       connections.add(this);
/*     */     }
/*     */     
/* 194 */     if (this.connection.isIncoming())
/*     */     {
/* 196 */       rateLimit(this.connection.getEndpoint().getNotionalAddress());
/*     */     }
/*     */     
/* 199 */     this.sts_engine = this.core.getCryptoManager().getECCHandler().getSTSEngine(this.reason);
/*     */     
/* 201 */     this.connection.addListener(new GenericMessageConnectionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void connected(GenericMessageConnection connection)
/*     */       {
/*     */ 
/* 208 */         SESTSConnectionImpl.this.reportConnected();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void receive(GenericMessageConnection connection, PooledByteBuffer message)
/*     */         throws MessageException
/*     */       {
/* 218 */         SESTSConnectionImpl.this.receive(message);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void failed(GenericMessageConnection connection, Throwable error)
/*     */         throws MessageException
/*     */       {
/* 228 */         SESTSConnectionImpl.this.reportFailed(error);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getConnectMethodCount()
/*     */   {
/* 236 */     return this.connection.getConnectMethodCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void rateLimit(InetSocketAddress originator)
/*     */     throws Exception
/*     */   {
/* 245 */     synchronized (SESTSConnectionImpl.class)
/*     */     {
/* 247 */       int hit_count = generate_bloom.add(AddressUtils.getAddressBytes(originator));
/*     */       
/* 249 */       long now = SystemTime.getCurrentTime();
/*     */       
/*     */ 
/*     */ 
/* 253 */       if (generate_bloom.getSize() / generate_bloom.getEntryCount() < 10)
/*     */       {
/* 255 */         generate_bloom = BloomFilterFactory.createAddRemove4Bit(generate_bloom.getSize() + 500);
/*     */         
/* 257 */         generate_bloom_create_time = now;
/*     */         
/* 259 */         Logger.log(new LogEvent(LOGID, "STS bloom: size increased to " + generate_bloom.getSize()));
/*     */       }
/* 261 */       else if ((now < generate_bloom_create_time) || (now - generate_bloom_create_time > 30000L))
/*     */       {
/* 263 */         generate_bloom = BloomFilterFactory.createAddRemove4Bit(generate_bloom.getSize());
/*     */         
/* 265 */         generate_bloom_create_time = now;
/*     */       }
/*     */       
/* 268 */       if (hit_count >= 15)
/*     */       {
/* 270 */         Logger.log(new LogEvent(LOGID, "STS bloom: too many recent connection attempts from " + originator));
/*     */         
/* 272 */         Debug.out("STS: too many recent connection attempts from " + originator);
/*     */         
/* 274 */         throw new IOException("Too many recent connection attempts (sts)");
/*     */       }
/*     */       
/* 277 */       long since_last = now - last_incoming_sts_create;
/*     */       
/* 279 */       long delay = 100L - since_last;
/*     */       
/*     */ 
/*     */ 
/* 283 */       if ((delay > 0L) && (delay < 100L)) {
/*     */         try
/*     */         {
/* 286 */           Logger.log(new LogEvent(LOGID, "STS: too many recent connection attempts, delaying " + delay));
/*     */           
/* 288 */           Thread.sleep(delay);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 294 */       last_incoming_sts_create = now;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericMessageEndpoint getEndpoint()
/*     */   {
/* 301 */     return this.connection.getEndpoint();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumMessageSize()
/*     */   {
/* 307 */     int max = this.connection.getMaximumMessageSize();
/*     */     
/* 309 */     if (this.outgoing_cipher != null)
/*     */     {
/* 311 */       max -= this.outgoing_cipher.getBlockSize();
/*     */     }
/*     */     
/* 314 */     return max;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 320 */     String con_type = this.connection.getType();
/*     */     
/* 322 */     if (con_type.length() == 0)
/*     */     {
/* 324 */       return "";
/*     */     }
/*     */     
/* 327 */     return "AES " + con_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransportType()
/*     */   {
/* 333 */     return this.connection.getTransportType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addInboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 340 */     this.connection.addInboundRateLimiter(limiter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeInboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 347 */     this.connection.removeInboundRateLimiter(limiter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addOutboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 354 */     this.connection.addOutboundRateLimiter(limiter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeOutboundRateLimiter(RateLimiter limiter)
/*     */   {
/* 361 */     this.connection.removeOutboundRateLimiter(limiter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws MessageException
/*     */   {
/* 369 */     if (this.connection.isIncoming())
/*     */     {
/* 371 */       this.connection.connect();
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 376 */         ByteBuffer buffer = ByteBuffer.allocate(32768);
/*     */         
/* 378 */         this.sts_engine.getKeys(buffer);
/*     */         
/* 380 */         buffer.flip();
/*     */         
/* 382 */         this.sent_keys = true;
/*     */         
/* 384 */         this.connection.connect(buffer);
/*     */       }
/*     */       catch (CryptoManagerException e)
/*     */       {
/* 388 */         throw new MessageException("Failed to get initial keys", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setFailed()
/*     */   {
/* 396 */     this.failed = true;
/*     */     try
/*     */     {
/* 399 */       cryptoComplete();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 403 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void receive(PooledByteBuffer message)
/*     */     throws MessageException
/*     */   {
/*     */     try
/*     */     {
/* 414 */       boolean forward = false;
/* 415 */       boolean crypto_completed = false;
/*     */       
/* 417 */       ByteBuffer out_buffer = null;
/*     */       
/* 419 */       synchronized (this)
/*     */       {
/* 421 */         if (this.crypto_complete.isReleasedForever())
/*     */         {
/* 423 */           forward = true;
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
/*     */         }
/*     */         else
/*     */         {
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
/* 447 */           ByteBuffer in_buffer = ByteBuffer.wrap(message.toByteArray());
/*     */           
/* 449 */           message.returnToPool();
/*     */           
/*     */ 
/*     */ 
/* 453 */           if (!this.sent_keys)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 460 */             out_buffer = ByteBuffer.allocate(65536);
/*     */             
/*     */ 
/*     */ 
/* 464 */             this.sts_engine.getKeys(out_buffer);
/*     */             
/* 466 */             this.sent_keys = true;
/*     */             
/*     */ 
/*     */ 
/* 470 */             this.sts_engine.putKeys(in_buffer);
/*     */             
/*     */ 
/*     */ 
/* 474 */             this.sts_engine.getAuth(out_buffer);
/*     */             
/* 476 */             this.sent_auth = true;
/*     */           }
/* 478 */           else if (!this.sent_auth)
/*     */           {
/* 480 */             out_buffer = ByteBuffer.allocate(65536);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 489 */             this.sts_engine.putKeys(in_buffer);
/*     */             
/*     */ 
/*     */ 
/* 493 */             this.sts_engine.getAuth(out_buffer);
/*     */             
/* 495 */             this.sent_auth = true;
/*     */             
/*     */ 
/*     */ 
/* 499 */             this.sts_engine.putAuth(in_buffer);
/*     */             
/*     */ 
/*     */ 
/* 503 */             byte[] rem_key = this.sts_engine.getRemotePublicKey();
/*     */             
/* 505 */             if (!this.key_locator.accept(this, new SEPublicKeyImpl(this.my_public_key.getType(), rem_key)))
/*     */             {
/*     */ 
/*     */ 
/* 509 */               throw new MessageException("remote public key not accepted");
/*     */             }
/*     */             
/* 512 */             setupBlockCrypto();
/*     */             
/* 514 */             if (this.pending_message != null)
/*     */             {
/* 516 */               byte[] pending_bytes = this.pending_message.toByteArray();
/*     */               
/* 518 */               int pending_size = pending_bytes.length;
/*     */               
/* 520 */               if (this.outgoing_cipher != null)
/*     */               {
/* 522 */                 pending_size = (pending_size + this.AES_KEY_SIZE_BYTES - 1) / this.AES_KEY_SIZE_BYTES * this.AES_KEY_SIZE_BYTES;
/*     */                 
/* 524 */                 if (pending_size == 0)
/*     */                 {
/* 526 */                   pending_size = this.AES_KEY_SIZE_BYTES;
/*     */                 }
/*     */               }
/*     */               
/* 530 */               if (out_buffer.remaining() >= pending_size)
/*     */               {
/* 532 */                 if (this.outgoing_cipher != null)
/*     */                 {
/*     */ 
/* 535 */                   out_buffer.put(this.outgoing_cipher.doFinal(pending_bytes));
/*     */                 }
/*     */                 else
/*     */                 {
/* 539 */                   out_buffer.put(pending_bytes);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 544 */                 this.pending_message = null;
/*     */               }
/*     */             }
/*     */             
/* 548 */             crypto_completed = true;
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*     */ 
/* 556 */             this.sts_engine.putAuth(in_buffer);
/*     */             
/*     */ 
/*     */ 
/* 560 */             byte[] rem_key = this.sts_engine.getRemotePublicKey();
/*     */             
/* 562 */             if (!this.key_locator.accept(this, new SEPublicKeyImpl(this.my_public_key.getType(), rem_key)))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 568 */               this.connection.closing();
/*     */               
/* 570 */               throw new MessageException("remote public key not accepted");
/*     */             }
/*     */             
/* 573 */             setupBlockCrypto();
/*     */             
/* 575 */             crypto_completed = true;
/*     */             
/*     */ 
/*     */ 
/* 579 */             if (in_buffer.hasRemaining())
/*     */             {
/* 581 */               message = new PooledByteBufferImpl(new DirectByteBuffer(in_buffer.slice()));
/*     */               
/* 583 */               forward = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 589 */       if (out_buffer != null)
/*     */       {
/* 591 */         out_buffer.flip();
/*     */         
/* 593 */         this.connection.send(new PooledByteBufferImpl(new DirectByteBuffer(out_buffer)));
/*     */       }
/*     */       
/* 596 */       if (crypto_completed)
/*     */       {
/* 598 */         cryptoComplete();
/*     */       }
/* 600 */       if (forward)
/*     */       {
/* 602 */         receiveContent(message);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 606 */       reportFailed(e);
/*     */       
/* 608 */       if ((e instanceof MessageException))
/*     */       {
/* 610 */         throw ((MessageException)e);
/*     */       }
/*     */       
/*     */ 
/* 614 */       throw new MessageException("Receive failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setupBlockCrypto()
/*     */     throws MessageException
/*     */   {
/* 624 */     if (!this.failed)
/*     */     {
/* 626 */       if (this.block_crypto == 1)
/*     */       {
/* 628 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 632 */         byte[] shared_secret = this.sts_engine.getSharedSecret();
/*     */         
/* 634 */         SecretKeySpec secret_key_spec1 = new SecretKeySpec(shared_secret, 0, 16, "AES");
/* 635 */         SecretKeySpec secret_key_spec2 = new SecretKeySpec(shared_secret, 8, 16, "AES");
/*     */         
/* 637 */         AlgorithmParameterSpec param_spec1 = new IvParameterSpec(AES_IV1);
/* 638 */         AlgorithmParameterSpec param_spec2 = new IvParameterSpec(AES_IV2);
/*     */         
/* 640 */         Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
/* 641 */         Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*     */         
/* 643 */         if (this.connection.isIncoming())
/*     */         {
/* 645 */           cipher1.init(1, secret_key_spec1, param_spec1);
/* 646 */           cipher2.init(2, secret_key_spec2, param_spec2);
/*     */           
/* 648 */           this.incoming_cipher = cipher2;
/* 649 */           this.outgoing_cipher = cipher1;
/*     */         }
/*     */         else
/*     */         {
/* 653 */           cipher1.init(2, secret_key_spec1, param_spec1);
/* 654 */           cipher2.init(1, secret_key_spec2, param_spec2);
/*     */           
/* 656 */           this.incoming_cipher = cipher1;
/* 657 */           this.outgoing_cipher = cipher2;
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 662 */         throw new MessageException("Failed to setup block encryption", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void cryptoComplete()
/*     */     throws MessageException
/*     */   {
/* 672 */     this.crypto_complete.releaseForever();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(PooledByteBuffer message)
/*     */     throws MessageException
/*     */   {
/* 681 */     if (this.failed)
/*     */     {
/* 683 */       throw new MessageException("Connection failed");
/*     */     }
/*     */     try
/*     */     {
/* 687 */       if (this.crypto_complete.isReleasedForever())
/*     */       {
/* 689 */         sendContent(message);
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 696 */         synchronized (this)
/*     */         {
/* 698 */           if (this.pending_message == null)
/*     */           {
/* 700 */             this.pending_message = message;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 705 */       this.crypto_complete.reserve();
/*     */       
/*     */ 
/*     */ 
/* 709 */       boolean send_it = false;
/*     */       
/* 711 */       synchronized (this)
/*     */       {
/* 713 */         if (this.pending_message == message)
/*     */         {
/* 715 */           this.pending_message = null;
/*     */           
/* 717 */           send_it = true;
/*     */         }
/*     */       }
/*     */       
/* 721 */       if (send_it)
/*     */       {
/* 723 */         sendContent(message);
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 728 */       setFailed();
/*     */       
/* 730 */       if ((e instanceof MessageException))
/*     */       {
/* 732 */         throw ((MessageException)e);
/*     */       }
/*     */       
/*     */ 
/* 736 */       throw new MessageException("Send failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void sendContent(PooledByteBuffer message)
/*     */     throws MessageException
/*     */   {
/* 747 */     if (this.outgoing_cipher != null)
/*     */     {
/*     */       try {
/* 750 */         byte[] plain = message.toByteArray();
/* 751 */         byte[] enc = this.outgoing_cipher.doFinal(plain);
/*     */         
/* 753 */         PooledByteBuffer temp = new PooledByteBufferImpl(enc);
/*     */         try
/*     */         {
/* 756 */           this.connection.send(temp);
/*     */           
/*     */ 
/*     */ 
/* 760 */           message.returnToPool();
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/* 766 */           temp.returnToPool();
/*     */           
/* 768 */           throw e;
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 773 */         throw new MessageException("Failed to encrypt data", e);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 778 */       if (this.block_crypto != 1)
/*     */       {
/* 780 */         this.connection.close();
/*     */         
/* 782 */         throw new MessageException("Crypto isn't setup");
/*     */       }
/*     */       
/* 785 */       this.connection.send(message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void receiveContent(PooledByteBuffer message)
/*     */     throws MessageException
/*     */   {
/* 795 */     boolean buffer_handled = false;
/*     */     try
/*     */     {
/* 798 */       if (this.incoming_cipher != null) {
/*     */         try
/*     */         {
/* 801 */           byte[] enc = message.toByteArray();
/* 802 */           byte[] plain = this.incoming_cipher.doFinal(enc);
/*     */           
/* 804 */           PooledByteBuffer temp = new PooledByteBufferImpl(plain);
/*     */           
/* 806 */           message.returnToPool();
/*     */           
/* 808 */           buffer_handled = true;
/*     */           
/* 810 */           message = temp;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 814 */           throw new MessageException("Failed to decrypt data", e);
/*     */         }
/*     */         
/* 817 */       } else if (this.block_crypto != 1)
/*     */       {
/* 819 */         throw new MessageException("Crypto isn't setup");
/*     */       }
/*     */       
/* 822 */       List listeners_ref = this.listeners.getList();
/*     */       
/* 824 */       MessageException last_error = null;
/*     */       
/* 826 */       for (int i = 0; i < listeners_ref.size(); i++)
/*     */       {
/*     */         PooledByteBuffer message_to_deliver;
/*     */         PooledByteBuffer message_to_deliver;
/* 830 */         if (i == 0)
/*     */         {
/* 832 */           message_to_deliver = message;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 838 */           message_to_deliver = new PooledByteBufferImpl(message.toByteArray());
/*     */         }
/*     */         try
/*     */         {
/* 842 */           ((GenericMessageConnectionListener)listeners_ref.get(i)).receive(this, message_to_deliver);
/*     */           
/* 844 */           if (message_to_deliver == message)
/*     */           {
/* 846 */             buffer_handled = true;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 850 */           message_to_deliver.returnToPool();
/*     */           
/* 852 */           if (message_to_deliver == message)
/*     */           {
/* 854 */             buffer_handled = true;
/*     */           }
/*     */           
/* 857 */           if ((e instanceof MessageException))
/*     */           {
/* 859 */             last_error = (MessageException)e;
/*     */           }
/*     */           else
/*     */           {
/* 863 */             last_error = new MessageException("Failed to process message", e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 868 */       if (last_error != null)
/*     */       {
/* 870 */         throw last_error;
/*     */       }
/*     */     }
/*     */     finally {
/* 874 */       if (!buffer_handled)
/*     */       {
/* 876 */         message.returnToPool();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws MessageException
/*     */   {
/* 886 */     synchronized (connections)
/*     */     {
/* 888 */       connections.remove(this);
/*     */     }
/*     */     
/* 891 */     this.connection.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reportConnected()
/*     */   {
/* 901 */     new AEThread2("SESTSConnection:connected", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 906 */         List listeners_ref = SESTSConnectionImpl.this.listeners.getList();
/*     */         
/* 908 */         for (int i = 0; i < listeners_ref.size(); i++) {
/*     */           try
/*     */           {
/* 911 */             ((GenericMessageConnectionListener)listeners_ref.get(i)).connected(SESTSConnectionImpl.this);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 915 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reportFailed(final Throwable error)
/*     */   {
/* 927 */     setFailed();
/*     */     
/* 929 */     new AEThread2("SESTSConnection:failed", true)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 935 */           List listeners_ref = SESTSConnectionImpl.this.listeners.getList();
/*     */           
/* 937 */           for (int i = 0; i < listeners_ref.size(); i++) {
/*     */             try
/*     */             {
/* 940 */               ((GenericMessageConnectionListener)listeners_ref.get(i)).failed(SESTSConnectionImpl.this, error);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 944 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */           return;
/*     */         } finally {
/*     */           try {
/* 950 */             SESTSConnectionImpl.this.close();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 954 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(GenericMessageConnectionListener listener)
/*     */   {
/* 965 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(GenericMessageConnectionListener listener)
/*     */   {
/* 972 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/security/SESTSConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */