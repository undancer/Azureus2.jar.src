/*     */ package org.gudy.azureus2.pluginsimpl.local.network;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.plugins.network.Connection;
/*     */ import org.gudy.azureus2.plugins.network.ConnectionListener;
/*     */ import org.gudy.azureus2.plugins.network.IncomingMessageQueue;
/*     */ import org.gudy.azureus2.plugins.network.OutgoingMessageQueue;
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
/*     */ public class ConnectionImpl
/*     */   implements Connection
/*     */ {
/*     */   private final NetworkConnection core_connection;
/*     */   private final OutgoingMessageQueueImpl out_queue;
/*     */   private final IncomingMessageQueueImpl in_queue;
/*     */   private final TransportImpl transport;
/*     */   private final boolean incoming;
/*     */   
/*     */   public ConnectionImpl(NetworkConnection core_connection, boolean incoming)
/*     */   {
/*  43 */     this.core_connection = core_connection;
/*  44 */     this.out_queue = new OutgoingMessageQueueImpl(core_connection.getOutgoingMessageQueue());
/*  45 */     this.in_queue = new IncomingMessageQueueImpl(core_connection.getIncomingMessageQueue());
/*  46 */     this.transport = new TransportImpl(core_connection);
/*  47 */     this.incoming = incoming;
/*     */   }
/*     */   
/*     */   public void connect(final ConnectionListener listener)
/*     */   {
/*  52 */     this.core_connection.connect(3, new NetworkConnection.ConnectionListener() {
/*  53 */       public int connectStarted(int ct) { listener.connectStarted();return ct; }
/*     */       
/*  55 */       public void connectSuccess(ByteBuffer remaining_initial_data) { listener.connectSuccess(); }
/*     */       
/*  57 */       public void connectFailure(Throwable failure_msg) { listener.connectFailure(failure_msg); }
/*  58 */       public void exceptionThrown(Throwable error) { listener.exceptionThrown(error); }
/*     */       
/*  60 */       public Object getConnectionProperty(String property_name) { return null; }
/*     */       
/*     */ 
/*     */       public String getDescription()
/*     */       {
/*  65 */         return "plugin connection: " + ConnectionImpl.this.core_connection.getString();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/*  72 */     this.core_connection.close(null);
/*     */   }
/*     */   
/*     */ 
/*  76 */   public OutgoingMessageQueue getOutgoingMessageQueue() { return this.out_queue; }
/*     */   
/*  78 */   public IncomingMessageQueue getIncomingMessageQueue() { return this.in_queue; }
/*     */   
/*     */ 
/*     */   public void startMessageProcessing()
/*     */   {
/*  83 */     this.core_connection.startMessageProcessing();
/*     */     
/*  85 */     this.core_connection.enableEnhancedMessageProcessing(true, -1);
/*     */   }
/*     */   
/*     */   public org.gudy.azureus2.plugins.network.Transport getTransport() {
/*  89 */     return this.transport;
/*     */   }
/*     */   
/*     */   public NetworkConnection getCoreConnection() {
/*  93 */     return this.core_connection;
/*     */   }
/*     */   
/*     */   public boolean isIncoming() {
/*  97 */     return this.incoming;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 103 */     com.aelitis.azureus.core.networkmanager.Transport t = this.core_connection.getTransport();
/*     */     
/* 105 */     if (t == null)
/*     */     {
/* 107 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 111 */     return t.getEncryption(false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/ConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */