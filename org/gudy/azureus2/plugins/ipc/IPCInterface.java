package org.gudy.azureus2.plugins.ipc;

public abstract interface IPCInterface
{
  public abstract Object invoke(String paramString, Object[] paramArrayOfObject)
    throws IPCException;
  
  public abstract boolean canInvoke(String paramString, Object[] paramArrayOfObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipc/IPCInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */