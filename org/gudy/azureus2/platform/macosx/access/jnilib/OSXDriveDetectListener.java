package org.gudy.azureus2.platform.macosx.access.jnilib;

import java.io.File;
import java.util.Map;

public abstract interface OSXDriveDetectListener
{
  public abstract void driveDetected(File paramFile, Map paramMap);
  
  public abstract void driveRemoved(File paramFile, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/macosx/access/jnilib/OSXDriveDetectListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */