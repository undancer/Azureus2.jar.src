package org.gudy.azureus2.plugins.clientid;

import java.util.Properties;

public abstract interface ClientIDGenerator
{
  public static final String PR_URL = "URL";
  public static final String PR_PROXY = "Proxy";
  public static final String PR_RAW_REQUEST = "Raw-Request";
  public static final String PR_USER_AGENT = "User-Agent";
  public static final String PR_SNI_HACK = "SNI-Hack";
  public static final String PR_CLIENT_NAME = "Client-Name";
  public static final String PR_MESSAGING_MODE = "Messaging-Mode";
  
  public abstract byte[] generatePeerID(byte[] paramArrayOfByte, boolean paramBoolean)
    throws ClientIDException;
  
  public abstract void generateHTTPProperties(byte[] paramArrayOfByte, Properties paramProperties)
    throws ClientIDException;
  
  public abstract String[] filterHTTP(byte[] paramArrayOfByte, String[] paramArrayOfString)
    throws ClientIDException;
  
  public abstract Object getProperty(byte[] paramArrayOfByte, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/clientid/ClientIDGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */