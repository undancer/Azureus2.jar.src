package org.gudy.azureus2.plugins.sharing;

public abstract interface ShareResourceWillBeDeletedListener
{
  public abstract void resourceWillBeDeleted(ShareResource paramShareResource)
    throws ShareResourceDeletionVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareResourceWillBeDeletedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */