package org.gudy.azureus2.plugins.update;

public abstract interface UpdateCheckInstance
{
  public static final int UCI_INSTALL = 1;
  public static final int UCI_UPDATE = 2;
  public static final int UCI_UNINSTALL = 3;
  public static final int PT_UI_STYLE = 1;
  public static final int PT_UI_STYLE_DEFAULT = 1;
  public static final int PT_UI_STYLE_SIMPLE = 2;
  public static final int PT_UI_STYLE_NONE = 3;
  public static final int PT_UI_PARENT_SWT_COMPOSITE = 2;
  public static final int PT_UI_DISABLE_ON_SUCCESS_SLIDEY = 3;
  public static final int PT_CLOSE_OR_RESTART_ALREADY_IN_PROGRESS = 4;
  public static final int PT_UNINSTALL_RESTART_REQUIRED = 5;
  
  public abstract int getType();
  
  public abstract String getName();
  
  public abstract void start();
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
  
  public abstract UpdateChecker[] getCheckers();
  
  public abstract Update[] getUpdates();
  
  public abstract UpdateInstaller createInstaller()
    throws UpdateException;
  
  public abstract void addUpdatableComponent(UpdatableComponent paramUpdatableComponent, boolean paramBoolean);
  
  public abstract UpdateManager getManager();
  
  public abstract void setAutomatic(boolean paramBoolean);
  
  public abstract boolean isAutomatic();
  
  public abstract void setLowNoise(boolean paramBoolean);
  
  public abstract boolean isLowNoise();
  
  public abstract boolean isCompleteOrCancelled();
  
  public abstract Object getProperty(int paramInt);
  
  public abstract void setProperty(int paramInt, Object paramObject);
  
  public abstract void addDecisionListener(UpdateManagerDecisionListener paramUpdateManagerDecisionListener);
  
  public abstract void removeDecisionListener(UpdateManagerDecisionListener paramUpdateManagerDecisionListener);
  
  public abstract void addListener(UpdateCheckInstanceListener paramUpdateCheckInstanceListener);
  
  public abstract void removeListener(UpdateCheckInstanceListener paramUpdateCheckInstanceListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateCheckInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */