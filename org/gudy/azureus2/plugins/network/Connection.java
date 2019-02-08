package org.gudy.azureus2.plugins.network;

public abstract interface Connection
{
  public abstract void connect(ConnectionListener paramConnectionListener);
  
  public abstract void close();
  
  public abstract OutgoingMessageQueue getOutgoingMessageQueue();
  
  public abstract IncomingMessageQueue getIncomingMessageQueue();
  
  public abstract void startMessageProcessing();
  
  public abstract Transport getTransport();
  
  public abstract boolean isIncoming();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/Connection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */