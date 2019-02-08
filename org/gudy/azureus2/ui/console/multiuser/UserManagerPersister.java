package org.gudy.azureus2.ui.console.multiuser;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract interface UserManagerPersister
{
  public abstract void doLoad(InputStream paramInputStream, Map paramMap);
  
  public abstract void doSave(OutputStream paramOutputStream, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/UserManagerPersister.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */