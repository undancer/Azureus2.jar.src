/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TransferProcessor
/*     */ {
/*     */   private static final boolean RATE_LIMIT_LAN_TOO = false;
/*  45 */   private static boolean RATE_LIMIT_UP_INCLUDES_PROTOCOL = false;
/*  46 */   private static boolean RATE_LIMIT_DOWN_INCLUDES_PROTOCOL = false;
/*     */   
/*     */   public static final int TYPE_UPLOAD = 0;
/*     */   public static final int TYPE_DOWNLOAD = 1;
/*     */   private final int processor_type;
/*     */   
/*     */   static
/*     */   {
/*  54 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Up Rate Limits Include Protocol", "Down Rate Limits Include Protocol" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  62 */         TransferProcessor.access$002(COConfigurationManager.getBooleanParameter("Up Rate Limits Include Protocol"));
/*  63 */         TransferProcessor.access$102(COConfigurationManager.getBooleanParameter("Down Rate Limits Include Protocol"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final LimitedRateGroup max_rate;
/*     */   
/*     */   private final RateHandler main_rate_handler;
/*     */   
/*     */   private final ByteBucket main_bucket;
/*     */   
/*     */   private final EntityHandler main_controller;
/*     */   
/*  78 */   private final HashMap<LimitedRateGroup, GroupData> group_buckets = new HashMap();
/*  79 */   private final HashMap<NetworkConnectionBase, ConnectionData> connections = new HashMap();
/*     */   
/*     */ 
/*     */   private final AEMonitor connections_mon;
/*     */   
/*     */ 
/*     */   private final boolean multi_threaded;
/*     */   
/*     */ 
/*     */ 
/*     */   public TransferProcessor(final int _processor_type, LimitedRateGroup max_rate_limit, boolean multi_threaded)
/*     */   {
/*  91 */     this.processor_type = _processor_type;
/*  92 */     this.max_rate = max_rate_limit;
/*  93 */     this.multi_threaded = multi_threaded;
/*     */     
/*  95 */     this.connections_mon = new AEMonitor("TransferProcessor:" + this.processor_type);
/*     */     
/*  97 */     this.main_bucket = createBucket(this.max_rate.getRateLimitBytesPerSecond());
/*     */     
/*  99 */     this.main_rate_handler = new RateHandler()
/*     */     {
/*     */ 
/* 102 */       final int pt = _processor_type;
/*     */       
/*     */ 
/*     */       public int[] getCurrentNumBytesAllowed()
/*     */       {
/* 107 */         if (TransferProcessor.this.main_bucket.getRate() != TransferProcessor.this.max_rate.getRateLimitBytesPerSecond()) {
/* 108 */           TransferProcessor.this.main_bucket.setRate(TransferProcessor.this.max_rate.getRateLimitBytesPerSecond());
/*     */         }
/*     */         
/*     */         int special;
/*     */         int special;
/* 113 */         if (this.pt == 0) {
/*     */           int special;
/* 115 */           if (TransferProcessor.RATE_LIMIT_UP_INCLUDES_PROTOCOL)
/*     */           {
/* 117 */             special = 0;
/*     */           }
/*     */           else
/*     */           {
/* 121 */             special = Integer.MAX_VALUE;
/*     */           }
/*     */         } else {
/*     */           int special;
/* 125 */           if (TransferProcessor.RATE_LIMIT_DOWN_INCLUDES_PROTOCOL)
/*     */           {
/* 127 */             special = 0;
/*     */           }
/*     */           else
/*     */           {
/* 131 */             special = Integer.MAX_VALUE;
/*     */           }
/*     */         }
/*     */         
/* 135 */         return new int[] { TransferProcessor.this.main_bucket.getAvailableByteCount(), special };
/*     */       }
/*     */       
/*     */ 
/*     */       public void bytesProcessed(int data_bytes, int protocol_bytes)
/*     */       {
/*     */         int num_bytes_written;
/*     */         
/*     */         int num_bytes_written;
/*     */         
/* 145 */         if (this.pt == 0)
/*     */         {
/* 147 */           num_bytes_written = TransferProcessor.RATE_LIMIT_UP_INCLUDES_PROTOCOL ? data_bytes + protocol_bytes : data_bytes;
/*     */         }
/*     */         else
/*     */         {
/* 151 */           num_bytes_written = TransferProcessor.RATE_LIMIT_DOWN_INCLUDES_PROTOCOL ? data_bytes + protocol_bytes : data_bytes;
/*     */         }
/*     */         
/* 154 */         TransferProcessor.this.main_bucket.setBytesUsed(num_bytes_written);
/* 155 */         TransferProcessor.this.max_rate.updateBytesUsed(num_bytes_written);
/*     */       }
/*     */       
/* 158 */     };
/* 159 */     this.main_controller = new EntityHandler(this.processor_type, this.main_rate_handler);
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
/*     */   public void registerPeerConnection(NetworkConnectionBase connection, boolean upload)
/*     */   {
/* 172 */     ConnectionData conn_data = new ConnectionData(null);
/*     */     try {
/* 174 */       this.connections_mon.enter();
/*     */       
/* 176 */       LimitedRateGroup[] groups = connection.getRateLimiters(upload);
/*     */       
/* 178 */       GroupData[] group_datas = new GroupData[groups.length];
/*     */       
/* 180 */       for (int i = 0; i < groups.length; i++) {
/* 181 */         LimitedRateGroup group = groups[i];
/*     */         
/*     */ 
/*     */ 
/* 185 */         GroupData group_data = (GroupData)this.group_buckets.get(group);
/* 186 */         if (group_data == null) {
/* 187 */           int limit = NetworkManagerUtilities.getGroupRateLimit(group);
/* 188 */           group_data = new GroupData(createBucket(limit), null);
/* 189 */           this.group_buckets.put(group, group_data);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */         GroupData.access$608(group_data);
/*     */         
/* 199 */         group_datas[i] = group_data;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */       conn_data.groups = groups;
/* 208 */       conn_data.group_datas = group_datas;
/* 209 */       conn_data.state = 0;
/*     */       
/*     */ 
/* 212 */       this.connections.put(connection, conn_data);
/*     */     } finally {
/* 214 */       this.connections_mon.exit();
/*     */     }
/* 216 */     this.main_controller.registerPeerConnection(connection);
/*     */   }
/*     */   
/*     */   public List<NetworkConnectionBase> getConnections()
/*     */   {
/*     */     try
/*     */     {
/* 223 */       this.connections_mon.enter();
/*     */       
/* 225 */       return new ArrayList(this.connections.keySet());
/*     */     }
/*     */     finally
/*     */     {
/* 229 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isRegistered(NetworkConnectionBase connection) {
/* 234 */     try { this.connections_mon.enter();
/* 235 */       return this.connections.containsKey(connection);
/*     */     } finally {
/* 237 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void deregisterPeerConnection(NetworkConnectionBase connection)
/*     */   {
/*     */     try
/*     */     {
/* 245 */       this.connections_mon.enter();
/* 246 */       ConnectionData conn_data = (ConnectionData)this.connections.remove(connection);
/*     */       
/* 248 */       if (conn_data != null)
/*     */       {
/* 250 */         GroupData[] group_datas = conn_data.group_datas;
/*     */         
/*     */ 
/*     */ 
/* 254 */         for (int i = 0; i < group_datas.length; i++)
/*     */         {
/* 256 */           GroupData group_data = group_datas[i];
/*     */           
/* 258 */           if (group_data.group_size == 1)
/*     */           {
/* 260 */             this.group_buckets.remove(conn_data.groups[i]);
/*     */           }
/*     */           else
/*     */           {
/* 264 */             GroupData.access$610(group_data);
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 269 */       this.connections_mon.exit();
/*     */     }
/*     */     
/* 272 */     this.main_controller.cancelPeerConnection(connection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRateLimiterFreezeState(boolean frozen)
/*     */   {
/* 279 */     this.main_bucket.setFrozen(frozen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addRateLimiter(NetworkConnectionBase connection, LimitedRateGroup group)
/*     */   {
/*     */     try
/*     */     {
/* 288 */       this.connections_mon.enter();
/*     */       
/* 290 */       ConnectionData conn_data = (ConnectionData)this.connections.get(connection);
/*     */       
/* 292 */       if (conn_data != null)
/*     */       {
/* 294 */         LimitedRateGroup[] groups = conn_data.groups;
/*     */         
/* 296 */         for (int i = 0; i < groups.length;)
/*     */         {
/* 298 */           if (groups[i] == group) {
/*     */             return;
/*     */           }
/* 296 */           i++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 306 */         GroupData group_data = (GroupData)this.group_buckets.get(group);
/*     */         
/* 308 */         if (group_data == null)
/*     */         {
/* 310 */           int limit = NetworkManagerUtilities.getGroupRateLimit(group);
/*     */           
/* 312 */           group_data = new GroupData(createBucket(limit), null);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 320 */           this.group_buckets.put(group, group_data);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 329 */         GroupData.access$608(group_data);
/*     */         
/* 331 */         GroupData[] group_datas = conn_data.group_datas;
/*     */         
/* 333 */         int len = groups.length;
/*     */         
/* 335 */         LimitedRateGroup[] new_groups = new LimitedRateGroup[len + 1];
/*     */         
/* 337 */         System.arraycopy(groups, 0, new_groups, 0, len);
/* 338 */         new_groups[len] = group;
/*     */         
/* 340 */         conn_data.groups = new_groups;
/*     */         
/* 342 */         GroupData[] new_group_datas = new GroupData[len + 1];
/*     */         
/* 344 */         System.arraycopy(group_datas, 0, new_group_datas, 0, len);
/* 345 */         new_group_datas[len] = group_data;
/*     */         
/* 347 */         conn_data.group_datas = new_group_datas;
/*     */       }
/*     */     }
/*     */     finally {
/* 351 */       this.connections_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeRateLimiter(NetworkConnectionBase connection, LimitedRateGroup group)
/*     */   {
/*     */     try
/*     */     {
/* 361 */       this.connections_mon.enter();
/*     */       
/* 363 */       ConnectionData conn_data = (ConnectionData)this.connections.get(connection);
/*     */       
/* 365 */       if (conn_data != null)
/*     */       {
/* 367 */         LimitedRateGroup[] groups = conn_data.groups;
/* 368 */         GroupData[] group_datas = conn_data.group_datas;
/*     */         
/* 370 */         int len = groups.length;
/*     */         
/* 372 */         if (len == 0) {
/*     */           return;
/*     */         }
/*     */         
/*     */ 
/* 377 */         LimitedRateGroup[] new_groups = new LimitedRateGroup[len - 1];
/* 378 */         GroupData[] new_group_datas = new GroupData[len - 1];
/*     */         
/* 380 */         int pos = 0;
/*     */         
/* 382 */         for (int i = 0; i < groups.length; i++)
/*     */         {
/* 384 */           if (groups[i] == group)
/*     */           {
/* 386 */             GroupData group_data = conn_data.group_datas[i];
/*     */             
/* 388 */             if (group_data.group_size == 1)
/*     */             {
/* 390 */               this.group_buckets.remove(conn_data.groups[i]);
/*     */             }
/*     */             else
/*     */             {
/* 394 */               GroupData.access$610(group_data);
/*     */             }
/*     */           }
/*     */           else {
/* 398 */             if (pos == new_groups.length) {
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/* 403 */             new_groups[pos] = groups[i];
/* 404 */             new_group_datas[pos] = group_datas[i];
/*     */             
/* 406 */             pos++;
/*     */           }
/*     */         }
/*     */         
/* 410 */         conn_data.groups = new_groups;
/* 411 */         conn_data.group_datas = new_group_datas;
/*     */       }
/*     */     }
/*     */     finally {
/* 415 */       this.connections_mon.exit();
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
/*     */ 
/*     */ 
/*     */   public void upgradePeerConnection(final NetworkConnectionBase connection, int partition_id)
/*     */   {
/* 431 */     ConnectionData connection_data = null;
/*     */     try
/*     */     {
/* 434 */       this.connections_mon.enter();
/*     */       
/* 436 */       connection_data = (ConnectionData)this.connections.get(connection);
/*     */     }
/*     */     finally
/*     */     {
/* 440 */       this.connections_mon.exit();
/*     */     }
/*     */     
/* 443 */     if ((connection_data != null) && (connection_data.state == 0))
/*     */     {
/* 445 */       final ConnectionData conn_data = connection_data;
/*     */       
/* 447 */       this.main_controller.upgradePeerConnection(connection, new RateHandler()
/*     */       {
/*     */ 
/*     */ 
/* 451 */         final int pt = TransferProcessor.this.processor_type;
/*     */         
/*     */ 
/*     */         public int[] getCurrentNumBytesAllowed()
/*     */         {
/*     */           int special;
/*     */           int special;
/* 458 */           if (this.pt == 0) {
/*     */             int special;
/* 460 */             if (TransferProcessor.RATE_LIMIT_UP_INCLUDES_PROTOCOL)
/*     */             {
/* 462 */               special = 0;
/*     */             }
/*     */             else
/*     */             {
/* 466 */               special = Integer.MAX_VALUE;
/*     */             }
/*     */           } else {
/*     */             int special;
/* 470 */             if (TransferProcessor.RATE_LIMIT_DOWN_INCLUDES_PROTOCOL)
/*     */             {
/* 472 */               special = 0;
/*     */             }
/*     */             else
/*     */             {
/* 476 */               special = Integer.MAX_VALUE;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 482 */           if (TransferProcessor.this.main_bucket.getRate() != TransferProcessor.this.max_rate.getRateLimitBytesPerSecond())
/*     */           {
/* 484 */             TransferProcessor.this.main_bucket.setRate(TransferProcessor.this.max_rate.getRateLimitBytesPerSecond());
/*     */           }
/*     */           
/* 487 */           int allowed = TransferProcessor.this.main_bucket.getAvailableByteCount();
/*     */           
/*     */ 
/*     */ 
/* 491 */           allowed -= connection.getMssSize();
/*     */           
/* 493 */           if (allowed < 0) { allowed = 0;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 504 */           if ((!connection.isLANLocal()) || (!NetworkManager.isLANRateEnabled()))
/*     */           {
/*     */ 
/*     */ 
/* 508 */             LimitedRateGroup[] groups = TransferProcessor.ConnectionData.access$700(conn_data);
/* 509 */             TransferProcessor.GroupData[] group_datas = TransferProcessor.ConnectionData.access$800(conn_data);
/*     */             
/* 511 */             if (groups.length != group_datas.length) {
/*     */               try
/*     */               {
/* 514 */                 TransferProcessor.this.connections_mon.enter();
/*     */                 
/* 516 */                 groups = TransferProcessor.ConnectionData.access$700(conn_data);
/* 517 */                 group_datas = TransferProcessor.ConnectionData.access$800(conn_data);
/*     */               } finally {
/* 519 */                 TransferProcessor.this.connections_mon.exit();
/*     */               }
/*     */             }
/*     */             try
/*     */             {
/* 524 */               for (int i = 0; i < group_datas.length; i++)
/*     */               {
/*     */ 
/*     */ 
/* 528 */                 int group_rate = NetworkManagerUtilities.getGroupRateLimit(groups[i]);
/*     */                 
/* 530 */                 ByteBucket group_bucket = TransferProcessor.GroupData.access$1200(group_datas[i]);
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
/* 542 */                 if (group_bucket.getRate() != group_rate)
/*     */                 {
/* 544 */                   group_bucket.setRate(group_rate);
/*     */                 }
/*     */                 
/* 547 */                 int group_allowed = group_bucket.getAvailableByteCount();
/*     */                 
/* 549 */                 if (group_allowed < allowed)
/*     */                 {
/* 551 */                   allowed = group_allowed;
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 559 */               if (!(e instanceof IndexOutOfBoundsException))
/*     */               {
/* 561 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 566 */           return new int[] { allowed, special };
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void bytesProcessed(int data_bytes, int protocol_bytes)
/*     */         {
/*     */           int num_bytes_written;
/*     */           
/*     */ 
/*     */           int num_bytes_written;
/*     */           
/* 578 */           if (this.pt == 0)
/*     */           {
/* 580 */             num_bytes_written = TransferProcessor.RATE_LIMIT_UP_INCLUDES_PROTOCOL ? data_bytes + protocol_bytes : data_bytes;
/*     */           }
/*     */           else
/*     */           {
/* 584 */             num_bytes_written = TransferProcessor.RATE_LIMIT_DOWN_INCLUDES_PROTOCOL ? data_bytes + protocol_bytes : data_bytes;
/*     */           }
/*     */           
/* 587 */           if ((!connection.isLANLocal()) || (!NetworkManager.isLANRateEnabled()))
/*     */           {
/* 589 */             LimitedRateGroup[] groups = TransferProcessor.ConnectionData.access$700(conn_data);
/* 590 */             TransferProcessor.GroupData[] group_datas = TransferProcessor.ConnectionData.access$800(conn_data);
/*     */             
/* 592 */             if (groups.length != group_datas.length) {
/*     */               try
/*     */               {
/* 595 */                 TransferProcessor.this.connections_mon.enter();
/*     */                 
/* 597 */                 groups = TransferProcessor.ConnectionData.access$700(conn_data);
/* 598 */                 group_datas = TransferProcessor.ConnectionData.access$800(conn_data);
/*     */               } finally {
/* 600 */                 TransferProcessor.this.connections_mon.exit();
/*     */               }
/*     */             }
/*     */             
/* 604 */             for (int i = 0; i < group_datas.length; i++)
/*     */             {
/* 606 */               TransferProcessor.GroupData.access$1200(group_datas[i]).setBytesUsed(num_bytes_written);
/*     */               
/* 608 */               groups[i].updateBytesUsed(num_bytes_written);
/*     */             }
/*     */           }
/*     */           
/* 612 */           TransferProcessor.this.main_bucket.setBytesUsed(num_bytes_written); } }, partition_id);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 617 */       conn_data.state = 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downgradePeerConnection(NetworkConnectionBase connection)
/*     */   {
/* 627 */     ConnectionData conn_data = null;
/*     */     try {
/* 629 */       this.connections_mon.enter();
/* 630 */       conn_data = (ConnectionData)this.connections.get(connection);
/*     */     } finally {
/* 632 */       this.connections_mon.exit();
/*     */     }
/* 634 */     if ((conn_data != null) && (conn_data.state == 1)) {
/* 635 */       this.main_controller.downgradePeerConnection(connection);
/* 636 */       conn_data.state = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public RateHandler getRateHandler()
/*     */   {
/* 643 */     return this.main_rate_handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RateHandler getRateHandler(NetworkConnectionBase connection)
/*     */   {
/* 650 */     return this.main_controller.getRateHandler(connection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ByteBucket createBucket(int bytes_per_sec)
/*     */   {
/* 657 */     if (this.multi_threaded)
/*     */     {
/* 659 */       return new ByteBucketMT(bytes_per_sec);
/*     */     }
/*     */     
/*     */ 
/* 663 */     return new ByteBucketST(bytes_per_sec);
/*     */   }
/*     */   
/*     */ 
/*     */   private static class ConnectionData
/*     */   {
/*     */     private static final int STATE_NORMAL = 0;
/*     */     private static final int STATE_UPGRADED = 1;
/*     */     private int state;
/*     */     private LimitedRateGroup[] groups;
/*     */     private TransferProcessor.GroupData[] group_datas;
/*     */   }
/*     */   
/*     */   private static class GroupData
/*     */   {
/*     */     private final ByteBucket bucket;
/* 679 */     private int group_size = 0;
/*     */     
/*     */     private GroupData(ByteBucket bucket) {
/* 682 */       this.bucket = bucket;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransferProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */