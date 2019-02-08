/*      */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*      */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualAbstractSelectorListener;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.Socket;
/*      */ import java.nio.channels.CancelledKeyException;
/*      */ import java.nio.channels.SelectionKey;
/*      */ import java.nio.channels.Selector;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.nio.channels.spi.AbstractSelectableChannel;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class VirtualChannelSelectorImpl
/*      */ {
/*   41 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*      */   
/*      */ 
/*      */   private static final boolean MAYBE_BROKEN_SELECT;
/*      */   
/*      */ 
/*      */   private static final int SELECTOR_TIMEOUT = 15000;
/*      */   
/*      */ 
/*      */   static
/*      */   {
/*   52 */     String jvm_name = System.getProperty("java.vm.name", "");
/*      */     
/*   54 */     boolean is_diablo = jvm_name.startsWith("Diablo");
/*      */     
/*   56 */     boolean is_freebsd_7_or_higher = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*   63 */       if ((Constants.isFreeBSD) || (Constants.isLinux))
/*      */       {
/*   65 */         String os_type = System.getenv("OSTYPE");
/*      */         
/*   67 */         if ((os_type != null) && (os_type.equals("FreeBSD")))
/*      */         {
/*   69 */           String os_version = System.getProperty("os.version", "");
/*      */           
/*   71 */           String digits = "";
/*      */           
/*   73 */           for (int i = 0; i < os_version.length(); i++)
/*      */           {
/*   75 */             char c = os_version.charAt(i);
/*      */             
/*   77 */             if (!Character.isDigit(c))
/*      */               break;
/*   79 */             digits = digits + c;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   87 */           if (digits.length() > 0)
/*      */           {
/*   89 */             is_freebsd_7_or_higher = Integer.parseInt(digits) >= 7;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*   95 */       e.printStackTrace();
/*      */     }
/*      */     
/*   98 */     MAYBE_BROKEN_SELECT = (is_freebsd_7_or_higher) || (is_diablo) || (Constants.isOSX_10_6_OrHigher);
/*      */     
/*  100 */     if (MAYBE_BROKEN_SELECT)
/*      */     {
/*  102 */       System.out.println("Enabling broken select detection: diablo=" + is_diablo + ", freebsd 7+=" + is_freebsd_7_or_higher + ", osx 10.6+=" + Constants.isOSX_10_6_OrHigher);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  108 */   static final AESemaphore get_selector_allowed = new AESemaphore("getSelectorAllowed", 1);
/*      */   private boolean select_is_broken;
/*      */   private int select_looks_broken_count;
/*      */   private boolean logged_broken_select;
/*      */   protected Selector selector;
/*      */   private final SelectorGuard selector_guard;
/*      */   private int consec_select_fails;
/*      */   private long consec_select_fails_start;
/*      */   
/*  117 */   private static class SelectorTimeoutException extends IOException { private SelectorTimeoutException() { super(); }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Selector getSelector()
/*      */     throws IOException
/*      */   {
/*  128 */     if (!get_selector_allowed.reserve(15000L))
/*      */     {
/*  130 */       Debug.out("Selector timeout (existing incomplete)");
/*      */       
/*  132 */       throw new SelectorTimeoutException(null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  137 */     final Object[] result = { null };
/*      */     
/*  139 */     final AESemaphore sem = new AESemaphore("getSelector");
/*      */     
/*  141 */     synchronized (VirtualChannelSelectorImpl.class)
/*      */     {
/*      */       try {
/*  144 */         final TimerEvent event = SimpleTimer.addEvent("getSelector", SystemTime.getOffsetTime(15000L), new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  154 */             synchronized (VirtualChannelSelectorImpl.class)
/*      */             {
/*  156 */               if (this.val$result[0] == null)
/*      */               {
/*  158 */                 Debug.out("Selector timeout");
/*      */                 
/*  160 */                 this.val$result[0] = new VirtualChannelSelectorImpl.SelectorTimeoutException(null);
/*      */                 
/*  162 */                 sem.release();
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*  167 */         });
/*  168 */         new AEThread2("getSelector")
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  174 */               Selector sel = Selector.open();
/*      */               
/*  176 */               synchronized (VirtualChannelSelectorImpl.class)
/*      */               {
/*  178 */                 if (result[0] == null)
/*      */                 {
/*  180 */                   result[0] = sel; return;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  186 */               sel.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  190 */               synchronized (VirtualChannelSelectorImpl.class)
/*      */               {
/*  192 */                 if (result[0] == null)
/*      */                 {
/*  194 */                   if ((e instanceof IOException))
/*      */                   {
/*  196 */                     result[0] = e;
/*      */                   }
/*      */                   else
/*      */                   {
/*  200 */                     result[0] = new IOException(Debug.getNestedExceptionMessage(e));
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/*  206 */               VirtualChannelSelectorImpl.get_selector_allowed.release();
/*      */               
/*  208 */               sem.release();
/*      */               
/*  210 */               event.cancel();
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  217 */         get_selector_allowed.release();
/*      */         
/*  219 */         throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/*  223 */     sem.reserve();
/*      */     
/*      */ 
/*      */ 
/*  227 */     if ((result[0] instanceof IOException))
/*      */     {
/*  229 */       throw ((IOException)result[0]);
/*      */     }
/*      */     
/*  232 */     return (Selector)result[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  270 */   private final LinkedList<Object> register_cancel_list = new LinkedList();
/*  271 */   private final AEMonitor register_cancel_list_mon = new AEMonitor("VirtualChannelSelector:RCL");
/*      */   
/*  273 */   private final HashMap<AbstractSelectableChannel, Boolean> paused_states = new HashMap();
/*      */   
/*      */ 
/*      */   private final int INTEREST_OP;
/*      */   
/*      */ 
/*      */   private final boolean pause_after_select;
/*      */   
/*      */ 
/*      */   protected final VirtualChannelSelector parent;
/*      */   
/*      */   private volatile boolean destroyed;
/*      */   
/*      */   private boolean randomise_keys;
/*      */   
/*  288 */   private int next_select_loop_pos = 0;
/*      */   
/*      */   private static final int WRITE_SELECTOR_DEBUG_CHECK_PERIOD = 10000;
/*      */   
/*      */   private static final int WRITE_SELECTOR_DEBUG_MAX_TIME = 20000;
/*      */   
/*      */   private long last_write_select_debug;
/*      */   private long last_select_debug;
/*  296 */   private long last_reopen_attempt = SystemTime.getMonotonousTime();
/*      */   
/*      */   public VirtualChannelSelectorImpl(VirtualChannelSelector _parent, int _interest_op, boolean _pause_after_select, boolean _randomise_keys) {
/*  299 */     this.parent = _parent;
/*  300 */     this.INTEREST_OP = _interest_op;
/*      */     
/*  302 */     this.pause_after_select = _pause_after_select;
/*  303 */     this.randomise_keys = _randomise_keys;
/*      */     
/*      */     String type;
/*  306 */     switch (this.INTEREST_OP) {
/*      */     case 8: 
/*  308 */       type = "OP_CONNECT"; break;
/*      */     case 1: 
/*  310 */       type = "OP_READ"; break;
/*      */     default: 
/*  312 */       type = "OP_WRITE";
/*      */     }
/*      */     
/*      */     
/*  316 */     this.selector_guard = new SelectorGuard(type, new SelectorGuard.GuardListener() {
/*      */       public boolean safeModeSelectEnabled() {
/*  318 */         return VirtualChannelSelectorImpl.this.parent.isSafeSelectionModeEnabled();
/*      */       }
/*      */       
/*      */       public void spinDetected() {
/*  322 */         VirtualChannelSelectorImpl.this.closeExistingSelector();
/*  323 */         try { Thread.sleep(1000L); } catch (Throwable x) { x.printStackTrace(); }
/*  324 */         VirtualChannelSelectorImpl.this.parent.enableSafeSelectionMode();
/*      */       }
/*      */       
/*      */       public void failureDetected() {
/*  328 */         try { Thread.sleep(10000L); } catch (Throwable x) { x.printStackTrace(); }
/*  329 */         VirtualChannelSelectorImpl.this.closeExistingSelector();
/*  330 */         try { Thread.sleep(1000L); } catch (Throwable x) { x.printStackTrace(); }
/*  331 */         VirtualChannelSelectorImpl.this.selector = VirtualChannelSelectorImpl.this.openNewSelector();
/*      */       }
/*      */       
/*  334 */     });
/*  335 */     this.selector = openNewSelector();
/*      */   }
/*      */   
/*      */ 
/*      */   protected Selector openNewSelector()
/*      */   {
/*  341 */     Selector sel = null;
/*      */     
/*  343 */     int MAX_TRIES = 10;
/*      */     try
/*      */     {
/*  346 */       sel = getSelector();
/*      */       
/*  348 */       AEDiagnostics.logWithStack("seltrace", "Selector created for '" + this.parent.getName() + "'," + this.selector_guard.getType());
/*      */     }
/*      */     catch (Throwable t) {
/*  351 */       Debug.out("ERROR: caught exception on Selector.open()", t);
/*      */       try {
/*  353 */         Thread.sleep(3000L); } catch (Throwable x) { x.printStackTrace();
/*      */       }
/*  355 */       int fail_count = (t instanceof SelectorTimeoutException) ? 1000 : 1;
/*      */       
/*  357 */       while (fail_count < 10) {
/*      */         try
/*      */         {
/*  360 */           sel = getSelector();
/*      */           
/*  362 */           AEDiagnostics.logWithStack("seltrace", "Selector created for '" + this.parent.getName() + "'," + this.selector_guard.getType());
/*      */ 
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/*      */ 
/*  368 */           Debug.out(f);
/*      */           
/*  370 */           if ((f instanceof SelectorTimeoutException))
/*      */           {
/*  372 */             fail_count = 1000;
/*      */           }
/*      */           else
/*      */           {
/*  376 */             fail_count++;
/*      */           }
/*      */           
/*  379 */           if (fail_count < 10) {
/*      */             try {
/*  381 */               Thread.sleep(3000L); } catch (Throwable x) { x.printStackTrace();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  390 */       if (fail_count < 10) {
/*  391 */         Debug.out("NOTICE: socket Selector successfully opened after " + fail_count + " failures.");
/*      */       }
/*      */       else {
/*  394 */         Logger.log(new LogAlert(true, 3, "ERROR: socket Selector.open() failed " + (fail_count == 1000 ? "due to timeout" : "10 times in a row") + ", aborting." + "\nAzureus / Java is likely being firewalled!"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  400 */     return sel;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRandomiseKeys(boolean r)
/*      */   {
/*  408 */     this.randomise_keys = r;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void pauseSelects(AbstractSelectableChannel channel)
/*      */   {
/*  417 */     if (channel == null) {
/*  418 */       return;
/*      */     }
/*      */     
/*  421 */     SelectionKey key = channel.keyFor(this.selector);
/*      */     
/*  423 */     if ((key != null) && (key.isValid())) {
/*  424 */       key.interestOps(key.interestOps() & (this.INTEREST_OP ^ 0xFFFFFFFF));
/*      */ 
/*      */     }
/*  427 */     else if (channel.isOpen()) {
/*  428 */       try { this.register_cancel_list_mon.enter();
/*      */         
/*  430 */         this.paused_states.put(channel, Boolean.TRUE);
/*      */       }
/*      */       finally {
/*  433 */         this.register_cancel_list_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void resumeSelects(AbstractSelectableChannel channel)
/*      */   {
/*  443 */     if (channel == null) {
/*  444 */       Debug.printStackTrace(new Exception("resumeSelects():: channel == null"));
/*  445 */       return;
/*      */     }
/*      */     
/*  448 */     SelectionKey key = channel.keyFor(this.selector);
/*      */     
/*  450 */     if ((key != null) && (key.isValid()))
/*      */     {
/*      */ 
/*  453 */       if ((key.interestOps() & this.INTEREST_OP) == 0) {
/*  454 */         RegistrationData data = (RegistrationData)key.attachment();
/*      */         
/*  456 */         data.last_select_success_time = SystemTime.getCurrentTime();
/*  457 */         data.non_progress_count = 0;
/*      */       }
/*  459 */       key.interestOps(key.interestOps() | this.INTEREST_OP);
/*      */     } else {
/*      */       try {
/*  462 */         this.register_cancel_list_mon.enter();
/*  463 */         this.paused_states.remove(channel);
/*      */       } finally {
/*  465 */         this.register_cancel_list_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void cancel(AbstractSelectableChannel channel)
/*      */   {
/*  483 */     if ((!this.destroyed) || 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  488 */       (channel == null))
/*      */     {
/*  490 */       Debug.out("Attempt to cancel selects for null channel");
/*      */       
/*  492 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  496 */       this.register_cancel_list_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  501 */       for (Iterator<Object> it = this.register_cancel_list.iterator(); it.hasNext();)
/*      */       {
/*  503 */         Object obj = it.next();
/*      */         
/*  505 */         if ((channel == obj) || (((obj instanceof RegistrationData)) && (((RegistrationData)obj).channel == channel)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  511 */           it.remove();
/*      */           
/*  513 */           break;
/*      */         }
/*      */       }
/*      */       
/*  517 */       pauseSelects(channel);
/*      */       
/*  519 */       this.register_cancel_list.add(channel);
/*      */     }
/*      */     finally
/*      */     {
/*  523 */       this.register_cancel_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void register(AbstractSelectableChannel channel, VirtualChannelSelector.VirtualAbstractSelectorListener listener, Object attachment)
/*      */   {
/*  535 */     if (this.destroyed)
/*      */     {
/*  537 */       Debug.out("register called after selector destroyed");
/*      */     }
/*      */     
/*  540 */     if (channel == null)
/*      */     {
/*  542 */       Debug.out("Attempt to register selects for null channel");
/*      */       
/*  544 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  548 */       this.register_cancel_list_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  553 */       for (Iterator<Object> it = this.register_cancel_list.iterator(); it.hasNext();)
/*      */       {
/*  555 */         Object obj = it.next();
/*      */         
/*  557 */         if ((channel == obj) || (((obj instanceof RegistrationData)) && (((RegistrationData)obj).channel == channel)))
/*      */         {
/*      */ 
/*      */ 
/*  561 */           it.remove();
/*      */           
/*  563 */           break;
/*      */         }
/*      */       }
/*      */       
/*  567 */       this.paused_states.remove(channel);
/*      */       
/*  569 */       this.register_cancel_list.add(new RegistrationData(channel, listener, attachment, null));
/*      */     }
/*      */     finally
/*      */     {
/*  573 */       this.register_cancel_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int select(long timeout)
/*      */   {
/*  581 */     long select_start_time = SystemTime.getCurrentTime();
/*      */     
/*  583 */     if (this.selector == null) {
/*  584 */       long mono_now = SystemTime.getMonotonousTime();
/*  585 */       if ((mono_now - this.last_reopen_attempt > 60000L) && (!this.destroyed)) {
/*  586 */         this.last_reopen_attempt = mono_now;
/*  587 */         this.selector = openNewSelector();
/*      */       }
/*  589 */       if (this.selector == null) {
/*  590 */         Debug.out("VirtualChannelSelector.select() op called with null selector");
/*  591 */         try { Thread.sleep(3000L); } catch (Throwable x) { x.printStackTrace(); }
/*  592 */         return 0;
/*      */       }
/*      */     }
/*      */     
/*  596 */     if (!this.selector.isOpen()) {
/*  597 */       Debug.out("VirtualChannelSelector.select() op called with closed selector");
/*  598 */       try { Thread.sleep(3000L); } catch (Throwable x) { x.printStackTrace(); }
/*  599 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  605 */     RegistrationData select_fail_data = null;
/*  606 */     Throwable select_fail_excep = null;
/*      */     
/*      */     try
/*      */     {
/*  610 */       this.register_cancel_list_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  620 */       while (this.register_cancel_list.size() > 0)
/*      */       {
/*  622 */         Object obj = this.register_cancel_list.remove(0);
/*      */         
/*  624 */         if ((obj instanceof AbstractSelectableChannel))
/*      */         {
/*      */ 
/*      */ 
/*  628 */           AbstractSelectableChannel canceled_channel = (AbstractSelectableChannel)obj;
/*      */           try
/*      */           {
/*  631 */             SelectionKey key = canceled_channel.keyFor(this.selector);
/*      */             
/*  633 */             if (key != null)
/*      */             {
/*  635 */               key.cancel();
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  640 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  645 */           RegistrationData data = (RegistrationData)obj;
/*      */           
/*      */           try
/*      */           {
/*  649 */             if (data == null) {
/*  650 */               throw new Exception("data == null");
/*      */             }
/*      */             
/*  653 */             if (data.channel == null) {
/*  654 */               throw new Exception("data.channel == null");
/*      */             }
/*      */             
/*  657 */             if (data.channel.isOpen())
/*      */             {
/*      */ 
/*  660 */               SelectionKey key = data.channel.keyFor(this.selector);
/*      */               
/*  662 */               if ((key != null) && (key.isValid())) {
/*  663 */                 key.attach(data);
/*  664 */                 key.interestOps(key.interestOps() | this.INTEREST_OP);
/*      */               }
/*      */               else {
/*  667 */                 data.channel.register(this.selector, this.INTEREST_OP, data);
/*      */               }
/*      */               
/*      */ 
/*  671 */               Object paused = this.paused_states.get(data.channel);
/*      */               
/*  673 */               if (paused != null) {
/*  674 */                 pauseSelects(data.channel);
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/*  679 */               select_fail_data = data;
/*  680 */               select_fail_excep = new Throwable("select registration: channel is closed");
/*      */             }
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*  685 */             Debug.printStackTrace(t);
/*      */             
/*  687 */             select_fail_data = data;
/*  688 */             select_fail_excep = t;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  693 */       this.paused_states.clear();
/*      */     }
/*      */     finally
/*      */     {
/*  697 */       this.register_cancel_list_mon.exit();
/*      */     }
/*      */     
/*  700 */     if (select_fail_data != null) {
/*      */       try
/*      */       {
/*  703 */         this.parent.selectFailure(select_fail_data.listener, select_fail_data.channel, select_fail_data.attachment, select_fail_excep);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*      */ 
/*  711 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  717 */     int count = 0;
/*      */     
/*  719 */     this.selector_guard.markPreSelectTime();
/*      */     try
/*      */     {
/*  722 */       count = this.selector.select(timeout);
/*      */       
/*  724 */       this.consec_select_fails = 0;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  728 */       long now = SystemTime.getMonotonousTime();
/*      */       
/*  730 */       this.consec_select_fails += 1;
/*      */       
/*  732 */       if (this.consec_select_fails == 1)
/*      */       {
/*  734 */         this.consec_select_fails_start = now;
/*      */       }
/*      */       
/*  737 */       if ((this.consec_select_fails > 20) && (this.consec_select_fails_start - now > 16000L))
/*      */       {
/*  739 */         this.consec_select_fails = 0;
/*      */         
/*  741 */         Debug.out("Consecutive fail exceeded (" + this.consec_select_fails + ") - recreating selector");
/*      */         
/*  743 */         closeExistingSelector();
/*      */         try {
/*  745 */           Thread.sleep(1000L); } catch (Throwable x) { x.printStackTrace();
/*      */         }
/*  747 */         this.selector = openNewSelector();
/*      */         
/*  749 */         return 0;
/*      */       }
/*      */       
/*  752 */       if (now - this.last_select_debug > 5000L)
/*      */       {
/*  754 */         this.last_select_debug = now;
/*      */         
/*  756 */         String msg = t.getMessage();
/*      */         
/*  758 */         if ((msg == null) || (!msg.equalsIgnoreCase("bad file descriptor")))
/*      */         {
/*  760 */           Debug.out("Caught exception on selector.select() op: " + msg, t); }
/*      */       }
/*      */       try {
/*  763 */         Thread.sleep(timeout); } catch (Throwable e) { e.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  769 */     if (this.destroyed)
/*      */     {
/*  771 */       closeExistingSelector();
/*      */       
/*  773 */       return 0;
/*      */     }
/*      */     
/*  776 */     if ((MAYBE_BROKEN_SELECT) && (!this.select_is_broken) && ((this.INTEREST_OP == 1) || (this.INTEREST_OP == 4)))
/*      */     {
/*      */ 
/*      */ 
/*  780 */       if (this.selector.selectedKeys().size() == 0)
/*      */       {
/*  782 */         Set<SelectionKey> keys = this.selector.keys();
/*      */         
/*  784 */         for (SelectionKey key : keys)
/*      */         {
/*  786 */           if ((key.readyOps() & this.INTEREST_OP) != 0)
/*      */           {
/*  788 */             this.select_looks_broken_count += 1;
/*      */             
/*  790 */             break;
/*      */           }
/*      */         }
/*      */         
/*  794 */         if (this.select_looks_broken_count >= 5)
/*      */         {
/*  796 */           this.select_is_broken = true;
/*      */           
/*  798 */           if (!this.logged_broken_select)
/*      */           {
/*  800 */             this.logged_broken_select = true;
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  808 */         this.select_looks_broken_count = 0;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  831 */     this.selector_guard.verifySelectorIntegrity(count, 12L);
/*      */     
/*  833 */     if (!this.selector.isOpen()) { return count;
/*      */     }
/*  835 */     int progress_made_key_count = 0;
/*  836 */     int total_key_count = 0;
/*      */     
/*  838 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  844 */     Set<SelectionKey> non_selected_keys = null;
/*      */     
/*  846 */     if (this.INTEREST_OP == 4)
/*      */     {
/*  848 */       if ((now < this.last_write_select_debug) || (now - this.last_write_select_debug > 10000L))
/*      */       {
/*      */ 
/*  851 */         this.last_write_select_debug = now;
/*      */         
/*  853 */         non_selected_keys = new HashSet(this.selector.keys());
/*      */       }
/*      */     }
/*      */     
/*      */     List<SelectionKey> ready_keys;
/*      */     List<SelectionKey> ready_keys;
/*  859 */     if ((MAYBE_BROKEN_SELECT) && (this.select_is_broken))
/*      */     {
/*  861 */       Set<SelectionKey> all_keys = this.selector.keys();
/*      */       
/*  863 */       ready_keys = new ArrayList();
/*      */       
/*  865 */       for (SelectionKey key : all_keys)
/*      */       {
/*  867 */         if ((key.readyOps() & this.INTEREST_OP) != 0)
/*      */         {
/*  869 */           ready_keys.add(key);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  874 */       Set<SelectionKey> selected = this.selector.selectedKeys();
/*      */       List<SelectionKey> ready_keys;
/*  876 */       if (selected.size() == 0)
/*      */       {
/*  878 */         ready_keys = Collections.emptyList();
/*      */       }
/*      */       else
/*      */       {
/*  882 */         ready_keys = new ArrayList(selected);
/*      */       }
/*      */     }
/*      */     
/*  886 */     boolean randy = this.randomise_keys;
/*      */     
/*  888 */     if (randy)
/*      */     {
/*  890 */       Collections.shuffle(ready_keys);
/*      */     }
/*      */     
/*  893 */     Set<SelectionKey> selected_keys = this.selector.selectedKeys();
/*      */     
/*  895 */     int ready_key_size = ready_keys.size();
/*  896 */     int start_pos = this.next_select_loop_pos++;
/*  897 */     int end_pos = start_pos + ready_key_size;
/*      */     
/*  899 */     for (int i = start_pos; i < end_pos; i++)
/*      */     {
/*  901 */       SelectionKey key = (SelectionKey)ready_keys.get(i % ready_key_size);
/*      */       
/*  903 */       total_key_count++;
/*      */       
/*  905 */       selected_keys.remove(key);
/*      */       
/*  907 */       RegistrationData data = (RegistrationData)key.attachment();
/*      */       
/*  909 */       if (non_selected_keys != null)
/*      */       {
/*  911 */         non_selected_keys.remove(key);
/*      */       }
/*      */       
/*  914 */       data.last_select_success_time = now;
/*      */       
/*      */ 
/*  917 */       if (key.isValid()) {
/*  918 */         if ((key.interestOps() & this.INTEREST_OP) != 0)
/*      */         {
/*      */ 
/*      */ 
/*  922 */           if (this.pause_after_select) {
/*      */             try
/*      */             {
/*  925 */               key.interestOps(key.interestOps() & (this.INTEREST_OP ^ 0xFFFFFFFF));
/*      */             }
/*      */             catch (CancelledKeyException e) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  932 */           boolean progress_indicator = this.parent.selectSuccess(data.listener, data.channel, data.attachment);
/*      */           
/*  934 */           if (progress_indicator)
/*      */           {
/*      */ 
/*      */ 
/*  938 */             progress_made_key_count++;
/*      */             
/*  940 */             data.non_progress_count = 0;
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  946 */             data.non_progress_count += 1;
/*      */             
/*  948 */             boolean loopback_connection = false;
/*      */             
/*  950 */             if (this.INTEREST_OP != 16)
/*      */             {
/*  952 */               SocketChannel sc = (SocketChannel)data.channel;
/*      */               
/*  954 */               Socket socket = sc.socket();
/*      */               
/*  956 */               InetAddress address = socket.getInetAddress();
/*      */               
/*  958 */               if (address != null)
/*      */               {
/*  960 */                 loopback_connection = address.isLoopbackAddress();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  967 */             if (loopback_connection)
/*      */             {
/*  969 */               if (data.non_progress_count == 10000)
/*      */               {
/*  971 */                 Debug.out("No progress for " + data.non_progress_count + ", closing connection");
/*      */                 try
/*      */                 {
/*  974 */                   data.channel.close();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  978 */                   e.printStackTrace();
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*  983 */             else if ((data.non_progress_count == 10) || ((data.non_progress_count % 100 == 0) && (data.non_progress_count > 0)))
/*      */             {
/*      */ 
/*  986 */               boolean do_log = true;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  991 */               if ((data.non_progress_count == 10) && (this.INTEREST_OP == 4))
/*      */               {
/*  993 */                 do_log = false;
/*      */               }
/*      */               
/*  996 */               if (do_log)
/*      */               {
/*  998 */                 Debug.out("VirtualChannelSelector: No progress for op " + this.INTEREST_OP + ": listener = " + data.listener.getClass() + ", count = " + data.non_progress_count + ", socket: open = " + data.channel.isOpen() + (this.INTEREST_OP == 16 ? "" : new StringBuilder().append(", connected = ").append(((SocketChannel)data.channel).isConnected()).toString()));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1007 */               if (data.non_progress_count == 1000)
/*      */               {
/* 1009 */                 Debug.out("No progress for " + data.non_progress_count + ", closing connection");
/*      */                 try
/*      */                 {
/* 1012 */                   data.channel.close();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1016 */                   e.printStackTrace();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1025 */         key.cancel();
/* 1026 */         this.parent.selectFailure(data.listener, data.channel, data.attachment, new Throwable("key is invalid"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Iterator<SelectionKey> i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1049 */     if (non_selected_keys != null)
/*      */     {
/* 1051 */       for (i = non_selected_keys.iterator(); i.hasNext();)
/*      */       {
/* 1053 */         SelectionKey key = (SelectionKey)i.next();
/*      */         
/* 1055 */         RegistrationData data = (RegistrationData)key.attachment();
/*      */         try
/*      */         {
/* 1058 */           if ((key.interestOps() & this.INTEREST_OP) == 0) {
/*      */             continue;
/*      */           }
/*      */         }
/*      */         catch (CancelledKeyException e) {}
/*      */         
/*      */ 
/*      */ 
/* 1066 */         continue;
/*      */         
/*      */ 
/* 1069 */         long stall_time = now - data.last_select_success_time;
/*      */         
/* 1071 */         if (stall_time < 0L)
/*      */         {
/* 1073 */           data.last_select_success_time = now;
/*      */ 
/*      */ 
/*      */         }
/* 1077 */         else if (stall_time > 20000L)
/*      */         {
/* 1079 */           Logger.log(new LogEvent(LOGID, 1, "Write select for " + key.channel() + " stalled for " + stall_time));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1084 */           if (key.isValid())
/*      */           {
/* 1086 */             if (this.pause_after_select)
/*      */             {
/* 1088 */               key.interestOps(key.interestOps() & (this.INTEREST_OP ^ 0xFFFFFFFF));
/*      */             }
/*      */             
/* 1091 */             if (this.parent.selectSuccess(data.listener, data.channel, data.attachment))
/*      */             {
/* 1093 */               data.non_progress_count = 0;
/*      */             }
/*      */           }
/*      */           else {
/* 1097 */             key.cancel();
/*      */             
/* 1099 */             this.parent.selectFailure(data.listener, data.channel, data.attachment, new Throwable("key is invalid"));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1110 */     if ((total_key_count == 0) || (progress_made_key_count != total_key_count))
/*      */     {
/* 1112 */       long time_diff = SystemTime.getCurrentTime() - select_start_time;
/*      */       
/* 1114 */       if ((time_diff < timeout) && (time_diff >= 0L)) {
/* 1115 */         try { Thread.sleep(timeout - time_diff); } catch (Throwable e) { e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1161 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1172 */     this.destroyed = true;
/*      */   }
/*      */   
/*      */   protected void closeExistingSelector() {
/* 1176 */     for (Iterator<SelectionKey> i = this.selector.keys().iterator(); i.hasNext();) {
/* 1177 */       SelectionKey key = (SelectionKey)i.next();
/* 1178 */       RegistrationData data = (RegistrationData)key.attachment();
/* 1179 */       this.parent.selectFailure(data.listener, data.channel, data.attachment, new Throwable("selector destroyed"));
/*      */     }
/*      */     try
/*      */     {
/* 1183 */       this.selector.close();
/*      */       
/* 1185 */       AEDiagnostics.log("seltrace", "Selector destroyed for '" + this.parent.getName() + "'," + this.selector_guard.getType());
/*      */     } catch (Throwable t) {
/* 1187 */       t.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class RegistrationData
/*      */   {
/*      */     protected final AbstractSelectableChannel channel;
/*      */     protected final VirtualChannelSelector.VirtualAbstractSelectorListener listener;
/*      */     protected final Object attachment;
/*      */     protected int non_progress_count;
/*      */     protected long last_select_success_time;
/*      */     
/*      */     private RegistrationData(AbstractSelectableChannel _channel, VirtualChannelSelector.VirtualAbstractSelectorListener _listener, Object _attachment)
/*      */     {
/* 1202 */       this.channel = _channel;
/* 1203 */       this.listener = _listener;
/* 1204 */       this.attachment = _attachment;
/*      */       
/* 1206 */       this.last_select_success_time = SystemTime.getCurrentTime();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/VirtualChannelSelectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */