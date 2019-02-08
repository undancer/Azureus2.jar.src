/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionScheduler;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*     */ public class Subscriptions
/*     */   extends IConsoleCommand
/*     */ {
/*     */   private Subscription[] current_subs;
/*     */   private Subscription current_sub;
/*     */   private List<SubscriptionResult> current_results;
/*     */   
/*     */   public Subscriptions()
/*     */   {
/*  40 */     super("subscriptions", "subs");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  45 */     return "subscriptions\t\tsubs\tAccess to subscriptions.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  49 */     out.println("> -----");
/*  50 */     out.println("Subcommands:");
/*  51 */     out.println("\tlist\t: List subscriptions");
/*  52 */     out.println("\tcreate <name> <rss_url>\t: Create a new subscription");
/*  53 */     out.println("\tselect <number>\t: Select subscription <number> for further operations");
/*  54 */     out.println("The following commands operate on a selected subscription");
/*  55 */     out.println("\tupdate \t: Update the subscription");
/*  56 */     out.println("\tset_autodownload [yes|no] \t: Set the auto-download setting");
/*  57 */     out.println("\tset_updatemins <number>\t: Set the refresh frequency to <number> minutes");
/*  58 */     out.println("\tresults [all]\t: List the subscription results, unread only unless 'all' supplied");
/*  59 */     out.println("\tset_read [<number>|all]\t: Mark specified result, or all, as read");
/*  60 */     out.println("\tset_unread [<number>|all]\t: Mark specified result, or all, as un-read");
/*  61 */     out.println("\tdownload [<number>|all]\t: Download the specified result, or all");
/*  62 */     out.println("\tdelete\t: Delete the subscription");
/*  63 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*     */   {
/*  70 */     if (args.isEmpty())
/*     */     {
/*  72 */       printHelp(ci.out, args);
/*     */       
/*  74 */       return;
/*     */     }
/*     */     
/*  77 */     String cmd = (String)args.get(0);
/*     */     
/*  79 */     SubscriptionManager sub_man = SubscriptionManagerFactory.getSingleton();
/*     */     
/*  81 */     if (cmd.equals("list"))
/*     */     {
/*  83 */       ci.out.println("> -----");
/*     */       
/*  85 */       this.current_subs = sub_man.getSubscriptions(true);
/*     */       
/*  87 */       int index = 1;
/*     */       
/*  89 */       for (Subscription sub : this.current_subs)
/*     */       {
/*  91 */         SubscriptionHistory history = sub.getHistory();
/*     */         
/*  93 */         String index_str = "" + index++;
/*     */         
/*  95 */         while (index_str.length() < 3) {
/*  96 */           index_str = index_str + " ";
/*     */         }
/*     */         
/*  99 */         String str = index_str + sub.getName() + ", unread=" + history.getNumUnread() + ", auto_download=" + (history.isAutoDownload() ? "yes" : "no");
/*     */         
/* 101 */         str = str + ", check_period=" + history.getCheckFrequencyMins() + " mins";
/*     */         
/* 103 */         long last_check = history.getLastScanTime();
/*     */         
/* 105 */         str = str + ", last_check=" + (last_check <= 0L ? "never" : new SimpleDateFormat("yy/MM/dd HH:mm").format(Long.valueOf(last_check)));
/*     */         
/* 107 */         String last_error = history.getLastError();
/*     */         
/* 109 */         if ((last_error != null) && (last_error.length() > 0))
/*     */         {
/* 111 */           str = str + ", last_error=" + last_error;
/*     */         }
/*     */         
/* 114 */         ci.out.println(str);
/*     */       }
/*     */       
/* 117 */       if (this.current_subs.length == 0)
/*     */       {
/* 119 */         ci.out.println("No Subscriptions");
/*     */       }
/* 121 */     } else if (cmd.equals("create"))
/*     */     {
/* 123 */       if (args.size() < 3)
/*     */       {
/* 125 */         ci.out.println("Usage: subs create <name> <rss_feed_url>");
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 130 */           sub_man.createRSS((String)args.get(1), new URL((String)args.get(2)), 120, new HashMap());
/*     */           
/* 132 */           ci.out.println("Subscription created");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 136 */           ci.out.println("Failed to create subscription: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/*     */     }
/* 140 */     else if (cmd.equals("select"))
/*     */     {
/* 142 */       if (args.size() < 2)
/*     */       {
/* 144 */         ci.out.println("Usage: subs select <number>");
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 149 */           int index = Integer.parseInt((String)args.get(1));
/*     */           
/* 151 */           if (this.current_subs == null)
/*     */           {
/* 153 */             throw new Exception("subscriptions must be listed prior to being selected");
/*     */           }
/* 155 */           if (this.current_subs.length == 0)
/*     */           {
/* 157 */             throw new Exception("no subscriptions exist");
/*     */           }
/* 159 */           if ((index < 0) || (index > this.current_subs.length))
/*     */           {
/* 161 */             throw new Exception("subscription index '" + index + "' is out of range");
/*     */           }
/*     */           
/* 164 */           this.current_sub = this.current_subs[(index - 1)];
/*     */           
/* 166 */           this.current_results = null;
/*     */           
/* 168 */           ci.out.println("Selected subscription '" + this.current_sub.getName() + "'");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 172 */           ci.out.println("Failed to select subscription: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 177 */     else if ((cmd.equals("update")) || (cmd.equals("results")) || (cmd.equals("set_autodownload")) || (cmd.equals("set_updatemins")) || (cmd.equals("set_read")) || (cmd.equals("set_unread")) || (cmd.equals("download")) || (cmd.equals("delete")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 186 */       if (this.current_sub == null)
/*     */       {
/* 188 */         ci.out.println("No current subscription - select one!");
/*     */ 
/*     */ 
/*     */       }
/* 192 */       else if (cmd.equals("update"))
/*     */       {
/*     */         try {
/* 195 */           sub_man.getScheduler().downloadAsync(this.current_sub, true);
/*     */           
/* 197 */           ci.out.println("Subscription scheduled for update");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 201 */           ci.out.println("Subscription update failed: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/* 204 */       else if (cmd.equals("results"))
/*     */       {
/* 206 */         boolean do_all = (args.size() > 1) && (((String)args.get(1)).equals("all"));
/*     */         
/* 208 */         int index = 1;
/*     */         
/* 210 */         int total_read = 0;
/* 211 */         int total_unread = 0;
/*     */         
/* 213 */         this.current_results = new ArrayList();
/*     */         
/* 215 */         SubscriptionResult[] results = this.current_sub.getHistory().getResults(false);
/*     */         
/* 217 */         for (SubscriptionResult result : results)
/*     */         {
/* 219 */           boolean is_read = result.getRead();
/*     */           
/* 221 */           if (is_read)
/*     */           {
/* 223 */             total_read++;
/*     */           }
/*     */           else
/*     */           {
/* 227 */             total_unread++;
/*     */           }
/*     */           
/* 230 */           if ((!is_read) || (do_all))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 235 */             this.current_results.add(result);
/*     */             
/* 237 */             Map map = result.toJSONMap();
/*     */             
/* 239 */             String index_str = "" + index++;
/*     */             
/* 241 */             while (index_str.length() < 3) {
/* 242 */               index_str = index_str + " ";
/*     */             }
/*     */             
/* 245 */             String str = index_str + map.get("n") + ", size=" + map.get("l") + ", seeds=" + map.get("s") + ", peers=" + map.get("p");
/*     */             
/* 247 */             if (do_all)
/*     */             {
/* 249 */               str = str + ", read=" + (is_read ? "yes" : "no");
/*     */             }
/*     */             
/* 252 */             ci.out.println(str);
/*     */           }
/*     */         }
/* 255 */         ci.out.println("> -----");
/* 256 */         ci.out.println("Total read=" + total_read + ", unread=" + total_unread);
/*     */       }
/* 258 */       else if (cmd.equals("set_autodownload"))
/*     */       {
/* 260 */         if (args.size() < 2)
/*     */         {
/* 262 */           ci.out.println("Usage: " + cmd + " [yes|no]");
/*     */         }
/* 264 */         else if (!this.current_sub.isAutoDownloadSupported())
/*     */         {
/* 266 */           ci.out.println("Auto-download not supported for this subscription");
/*     */         }
/*     */         else
/*     */         {
/* 270 */           String temp = (String)args.get(1);
/*     */           
/* 272 */           if (temp.equals("yes"))
/*     */           {
/* 274 */             this.current_sub.getHistory().setAutoDownload(true);
/*     */           }
/* 276 */           else if (temp.equals("no"))
/*     */           {
/* 278 */             this.current_sub.getHistory().setAutoDownload(false);
/*     */           }
/*     */           else
/*     */           {
/* 282 */             ci.out.println("Usage: " + cmd + " [yes|no]");
/*     */           }
/*     */         }
/*     */       }
/* 286 */       else if (cmd.equals("set_updatemins"))
/*     */       {
/* 288 */         if (args.size() < 2)
/*     */         {
/* 290 */           ci.out.println("Usage: " + cmd + " <minutes>");
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 296 */             int mins = Integer.parseInt((String)args.get(1));
/*     */             
/* 298 */             this.current_sub.getHistory().setCheckFrequencyMins(mins);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 302 */             ci.out.println("Usage: " + cmd + " <minutes>");
/*     */           }
/*     */         }
/* 305 */       } else if ((cmd.equals("set_read")) || (cmd.equals("set_unread")) || (cmd.equals("download")))
/*     */       {
/*     */ 
/*     */ 
/* 309 */         if (args.size() < 2)
/*     */         {
/* 311 */           ci.out.println("Usage: " + cmd + " <result_number>|all");
/*     */         }
/* 313 */         else if (this.current_results == null)
/*     */         {
/* 315 */           ci.out.println("results must be listed before operating on them");
/*     */         } else {
/*     */           try
/*     */           {
/* 319 */             String temp = (String)args.get(1);
/*     */             
/* 321 */             int do_index = -1;
/*     */             
/* 323 */             if (!temp.equals("all"))
/*     */             {
/* 325 */               do_index = Integer.parseInt(temp);
/*     */               
/* 327 */               if ((do_index < 1) || (do_index > this.current_results.size()))
/*     */               {
/* 329 */                 throw new Exception("Invalid result index");
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 334 */             List<SubscriptionResult> to_do = new ArrayList();
/*     */             
/* 336 */             if (do_index == -1)
/*     */             {
/* 338 */               to_do.addAll(this.current_results);
/*     */             }
/*     */             else
/*     */             {
/* 342 */               to_do.add(this.current_results.get(do_index - 1));
/*     */             }
/*     */             
/* 345 */             for (SubscriptionResult result : to_do)
/*     */             {
/* 347 */               if (cmd.equals("set_read"))
/*     */               {
/* 349 */                 result.setRead(true);
/*     */               }
/* 351 */               else if (cmd.equals("set_unread"))
/*     */               {
/* 353 */                 result.setRead(false);
/*     */               }
/* 355 */               else if (cmd.equals("download"))
/*     */               {
/* 357 */                 String download_link = result.getDownloadLink();
/*     */                 try
/*     */                 {
/* 360 */                   URL url = new URL(result.getDownloadLink());
/*     */                   
/* 362 */                   ci.downloadRemoteTorrent(url.toExternalForm());
/*     */                   
/* 364 */                   ci.out.println("Queueing '" + download_link + "' for download");
/*     */                   
/* 366 */                   result.setRead(true);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 370 */                   ci.out.println("Failed to add download from URL '" + download_link + "': " + Debug.getNestedExceptionMessage(e));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 377 */             ci.out.println("Operation failed: " + Debug.getNestedExceptionMessage(e));
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 382 */       else if (cmd.equals("delete"))
/*     */       {
/* 384 */         ci.out.println("Subscription '" + this.current_sub.getName() + "' deleted");
/*     */         
/* 386 */         this.current_sub.remove();
/*     */         
/* 388 */         this.current_sub = null;
/* 389 */         this.current_results = null;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 394 */       ci.out.println("Unsupported sub-command: " + cmd);
/*     */     }
/*     */     
/* 397 */     ci.out.println("> -----");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Subscriptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */