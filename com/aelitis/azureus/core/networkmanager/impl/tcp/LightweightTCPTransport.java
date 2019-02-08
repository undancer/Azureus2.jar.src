/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
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
/*     */ public class LightweightTCPTransport
/*     */   implements Transport
/*     */ {
/*     */   private final TransportStartpointTCP transport_startpoint;
/*     */   private final TransportEndpointTCP transport_endpoint;
/*     */   private final TransportHelperFilter filter;
/*     */   
/*     */   public LightweightTCPTransport(ProtocolEndpoint pe, TransportHelperFilter filter)
/*     */   {
/*  45 */     SocketChannel channel = ((TCPTransportHelper)filter.getHelper()).getSocketChannel();
/*  46 */     this.transport_endpoint = new TransportEndpointTCP(pe, channel);
/*  47 */     this.transport_startpoint = new TransportStartpointTCP(this.transport_endpoint);
/*     */     
/*  49 */     this.filter = filter;
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportEndpoint getTransportEndpoint()
/*     */   {
/*  55 */     return this.transport_endpoint;
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportStartpoint getTransportStartpoint()
/*     */   {
/*  61 */     return this.transport_startpoint;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] buffers, int array_offset, int length) throws IOException {
/*  65 */     return this.filter.write(buffers, array_offset, length);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] buffers, int array_offset, int length) throws IOException
/*     */   {
/*  70 */     return this.filter.read(buffers, array_offset, length);
/*     */   }
/*     */   
/*     */   public SocketChannel getSocketChannel() {
/*  74 */     return ((TCPTransportHelper)this.filter.getHelper()).getSocketChannel();
/*     */   }
/*     */   
/*     */   public InetSocketAddress getRemoteAddress()
/*     */   {
/*  79 */     return new InetSocketAddress(getSocketChannel().socket().getInetAddress(), getSocketChannel().socket().getPort());
/*     */   }
/*     */   
/*  82 */   public String getDescription() { return getSocketChannel().socket().getInetAddress().getHostAddress() + ": " + getSocketChannel().socket().getPort(); }
/*     */   
/*     */   public void close(String reason) {
/*     */     try {
/*  86 */       getSocketChannel().close();
/*     */     } catch (Throwable t) {
/*  88 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getMssSize()
/*     */   {
/*  94 */     return TCPNetworkManager.getTcpMssSize();
/*     */   }
/*     */   
/*  97 */   public void setAlreadyRead(ByteBuffer bytes_already_read) { throw new RuntimeException("not implemented"); }
/*  98 */   public boolean isReadyForWrite(EventWaiter waiter) { throw new RuntimeException("not implemented"); }
/*  99 */   public long isReadyForRead(EventWaiter waiter) { throw new RuntimeException("not implemented"); }
/* 100 */   public void setReadyForRead() { throw new RuntimeException("not implemented"); }
/* 101 */   public void connectOutbound(ByteBuffer initial_data, Transport.ConnectListener listener, int priority) { throw new RuntimeException("not implemented"); }
/* 102 */   public void connectedInbound() { throw new RuntimeException("not implemented"); }
/* 103 */   public void setTransportMode(int mode) { throw new RuntimeException("not implemented"); }
/* 104 */   public int getTransportMode() { throw new RuntimeException("not implemented"); }
/*     */   
/*     */ 
/*     */   public void setTrace(boolean on) {}
/*     */   
/* 109 */   public String getEncryption(boolean verbose) { return this.filter.getName(verbose); }
/* 110 */   public boolean isEncrypted() { return this.filter.isEncrypted(); }
/* 111 */   public boolean isTCP() { return true; }
/* 112 */   public boolean isSOCKS() { return false; }
/* 113 */   public String getProtocol() { return "TCP"; }
/*     */   
/*     */   public void bindConnection(NetworkConnection connection) {}
/*     */   
/*     */   public void unbindConnection(NetworkConnection connection) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/LightweightTCPTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */