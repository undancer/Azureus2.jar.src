/*      */ package com.aelitis.azureus.plugins.net.buddy;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.security.SecureRandom;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*      */ public class BuddyPluginAZ2
/*      */ {
/*      */   public static final int RT_AZ2_REQUEST_MESSAGE = 1;
/*      */   public static final int RT_AZ2_REPLY_MESSAGE = 2;
/*      */   public static final int RT_AZ2_REQUEST_SEND_TORRENT = 3;
/*      */   public static final int RT_AZ2_REPLY_SEND_TORRENT = 4;
/*      */   public static final int RT_AZ2_REQUEST_CHAT = 5;
/*      */   public static final int RT_AZ2_REPLY_CHAT = 6;
/*      */   public static final int RT_AZ2_REQUEST_TRACK = 7;
/*      */   public static final int RT_AZ2_REPLY_TRACK = 8;
/*      */   public static final int RT_AZ2_REQUEST_RSS = 9;
/*      */   public static final int RT_AZ2_REPLY_RSS = 10;
/*      */   public static final int CHAT_MSG_TYPE_TEXT = 1;
/*      */   public static final int CHAT_MSG_TYPE_PARTICIPANTS_ADDED = 2;
/*      */   public static final int CHAT_MSG_TYPE_PARTICIPANTS_REMOVED = 3;
/*      */   private static final int SEND_TIMEOUT = 120000;
/*      */   private BuddyPlugin plugin;
/*   62 */   private Map chats = new HashMap();
/*      */   
/*   64 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*   66 */   private CopyOnWriteList track_listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */   protected BuddyPluginAZ2(BuddyPlugin _plugin)
/*      */   {
/*   72 */     this.plugin = _plugin;
/*      */     
/*   74 */     this.plugin.addRequestListener(new BuddyPluginBuddyRequestListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public Map requestReceived(BuddyPluginBuddy from_buddy, int subsystem, Map request)
/*      */         throws BuddyPluginException
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   85 */         if (subsystem == 1)
/*      */         {
/*   87 */           if (!from_buddy.isAuthorised())
/*      */           {
/*   89 */             throw new BuddyPluginException("Unauthorised");
/*      */           }
/*      */           
/*   92 */           return BuddyPluginAZ2.this.processAZ2Request(from_buddy, request);
/*      */         }
/*      */         
/*   95 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void pendingMessages(BuddyPluginBuddy[] from_buddies) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map processAZ2Request(final BuddyPluginBuddy from_buddy, Map request)
/*      */     throws BuddyPluginException
/*      */   {
/*  113 */     logMessage("AZ2 request received: " + from_buddy.getString() + " -> " + request);
/*      */     
/*  115 */     int type = ((Long)request.get("type")).intValue();
/*      */     
/*  117 */     Map reply = new HashMap();
/*      */     
/*  119 */     if (type == 1)
/*      */     {
/*      */       try {
/*  122 */         String msg = new String((byte[])request.get("msg"), "UTF8");
/*      */         
/*  124 */         from_buddy.setLastMessageReceived(msg);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*  130 */       reply.put("type", new Long(2L));
/*      */     }
/*  132 */     else if (type == 3)
/*      */     {
/*      */       try {
/*  135 */         final Torrent torrent = this.plugin.getPluginInterface().getTorrentManager().createFromBEncodedData((byte[])request.get("torrent"));
/*      */         
/*  137 */         new AEThread2("torrentAdder", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*  142 */             PluginInterface pi = BuddyPluginAZ2.this.plugin.getPluginInterface();
/*      */             
/*  144 */             String msg = pi.getUtilities().getLocaleUtilities().getLocalisedMessageText("azbuddy.addtorrent.msg", new String[] { from_buddy.getName(), torrent.getName() });
/*      */             
/*      */ 
/*      */ 
/*  148 */             long res = pi.getUIManager().showMessageBox("azbuddy.addtorrent.title", "!" + msg + "!", 12L);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  153 */             if (res == 4L)
/*      */             {
/*  155 */               pi.getUIManager().openTorrent(torrent);
/*      */             }
/*      */             
/*      */           }
/*  159 */         }.start();
/*  160 */         reply.put("type", new Long(4L));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  164 */         throw new BuddyPluginException("Torrent receive failed " + type);
/*      */       }
/*  166 */     } else if (type == 5)
/*      */     {
/*  168 */       Map msg = (Map)request.get("msg");
/*      */       
/*  170 */       String id = new String((byte[])msg.get("id"));
/*      */       
/*      */ 
/*  173 */       boolean new_chat = false;
/*      */       chatInstance chat;
/*  175 */       synchronized (this.chats)
/*      */       {
/*  177 */         chat = (chatInstance)this.chats.get(id);
/*      */         
/*  179 */         if (chat == null)
/*      */         {
/*  181 */           if (this.chats.size() > 32)
/*      */           {
/*  183 */             throw new BuddyPluginException("Too many chats");
/*      */           }
/*      */           
/*  186 */           chat = new chatInstance(id);
/*      */           
/*  188 */           this.chats.put(id, chat);
/*      */           
/*  190 */           new_chat = true;
/*      */         }
/*      */       }
/*      */       
/*  194 */       if (new_chat)
/*      */       {
/*  196 */         informCreated(chat);
/*      */       }
/*      */       
/*  199 */       chat.addParticipant(from_buddy);
/*      */       
/*  201 */       chat.process(from_buddy, msg);
/*      */       
/*  203 */       reply.put("type", new Long(6L));
/*      */     }
/*  205 */     else if (type == 7)
/*      */     {
/*  207 */       Map msg = (Map)request.get("msg");
/*      */       
/*  209 */       Iterator it = this.track_listeners.iterator();
/*      */       
/*  211 */       boolean ok = false;
/*      */       
/*  213 */       while (it.hasNext())
/*      */       {
/*      */         try
/*      */         {
/*  217 */           Map res = ((BuddyPluginAZ2TrackerListener)it.next()).messageReceived(from_buddy, msg);
/*      */           
/*  219 */           if (res != null)
/*      */           {
/*  221 */             reply.put("msg", res);
/*  222 */             reply.put("type", new Long(8L));
/*      */             
/*  224 */             ok = true;
/*      */             
/*  226 */             break;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  230 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  234 */       if (!ok)
/*      */       {
/*  236 */         throw new BuddyPluginException("Unhandled request type " + type);
/*      */       }
/*  238 */     } else if (type == 9)
/*      */     {
/*      */       try {
/*  241 */         Map<String, Object> res = new HashMap();
/*      */         
/*  243 */         reply.put("msg", res);
/*  244 */         reply.put("type", new Long(10L));
/*      */         
/*  246 */         Map msg = (Map)request.get("msg");
/*      */         
/*  248 */         String category = new String((byte[])msg.get("cat"), "UTF-8");
/*      */         
/*  250 */         byte[] hash = (byte[])msg.get("hash");
/*      */         
/*  252 */         if (hash == null)
/*      */         {
/*  254 */           byte[] if_mod = (byte[])msg.get("if_mod");
/*      */           
/*  256 */           BuddyPlugin.feedDetails feed = this.plugin.getRSS(from_buddy, category, if_mod == null ? null : new String(if_mod, "UTF-8"));
/*      */           
/*  258 */           res.put("rss", feed.getContent());
/*      */           
/*  260 */           res.put("last_mod", feed.getLastModified());
/*      */         }
/*      */         else
/*      */         {
/*  264 */           res.put("torrent", this.plugin.getRSSTorrent(from_buddy, category, hash));
/*      */         }
/*      */       }
/*      */       catch (BuddyPluginException e) {
/*  268 */         throw e;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  272 */         throw new BuddyPluginException("Failed to handle rss", e);
/*      */       }
/*      */     }
/*      */     else {
/*  276 */       throw new BuddyPluginException("Unrecognised request type " + type);
/*      */     }
/*      */     
/*  279 */     logMessage("AZ2 reply sent: " + from_buddy.getString() + " <- " + reply);
/*      */     
/*  281 */     return reply;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public chatInstance createChat(BuddyPluginBuddy[] buddies)
/*      */   {
/*  288 */     byte[] id_bytes = new byte[20];
/*      */     
/*  290 */     RandomUtils.SECURE_RANDOM.nextBytes(id_bytes);
/*      */     
/*  292 */     String id = Base32.encode(id_bytes);
/*      */     
/*      */     chatInstance chat;
/*      */     
/*  296 */     synchronized (this.chats)
/*      */     {
/*  298 */       chat = new chatInstance(id);
/*      */       
/*  300 */       this.chats.put(id, chat);
/*      */     }
/*      */     
/*  303 */     logMessage("Chat " + chat.getID() + " created");
/*      */     
/*  305 */     informCreated(chat);
/*      */     
/*  307 */     chat.addParticipants(buddies, true);
/*      */     
/*  309 */     return chat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void destroyChat(chatInstance chat)
/*      */   {
/*  316 */     synchronized (this.chats)
/*      */     {
/*  318 */       this.chats.remove(chat.getID());
/*      */     }
/*      */     
/*  321 */     logMessage("Chat " + chat.getID() + " destroyed");
/*      */     
/*  323 */     informDestroyed(chat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informCreated(chatInstance chat)
/*      */   {
/*  330 */     Iterator it = this.listeners.iterator();
/*      */     
/*  332 */     while (it.hasNext())
/*      */     {
/*  334 */       ((BuddyPluginAZ2Listener)it.next()).chatCreated(chat);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informDestroyed(chatInstance chat)
/*      */   {
/*  342 */     Iterator it = this.listeners.iterator();
/*      */     
/*  344 */     while (it.hasNext())
/*      */     {
/*  346 */       ((BuddyPluginAZ2Listener)it.next()).chatDestroyed(chat);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendAZ2Message(BuddyPluginBuddy buddy, String msg)
/*      */   {
/*      */     try
/*      */     {
/*  356 */       Map request = new HashMap();
/*      */       
/*  358 */       request.put("type", new Long(1L));
/*  359 */       request.put("msg", msg.getBytes());
/*      */       
/*  361 */       sendMessage(buddy, request);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  365 */       logMessageAndPopup("Send message failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sendAZ2Chat(BuddyPluginBuddy buddy, Map msg)
/*      */   {
/*      */     try
/*      */     {
/*  375 */       Map request = new HashMap();
/*      */       
/*  377 */       request.put("type", new Long(5L));
/*  378 */       request.put("msg", msg);
/*      */       
/*  380 */       sendMessage(buddy, request);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  384 */       logMessageAndPopup("Send message failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendAZ2Torrent(Torrent torrent, BuddyPluginBuddy buddy)
/*      */   {
/*      */     try
/*      */     {
/*  395 */       Map request = new HashMap();
/*      */       
/*  397 */       request.put("type", new Long(3L));
/*  398 */       request.put("torrent", torrent.writeToBEncodedData());
/*      */       
/*  400 */       sendMessage(buddy, request);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  404 */       logMessageAndPopup("Send torrent failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendAZ2TrackerMessage(BuddyPluginBuddy buddy, Map msg, final BuddyPluginAZ2TrackerListener listener)
/*      */   {
/*  414 */     logMessage("AZ2 request sent: " + buddy.getString() + " <- " + msg);
/*      */     try
/*      */     {
/*  417 */       Map request = new HashMap();
/*      */       
/*  419 */       request.put("type", new Long(7L));
/*  420 */       request.put("msg", msg);
/*      */       
/*  422 */       buddy.sendMessage(1, request, 120000, new BuddyPluginBuddyReplyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  433 */           int type = ((Long)reply.get("type")).intValue();
/*      */           
/*  435 */           if (type != 8)
/*      */           {
/*  437 */             sendFailed(from_buddy, new BuddyPluginException("Mismatched reply type"));
/*      */           }
/*      */           
/*  440 */           listener.messageReceived(from_buddy, (Map)reply.get("msg"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */         {
/*  448 */           listener.messageFailed(to_buddy, cause);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  454 */       logMessageAndPopup("Send message failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendAZ2RSSMessage(BuddyPluginBuddy buddy, Map msg, final BuddyPluginAZ2TrackerListener listener)
/*      */   {
/*  464 */     logMessage("AZ2 request sent: " + buddy.getString() + " <- " + msg);
/*      */     try
/*      */     {
/*  467 */       Map request = new HashMap();
/*      */       
/*  469 */       request.put("type", new Long(9L));
/*  470 */       request.put("msg", msg);
/*      */       
/*  472 */       buddy.sendMessage(1, request, 120000, new BuddyPluginBuddyReplyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void replyReceived(BuddyPluginBuddy from_buddy, Map reply)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  483 */           int type = ((Long)reply.get("type")).intValue();
/*      */           
/*  485 */           if (type != 10)
/*      */           {
/*  487 */             sendFailed(from_buddy, new BuddyPluginException("Mismatched reply type"));
/*      */           }
/*      */           
/*  490 */           listener.messageReceived(from_buddy, (Map)reply.get("msg"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void sendFailed(BuddyPluginBuddy to_buddy, BuddyPluginException cause)
/*      */         {
/*  498 */           listener.messageFailed(to_buddy, cause);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  504 */       logMessage("Send message failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(BuddyPluginBuddy buddy, Map request)
/*      */     throws BuddyPluginException
/*      */   {
/*  515 */     logMessage("AZ2 request sent: " + buddy.getString() + " <- " + request);
/*      */     
/*  517 */     buddy.getMessageHandler().queueMessage(1, request, 120000);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(BuddyPluginAZ2Listener listener)
/*      */   {
/*  527 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(BuddyPluginAZ2Listener listener)
/*      */   {
/*  534 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(BuddyPluginAZ2TrackerListener listener)
/*      */   {
/*  541 */     this.track_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTrackerListener(BuddyPluginAZ2TrackerListener listener)
/*      */   {
/*  548 */     this.track_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logMessageAndPopup(String str, Throwable e)
/*      */   {
/*  556 */     logMessageAndPopup(str + ": " + Debug.getNestedExceptionMessage(e));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void logMessageAndPopup(String str)
/*      */   {
/*  563 */     logMessage(str);
/*      */     
/*  565 */     this.plugin.getPluginInterface().getUIManager().showMessageBox("azbuddy.msglog.title", "!" + str + "!", 1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logMessage(String str)
/*      */   {
/*  573 */     this.plugin.logMessage(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logMessage(String str, Throwable e)
/*      */   {
/*  581 */     this.plugin.logMessage(str + ": " + Debug.getNestedExceptionMessage(e));
/*      */   }
/*      */   
/*      */ 
/*      */   public class chatInstance
/*      */     extends BuddyPluginAdapter
/*      */   {
/*      */     private String id;
/*      */     
/*  590 */     private Map participants = new HashMap();
/*  591 */     private CopyOnWriteList listeners = new CopyOnWriteList();
/*      */     
/*  593 */     private List history = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */     protected chatInstance(String _id)
/*      */     {
/*  599 */       this.id = _id;
/*      */       
/*  601 */       BuddyPluginAZ2.this.plugin.addListener(this);
/*      */     }
/*      */     
/*      */ 
/*      */     public String getID()
/*      */     {
/*  607 */       return this.id;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void buddyAdded(BuddyPluginBuddy buddy)
/*      */     {
/*  614 */       buddyChanged(buddy);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void buddyRemoved(BuddyPluginBuddy buddy)
/*      */     {
/*  621 */       BuddyPluginAZ2.chatParticipant p = getParticipant(buddy);
/*      */       
/*  623 */       if (p != null)
/*      */       {
/*  625 */         Iterator it = this.listeners.iterator();
/*      */         
/*  627 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  630 */             ((BuddyPluginAZ2ChatListener)it.next()).participantRemoved(p);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  634 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void buddyChanged(BuddyPluginBuddy buddy)
/*      */     {
/*  644 */       BuddyPluginAZ2.chatParticipant p = getParticipant(buddy);
/*      */       
/*  646 */       if (p != null)
/*      */       {
/*  648 */         Iterator it = this.listeners.iterator();
/*      */         
/*  650 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  653 */             ((BuddyPluginAZ2ChatListener)it.next()).participantChanged(p);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  657 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void process(BuddyPluginBuddy from_buddy, Map msg)
/*      */     {
/*  668 */       BuddyPluginAZ2.chatParticipant p = getOrAddParticipant(from_buddy);
/*      */       
/*  670 */       int type = ((Long)msg.get("type")).intValue();
/*      */       
/*  672 */       if (type == 1)
/*      */       {
/*  674 */         Iterator it = this.listeners.iterator();
/*      */         
/*  676 */         synchronized (this.history)
/*      */         {
/*  678 */           this.history.add(new BuddyPluginAZ2.chatMessage(p.getName(), msg));
/*      */           
/*  680 */           if (this.history.size() > 128)
/*      */           {
/*  682 */             this.history.remove(0);
/*      */           }
/*      */         }
/*      */         
/*  686 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  689 */             ((BuddyPluginAZ2ChatListener)it.next()).messageReceived(p, msg);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  693 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*  696 */       } else if (type == 2)
/*      */       {
/*  698 */         List added = (List)msg.get("p");
/*      */         
/*  700 */         for (int i = 0; i < added.size(); i++)
/*      */         {
/*  702 */           Map participant = (Map)added.get(i);
/*      */           
/*  704 */           String pk = new String((byte[])participant.get("pk"));
/*      */           
/*  706 */           if (!pk.equals(BuddyPluginAZ2.this.plugin.getPublicKey()))
/*      */           {
/*  708 */             addParticipant(pk);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void sendMessage(Map msg)
/*      */     {
/*  718 */       msg.put("type", new Long(1L));
/*      */       
/*  720 */       sendMessageBase(msg);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void sendMessageBase(Map msg)
/*      */     {
/*      */       Map ps;
/*      */       
/*  729 */       synchronized (this.participants)
/*      */       {
/*  731 */         ps = new HashMap(this.participants);
/*      */       }
/*      */       
/*  734 */       msg.put("id", this.id);
/*      */       
/*  736 */       Iterator it = ps.values().iterator();
/*      */       
/*  738 */       while (it.hasNext())
/*      */       {
/*  740 */         BuddyPluginAZ2.chatParticipant participant = (BuddyPluginAZ2.chatParticipant)it.next();
/*      */         
/*  742 */         if (participant.isAuthorised())
/*      */         {
/*  744 */           BuddyPluginAZ2.this.sendAZ2Chat(participant.getBuddy(), msg);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginAZ2.chatMessage[] getHistory()
/*      */     {
/*  752 */       synchronized (this.history)
/*      */       {
/*  754 */         BuddyPluginAZ2.chatMessage[] res = new BuddyPluginAZ2.chatMessage[this.history.size()];
/*      */         
/*  756 */         this.history.toArray(res);
/*      */         
/*  758 */         return res;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected BuddyPluginAZ2.chatParticipant getOrAddParticipant(BuddyPluginBuddy buddy)
/*      */     {
/*  766 */       return addParticipant(buddy);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public BuddyPluginAZ2.chatParticipant addParticipant(String pk)
/*      */     {
/*  775 */       BuddyPluginBuddy buddy = BuddyPluginAZ2.this.plugin.getBuddyFromPublicKey(pk);
/*      */       BuddyPluginAZ2.chatParticipant p;
/*  777 */       synchronized (this.participants)
/*      */       {
/*  779 */         p = (BuddyPluginAZ2.chatParticipant)this.participants.get(pk);
/*      */         
/*  781 */         if (p != null)
/*      */         {
/*  783 */           return p;
/*      */         }
/*      */         
/*  786 */         if (buddy == null)
/*      */         {
/*  788 */           p = new BuddyPluginAZ2.chatParticipant(pk);
/*      */         }
/*      */         else
/*      */         {
/*  792 */           p = new BuddyPluginAZ2.chatParticipant(buddy);
/*      */         }
/*      */         
/*  795 */         this.participants.put(pk, p);
/*      */       }
/*      */       
/*  798 */       Iterator it = this.listeners.iterator();
/*      */       
/*  800 */       while (it.hasNext()) {
/*      */         try
/*      */         {
/*  803 */           ((BuddyPluginAZ2ChatListener)it.next()).participantAdded(p);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  807 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  811 */       return p;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public BuddyPluginAZ2.chatParticipant addParticipant(BuddyPluginBuddy buddy)
/*      */     {
/*  818 */       return addParticipant(buddy.getPublicKey());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addParticipants(BuddyPluginBuddy[] buddies, boolean inform_others)
/*      */     {
/*  826 */       for (int i = 0; i < buddies.length; i++)
/*      */       {
/*  828 */         addParticipant(buddies[i]);
/*      */       }
/*      */       
/*  831 */       if (inform_others)
/*      */       {
/*  833 */         Map msg = new HashMap();
/*      */         
/*  835 */         msg.put("type", new Long(2L));
/*      */         
/*  837 */         List added = new ArrayList();
/*      */         
/*  839 */         msg.put("p", added);
/*      */         
/*  841 */         for (int i = 0; i < buddies.length; i++)
/*      */         {
/*  843 */           Map map = new HashMap();
/*      */           
/*  845 */           map.put("pk", buddies[i].getPublicKey());
/*      */           
/*  847 */           added.add(map);
/*      */         }
/*      */         
/*  850 */         sendMessageBase(msg);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected BuddyPluginAZ2.chatParticipant getParticipant(BuddyPluginBuddy b)
/*      */     {
/*  858 */       String pk = b.getPublicKey();
/*      */       
/*  860 */       synchronized (this.participants)
/*      */       {
/*  862 */         BuddyPluginAZ2.chatParticipant p = (BuddyPluginAZ2.chatParticipant)this.participants.get(pk);
/*      */         
/*  864 */         if (p != null)
/*      */         {
/*  866 */           return p;
/*      */         }
/*      */       }
/*      */       
/*  870 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginAZ2.chatParticipant[] getParticipants()
/*      */     {
/*  876 */       synchronized (this.participants)
/*      */       {
/*  878 */         BuddyPluginAZ2.chatParticipant[] res = new BuddyPluginAZ2.chatParticipant[this.participants.size()];
/*      */         
/*  880 */         this.participants.values().toArray(res);
/*      */         
/*  882 */         return res;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void removeParticipant(BuddyPluginAZ2.chatParticipant p)
/*      */     {
/*      */       boolean removed;
/*      */       
/*  892 */       synchronized (this.participants)
/*      */       {
/*  894 */         removed = this.participants.remove(p.getPublicKey()) != null;
/*      */       }
/*      */       
/*  897 */       if (removed)
/*      */       {
/*  899 */         Iterator it = this.listeners.iterator();
/*      */         
/*  901 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  904 */             ((BuddyPluginAZ2ChatListener)it.next()).participantRemoved(p);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  908 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void destroy()
/*      */     {
/*  917 */       BuddyPluginAZ2.this.plugin.removeListener(this);
/*      */       
/*  919 */       BuddyPluginAZ2.this.destroyChat(this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(BuddyPluginAZ2ChatListener listener)
/*      */     {
/*  926 */       this.listeners.add(listener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(BuddyPluginAZ2ChatListener listener)
/*      */     {
/*  933 */       this.listeners.remove(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static class chatParticipant
/*      */   {
/*      */     private BuddyPluginBuddy buddy;
/*      */     
/*      */     private String public_key;
/*      */     
/*      */ 
/*      */     protected chatParticipant(BuddyPluginBuddy _buddy)
/*      */     {
/*  947 */       this.buddy = _buddy;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected chatParticipant(String pk)
/*      */     {
/*  954 */       this.public_key = pk;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isAuthorised()
/*      */     {
/*  960 */       return this.buddy != null;
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginBuddy getBuddy()
/*      */     {
/*  966 */       return this.buddy;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getPublicKey()
/*      */     {
/*  972 */       if (this.buddy != null)
/*      */       {
/*  974 */         return this.buddy.getPublicKey();
/*      */       }
/*      */       
/*  977 */       return this.public_key;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/*  983 */       if (this.buddy != null)
/*      */       {
/*  985 */         return this.buddy.getName();
/*      */       }
/*      */       
/*  988 */       return this.public_key;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class chatMessage
/*      */   {
/*      */     private String nick;
/*      */     
/*      */     private Map map;
/*      */     
/*      */ 
/*      */     protected chatMessage(String _nick, Map _map)
/*      */     {
/* 1003 */       this.nick = _nick;
/* 1004 */       this.map = _map;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getNickName()
/*      */     {
/* 1010 */       return this.nick;
/*      */     }
/*      */     
/*      */ 
/*      */     public Map getMessage()
/*      */     {
/* 1016 */       return this.map;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginAZ2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */