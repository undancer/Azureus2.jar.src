package org.gudy.azureus2.plugins.sharing;

import java.io.File;
import java.util.Map;

public abstract interface ShareManager
{
  public static final String PR_PERSONAL = "personal";
  public static final String PR_NETWORKS = "networks";
  public static final String PR_TAGS = "tags";
  public static final String PR_USER_DATA = "user_data";
  public static final String PR_PERSISTENT = "persistent";
  
  public abstract void initialise()
    throws ShareException;
  
  public abstract boolean isInitialising();
  
  public abstract ShareResource[] getShares();
  
  public abstract ShareResource getShare(File paramFile);
  
  public abstract ShareResourceFile addFile(File paramFile)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract ShareResourceFile addFile(File paramFile, Map<String, String> paramMap)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract ShareResourceDir addDir(File paramFile)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract ShareResourceDir addDir(File paramFile, Map<String, String> paramMap)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract ShareResourceDirContents addDirContents(File paramFile, boolean paramBoolean)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract ShareResourceDirContents addDirContents(File paramFile, boolean paramBoolean, Map<String, String> paramMap)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract void cancelOperation();
  
  public abstract void addListener(ShareManagerListener paramShareManagerListener);
  
  public abstract void removeListener(ShareManagerListener paramShareManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */