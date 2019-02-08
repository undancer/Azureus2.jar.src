/*    */ package com.aelitis.azureus.core.networkmanager;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.impl.NetworkConnectionImpl;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*    */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NetworkConnectionFactory
/*    */ {
/* 35 */   private static final CopyOnWriteList<NetworkConnectionFactoryListener> listeners = new CopyOnWriteList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected static NetworkConnection create(ConnectionEndpoint target, MessageStreamEncoder encoder, MessageStreamDecoder decoder, boolean connect_with_crypto, boolean allow_fallback, byte[][] shared_secrets)
/*    */   {
/* 45 */     NetworkConnection connection = new NetworkConnectionImpl(target, encoder, decoder, connect_with_crypto, allow_fallback, shared_secrets);
/*    */     
/* 47 */     for (NetworkConnectionFactoryListener listener : listeners) {
/*    */       try
/*    */       {
/* 50 */         listener.connectionCreated(connection);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 54 */         Debug.out(e);
/*    */       }
/*    */     }
/*    */     
/* 58 */     return connection;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected static NetworkConnection create(Transport transport, MessageStreamEncoder encoder, MessageStreamDecoder decoder)
/*    */   {
/* 72 */     NetworkConnection connection = new NetworkConnectionImpl(transport, encoder, decoder);
/*    */     
/* 74 */     for (NetworkConnectionFactoryListener listener : listeners) {
/*    */       try
/*    */       {
/* 77 */         listener.connectionCreated(connection);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 81 */         Debug.out(e);
/*    */       }
/*    */     }
/*    */     
/* 85 */     return connection;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void addListener(NetworkConnectionFactoryListener l)
/*    */   {
/* 92 */     listeners.add(l);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void removeListener(NetworkConnectionFactoryListener l)
/*    */   {
/* 99 */     listeners.remove(l);
/*    */   }
/*    */   
/*    */   public static abstract interface NetworkConnectionFactoryListener
/*    */   {
/*    */     public abstract void connectionCreated(NetworkConnection paramNetworkConnection);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/NetworkConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */