package org.gudy.azureus2.plugins.ui;

public abstract interface UIMessage
{
  public static final int MSG_NONE = 0;
  public static final int MSG_ERROR = 1;
  public static final int MSG_INFO = 2;
  public static final int MSG_WARN = 3;
  public static final int MSG_QUESTION = 4;
  public static final int MSG_WORKING = 5;
  public static final int INPUT_OK = 0;
  public static final int INPUT_OK_CANCEL = 1;
  public static final int INPUT_YES_NO = 2;
  public static final int INPUT_YES_NO_CANCEL = 3;
  public static final int INPUT_RETRY_CANCEL = 4;
  public static final int INPUT_RETRY_CANCEL_IGNORE = 5;
  public static final int ANSWER_OK = 0;
  public static final int ANSWER_CANCEL = 1;
  public static final int ANSWER_YES = 2;
  public static final int ANSWER_NO = 3;
  public static final int ANSWER_RETRY = 4;
  public static final int ANSWER_IGNORE = 5;
  public static final int ANSWER_ABORT = 1;
  
  public abstract void setMessageType(int paramInt);
  
  public abstract void setInputType(int paramInt);
  
  public abstract void setTitle(String paramString);
  
  public abstract void setLocalisedTitle(String paramString);
  
  public abstract void setMessage(String paramString);
  
  public abstract void setLocalisedMessage(String paramString);
  
  public abstract void setMessages(String[] paramArrayOfString);
  
  public abstract void setLocalisedMessages(String[] paramArrayOfString);
  
  public abstract int ask();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */