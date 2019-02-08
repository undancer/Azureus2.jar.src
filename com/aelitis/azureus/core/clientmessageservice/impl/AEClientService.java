/*     */ package com.aelitis.azureus.core.clientmessageservice.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageService;
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportImpl;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class AEClientService
/*     */   implements ClientMessageService
/*     */ {
/*     */   private final String address;
/*     */   private final int port;
/*     */   private final String msg_type_id;
/*     */   private final int timeout_secs;
/*  46 */   private int max_message_bytes = -1;
/*     */   
/*     */   private ClientConnection conn;
/*  49 */   private final AESemaphore read_block = new AESemaphore("AEClientService:R");
/*  50 */   private final AESemaphore write_block = new AESemaphore("AEClientService:W");
/*     */   
/*  52 */   private final ArrayList received_messages = new ArrayList();
/*     */   
/*     */   private final NonBlockingReadWriteService rw_service;
/*     */   
/*     */   private volatile Throwable error;
/*     */   
/*     */ 
/*     */   public AEClientService(String server_address, int server_port, String _msg_type_id)
/*     */   {
/*  61 */     this(server_address, server_port, 30, _msg_type_id);
/*     */   }
/*     */   
/*     */   public AEClientService(String server_address, int server_port, int timeout, String _msg_type_id) {
/*  65 */     this.address = server_address;
/*  66 */     this.port = server_port;
/*  67 */     this.timeout_secs = timeout;
/*  68 */     this.msg_type_id = _msg_type_id;
/*     */     try
/*     */     {
/*  71 */       AZMessageFactory.registerGenericMapPayloadMessageType(this.msg_type_id);
/*     */     }
/*     */     catch (MessageException me) {}
/*     */     
/*  75 */     this.rw_service = new NonBlockingReadWriteService(this.msg_type_id, timeout, 0, new NonBlockingReadWriteService.ServiceListener() {
/*     */       public void messageReceived(ClientMessage message) {
/*  77 */         AEClientService.this.received_messages.add(message.getPayload());
/*  78 */         AEClientService.this.read_block.release();
/*     */       }
/*     */       
/*     */       public void connectionError(ClientConnection connection, Throwable msg) {
/*  82 */         AEClientService.this.error = msg;
/*  83 */         AEClientService.this.read_block.releaseForever();
/*  84 */         AEClientService.this.write_block.releaseForever();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void connect()
/*     */     throws IOException
/*     */   {
/*  95 */     InetSocketAddress tcp_target = new InetSocketAddress(this.address, this.port);
/*     */     
/*  97 */     ConnectionEndpoint ce = new ConnectionEndpoint(tcp_target);
/*     */     
/*  99 */     ProtocolEndpointFactory.createEndpoint(1, ce, tcp_target);
/*     */     
/* 101 */     final AESemaphore connect_block = new AESemaphore("AEClientService:C");
/*     */     
/* 103 */     ce.connectOutbound(false, false, (byte[][])null, null, 3, new Transport.ConnectListener() {
/* 104 */       public int connectAttemptStarted(int default_connect_timeout) { return default_connect_timeout; }
/*     */       
/*     */       public void connectSuccess(Transport transport, ByteBuffer remaining_initial_data) {
/* 107 */         AEClientService.this.conn = new ClientConnection((TCPTransportImpl)transport);
/* 108 */         if (AEClientService.this.max_message_bytes != -1) {
/* 109 */           AEClientService.this.conn.setMaximumMessageSize(AEClientService.this.max_message_bytes);
/*     */         }
/* 111 */         connect_block.release();
/*     */       }
/*     */       
/*     */       public void connectFailure(Throwable failure_msg) {
/* 115 */         AEClientService.this.error = failure_msg;
/* 116 */         connect_block.release();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object getConnectionProperty(String property_name)
/*     */       {
/* 123 */         return null;
/*     */       }
/*     */     });
/*     */     
/* 127 */     if (!connect_block.reserve(this.timeout_secs * 1000)) {
/* 128 */       throw new IOException("connect op failed: timeout");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 133 */     if (this.error != null) {
/* 134 */       close();
/* 135 */       throw new IOException("connect op failed: " + (this.error.getMessage() == null ? "[]" : this.error.getMessage()));
/*     */     }
/*     */     
/* 138 */     this.rw_service.addClientConnection(this.conn);
/*     */   }
/*     */   
/*     */ 
/*     */   public void sendMessage(Map message)
/*     */     throws IOException
/*     */   {
/* 145 */     if (this.conn == null) {
/* 146 */       connect();
/*     */     }
/*     */     
/* 149 */     if (this.error != null) {
/* 150 */       close();
/* 151 */       throw new IOException("send op failed: " + (this.error.getMessage() == null ? "[]" : this.error.getMessage()));
/*     */     }
/*     */     
/* 154 */     ClientMessage client_msg = new ClientMessage(this.msg_type_id, this.conn, message, new ClientMessageHandler() {
/* 155 */       public String getMessageTypeID() { return AEClientService.this.msg_type_id; }
/*     */       
/*     */       public void processMessage(ClientMessage message) {
/* 158 */         Debug.out("ERROR: should never be called");
/*     */       }
/*     */       
/*     */ 
/* 162 */       public void sendAttemptCompleted(ClientMessage message) { AEClientService.this.write_block.release(); }
/*     */       
/*     */       public void sendAttemptFailed(ClientMessage message, Throwable cause) {
/* 165 */         AEClientService.this.error = cause;
/* 166 */         AEClientService.this.write_block.release();
/*     */       }
/*     */       
/* 169 */     });
/* 170 */     this.rw_service.sendMessage(client_msg);
/*     */     
/* 172 */     this.write_block.reserve();
/*     */     
/*     */ 
/*     */ 
/* 176 */     if (this.error != null) {
/* 177 */       close();
/* 178 */       throw new IOException("send op failed: " + (this.error.getMessage() == null ? "[]" : this.error.getMessage()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Map receiveMessage()
/*     */     throws IOException
/*     */   {
/* 186 */     if (this.conn == null) {
/* 187 */       connect();
/*     */     }
/*     */     
/* 190 */     this.read_block.reserve();
/*     */     
/* 192 */     if (!this.received_messages.isEmpty()) {
/* 193 */       Map recv_msg = (Map)this.received_messages.remove(0);
/* 194 */       return recv_msg;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 199 */     if (this.error == null) {
/* 200 */       this.error = new IOException("receive op inconsistent");
/*     */     }
/*     */     
/* 203 */     close();
/* 204 */     throw new IOException("receive op failed: " + (this.error.getMessage() == null ? "[]" : this.error.getMessage()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 211 */     if (this.conn != null) {
/* 212 */       this.rw_service.removeClientConnection(this.conn);
/* 213 */       this.conn.close(new Exception("Connection closed"));
/*     */     }
/* 215 */     this.rw_service.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setMaximumMessageSize(int max_bytes)
/*     */   {
/* 221 */     this.max_message_bytes = max_bytes;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/impl/AEClientService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */