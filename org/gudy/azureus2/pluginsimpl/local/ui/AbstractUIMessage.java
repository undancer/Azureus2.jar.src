/*    */ package org.gudy.azureus2.pluginsimpl.local.ui;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.plugins.ui.UIMessage;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractUIMessage
/*    */   implements UIMessage
/*    */ {
/* 29 */   protected int message_type = 0;
/* 30 */   protected int input_type = 0;
/* 31 */   protected String title = "";
/* 32 */   protected String[] messages = new String[0];
/*    */   
/* 34 */   public void setInputType(int input_type) { this.input_type = input_type; }
/* 35 */   public void setMessageType(int msg_type) { this.message_type = msg_type; }
/* 36 */   public void setLocalisedTitle(String title) { this.title = title; }
/* 37 */   public void setLocalisedMessage(String message) { setLocalisedMessages(new String[] { message }); }
/* 38 */   public void setLocalisedMessages(String[] messages) { this.messages = messages; }
/* 39 */   public void setMessage(String message) { setLocalisedMessage(localise(message)); }
/* 40 */   public void setTitle(String title) { setLocalisedTitle(localise(title)); }
/*    */   
/*    */   protected final String messagesAsString() {
/* 43 */     if (this.messages.length == 0) {
/* 44 */       return "";
/*    */     }
/* 46 */     StringBuilder sb = new StringBuilder(this.messages[0]);
/* 47 */     for (int i = 1; i < this.messages.length; i++) {
/* 48 */       sb.append("\n");
/* 49 */       sb.append(this.messages[i]);
/*    */     }
/* 51 */     return sb.toString();
/*    */   }
/*    */   
/*    */   public void setMessages(String[] messages) {
/* 55 */     String[] new_messages = new String[messages.length];
/* 56 */     for (int i = 0; i < new_messages.length; i++) {
/* 57 */       new_messages[i] = localise(messages[i]);
/*    */     }
/* 59 */     setLocalisedMessages(new_messages);
/*    */   }
/*    */   
/*    */   private final String localise(String key) {
/* 63 */     return MessageText.getString(key);
/*    */   }
/*    */   
/*    */   public int ask() {
/* 67 */     return 0;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/AbstractUIMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */