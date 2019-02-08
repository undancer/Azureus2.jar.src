/*     */ package com.aelitis.azureus.core.clientmessageservice.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZGenericMapPayload;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ public class NonBlockingReadWriteService
/*     */ {
/*     */   private final VirtualChannelSelector read_selector;
/*     */   private final VirtualChannelSelector write_selector;
/*  39 */   private final ArrayList connections = new ArrayList();
/*  40 */   private final AEMonitor connections_mon = new AEMonitor("connections");
/*     */   
/*     */   private final ServiceListener listener;
/*     */   
/*     */   private final String service_name;
/*     */   private volatile boolean destroyed;
/*  46 */   private long last_timeout_check_time = 0L;
/*     */   private static final int TIMEOUT_CHECK_INTERVAL_MS = 10000;
/*     */   private final int activity_timeout_period_ms;
/*     */   private final int close_delay_period_ms;
/*     */   
/*     */   public NonBlockingReadWriteService(String _service_name, int timeout, ServiceListener _listener)
/*     */   {
/*  53 */     this(_service_name, timeout, 0, _listener);
/*     */   }
/*     */   
/*     */   public NonBlockingReadWriteService(String _service_name, int timeout, int close_delay, ServiceListener _listener) {
/*  57 */     this.service_name = _service_name;
/*  58 */     this.listener = _listener;
/*     */     
/*  60 */     this.read_selector = new VirtualChannelSelector(this.service_name, 1, false);
/*  61 */     this.write_selector = new VirtualChannelSelector(this.service_name, 4, true);
/*     */     
/*  63 */     if (timeout < 10) timeout = 10;
/*  64 */     this.activity_timeout_period_ms = (timeout * 1000);
/*  65 */     this.close_delay_period_ms = (close_delay * 1000);
/*     */     
/*  67 */     new AEThread2("[" + this.service_name + "] Service Select", true)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         for (;;) {
/*  72 */           boolean stop_after_select = NonBlockingReadWriteService.this.destroyed;
/*     */           
/*  74 */           if (stop_after_select) {
/*  75 */             NonBlockingReadWriteService.this.read_selector.destroy();
/*  76 */             NonBlockingReadWriteService.this.write_selector.destroy();
/*     */           }
/*     */           try
/*     */           {
/*  80 */             NonBlockingReadWriteService.this.read_selector.select(50L);
/*  81 */             NonBlockingReadWriteService.this.write_selector.select(50L);
/*     */           }
/*     */           catch (Throwable t) {
/*  84 */             Debug.out("[" + NonBlockingReadWriteService.this.service_name + "] SelectorLoop() EXCEPTION: ", t);
/*     */           }
/*     */           
/*  87 */           if (stop_after_select) {
/*     */             break;
/*     */           }
/*     */           
/*  91 */           NonBlockingReadWriteService.this.doConnectionTimeoutChecks();
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 106 */       this.connections_mon.enter();
/*     */       
/* 108 */       this.connections.clear();
/*     */       
/* 110 */       this.destroyed = true;
/*     */     }
/*     */     finally {
/* 113 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addClientConnection(ClientConnection connection)
/*     */   {
/*     */     try
/*     */     {
/* 122 */       this.connections_mon.enter();
/*     */       
/* 124 */       if (this.destroyed)
/*     */       {
/* 126 */         Debug.out("connection added after destroy");
/*     */       }
/*     */       
/* 129 */       this.connections.add(connection);
/*     */     } finally {
/* 131 */       this.connections_mon.exit();
/*     */     }
/*     */     
/* 134 */     registerForSelection(connection);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeClientConnection(ClientConnection connection)
/*     */   {
/* 140 */     this.read_selector.cancel(connection.getSocketChannel());
/* 141 */     this.write_selector.cancel(connection.getSocketChannel());
/*     */     try
/*     */     {
/* 144 */       this.connections_mon.enter();
/* 145 */       this.connections.remove(connection);
/*     */     } finally {
/* 147 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void registerForSelection(final ClientConnection client)
/*     */   {
/* 156 */     VirtualChannelSelector.VirtualSelectorListener read_listener = new VirtualChannelSelector.VirtualSelectorListener()
/*     */     {
/*     */       public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment) {
/*     */         try {
/* 160 */           Message[] messages = client.readMessages();
/*     */           
/* 162 */           if (messages != null) {
/* 163 */             for (int i = 0; i < messages.length; i++) {
/* 164 */               AZGenericMapPayload msg = (AZGenericMapPayload)messages[i];
/* 165 */               ClientMessage client_msg = new ClientMessage(msg.getID(), client, msg.getMapPayload(), null);
/* 166 */               NonBlockingReadWriteService.this.listener.messageReceived(client_msg);
/*     */             }
/*     */           }
/*     */           
/* 170 */           return client.getLastReadMadeProgress();
/*     */         }
/*     */         catch (Throwable t) {
/* 173 */           if (!client.isClosePending()) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 178 */           NonBlockingReadWriteService.this.listener.connectionError(client, t); }
/* 179 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */       public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */       {
/* 185 */         if (!NonBlockingReadWriteService.this.destroyed) {
/* 186 */           msg.printStackTrace();
/*     */         }
/* 188 */         NonBlockingReadWriteService.this.listener.connectionError(client, msg);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 193 */     };
/* 194 */     VirtualChannelSelector.VirtualSelectorListener write_listener = new VirtualChannelSelector.VirtualSelectorListener() {
/*     */       public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment) {
/*     */         try {
/* 197 */           boolean more_writes_needed = client.writeMessages();
/*     */           
/* 199 */           if (more_writes_needed) {
/* 200 */             NonBlockingReadWriteService.this.write_selector.resumeSelects(client.getSocketChannel());
/*     */           }
/*     */           
/* 203 */           return client.getLastWriteMadeProgress();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 207 */           NonBlockingReadWriteService.this.listener.connectionError(client, t); }
/* 208 */         return false;
/*     */       }
/*     */       
/*     */       public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */       {
/* 213 */         if (!NonBlockingReadWriteService.this.destroyed) {
/* 214 */           msg.printStackTrace();
/*     */         }
/* 216 */         NonBlockingReadWriteService.this.listener.connectionError(client, msg);
/*     */       }
/*     */       
/* 219 */     };
/* 220 */     this.write_selector.register(client.getSocketChannel(), write_listener, null);
/* 221 */     this.write_selector.pauseSelects(client.getSocketChannel());
/*     */     
/* 223 */     this.read_selector.register(client.getSocketChannel(), read_listener, null);
/*     */   }
/*     */   
/*     */ 
/*     */   private void doConnectionTimeoutChecks()
/*     */   {
/* 229 */     long time = System.currentTimeMillis();
/* 230 */     if ((time < this.last_timeout_check_time) || (time - this.last_timeout_check_time > 10000L)) {
/* 231 */       ArrayList timed_out = new ArrayList();
/*     */       try {
/* 233 */         this.connections_mon.enter();
/* 234 */         long current_time = System.currentTimeMillis();
/*     */         
/* 236 */         for (int i = 0; i < this.connections.size(); i++) {
/* 237 */           ClientConnection vconn = (ClientConnection)this.connections.get(i);
/*     */           
/* 239 */           if (current_time < vconn.getLastActivityTime()) {
/* 240 */             vconn.resetLastActivityTime();
/*     */ 
/*     */           }
/* 243 */           else if ((current_time - vconn.getLastActivityTime() > this.activity_timeout_period_ms) || ((this.close_delay_period_ms > 0) && (current_time - vconn.getLastActivityTime() > this.close_delay_period_ms)))
/*     */           {
/*     */ 
/*     */ 
/* 247 */             timed_out.add(vconn);
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 252 */         this.connections_mon.exit();
/*     */       }
/* 254 */       for (int i = 0; i < timed_out.size(); i++) {
/* 255 */         ClientConnection vconn = (ClientConnection)timed_out.get(i);
/*     */         
/* 257 */         this.listener.connectionError(vconn, new Exception("Timeout"));
/*     */       }
/*     */       
/* 260 */       this.last_timeout_check_time = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendMessage(ClientMessage message)
/*     */   {
/* 269 */     ClientConnection vconn = message.getClient();
/*     */     boolean still_connected;
/*     */     try
/*     */     {
/* 273 */       this.connections_mon.enter();
/* 274 */       still_connected = this.connections.contains(vconn);
/*     */     } finally {
/* 276 */       this.connections_mon.exit();
/*     */     }
/* 278 */     if (!still_connected)
/*     */     {
/* 280 */       message.reportFailed(new Exception("No longer connected"));
/*     */       
/* 282 */       return;
/*     */     }
/*     */     
/* 285 */     Object reply = new AZGenericMapPayload(message.getMessageID(), message.getPayload(), (byte)1);
/*     */     
/* 287 */     vconn.sendMessage(message, (Message)reply);
/*     */     
/* 289 */     this.write_selector.resumeSelects(vconn.getSocketChannel());
/*     */   }
/*     */   
/*     */   public static abstract interface ServiceListener
/*     */   {
/*     */     public abstract void messageReceived(ClientMessage paramClientMessage);
/*     */     
/*     */     public abstract void connectionError(ClientConnection paramClientConnection, Throwable paramThrowable);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/impl/NonBlockingReadWriteService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */