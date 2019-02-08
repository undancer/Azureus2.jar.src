/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Stack;
/*     */ import java.util.WeakHashMap;
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
/*     */ public abstract class AEMonSem
/*     */ {
/*     */   protected static final boolean DEBUG = false;
/*     */   protected static final boolean DEBUG_CHECK_DUPLICATES = false;
/*     */   protected static final long DEBUG_TIMER = 30000L;
/*  37 */   private static final ThreadLocal tls = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public Object initialValue()
/*     */     {
/*     */ 
/*  43 */       return new Stack();
/*     */     }
/*     */   };
/*     */   
/*     */   private static long monitor_id_next;
/*     */   
/*     */   private static long semaphore_id_next;
/*  50 */   private static final Map debug_traces = new HashMap();
/*  51 */   static final List debug_recursions = new ArrayList();
/*  52 */   private static final List debug_reciprocals = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  57 */   private static final Map debug_name_mapping = new WeakHashMap();
/*  58 */   private static final Map debug_monitors = new WeakHashMap();
/*  59 */   private static final Map debug_semaphores = new WeakHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long entry_count;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long last_entry_count;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String last_trace_key;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final String name;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean is_monitor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void check(AEDiagnosticsLogger diag_logger)
/*     */   {
/* 116 */     List active = new ArrayList();
/* 117 */     List waiting_monitors = new ArrayList();
/* 118 */     List busy_monitors = new ArrayList();
/* 119 */     List waiting_semaphores = new ArrayList();
/*     */     
/* 121 */     synchronized (AEMonSem.class)
/*     */     {
/*     */ 
/*     */ 
/* 125 */       diag_logger.log("AEMonSem: mid = " + monitor_id_next + ", sid = " + semaphore_id_next + ", monitors = " + debug_monitors.size() + ", semaphores = " + debug_semaphores.size() + ", names = " + debug_name_mapping.size() + ", traces = " + debug_traces.size());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 134 */       Iterator it = debug_monitors.keySet().iterator();
/*     */       
/* 136 */       long new_mon_entries = 0L;
/*     */       
/* 138 */       while (it.hasNext())
/*     */       {
/* 140 */         AEMonitor monitor = (AEMonitor)it.next();
/*     */         
/* 142 */         long diff = monitor.entry_count - monitor.last_entry_count;
/*     */         
/* 144 */         if (diff != 0L)
/*     */         {
/* 146 */           active.add(monitor);
/*     */           
/* 148 */           new_mon_entries += diff;
/*     */         }
/*     */         
/* 151 */         if (monitor.waiting > 0)
/*     */         {
/* 153 */           waiting_monitors.add(monitor);
/*     */         }
/* 155 */         else if (monitor.owner != null)
/*     */         {
/* 157 */           busy_monitors.add(monitor);
/*     */         }
/*     */       }
/*     */       
/* 161 */       it = debug_semaphores.keySet().iterator();
/*     */       
/* 163 */       long new_sem_entries = 0L;
/*     */       
/* 165 */       while (it.hasNext())
/*     */       {
/* 167 */         AEMonSem semaphore = (AEMonSem)it.next();
/*     */         
/* 169 */         long diff = semaphore.entry_count - semaphore.last_entry_count;
/*     */         
/* 171 */         if (diff != 0L)
/*     */         {
/* 173 */           active.add(semaphore);
/*     */           
/* 175 */           new_sem_entries += diff;
/*     */         }
/*     */         
/* 178 */         if (semaphore.waiting > 0)
/*     */         {
/* 180 */           waiting_semaphores.add(semaphore);
/*     */         }
/*     */       }
/*     */       
/* 184 */       diag_logger.log("    activity: monitors = " + new_mon_entries + " - " + new_mon_entries / 30L + "/sec, semaphores = " + new_sem_entries + " - " + new_sem_entries / 30L + "/sec ");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 190 */     AEMonSem[] x = new AEMonSem[active.size()];
/*     */     
/* 192 */     active.toArray(x);
/*     */     
/*     */ 
/*     */ 
/* 196 */     Arrays.sort(x, new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/*     */ 
/*     */ 
/* 205 */         AEMonSem a1 = (AEMonSem)o1;
/* 206 */         AEMonSem a2 = (AEMonSem)o2;
/*     */         
/* 208 */         return a1.name.compareTo(a2.name);
/*     */       }
/*     */       
/*     */ 
/* 212 */     });
/* 213 */     AEMonSem current = null;
/* 214 */     long current_total = 0L;
/*     */     
/* 216 */     Object[][] total_x = new Object[x.length][];
/*     */     
/* 218 */     int total_pos = 0;
/*     */     
/* 220 */     for (int i = 0; i < x.length; i++)
/*     */     {
/* 222 */       AEMonSem ms = x[i];
/*     */       
/* 224 */       long diff = ms.entry_count - ms.last_entry_count;
/*     */       
/* 226 */       if (current == null)
/*     */       {
/* 228 */         current = ms;
/*     */ 
/*     */ 
/*     */       }
/* 232 */       else if (current.name.equals(ms.name))
/*     */       {
/* 234 */         current_total += diff;
/*     */       }
/*     */       else {
/* 237 */         total_x[(total_pos++)] = { current.name, new Long(current_total) };
/*     */         
/* 239 */         current = ms;
/* 240 */         current_total = diff;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 245 */     if (current != null)
/*     */     {
/* 247 */       total_x[(total_pos++)] = { current.name, new Long(current_total) };
/*     */     }
/*     */     
/* 250 */     Arrays.sort(total_x, new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/*     */ 
/*     */ 
/* 259 */         Object[] a1 = (Object[])o1;
/* 260 */         Object[] a2 = (Object[])o2;
/*     */         
/* 262 */         if ((a1 == null) && (a2 == null))
/*     */         {
/* 264 */           return 0;
/*     */         }
/* 266 */         if (a1 == null)
/*     */         {
/* 268 */           return 1;
/*     */         }
/* 270 */         if (a2 == null)
/*     */         {
/* 272 */           return -1;
/*     */         }
/*     */         
/* 275 */         long a1_count = ((Long)a1[1]).longValue();
/* 276 */         long a2_count = ((Long)a2[1]).longValue();
/*     */         
/* 278 */         return (int)(a2_count - a1_count);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 285 */     });
/* 286 */     String top_act_str = "    top activity: ";
/*     */     
/* 288 */     for (int i = 0; i < Math.min(10, total_x.length); i++)
/*     */     {
/* 290 */       if (total_x[i] != null)
/*     */       {
/* 292 */         top_act_str = top_act_str + (i == 0 ? "" : ", ") + total_x[i][0] + " = " + total_x[i][1];
/*     */       }
/*     */     }
/*     */     
/* 296 */     diag_logger.log(top_act_str);
/*     */     
/* 298 */     if (waiting_monitors.size() > 0)
/*     */     {
/* 300 */       diag_logger.log("    waiting monitors");
/*     */       
/* 302 */       for (int i = 0; i < waiting_monitors.size(); i++)
/*     */       {
/* 304 */         AEMonSem ms = (AEMonSem)waiting_monitors.get(i);
/*     */         
/* 306 */         Thread last_waiter = ((AEMonitor)ms).last_waiter;
/*     */         
/* 308 */         diag_logger.log("        [" + (last_waiter == null ? "<waiter lost>" : last_waiter.getName()) + "] " + ms.name + " - " + ms.last_trace_key);
/*     */       }
/*     */     }
/*     */     
/* 312 */     if (busy_monitors.size() > 0)
/*     */     {
/* 314 */       diag_logger.log("    busy monitors");
/*     */       
/* 316 */       for (int i = 0; i < busy_monitors.size(); i++)
/*     */       {
/* 318 */         AEMonSem ms = (AEMonSem)busy_monitors.get(i);
/*     */         
/* 320 */         Thread owner = ((AEMonitor)ms).owner;
/*     */         
/* 322 */         diag_logger.log("        [" + (owner == null ? "<owner lost>" : owner.getName()) + "] " + ms.name + " - " + ms.last_trace_key);
/*     */       }
/*     */     }
/*     */     
/* 326 */     if (waiting_semaphores.size() > 0)
/*     */     {
/* 328 */       diag_logger.log("    waiting semaphores");
/*     */       
/* 330 */       for (int i = 0; i < waiting_semaphores.size(); i++)
/*     */       {
/* 332 */         AEMonSem ms = (AEMonSem)waiting_semaphores.get(i);
/*     */         
/* 334 */         Thread last_waiter = ((AESemaphore)ms).latest_waiter;
/*     */         
/* 336 */         diag_logger.log("        [" + (last_waiter == null ? "<waiter lost>" : last_waiter.getName()) + "] " + ms.name + " - " + ms.last_trace_key);
/*     */       }
/*     */     }
/*     */     
/* 340 */     for (int i = 0; i < x.length; i++)
/*     */     {
/* 342 */       AEMonSem ms = x[i];
/*     */       
/* 344 */       ms.last_entry_count = ms.entry_count;
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
/* 356 */   protected int waiting = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AEMonSem(String _name, boolean _monitor)
/*     */   {
/* 363 */     this.is_monitor = _monitor;
/*     */     
/* 365 */     if (this.is_monitor)
/*     */     {
/* 367 */       this.name = _name;
/*     */     }
/*     */     else {
/* 370 */       this.name = StringInterner.intern("(S)" + _name);
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
/*     */   protected void debugEntry()
/*     */   {
/*     */     try
/*     */     {
/* 435 */       Stack stack = (Stack)tls.get();
/*     */       
/* 437 */       if (stack.size() > 64)
/*     */       {
/* 439 */         StringBuilder sb = new StringBuilder(1024);
/*     */         
/* 441 */         for (int i = 0; i < stack.size(); i++)
/*     */         {
/* 443 */           AEMonSem mon = (AEMonSem)stack.get(i);
/*     */           
/* 445 */           sb.append("$").append(mon.name);
/*     */         }
/*     */         
/* 448 */         Debug.out("**** Whoaaaaaa, AEMonSem debug stack is getting too large!!!! **** " + sb);
/*     */       }
/*     */       
/* 451 */       if (!stack.isEmpty())
/*     */       {
/* 453 */         String recursion_trace = "";
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
/* 469 */         StringBuilder sb = new StringBuilder();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 474 */         boolean check_recursion = (this.is_monitor) && (!debug_recursions.contains(this.name));
/*     */         
/* 476 */         String prev_name = null;
/*     */         
/* 478 */         for (int i = 0; i < stack.size(); i++)
/*     */         {
/* 480 */           AEMonSem mon = (AEMonSem)stack.get(i);
/*     */           
/* 482 */           if ((check_recursion) && 
/* 483 */             (mon.name.equals(this.name)) && (mon != this))
/*     */           {
/*     */ 
/* 486 */             recursion_trace = recursion_trace + (recursion_trace.length() == 0 ? "" : "\r\n") + "Recursive locks on different instances: " + this.name;
/*     */             
/*     */ 
/*     */ 
/* 490 */             debug_recursions.add(this.name);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 496 */           if ((prev_name == null) || (!mon.name.equals(prev_name)))
/*     */           {
/* 498 */             sb.append("$");
/* 499 */             sb.append(mon.name);
/*     */           }
/*     */           
/* 502 */           prev_name = mon.name;
/*     */         }
/*     */         
/* 505 */         sb.append("$");
/* 506 */         sb.append(this.name);
/* 507 */         sb.append("$");
/*     */         
/* 509 */         String trace_key = sb.toString();
/*     */         
/* 511 */         if (recursion_trace.length() > 0)
/*     */         {
/* 513 */           Debug.outNoStack(recursion_trace + "\r\n    " + trace_key);
/*     */         }
/*     */         
/* 516 */         this.last_trace_key = trace_key;
/*     */         
/* 518 */         if (!this.is_monitor)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 524 */           boolean match = false;
/*     */           
/* 526 */           for (int i = 0; i < stack.size(); i++)
/*     */           {
/* 528 */             AEMonSem ms = (AEMonSem)stack.get(i);
/*     */             
/* 530 */             if (ms.name.equals(this.name))
/*     */             {
/* 532 */               match = true;
/*     */               
/* 534 */               break;
/*     */             }
/*     */           }
/*     */           
/* 538 */           if (!match)
/*     */           {
/* 540 */             stack.push(this);
/*     */           }
/*     */         }
/*     */         else {
/* 544 */           stack.push(this);
/*     */         }
/*     */         
/* 547 */         synchronized (debug_traces)
/*     */         {
/* 549 */           if (debug_traces.get(trace_key) == null)
/*     */           {
/* 551 */             Thread thread = Thread.currentThread();
/*     */             
/* 553 */             String thread_name = thread.getName() + "[" + thread.hashCode() + "]";
/*     */             
/* 555 */             String stack_trace = Debug.getStackTrace(true, false);
/*     */             
/* 557 */             Iterator it = debug_traces.keySet().iterator();
/*     */             
/* 559 */             while (it.hasNext())
/*     */             {
/* 561 */               String old_key = (String)it.next();
/*     */               
/* 563 */               String[] data = (String[])debug_traces.get(old_key);
/*     */               
/* 565 */               String old_thread_name = data[0];
/* 566 */               String old_trace = data[1];
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 571 */               if (!thread_name.equals(old_thread_name))
/*     */               {
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
/* 583 */                 int earliest_common = stack.size();
/* 584 */                 int common_count = 0;
/*     */                 
/* 586 */                 for (int i = 0; i < stack.size(); i++)
/*     */                 {
/* 588 */                   String n1 = ((AEMonSem)stack.get(i)).name;
/*     */                   
/* 590 */                   int p1 = old_key.indexOf("$" + n1 + "$");
/*     */                   
/* 592 */                   if (p1 != -1)
/*     */                   {
/* 594 */                     common_count++;
/*     */                     
/* 596 */                     earliest_common = Math.min(earliest_common, i + 1);
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 602 */                 if (common_count >= 2)
/*     */                 {
/* 604 */                   for (int i = 0; i < earliest_common; i++)
/*     */                   {
/* 606 */                     AEMonSem ms1 = (AEMonSem)stack.get(i);
/*     */                     
/* 608 */                     if (ms1.is_monitor)
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/* 613 */                       String n1 = ms1.name;
/*     */                       
/* 615 */                       for (int j = i + 1; j < stack.size(); j++)
/*     */                       {
/* 617 */                         AEMonSem ms2 = (AEMonSem)stack.get(j);
/*     */                         
/* 619 */                         if (ms2.is_monitor)
/*     */                         {
/*     */ 
/*     */ 
/*     */ 
/* 624 */                           String n2 = ms2.name;
/*     */                           
/*     */ 
/*     */ 
/* 628 */                           if (!n1.equals(n2))
/*     */                           {
/* 630 */                             int p1 = old_key.indexOf("$" + n1 + "$");
/* 631 */                             int p2 = old_key.indexOf("$" + n2 + "$");
/*     */                             
/* 633 */                             if ((p1 != -1) && (p2 != -1) && (p1 > p2))
/*     */                             {
/* 635 */                               String reciprocal_log = trace_key + " / " + old_key;
/*     */                               
/* 637 */                               if (!debug_reciprocals.contains(reciprocal_log))
/*     */                               {
/* 639 */                                 debug_reciprocals.add(reciprocal_log);
/*     */                                 
/* 641 */                                 Debug.outNoStack("AEMonSem: Reciprocal usage:\r\n    " + trace_key + "\r\n" + "        [" + thread_name + "] " + stack_trace + "\r\n" + "    " + old_key + "\r\n" + "        [" + old_thread_name + "] " + old_trace);
/*     */                               }
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 655 */             debug_traces.put(trace_key, new String[] { thread_name, stack_trace });
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 663 */         this.last_trace_key = ("$" + this.name + "$");
/*     */         
/* 665 */         stack.push(this);
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try {
/* 671 */         Debug.printStackTrace(e);
/*     */       }
/*     */       catch (Throwable f) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void debugExit()
/*     */   {
/*     */     try
/*     */     {
/* 683 */       Stack stack = (Stack)tls.get();
/*     */       
/* 685 */       if (this.is_monitor)
/*     */       {
/*     */ 
/*     */ 
/* 689 */         while (stack.peek() != this)
/*     */         {
/* 691 */           stack.pop();
/*     */         }
/*     */         
/* 694 */         stack.pop();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 702 */       else if (!stack.isEmpty())
/*     */       {
/* 704 */         if (stack.peek() == this)
/*     */         {
/* 706 */           stack.pop();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try {
/* 713 */         Debug.printStackTrace(e);
/*     */       }
/*     */       catch (Throwable f) {}
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
/*     */   public String getName()
/*     */   {
/* 787 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class monSemData
/*     */   {
/*     */     protected final String class_name;
/*     */     
/*     */ 
/*     */     protected final int line_number;
/*     */     
/*     */ 
/*     */     protected monSemData(String _class_name, int _line_number)
/*     */     {
/* 802 */       this.class_name = _class_name;
/* 803 */       this.line_number = _line_number;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEMonSem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */