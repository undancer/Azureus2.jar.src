/*    */ package com.aelitis.azureus.core.nat;
/*    */ 
/*    */ import java.net.InetSocketAddress;
/*    */ import java.util.Map;
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
/*    */ public abstract interface NATTraversalObserver
/*    */ {
/*    */   public static final int FT_NO_RENDEZVOUS = 1;
/*    */   public static final int FT_QUEUE_FULL = 2;
/*    */   public static final int FT_CANCELLED = 3;
/* 32 */   public static final String[] FT_STRINGS = { "Unknown", "No rendezvous", "Queue full", "Operation cancelled" };
/*    */   
/*    */   public abstract void succeeded(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2, Map paramMap);
/*    */   
/*    */   public abstract void failed(int paramInt);
/*    */   
/*    */   public abstract void failed(Throwable paramThrowable);
/*    */   
/*    */   public abstract void disabled();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/nat/NATTraversalObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */