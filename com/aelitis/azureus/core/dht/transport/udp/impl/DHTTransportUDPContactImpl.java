/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPositionProvider;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDPContact;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler.RunStateChangeListener;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class DHTTransportUDPContactImpl
/*     */   implements DHTTransportUDPContact
/*     */ {
/*     */   public static final int NODE_STATUS_UNKNOWN = -1;
/*     */   public static final int NODE_STATUS_ROUTABLE = 1;
/*     */   final DHTTransportUDPImpl transport;
/*     */   private InetSocketAddress external_address;
/*     */   private InetSocketAddress transport_address;
/*     */   private byte[] id;
/*     */   private byte protocol_version;
/*     */   private int instance_id;
/*     */   private final long skew;
/*     */   private byte generic_flags;
/*     */   private int random_id;
/*     */   
/*     */   static
/*     */   {
/*  54 */     AERunStateHandler.addListener(new AERunStateHandler.RunStateChangeListener()
/*     */     {
/*     */ 
/*  57 */       private VivaldiPositionProvider provider = null;
/*     */       
/*     */ 
/*     */ 
/*     */       public void runStateChanged(long run_state)
/*     */       {
/*  63 */         synchronized (this)
/*     */         {
/*  65 */           if (AERunStateHandler.isDHTSleeping())
/*     */           {
/*  67 */             if (this.provider != null)
/*     */             {
/*  69 */               DHTNetworkPositionManager.unregisterProvider(this.provider);
/*     */               
/*  71 */               this.provider = null;
/*     */             }
/*     */             
/*     */           }
/*  75 */           else if (this.provider == null)
/*     */           {
/*  77 */             this.provider = new VivaldiPositionProvider();
/*     */             
/*  79 */             DHTNetworkPositionManager.registerProvider(this.provider); } } } }, true);
/*     */   }
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
/*  99 */   private int node_status = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DHTNetworkPosition[] network_positions;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTTransportUDPContactImpl(boolean _is_local, DHTTransportUDPImpl _transport, InetSocketAddress _transport_address, InetSocketAddress _external_address, byte _protocol_version, int _instance_id, long _skew, byte _generic_flags)
/*     */     throws DHTTransportException
/*     */   {
/* 116 */     this.transport = _transport;
/* 117 */     this.transport_address = _transport_address;
/* 118 */     this.external_address = _external_address;
/* 119 */     this.protocol_version = _protocol_version;
/*     */     
/* 121 */     if (this.transport_address.equals(this.external_address))
/*     */     {
/* 123 */       this.external_address = this.transport_address;
/*     */     }
/*     */     
/* 126 */     this.instance_id = _instance_id;
/* 127 */     this.skew = _skew;
/* 128 */     this.generic_flags = _generic_flags;
/*     */     
/* 130 */     if ((this.transport_address == this.external_address) || (this.transport_address.getAddress().equals(this.external_address.getAddress())))
/*     */     {
/*     */ 
/* 133 */       this.id = DHTUDPUtils.getNodeID(this.external_address, this.protocol_version);
/*     */     }
/*     */     
/* 136 */     createNetworkPositions(_is_local);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransport getTransport()
/*     */   {
/* 142 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/* 148 */     return this.protocol_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setProtocolVersion(byte v)
/*     */   {
/* 155 */     this.protocol_version = v;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getClockSkew()
/*     */   {
/* 161 */     return this.skew;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRandomIDType()
/*     */   {
/* 167 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRandomID(int _random_id)
/*     */   {
/* 174 */     this.random_id = _random_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRandomID()
/*     */   {
/* 180 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getRandomID2()
/*     */   {
/* 192 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getNodeStatus()
/*     */   {
/* 198 */     return this.node_status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNodeStatus(int ns)
/*     */   {
/* 205 */     this.node_status = ns;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 211 */     return (addressMatchesID()) && (!this.transport.invalidExternalAddress(this.external_address.getAddress()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSleeping()
/*     */   {
/* 218 */     return (this.generic_flags & 0x1) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setGenericFlags(byte flags)
/*     */   {
/* 225 */     this.generic_flags = flags;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean addressMatchesID()
/*     */   {
/* 231 */     return this.id != null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getTransportAddress()
/*     */   {
/* 237 */     return this.transport_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTransportAddress(InetSocketAddress address)
/*     */   {
/* 244 */     this.transport_address = address;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getExternalAddress()
/*     */   {
/* 250 */     return this.external_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 256 */     return DHTLog.getString2(this.id);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBloomKey()
/*     */   {
/* 262 */     return getAddress().getAddress().getAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 268 */     return getExternalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxFailForLiveCount()
/*     */   {
/* 274 */     return this.transport.getMaxFailForLiveCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxFailForUnknownCount()
/*     */   {
/* 280 */     return this.transport.getMaxFailForUnknownCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInstanceID()
/*     */   {
/* 286 */     return this.instance_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstanceIDAndVersion(int _instance_id, byte _protocol_version)
/*     */   {
/* 294 */     this.instance_id = _instance_id;
/*     */     
/*     */ 
/*     */ 
/* 298 */     if (_protocol_version > this.protocol_version)
/*     */     {
/* 300 */       this.protocol_version = _protocol_version;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlive(long timeout)
/*     */   {
/* 308 */     final AESemaphore sem = new AESemaphore("DHTTransportContact:alive");
/*     */     
/* 310 */     final boolean[] alive = { false };
/*     */     try
/*     */     {
/* 313 */       sendPing(new DHTTransportReplyHandlerAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void pingReply(DHTTransportContact contact)
/*     */         {
/*     */ 
/* 320 */           alive[0] = true;
/*     */           
/* 322 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void failed(DHTTransportContact contact, Throwable cause)
/*     */         {
/* 330 */           sem.release();
/*     */         }
/*     */         
/* 333 */       });
/* 334 */       sem.reserve(timeout);
/*     */       
/* 336 */       return alive[0];
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 340 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void isAlive(DHTTransportReplyHandler handler, long timeout)
/*     */   {
/* 349 */     this.transport.sendPing(this, handler, timeout, 99);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void sendPing(DHTTransportReplyHandler handler)
/*     */   {
/* 356 */     this.transport.sendPing(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendImmediatePing(DHTTransportReplyHandler handler, long timeout)
/*     */   {
/* 364 */     this.transport.sendImmediatePing(this, handler, timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void sendStats(DHTTransportReplyHandler handler)
/*     */   {
/* 371 */     this.transport.sendStats(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendStore(DHTTransportReplyHandler handler, byte[][] keys, DHTTransportValue[][] value_sets, boolean immediate)
/*     */   {
/* 381 */     this.transport.sendStore(this, handler, keys, value_sets, immediate ? 99 : 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendQueryStore(DHTTransportReplyHandler handler, int header_length, List<Object[]> key_details)
/*     */   {
/* 392 */     this.transport.sendQueryStore(this, handler, header_length, key_details);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindNode(DHTTransportReplyHandler handler, byte[] nid, short flags)
/*     */   {
/* 401 */     this.transport.sendFindNode(this, handler, nid);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindValue(DHTTransportReplyHandler handler, byte[] key, int max_values, short flags)
/*     */   {
/* 411 */     this.transport.sendFindValue(this, handler, key, max_values, flags);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendKeyBlock(final DHTTransportReplyHandler handler, final byte[] request, final byte[] signature)
/*     */   {
/* 422 */     sendFindNode(new DHTTransportReplyHandlerAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void findNodeReply(DHTTransportContact contact, DHTTransportContact[] contacts)
/*     */       {
/*     */ 
/*     */ 
/* 430 */         DHTTransportUDPContactImpl.this.transport.sendKeyBlockRequest(DHTTransportUDPContactImpl.this, handler, request, signature);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 437 */       public void failed(DHTTransportContact _contact, Throwable _error) { handler.failed(_contact, _error); } }, new byte[0], (short)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportFullStats getStats()
/*     */   {
/* 448 */     return this.transport.getFullStats(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/* 454 */     if (this.id == null)
/*     */     {
/* 456 */       throw new RuntimeException("Invalid contact");
/*     */     }
/*     */     
/* 459 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void exportContact(DataOutputStream os)
/*     */     throws IOException, DHTTransportException
/*     */   {
/* 468 */     this.transport.exportContact(this, os);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> exportContactToMap()
/*     */   {
/* 474 */     return this.transport.exportContactToMap(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */   {
/* 480 */     this.transport.removeContact(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNetworkPositions(DHTNetworkPosition[] positions)
/*     */   {
/* 487 */     this.network_positions = positions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void createNetworkPositions(boolean is_local)
/*     */   {
/* 494 */     this.network_positions = DHTNetworkPositionManager.createPositions(this.id == null ? DHTUDPUtils.getBogusNodeID() : this.id, is_local);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTNetworkPosition[] getNetworkPositions()
/*     */   {
/* 500 */     return this.network_positions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTNetworkPosition getNetworkPosition(byte position_type)
/*     */   {
/* 507 */     for (int i = 0; i < this.network_positions.length; i++)
/*     */     {
/* 509 */       if (this.network_positions[i].getPositionType() == position_type)
/*     */       {
/* 511 */         return this.network_positions[i];
/*     */       }
/*     */     }
/*     */     
/* 515 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 521 */     if (this.transport_address.equals(this.external_address))
/*     */     {
/* 523 */       return DHTLog.getString2(this.id) + "[" + this.transport_address.toString() + ",V" + getProtocolVersion() + "]";
/*     */     }
/*     */     
/* 526 */     return DHTLog.getString2(this.id) + "[tran=" + this.transport_address.toString() + ",ext=" + this.external_address + ",V" + getProtocolVersion() + "]";
/*     */   }
/*     */   
/*     */   public void setRandomID2(byte[] id) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportUDPContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */