/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZHave;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHave;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class OutgoingBTHaveMessageAggregator
/*     */ {
/*  41 */   private final ArrayList pending_haves = new ArrayList();
/*  42 */   private final AEMonitor pending_haves_mon = new AEMonitor("OutgoingBTHaveMessageAggregator:PH");
/*     */   
/*     */   private byte bt_have_version;
/*     */   
/*     */   private byte az_have_version;
/*  47 */   private boolean destroyed = false;
/*     */   
/*     */   private final OutgoingMessageQueue outgoing_message_q;
/*     */   
/*  51 */   private final OutgoingMessageQueue.MessageQueueListener added_message_listener = new OutgoingMessageQueue.MessageQueueListener() {
/*  52 */     public boolean messageAdded(Message message) { return true; }
/*     */     
/*     */ 
/*     */     public void messageQueued(Message message)
/*     */     {
/*  57 */       String message_id = message.getID();
/*     */       
/*  59 */       if ((!message_id.equals("BT_HAVE")) && (!message_id.equals("AZ_HAVE"))) {
/*  60 */         OutgoingBTHaveMessageAggregator.this.sendPendingHaves();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void messageRemoved(Message message) {}
/*     */     
/*     */ 
/*     */     public void messageSent(Message message) {}
/*     */     
/*     */ 
/*     */     public void protocolBytesSent(int byte_count) {}
/*     */     
/*     */ 
/*     */     public void dataBytesSent(int byte_count) {}
/*     */     
/*     */ 
/*     */     public void flush() {}
/*     */   };
/*     */   
/*     */ 
/*     */   public OutgoingBTHaveMessageAggregator(OutgoingMessageQueue outgoing_message_q, byte _bt_have_version, byte _az_have_version)
/*     */   {
/*  83 */     this.outgoing_message_q = outgoing_message_q;
/*  84 */     this.bt_have_version = _bt_have_version;
/*  85 */     this.az_have_version = _az_have_version;
/*     */     
/*  87 */     outgoing_message_q.registerQueueListener(this.added_message_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHaveVersion(byte bt_version, byte az_version)
/*     */   {
/*  95 */     this.bt_have_version = bt_version;
/*  96 */     this.az_have_version = az_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void queueHaveMessage(int piece_number, boolean force)
/*     */   {
/* 104 */     if (this.destroyed) return;
/*     */     try
/*     */     {
/* 107 */       this.pending_haves_mon.enter();
/*     */       
/* 109 */       this.pending_haves.add(new Integer(piece_number));
/* 110 */       if (force) {
/* 111 */         sendPendingHaves();
/*     */       }
/*     */       else {
/* 114 */         int pending_bytes = this.pending_haves.size() * 9;
/* 115 */         if (pending_bytes >= this.outgoing_message_q.getMssSize())
/*     */         {
/*     */ 
/* 118 */           sendPendingHaves();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 123 */       this.pending_haves_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 133 */       this.pending_haves_mon.enter();
/*     */       
/* 135 */       this.pending_haves.clear();
/* 136 */       this.destroyed = true;
/*     */     }
/*     */     finally {
/* 139 */       this.pending_haves_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void forceSendOfPending()
/*     */   {
/* 148 */     sendPendingHaves();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasPending()
/*     */   {
/* 157 */     return !this.pending_haves.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   private void sendPendingHaves()
/*     */   {
/* 163 */     if (this.destroyed)
/*     */     {
/* 165 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 169 */       this.pending_haves_mon.enter();
/*     */       
/* 171 */       int num_haves = this.pending_haves.size();
/*     */       
/* 173 */       if (num_haves == 0) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 180 */       if ((num_haves == 1) || (this.az_have_version < 2))
/*     */       {
/* 182 */         for (int i = 0; i < num_haves; i++)
/*     */         {
/* 184 */           Integer piece_num = (Integer)this.pending_haves.get(i);
/*     */           
/* 186 */           this.outgoing_message_q.addMessage(new BTHave(piece_num.intValue(), this.bt_have_version), true);
/*     */         }
/*     */       }
/*     */       else {
/* 190 */         int[] piece_numbers = new int[num_haves];
/*     */         
/* 192 */         for (int i = 0; i < num_haves; i++)
/*     */         {
/* 194 */           piece_numbers[i] = ((Integer)this.pending_haves.get(i)).intValue();
/*     */         }
/*     */         
/* 197 */         this.outgoing_message_q.addMessage(new AZHave(piece_numbers, this.az_have_version), true);
/*     */       }
/*     */       
/*     */ 
/* 201 */       this.outgoing_message_q.doListenerNotifications();
/*     */       
/* 203 */       this.pending_haves.clear();
/*     */     }
/*     */     finally
/*     */     {
/* 207 */       this.pending_haves_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/OutgoingBTHaveMessageAggregator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */