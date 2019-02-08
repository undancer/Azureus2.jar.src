package com.aelitis.azureus.core.tag;

public abstract interface Taggable
  extends org.gudy.azureus2.plugins.tag.Taggable
{
  public static final int TT_DOWNLOAD = 2;
  public static final int TT_PEER = 4;
  
  public abstract int getTaggableType();
  
  public abstract String getTaggableID();
  
  public abstract TaggableResolver getTaggableResolver();
  
  public abstract void setTaggableTransientProperty(String paramString, Object paramObject);
  
  public abstract Object getTaggableTransientProperty(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/Taggable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */