package com.aelitis.azureus.core.tag;

public abstract interface TagTypeListener
{
  public abstract void tagTypeChanged(TagType paramTagType);
  
  public abstract void tagEventOccurred(TagEvent paramTagEvent);
  
  public static abstract interface TagEvent
  {
    public static final int ET_TAG_ADDED = 0;
    public static final int ET_TAG_CHANGED = 1;
    public static final int ET_TAG_REMOVED = 2;
    public static final int ET_TAG_ATTENTION_REQUESTED = 3;
    
    public abstract Tag getTag();
    
    public abstract int getEventType();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagTypeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */