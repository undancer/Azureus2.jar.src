package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureTranscode
{
  public abstract boolean supportsTagTranscode();
  
  public abstract String[] getTagTranscodeTarget();
  
  public abstract void setTagTranscodeTarget(String paramString1, String paramString2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureTranscode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */