package org.gudy.azureus2.ui.swt.mainwindow;

import com.aelitis.azureus.ui.UIStatusTextClickListener;
import org.gudy.azureus2.ui.swt.update.UpdateWindow;

public abstract interface IMainStatusBar
{
  public abstract void createStatusEntry(CLabelUpdater paramCLabelUpdater);
  
  public abstract boolean isMouseOver();
  
  public abstract void setUpdateNeeded(UpdateWindow paramUpdateWindow);
  
  public abstract void setStatusText(String paramString);
  
  public abstract void setStatusText(int paramInt, String paramString, UIStatusTextClickListener paramUIStatusTextClickListener);
  
  public abstract void setDebugInfo(String paramString);
  
  public static abstract interface CLabelUpdater
  {
    public abstract void created(MainStatusBar.CLabelPadding paramCLabelPadding);
    
    public abstract boolean update(MainStatusBar.CLabelPadding paramCLabelPadding);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/IMainStatusBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */