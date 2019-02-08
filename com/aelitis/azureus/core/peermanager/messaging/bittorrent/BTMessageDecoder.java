/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ public class BTMessageDecoder
/*     */   implements MessageStreamDecoder
/*     */ {
/*     */   private static final int MIN_MESSAGE_LENGTH = 1;
/*     */   private static final int MAX_MESSAGE_LENGTH = 131072;
/*     */   private static final int HANDSHAKE_FAKE_LENGTH = 323119476;
/*     */   private static final byte SS = 11;
/*  43 */   private DirectByteBuffer payload_buffer = null;
/*  44 */   private final DirectByteBuffer length_buffer = DirectByteBufferPool.getBuffer((byte)12, 4);
/*  45 */   private final ByteBuffer[] decode_array = { null, this.length_buffer.getBuffer(11) };
/*     */   
/*     */ 
/*  48 */   private boolean reading_length_mode = true;
/*  49 */   private boolean reading_handshake_message = false;
/*     */   
/*     */   private int message_length;
/*     */   
/*     */   private int pre_read_start_buffer;
/*     */   private int pre_read_start_position;
/*  55 */   private boolean last_received_was_keepalive = false;
/*     */   
/*  57 */   private volatile boolean destroyed = false;
/*  58 */   private volatile boolean is_paused = false;
/*     */   
/*  60 */   private final ArrayList messages_last_read = new ArrayList();
/*  61 */   private int protocol_bytes_last_read = 0;
/*  62 */   private int data_bytes_last_read = 0;
/*  63 */   private int percent_complete = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int performStreamDecode(Transport transport, int max_bytes)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  74 */       this.protocol_bytes_last_read = 0;
/*  75 */       this.data_bytes_last_read = 0;
/*     */       
/*  77 */       int bytes_remaining = max_bytes;
/*     */       
/*  79 */       while (bytes_remaining > 0)
/*     */       {
/*  81 */         if (this.destroyed) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */         if (this.is_paused) {
/*     */           break;
/*     */         }
/*     */         
/*  93 */         int bytes_possible = preReadProcess(bytes_remaining);
/*     */         
/*  95 */         if (bytes_possible < 1) {
/*  96 */           Debug.out("ERROR BT: bytes_possible < 1");
/*  97 */           break;
/*     */         }
/*     */         
/* 100 */         if (this.reading_length_mode) {
/* 101 */           transport.read(this.decode_array, 1, 1);
/*     */         }
/*     */         else {
/* 104 */           transport.read(this.decode_array, 0, 2);
/*     */         }
/*     */         
/* 107 */         int bytes_read = postReadProcess();
/*     */         
/* 109 */         bytes_remaining -= bytes_read;
/*     */         
/* 111 */         if (bytes_read < bytes_possible) {
/*     */           break;
/*     */         }
/*     */         
/* 115 */         if ((this.reading_length_mode) && (this.last_received_was_keepalive))
/*     */         {
/*     */ 
/* 118 */           this.last_received_was_keepalive = false;
/* 119 */           break;
/*     */         }
/*     */       }
/*     */       
/* 123 */       return max_bytes - bytes_remaining;
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */ 
/* 130 */       throw new IOException("Decoder has most likely been destroyed");
/*     */     }
/*     */   }
/*     */   
/*     */   public int getPercentDoneOfCurrentMessage()
/*     */   {
/* 136 */     return this.percent_complete;
/*     */   }
/*     */   
/*     */   public Message[] removeDecodedMessages()
/*     */   {
/* 141 */     if (this.messages_last_read.isEmpty()) { return null;
/*     */     }
/* 143 */     Message[] msgs = (Message[])this.messages_last_read.toArray(new Message[this.messages_last_read.size()]);
/*     */     
/* 145 */     this.messages_last_read.clear();
/*     */     
/* 147 */     return msgs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 152 */   public int getProtocolBytesDecoded() { return this.protocol_bytes_last_read; }
/* 153 */   public int getDataBytesDecoded() { return this.data_bytes_last_read; }
/*     */   
/*     */ 
/*     */   public ByteBuffer destroy()
/*     */   {
/* 158 */     if (this.destroyed) {
/* 159 */       Debug.out("Trying to redestroy message decoder, stack trace follows: " + this);
/* 160 */       Debug.outStackTrace();
/*     */     }
/*     */     
/* 163 */     this.is_paused = true;
/* 164 */     this.destroyed = true;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */     int lbuff_read = 0;
/* 171 */     int pbuff_read = 0;
/* 172 */     this.length_buffer.limit((byte)11, 4);
/*     */     
/* 174 */     DirectByteBuffer plb = this.payload_buffer;
/*     */     
/* 176 */     if (this.reading_length_mode) {
/* 177 */       lbuff_read = this.length_buffer.position((byte)11);
/*     */     }
/*     */     else {
/* 180 */       this.length_buffer.position((byte)11, 4);
/* 181 */       lbuff_read = 4;
/* 182 */       pbuff_read = plb == null ? 0 : plb.position((byte)11);
/*     */     }
/*     */     
/* 185 */     ByteBuffer unused = ByteBuffer.allocate(lbuff_read + pbuff_read);
/*     */     
/* 187 */     this.length_buffer.flip((byte)11);
/* 188 */     unused.put(this.length_buffer.getBuffer((byte)11));
/*     */     try
/*     */     {
/* 191 */       if (plb != null) {
/* 192 */         plb.flip((byte)11);
/* 193 */         unused.put(plb.getBuffer((byte)11));
/*     */       }
/*     */     } catch (RuntimeException e) {
/* 196 */       Debug.out("hit known threading issue");
/*     */     }
/*     */     
/* 199 */     unused.flip();
/*     */     
/* 201 */     this.length_buffer.returnToPool();
/*     */     
/* 203 */     if (plb != null) {
/* 204 */       plb.returnToPool();
/* 205 */       this.payload_buffer = null;
/*     */     }
/*     */     try
/*     */     {
/* 209 */       for (int i = 0; i < this.messages_last_read.size(); i++) {
/* 210 */         Message msg = (Message)this.messages_last_read.get(i);
/* 211 */         msg.destroy();
/*     */       }
/*     */     }
/*     */     catch (RuntimeException e) {
/* 215 */       Debug.out("hit known threading issue");
/*     */     }
/* 217 */     this.messages_last_read.clear();
/*     */     
/* 219 */     return unused;
/*     */   }
/*     */   
/*     */ 
/*     */   private int preReadProcess(int allowed)
/*     */   {
/* 225 */     if (allowed < 1) {
/* 226 */       Debug.out("allowed < 1");
/*     */     }
/*     */     
/* 229 */     this.decode_array[0] = (this.payload_buffer == null ? null : this.payload_buffer.getBuffer(11));
/*     */     
/* 231 */     int bytes_available = 0;
/* 232 */     boolean shrink_remaining_buffers = false;
/* 233 */     int start_buff = this.reading_length_mode ? 1 : 0;
/* 234 */     boolean marked = false;
/*     */     
/* 236 */     for (int i = start_buff; i < 2; i++) {
/* 237 */       ByteBuffer bb = this.decode_array[i];
/*     */       
/* 239 */       if (bb == null) {
/* 240 */         Debug.out("preReadProcess:: bb[" + i + "] == null, decoder destroyed=" + this.destroyed);
/*     */         
/* 242 */         throw new RuntimeException("decoder destroyed");
/*     */       }
/*     */       
/* 245 */       if (shrink_remaining_buffers) {
/* 246 */         bb.limit(0);
/*     */       }
/*     */       else {
/* 249 */         int remaining = bb.remaining();
/*     */         
/* 251 */         if (remaining >= 1)
/*     */         {
/* 253 */           if (!marked) {
/* 254 */             this.pre_read_start_buffer = i;
/* 255 */             this.pre_read_start_position = bb.position();
/* 256 */             marked = true;
/*     */           }
/*     */           
/* 259 */           if (remaining > allowed) {
/* 260 */             bb.limit(bb.position() + allowed);
/* 261 */             bytes_available += bb.remaining();
/* 262 */             shrink_remaining_buffers = true;
/*     */           }
/*     */           else {
/* 265 */             bytes_available += remaining;
/* 266 */             allowed -= remaining;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 271 */     return bytes_available;
/*     */   }
/*     */   
/*     */ 
/*     */   private int postReadProcess()
/*     */     throws IOException
/*     */   {
/* 278 */     int prot_bytes_read = 0;
/* 279 */     int data_bytes_read = 0;
/*     */     
/* 281 */     if ((!this.reading_length_mode) && (!this.destroyed))
/*     */     {
/* 283 */       this.payload_buffer.limit((byte)11, this.message_length);
/* 284 */       this.length_buffer.limit((byte)11, 4);
/*     */       
/* 286 */       int read = this.payload_buffer.position((byte)11) - this.pre_read_start_position;
/*     */       
/* 288 */       if (this.payload_buffer.position((byte)11) > 0) {
/* 289 */         if (BTMessageFactory.getMessageType(this.payload_buffer) == 1) {
/* 290 */           data_bytes_read += read;
/*     */         }
/*     */         else {
/* 293 */           prot_bytes_read += read;
/*     */         }
/*     */       }
/*     */       
/* 297 */       if ((!this.payload_buffer.hasRemaining((byte)11)) && (!this.is_paused)) {
/* 298 */         this.payload_buffer.position((byte)11, 0);
/*     */         
/* 300 */         DirectByteBuffer ref_buff = this.payload_buffer;
/* 301 */         this.payload_buffer = null;
/*     */         
/* 303 */         if (this.reading_handshake_message) {
/* 304 */           this.reading_handshake_message = false;
/*     */           
/* 306 */           DirectByteBuffer handshake_data = DirectByteBufferPool.getBuffer((byte)16, 68);
/* 307 */           handshake_data.putInt((byte)11, 323119476);
/* 308 */           handshake_data.put((byte)11, ref_buff);
/* 309 */           handshake_data.flip((byte)11);
/*     */           
/* 311 */           ref_buff.returnToPool();
/*     */           try
/*     */           {
/* 314 */             Message handshake = MessageManager.getSingleton().createMessage(BTMessage.ID_BT_HANDSHAKE_BYTES, handshake_data, (byte)1);
/* 315 */             this.messages_last_read.add(handshake);
/*     */           }
/*     */           catch (MessageException me) {
/* 318 */             handshake_data.returnToPool();
/* 319 */             throw new IOException("BT message decode failed: " + me.getMessage());
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 324 */           pauseDecoding();
/*     */         }
/*     */         else {
/*     */           try {
/* 328 */             this.messages_last_read.add(createMessage(ref_buff));
/*     */           }
/*     */           catch (Throwable e) {
/* 331 */             ref_buff.returnToPoolIfNotFree();
/*     */             
/*     */ 
/*     */ 
/* 335 */             if ((e instanceof RuntimeException))
/*     */             {
/* 337 */               throw ((RuntimeException)e);
/*     */             }
/*     */             
/* 340 */             throw new IOException("BT message decode failed: " + e.getMessage());
/*     */           }
/*     */         }
/*     */         
/* 344 */         this.reading_length_mode = true;
/* 345 */         this.percent_complete = -1;
/*     */       }
/*     */       else {
/* 348 */         this.percent_complete = (this.payload_buffer.position((byte)11) * 100 / this.message_length);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 353 */     if ((this.reading_length_mode) && (!this.destroyed)) {
/* 354 */       this.length_buffer.limit((byte)11, 4);
/*     */       
/* 356 */       prot_bytes_read += (this.pre_read_start_buffer == 1 ? this.length_buffer.position((byte)11) - this.pre_read_start_position : this.length_buffer.position((byte)11));
/*     */       
/* 358 */       if (!this.length_buffer.hasRemaining((byte)11)) {
/* 359 */         this.reading_length_mode = false;
/*     */         
/* 361 */         this.length_buffer.position((byte)11, 0);
/* 362 */         this.message_length = this.length_buffer.getInt((byte)11);
/*     */         
/* 364 */         this.length_buffer.position((byte)11, 0);
/*     */         
/* 366 */         if (this.message_length == 323119476) {
/* 367 */           this.reading_handshake_message = true;
/* 368 */           this.message_length = 64;
/* 369 */           this.payload_buffer = DirectByteBufferPool.getBuffer((byte)16, this.message_length);
/*     */         }
/* 371 */         else if (this.message_length == 0) {
/* 372 */           this.reading_length_mode = true;
/* 373 */           this.last_received_was_keepalive = true;
/*     */           try
/*     */           {
/* 376 */             Message keep_alive = MessageManager.getSingleton().createMessage(BTMessage.ID_BT_KEEP_ALIVE_BYTES, null, (byte)1);
/* 377 */             this.messages_last_read.add(keep_alive);
/*     */           }
/*     */           catch (MessageException me) {
/* 380 */             throw new IOException("BT message decode failed: " + me.getMessage());
/*     */           }
/*     */         } else {
/* 383 */           if ((this.message_length < 1) || (this.message_length > 131072)) {
/* 384 */             throw new IOException("Invalid message length given for BT message decode: " + this.message_length);
/*     */           }
/*     */           
/* 387 */           this.payload_buffer = DirectByteBufferPool.getBuffer((byte)23, this.message_length);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 392 */     this.protocol_bytes_last_read += prot_bytes_read;
/* 393 */     this.data_bytes_last_read += data_bytes_read;
/*     */     
/* 395 */     return prot_bytes_read + data_bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseDecoding()
/*     */   {
/* 401 */     this.is_paused = true;
/*     */   }
/*     */   
/*     */   public void resumeDecoding()
/*     */   {
/* 406 */     this.is_paused = false;
/*     */   }
/*     */   
/*     */   protected Message createMessage(DirectByteBuffer ref_buff) throws MessageException {
/*     */     try {
/* 411 */       return BTMessageFactory.createBTMessage(ref_buff);
/*     */ 
/*     */     }
/*     */     catch (MessageException me)
/*     */     {
/* 416 */       throw me;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */