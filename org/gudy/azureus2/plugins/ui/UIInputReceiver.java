package org.gudy.azureus2.plugins.ui;

public abstract interface UIInputReceiver
{
  public abstract void setTitle(String paramString);
  
  public abstract void setLocalisedTitle(String paramString);
  
  public abstract void setMessage(String paramString);
  
  public abstract void setLocalisedMessage(String paramString);
  
  public abstract void setMessages(String[] paramArrayOfString);
  
  public abstract void setLocalisedMessages(String[] paramArrayOfString);
  
  public abstract void setPreenteredText(String paramString, boolean paramBoolean);
  
  public abstract void setMultiLine(boolean paramBoolean);
  
  public abstract void maintainWhitespace(boolean paramBoolean);
  
  public abstract void allowEmptyInput(boolean paramBoolean);
  
  public abstract void setInputValidator(UIInputValidator paramUIInputValidator);
  
  @Deprecated
  public abstract void prompt();
  
  public abstract void prompt(UIInputReceiverListener paramUIInputReceiverListener);
  
  public abstract boolean hasSubmittedInput();
  
  public abstract String getSubmittedInput();
  
  public abstract void setTextLimit(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIInputReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */