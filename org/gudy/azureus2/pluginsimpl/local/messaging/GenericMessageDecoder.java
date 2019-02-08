/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class GenericMessageDecoder
/*     */   implements MessageStreamDecoder
/*     */ {
/*     */   public static final int MAX_MESSAGE_LENGTH = 262144;
/*  39 */   private final ByteBuffer length_buffer = ByteBuffer.allocate(4);
/*     */   
/*  41 */   private final ByteBuffer[] buffers = { this.length_buffer, null };
/*     */   
/*     */   private final String msg_type;
/*     */   
/*     */   private final String msg_desc;
/*  46 */   private List messages = new ArrayList();
/*     */   
/*  48 */   private int protocol_bytes_last_read = 0;
/*  49 */   private int data_bytes_last_read = 0;
/*     */   
/*     */ 
/*     */   private volatile boolean destroyed;
/*     */   
/*     */ 
/*     */ 
/*     */   protected GenericMessageDecoder(String _msg_type, String _msg_desc)
/*     */   {
/*  58 */     this.msg_type = _msg_type;
/*  59 */     this.msg_desc = _msg_desc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int performStreamDecode(Transport transport, int max_bytes)
/*     */     throws IOException
/*     */   {
/*  69 */     this.protocol_bytes_last_read = 0;
/*  70 */     this.data_bytes_last_read = 0;
/*     */     
/*  72 */     long total_read = 0L;
/*     */     
/*  74 */     while (total_read < max_bytes)
/*     */     {
/*     */ 
/*     */ 
/*  78 */       int read_lim = (int)(max_bytes - total_read);
/*     */       
/*  80 */       ByteBuffer payload_buffer = this.buffers[1];
/*     */       long bytes_read;
/*  82 */       if (payload_buffer == null)
/*     */       {
/*  84 */         int rem = this.length_buffer.remaining();
/*  85 */         int lim = this.length_buffer.limit();
/*     */         
/*  87 */         if (rem > read_lim)
/*     */         {
/*  89 */           this.length_buffer.limit(this.length_buffer.position() + read_lim);
/*     */         }
/*     */         
/*  92 */         long bytes_read = transport.read(this.buffers, 0, 1);
/*     */         
/*  94 */         this.length_buffer.limit(lim);
/*     */         
/*  96 */         this.protocol_bytes_last_read = ((int)(this.protocol_bytes_last_read + bytes_read));
/*     */         
/*  98 */         if (this.length_buffer.hasRemaining())
/*     */         {
/* 100 */           total_read += bytes_read;
/*     */           
/* 102 */           break;
/*     */         }
/*     */         
/*     */ 
/* 106 */         this.length_buffer.flip();
/*     */         
/* 108 */         int size = this.length_buffer.getInt();
/*     */         
/* 110 */         if (size > 262144)
/*     */         {
/* 112 */           Debug.out("Message too large for generic payload");
/*     */           
/* 114 */           throw new IOException("message too large");
/*     */         }
/*     */         
/* 117 */         this.buffers[1] = ByteBuffer.allocate(size);
/*     */         
/* 119 */         this.length_buffer.flip();
/*     */       }
/*     */       else
/*     */       {
/* 123 */         int rem = payload_buffer.remaining();
/* 124 */         int lim = payload_buffer.limit();
/*     */         
/* 126 */         if (rem > read_lim)
/*     */         {
/* 128 */           payload_buffer.limit(payload_buffer.position() + read_lim);
/*     */         }
/*     */         
/* 131 */         bytes_read = transport.read(this.buffers, 1, 1);
/*     */         
/* 133 */         payload_buffer.limit(lim);
/*     */         
/* 135 */         this.data_bytes_last_read = ((int)(this.data_bytes_last_read + bytes_read));
/*     */         
/* 137 */         if (payload_buffer.hasRemaining())
/*     */         {
/* 139 */           total_read += bytes_read;
/*     */           
/* 141 */           break;
/*     */         }
/*     */         
/* 144 */         payload_buffer.flip();
/*     */         
/* 146 */         this.messages.add(new GenericMessage(this.msg_type, this.msg_desc, new DirectByteBuffer(payload_buffer), false));
/*     */         
/* 148 */         this.buffers[1] = null;
/*     */       }
/*     */       
/* 151 */       total_read += bytes_read;
/*     */     }
/*     */     
/* 154 */     if (this.destroyed)
/*     */     {
/* 156 */       throw new IOException("decoder has been destroyed");
/*     */     }
/*     */     
/* 159 */     return (int)total_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public Message[] removeDecodedMessages()
/*     */   {
/* 165 */     if (this.messages.isEmpty()) { return null;
/*     */     }
/* 167 */     Message[] msgs = (Message[])this.messages.toArray(new Message[this.messages.size()]);
/*     */     
/* 169 */     this.messages.clear();
/*     */     
/* 171 */     return msgs;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getProtocolBytesDecoded()
/*     */   {
/* 177 */     return this.protocol_bytes_last_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataBytesDecoded()
/*     */   {
/* 183 */     return this.data_bytes_last_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentMessage()
/*     */   {
/* 189 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pauseDecoding() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void resumeDecoding() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public ByteBuffer destroy()
/*     */   {
/* 205 */     this.destroyed = true;
/*     */     
/* 207 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */