package org.gudy.azureus2.plugins.torrent;

import java.util.List;
import org.gudy.azureus2.plugins.tag.Tag;

public abstract interface TorrentOptions
{
  public abstract Torrent getTorrent();
  
  public abstract List<Tag> getTags();
  
  public abstract void addTag(Tag paramTag);
  
  public abstract void removeTag(Tag paramTag);
  
  public abstract void accept();
  
  public abstract void cancel();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */