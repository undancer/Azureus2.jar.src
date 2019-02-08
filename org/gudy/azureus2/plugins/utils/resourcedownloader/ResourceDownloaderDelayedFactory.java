package org.gudy.azureus2.plugins.utils.resourcedownloader;

public abstract interface ResourceDownloaderDelayedFactory
{
  public abstract ResourceDownloader create()
    throws ResourceDownloaderException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderDelayedFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */