/*      */ package com.aelitis.azureus.core.networkmanager.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.KeyPair;
/*      */ import java.security.KeyPairGenerator;
/*      */ import java.security.PublicKey;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import javax.crypto.KeyAgreement;
/*      */ import javax.crypto.interfaces.DHPublicKey;
/*      */ import javax.crypto.spec.DHParameterSpec;
/*      */ import javax.crypto.spec.DHPublicKeySpec;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ProtocolDecoderPHE
/*      */   extends ProtocolDecoder
/*      */ {
/*   63 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*      */   
/*      */   private static final byte CRYPTO_PLAIN = 1;
/*      */   
/*      */   private static final byte CRYPTO_RC4 = 2;
/*      */   
/*      */   private static final byte CRYPTO_XOR = 4;
/*      */   
/*      */   private static final byte CRYPTO_AES = 8;
/*      */   
/*      */   private static final String DH_P = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563";
/*      */   
/*      */   private static final String DH_G = "02";
/*      */   
/*      */   private static final int DH_L = 160;
/*      */   
/*   79 */   private static final int DH_SIZE_BYTES = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563".length() / 2;
/*      */   
/*   81 */   public static final int MIN_INCOMING_INITIAL_PACKET_SIZE = DH_SIZE_BYTES;
/*      */   
/*      */ 
/*   84 */   private static final BigInteger DH_P_BI = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563", 16);
/*   85 */   private static final BigInteger DH_G_BI = new BigInteger("02", 16);
/*      */   
/*      */   private static KeyPairGenerator dh_key_generator;
/*      */   
/*      */   private static long last_dh_incoming_key_generate;
/*      */   private static final int BLOOM_RECREATE = 30000;
/*      */   private static final int BLOOM_INCREASE = 1000;
/*   92 */   private static BloomFilter generate_bloom = BloomFilterFactory.createAddRemove4Bit(1000);
/*   93 */   private static long generate_bloom_create_time = SystemTime.getCurrentTime();
/*      */   
/*      */ 
/*      */   private static boolean crypto_setup_done;
/*      */   
/*      */ 
/*      */   private static boolean crypto_ok;
/*      */   
/*      */ 
/*      */   private static final String RC4_STREAM_ALG = "RC4";
/*      */   
/*      */ 
/*      */   private static final String RC4_STREAM_CIPHER = "RC4";
/*      */   
/*      */ 
/*      */   private static final int RC4_STREAM_KEY_SIZE = 128;
/*      */   
/*      */ 
/*      */   private static final int RC4_STREAM_KEY_SIZE_BYTES = 16;
/*      */   
/*      */ 
/*      */   private static final int PADDING_MAX = 512;
/*      */   
/*      */ 
/*      */   private static final int PADDING_MAX_NORMAL = 512;
/*      */   
/*      */ 
/*      */   private static final int PADDING_MAX_LIMITED = 128;
/*      */   
/*      */ 
/*      */ 
/*      */   public static int getMaxIncomingInitialPacketSize(boolean min_overheads)
/*      */   {
/*  126 */     return MIN_INCOMING_INITIAL_PACKET_SIZE + (min_overheads ? '' : 'Ȁ') / 2;
/*      */   }
/*      */   
/*  129 */   private static final Random random = RandomUtils.SECURE_RANDOM;
/*      */   
/*  131 */   private static final Map global_shared_secrets = new LightHashMap();
/*      */   private static final byte SUPPORTED_PROTOCOLS = 3;
/*      */   private static byte MIN_CRYPTO;
/*      */   
/*      */   private static boolean cryptoSetup() {
/*  136 */     synchronized (global_shared_secrets)
/*      */     {
/*  138 */       if (crypto_setup_done)
/*      */       {
/*  140 */         return crypto_ok;
/*      */       }
/*      */       
/*  143 */       crypto_setup_done = true;
/*      */       try
/*      */       {
/*  146 */         DHParameterSpec dh_param_spec = new DHParameterSpec(DH_P_BI, DH_G_BI, 160);
/*      */         
/*  148 */         dh_key_generator = KeyPairGenerator.getInstance("DH");
/*      */         
/*  150 */         dh_key_generator.initialize(dh_param_spec);
/*      */         
/*  152 */         dh_key_generator.generateKeyPair();
/*      */         
/*  154 */         byte[] rc4_test_secret = new byte[16];
/*      */         
/*  156 */         SecretKeySpec rc4_test_secret_key_spec = new SecretKeySpec(rc4_test_secret, 0, 16, "RC4");
/*      */         
/*  158 */         TransportCipher rc4_cipher = new TransportCipher("RC4", 1, rc4_test_secret_key_spec);
/*      */         
/*  160 */         rc4_cipher = new TransportCipher("RC4", 2, rc4_test_secret_key_spec);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  182 */         crypto_ok = true;
/*      */         
/*  184 */         if (Logger.isEnabled())
/*      */         {
/*  186 */           Logger.log(new LogEvent(LOGID, "PHE crypto initialised"));
/*      */         }
/*      */         
/*      */       }
/*      */       catch (NoClassDefFoundError e)
/*      */       {
/*  192 */         Logger.log(new LogEvent(LOGID, "PHE crypto disabled as classes unavailable"));
/*      */         
/*  194 */         crypto_ok = false;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  198 */         Logger.log(new LogEvent(LOGID, "PHE crypto initialisation failed", e));
/*      */         
/*  200 */         crypto_ok = false;
/*      */       }
/*      */       
/*  203 */       return crypto_ok;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isCryptoOK()
/*      */   {
/*  210 */     return cryptoSetup();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void addSecretsSupport(byte[][] secrets)
/*      */   {
/*  217 */     for (int i = 0; i < secrets.length; i++)
/*      */     {
/*  219 */       SHA1Hasher hasher = new SHA1Hasher();
/*      */       
/*  221 */       hasher.update(REQ2_IV);
/*  222 */       hasher.update(secrets[i]);
/*      */       
/*  224 */       byte[] encoded = hasher.getDigest();
/*      */       
/*  226 */       synchronized (global_shared_secrets)
/*      */       {
/*  228 */         global_shared_secrets.put(new HashWrapper(encoded), secrets[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeSecretsSupport(byte[][] secrets)
/*      */   {
/*  237 */     for (int i = 0; i < secrets.length; i++)
/*      */     {
/*  239 */       SHA1Hasher hasher = new SHA1Hasher();
/*      */       
/*  241 */       hasher.update(REQ2_IV);
/*  242 */       hasher.update(secrets[i]);
/*      */       
/*  244 */       byte[] encoded = hasher.getDigest();
/*      */       
/*  246 */       synchronized (global_shared_secrets)
/*      */       {
/*  248 */         global_shared_secrets.remove(new HashWrapper(encoded));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int PS_OUTBOUND_1 = 0;
/*      */   
/*      */   private static final int PS_OUTBOUND_2 = 1;
/*      */   private static final int PS_OUTBOUND_3 = 2;
/*      */   static
/*      */   {
/*  260 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.transport.encrypted.min_level" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String ignore)
/*      */       {
/*      */ 
/*      */ 
/*  268 */         if ((NetworkManager.REQUIRE_CRYPTO_HANDSHAKE) && (!ProtocolDecoderPHE.isCryptoOK())) {
/*  269 */           Logger.log(new LogAlert(true, 3, "Connection encryption unavailable, please update your Java version"));
/*      */         }
/*      */         
/*  272 */         String min = COConfigurationManager.getStringParameter("network.transport.encrypted.min_level");
/*      */         
/*  274 */         if (min.equals("XOR"))
/*      */         {
/*  276 */           ProtocolDecoderPHE.access$002((byte)14);
/*      */         }
/*  278 */         else if (min.equals("RC4"))
/*      */         {
/*  280 */           ProtocolDecoderPHE.access$002((byte)10);
/*      */         }
/*  282 */         else if (min.equals("AES"))
/*      */         {
/*  284 */           ProtocolDecoderPHE.access$002((byte)8);
/*      */         }
/*      */         else
/*      */         {
/*  288 */           ProtocolDecoderPHE.access$002((byte)15);
/*      */         }
/*      */         
/*  291 */         ProtocolDecoderPHE.access$002((byte)(ProtocolDecoderPHE.MIN_CRYPTO & 0x3));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int PS_OUTBOUND_4 = 3;
/*      */   
/*      */   private static final int PS_INBOUND_1 = 10;
/*      */   
/*      */   private static final int PS_INBOUND_2 = 11;
/*      */   
/*      */   private static final int PS_INBOUND_3 = 12;
/*      */   
/*      */   private static final int PS_INBOUND_4 = 13;
/*      */   
/*  307 */   public static final byte[] KEYA_IV = "keyA".getBytes();
/*  308 */   public static final byte[] KEYB_IV = "keyB".getBytes();
/*  309 */   public static final byte[] REQ1_IV = "req1".getBytes();
/*  310 */   public static final byte[] REQ2_IV = "req2".getBytes();
/*  311 */   public static final byte[] REQ3_IV = "req3".getBytes();
/*  312 */   public static final byte[] VC = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */   
/*      */   private TransportHelper transport;
/*      */   
/*      */   private ByteBuffer write_buffer;
/*      */   
/*      */   private ByteBuffer read_buffer;
/*      */   
/*      */   private ProtocolDecoderAdapter adapter;
/*      */   
/*      */   private KeyAgreement key_agreement;
/*      */   
/*      */   private byte[] dh_public_key_bytes;
/*      */   
/*      */   private byte[] shared_secret;
/*      */   
/*      */   private byte[] secret_bytes;
/*      */   
/*      */   private ByteBuffer initial_data_out;
/*      */   
/*      */   private ByteBuffer initial_data_in;
/*      */   
/*      */   private TransportCipher write_cipher;
/*      */   
/*      */   private TransportCipher read_cipher;
/*      */   
/*      */   private byte[] padding_skip_marker;
/*      */   
/*      */   private byte my_supported_protocols;
/*      */   
/*      */   private byte selected_protocol;
/*      */   
/*      */   private boolean outbound;
/*      */   private int protocol_state;
/*      */   private int protocol_substate;
/*      */   private boolean handshake_complete;
/*      */   private int bytes_read;
/*      */   private int bytes_written;
/*  350 */   private long last_read_time = SystemTime.getCurrentTime();
/*      */   
/*      */   private TransportHelperFilter filter;
/*      */   
/*      */   private boolean delay_outbound_4;
/*      */   
/*      */   private boolean processing_complete;
/*      */   
/*  358 */   private final AEMonitor process_mon = new AEMonitor("ProtocolDecoderPHE:process");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ProtocolDecoderPHE(TransportHelper _transport, byte[][] _shared_secrets, ByteBuffer _header, ByteBuffer _initial_data, ProtocolDecoderAdapter _adapter)
/*      */     throws IOException
/*      */   {
/*  370 */     super(false);
/*      */     
/*  372 */     if (!isCryptoOK())
/*      */     {
/*  374 */       throw new IOException("PHE crypto broken");
/*      */     }
/*      */     
/*  377 */     this.transport = _transport;
/*      */     
/*  379 */     this.transport.setScatteringMode(768 + random.nextInt(256));
/*  380 */     this.initial_data_out = _initial_data;
/*  381 */     this.adapter = _adapter;
/*      */     
/*  383 */     if ((_shared_secrets == null) || (_shared_secrets.length == 0))
/*      */     {
/*  385 */       this.shared_secret = new byte[0];
/*      */ 
/*      */ 
/*      */     }
/*  389 */     else if (_shared_secrets.length == 1)
/*      */     {
/*  391 */       this.shared_secret = _shared_secrets[0];
/*      */     }
/*      */     else
/*      */     {
/*  395 */       this.shared_secret = _shared_secrets[random.nextInt(_shared_secrets.length)];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  401 */     this.outbound = (_header == null);
/*      */     
/*  403 */     this.my_supported_protocols = 3;
/*      */     
/*  405 */     if (this.outbound)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  413 */       this.my_supported_protocols = MIN_CRYPTO;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  419 */     else if (NetworkManager.REQUIRE_CRYPTO_HANDSHAKE)
/*      */     {
/*  421 */       this.my_supported_protocols = MIN_CRYPTO;
/*      */     }
/*      */     
/*      */ 
/*  425 */     initCrypto();
/*      */     try
/*      */     {
/*  428 */       this.process_mon.enter();
/*      */       
/*  430 */       this.transport.registerForReadSelects(new TransportHelper.selectListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean selectSuccess(TransportHelper helper, Object attachment)
/*      */         {
/*      */ 
/*      */ 
/*  438 */           return ProtocolDecoderPHE.this.selectSuccess(helper, attachment, false);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  447 */         public void selectFailure(TransportHelper helper, Object attachment, Throwable msg) { ProtocolDecoderPHE.this.selectFailure(helper, attachment, msg); } }, null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  452 */       this.transport.registerForWriteSelects(new TransportHelper.selectListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean selectSuccess(TransportHelper helper, Object attachment)
/*      */         {
/*      */ 
/*      */ 
/*  460 */           return ProtocolDecoderPHE.this.selectSuccess(helper, attachment, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  469 */         public void selectFailure(TransportHelper helper, Object attachment, Throwable msg) { ProtocolDecoderPHE.this.selectFailure(helper, attachment, msg); } }, null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  474 */       this.transport.pauseWriteSelects();
/*      */       
/*  476 */       if (this.outbound)
/*      */       {
/*  478 */         this.protocol_state = 0;
/*      */         
/*  480 */         this.transport.pauseReadSelects();
/*      */       }
/*      */       else
/*      */       {
/*  484 */         this.protocol_state = 10;
/*      */         
/*  486 */         this.read_buffer = ByteBuffer.allocate(this.dh_public_key_bytes.length);
/*      */         
/*  488 */         this.read_buffer.put(_header);
/*      */         
/*  490 */         this.bytes_read += _header.limit();
/*      */       }
/*      */     }
/*      */     finally {
/*  494 */       this.process_mon.exit();
/*      */     }
/*      */     
/*  497 */     process();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initCrypto()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  506 */       KeyPair key_pair = generateDHKeyPair(this.transport, this.outbound);
/*      */       
/*  508 */       this.key_agreement = KeyAgreement.getInstance("DH");
/*      */       
/*  510 */       this.key_agreement.init(key_pair.getPrivate());
/*      */       
/*  512 */       DHPublicKey dh_public_key = (DHPublicKey)key_pair.getPublic();
/*      */       
/*  514 */       BigInteger dh_y = dh_public_key.getY();
/*      */       
/*  516 */       this.dh_public_key_bytes = bigIntegerToBytes(dh_y, DH_SIZE_BYTES);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  520 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void completeDH(byte[] buffer)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  531 */       BigInteger other_dh_y = bytesToBigInteger(buffer, 0, DH_SIZE_BYTES);
/*      */       
/*  533 */       KeyFactory dh_key_factory = KeyFactory.getInstance("DH");
/*      */       
/*  535 */       PublicKey other_public_key = dh_key_factory.generatePublic(new DHPublicKeySpec(other_dh_y, DH_P_BI, DH_G_BI));
/*      */       
/*  537 */       this.key_agreement.doPhase(other_public_key, true);
/*      */       
/*  539 */       this.secret_bytes = this.key_agreement.generateSecret();
/*      */       
/*  541 */       this.adapter.gotSecret(this.secret_bytes);
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*  547 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setupCrypto()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  560 */       SHA1Hasher hasher = new SHA1Hasher();
/*      */       
/*  562 */       hasher.update(KEYA_IV);
/*  563 */       hasher.update(this.secret_bytes);
/*  564 */       hasher.update(this.shared_secret);
/*      */       
/*  566 */       byte[] a_key = hasher.getDigest();
/*      */       
/*  568 */       hasher = new SHA1Hasher();
/*      */       
/*  570 */       hasher.update(KEYB_IV);
/*  571 */       hasher.update(this.secret_bytes);
/*  572 */       hasher.update(this.shared_secret);
/*      */       
/*  574 */       byte[] b_key = hasher.getDigest();
/*      */       
/*  576 */       SecretKeySpec secret_key_spec_a = new SecretKeySpec(a_key, "RC4");
/*      */       
/*  578 */       SecretKeySpec secret_key_spec_b = new SecretKeySpec(b_key, "RC4");
/*      */       
/*  580 */       this.write_cipher = new TransportCipher("RC4", 1, this.outbound ? secret_key_spec_a : secret_key_spec_b);
/*      */       
/*  582 */       this.read_cipher = new TransportCipher("RC4", 2, this.outbound ? secret_key_spec_b : secret_key_spec_a);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  586 */       e.printStackTrace();
/*      */       
/*  588 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handshakeComplete()
/*      */     throws IOException
/*      */   {
/*  638 */     if (this.selected_protocol == 1)
/*      */     {
/*  640 */       this.filter = new TransportHelperFilterTransparent(this.transport, true);
/*      */     }
/*  642 */     else if (this.selected_protocol == 4)
/*      */     {
/*  644 */       this.filter = new TransportHelperFilterStreamXOR(this.transport, this.secret_bytes);
/*      */     }
/*  646 */     else if (this.selected_protocol == 2)
/*      */     {
/*  648 */       this.filter = new TransportHelperFilterStreamCipher(this.transport, this.read_cipher, this.write_cipher);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  679 */       throw new IOException("Invalid selected protocol '" + this.selected_protocol + "'");
/*      */     }
/*      */     
/*  682 */     if (this.initial_data_in != null)
/*      */     {
/*  684 */       this.filter = new TransportHelperFilterInserter(this.filter, this.initial_data_in);
/*      */     }
/*      */     
/*  687 */     this.handshake_complete = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void process()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  716 */       this.process_mon.enter();
/*      */       
/*  718 */       if (this.handshake_complete)
/*      */       {
/*  720 */         Debug.out("Handshake process already completed");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  725 */         boolean loop = true;
/*      */         
/*  727 */         while (loop)
/*      */         {
/*      */ 
/*      */ 
/*  731 */           if (this.protocol_state == 0)
/*      */           {
/*  733 */             if (this.write_buffer == null)
/*      */             {
/*      */ 
/*      */ 
/*  737 */               byte[] padding_a = getRandomPadding(getPaddingMax() / 2);
/*      */               
/*  739 */               this.write_buffer = ByteBuffer.allocate(this.dh_public_key_bytes.length + padding_a.length);
/*      */               
/*  741 */               this.write_buffer.put(this.dh_public_key_bytes);
/*      */               
/*  743 */               this.write_buffer.put(padding_a);
/*      */               
/*  745 */               this.write_buffer.flip();
/*      */             }
/*      */             
/*  748 */             write(this.write_buffer);
/*      */             
/*  750 */             if (!this.write_buffer.hasRemaining())
/*      */             {
/*  752 */               this.write_buffer = null;
/*      */               
/*  754 */               this.protocol_state = 11;
/*      */             }
/*      */           }
/*  757 */           else if (this.protocol_state == 10)
/*      */           {
/*      */ 
/*      */ 
/*  761 */             read(this.read_buffer);
/*      */             
/*  763 */             if (!this.read_buffer.hasRemaining())
/*      */             {
/*  765 */               this.read_buffer.flip();
/*      */               
/*  767 */               byte[] other_dh_public_key_bytes = new byte[this.read_buffer.remaining()];
/*      */               
/*  769 */               this.read_buffer.get(other_dh_public_key_bytes);
/*      */               
/*  771 */               completeDH(other_dh_public_key_bytes);
/*      */               
/*  773 */               this.read_buffer = null;
/*      */               
/*  775 */               this.protocol_state = 1;
/*      */             }
/*      */           }
/*  778 */           else if (this.protocol_state == 1)
/*      */           {
/*      */ 
/*      */ 
/*  782 */             if (this.write_buffer == null)
/*      */             {
/*  784 */               byte[] padding_b = getRandomPadding(getPaddingMax() / 2);
/*      */               
/*  786 */               this.write_buffer = ByteBuffer.allocate(this.dh_public_key_bytes.length + padding_b.length);
/*      */               
/*  788 */               this.write_buffer.put(this.dh_public_key_bytes);
/*      */               
/*  790 */               this.write_buffer.put(padding_b);
/*      */               
/*  792 */               this.write_buffer.flip();
/*      */             }
/*      */             
/*  795 */             write(this.write_buffer);
/*      */             
/*  797 */             if (!this.write_buffer.hasRemaining())
/*      */             {
/*  799 */               this.write_buffer = null;
/*      */               
/*  801 */               this.protocol_state = 12;
/*      */             }
/*      */           }
/*  804 */           else if (this.protocol_state == 11)
/*      */           {
/*      */ 
/*      */ 
/*  808 */             if (this.read_buffer == null)
/*      */             {
/*  810 */               this.read_buffer = ByteBuffer.allocate(this.dh_public_key_bytes.length);
/*      */             }
/*      */             
/*  813 */             read(this.read_buffer);
/*      */             
/*  815 */             if (!this.read_buffer.hasRemaining())
/*      */             {
/*  817 */               this.read_buffer.flip();
/*      */               
/*  819 */               byte[] other_dh_public_key_bytes = new byte[this.read_buffer.remaining()];
/*      */               
/*  821 */               this.read_buffer.get(other_dh_public_key_bytes);
/*      */               
/*  823 */               completeDH(other_dh_public_key_bytes);
/*      */               
/*      */ 
/*      */ 
/*  827 */               setupCrypto();
/*      */               
/*  829 */               this.read_buffer = null;
/*      */               
/*  831 */               this.protocol_state = 2;
/*      */             }
/*      */           }
/*  834 */           else if (this.protocol_state == 2)
/*      */           {
/*      */ 
/*      */ 
/*  838 */             if (this.write_buffer == null)
/*      */             {
/*  840 */               int initial_data_out_len = this.initial_data_out == null ? 0 : this.initial_data_out.remaining();
/*      */               
/*      */ 
/*      */ 
/*  844 */               int pad_max = getPaddingMax();
/*      */               
/*  846 */               byte[] padding_a = getRandomPadding(pad_max / 2);
/*      */               
/*  848 */               byte[] padding_c = getZeroPadding(pad_max);
/*      */               
/*  850 */               this.write_buffer = ByteBuffer.allocate(padding_a.length + 20 + 20 + (VC.length + 4 + 2 + padding_c.length + 2) + initial_data_out_len);
/*      */               
/*  852 */               this.write_buffer.put(padding_a);
/*      */               
/*      */ 
/*      */ 
/*  856 */               SHA1Hasher hasher = new SHA1Hasher();
/*      */               
/*  858 */               hasher.update(REQ1_IV);
/*  859 */               hasher.update(this.secret_bytes);
/*      */               
/*  861 */               byte[] sha1 = hasher.getDigest();
/*      */               
/*  863 */               this.write_buffer.put(sha1);
/*      */               
/*      */ 
/*      */ 
/*  867 */               hasher = new SHA1Hasher();
/*      */               
/*  869 */               hasher.update(REQ2_IV);
/*  870 */               hasher.update(this.shared_secret);
/*      */               
/*  872 */               byte[] sha1_1 = hasher.getDigest();
/*      */               
/*  874 */               hasher = new SHA1Hasher();
/*      */               
/*  876 */               hasher.update(REQ3_IV);
/*  877 */               hasher.update(this.secret_bytes);
/*      */               
/*  879 */               byte[] sha1_2 = hasher.getDigest();
/*      */               
/*  881 */               for (int i = 0; i < sha1_1.length; i++)
/*      */               {
/*  883 */                 int tmp621_619 = i; byte[] tmp621_617 = sha1_1;tmp621_617[tmp621_619] = ((byte)(tmp621_617[tmp621_619] ^ sha1_2[i]));
/*      */               }
/*      */               
/*  886 */               this.write_buffer.put(sha1_1);
/*      */               
/*      */ 
/*      */ 
/*  890 */               this.write_buffer.put(this.write_cipher.update(VC));
/*      */               
/*  892 */               this.write_buffer.put(this.write_cipher.update(new byte[] { 0, 0, 0, this.my_supported_protocols }));
/*      */               
/*  894 */               this.write_buffer.put(this.write_cipher.update(new byte[] { (byte)(padding_c.length >> 8), (byte)padding_c.length }));
/*      */               
/*  896 */               this.write_buffer.put(this.write_cipher.update(padding_c));
/*      */               
/*  898 */               this.write_buffer.put(this.write_cipher.update(new byte[] { (byte)(initial_data_out_len >> 8), (byte)initial_data_out_len }));
/*      */               
/*  900 */               if (initial_data_out_len > 0)
/*      */               {
/*  902 */                 int save_pos = this.initial_data_out.position();
/*      */                 
/*  904 */                 this.write_cipher.update(this.initial_data_out, this.write_buffer);
/*      */                 
/*      */ 
/*      */ 
/*  908 */                 this.initial_data_out.position(save_pos);
/*      */                 
/*  910 */                 this.initial_data_out = null;
/*      */               }
/*      */               
/*  913 */               this.write_buffer.flip();
/*      */             }
/*      */             
/*  916 */             write(this.write_buffer);
/*      */             
/*  918 */             if (!this.write_buffer.hasRemaining())
/*      */             {
/*  920 */               this.write_buffer = null;
/*      */               
/*  922 */               this.protocol_state = 13;
/*      */             }
/*      */           }
/*  925 */           else if (this.protocol_state == 12)
/*      */           {
/*      */ 
/*      */ 
/*  929 */             if (this.read_buffer == null)
/*      */             {
/*  931 */               this.read_buffer = ByteBuffer.allocate(532);
/*      */               
/*  933 */               this.read_buffer.limit(20);
/*      */               
/*  935 */               SHA1Hasher hasher = new SHA1Hasher();
/*      */               
/*  937 */               hasher.update(REQ1_IV);
/*  938 */               hasher.update(this.secret_bytes);
/*      */               
/*  940 */               this.padding_skip_marker = hasher.getDigest();
/*      */               
/*  942 */               this.protocol_substate = 1;
/*      */             }
/*      */             do
/*      */             {
/*      */               for (;;) {
/*  947 */                 read(this.read_buffer);
/*      */                 
/*  949 */                 if (this.read_buffer.hasRemaining()) {
/*      */                   break label2469;
/*      */                 }
/*      */                 
/*      */ 
/*  954 */                 if (this.protocol_substate == 1)
/*      */                 {
/*      */ 
/*      */ 
/*  958 */                   int limit = this.read_buffer.limit();
/*      */                   
/*  960 */                   this.read_buffer.position(limit - 20);
/*      */                   
/*  962 */                   boolean match = true;
/*      */                   
/*  964 */                   for (int i = 0; i < 20; i++)
/*      */                   {
/*  966 */                     if (this.read_buffer.get() != this.padding_skip_marker[i])
/*      */                     {
/*  968 */                       match = false;
/*      */                       
/*  970 */                       break;
/*      */                     }
/*      */                   }
/*      */                   
/*  974 */                   if (match)
/*      */                   {
/*  976 */                     this.read_buffer = ByteBuffer.allocate(20 + VC.length + 4 + 2);
/*      */                     
/*  978 */                     this.protocol_substate = 2;
/*      */                     
/*      */ 
/*      */                     break label2469;
/*      */                   }
/*      */                   
/*  984 */                   if (limit == this.read_buffer.capacity())
/*      */                   {
/*  986 */                     throw new IOException("PHE skip to SHA1 marker failed");
/*      */                   }
/*      */                   
/*  989 */                   this.read_buffer.limit(limit + 1);
/*      */                   
/*  991 */                   this.read_buffer.position(limit);
/*      */                 }
/*  993 */                 else if (this.protocol_substate == 2)
/*      */                 {
/*      */ 
/*      */ 
/*  997 */                   this.read_buffer.flip();
/*      */                   
/*  999 */                   byte[] decode = new byte[20];
/*      */                   
/* 1001 */                   this.read_buffer.get(decode);
/*      */                   
/* 1003 */                   SHA1Hasher hasher = new SHA1Hasher();
/*      */                   
/* 1005 */                   hasher.update(REQ3_IV);
/* 1006 */                   hasher.update(this.secret_bytes);
/*      */                   
/* 1008 */                   byte[] sha1 = hasher.getDigest();
/*      */                   
/* 1010 */                   for (int i = 0; i < decode.length; i++)
/*      */                   {
/* 1012 */                     int tmp1175_1173 = i; byte[] tmp1175_1172 = decode;tmp1175_1172[tmp1175_1173] = ((byte)(tmp1175_1172[tmp1175_1173] ^ sha1[i]));
/*      */                   }
/*      */                   
/* 1015 */                   synchronized (global_shared_secrets)
/*      */                   {
/* 1017 */                     this.shared_secret = ((byte[])global_shared_secrets.get(new HashWrapper(decode)));
/*      */                   }
/*      */                   
/* 1020 */                   if (this.shared_secret == null)
/*      */                   {
/* 1022 */                     throw new IOException("No matching shared secret");
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 1027 */                   setupCrypto();
/*      */                   
/* 1029 */                   byte[] crypted = new byte[VC.length + 4 + 2];
/*      */                   
/* 1031 */                   this.read_buffer.get(crypted);
/*      */                   
/* 1033 */                   byte[] plain = this.read_cipher.update(crypted);
/*      */                   
/* 1035 */                   byte other_supported_protocols = plain[(VC.length + 3)];
/*      */                   
/* 1037 */                   int common_protocols = this.my_supported_protocols & other_supported_protocols;
/*      */                   
/* 1039 */                   if ((common_protocols & 0x1) != 0)
/*      */                   {
/* 1041 */                     this.selected_protocol = 1;
/*      */                   }
/* 1043 */                   else if ((common_protocols & 0x4) != 0)
/*      */                   {
/* 1045 */                     this.selected_protocol = 4;
/*      */                   }
/* 1047 */                   else if ((common_protocols & 0x2) != 0)
/*      */                   {
/* 1049 */                     this.selected_protocol = 2;
/*      */                   }
/* 1051 */                   else if ((common_protocols & 0x8) != 0)
/*      */                   {
/* 1053 */                     this.selected_protocol = 8;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1057 */                     throw new IOException("No crypto protocol in common: mine = " + Integer.toHexString(this.my_supported_protocols) + ", theirs = " + Integer.toHexString(other_supported_protocols));
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1064 */                   int padding = ((plain[(VC.length + 4)] & 0xFF) << 8) + (plain[(VC.length + 5)] & 0xFF);
/*      */                   
/* 1066 */                   if (padding > 512)
/*      */                   {
/* 1068 */                     throw new IOException("Invalid padding '" + padding + "'");
/*      */                   }
/*      */                   
/* 1071 */                   this.read_buffer = ByteBuffer.allocate(padding + 2);
/*      */                   
/*      */ 
/*      */ 
/* 1075 */                   this.protocol_substate = 3;
/*      */                 } else {
/* 1077 */                   if (this.protocol_substate != 3) {
/*      */                     break;
/*      */                   }
/*      */                   
/* 1081 */                   this.read_buffer.flip();
/*      */                   
/* 1083 */                   byte[] data = new byte[this.read_buffer.remaining()];
/*      */                   
/* 1085 */                   this.read_buffer.get(data);
/*      */                   
/* 1087 */                   data = this.read_cipher.update(data);
/*      */                   
/* 1089 */                   int ia_len = 0xFFFF & ((data[(data.length - 2)] & 0xFF) << 8) + (data[(data.length - 1)] & 0xFF);
/*      */                   
/* 1091 */                   if (ia_len > 65535)
/*      */                   {
/* 1093 */                     throw new IOException("Invalid IA length '" + ia_len + "'");
/*      */                   }
/*      */                   
/* 1096 */                   if (ia_len > 0)
/*      */                   {
/* 1098 */                     this.read_buffer = ByteBuffer.allocate(ia_len);
/*      */                     
/*      */ 
/*      */ 
/* 1102 */                     this.protocol_substate = 4;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1106 */                     this.read_buffer = null;
/*      */                     
/* 1108 */                     this.protocol_state = 3;
/*      */                     break label2469;
/*      */                   }
/*      */                 }
/* 1112 */               } } while (this.protocol_substate != 4);
/*      */             
/*      */ 
/*      */ 
/* 1116 */             this.read_buffer.flip();
/*      */             
/* 1118 */             byte[] data = new byte[this.read_buffer.remaining()];
/*      */             
/* 1120 */             this.read_buffer.get(data);
/*      */             
/* 1122 */             data = this.read_cipher.update(data);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1129 */             this.delay_outbound_4 = new String(data).contains("BitTorrent");
/*      */             
/*      */ 
/*      */ 
/* 1133 */             this.initial_data_in = ByteBuffer.wrap(data);
/*      */             
/* 1135 */             this.read_buffer = null;
/*      */             
/* 1137 */             this.protocol_state = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/* 1142 */           else if (this.protocol_state == 3)
/*      */           {
/*      */ 
/*      */ 
/* 1146 */             if (this.write_buffer == null)
/*      */             {
/* 1148 */               int pad_max = getPaddingMax();
/*      */               
/* 1150 */               byte[] padding_b = getRandomPadding(pad_max / 2);
/*      */               
/* 1152 */               byte[] padding_d = getZeroPadding(pad_max);
/*      */               
/* 1154 */               this.write_buffer = ByteBuffer.allocate(padding_b.length + VC.length + 4 + 2 + padding_d.length);
/*      */               
/* 1156 */               this.write_buffer.put(padding_b);
/*      */               
/* 1158 */               this.write_buffer.put(this.write_cipher.update(VC));
/*      */               
/* 1160 */               this.write_buffer.put(this.write_cipher.update(new byte[] { 0, 0, 0, this.selected_protocol }));
/*      */               
/* 1162 */               this.write_buffer.put(this.write_cipher.update(new byte[] { (byte)(padding_d.length >> 8), (byte)padding_d.length }));
/*      */               
/* 1164 */               this.write_buffer.put(this.write_cipher.update(padding_d));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1170 */               this.write_buffer.flip();
/*      */             }
/*      */             
/* 1173 */             if (this.delay_outbound_4)
/*      */             {
/* 1175 */               if (this.transport.delayWrite(this.write_buffer))
/*      */               {
/* 1177 */                 this.write_buffer = null;
/*      */                 
/* 1179 */                 handshakeComplete();
/*      */               }
/*      */               else
/*      */               {
/* 1183 */                 this.delay_outbound_4 = false;
/*      */               }
/*      */             }
/*      */             
/* 1187 */             if (!this.delay_outbound_4)
/*      */             {
/* 1189 */               write(this.write_buffer);
/*      */               
/* 1191 */               if (!this.write_buffer.hasRemaining())
/*      */               {
/* 1193 */                 this.write_buffer = null;
/*      */                 
/* 1195 */                 handshakeComplete();
/*      */               }
/*      */             }
/* 1198 */           } else if (this.protocol_state == 13)
/*      */           {
/*      */ 
/*      */ 
/* 1202 */             if (this.read_buffer == null)
/*      */             {
/* 1204 */               this.read_buffer = ByteBuffer.allocate(VC.length + 512);
/*      */               
/* 1206 */               this.read_buffer.limit(VC.length);
/*      */               
/* 1208 */               this.padding_skip_marker = new byte[VC.length];
/*      */               
/* 1210 */               this.padding_skip_marker = this.read_cipher.update(this.padding_skip_marker);
/*      */               
/* 1212 */               this.protocol_substate = 1;
/*      */             }
/*      */             do
/*      */             {
/*      */               for (;;) {
/* 1217 */                 read(this.read_buffer);
/*      */                 
/* 1219 */                 if (this.read_buffer.hasRemaining()) {
/*      */                   break label2469;
/*      */                 }
/*      */                 
/*      */ 
/* 1224 */                 if (this.protocol_substate == 1)
/*      */                 {
/*      */ 
/*      */ 
/* 1228 */                   int limit = this.read_buffer.limit();
/*      */                   
/* 1230 */                   this.read_buffer.position(limit - VC.length);
/*      */                   
/* 1232 */                   boolean match = true;
/*      */                   
/* 1234 */                   for (int i = 0; i < VC.length; i++)
/*      */                   {
/* 1236 */                     if (this.read_buffer.get() != this.padding_skip_marker[i])
/*      */                     {
/* 1238 */                       match = false;
/*      */                       
/* 1240 */                       break;
/*      */                     }
/*      */                   }
/*      */                   
/* 1244 */                   if (match)
/*      */                   {
/* 1246 */                     this.read_buffer = ByteBuffer.allocate(6);
/*      */                     
/* 1248 */                     this.protocol_substate = 2;
/*      */                     
/*      */ 
/*      */                     break label2469;
/*      */                   }
/*      */                   
/* 1254 */                   if (limit == this.read_buffer.capacity())
/*      */                   {
/* 1256 */                     throw new IOException("PHE skip to SHA1 marker failed");
/*      */                   }
/*      */                   
/* 1259 */                   this.read_buffer.limit(limit + 1);
/*      */                   
/* 1261 */                   this.read_buffer.position(limit);
/*      */                 } else {
/* 1263 */                   if (this.protocol_substate != 2) {
/*      */                     break;
/*      */                   }
/*      */                   
/* 1267 */                   this.read_buffer.flip();
/*      */                   
/* 1269 */                   byte[] crypted = new byte[6];
/*      */                   
/* 1271 */                   this.read_buffer.get(crypted);
/*      */                   
/* 1273 */                   byte[] plain = this.read_cipher.update(crypted);
/*      */                   
/* 1275 */                   this.selected_protocol = plain[3];
/*      */                   
/* 1277 */                   if ((this.selected_protocol & this.my_supported_protocols) == 0)
/*      */                   {
/*      */ 
/* 1280 */                     throw new IOException("Selected protocol has nothing in common: mine = " + Integer.toHexString(this.my_supported_protocols) + ", theirs = " + Integer.toHexString(this.selected_protocol));
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1287 */                   int pad_len = 0xFFFF & ((plain[4] & 0xFF) << 8) + (plain[5] & 0xFF);
/*      */                   
/* 1289 */                   if (pad_len > 65535)
/*      */                   {
/* 1291 */                     throw new IOException("Invalid pad length '" + pad_len + "'");
/*      */                   }
/*      */                   
/* 1294 */                   this.read_buffer = ByteBuffer.allocate(pad_len);
/*      */                   
/* 1296 */                   this.protocol_substate = 3;
/*      */                 }
/* 1298 */               } } while (this.protocol_substate != 3);
/*      */             
/* 1300 */             this.read_buffer.flip();
/*      */             
/* 1302 */             byte[] data = new byte[this.read_buffer.remaining()];
/*      */             
/* 1304 */             this.read_buffer.get(data);
/*      */             
/* 1306 */             data = this.read_cipher.update(data);
/*      */             
/* 1308 */             handshakeComplete();
/*      */             
/* 1310 */             this.read_buffer = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           label2469:
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1345 */           if (this.handshake_complete)
/*      */           {
/* 1347 */             this.transport.cancelReadSelects();
/*      */             
/* 1349 */             this.transport.cancelWriteSelects();
/*      */             
/* 1351 */             loop = false;
/*      */             
/* 1353 */             complete();
/*      */           }
/*      */           else
/*      */           {
/* 1357 */             if (this.read_buffer == null)
/*      */             {
/* 1359 */               this.transport.pauseReadSelects();
/*      */             }
/*      */             else
/*      */             {
/* 1363 */               this.transport.resumeReadSelects();
/*      */               
/* 1365 */               loop = false;
/*      */             }
/*      */             
/*      */ 
/* 1369 */             if (this.write_buffer == null)
/*      */             {
/* 1371 */               this.transport.pauseWriteSelects();
/*      */             }
/*      */             else
/*      */             {
/* 1375 */               this.transport.resumeWriteSelects();
/*      */               
/* 1377 */               loop = false;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/* 1383 */       failed(e);
/*      */       
/* 1385 */       if ((e instanceof IOException))
/*      */       {
/* 1387 */         throw ((IOException)e);
/*      */       }
/*      */       
/*      */ 
/* 1391 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */     finally
/*      */     {
/* 1395 */       this.process_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void read(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1802 */     int len = this.transport.read(buffer);
/*      */     
/*      */ 
/*      */ 
/* 1806 */     if (len < 0)
/*      */     {
/* 1808 */       throw new IOException("end of stream on socket read - phe: " + getString());
/*      */     }
/*      */     
/* 1811 */     this.bytes_read += len;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void write(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1822 */     int len = this.transport.write(buffer, false);
/*      */     
/*      */ 
/*      */ 
/* 1826 */     if (len < 0)
/*      */     {
/* 1828 */       throw new IOException("bytes written < 0 ");
/*      */     }
/*      */     
/* 1831 */     this.bytes_written += len;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean selectSuccess(TransportHelper transport, Object attachment, boolean write_operation)
/*      */   {
/*      */     try
/*      */     {
/* 1841 */       int old_bytes_read = this.bytes_read;
/* 1842 */       int old_bytes_written = this.bytes_written;
/*      */       
/* 1844 */       process();
/*      */       
/* 1846 */       if (write_operation)
/*      */       {
/* 1848 */         return this.bytes_written != old_bytes_written;
/*      */       }
/*      */       
/*      */ 
/* 1852 */       boolean progress = this.bytes_read != old_bytes_read;
/*      */       
/* 1854 */       if (progress)
/*      */       {
/* 1856 */         this.last_read_time = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/* 1859 */       return progress;
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1864 */       failed(e);
/*      */     }
/* 1866 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void selectFailure(TransportHelper transport, Object attachment, Throwable msg)
/*      */   {
/* 1876 */     failed(msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] bigIntegerToBytes(BigInteger bi, int num_bytes)
/*      */   {
/* 1884 */     String str = bi.toString(16);
/*      */     
/* 1886 */     while (str.length() < num_bytes * 2) {
/* 1887 */       str = "0" + str;
/*      */     }
/*      */     
/* 1890 */     return ByteFormatter.decodeString(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BigInteger bytesToBigInteger(byte[] bytes, int offset, int len)
/*      */   {
/* 1899 */     return new BigInteger(ByteFormatter.encodeString(bytes, offset, len), 16);
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getPaddingMax()
/*      */   {
/* 1905 */     if (this.transport.minimiseOverheads())
/*      */     {
/* 1907 */       return 128;
/*      */     }
/*      */     
/*      */ 
/* 1911 */     return 512;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static synchronized byte[] getRandomPadding(int max_len)
/*      */   {
/* 1919 */     byte[] bytes = new byte[random.nextInt(max_len)];
/*      */     
/* 1921 */     random.nextBytes(bytes);
/*      */     
/* 1923 */     return bytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static synchronized byte[] getZeroPadding(int max_len)
/*      */   {
/* 1930 */     byte[] bytes = new byte[random.nextInt(max_len)];
/*      */     
/* 1932 */     return bytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static KeyPair generateDHKeyPair(TransportHelper transport, boolean outbound)
/*      */     throws IOException
/*      */   {
/* 1942 */     if (dh_key_generator == null)
/*      */     {
/* 1944 */       throw new IOException("Crypto not setup");
/*      */     }
/*      */     
/* 1947 */     synchronized (dh_key_generator)
/*      */     {
/* 1949 */       if (!outbound)
/*      */       {
/* 1951 */         InetSocketAddress is_address = transport.getAddress();
/*      */         
/* 1953 */         byte[] address = AddressUtils.getAddressBytes(is_address);
/*      */         
/* 1955 */         int hit_count = generate_bloom.add(address);
/*      */         
/* 1957 */         long now = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/*      */ 
/* 1961 */         if (generate_bloom.getSize() / generate_bloom.getEntryCount() < 10)
/*      */         {
/* 1963 */           generate_bloom = BloomFilterFactory.createAddRemove4Bit(generate_bloom.getSize() + 1000);
/*      */           
/* 1965 */           generate_bloom_create_time = now;
/*      */           
/* 1967 */           Logger.log(new LogEvent(LOGID, "PHE bloom: size increased to " + generate_bloom.getSize()));
/*      */         }
/* 1969 */         else if ((now < generate_bloom_create_time) || (now - generate_bloom_create_time > 30000L))
/*      */         {
/* 1971 */           generate_bloom = BloomFilterFactory.createAddRemove4Bit(generate_bloom.getSize());
/*      */           
/* 1973 */           generate_bloom_create_time = now;
/*      */         }
/*      */         
/* 1976 */         if (hit_count >= 15)
/*      */         {
/* 1978 */           Logger.log(new LogEvent(LOGID, "PHE bloom: too many recent connection attempts from " + transport.getAddress()));
/*      */           
/* 1980 */           throw new IOException("Too many recent connection attempts (phe)");
/*      */         }
/*      */         
/* 1983 */         long since_last = now - last_dh_incoming_key_generate;
/*      */         
/* 1985 */         long delay = 100L - since_last;
/*      */         
/*      */ 
/*      */ 
/* 1989 */         if ((delay > 0L) && (delay < 100L)) {
/*      */           try
/*      */           {
/* 1992 */             Thread.sleep(delay);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/* 1998 */         last_dh_incoming_key_generate = now;
/*      */       }
/*      */       
/* 2001 */       KeyPair res = dh_key_generator.generateKeyPair();
/*      */       
/* 2003 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void complete()
/*      */   {
/* 2012 */     this.processing_complete = true;
/*      */     
/* 2014 */     this.transport.setScatteringMode(0L);
/*      */     
/* 2016 */     this.adapter.decodeComplete(this, this.initial_data_out);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void failed(Throwable cause)
/*      */   {
/* 2025 */     this.processing_complete = true;
/*      */     
/* 2027 */     this.transport.cancelReadSelects();
/*      */     
/* 2029 */     this.transport.cancelWriteSelects();
/*      */     
/* 2031 */     this.adapter.decodeFailed(this, cause);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isComplete(long now)
/*      */   {
/* 2038 */     return this.processing_complete;
/*      */   }
/*      */   
/*      */ 
/*      */   public TransportHelperFilter getFilter()
/*      */   {
/* 2044 */     return this.filter;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastReadTime()
/*      */   {
/* 2050 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 2052 */     if (this.last_read_time > now)
/*      */     {
/* 2054 */       this.last_read_time = now;
/*      */     }
/*      */     
/* 2057 */     return this.last_read_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 2063 */     return "state=" + this.protocol_state + ",sub=" + this.protocol_substate + ",in=" + this.bytes_read + ",out=" + this.bytes_written;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ProtocolDecoderPHE.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */