package com.aelitis.azureus.core.tag;

import java.util.List;

public class TaggableLifecycleAdapter
  implements TaggableLifecycleListener
{
  public void initialised(List<Taggable> current_taggables) {}
  
  public void taggableCreated(Taggable taggable) {}
  
  public void taggableDestroyed(Taggable taggable) {}
  
  public void taggableTagged(TagType tag_type, Tag tag, Taggable taggable) {}
  
  public void taggableUntagged(TagType tag_type, Tag tag, Taggable taggable) {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TaggableLifecycleAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */