/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class ProtocolDecoderInitial
/*     */   extends ProtocolDecoder
/*     */ {
/*  39 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*     */   final ProtocolDecoderAdapter adapter;
/*     */   
/*     */   private TransportHelperFilter filter;
/*     */   
/*     */   final TransportHelper transport;
/*     */   
/*     */   private final byte[][] shared_secrets;
/*     */   
/*     */   final ByteBuffer initial_data;
/*     */   private ByteBuffer decode_buffer;
/*     */   private int decode_read;
/*  52 */   private long start_time = SystemTime.getCurrentTime();
/*     */   
/*     */   private ProtocolDecoderPHE phe_decoder;
/*     */   
/*  56 */   private long last_read_time = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean processing_complete;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProtocolDecoderInitial(TransportHelper _transport, byte[][] _shared_secrets, boolean _outgoing, ByteBuffer _initial_data, ProtocolDecoderAdapter _adapter)
/*     */     throws IOException
/*     */   {
/*  70 */     super(true);
/*     */     
/*  72 */     this.transport = _transport;
/*  73 */     this.shared_secrets = _shared_secrets;
/*  74 */     this.initial_data = _initial_data;
/*  75 */     this.adapter = _adapter;
/*     */     
/*  77 */     final TransportHelperFilterTransparent transparent_filter = new TransportHelperFilterTransparent(this.transport, false);
/*     */     
/*  79 */     this.filter = transparent_filter;
/*     */     
/*  81 */     if (_outgoing)
/*     */     {
/*  83 */       if (ProtocolDecoderPHE.isCryptoOK())
/*     */       {
/*  85 */         decodePHE(null);
/*     */       }
/*     */       else
/*     */       {
/*  89 */         throw new IOException("Crypto required but unavailable");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  94 */       this.decode_buffer = ByteBuffer.allocate(this.adapter.getMaximumPlainHeaderLength());
/*     */       
/*  96 */       this.transport.registerForReadSelects(new TransportHelper.selectListener()
/*     */       {
/*     */ 
/*     */         public boolean selectSuccess(TransportHelper helper, Object attachment)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 105 */             int len = helper.read(ProtocolDecoderInitial.this.decode_buffer);
/*     */             
/* 107 */             if (len < 0)
/*     */             {
/* 109 */               ProtocolDecoderInitial.this.failed(new IOException("end of stream on socket read: in=" + ProtocolDecoderInitial.this.decode_buffer.position()));
/*     */             }
/* 111 */             else if (len == 0)
/*     */             {
/* 113 */               return false;
/*     */             }
/*     */             
/* 116 */             ProtocolDecoderInitial.this.last_read_time = SystemTime.getCurrentTime();
/*     */             
/* 118 */             ProtocolDecoderInitial.access$212(ProtocolDecoderInitial.this, len);
/*     */             
/* 120 */             int match = ProtocolDecoderInitial.this.adapter.matchPlainHeader(ProtocolDecoderInitial.this.decode_buffer);
/*     */             
/* 122 */             if (match != 1)
/*     */             {
/* 124 */               helper.cancelReadSelects();
/*     */               
/* 126 */               if ((NetworkManager.REQUIRE_CRYPTO_HANDSHAKE) && (match == 2))
/*     */               {
/* 128 */                 InetSocketAddress isa = ProtocolDecoderInitial.this.transport.getAddress();
/*     */                 
/* 130 */                 if (NetworkManager.INCOMING_HANDSHAKE_FALLBACK_ALLOWED) {
/* 131 */                   if (Logger.isEnabled()) {
/* 132 */                     Logger.log(new LogEvent(ProtocolDecoderInitial.LOGID, "Incoming connection [" + isa + "] is not encrypted but has been accepted as fallback is enabled"));
/*     */                   }
/* 134 */                 } else if (AddressUtils.isLANLocalAddress(AddressUtils.getHostAddress(isa)) == 1) {
/* 135 */                   if (Logger.isEnabled()) {
/* 136 */                     Logger.log(new LogEvent(ProtocolDecoderInitial.LOGID, "Incoming connection [" + isa + "] is not encrypted but has been accepted as lan-local"));
/*     */                   }
/* 138 */                 } else if (AENetworkClassifier.categoriseAddress(isa) != "Public") {
/* 139 */                   if (Logger.isEnabled()) {
/* 140 */                     Logger.log(new LogEvent(ProtocolDecoderInitial.LOGID, "Incoming connection [" + isa + "] is not encrypted but has been accepted as not a public network"));
/*     */                   }
/*     */                 } else {
/* 143 */                   throw new IOException("Crypto required but incoming connection has none");
/*     */                 }
/*     */               }
/*     */               
/* 147 */               ProtocolDecoderInitial.this.decode_buffer.flip();
/*     */               
/* 149 */               transparent_filter.insertRead(ProtocolDecoderInitial.this.decode_buffer);
/*     */               
/* 151 */               ProtocolDecoderInitial.this.complete(ProtocolDecoderInitial.this.initial_data);
/*     */ 
/*     */ 
/*     */             }
/* 155 */             else if (!ProtocolDecoderInitial.this.decode_buffer.hasRemaining())
/*     */             {
/* 157 */               helper.cancelReadSelects();
/*     */               
/* 159 */               if (NetworkManager.INCOMING_CRYPTO_ALLOWED)
/*     */               {
/* 161 */                 ProtocolDecoderInitial.this.decode_buffer.flip();
/*     */                 
/* 163 */                 ProtocolDecoderInitial.this.decodePHE(ProtocolDecoderInitial.this.decode_buffer);
/*     */               }
/*     */               else
/*     */               {
/* 167 */                 if (Logger.isEnabled()) {
/* 168 */                   Logger.log(new LogEvent(ProtocolDecoderInitial.LOGID, "Incoming connection [" + ProtocolDecoderInitial.this.transport.getAddress() + "] encrypted but rejected as not permitted"));
/*     */                 }
/* 170 */                 throw new IOException("Incoming crypto connection not permitted");
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 175 */             return true;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 179 */             selectFailure(helper, attachment, e);
/*     */           }
/* 181 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void selectFailure(TransportHelper helper, Object attachment, Throwable msg)
/*     */         {
/* 191 */           helper.cancelReadSelects();
/*     */           
/* 193 */           ProtocolDecoderInitial.this.failed(msg); } }, this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void decodePHE(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 206 */     ProtocolDecoderAdapter phe_adapter = new ProtocolDecoderAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void decodeComplete(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*     */       {
/*     */ 
/*     */ 
/* 214 */         ProtocolDecoderInitial.this.filter = decoder.getFilter();
/*     */         
/* 216 */         ProtocolDecoderInitial.this.complete(remaining_initial_data);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void decodeFailed(ProtocolDecoder decoder, Throwable cause)
/*     */       {
/* 224 */         ProtocolDecoderInitial.this.failed(cause);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void gotSecret(byte[] session_secret)
/*     */       {
/* 231 */         ProtocolDecoderInitial.this.adapter.gotSecret(session_secret);
/*     */       }
/*     */       
/*     */ 
/*     */       public int getMaximumPlainHeaderLength()
/*     */       {
/* 237 */         throw new RuntimeException();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public int matchPlainHeader(ByteBuffer buffer)
/*     */       {
/* 244 */         throw new RuntimeException();
/*     */       }
/*     */       
/* 247 */     };
/* 248 */     this.phe_decoder = new ProtocolDecoderPHE(this.transport, this.shared_secrets, buffer, this.initial_data, phe_adapter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isComplete(long now)
/*     */   {
/* 255 */     if (this.transport == null)
/*     */     {
/*     */ 
/*     */ 
/* 259 */       return false;
/*     */     }
/*     */     
/* 262 */     if (!this.processing_complete)
/*     */     {
/* 264 */       if (this.start_time > now)
/*     */       {
/* 266 */         this.start_time = now;
/*     */       }
/*     */       
/* 269 */       if (this.last_read_time > now)
/*     */       {
/* 271 */         this.last_read_time = now;
/*     */       }
/*     */       
/* 274 */       if (this.phe_decoder != null)
/*     */       {
/* 276 */         this.last_read_time = this.phe_decoder.getLastReadTime();
/*     */       }
/*     */       
/*     */       long time;
/*     */       long timeout;
/*     */       long time;
/* 282 */       if (this.last_read_time == 0L)
/*     */       {
/* 284 */         long timeout = this.transport.getConnectTimeout();
/* 285 */         time = this.start_time;
/*     */       }
/*     */       else
/*     */       {
/* 289 */         timeout = this.transport.getReadTimeout();
/* 290 */         time = this.last_read_time;
/*     */       }
/*     */       
/* 293 */       if (now - time > timeout)
/*     */       {
/*     */         try {
/* 296 */           this.transport.cancelReadSelects();
/*     */           
/* 298 */           this.transport.cancelWriteSelects();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*     */ 
/* 304 */         String phe_str = "";
/*     */         
/* 306 */         if (this.phe_decoder != null)
/*     */         {
/* 308 */           phe_str = ", crypto: " + this.phe_decoder.getString();
/*     */         }
/*     */         
/* 311 */         if (Logger.isEnabled())
/*     */         {
/* 313 */           Logger.log(new LogEvent(LOGID, "Connection [" + this.transport.getAddress() + "] forcibly timed out after " + timeout / 1000L + "sec due to socket inactivity"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 318 */         failed(new Throwable("Protocol decode aborted: timed out after " + timeout / 1000L + "sec: " + this.decode_read + " bytes read" + phe_str));
/*     */       }
/*     */     }
/*     */     
/* 322 */     return this.processing_complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportHelperFilter getFilter()
/*     */   {
/* 328 */     return this.filter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void complete(ByteBuffer remaining_initial_data)
/*     */   {
/* 335 */     if (!this.processing_complete)
/*     */     {
/* 337 */       this.processing_complete = true;
/*     */       
/* 339 */       this.adapter.decodeComplete(this, remaining_initial_data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void failed(Throwable reason)
/*     */   {
/* 347 */     if (!this.processing_complete)
/*     */     {
/* 349 */       this.processing_complete = true;
/*     */       
/* 351 */       this.adapter.decodeFailed(this, reason);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ProtocolDecoderInitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */