package org.gudy.azureus2.plugins.update;

public abstract interface UpdateManager
{
  public abstract void registerUpdatableComponent(UpdatableComponent paramUpdatableComponent, boolean paramBoolean);
  
  public abstract UpdateCheckInstance createUpdateCheckInstance();
  
  public abstract UpdateCheckInstance createUpdateCheckInstance(int paramInt, String paramString);
  
  public abstract UpdateCheckInstance createEmptyUpdateCheckInstance(int paramInt, String paramString);
  
  public abstract UpdateCheckInstance[] getCheckInstances();
  
  public abstract UpdateInstaller createInstaller()
    throws UpdateException;
  
  public abstract String getInstallDir();
  
  public abstract String getUserDir();
  
  public abstract UpdateInstaller[] getInstallers();
  
  /**
   * @deprecated
   */
  public abstract void restart()
    throws UpdateException;
  
  public abstract void applyUpdates(boolean paramBoolean)
    throws UpdateException;
  
  public abstract void addVerificationListener(UpdateManagerVerificationListener paramUpdateManagerVerificationListener);
  
  public abstract void removeVerificationListener(UpdateManagerVerificationListener paramUpdateManagerVerificationListener);
  
  public abstract void addListener(UpdateManagerListener paramUpdateManagerListener);
  
  public abstract void removeListener(UpdateManagerListener paramUpdateManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */