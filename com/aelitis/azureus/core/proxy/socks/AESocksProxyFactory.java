/*    */ package com.aelitis.azureus.core.proxy.socks;
/*    */ 
/*    */ import com.aelitis.azureus.core.proxy.AEProxyException;
/*    */ import com.aelitis.azureus.core.proxy.socks.impl.AESocksProxyImpl;
/*    */ import com.aelitis.azureus.core.proxy.socks.impl.AESocksProxyPlugableConnectionDefault;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AESocksProxyFactory
/*    */ {
/*    */   public static AESocksProxy create(int port, long connect_timeout, long read_timeout)
/*    */     throws AEProxyException
/*    */   {
/* 41 */     create(port, connect_timeout, read_timeout, new AESocksProxyPlugableConnectionFactory()
/*    */     {
/*    */ 
/*    */ 
/*    */ 
/*    */       public AESocksProxyPlugableConnection create(AESocksProxyConnection connection)
/*    */       {
/*    */ 
/*    */ 
/* 50 */         return new AESocksProxyPlugableConnectionDefault(connection);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static AESocksProxy create(int port, long connect_timeout, long read_timeout, AESocksProxyPlugableConnectionFactory connection_factory)
/*    */     throws AEProxyException
/*    */   {
/* 64 */     return new AESocksProxyImpl(port, connect_timeout, read_timeout, connection_factory);
/*    */   }
/*    */   
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try
/*    */     {
/* 72 */       AESocksProxy proxy = create(1080, 30000L, 30000L);
/*    */       
/* 74 */       Thread.sleep(864000000L);
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 78 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */