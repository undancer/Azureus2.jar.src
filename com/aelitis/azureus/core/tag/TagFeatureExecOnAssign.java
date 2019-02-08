package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureExecOnAssign
  extends TagFeature
{
  public static final int ACTION_NONE = 0;
  public static final int ACTION_DESTROY = 1;
  public static final int ACTION_START = 2;
  public static final int ACTION_STOP = 4;
  public static final int ACTION_FORCE_START = 8;
  public static final int ACTION_NOT_FORCE_START = 16;
  public static final int ACTION_SCRIPT = 32;
  public static final int ACTION_PAUSE = 64;
  public static final int ACTION_RESUME = 128;
  
  public abstract int getSupportedActions();
  
  public abstract boolean supportsAction(int paramInt);
  
  public abstract boolean isActionEnabled(int paramInt);
  
  public abstract void setActionEnabled(int paramInt, boolean paramBoolean);
  
  public abstract String getActionScript();
  
  public abstract void setActionScript(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureExecOnAssign.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */