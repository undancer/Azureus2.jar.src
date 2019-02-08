/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.nat.NATTraverser;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.messaging.MessageException;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageEndpoint;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageHandler;
/*     */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
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
/*     */ public class GenericMessageConnectionIndirect
/*     */   implements GenericMessageConnectionAdapter
/*     */ {
/*  55 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final boolean TRACE = false;
/*     */   
/*     */   public static final int MAX_MESSAGE_SIZE = 32768;
/*     */   
/*     */   private static final int MESSAGE_TYPE_CONNECT = 1;
/*     */   
/*     */   private static final int MESSAGE_TYPE_ERROR = 2;
/*     */   
/*     */   private static final int MESSAGE_TYPE_DATA = 3;
/*     */   
/*     */   private static final int MESSAGE_TYPE_DISCONNECT = 4;
/*     */   
/*     */   private static final int TICK_PERIOD = 5000;
/*     */   
/*     */   private static final int KEEP_ALIVE_CHECK_PERIOD = 5000;
/*     */   
/*     */   private static final int KEEP_ALIVE_MIN = 10000;
/*     */   
/*     */   private static final int STATS_PERIOD = 60000;
/*     */   
/*     */   private static final int KEEP_ALIVE_CHECK_TICKS = 1;
/*     */   
/*     */   private static final int STATS_TICKS = 12;
/*     */   
/*     */   private static final int MAX_REMOTE_CONNECTIONS = 1024;
/*     */   
/*     */   private static final int MAX_REMOTE_CONNECTIONS_PER_IP = 32;
/*     */   
/*  85 */   private static long connection_id_next = RandomUtils.nextLong();
/*     */   
/*     */ 
/*  88 */   private static Map local_connections = new HashMap();
/*  89 */   private static Map remote_connections = new HashMap();
/*     */   
/*  91 */   private static ThreadPool keep_alive_pool = new ThreadPool("GenericMessageConnectionIndirect:keepAlive", 8, true);
/*     */   private MessageManagerImpl message_manager;
/*     */   private String msg_id;
/*     */   private String msg_desc;
/*     */   private GenericMessageEndpoint endpoint;
/*     */   private NATTraverser nat_traverser;
/*     */   
/*     */   static
/*     */   {
/* 100 */     SimpleTimer.addPeriodicEvent("DDBTorrent:timeout2", 5000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 105 */       private int tick_count = 0;
/*     */       
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/* 111 */         this.tick_count += 1;
/*     */         
/* 113 */         if (this.tick_count % 12 == 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 118 */           if (Logger.isEnabled()) {
/*     */             int local_total;
/* 120 */             synchronized (GenericMessageConnectionIndirect.local_connections)
/*     */             {
/* 122 */               local_total = GenericMessageConnectionIndirect.local_connections.size();
/*     */             }
/*     */             int remote_total;
/* 125 */             synchronized (GenericMessageConnectionIndirect.remote_connections)
/*     */             {
/* 127 */               remote_total = GenericMessageConnectionIndirect.remote_connections.size();
/*     */             }
/*     */             
/* 130 */             if (local_total + remote_total > 0)
/*     */             {
/* 132 */               GenericMessageConnectionIndirect.log("local=" + local_total + " [" + GenericMessageConnectionIndirect.getLocalConnectionStatus() + "], remote=" + remote_total + " [" + GenericMessageConnectionIndirect.getRemoteConnectionStatus() + "]");
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 137 */         if (this.tick_count % 1 == 0)
/*     */         {
/* 139 */           synchronized (GenericMessageConnectionIndirect.local_connections)
/*     */           {
/* 141 */             Iterator it = GenericMessageConnectionIndirect.local_connections.values().iterator();
/*     */             
/* 143 */             while (it.hasNext())
/*     */             {
/* 145 */               final GenericMessageConnectionIndirect con = (GenericMessageConnectionIndirect)it.next();
/*     */               
/* 147 */               if (con.prepareForKeepAlive(false))
/*     */               {
/* 149 */                 GenericMessageConnectionIndirect.keep_alive_pool.run(new AERunnable()
/*     */                 {
/*     */ 
/*     */                   public void runSupport()
/*     */                   {
/*     */ 
/* 155 */                     con.keepAlive();
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 162 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 164 */           synchronized (GenericMessageConnectionIndirect.remote_connections)
/*     */           {
/* 166 */             if (GenericMessageConnectionIndirect.remote_connections.size() > 0)
/*     */             {
/*     */ 
/*     */ 
/* 170 */               Iterator it = new ArrayList(GenericMessageConnectionIndirect.remote_connections.values()).iterator();
/*     */               
/* 172 */               while (it.hasNext())
/*     */               {
/* 174 */                 GenericMessageConnectionIndirect con = (GenericMessageConnectionIndirect)it.next();
/*     */                 
/* 176 */                 long last_receive = con.getLastMessageReceivedTime();
/*     */                 
/* 178 */                 if (now - last_receive > 30000L) {
/*     */                   try
/*     */                   {
/* 181 */                     con.close(new Throwable("Timeout"));
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 185 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
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
/*     */   protected static Map receive(MessageManagerImpl message_manager, InetSocketAddress originator, Map message)
/*     */   {
/* 207 */     if (!message.containsKey("type"))
/*     */     {
/* 209 */       return null;
/*     */     }
/*     */     
/* 212 */     int type = ((Long)message.get("type")).intValue();
/*     */     
/* 214 */     if (type == 1)
/*     */     {
/* 216 */       String msg_id = new String((byte[])message.get("msg_id"));
/* 217 */       String msg_desc = new String((byte[])message.get("msg_desc"));
/*     */       
/* 219 */       GenericMessageEndpointImpl endpoint = new GenericMessageEndpointImpl(originator);
/*     */       
/* 221 */       endpoint.addUDP(originator);
/*     */       
/* 223 */       GenericMessageHandler handler = message_manager.getHandler(msg_id);
/*     */       
/* 225 */       if (handler == null)
/*     */       {
/* 227 */         Debug.out("No message handler registered for '" + msg_id + "'");
/*     */         
/* 229 */         return null;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/*     */         Long con_id;
/* 235 */         synchronized (remote_connections)
/*     */         {
/* 237 */           if (remote_connections.size() >= 1024)
/*     */           {
/* 239 */             Debug.out("Maximum remote connections exceeded - request from " + originator + " denied [" + getRemoteConnectionStatus() + "]");
/*     */             
/* 241 */             return null;
/*     */           }
/*     */           
/* 244 */           int num_from_this_ip = 0;
/*     */           
/* 246 */           Iterator it = remote_connections.values().iterator();
/*     */           
/* 248 */           while (it.hasNext())
/*     */           {
/* 250 */             GenericMessageConnectionIndirect con = (GenericMessageConnectionIndirect)it.next();
/*     */             
/* 252 */             if (con.getEndpoint().getNotionalAddress().getAddress().equals(originator.getAddress()))
/*     */             {
/* 254 */               num_from_this_ip++;
/*     */             }
/*     */           }
/*     */           
/* 258 */           if (num_from_this_ip >= 32)
/*     */           {
/* 260 */             Debug.out("Maximum remote connections per-ip exceeded - request from " + originator + " denied [" + getRemoteConnectionStatus() + "]");
/*     */             
/* 262 */             return null;
/*     */           }
/*     */           
/* 265 */           con_id = new Long(connection_id_next++);
/*     */         }
/*     */         
/* 268 */         GenericMessageConnectionIndirect indirect_connection = new GenericMessageConnectionIndirect(message_manager, msg_id, msg_desc, endpoint, con_id.longValue());
/*     */         
/*     */ 
/*     */ 
/* 272 */         GenericMessageConnectionImpl new_connection = new GenericMessageConnectionImpl(message_manager, indirect_connection);
/*     */         
/* 274 */         if (handler.accept(new_connection))
/*     */         {
/* 276 */           new_connection.accepted();
/*     */           
/* 278 */           synchronized (remote_connections)
/*     */           {
/* 280 */             remote_connections.put(con_id, indirect_connection);
/*     */           }
/*     */           
/* 283 */           List replies = indirect_connection.receive((List)message.get("data"));
/*     */           
/* 285 */           Map reply = new HashMap();
/*     */           
/* 287 */           reply.put("type", new Long(1L));
/* 288 */           reply.put("con_id", con_id);
/* 289 */           reply.put("data", replies);
/*     */           
/* 291 */           return reply;
/*     */         }
/*     */         
/*     */ 
/* 295 */         return null;
/*     */ 
/*     */       }
/*     */       catch (MessageException e)
/*     */       {
/* 300 */         Debug.out("Error accepting message", e);
/*     */         
/* 302 */         return null;
/*     */       }
/*     */     }
/* 305 */     if (type == 3)
/*     */     {
/* 307 */       Long con_id = (Long)message.get("con_id");
/*     */       
/*     */       GenericMessageConnectionIndirect indirect_connection;
/*     */       
/* 311 */       synchronized (remote_connections)
/*     */       {
/* 313 */         indirect_connection = (GenericMessageConnectionIndirect)remote_connections.get(con_id);
/*     */       }
/*     */       
/* 316 */       if (indirect_connection == null)
/*     */       {
/* 318 */         return null;
/*     */       }
/*     */       
/* 321 */       Map reply = new HashMap();
/*     */       
/* 323 */       if (indirect_connection.isClosed())
/*     */       {
/* 325 */         reply.put("type", new Long(4L));
/*     */       }
/*     */       else
/*     */       {
/* 329 */         List replies = indirect_connection.receive((List)message.get("data"));
/*     */         
/* 331 */         reply.put("type", new Long(3L));
/* 332 */         reply.put("data", replies);
/*     */         
/* 334 */         if (indirect_connection.receiveIncomplete())
/*     */         {
/* 336 */           reply.put("more_data", new Long(1L));
/*     */         }
/*     */       }
/*     */       
/* 340 */       return reply;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 346 */     Long con_id = (Long)message.get("con_id");
/*     */     
/*     */     GenericMessageConnectionIndirect indirect_connection;
/*     */     
/* 350 */     synchronized (remote_connections)
/*     */     {
/* 352 */       indirect_connection = (GenericMessageConnectionIndirect)remote_connections.get(con_id);
/*     */     }
/*     */     
/* 355 */     if (indirect_connection != null) {
/*     */       try
/*     */       {
/* 358 */         indirect_connection.close(new Throwable("Remote closed connection"));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 362 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 366 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static String getRemoteConnectionStatus()
/*     */   {
/* 373 */     return getConnectionStatus(remote_connections);
/*     */   }
/*     */   
/*     */ 
/*     */   protected static String getLocalConnectionStatus()
/*     */   {
/* 379 */     return getConnectionStatus(local_connections);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static String getConnectionStatus(Map connections)
/*     */   {
/* 386 */     Map totals = new HashMap();
/*     */     
/* 388 */     synchronized (connections)
/*     */     {
/* 390 */       Iterator it = connections.values().iterator();
/*     */       
/* 392 */       while (it.hasNext())
/*     */       {
/* 394 */         GenericMessageConnectionIndirect con = (GenericMessageConnectionIndirect)it.next();
/*     */         
/* 396 */         InetAddress originator = con.getEndpoint().getNotionalAddress().getAddress();
/*     */         
/* 398 */         Integer i = (Integer)totals.get(originator);
/*     */         
/* 400 */         if (i == null)
/*     */         {
/* 402 */           i = new Integer(1);
/*     */         }
/*     */         else
/*     */         {
/* 406 */           i = new Integer(i.intValue() + 1);
/*     */         }
/*     */         
/* 409 */         totals.put(originator, i);
/*     */       }
/*     */     }
/*     */     
/* 413 */     String str = "";
/*     */     
/* 415 */     Iterator it = totals.entrySet().iterator();
/*     */     
/* 417 */     while (it.hasNext())
/*     */     {
/* 419 */       Map.Entry entry = (Map.Entry)it.next();
/*     */       
/* 421 */       str = str + (str.length() == 0 ? "" : ",") + entry.getKey() + ":" + entry.getValue();
/*     */     }
/*     */     
/* 424 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private GenericMessageConnectionImpl owner;
/*     */   
/*     */ 
/*     */   private InetSocketAddress rendezvous;
/*     */   
/*     */ 
/*     */   private InetSocketAddress target;
/*     */   
/*     */ 
/*     */   private long connection_id;
/*     */   
/*     */ 
/*     */   private boolean incoming;
/*     */   
/*     */   private boolean closed;
/*     */   
/* 445 */   private LinkedList<byte[]> send_queue = new LinkedList();
/*     */   
/* 447 */   private AESemaphore send_queue_sem = new AESemaphore("GenericMessageConnectionIndirect:sendq");
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile long last_message_sent;
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile long last_message_received;
/*     */   
/*     */ 
/*     */   private volatile boolean keep_alive_in_progress;
/*     */   
/*     */ 
/*     */ 
/*     */   protected GenericMessageConnectionIndirect(MessageManagerImpl _message_manager, String _msg_id, String _msg_desc, GenericMessageEndpoint _endpoint, InetSocketAddress _rendezvous, InetSocketAddress _target)
/*     */   {
/* 464 */     this.message_manager = _message_manager;
/* 465 */     this.msg_id = _msg_id;
/* 466 */     this.msg_desc = _msg_desc;
/* 467 */     this.endpoint = _endpoint;
/* 468 */     this.rendezvous = _rendezvous;
/* 469 */     this.target = _target;
/*     */     
/* 471 */     this.nat_traverser = this.message_manager.getNATTraverser();
/*     */     
/* 473 */     log("outgoing connection to " + this.endpoint.getNotionalAddress());
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
/*     */   protected GenericMessageConnectionIndirect(MessageManagerImpl _message_manager, String _msg_id, String _msg_desc, GenericMessageEndpoint _endpoint, long _connection_id)
/*     */   {
/* 486 */     this.message_manager = _message_manager;
/* 487 */     this.msg_id = _msg_id;
/* 488 */     this.msg_desc = _msg_desc;
/* 489 */     this.endpoint = _endpoint;
/* 490 */     this.connection_id = _connection_id;
/*     */     
/* 492 */     this.incoming = true;
/*     */     
/* 494 */     this.last_message_received = SystemTime.getCurrentTime();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 500 */     log("incoming connection from " + this.endpoint.getNotionalAddress());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOwner(GenericMessageConnectionImpl _owner)
/*     */   {
/* 507 */     this.owner = _owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumMessageSize()
/*     */   {
/* 513 */     return 32768;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 519 */     return "Tunnel";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransportType()
/*     */   {
/* 525 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastMessageReceivedTime()
/*     */   {
/* 531 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 533 */     if (now < this.last_message_received)
/*     */     {
/* 535 */       this.last_message_received = now;
/*     */     }
/*     */     
/* 538 */     return this.last_message_received;
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericMessageEndpoint getEndpoint()
/*     */   {
/* 544 */     return this.endpoint;
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
/*     */   public void connect(ByteBuffer initial_data, GenericMessageConnectionAdapter.ConnectionListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 586 */       Map message = new HashMap();
/*     */       
/* 588 */       byte[] initial_data_bytes = new byte[initial_data.remaining()];
/*     */       
/* 590 */       initial_data.get(initial_data_bytes);
/*     */       
/* 592 */       List initial_messages = new ArrayList();
/*     */       
/* 594 */       initial_messages.add(initial_data_bytes);
/*     */       
/* 596 */       message.put("type", new Long(1L));
/* 597 */       message.put("msg_id", this.msg_id);
/* 598 */       message.put("msg_desc", this.msg_desc);
/* 599 */       message.put("data", initial_messages);
/*     */       
/* 601 */       Map reply = this.nat_traverser.sendMessage(this.message_manager, this.rendezvous, this.target, message);
/*     */       
/* 603 */       this.last_message_sent = SystemTime.getCurrentTime();
/*     */       
/* 605 */       if ((reply == null) || (!reply.containsKey("type")))
/*     */       {
/* 607 */         listener.connectFailure(new Throwable("Indirect connect failed (response=" + reply + ")"));
/*     */       }
/*     */       else
/*     */       {
/* 611 */         int reply_type = ((Long)reply.get("type")).intValue();
/*     */         
/* 613 */         if (reply_type == 2)
/*     */         {
/* 615 */           listener.connectFailure(new Throwable(new String((byte[])reply.get("error"))));
/*     */         }
/* 617 */         else if (reply_type == 4)
/*     */         {
/* 619 */           listener.connectFailure(new Throwable("Disconnected"));
/*     */         }
/* 621 */         else if (reply_type == 1)
/*     */         {
/* 623 */           this.connection_id = ((Long)reply.get("con_id")).longValue();
/*     */           
/* 625 */           synchronized (local_connections)
/*     */           {
/* 627 */             local_connections.put(new Long(this.connection_id), this);
/*     */           }
/*     */           
/* 630 */           listener.connectSuccess();
/*     */           
/* 632 */           List<byte[]> replies = (List)reply.get("data");
/*     */           
/* 634 */           for (int i = 0; i < replies.size(); i++)
/*     */           {
/* 636 */             this.owner.receive(new GenericMessage(this.msg_id, this.msg_desc, new DirectByteBuffer(ByteBuffer.wrap((byte[])replies.get(i))), false));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 641 */           Debug.out("Unexpected reply type - " + reply_type);
/*     */           
/* 643 */           listener.connectFailure(new Throwable("Unexpected reply type - " + reply_type));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 648 */       listener.connectFailure(e);
/*     */     }
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
/*     */   public void send(PooledByteBuffer pbb)
/*     */     throws MessageException
/*     */   {
/* 663 */     byte[] bytes = pbb.toByteArray();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 669 */     if (this.incoming)
/*     */     {
/* 671 */       synchronized (this.send_queue)
/*     */       {
/* 673 */         if (this.send_queue.size() > 64)
/*     */         {
/* 675 */           throw new MessageException("Send queue limit exceeded");
/*     */         }
/*     */         
/* 678 */         this.send_queue.add(bytes);
/*     */       }
/*     */       
/* 681 */       this.send_queue_sem.release();
/*     */     }
/*     */     else
/*     */     {
/* 685 */       List messages = new ArrayList();
/*     */       
/* 687 */       messages.add(bytes);
/*     */       
/* 689 */       send(messages);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void send(List messages)
/*     */   {
/*     */     try
/*     */     {
/* 702 */       Map message = new HashMap();
/*     */       
/* 704 */       message.put("con_id", new Long(this.connection_id));
/* 705 */       message.put("type", new Long(3L));
/* 706 */       message.put("data", messages);
/*     */       
/* 708 */       Map reply = this.nat_traverser.sendMessage(this.message_manager, this.rendezvous, this.target, message);
/*     */       
/* 710 */       this.last_message_sent = SystemTime.getCurrentTime();
/*     */       
/* 712 */       if ((reply == null) || (!reply.containsKey("type")))
/*     */       {
/* 714 */         this.owner.reportFailed(new Throwable("Indirect message send failed (response=" + reply + ")"));
/*     */       }
/*     */       else
/*     */       {
/* 718 */         int reply_type = ((Long)reply.get("type")).intValue();
/*     */         
/* 720 */         if (reply_type == 2)
/*     */         {
/* 722 */           this.owner.reportFailed(new Throwable(new String((byte[])reply.get("error"))));
/*     */         }
/* 724 */         else if (reply_type == 3)
/*     */         {
/* 726 */           List<byte[]> replies = (List)reply.get("data");
/*     */           
/* 728 */           for (int i = 0; i < replies.size(); i++)
/*     */           {
/* 730 */             this.owner.receive(new GenericMessage(this.msg_id, this.msg_desc, new DirectByteBuffer(ByteBuffer.wrap((byte[])replies.get(i))), false));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 736 */           if (reply.get("more_data") != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 742 */             new DelayedEvent("GenMsg:kap", 500L, new AERunnable()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void runSupport()
/*     */               {
/*     */ 
/*     */ 
/* 750 */                 if (GenericMessageConnectionIndirect.this.prepareForKeepAlive(true))
/*     */                 {
/* 752 */                   GenericMessageConnectionIndirect.keep_alive_pool.run(new AERunnable()
/*     */                   {
/*     */ 
/*     */                     public void runSupport()
/*     */                     {
/*     */ 
/* 758 */                       GenericMessageConnectionIndirect.this.keepAlive();
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/* 765 */         } else if (reply_type == 4)
/*     */         {
/* 767 */           this.owner.reportFailed(new Throwable("Disconnected"));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 772 */       this.owner.reportFailed(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List<byte[]> receive(List<byte[]> messages)
/*     */   {
/* 784 */     this.last_message_received = SystemTime.getCurrentTime();
/*     */     
/* 786 */     for (int i = 0; i < messages.size(); i++)
/*     */     {
/* 788 */       this.owner.receive(new GenericMessage(this.msg_id, this.msg_desc, new DirectByteBuffer(ByteBuffer.wrap((byte[])messages.get(i))), false));
/*     */     }
/*     */     
/* 791 */     List<byte[]> reply = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/* 795 */     if (this.send_queue_sem.reserve(2500L))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/* 800 */         Thread.sleep(250L);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 805 */       int max = getMaximumMessageSize();
/* 806 */       int total = 0;
/*     */       
/* 808 */       synchronized (this.send_queue)
/*     */       {
/* 810 */         while (this.send_queue.size() > 0)
/*     */         {
/* 812 */           byte[] data = (byte[])this.send_queue.getFirst();
/*     */           
/* 814 */           if ((total > 0) && (total + data.length > max)) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 819 */           reply.add(this.send_queue.removeFirst());
/*     */           
/* 821 */           total += data.length;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 830 */       if (reply.size() == 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 835 */         this.send_queue_sem.release();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 840 */         for (int i = 1; i < reply.size(); i++)
/*     */         {
/* 842 */           this.send_queue_sem.reserve();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 847 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean receiveIncomplete()
/*     */   {
/* 853 */     synchronized (this.send_queue)
/*     */     {
/* 855 */       return this.send_queue.size() > 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws MessageException
/*     */   {
/* 864 */     close(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void close(Throwable close_cause)
/*     */     throws MessageException
/*     */   {
/* 873 */     if (this.closed)
/*     */     {
/* 875 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 886 */     log("connection to " + this.endpoint.getNotionalAddress() + " closed" + (close_cause == null ? "" : new StringBuilder().append(" (").append(close_cause).append(")").toString()));
/*     */     try
/*     */     {
/* 889 */       this.closed = true;
/*     */       
/* 891 */       if (this.incoming)
/*     */       {
/* 893 */         synchronized (remote_connections)
/*     */         {
/* 895 */           remote_connections.remove(new Long(this.connection_id));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 900 */         synchronized (local_connections)
/*     */         {
/* 902 */           local_connections.remove(new Long(this.connection_id));
/*     */         }
/*     */         
/* 905 */         Map message = new HashMap();
/*     */         
/* 907 */         message.put("con_id", new Long(this.connection_id));
/* 908 */         message.put("type", new Long(4L));
/*     */         try
/*     */         {
/* 911 */           this.nat_traverser.sendMessage(this.message_manager, this.rendezvous, this.target, message);
/*     */           
/* 913 */           this.last_message_sent = SystemTime.getCurrentTime();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 917 */           throw new MessageException("Close operation failed", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 922 */       if (close_cause != null)
/*     */       {
/* 924 */         this.owner.reportFailed(close_cause);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isClosed()
/*     */   {
/* 932 */     return this.closed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean prepareForKeepAlive(boolean force)
/*     */   {
/* 939 */     if (this.keep_alive_in_progress)
/*     */     {
/* 941 */       return false;
/*     */     }
/*     */     
/* 944 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 946 */     if ((force) || (now < this.last_message_sent) || (now - this.last_message_sent > 10000L))
/*     */     {
/* 948 */       this.keep_alive_in_progress = true;
/*     */       
/* 950 */       return true;
/*     */     }
/*     */     
/* 953 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void keepAlive()
/*     */   {
/*     */     try
/*     */     {
/* 965 */       send(new ArrayList());
/*     */     }
/*     */     finally
/*     */     {
/* 969 */       this.keep_alive_in_progress = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static void log(String str)
/*     */   {
/* 977 */     if (Logger.isEnabled())
/*     */     {
/* 979 */       Logger.log(new LogEvent(LOGID, "GenericMessaging (indirect):" + str));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInboundRateLimiter(RateLimiter limiter) {}
/*     */   
/*     */   public void removeInboundRateLimiter(RateLimiter limiter) {}
/*     */   
/*     */   public void addOutboundRateLimiter(RateLimiter limiter) {}
/*     */   
/*     */   public void removeOutboundRateLimiter(RateLimiter limiter) {}
/*     */   
/*     */   public void accepted() {}
/*     */   
/*     */   protected void trace(String str) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageConnectionIndirect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */