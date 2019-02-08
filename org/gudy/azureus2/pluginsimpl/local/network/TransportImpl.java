/*    */ package org.gudy.azureus2.pluginsimpl.local.network;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.gudy.azureus2.plugins.network.TransportFilter;
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
/*    */ public class TransportImpl
/*    */   implements org.gudy.azureus2.plugins.network.Transport
/*    */ {
/*    */   private com.aelitis.azureus.core.networkmanager.Transport core_transport;
/*    */   private NetworkConnection core_network;
/*    */   
/*    */   public TransportImpl(NetworkConnection core_network)
/*    */   {
/* 37 */     this.core_network = core_network;
/*    */   }
/*    */   
/*    */   public TransportImpl(com.aelitis.azureus.core.networkmanager.Transport core_transport) {
/* 41 */     this.core_transport = core_transport;
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] buffers, int array_offset, int length) throws IOException {
/* 45 */     return coreTransport().read(buffers, array_offset, length);
/*    */   }
/*    */   
/*    */   public long write(ByteBuffer[] buffers, int array_offset, int length) throws IOException {
/* 49 */     return coreTransport().write(buffers, array_offset, length);
/*    */   }
/*    */   
/*    */   public com.aelitis.azureus.core.networkmanager.Transport coreTransport() throws IOException {
/* 53 */     if (this.core_transport == null) {
/* 54 */       this.core_transport = this.core_network.getTransport();
/* 55 */       if (this.core_transport == null) {
/* 56 */         throw new IOException("Not connected");
/*    */       }
/*    */     }
/* 59 */     return this.core_transport;
/*    */   }
/*    */   
/*    */   public void setFilter(TransportFilter filter) throws IOException {
/* 63 */     ((com.aelitis.azureus.core.networkmanager.impl.TransportImpl)coreTransport()).setFilter(((TransportFilterImpl)filter).filter);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/TransportImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */