/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.nat.NATTraversalHandler;
/*     */ import com.aelitis.azureus.core.nat.NATTraverser;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.ByteMatcher;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.RoutingListener;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamFactory;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*     */ import org.gudy.azureus2.plugins.messaging.Message;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageManagerListener;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageHandler;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageRegistration;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerListener;
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
/*     */ public class MessageManagerImpl
/*     */   implements org.gudy.azureus2.plugins.messaging.MessageManager, NATTraversalHandler
/*     */ {
/*     */   private static MessageManagerImpl instance;
/*  56 */   private final HashMap compat_checks = new HashMap();
/*     */   
/*  58 */   private final DownloadManagerListener download_manager_listener = new DownloadManagerListener() {
/*     */     public void downloadAdded(Download dwnld) {
/*  60 */       dwnld.addPeerListener(new DownloadPeerListener() {
/*     */         public void peerManagerAdded(final Download download, PeerManager peer_manager) {
/*  62 */           peer_manager.addListener(new PeerManagerListener() {
/*     */             public void peerAdded(PeerManager manager, final Peer peer) {
/*  64 */               peer.addListener(new PeerListener()
/*     */               {
/*     */                 public void stateChanged(int new_state) {
/*  67 */                   if ((new_state == 30) && 
/*  68 */                     (peer.supportsMessaging()))
/*     */                   {
/*  70 */                     Message[] messages = peer.getSupportedMessages();
/*     */                     Message msg;
/*  72 */                     Iterator it; for (int i = 0; i < messages.length; i++) {
/*  73 */                       msg = messages[i];
/*     */                       
/*  75 */                       for (it = MessageManagerImpl.this.compat_checks.entrySet().iterator(); it.hasNext();) {
/*  76 */                         Map.Entry entry = (Map.Entry)it.next();
/*  77 */                         Message message = (Message)entry.getKey();
/*     */                         
/*  79 */                         if (msg.getID().equals(message.getID())) {
/*  80 */                           MessageManagerListener listener = (MessageManagerListener)entry.getValue();
/*     */                           
/*  82 */                           listener.compatiblePeerFound(MessageManagerImpl.1.1.1.this.val$download, peer, message);
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/*     */                 public void sentBadChunk(int piece_num, int total_bad_chunks) {}
/*     */               });
/*     */             }
/*     */             
/*     */             public void peerRemoved(PeerManager manager, Peer peer)
/*     */             {
/*  95 */               for (Iterator i = MessageManagerImpl.this.compat_checks.values().iterator(); i.hasNext();) {
/*  96 */                 MessageManagerListener listener = (MessageManagerListener)i.next();
/*     */                 
/*  98 */                 listener.peerRemoved(download, peer);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void peerManagerRemoved(Download download, PeerManager peer_manager) {}
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public void downloadRemoved(Download download) {}
/*     */   };
/*     */   
/*     */   private AzureusCore core;
/*     */   
/*     */ 
/*     */   public static synchronized MessageManagerImpl getSingleton(AzureusCore core)
/*     */   {
/* 119 */     if (instance == null)
/*     */     {
/* 121 */       instance = new MessageManagerImpl(core);
/*     */     }
/*     */     
/* 124 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 129 */   private Map message_handlers = new HashMap();
/*     */   
/*     */   private MessageManagerImpl(AzureusCore _core)
/*     */   {
/* 133 */     this.core = _core;
/*     */     
/* 135 */     this.core.getNATTraverser().registerHandler(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public NATTraverser getNATTraverser()
/*     */   {
/* 141 */     return this.core.getNATTraverser();
/*     */   }
/*     */   
/*     */   public void registerMessageType(Message message) throws org.gudy.azureus2.plugins.messaging.MessageException {
/*     */     try {
/* 146 */       com.aelitis.azureus.core.peermanager.messaging.MessageManager.getSingleton().registerMessageType(new MessageAdapter(message));
/*     */     }
/*     */     catch (com.aelitis.azureus.core.peermanager.messaging.MessageException me) {
/* 149 */       throw new org.gudy.azureus2.plugins.messaging.MessageException(me.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public void deregisterMessageType(Message message) {
/* 154 */     com.aelitis.azureus.core.peermanager.messaging.MessageManager.getSingleton().deregisterMessageType(new MessageAdapter(message));
/*     */   }
/*     */   
/*     */ 
/*     */   public void locateCompatiblePeers(PluginInterface plug_interface, Message message, MessageManagerListener listener)
/*     */   {
/* 160 */     this.compat_checks.put(message, listener);
/*     */     
/* 162 */     if (this.compat_checks.size() == 1) {
/* 163 */       plug_interface.getDownloadManager().addListener(this.download_manager_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cancelCompatiblePeersLocation(MessageManagerListener orig_listener)
/*     */   {
/* 169 */     for (Iterator it = this.compat_checks.values().iterator(); it.hasNext();) {
/* 170 */       MessageManagerListener listener = (MessageManagerListener)it.next();
/*     */       
/* 172 */       if (listener == orig_listener) {
/* 173 */         it.remove();
/* 174 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GenericMessageRegistration registerGenericMessageType(String _type, final String description, final int stream_crypto, final GenericMessageHandler handler)
/*     */     throws org.gudy.azureus2.plugins.messaging.MessageException
/*     */   {
/* 188 */     final String type = "AEGEN:" + _type;
/* 189 */     final byte[] type_bytes = type.getBytes();
/*     */     
/* 191 */     final byte[][] shared_secrets = { new SHA1Simple().calculateHash(type_bytes) };
/*     */     
/* 193 */     synchronized (this.message_handlers)
/*     */     {
/* 195 */       this.message_handlers.put(type, handler);
/*     */     }
/*     */     
/* 198 */     final NetworkManager.ByteMatcher matcher = new NetworkManager.ByteMatcher()
/*     */     {
/*     */ 
/*     */       public int matchThisSizeOrBigger()
/*     */       {
/*     */ 
/* 204 */         return maxSize();
/*     */       }
/*     */       
/*     */ 
/*     */       public int maxSize()
/*     */       {
/* 210 */         return type_bytes.length;
/*     */       }
/*     */       
/*     */ 
/*     */       public int minSize()
/*     */       {
/* 216 */         return maxSize();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object matches(TransportHelper transport, ByteBuffer to_compare, int port)
/*     */       {
/* 223 */         int old_limit = to_compare.limit();
/*     */         
/* 225 */         to_compare.limit(to_compare.position() + maxSize());
/*     */         
/* 227 */         boolean matches = to_compare.equals(ByteBuffer.wrap(type_bytes));
/*     */         
/* 229 */         to_compare.limit(old_limit);
/*     */         
/* 231 */         return matches ? "" : null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object minMatches(TransportHelper transport, ByteBuffer to_compare, int port)
/*     */       {
/* 238 */         return matches(transport, to_compare, port);
/*     */       }
/*     */       
/*     */ 
/*     */       public byte[][] getSharedSecrets()
/*     */       {
/* 244 */         return shared_secrets;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSpecificPort()
/*     */       {
/* 250 */         return -1;
/*     */       }
/*     */       
/* 253 */     };
/* 254 */     NetworkManager.getSingleton().requestIncomingConnectionRouting(matcher, new NetworkManager.RoutingListener()
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
/* 306 */       new MessageStreamFactory
/*     */       {
/*     */ 
/*     */         public void connectionRouted(NetworkConnection connection, Object routing_data)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 263 */             ByteBuffer[] skip_buffer = { ByteBuffer.allocate(type_bytes.length) };
/*     */             
/* 265 */             connection.getTransport().read(skip_buffer, 0, 1);
/*     */             
/* 267 */             if (skip_buffer[0].remaining() != 0)
/*     */             {
/* 269 */               Debug.out("incomplete read");
/*     */             }
/*     */             
/* 272 */             GenericMessageEndpointImpl endpoint = new GenericMessageEndpointImpl(connection.getEndpoint());
/*     */             
/* 274 */             GenericMessageConnectionDirect direct_connection = GenericMessageConnectionDirect.receive(endpoint, type, description, stream_crypto, shared_secrets);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 282 */             GenericMessageConnectionImpl new_connection = new GenericMessageConnectionImpl(MessageManagerImpl.this, direct_connection);
/*     */             
/* 284 */             direct_connection.connect(connection);
/*     */             
/* 286 */             if (handler.accept(new_connection))
/*     */             {
/* 288 */               new_connection.accepted();
/*     */             }
/*     */             else
/*     */             {
/* 292 */               connection.close("connection not accepted");
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 297 */             Debug.printStackTrace(e);
/*     */             
/* 299 */             connection.close(e == null ? null : Debug.getNestedExceptionMessage(e));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 306 */         public boolean autoCryptoFallback() { return stream_crypto != 3; } }, new MessageStreamFactory()
/*     */       {
/*     */ 
/*     */         public MessageStreamEncoder createEncoder() {
/* 310 */           return new GenericMessageEncoder(); }
/* 311 */         public MessageStreamDecoder createDecoder() { return new GenericMessageDecoder(type, description);
/*     */         }
/* 313 */       });
/* 314 */     new GenericMessageRegistration()
/*     */     {
/*     */ 
/*     */ 
/*     */       public GenericMessageEndpoint createEndpoint(InetSocketAddress notional_target)
/*     */       {
/*     */ 
/* 321 */         return new GenericMessageEndpointImpl(notional_target);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public GenericMessageConnection createConnection(GenericMessageEndpoint endpoint)
/*     */         throws org.gudy.azureus2.plugins.messaging.MessageException
/*     */       {
/* 330 */         return new GenericMessageConnectionImpl(MessageManagerImpl.this, type, description, (GenericMessageEndpointImpl)endpoint, stream_crypto, shared_secrets);
/*     */       }
/*     */       
/*     */ 
/*     */       public void cancel()
/*     */       {
/* 336 */         NetworkManager.getSingleton().cancelIncomingConnectionRouting(matcher);
/*     */         
/* 338 */         synchronized (MessageManagerImpl.this.message_handlers)
/*     */         {
/* 340 */           MessageManagerImpl.this.message_handlers.remove(type);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected GenericMessageHandler getHandler(String type)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 217	org/gudy/azureus2/pluginsimpl/local/messaging/MessageManagerImpl:message_handlers	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 217	org/gudy/azureus2/pluginsimpl/local/messaging/MessageManagerImpl:message_handlers	Ljava/util/Map;
/*     */     //   11: aload_1
/*     */     //   12: invokeinterface 252 2 0
/*     */     //   17: checkcast 123	org/gudy/azureus2/plugins/messaging/generic/GenericMessageHandler
/*     */     //   20: aload_2
/*     */     //   21: monitorexit
/*     */     //   22: areturn
/*     */     //   23: astore_3
/*     */     //   24: aload_2
/*     */     //   25: monitorexit
/*     */     //   26: aload_3
/*     */     //   27: athrow
/*     */     // Line number table:
/*     */     //   Java source line #351	-> byte code offset #0
/*     */     //   Java source line #353	-> byte code offset #7
/*     */     //   Java source line #354	-> byte code offset #23
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	28	0	this	MessageManagerImpl
/*     */     //   0	28	1	type	String
/*     */     //   5	20	2	Ljava/lang/Object;	Object
/*     */     //   23	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	22	23	finally
/*     */     //   23	26	23	finally
/*     */   }
/*     */   
/*     */   public int getType()
/*     */   {
/* 363 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 369 */     return "Generic Messaging";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map process(InetSocketAddress originator, Map message)
/*     */   {
/* 377 */     return GenericMessageConnectionIndirect.receive(this, originator, message);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/MessageManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */