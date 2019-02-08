package org.gudy.azureus2.plugins.update;

public abstract interface UpdateManagerDecisionListener
{
  public static final int DT_STRING_ARRAY_TO_STRING = 0;
  
  public abstract Object decide(Update paramUpdate, int paramInt, String paramString1, String paramString2, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateManagerDecisionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */