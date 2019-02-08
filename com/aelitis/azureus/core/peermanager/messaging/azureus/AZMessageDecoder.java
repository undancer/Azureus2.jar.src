/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
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
/*     */ 
/*     */ 
/*     */ public class AZMessageDecoder
/*     */   implements MessageStreamDecoder
/*     */ {
/*     */   private static final int MIN_MESSAGE_LENGTH = 6;
/*     */   private static final int MAX_MESSAGE_LENGTH = 131072;
/*     */   private static final byte SS = 11;
/*  43 */   private DirectByteBuffer payload_buffer = null;
/*  44 */   private final DirectByteBuffer length_buffer = DirectByteBufferPool.getBuffer((byte)12, 4);
/*  45 */   private final ByteBuffer[] decode_array = { null, this.length_buffer.getBuffer(11) };
/*     */   
/*  47 */   private boolean reading_length_mode = true;
/*     */   
/*     */   private int message_length;
/*     */   
/*     */   private int pre_read_start_buffer;
/*     */   private int pre_read_start_position;
/*  53 */   private volatile boolean destroyed = false;
/*  54 */   private volatile boolean is_paused = false;
/*     */   
/*  56 */   private final ArrayList messages_last_read = new ArrayList();
/*  57 */   private int protocol_bytes_last_read = 0;
/*  58 */   private int data_bytes_last_read = 0;
/*  59 */   private int percent_complete = -1;
/*     */   
/*     */ 
/*  62 */   private byte[] msg_id_bytes = null;
/*  63 */   private boolean msg_id_read_complete = false;
/*     */   
/*     */   private boolean last_read_made_progress;
/*     */   
/*  67 */   private int maximum_message_size = 131072;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMaximumMessageSize(int max_bytes)
/*     */   {
/*  77 */     this.maximum_message_size = max_bytes;
/*     */   }
/*     */   
/*     */   public int performStreamDecode(Transport transport, int max_bytes) throws IOException {
/*  81 */     this.protocol_bytes_last_read = 0;
/*  82 */     this.data_bytes_last_read = 0;
/*     */     
/*  84 */     int bytes_remaining = max_bytes;
/*     */     
/*  86 */     while ((bytes_remaining > 0) && 
/*  87 */       (!this.destroyed))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  94 */       if (this.is_paused) {
/*  95 */         Debug.out("AZ decoder paused");
/*     */       }
/*     */       else
/*     */       {
/*  99 */         int bytes_possible = preReadProcess(bytes_remaining);
/*     */         
/* 101 */         if (bytes_possible < 1) {
/* 102 */           Debug.out("ERROR AZ: bytes_possible < 1");
/*     */         }
/*     */         else
/*     */         {
/*     */           long actual_read;
/*     */           long actual_read;
/* 108 */           if (this.reading_length_mode) {
/* 109 */             actual_read = transport.read(this.decode_array, 1, 1);
/*     */           }
/*     */           else {
/* 112 */             actual_read = transport.read(this.decode_array, 0, 2);
/*     */           }
/*     */           
/* 115 */           this.last_read_made_progress = (actual_read > 0L);
/*     */           
/* 117 */           int bytes_read = postReadProcess();
/*     */           
/* 119 */           bytes_remaining -= bytes_read;
/*     */           
/* 121 */           if (bytes_read < bytes_possible)
/*     */             break;
/*     */         }
/*     */       }
/*     */     }
/* 126 */     return max_bytes - bytes_remaining;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentMessage()
/*     */   {
/* 132 */     return this.percent_complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public Message[] removeDecodedMessages()
/*     */   {
/* 138 */     if (this.messages_last_read.isEmpty()) { return null;
/*     */     }
/* 140 */     Message[] msgs = (Message[])this.messages_last_read.toArray(new Message[this.messages_last_read.size()]);
/* 141 */     this.messages_last_read.clear();
/*     */     
/* 143 */     return msgs;
/*     */   }
/*     */   
/*     */   public int getProtocolBytesDecoded()
/*     */   {
/* 148 */     return this.protocol_bytes_last_read;
/*     */   }
/*     */   
/*     */ 
/* 152 */   public int getDataBytesDecoded() { return this.data_bytes_last_read; }
/*     */   
/* 154 */   public boolean getLastReadMadeProgress() { return this.last_read_made_progress; }
/*     */   
/*     */   public ByteBuffer destroy() {
/* 157 */     this.is_paused = true;
/* 158 */     this.destroyed = true;
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
/* 187 */     this.length_buffer.returnToPool();
/*     */     
/* 189 */     if (this.payload_buffer != null) {
/* 190 */       this.payload_buffer.returnToPool();
/* 191 */       this.payload_buffer = null;
/*     */     }
/*     */     try
/*     */     {
/* 195 */       for (int i = 0; i < this.messages_last_read.size(); i++) {
/* 196 */         Message msg = (Message)this.messages_last_read.get(i);
/* 197 */         msg.destroy();
/*     */       }
/*     */     }
/*     */     catch (IndexOutOfBoundsException e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 205 */     this.messages_last_read.clear();
/*     */     
/*     */ 
/* 208 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int preReadProcess(int allowed)
/*     */   {
/* 216 */     if (allowed < 1) {
/* 217 */       Debug.out("allowed < 1");
/*     */     }
/*     */     
/* 220 */     this.decode_array[0] = (this.payload_buffer == null ? null : this.payload_buffer.getBuffer(11));
/*     */     
/* 222 */     int bytes_available = 0;
/* 223 */     boolean shrink_remaining_buffers = false;
/* 224 */     int start_buff = this.reading_length_mode ? 1 : 0;
/* 225 */     boolean marked = false;
/*     */     
/* 227 */     for (int i = start_buff; i < 2; i++) {
/* 228 */       ByteBuffer bb = this.decode_array[i];
/*     */       
/* 230 */       if (bb == null) {
/* 231 */         Debug.out("preReadProcess:: bb[" + i + "] == null, decoder destroyed=" + this.destroyed);
/*     */         
/* 233 */         throw new RuntimeException("decoder destroyed");
/*     */       }
/*     */       
/*     */ 
/* 237 */       if (shrink_remaining_buffers) {
/* 238 */         bb.limit(0);
/*     */       }
/*     */       else {
/* 241 */         int remaining = bb.remaining();
/*     */         
/* 243 */         if (remaining >= 1)
/*     */         {
/* 245 */           if (!marked) {
/* 246 */             this.pre_read_start_buffer = i;
/* 247 */             this.pre_read_start_position = bb.position();
/* 248 */             marked = true;
/*     */           }
/*     */           
/* 251 */           if (remaining > allowed) {
/* 252 */             bb.limit(bb.position() + allowed);
/* 253 */             bytes_available += bb.remaining();
/* 254 */             shrink_remaining_buffers = true;
/*     */           }
/*     */           else {
/* 257 */             bytes_available += remaining;
/* 258 */             allowed -= remaining;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 263 */     return bytes_available;
/*     */   }
/*     */   
/*     */ 
/*     */   private int postReadProcess()
/*     */     throws IOException
/*     */   {
/* 270 */     int prot_bytes_read = 0;
/* 271 */     int data_bytes_read = 0;
/*     */     
/* 273 */     if ((!this.reading_length_mode) && (!this.destroyed))
/*     */     {
/* 275 */       this.payload_buffer.limit((byte)11, this.message_length);
/* 276 */       this.length_buffer.limit((byte)11, 4);
/*     */       
/* 278 */       int curr_position = this.payload_buffer.position((byte)11);
/* 279 */       int read = curr_position - this.pre_read_start_position;
/*     */       
/* 281 */       if ((this.msg_id_bytes == null) && (curr_position >= 4)) {
/* 282 */         this.payload_buffer.position((byte)11, 0);
/* 283 */         int id_size = this.payload_buffer.getInt((byte)11);
/* 284 */         this.payload_buffer.position((byte)11, curr_position);
/* 285 */         if ((id_size < 1) || (id_size > 1024)) throw new IOException("invalid id_size [" + id_size + "]");
/* 286 */         this.msg_id_bytes = new byte[id_size];
/*     */       }
/*     */       
/* 289 */       if ((this.msg_id_bytes != null) && (curr_position >= this.msg_id_bytes.length + 4)) {
/* 290 */         if (!this.msg_id_read_complete) {
/* 291 */           this.payload_buffer.position((byte)11, 4);
/* 292 */           this.payload_buffer.get((byte)11, this.msg_id_bytes);
/* 293 */           this.payload_buffer.position((byte)11, curr_position);
/* 294 */           this.msg_id_read_complete = true;
/*     */         }
/*     */         
/* 297 */         Message message = MessageManager.getSingleton().lookupMessage(this.msg_id_bytes);
/*     */         
/* 299 */         if (message == null)
/*     */         {
/* 301 */           Debug.out("Unknown message type '" + new String(this.msg_id_bytes) + "'");
/*     */           
/* 303 */           throw new IOException("Unknown message type");
/*     */         }
/*     */         
/* 306 */         if (message.getType() == 1) {
/* 307 */           data_bytes_read += read;
/*     */         } else {
/* 309 */           prot_bytes_read += read;
/*     */         }
/*     */       }
/*     */       else {
/* 313 */         prot_bytes_read += read;
/*     */       }
/*     */       
/* 316 */       if ((!this.payload_buffer.hasRemaining((byte)11)) && (!this.is_paused)) {
/* 317 */         this.payload_buffer.position((byte)11, 0);
/*     */         
/* 319 */         DirectByteBuffer ref_buff = this.payload_buffer;
/* 320 */         this.payload_buffer = null;
/*     */         try
/*     */         {
/* 323 */           Message msg = AZMessageFactory.createAZMessage(ref_buff);
/* 324 */           this.messages_last_read.add(msg);
/*     */         }
/*     */         catch (Throwable e) {
/* 327 */           ref_buff.returnToPoolIfNotFree();
/*     */           
/*     */ 
/*     */ 
/* 331 */           if ((e instanceof RuntimeException))
/*     */           {
/* 333 */             throw ((RuntimeException)e);
/*     */           }
/*     */           
/* 336 */           throw new IOException("AZ message decode failed: " + e.getMessage());
/*     */         }
/*     */         
/* 339 */         this.reading_length_mode = true;
/* 340 */         this.percent_complete = -1;
/* 341 */         this.msg_id_bytes = null;
/* 342 */         this.msg_id_read_complete = false;
/*     */       }
/*     */       else {
/* 345 */         this.percent_complete = (this.payload_buffer.position((byte)11) * 100 / this.message_length);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 350 */     if ((this.reading_length_mode) && (!this.destroyed)) {
/* 351 */       this.length_buffer.limit((byte)11, 4);
/*     */       
/* 353 */       prot_bytes_read += (this.pre_read_start_buffer == 1 ? this.length_buffer.position((byte)11) - this.pre_read_start_position : this.length_buffer.position((byte)11));
/*     */       
/* 355 */       if (!this.length_buffer.hasRemaining((byte)11)) {
/* 356 */         this.reading_length_mode = false;
/* 357 */         this.length_buffer.position((byte)11, 0);
/*     */         
/* 359 */         this.message_length = this.length_buffer.getInt((byte)11);
/*     */         
/* 361 */         this.length_buffer.position((byte)11, 0);
/*     */         
/* 363 */         if ((this.message_length < 6) || (this.message_length > this.maximum_message_size)) {
/* 364 */           throw new IOException("Invalid message length given for AZ message decode: " + this.message_length + " (max=" + this.maximum_message_size + ")");
/*     */         }
/*     */         
/* 367 */         this.payload_buffer = DirectByteBufferPool.getBuffer((byte)24, this.message_length);
/*     */       }
/*     */     }
/*     */     
/* 371 */     this.protocol_bytes_last_read += prot_bytes_read;
/* 372 */     this.data_bytes_last_read += data_bytes_read;
/*     */     
/* 374 */     return prot_bytes_read + data_bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseDecoding()
/*     */   {
/* 380 */     this.is_paused = true;
/*     */   }
/*     */   
/*     */   public void resumeDecoding()
/*     */   {
/* 385 */     this.is_paused = false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */