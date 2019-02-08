/*     */ package org.gudy.azureus2.pluginsimpl.local.ui;
/*     */ 
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputValidator;
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
/*     */ public abstract class AbstractUIInputReceiver
/*     */   implements UIInputReceiver
/*     */ {
/*  38 */   private boolean prompted = false;
/*     */   
/*     */   protected final void assertPrePrompt()
/*     */   {
/*  42 */     if (this.prompted) {
/*  43 */       throw new RuntimeException("cannot invoke after prompt has been called");
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void assertPostPrompt() {
/*  48 */     if (!this.prompted) {
/*  49 */       throw new RuntimeException("cannot before after prompt has been called");
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLocalisedMessage(String message) {
/*  54 */     setLocalisedMessages(new String[] { message });
/*     */   }
/*     */   
/*  57 */   protected String[] messages = new String[0];
/*     */   
/*     */   public void setLocalisedMessages(String[] messages) {
/*  60 */     assertPrePrompt();
/*  61 */     this.messages = messages;
/*     */   }
/*     */   
/*  64 */   protected String title = null;
/*     */   
/*     */   public void setLocalisedTitle(String title) {
/*  67 */     assertPrePrompt();
/*  68 */     this.title = title;
/*     */   }
/*     */   
/*     */   public void setMessage(String message) {
/*  72 */     setLocalisedMessage(localise(message));
/*     */   }
/*     */   
/*     */   public void setMessages(String[] messages) {
/*  76 */     String[] new_messages = new String[messages.length];
/*  77 */     for (int i = 0; i < new_messages.length; i++) {
/*  78 */       new_messages[i] = localise(messages[i]);
/*     */     }
/*  80 */     setLocalisedMessages(new_messages);
/*     */   }
/*     */   
/*  83 */   protected boolean multiline_mode = false;
/*     */   
/*     */   public void setMultiLine(boolean multiline) {
/*  86 */     assertPrePrompt();
/*  87 */     this.multiline_mode = multiline;
/*     */   }
/*     */   
/*  90 */   protected String preentered_text = null;
/*     */   
/*     */   public void setPreenteredText(String text, boolean as_suggested)
/*     */   {
/*  94 */     assertPrePrompt();
/*  95 */     this.preentered_text = text;
/*     */   }
/*     */   
/*     */   public void setTitle(String title)
/*     */   {
/* 100 */     setLocalisedTitle(localise(title));
/*     */   }
/*     */   
/* 103 */   protected UIInputValidator validator = null;
/*     */   
/*     */   public void setInputValidator(UIInputValidator validator) {
/* 106 */     assertPrePrompt();
/* 107 */     this.validator = validator;
/*     */   }
/*     */   
/* 110 */   private boolean result_recorded = false;
/*     */   protected UIInputReceiverListener receiver_listener;
/*     */   
/*     */   public final void prompt()
/*     */   {
/* 115 */     assertPrePrompt();
/* 116 */     promptForInput();
/* 117 */     if (!this.result_recorded) {
/* 118 */       throw new RuntimeException(toString() + " did not record a result.");
/*     */     }
/* 120 */     this.prompted = true;
/*     */   }
/*     */   
/*     */   protected boolean isResultRecorded() {
/* 124 */     return this.result_recorded;
/*     */   }
/*     */   
/*     */   public final void prompt(UIInputReceiverListener receiver_listener) {
/* 128 */     assertPrePrompt();
/* 129 */     this.receiver_listener = receiver_listener;
/* 130 */     promptForInput();
/*     */   }
/*     */   
/*     */   public final void triggerReceiverListener() {
/* 134 */     if (!this.result_recorded) {
/* 135 */       throw new RuntimeException(toString() + " did not record a result.");
/*     */     }
/* 137 */     this.prompted = true;
/* 138 */     if (this.receiver_listener != null) {
/* 139 */       this.receiver_listener.UIInputReceiverClosed(this);
/*     */     }
/*     */   }
/*     */   
/* 143 */   private boolean result_input_submitted = false;
/* 144 */   private String result_input = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void promptForInput();
/*     */   
/*     */ 
/*     */ 
/*     */   protected final void recordUserInput(String input)
/*     */   {
/* 155 */     this.result_recorded = true;
/* 156 */     this.result_input_submitted = true;
/* 157 */     this.result_input = input;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     if (!this.maintain_whitespace) {
/* 164 */       this.result_input = input.trim();
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void recordUserAbort() {
/* 169 */     this.result_recorded = true;
/* 170 */     this.result_input_submitted = false;
/* 171 */     this.result_input = null;
/*     */   }
/*     */   
/*     */   public boolean hasSubmittedInput() {
/* 175 */     assertPostPrompt();
/* 176 */     return this.result_input_submitted;
/*     */   }
/*     */   
/*     */   public String getSubmittedInput() {
/* 180 */     assertPostPrompt();
/* 181 */     return this.result_input;
/*     */   }
/*     */   
/* 184 */   protected boolean maintain_whitespace = false;
/*     */   
/*     */   public void maintainWhitespace(boolean keep_whitespace) {
/* 187 */     this.maintain_whitespace = keep_whitespace;
/*     */   }
/*     */   
/* 190 */   protected boolean allow_empty_input = true;
/*     */   
/*     */   public void allowEmptyInput(boolean empty_input) {
/* 193 */     this.allow_empty_input = empty_input;
/*     */   }
/*     */   
/*     */   protected final String localise(String key) {
/* 197 */     return MessageText.getString(key);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/AbstractUIInputReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */