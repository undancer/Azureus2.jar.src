package org.gudy.azureus2.plugins.sharing;

import java.util.Map;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

public abstract interface ShareResource
{
  public static final int ST_FILE = 1;
  public static final int ST_DIR = 2;
  public static final int ST_DIR_CONTENTS = 3;
  
  public abstract int getType();
  
  public abstract String getName();
  
  public abstract void delete()
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract void delete(boolean paramBoolean)
    throws ShareException, ShareResourceDeletionVetoException;
  
  public abstract void setAttribute(TorrentAttribute paramTorrentAttribute, String paramString);
  
  public abstract String getAttribute(TorrentAttribute paramTorrentAttribute);
  
  public abstract TorrentAttribute[] getAttributes();
  
  public abstract Map<String, String> getProperties();
  
  public abstract boolean isPersistent();
  
  public abstract boolean canBeDeleted()
    throws ShareResourceDeletionVetoException;
  
  public abstract ShareResourceDirContents getParent();
  
  public abstract void addChangeListener(ShareResourceListener paramShareResourceListener);
  
  public abstract void removeChangeListener(ShareResourceListener paramShareResourceListener);
  
  public abstract void addDeletionListener(ShareResourceWillBeDeletedListener paramShareResourceWillBeDeletedListener);
  
  public abstract void removeDeletionListener(ShareResourceWillBeDeletedListener paramShareResourceWillBeDeletedListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */