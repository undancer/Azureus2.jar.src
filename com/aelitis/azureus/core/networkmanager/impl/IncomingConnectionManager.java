/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager.ByteMatcher;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IncomingConnectionManager
/*     */ {
/*  35 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*  37 */   private static final IncomingConnectionManager singleton = new IncomingConnectionManager();
/*     */   
/*     */ 
/*     */   public static IncomingConnectionManager getSingleton()
/*     */   {
/*  42 */     return singleton;
/*     */   }
/*     */   
/*  45 */   private volatile Map match_buffers_cow = new HashMap();
/*  46 */   private final AEMonitor match_buffers_mon = new AEMonitor("IncomingConnectionManager:match");
/*  47 */   private int max_match_buffer_size = 0;
/*  48 */   private int max_min_match_buffer_size = 0;
/*     */   
/*     */ 
/*  51 */   private final ArrayList connections = new ArrayList();
/*  52 */   private final AEMonitor connections_mon = new AEMonitor("IncomingConnectionManager:conns");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected IncomingConnectionManager()
/*     */   {
/*  59 */     SimpleTimer.addPeriodicEvent("IncomingConnectionManager:timeouts", 5000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */ 
/*  65 */         IncomingConnectionManager.this.doTimeoutChecks();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  76 */     return this.match_buffers_cow.isEmpty();
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
/*     */   public Object[] checkForMatch(TransportHelper transport, int incoming_port, ByteBuffer to_check, boolean min_match)
/*     */   {
/*  89 */     int orig_position = to_check.position();
/*  90 */     int orig_limit = to_check.limit();
/*     */     
/*     */ 
/*  93 */     to_check.position(0);
/*     */     
/*  95 */     MatchListener listener = null;
/*  96 */     Object routing_data = null;
/*     */     
/*  98 */     for (Iterator i = this.match_buffers_cow.entrySet().iterator(); (i.hasNext()) && (!transport.isClosed());) {
/*  99 */       Map.Entry entry = (Map.Entry)i.next();
/* 100 */       NetworkManager.ByteMatcher bm = (NetworkManager.ByteMatcher)entry.getKey();
/* 101 */       MatchListener this_listener = (MatchListener)entry.getValue();
/*     */       
/* 103 */       int specific_port = bm.getSpecificPort();
/*     */       
/* 105 */       if ((specific_port == -1) || (specific_port == incoming_port))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 110 */         if (min_match) {
/* 111 */           if (orig_position >= bm.minSize())
/*     */           {
/*     */ 
/*     */ 
/* 115 */             routing_data = bm.minMatches(transport, to_check, incoming_port);
/*     */             
/* 117 */             if (routing_data != null) {
/* 118 */               listener = this_listener;
/* 119 */               break;
/*     */             }
/*     */           }
/* 122 */         } else if (orig_position >= bm.matchThisSizeOrBigger())
/*     */         {
/*     */ 
/*     */ 
/* 126 */           routing_data = bm.matches(transport, to_check, incoming_port);
/*     */           
/* 128 */           if (routing_data != null) {
/* 129 */             listener = this_listener;
/* 130 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 136 */     to_check.position(orig_position);
/* 137 */     to_check.limit(orig_limit);
/*     */     
/* 139 */     if (listener == null)
/*     */     {
/* 141 */       return null;
/*     */     }
/*     */     
/* 144 */     return new Object[] { listener, routing_data };
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
/*     */   public void registerMatchBytes(NetworkManager.ByteMatcher matcher, MatchListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 160 */       this.match_buffers_mon.enter();
/*     */       
/* 162 */       if (matcher.maxSize() > this.max_match_buffer_size) {
/* 163 */         this.max_match_buffer_size = matcher.maxSize();
/*     */       }
/*     */       
/* 166 */       if (matcher.minSize() > this.max_min_match_buffer_size) {
/* 167 */         this.max_min_match_buffer_size = matcher.minSize();
/*     */       }
/*     */       
/* 170 */       Map new_match_buffers = new HashMap(this.match_buffers_cow);
/*     */       
/* 172 */       new_match_buffers.put(matcher, listener);
/*     */       
/* 174 */       this.match_buffers_cow = new_match_buffers;
/*     */       
/* 176 */       addSharedSecrets(matcher.getSharedSecrets());
/*     */     }
/*     */     finally {
/* 179 */       this.match_buffers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deregisterMatchBytes(NetworkManager.ByteMatcher to_remove)
/*     */   {
/*     */     try
/*     */     {
/* 193 */       this.match_buffers_mon.enter();
/* 194 */       Map new_match_buffers = new HashMap(this.match_buffers_cow);
/*     */       
/* 196 */       new_match_buffers.remove(to_remove);
/*     */       Iterator i;
/* 198 */       if (to_remove.maxSize() == this.max_match_buffer_size) {
/* 199 */         this.max_match_buffer_size = 0;
/* 200 */         for (i = new_match_buffers.keySet().iterator(); i.hasNext();) {
/* 201 */           NetworkManager.ByteMatcher bm = (NetworkManager.ByteMatcher)i.next();
/* 202 */           if (bm.maxSize() > this.max_match_buffer_size) {
/* 203 */             this.max_match_buffer_size = bm.maxSize();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 208 */       this.match_buffers_cow = new_match_buffers;
/*     */       
/* 210 */       removeSharedSecrets(to_remove.getSharedSecrets());
/*     */     } finally {
/* 212 */       this.match_buffers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addSharedSecrets(byte[][] secrets)
/*     */   {
/* 219 */     if (secrets != null)
/*     */     {
/* 221 */       ProtocolDecoder.addSecrets(secrets);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeSharedSecrets(byte[][] secrets)
/*     */   {
/* 229 */     if (secrets != null)
/*     */     {
/* 231 */       ProtocolDecoder.removeSecrets(secrets);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxMatchBufferSize()
/*     */   {
/* 238 */     return this.max_match_buffer_size;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxMinMatchBufferSize()
/*     */   {
/* 244 */     return this.max_min_match_buffer_size;
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
/*     */   public void addConnection(int local_port, TransportHelperFilter filter, Transport new_transport)
/*     */   {
/* 258 */     TransportHelper transport_helper = filter.getHelper();
/*     */     
/* 260 */     if (isEmpty())
/*     */     {
/* 262 */       if (Logger.isEnabled())
/*     */       {
/* 264 */         Logger.log(new LogEvent(LOGID, "Incoming connection from [" + transport_helper.getAddress() + "] dropped because zero routing handlers registered"));
/*     */       }
/*     */       
/*     */ 
/* 268 */       transport_helper.close("No routing handler");
/*     */       
/* 270 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 277 */     IncomingConnection ic = new IncomingConnection(filter, getMaxMatchBufferSize());
/*     */     
/* 279 */     TransportHelper.selectListener sel_listener = new SelectorListener(local_port, new_transport);
/*     */     try
/*     */     {
/* 282 */       this.connections_mon.enter();
/*     */       
/* 284 */       this.connections.add(ic);
/*     */       
/* 286 */       transport_helper.registerForReadSelects(sel_listener, ic);
/*     */     }
/*     */     finally
/*     */     {
/* 290 */       this.connections_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 295 */     sel_listener.selectSuccess(transport_helper, ic);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeConnection(IncomingConnection connection, boolean close_as_well, String reason)
/*     */   {
/*     */     try
/*     */     {
/* 306 */       this.connections_mon.enter();
/*     */       
/* 308 */       connection.filter.getHelper().cancelReadSelects();
/*     */       
/* 310 */       this.connections.remove(connection);
/*     */     }
/*     */     finally
/*     */     {
/* 314 */       this.connections_mon.exit();
/*     */     }
/*     */     
/* 317 */     if (close_as_well)
/*     */     {
/* 319 */       connection.filter.getHelper().close("Tidy close" + ((reason == null) || (reason.length() == 0) ? "" : new StringBuilder().append(": ").append(reason).toString()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doTimeoutChecks()
/*     */   {
/*     */     try
/*     */     {
/* 328 */       this.connections_mon.enter();
/*     */       
/* 330 */       ArrayList to_close = null;
/*     */       
/* 332 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 334 */       for (int i = 0; i < this.connections.size(); i++)
/*     */       {
/* 336 */         IncomingConnection ic = (IncomingConnection)this.connections.get(i);
/*     */         
/* 338 */         TransportHelper transport_helper = ic.filter.getHelper();
/*     */         
/* 340 */         if (ic.last_read_time > 0L) {
/* 341 */           if (now < ic.last_read_time) {
/* 342 */             ic.last_read_time = now;
/*     */           }
/* 344 */           else if (now - ic.last_read_time > transport_helper.getReadTimeout()) {
/* 345 */             if (Logger.isEnabled()) {
/* 346 */               Logger.log(new LogEvent(LOGID, "Incoming connection [" + transport_helper.getAddress() + "] forcibly timed out due to socket read inactivity [" + ic.buffer.position() + " bytes read: " + new String(ic.buffer.array()) + "]"));
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 351 */             if (to_close == null) to_close = new ArrayList();
/* 352 */             to_close.add(ic);
/*     */           }
/*     */           
/*     */         }
/* 356 */         else if (now < ic.initial_connect_time) {
/* 357 */           ic.initial_connect_time = now;
/*     */         }
/* 359 */         else if (now - ic.initial_connect_time > transport_helper.getConnectTimeout()) {
/* 360 */           if (Logger.isEnabled()) {
/* 361 */             Logger.log(new LogEvent(LOGID, "Incoming connection [" + transport_helper.getAddress() + "] forcibly timed out after " + "60sec due to socket inactivity"));
/*     */           }
/*     */           
/* 364 */           if (to_close == null) to_close = new ArrayList();
/* 365 */           to_close.add(ic);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 370 */       if (to_close != null) {
/* 371 */         for (int i = 0; i < to_close.size(); i++) {
/* 372 */           IncomingConnection ic = (IncomingConnection)to_close.get(i);
/* 373 */           removeConnection(ic, true, "incoming connection routing timeout");
/*     */         }
/*     */       }
/*     */     } finally {
/* 377 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class IncomingConnection
/*     */   {
/*     */     protected final TransportHelperFilter filter;
/*     */     
/*     */     protected final ByteBuffer buffer;
/*     */     
/*     */     protected long initial_connect_time;
/*     */     
/* 391 */     protected long last_read_time = -1L;
/*     */     
/*     */ 
/*     */ 
/*     */     protected IncomingConnection(TransportHelperFilter filter, int buff_size)
/*     */     {
/* 397 */       this.filter = filter;
/* 398 */       this.buffer = ByteBuffer.allocate(buff_size);
/* 399 */       this.initial_connect_time = SystemTime.getCurrentTime();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface MatchListener
/*     */   {
/*     */     public abstract boolean autoCryptoFallback();
/*     */     
/*     */     public abstract void connectionMatched(Transport paramTransport, Object paramObject);
/*     */   }
/*     */   
/*     */   protected class SelectorListener implements TransportHelper.selectListener
/*     */   {
/*     */     private final int local_port;
/*     */     private final Transport transport;
/*     */     
/*     */     protected SelectorListener(int _local_port, Transport _transport) {
/* 416 */       this.local_port = _local_port;
/* 417 */       this.transport = _transport;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean selectSuccess(TransportHelper transport_helper, Object attachment)
/*     */     {
/* 424 */       IncomingConnectionManager.IncomingConnection ic = (IncomingConnectionManager.IncomingConnection)attachment;
/*     */       try
/*     */       {
/* 427 */         long bytes_read = ic.filter.read(new ByteBuffer[] { ic.buffer }, 0, 1);
/*     */         
/* 429 */         if (bytes_read < 0L) {
/* 430 */           throw new IOException("end of stream on socket read");
/*     */         }
/*     */         
/* 433 */         if (bytes_read == 0L) {
/* 434 */           return false;
/*     */         }
/*     */         
/* 437 */         ic.last_read_time = SystemTime.getCurrentTime();
/*     */         
/* 439 */         Object[] match_data = IncomingConnectionManager.this.checkForMatch(transport_helper, this.local_port, ic.buffer, false);
/*     */         
/* 441 */         if (match_data == null) {
/* 442 */           if ((transport_helper.isClosed()) || (ic.buffer.position() >= IncomingConnectionManager.this.getMaxMatchBufferSize()))
/*     */           {
/*     */ 
/* 445 */             ic.buffer.flip();
/* 446 */             if (Logger.isEnabled()) {
/* 447 */               Logger.log(new LogEvent(IncomingConnectionManager.LOGID, 1, "Incoming stream from [" + transport_helper.getAddress() + "] does not match " + "any known byte pattern: " + ByteFormatter.nicePrint(ic.buffer.array(), 128)));
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 453 */             IncomingConnectionManager.this.removeConnection(ic, true, "routing failed: unknown hash");
/*     */           }
/*     */         }
/*     */         else {
/* 457 */           ic.buffer.flip();
/* 458 */           if (Logger.isEnabled()) {
/* 459 */             Logger.log(new LogEvent(IncomingConnectionManager.LOGID, "Incoming stream from [" + transport_helper.getAddress() + "] recognized as " + "known byte pattern: " + ByteFormatter.nicePrint(ic.buffer.array(), 64)));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 464 */           IncomingConnectionManager.this.removeConnection(ic, false, null);
/*     */           
/* 466 */           this.transport.setAlreadyRead(ic.buffer);
/*     */           
/* 468 */           this.transport.connectedInbound();
/*     */           
/* 470 */           IncomingConnectionManager.MatchListener listener = (IncomingConnectionManager.MatchListener)match_data[0];
/* 471 */           listener.connectionMatched(this.transport, match_data[1]);
/*     */         }
/* 473 */         return true;
/*     */       }
/*     */       catch (Throwable t) {
/*     */         try {
/* 477 */           if (Logger.isEnabled()) {
/* 478 */             Logger.log(new LogEvent(IncomingConnectionManager.LOGID, 1, "Incoming connection [" + transport_helper.getAddress() + "] socket read exception: " + t.getMessage()));
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable x)
/*     */         {
/* 485 */           Debug.out("Caught exception on incoming exception log:");
/* 486 */           x.printStackTrace();
/* 487 */           System.out.println("CAUSED BY:");
/* 488 */           t.printStackTrace();
/*     */         }
/*     */         
/* 491 */         IncomingConnectionManager.this.removeConnection(ic, true, t == null ? null : Debug.getNestedExceptionMessage(t));
/*     */       }
/* 493 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void selectFailure(TransportHelper transport_helper, Object attachment, Throwable msg)
/*     */     {
/* 504 */       IncomingConnectionManager.IncomingConnection ic = (IncomingConnectionManager.IncomingConnection)attachment;
/* 505 */       if (Logger.isEnabled()) {
/* 506 */         Logger.log(new LogEvent(IncomingConnectionManager.LOGID, 1, "Incoming connection [" + transport_helper.getAddress() + "] socket select op failure: " + msg.getMessage()));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 512 */       IncomingConnectionManager.this.removeConnection(ic, true, msg == null ? null : Debug.getNestedExceptionMessage(msg));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/IncomingConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */