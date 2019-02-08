package com.aelitis.azureus.core.tag;

import java.util.List;

public abstract interface TaggableResolver
{
  public abstract long getResolverTaggableType();
  
  public abstract List<Taggable> getResolvedTaggables();
  
  public abstract Taggable resolveTaggable(String paramString);
  
  public abstract String getDisplayName(Taggable paramTaggable);
  
  public abstract void requestAttention(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TaggableResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */