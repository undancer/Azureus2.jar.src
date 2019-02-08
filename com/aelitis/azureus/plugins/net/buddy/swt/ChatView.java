/*     */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface.View;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface.ViewListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ public class ChatView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private BuddyPluginBeta.ChatInstance current_chat;
/*     */   private BuddyPluginBeta.ChatInstance initialized_chat;
/*     */   private BuddyPluginViewInterface.View chat_view;
/*     */   
/*     */   private void initialize(Composite _parent_composite)
/*     */   {
/*     */     try
/*     */     {
/*  51 */       if (this.current_chat != null)
/*     */       {
/*  53 */         Map<String, Object> chat_properties = new HashMap();
/*     */         
/*  55 */         chat_properties.put("swt_comp", _parent_composite);
/*     */         
/*     */ 
/*  58 */         chat_properties.put("chat", this.current_chat.getClone());
/*     */         
/*  60 */         this.chat_view = BuddyPluginUtils.buildChatView(chat_properties, new BuddyPluginViewInterface.ViewListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void chatActivated(BuddyPluginBeta.ChatInstance chat) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  72 */         });
/*  73 */         ChatMDIEntry mdi_entry = (ChatMDIEntry)this.current_chat.getUserData(SBC_ChatOverview.MDI_KEY);
/*     */         
/*  75 */         if (mdi_entry != null)
/*     */         {
/*  77 */           mdi_entry.setView(this);
/*     */         }
/*     */       }
/*     */       else {
/*  81 */         Debug.out("No current chat");
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*  85 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void handleDrop(String drop)
/*     */   {
/*  93 */     this.chat_view.handleDrop(drop);
/*     */   }
/*     */   
/*     */ 
/*     */   private void viewActivated()
/*     */   {
/*  99 */     if (this.chat_view != null)
/*     */     {
/* 101 */       this.chat_view.activate();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void viewDeactivated() {}
/*     */   
/*     */ 
/*     */ 
/*     */   private void dataSourceChanged(Object data)
/*     */   {
/* 114 */     synchronized (this)
/*     */     {
/* 116 */       if ((data instanceof BuddyPluginBeta.ChatInstance))
/*     */       {
/* 118 */         BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)data;
/*     */         
/* 120 */         this.current_chat = chat;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 129 */     switch (event.getType())
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 7: 
/* 136 */       synchronized (this)
/*     */       {
/* 138 */         if (this.current_chat != null)
/*     */         {
/* 140 */           this.current_chat.destroy();
/*     */           
/* 142 */           this.current_chat = null;
/*     */         }
/*     */       }
/*     */       
/* 146 */       break;
/*     */     
/*     */ 
/*     */     case 2: 
/* 150 */       synchronized (this)
/*     */       {
/* 152 */         if (this.current_chat != null) {
/*     */           try
/*     */           {
/* 155 */             this.current_chat.getClone();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 159 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/* 163 */       initialize((Composite)event.getData());
/*     */       
/* 165 */       break;
/*     */     
/*     */     case 6: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 1: 
/* 172 */       dataSourceChanged(event.getData());
/* 173 */       break;
/*     */     
/*     */     case 3: 
/* 176 */       viewActivated();
/* 177 */       break;
/*     */     
/*     */     case 4: 
/* 180 */       viewDeactivated();
/* 181 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 187 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/ChatView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */