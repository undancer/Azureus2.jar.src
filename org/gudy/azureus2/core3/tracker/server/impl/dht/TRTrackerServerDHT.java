/*    */ package org.gudy.azureus2.core3.tracker.server.impl.dht;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequestListener;
/*    */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerImpl;
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
/*    */ public class TRTrackerServerDHT
/*    */   extends TRTrackerServerImpl
/*    */ {
/*    */   public TRTrackerServerDHT(String _name, boolean _start_up_ready)
/*    */   {
/* 41 */     super(_name, _start_up_ready);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getHost()
/*    */   {
/* 47 */     return "dht";
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 53 */     return -1;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSSL()
/*    */   {
/* 59 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public InetAddress getBindIP()
/*    */   {
/* 65 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void addRequestListener(TRTrackerServerRequestListener l) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void removeRequestListener(TRTrackerServerRequestListener l) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void closeSupport()
/*    */   {
/* 83 */     destroySupport();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/dht/TRTrackerServerDHT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */