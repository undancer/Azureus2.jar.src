package com.aelitis.azureus.core.tag;

import java.util.List;

public abstract interface TaggableLifecycleListener
{
  public abstract void initialised(List<Taggable> paramList);
  
  public abstract void taggableCreated(Taggable paramTaggable);
  
  public abstract void taggableDestroyed(Taggable paramTaggable);
  
  public abstract void taggableTagged(TagType paramTagType, Tag paramTag, Taggable paramTaggable);
  
  public abstract void taggableUntagged(TagType paramTagType, Tag paramTag, Taggable paramTaggable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TaggableLifecycleListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */