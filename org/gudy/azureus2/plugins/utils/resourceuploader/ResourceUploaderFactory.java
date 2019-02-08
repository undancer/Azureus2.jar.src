package org.gudy.azureus2.plugins.utils.resourceuploader;

import java.io.InputStream;
import java.net.URL;

public abstract interface ResourceUploaderFactory
{
  public abstract ResourceUploader create(URL paramURL, InputStream paramInputStream);
  
  public abstract ResourceUploader create(URL paramURL, InputStream paramInputStream, String paramString1, String paramString2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourceuploader/ResourceUploaderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */