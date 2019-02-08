/*     */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class HTTPMessageDecoder
/*     */   implements MessageStreamDecoder
/*     */ {
/*     */   private static final int MAX_HEADER = 1024;
/*     */   private static final String NL = "\r\n";
/*     */   private HTTPNetworkConnection http_connection;
/*     */   private volatile boolean paused;
/*     */   private volatile boolean paused_internally;
/*     */   private volatile boolean destroyed;
/*  45 */   private final StringBuffer header_so_far = new StringBuffer();
/*     */   
/*     */   private boolean header_ready;
/*  48 */   private final List messages = new ArrayList();
/*     */   
/*     */ 
/*     */   private int protocol_bytes_read;
/*     */   
/*     */ 
/*     */ 
/*     */   public HTTPMessageDecoder() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public HTTPMessageDecoder(String pre_read_header)
/*     */   {
/*  61 */     this.header_so_far.append(pre_read_header);
/*     */     
/*  63 */     this.header_ready = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setConnection(HTTPNetworkConnection _http_connection)
/*     */   {
/*  70 */     this.http_connection = _http_connection;
/*     */     
/*  72 */     if (this.destroyed)
/*     */     {
/*  74 */       this.http_connection.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int performStreamDecode(Transport transport, int max_bytes)
/*     */     throws IOException
/*     */   {
/*  87 */     if (this.http_connection == null)
/*     */     {
/*  89 */       Debug.out("connection not yet assigned");
/*     */       
/*  91 */       throw new IOException("Internal error - connection not yet assigned");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  96 */     this.protocol_bytes_read = 0;
/*     */     
/*  98 */     if (this.paused_internally)
/*     */     {
/* 100 */       return 0;
/*     */     }
/*     */     
/* 103 */     if (this.header_ready)
/*     */     {
/* 105 */       this.header_ready = false;
/*     */       
/* 107 */       int len = this.header_so_far.length();
/*     */       
/* 109 */       this.http_connection.decodeHeader(this, this.header_so_far.toString());
/*     */       
/* 111 */       this.header_so_far.setLength(0);
/*     */       
/* 113 */       return len;
/*     */     }
/*     */     
/* 116 */     int rem = max_bytes;
/*     */     
/* 118 */     byte[] bytes = new byte[1];
/*     */     
/* 120 */     ByteBuffer bb = ByteBuffer.wrap(bytes);
/*     */     
/* 122 */     ByteBuffer[] bbs = { bb };
/*     */     
/* 124 */     while ((rem > 0) && (!this.paused) && (!this.paused_internally))
/*     */     {
/* 126 */       if (transport.read(bbs, 0, 1) == 0L) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 131 */       rem--;
/*     */       
/* 133 */       this.protocol_bytes_read += 1;
/*     */       
/* 135 */       bb.flip();
/*     */       
/* 137 */       char c = (char)(bytes[0] & 0xFF);
/*     */       
/* 139 */       this.header_so_far.append(c);
/*     */       
/* 141 */       if (this.header_so_far.length() > 1024)
/*     */       {
/* 143 */         throw new IOException("HTTP header exceeded maximum of 1024");
/*     */       }
/*     */       
/* 146 */       if (c == '\n')
/*     */       {
/* 148 */         String header_str = this.header_so_far.toString();
/*     */         
/* 150 */         if (header_str.endsWith("\r\n\r\n"))
/*     */         {
/* 152 */           this.http_connection.decodeHeader(this, header_str);
/*     */           
/* 154 */           this.header_so_far.setLength(0);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 159 */     return max_bytes - rem;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addMessage(Message message)
/*     */   {
/* 169 */     synchronized (this.messages)
/*     */     {
/* 171 */       this.messages.add(message);
/*     */     }
/*     */     
/* 174 */     this.http_connection.readWakeup();
/*     */   }
/*     */   
/*     */ 
/*     */   public Message[] removeDecodedMessages()
/*     */   {
/* 180 */     synchronized (this.messages)
/*     */     {
/* 182 */       if (this.messages.isEmpty())
/*     */       {
/* 184 */         return null;
/*     */       }
/*     */       
/* 187 */       Message[] msgs = (Message[])this.messages.toArray(new Message[this.messages.size()]);
/*     */       
/* 189 */       this.messages.clear();
/*     */       
/* 191 */       return msgs;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getProtocolBytesDecoded()
/*     */   {
/* 198 */     return this.protocol_bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataBytesDecoded()
/*     */   {
/* 204 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentMessage()
/*     */   {
/* 210 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void pauseInternally()
/*     */   {
/* 216 */     this.paused_internally = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseDecoding()
/*     */   {
/* 222 */     this.paused = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void resumeDecoding()
/*     */   {
/* 228 */     if (!this.destroyed)
/*     */     {
/* 230 */       this.paused = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getQueueSize()
/*     */   {
/* 237 */     return this.messages.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer destroy()
/*     */   {
/* 243 */     this.paused = true;
/* 244 */     this.destroyed = true;
/*     */     
/* 246 */     if (this.http_connection != null)
/*     */     {
/* 248 */       this.http_connection.destroy();
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 253 */       for (int i = 0; i < this.messages.size(); i++)
/*     */       {
/* 255 */         Message msg = (Message)this.messages.get(i);
/*     */         
/* 257 */         msg.destroy();
/*     */       }
/*     */     }
/*     */     catch (IndexOutOfBoundsException e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 265 */     this.messages.clear();
/*     */     
/* 267 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */