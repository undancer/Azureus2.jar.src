package com.aelitis.azureus.core.tag;

public abstract interface TagListener
{
  public abstract void taggableAdded(Tag paramTag, Taggable paramTaggable);
  
  public abstract void taggableSync(Tag paramTag);
  
  public abstract void taggableRemoved(Tag paramTag, Taggable paramTaggable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */