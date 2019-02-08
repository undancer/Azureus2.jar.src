/*     */ package com.aelitis.azureus.core.dht.transport.loopback;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFindValueReply;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportListener;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportProgressListener;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportRequestHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStoreReply;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportTransferHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportRequestCounter;
/*     */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportStatsImpl;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTTransportLoopbackImpl
/*     */   implements DHTTransport
/*     */ {
/*     */   public static final byte VERSION = 1;
/*  43 */   public static int LATENCY = 0;
/*  44 */   public static int FAIL_PERCENTAGE = 0;
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/*  49 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getMinimumProtocolVersion()
/*     */   {
/*  55 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetwork()
/*     */   {
/*  61 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIPV6()
/*     */   {
/*  67 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setLatency(int _latency)
/*     */   {
/*  74 */     LATENCY = _latency;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setFailPercentage(int p)
/*     */   {
/*  81 */     FAIL_PERCENTAGE = p;
/*     */   }
/*     */   
/*  84 */   private static long node_id_seed_next = 0L;
/*  85 */   private static final Map node_map = new HashMap();
/*     */   
/*  87 */   static final List dispatch_queue = new ArrayList();
/*  88 */   static final AESemaphore dispatch_queue_sem = new AESemaphore("DHTTransportLoopback");
/*     */   
/*  90 */   static final AEMonitor class_mon = new AEMonitor("DHTTransportLoopback:class");
/*     */   private byte[] node_id;
/*     */   
/*  93 */   static { AEThread dispatcher = new AEThread("DHTTransportLoopback")
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */         for (;;)
/*     */         {
/* 101 */           DHTTransportLoopbackImpl.dispatch_queue_sem.reserve();
/*     */           
/*     */           Runnable r;
/*     */           try
/*     */           {
/* 106 */             DHTTransportLoopbackImpl.class_mon.enter();
/*     */             
/* 108 */             r = (Runnable)DHTTransportLoopbackImpl.dispatch_queue.remove(0);
/*     */           }
/*     */           finally
/*     */           {
/* 112 */             DHTTransportLoopbackImpl.class_mon.exit();
/*     */           }
/*     */           
/* 115 */           if (DHTTransportLoopbackImpl.LATENCY > 0) {
/*     */             try
/*     */             {
/* 118 */               Thread.sleep(DHTTransportLoopbackImpl.LATENCY);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 125 */           r.run();
/*     */         }
/*     */         
/*     */       }
/* 129 */     };
/* 130 */     dispatcher.start();
/*     */   }
/*     */   
/*     */ 
/*     */   private DHTTransportContact local_contact;
/*     */   
/*     */   private final int id_byte_length;
/*     */   
/*     */   private DHTTransportRequestHandler request_handler;
/*     */   
/* 140 */   private final DHTTransportStatsImpl stats = new DHTTransportLoopbackStatsImpl((byte)1);
/*     */   
/* 142 */   private final List listeners = new ArrayList();
/*     */   
/*     */   public static DHTTransportStats getOverallStats()
/*     */   {
/*     */     try
/*     */     {
/* 148 */       class_mon.enter();
/*     */       
/* 150 */       DHTTransportStatsImpl overall_stats = new DHTTransportLoopbackStatsImpl((byte)1);
/*     */       
/* 152 */       Iterator it = node_map.values().iterator();
/*     */       
/* 154 */       while (it.hasNext())
/*     */       {
/* 156 */         overall_stats.add((DHTTransportStatsImpl)((DHTTransportLoopbackImpl)it.next()).getStats());
/*     */       }
/*     */       
/* 159 */       return overall_stats;
/*     */     }
/*     */     finally
/*     */     {
/* 163 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTTransportLoopbackImpl(int _id_byte_length)
/*     */   {
/* 171 */     this.id_byte_length = _id_byte_length;
/*     */     try
/*     */     {
/* 174 */       class_mon.enter();
/*     */       
/* 176 */       byte[] temp = new SHA1Simple().calculateHash(("" + node_id_seed_next++).getBytes());
/*     */       
/* 178 */       this.node_id = new byte[this.id_byte_length];
/*     */       
/* 180 */       System.arraycopy(temp, 0, this.node_id, 0, this.id_byte_length);
/*     */       
/* 182 */       node_map.put(new HashWrapper(this.node_id), this);
/*     */       
/* 184 */       this.local_contact = new DHTTransportLoopbackContactImpl(this, this.node_id);
/*     */     }
/*     */     finally {
/* 187 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportContact getLocalContact()
/*     */   {
/* 194 */     return this.local_contact;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 206 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getGenericFlags()
/*     */   {
/* 212 */     return 0;
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
/*     */   public long getTimeout()
/*     */   {
/* 230 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReachable()
/*     */   {
/* 242 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportContact[] getReachableContacts()
/*     */   {
/* 248 */     return new DHTTransportContact[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportContact[] getRecentContacts()
/*     */   {
/* 254 */     return new DHTTransportContact[0];
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportLoopbackImpl findTarget(byte[] id)
/*     */   {
/*     */     try
/*     */     {
/* 262 */       class_mon.enter();
/*     */       
/* 264 */       return (DHTTransportLoopbackImpl)node_map.get(new HashWrapper(id));
/*     */     }
/*     */     finally {
/* 267 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRequestHandler(DHTTransportRequestHandler _request_handler)
/*     */   {
/* 275 */     this.request_handler = new DHTTransportRequestCounter(_request_handler, this.stats);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected DHTTransportRequestHandler getRequestHandler()
/*     */   {
/* 282 */     return this.request_handler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map<String, Object> exportContactToMap(DHTTransportContact contact)
/*     */   {
/* 289 */     Map<String, Object> result = new HashMap();
/*     */     
/* 291 */     result.put("i", contact.getID());
/*     */     
/* 293 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void exportContact(DHTTransportContact contact, DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 303 */     os.writeInt(1);
/*     */     
/* 305 */     os.writeInt(this.id_byte_length);
/*     */     
/* 307 */     os.write(contact.getID());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportContact importContact(DataInputStream is, boolean is_bootstrap)
/*     */     throws IOException
/*     */   {
/* 317 */     int version = is.readInt();
/*     */     
/* 319 */     if (version != 1)
/*     */     {
/* 321 */       throw new IOException("Unsuported version");
/*     */     }
/*     */     
/* 324 */     int id_len = is.readInt();
/*     */     
/* 326 */     if (id_len != this.id_byte_length)
/*     */     {
/* 328 */       throw new IOException("Imported contact has incorrect ID length");
/*     */     }
/*     */     
/* 331 */     byte[] id = new byte[this.id_byte_length];
/*     */     
/* 333 */     is.read(id);
/*     */     
/* 335 */     DHTTransportContact contact = new DHTTransportLoopbackContactImpl(this, id);
/*     */     
/* 337 */     this.request_handler.contactImported(contact, is_bootstrap);
/*     */     
/* 339 */     return contact;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void run(AERunnable r)
/*     */   {
/*     */     try
/*     */     {
/* 353 */       class_mon.enter();
/*     */       
/* 355 */       dispatch_queue.add(r);
/*     */     }
/*     */     finally
/*     */     {
/* 359 */       class_mon.exit();
/*     */     }
/*     */     
/* 362 */     dispatch_queue_sem.release();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportStats getStats()
/*     */   {
/* 368 */     return this.stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendPing(final DHTTransportContact contact, final DHTTransportReplyHandler handler)
/*     */   {
/* 380 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 386 */         DHTTransportLoopbackImpl.this.sendPingSupport(contact, handler);
/*     */       }
/*     */       
/* 389 */     };
/* 390 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendPingSupport(DHTTransportContact contact, DHTTransportReplyHandler handler)
/*     */   {
/* 398 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 400 */     this.stats.pingSent(null);
/*     */     
/* 402 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 404 */       this.stats.pingFailed();
/*     */       
/* 406 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 410 */       this.stats.pingOK();
/*     */       
/* 412 */       target.getRequestHandler().pingRequest(new DHTTransportLoopbackContactImpl(target, this.node_id));
/*     */       
/* 414 */       handler.pingReply(contact, 0);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendKeyBlock(final DHTTransportContact contact, final DHTTransportReplyHandler handler, final byte[] request, final byte[] sig)
/*     */   {
/* 425 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 431 */         DHTTransportLoopbackImpl.this.sendKeyBlockSupport(contact, handler, request, sig);
/*     */       }
/*     */       
/* 434 */     };
/* 435 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendKeyBlockSupport(DHTTransportContact contact, DHTTransportReplyHandler handler, byte[] request, byte[] sig)
/*     */   {
/* 445 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 447 */     this.stats.keyBlockSent(null);
/*     */     
/* 449 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 451 */       this.stats.keyBlockFailed();
/*     */       
/* 453 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 457 */       this.stats.keyBlockOK();
/*     */       
/* 459 */       target.getRequestHandler().keyBlockRequest(new DHTTransportLoopbackContactImpl(target, this.node_id), request, sig);
/*     */       
/*     */ 
/*     */ 
/* 463 */       handler.keyBlockReply(contact);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendStats(final DHTTransportContact contact, final DHTTransportReplyHandler handler)
/*     */   {
/* 473 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 479 */         DHTTransportLoopbackImpl.this.sendStatsSupport(contact, handler);
/*     */       }
/*     */       
/* 482 */     };
/* 483 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendStatsSupport(DHTTransportContact contact, DHTTransportReplyHandler handler)
/*     */   {
/* 491 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 493 */     this.stats.statsSent(null);
/*     */     
/* 495 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 497 */       this.stats.statsFailed();
/*     */       
/* 499 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 503 */       this.stats.statsOK();
/*     */       
/* 505 */       DHTTransportFullStats res = target.getRequestHandler().statsRequest(new DHTTransportLoopbackContactImpl(target, this.node_id));
/*     */       
/* 507 */       handler.statsReply(contact, res);
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
/*     */   public void sendStore(final DHTTransportContact contact, final DHTTransportReplyHandler handler, final byte[][] keys, final DHTTransportValue[][] value_sets, boolean immediate)
/*     */   {
/* 521 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 527 */         DHTTransportLoopbackImpl.this.sendStoreSupport(contact, handler, keys, value_sets);
/*     */       }
/*     */       
/* 530 */     };
/* 531 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendStoreSupport(DHTTransportContact contact, DHTTransportReplyHandler handler, byte[][] keys, DHTTransportValue[][] value_sets)
/*     */   {
/* 541 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 543 */     this.stats.storeSent(null);
/*     */     
/* 545 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 547 */       this.stats.storeFailed();
/*     */       
/* 549 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 553 */       this.stats.storeOK();
/*     */       
/* 555 */       DHTTransportContact temp = new DHTTransportLoopbackContactImpl(target, this.node_id);
/*     */       
/* 557 */       temp.setRandomID(contact.getRandomID());
/*     */       
/* 559 */       DHTTransportStoreReply rep = target.getRequestHandler().storeRequest(temp, keys, value_sets);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 564 */       if (rep.blocked())
/*     */       {
/* 566 */         handler.keyBlockRequest(contact, rep.getBlockRequest(), rep.getBlockSignature());
/*     */         
/* 568 */         handler.failed(contact, new Throwable("key blocked"));
/*     */       }
/*     */       else
/*     */       {
/* 572 */         handler.storeReply(contact, rep.getDiversificationTypes());
/*     */       }
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
/*     */   public void sendQueryStore(DHTTransportContact contact, DHTTransportReplyHandler handler, int header_length, List<Object[]> key_details)
/*     */   {
/* 586 */     handler.failed(contact, new Throwable("not implemented"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindNode(final DHTTransportContact contact, final DHTTransportReplyHandler handler, final byte[] nid)
/*     */   {
/* 597 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 603 */         DHTTransportLoopbackImpl.this.sendFindNodeSupport(contact, handler, nid);
/*     */       }
/*     */       
/* 606 */     };
/* 607 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindNodeSupport(DHTTransportContact contact, DHTTransportReplyHandler handler, byte[] nid)
/*     */   {
/* 616 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 618 */     this.stats.findNodeSent(null);
/*     */     
/* 620 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 622 */       this.stats.findNodeFailed();
/*     */       
/* 624 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 628 */       this.stats.findNodeOK();
/*     */       
/* 630 */       DHTTransportContact temp = new DHTTransportLoopbackContactImpl(target, this.node_id);
/*     */       
/* 632 */       DHTTransportContact[] res = target.getRequestHandler().findNodeRequest(temp, nid);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 637 */       contact.setRandomID(temp.getRandomID());
/*     */       
/* 639 */       DHTTransportContact[] trans_res = new DHTTransportContact[res.length];
/*     */       
/* 641 */       for (int i = 0; i < res.length; i++)
/*     */       {
/* 643 */         trans_res[i] = new DHTTransportLoopbackContactImpl(this, res[i].getID());
/*     */       }
/*     */       
/* 646 */       handler.findNodeReply(contact, trans_res);
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
/*     */   public void sendFindValue(final DHTTransportContact contact, final DHTTransportReplyHandler handler, final byte[] key, final int max, final short flags)
/*     */   {
/* 660 */     AERunnable runnable = new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 666 */         DHTTransportLoopbackImpl.this.sendFindValueSupport(contact, handler, key, max, flags);
/*     */       }
/*     */       
/* 669 */     };
/* 670 */     run(runnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendFindValueSupport(DHTTransportContact contact, DHTTransportReplyHandler handler, byte[] key, int max, short flags)
/*     */   {
/* 681 */     DHTTransportLoopbackImpl target = findTarget(contact.getID());
/*     */     
/* 683 */     this.stats.findValueSent(null);
/*     */     
/* 685 */     if ((target == null) || (triggerFailure()))
/*     */     {
/* 687 */       this.stats.findValueFailed();
/*     */       
/* 689 */       handler.failed(contact, new Exception("failed"));
/*     */     }
/*     */     else
/*     */     {
/* 693 */       this.stats.findValueOK();
/*     */       
/* 695 */       DHTTransportFindValueReply find_res = target.getRequestHandler().findValueRequest(new DHTTransportLoopbackContactImpl(target, this.node_id), key, max, flags);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 700 */       if (find_res.hit())
/*     */       {
/* 702 */         handler.findValueReply(contact, find_res.getValues(), find_res.getDiversificationType(), false);
/*     */       }
/* 704 */       else if (find_res.blocked())
/*     */       {
/* 706 */         handler.keyBlockRequest(contact, find_res.getBlockedKey(), find_res.getBlockedSignature());
/*     */         
/* 708 */         handler.failed(contact, new Throwable("key blocked"));
/*     */       }
/*     */       else
/*     */       {
/* 712 */         DHTTransportContact[] res = find_res.getContacts();
/*     */         
/* 714 */         DHTTransportContact[] trans_res = new DHTTransportContact[res.length];
/*     */         
/* 716 */         for (int i = 0; i < res.length; i++)
/*     */         {
/* 718 */           trans_res[i] = new DHTTransportLoopbackContactImpl(this, res[i].getID());
/*     */         }
/*     */         
/* 721 */         handler.findValueReply(contact, trans_res);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean triggerFailure()
/*     */   {
/* 730 */     return Math.random() * 100.0D < FAIL_PERCENTAGE;
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
/*     */   public byte[] readTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, long timeout)
/*     */     throws DHTTransportException
/*     */   {
/* 765 */     throw new DHTTransportException("not imp");
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
/*     */   public void writeTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*     */     throws DHTTransportException
/*     */   {
/* 779 */     throw new DHTTransportException("not imp");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] writeReadTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] data, long timeout)
/*     */     throws DHTTransportException
/*     */   {
/* 792 */     throw new DHTTransportException("not imp");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean supportsStorage()
/*     */   {
/* 798 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(DHTTransportListener l)
/*     */   {
/* 805 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(DHTTransportListener l)
/*     */   {
/* 812 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */   public void setPort(int port) {}
/*     */   
/*     */   public void setGenericFlag(byte flag, boolean value) {}
/*     */   
/*     */   public void setSuspended(boolean susp) {}
/*     */   
/*     */   public void setTimeout(long millis) {}
/*     */   
/*     */   public void removeContact(DHTTransportContact contact) {}
/*     */   
/*     */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler) {}
/*     */   
/*     */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler, Map<String, Object> options) {}
/*     */   
/*     */   public void unregisterTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/loopback/DHTTransportLoopbackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */