package com.aelitis.azureus.ui;

public abstract interface UIFunctionsUserPrompter
{
  public abstract int getAutoCloseInMS();
  
  public abstract String getHtml();
  
  public abstract String getRememberID();
  
  public abstract String getRememberText();
  
  public abstract void open(UserPrompterResultListener paramUserPrompterResultListener);
  
  public abstract int waitUntilClosed();
  
  public abstract void setAutoCloseInMS(int paramInt);
  
  public abstract void setHtml(String paramString);
  
  public abstract void setRemember(String paramString1, boolean paramBoolean, String paramString2);
  
  public abstract void setRememberText(String paramString);
  
  public abstract void setRememberOnlyIfButton(int paramInt);
  
  public abstract void setUrl(String paramString);
  
  public abstract boolean isAutoClosed();
  
  public abstract void setIconResource(String paramString);
  
  public abstract void setRelatedObjects(Object[] paramArrayOfObject);
  
  public abstract void setRelatedObject(Object paramObject);
  
  public abstract void setOneInstanceOf(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/UIFunctionsUserPrompter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */