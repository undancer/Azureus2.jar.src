package com.aelitis.azureus.core.backup;

import java.io.File;

public abstract interface BackupManager
{
  public abstract void backup(File paramFile, BackupListener paramBackupListener);
  
  public abstract void restore(File paramFile, BackupListener paramBackupListener);
  
  public abstract void runAutoBackup(BackupListener paramBackupListener);
  
  public abstract long getLastBackupTime();
  
  public abstract String getLastBackupError();
  
  public static abstract interface BackupListener
  {
    public abstract boolean reportProgress(String paramString);
    
    public abstract void reportComplete();
    
    public abstract void reportError(Throwable paramThrowable);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/backup/BackupManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */