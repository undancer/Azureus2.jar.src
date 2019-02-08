package com.aelitis.azureus.core.tag;

import java.util.List;

public abstract interface TaggableLifecycleHandler
{
  public abstract void initialized(List<Taggable> paramList);
  
  public abstract void taggableCreated(Taggable paramTaggable);
  
  public abstract void taggableDestroyed(Taggable paramTaggable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TaggableLifecycleHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */