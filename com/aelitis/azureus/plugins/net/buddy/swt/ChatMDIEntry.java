/*     */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatAdapter;
/*     */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
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
/*     */ public class ChatMDIEntry
/*     */   implements ViewTitleInfo
/*     */ {
/*     */   private final MdiEntry mdi_entry;
/*     */   private final BuddyPluginBeta.ChatInstance chat;
/*     */   private ChatView view;
/*     */   private String drop_outstanding;
/*  42 */   private final BuddyPluginBeta.ChatAdapter adapter = new BuddyPluginBeta.ChatAdapter()
/*     */   {
/*     */ 
/*     */ 
/*     */     public void updated()
/*     */     {
/*     */ 
/*  49 */       ChatMDIEntry.this.update();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ChatMDIEntry(BuddyPluginBeta.ChatInstance _chat, MdiEntry _entry)
/*     */   {
/*  58 */     this.chat = _chat;
/*     */     
/*  60 */     this.mdi_entry = _entry;
/*     */     
/*  62 */     setupMdiEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   private void setupMdiEntry()
/*     */   {
/*  68 */     this.mdi_entry.setViewTitleInfo(this);
/*     */     
/*  70 */     MdiEntryDropListener drop_listener = new MdiEntryDropListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean mdiEntryDrop(MdiEntry entry, Object payload)
/*     */       {
/*     */ 
/*     */ 
/*  78 */         if ((payload instanceof String[]))
/*     */         {
/*  80 */           String[] derp = (String[])payload;
/*     */           
/*  82 */           if (derp.length > 0)
/*     */           {
/*  84 */             payload = derp[0];
/*     */           }
/*     */         }
/*     */         
/*  88 */         if (!(payload instanceof String))
/*     */         {
/*  90 */           return false;
/*     */         }
/*     */         
/*  93 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */         
/*  95 */         if (mdi != null)
/*     */         {
/*  97 */           String drop = (String)payload;
/*     */           
/*  99 */           if (ChatMDIEntry.this.view == null)
/*     */           {
/* 101 */             ChatMDIEntry.this.drop_outstanding = drop;
/*     */           }
/*     */           else
/*     */           {
/* 105 */             ChatMDIEntry.this.view.handleDrop(drop);
/*     */           }
/*     */           
/* 108 */           mdi.showEntry(ChatMDIEntry.this.mdi_entry);
/*     */           
/* 110 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 114 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 118 */     };
/* 119 */     this.mdi_entry.addListener(drop_listener);
/*     */     
/* 121 */     this.mdi_entry.addListener(new MdiCloseListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mdiEntryClosed(MdiEntry entry, boolean user)
/*     */       {
/*     */ 
/*     */ 
/* 129 */         ChatMDIEntry.this.chat.destroy();
/*     */       }
/*     */       
/* 132 */     });
/* 133 */     this.chat.addListener(this.adapter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setView(ChatView _view)
/*     */   {
/* 140 */     this.view = _view;
/*     */     
/* 142 */     String drop = this.drop_outstanding;
/*     */     
/* 144 */     if (drop != null)
/*     */     {
/* 146 */       this.drop_outstanding = null;
/*     */       
/* 148 */       this.view.handleDrop(drop);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void update()
/*     */   {
/* 155 */     this.mdi_entry.redraw();
/*     */     
/* 157 */     ViewTitleInfoManager.refreshTitleInfo(this.mdi_entry.getViewTitleInfo());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getTitleInfoProperty(int propertyID)
/*     */   {
/* 164 */     switch (propertyID)
/*     */     {
/*     */ 
/*     */     case 1: 
/* 168 */       return this.chat.getName();
/*     */     
/*     */ 
/*     */     case 5: 
/* 172 */       return this.chat.getName(true);
/*     */     
/*     */ 
/*     */     case 8: 
/* 176 */       if (this.chat.getMessageOutstanding())
/*     */       {
/* 178 */         if (this.chat.hasUnseenMessageWithNick())
/*     */         {
/* 180 */           return SBC_ChatOverview.COLOR_MESSAGE_WITH_NICK;
/*     */         }
/*     */       }
/*     */       
/* 184 */       return null;
/*     */     
/*     */ 
/*     */     case 0: 
/* 188 */       if (this.chat.getMessageOutstanding())
/*     */       {
/* 190 */         return "*";
/*     */       }
/*     */       
/*     */ 
/* 194 */       return null;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 200 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/ChatMDIEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */