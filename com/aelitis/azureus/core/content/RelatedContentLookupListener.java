package com.aelitis.azureus.core.content;

public abstract interface RelatedContentLookupListener
{
  public abstract void lookupStart();
  
  public abstract void contentFound(RelatedContent[] paramArrayOfRelatedContent);
  
  public abstract void lookupComplete();
  
  public abstract void lookupFailed(ContentException paramContentException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/RelatedContentLookupListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */