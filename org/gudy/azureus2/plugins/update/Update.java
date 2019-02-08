package org.gudy.azureus2.plugins.update;

import java.io.InputStream;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;

public abstract interface Update
{
  public static final int RESTART_REQUIRED_NO = 1;
  public static final int RESTART_REQUIRED_YES = 2;
  public static final int RESTART_REQUIRED_MAYBE = 3;
  
  public abstract String getName();
  
  public abstract String[] getDescription();
  
  public abstract String getRelativeURLBase();
  
  public abstract void setRelativeURLBase(String paramString);
  
  public abstract void setDescriptionURL(String paramString);
  
  public abstract String getDesciptionURL();
  
  public abstract String getOldVersion();
  
  public abstract String getNewVersion();
  
  public abstract ResourceDownloader[] getDownloaders();
  
  public abstract boolean isMandatory();
  
  public abstract void setRestartRequired(int paramInt);
  
  public abstract int getRestartRequired();
  
  public abstract void setUserObject(Object paramObject);
  
  public abstract Object getUserObject();
  
  public abstract void complete(boolean paramBoolean);
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
  
  public abstract boolean isComplete();
  
  public abstract boolean wasSuccessful();
  
  public abstract UpdateCheckInstance getCheckInstance();
  
  public abstract Object getDecision(int paramInt, String paramString1, String paramString2, Object paramObject);
  
  public abstract InputStream verifyData(InputStream paramInputStream, boolean paramBoolean)
    throws UpdateException;
  
  public abstract void addListener(UpdateListener paramUpdateListener);
  
  public abstract void removeListener(UpdateListener paramUpdateListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/Update.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */