package org.gudy.azureus2.ui.swt.wizard;

public abstract interface IWizardPanel<W>
{
  public abstract void show();
  
  public abstract IWizardPanel<W> getNextPanel();
  
  public abstract IWizardPanel<W> getPreviousPanel();
  
  public abstract IWizardPanel<W> getFinishPanel();
  
  public abstract boolean isPreviousEnabled();
  
  public abstract boolean isNextEnabled();
  
  public abstract boolean isFinishEnabled();
  
  public abstract boolean isFinishSelectionOK();
  
  public abstract void cancelled();
  
  public abstract void finish();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/wizard/IWizardPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */