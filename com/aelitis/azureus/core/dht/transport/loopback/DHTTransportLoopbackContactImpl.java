/*     */ package com.aelitis.azureus.core.dht.transport.loopback;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class DHTTransportLoopbackContactImpl
/*     */   implements DHTTransportContact
/*     */ {
/*     */   private final DHTTransportLoopbackImpl transport;
/*     */   private final byte[] id;
/*     */   private int random_id;
/*     */   
/*     */   protected DHTTransportLoopbackContactImpl(DHTTransportLoopbackImpl _transport, byte[] _id)
/*     */   {
/*  50 */     this.transport = _transport;
/*  51 */     this.id = _id;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransport getTransport()
/*     */   {
/*  57 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInstanceID()
/*     */   {
/*  63 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/*  69 */     return 0;
/*     */   }
/*     */   
/*     */   public long getClockSkew()
/*     */   {
/*  74 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRandomIDType()
/*     */   {
/*  80 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRandomID()
/*     */   {
/*  86 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRandomID(int _random_id)
/*     */   {
/*  93 */     this.random_id = _random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRandomID2(byte[] id) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getRandomID2()
/*     */   {
/* 105 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 111 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSleeping()
/*     */   {
/* 117 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxFailForLiveCount()
/*     */   {
/* 123 */     return 5;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxFailForUnknownCount()
/*     */   {
/* 129 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 135 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBloomKey()
/*     */   {
/* 141 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 148 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getTransportAddress()
/*     */   {
/* 154 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getExternalAddress()
/*     */   {
/* 160 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlive(long timeout)
/*     */   {
/* 167 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void isAlive(DHTTransportReplyHandler handler, long timeout)
/*     */   {
/* 175 */     this.transport.sendPing(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void sendPing(DHTTransportReplyHandler handler)
/*     */   {
/* 182 */     this.transport.sendPing(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendImmediatePing(DHTTransportReplyHandler handler, long timeout)
/*     */   {
/* 190 */     this.transport.sendPing(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendKeyBlock(DHTTransportReplyHandler handler, byte[] request, byte[] signature)
/*     */   {
/* 199 */     this.transport.sendKeyBlock(this, handler, request, signature);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void sendStats(DHTTransportReplyHandler handler)
/*     */   {
/* 206 */     this.transport.sendStats(this, handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendStore(DHTTransportReplyHandler handler, byte[][] keys, DHTTransportValue[][] value_sets, boolean immediate)
/*     */   {
/* 216 */     this.transport.sendStore(this, handler, keys, value_sets, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendQueryStore(DHTTransportReplyHandler handler, int header_length, List<Object[]> key_details)
/*     */   {
/* 225 */     this.transport.sendQueryStore(this, handler, header_length, key_details);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindNode(DHTTransportReplyHandler handler, byte[] nid, short flags)
/*     */   {
/* 234 */     this.transport.sendFindNode(this, handler, nid);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindValue(DHTTransportReplyHandler handler, byte[] key, int max, short flags)
/*     */   {
/* 244 */     this.transport.sendFindValue(this, handler, key, max, flags);
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportFullStats getStats()
/*     */   {
/* 250 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getID()
/*     */   {
/* 256 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void exportContact(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 265 */     this.transport.exportContact(this, os);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> exportContactToMap()
/*     */   {
/* 271 */     return this.transport.exportContactToMap(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */   {
/* 277 */     this.transport.removeContact(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createNetworkPositions(boolean is_local) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTNetworkPosition[] getNetworkPositions()
/*     */   {
/* 289 */     return new DHTNetworkPosition[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTNetworkPosition getNetworkPosition(byte type)
/*     */   {
/* 296 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 302 */     return DHTLog.getString(this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/loopback/DHTTransportLoopbackContactImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */