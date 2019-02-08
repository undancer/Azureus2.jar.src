package org.gudy.azureus2.plugins.utils.resourceuploader;

import java.io.InputStream;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;

public abstract interface ResourceUploader
{
  public abstract InputStream upload()
    throws ResourceUploaderException;
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws ResourceDownloaderException;
  
  public abstract Object getProperty(String paramString)
    throws ResourceDownloaderException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */