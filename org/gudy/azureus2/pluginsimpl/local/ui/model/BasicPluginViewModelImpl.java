/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.model;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.components.UIProgressBarImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.components.UITextAreaImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.components.UITextFieldImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicPluginViewModelImpl
/*     */   implements BasicPluginViewModel
/*     */ {
/*     */   private UIManagerImpl ui_manager;
/*     */   private String name;
/*     */   private UITextField status;
/*     */   private UITextField activity;
/*     */   private UITextArea log;
/*     */   private UIProgressBar progress;
/*     */   private String sConfigSectionID;
/*     */   
/*     */   public BasicPluginViewModelImpl(UIManagerImpl _ui_manager, String _name)
/*     */   {
/*  58 */     this.ui_manager = _ui_manager;
/*  59 */     this.name = _name;
/*     */     
/*  61 */     this.status = new UITextFieldImpl();
/*  62 */     this.activity = new UITextFieldImpl();
/*  63 */     this.log = new UITextAreaImpl();
/*  64 */     this.progress = new UIProgressBarImpl();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  70 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public UITextField getStatus()
/*     */   {
/*  76 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public UITextField getActivity()
/*     */   {
/*  82 */     return this.activity;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/*  88 */     return this.ui_manager.getPluginInterface();
/*     */   }
/*     */   
/*     */ 
/*     */   public UITextArea getLogArea()
/*     */   {
/*  94 */     return this.log;
/*     */   }
/*     */   
/*     */ 
/*     */   public UIProgressBar getProgress()
/*     */   {
/* 100 */     return this.progress;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setConfigSectionID(String id)
/*     */   {
/* 106 */     this.sConfigSectionID = id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getConfigSectionID()
/*     */   {
/* 112 */     return this.sConfigSectionID;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 118 */     this.ui_manager.destroy(this);
/*     */   }
/*     */   
/*     */   public void attachLoggerChannel(LoggerChannel channel) {
/* 122 */     channel.addListener(new LoggerChannelListener() {
/*     */       public void messageLogged(String message, Throwable t) {
/* 124 */         messageLogged(3, message, t);
/*     */       }
/*     */       
/* 127 */       public void messageLogged(int logtype, String message) { messageLogged(logtype, message, null); }
/*     */       
/*     */       public void messageLogged(int logtype, String message, Throwable t) {
/* 130 */         String log_type_s = null;
/* 131 */         switch (logtype) {
/*     */         case 2: 
/* 133 */           log_type_s = "warning";
/* 134 */           break;
/*     */         case 3: 
/* 136 */           log_type_s = "error";
/*     */         }
/*     */         
/* 139 */         if (log_type_s != null) {
/* 140 */           String prefix = MessageText.getString("AlertMessageBox." + log_type_s);
/* 141 */           BasicPluginViewModelImpl.this.log.appendText("[" + prefix.toUpperCase() + "] ");
/*     */         }
/* 143 */         BasicPluginViewModelImpl.this.log.appendText(message + "\n");
/* 144 */         if (t != null) {
/* 145 */           StringWriter sw = new StringWriter();
/* 146 */           PrintWriter pw = new PrintWriter(sw);
/* 147 */           t.printStackTrace(pw);
/* 148 */           BasicPluginViewModelImpl.this.log.appendText(sw.toString() + "\n");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/model/BasicPluginViewModelImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */