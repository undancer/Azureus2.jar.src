/*     */ package com.aelitis.azureus.core.proxy.socks.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyState;
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyConnection;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
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
/*     */ public class AESocksProxyState
/*     */   implements AEProxyState
/*     */ {
/*  40 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private final AESocksProxyConnection socks_connection;
/*     */   
/*     */   protected ByteBuffer buffer;
/*     */   
/*     */ 
/*     */   protected AESocksProxyState(AESocksProxyConnection _socks_connection)
/*     */   {
/*  49 */     this.socks_connection = _socks_connection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStateName()
/*     */   {
/*  60 */     String state = getClass().getName();
/*     */     
/*  62 */     int pos = state.indexOf("$");
/*     */     
/*  64 */     state = state.substring(pos + 1);
/*     */     
/*  66 */     return state + ", buffer = " + this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean read(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       return readSupport(sc);
/*     */     }
/*     */     finally
/*     */     {
/*  80 */       trace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean readSupport(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/*  90 */     throw new IOException("Read not supported: " + sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean write(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 100 */       return writeSupport(sc);
/*     */     }
/*     */     finally
/*     */     {
/* 104 */       trace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean writeSupport(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/* 114 */     throw new IOException("Write not supported: " + sc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean connect(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 124 */       return connectSupport(sc);
/*     */     }
/*     */     finally
/*     */     {
/* 128 */       trace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean connectSupport(SocketChannel sc)
/*     */     throws IOException
/*     */   {
/* 138 */     throw new IOException("Connect not supported: " + sc);
/*     */   }
/*     */   
/*     */   protected void trace() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/impl/AESocksProxyState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */