package org.gudy.azureus2.plugins.utils;

import java.util.Map;

public abstract interface ScriptProvider
{
  public static final String ST_JAVASCRIPT = "javascript";
  
  public abstract String getProviderName();
  
  public abstract String getScriptType();
  
  public abstract Object eval(String paramString, Map<String, Object> paramMap)
    throws Exception;
  
  public static abstract interface ScriptProviderListener
  {
    public abstract void scriptProviderAdded(ScriptProvider paramScriptProvider);
    
    public abstract void scriptProviderRemoved(ScriptProvider paramScriptProvider);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/ScriptProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */