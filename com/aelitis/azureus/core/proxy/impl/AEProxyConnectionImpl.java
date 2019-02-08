/*     */ package com.aelitis.azureus.core.proxy.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyConnection;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyConnectionListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyHandler;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyState;
/*     */ import java.io.EOFException;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class AEProxyConnectionImpl
/*     */   implements AEProxyConnection
/*     */ {
/*  41 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   protected final AEProxyImpl server;
/*     */   
/*     */   protected final SocketChannel source_channel;
/*  46 */   protected volatile AEProxyState proxy_read_state = null;
/*  47 */   protected volatile AEProxyState proxy_write_state = null;
/*  48 */   protected volatile AEProxyState proxy_connect_state = null;
/*     */   
/*     */   protected long time_stamp;
/*     */   
/*     */   protected boolean is_connected;
/*     */   
/*     */   protected boolean is_closed;
/*  55 */   protected final List listeners = new ArrayList(1);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AEProxyConnectionImpl(AEProxyImpl _server, SocketChannel _socket, AEProxyHandler _handler)
/*     */   {
/*  63 */     this.server = _server;
/*  64 */     this.source_channel = _socket;
/*     */     
/*  66 */     setTimeStamp();
/*     */     try
/*     */     {
/*  69 */       this.proxy_read_state = _handler.getInitialState(this);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  73 */       failed(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  80 */     String name = this.source_channel.socket().getInetAddress() + ":" + this.source_channel.socket().getPort() + " -> ";
/*     */     
/*  82 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */   public SocketChannel getSourceChannel()
/*     */   {
/*  88 */     return this.source_channel;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReadState(AEProxyState state)
/*     */   {
/*  95 */     this.proxy_read_state = state;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setWriteState(AEProxyState state)
/*     */   {
/* 102 */     this.proxy_write_state = state;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setConnectState(AEProxyState state)
/*     */   {
/* 109 */     this.proxy_connect_state = state;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean read(SocketChannel sc)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       return this.proxy_read_state.read(sc);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 121 */       failed(e);
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean write(SocketChannel sc)
/*     */   {
/*     */     try
/*     */     {
/* 132 */       return this.proxy_write_state.write(sc);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 136 */       failed(e);
/*     */     }
/* 138 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean connect(SocketChannel sc)
/*     */   {
/*     */     try
/*     */     {
/* 147 */       return this.proxy_connect_state.connect(sc);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 151 */       failed(e);
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestWriteSelect(SocketChannel sc)
/*     */   {
/* 162 */     this.server.requestWriteSelect(this, sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelWriteSelect(SocketChannel sc)
/*     */   {
/* 169 */     this.server.cancelWriteSelect(sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void requestConnectSelect(SocketChannel sc)
/*     */   {
/* 176 */     this.server.requestConnectSelect(this, sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelConnectSelect(SocketChannel sc)
/*     */   {
/* 183 */     this.server.cancelConnectSelect(sc);
/*     */   }
/*     */   
/*     */ 
/*     */   public void requestReadSelect(SocketChannel sc)
/*     */   {
/* 189 */     this.server.requestReadSelect(this, sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelReadSelect(SocketChannel sc)
/*     */   {
/* 196 */     this.server.cancelReadSelect(sc);
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable reason)
/*     */   {
/*     */     try
/*     */     {
/* 204 */       if (Logger.isEnabled())
/*     */       {
/* 206 */         if ((reason instanceof EOFException))
/*     */         {
/* 208 */           Logger.log(new LogEvent(LOGID, "AEProxyProcessor: " + getName() + ": connection closed"));
/*     */         }
/*     */         else
/*     */         {
/* 212 */           String message = Debug.getNestedExceptionMessage(reason);
/*     */           
/* 214 */           message = message.toLowerCase(Locale.US);
/*     */           
/* 216 */           if (((reason instanceof AsynchronousCloseException)) || (message.contains("closed")) || (message.contains("aborted")) || (message.contains("disconnected")) || (message.contains("timeout")) || (message.contains("timed")) || (message.contains("refused")) || (message.contains("unreachable")) || (message.contains("reset")) || (message.contains("no route")) || (message.contains("family")) || (message.contains("key is invalid")) || (message.contains("dns lookup")))
/*     */           {
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
/* 232 */             Logger.log(new LogEvent(LOGID, "AEProxyProcessor: " + getName() + " failed: " + message));
/*     */           }
/*     */           else
/*     */           {
/* 236 */             Logger.log(new LogEvent(LOGID, "AEProxyProcessor: " + getName() + " failed", reason));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 241 */       close();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 245 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 252 */     this.is_closed = true;
/*     */     try
/*     */     {
/*     */       try {
/* 256 */         cancelReadSelect(this.source_channel);
/*     */         
/* 258 */         cancelWriteSelect(this.source_channel);
/*     */         
/* 260 */         this.source_channel.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 264 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 267 */       for (int i = 0; i < this.listeners.size(); i++) {
/*     */         try
/*     */         {
/* 270 */           ((AEProxyConnectionListener)this.listeners.get(i)).connectionClosed(this);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 274 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 279 */       this.server.close(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 286 */     return this.is_closed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setConnected()
/*     */   {
/* 292 */     setTimeStamp();
/*     */     
/* 294 */     this.is_connected = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isConnected()
/*     */   {
/* 300 */     return this.is_connected;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTimeStamp()
/*     */   {
/* 306 */     this.time_stamp = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTimeStamp()
/*     */   {
/* 312 */     return this.time_stamp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(AEProxyConnectionListener l)
/*     */   {
/* 319 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(AEProxyConnectionListener l)
/*     */   {
/* 326 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getStateString()
/*     */   {
/* 332 */     return getName() + "connected = " + this.is_connected + ", closed = " + this.is_closed + ", " + "chan: reg = " + this.source_channel.isRegistered() + ", open = " + this.source_channel.isOpen() + ", " + "read:" + (this.proxy_read_state == null ? null : this.proxy_read_state.getStateName()) + ", " + "write:" + (this.proxy_write_state == null ? null : this.proxy_write_state.getStateName()) + ", " + "connect:" + (this.proxy_connect_state == null ? null : this.proxy_connect_state.getStateName());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/AEProxyConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */