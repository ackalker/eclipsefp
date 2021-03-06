/**
 * Copyright (c) 2012 by JP Moresmau
 * This code is made available under the terms of the Eclipse Public License,
 * version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
 */
package net.sf.eclipsefp.haskell.ui.handlers;

import net.sf.eclipsefp.haskell.buildwrapper.types.ThingAtPoint;
import net.sf.eclipsefp.haskell.buildwrapper.usage.UsageQueryFlags;
import net.sf.eclipsefp.haskell.ui.internal.editors.haskell.HaskellEditor;
import net.sf.eclipsefp.haskell.ui.internal.search.UsageQuery;
import net.sf.eclipsefp.haskell.ui.internal.util.UITexts;
import net.sf.eclipsefp.haskell.ui.util.text.WordFinder;
import net.sf.eclipsefp.haskell.ui.util.text.WordFinder.EditorThing;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;


/**
 * @author JP Moresmau
 *
 */
public class ReferencesWorkspaceHandler extends AbstractHandler {
  protected IProject project=null;

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute( final ExecutionEvent event )     {
    IEditorPart editor = HandlerUtil.getActiveEditor( event );
    if( !( editor instanceof HaskellEditor ) ) {
      return null;
    }
    final ISearchResultViewPart p=NewSearchUI.activateSearchResultView();
    //NewSearchUI.getSearchResultView();


    final HaskellEditor haskellEditor = ( HaskellEditor )editor;
    WordFinder.getEditorThing( haskellEditor,
        new WordFinder.EditorThingHandler() {

          @Override
          public void handle( final EditorThing thing ) {
            if (thing!=null){
              final UsageQuery uq=getUsageQuery( haskellEditor, project, thing.getThing() );
              new UIJob( UITexts.openDefinition_select_job ) {

                @Override
                public IStatus runInUIThread( final IProgressMonitor monitor ) {

                  NewSearchUI.runQueryInBackground( uq,p);
                  return Status.OK_STATUS;
                }
              }.schedule();
            }
          }
    }  );
    return null;
  }

  public static UsageQuery getUsageQuery(final HaskellEditor haskellEditor,final IProject project,final ThingAtPoint thing){
    String term=haskellEditor.getModuleName();
    int typeFlags=UsageQueryFlags.TYPE_ALL;
    if(thing!=null && thing.getName()!=null  && thing.getName().length()>0) {
      String shortName = thing.getName();
      term=shortName;
      if ("m".equals(thing.getHaddockType()) || term.equals( haskellEditor.getModuleName() )){
        typeFlags=UsageQueryFlags.TYPE_MODULE;
      } else {
          String module = thing.getModule();
          if (module!=null){
            term=module+"."+shortName;
          }
          if (shortName.length()>0 && Character.isUpperCase( shortName.charAt( 0 ) )){
            String ht=thing.getHaddockType();

            typeFlags="t".equals( ht )?UsageQueryFlags.TYPE_TYPE : UsageQueryFlags.TYPE_CONSTRUCTOR;
          } else {
            typeFlags=UsageQueryFlags.TYPE_VAR;
          }
      }
    }

    final UsageQuery uq=new UsageQuery(term,project);
    uq.setTypeFlags( typeFlags );
    uq.setScopeFlags( UsageQueryFlags.SCOPE_REFERENCES );
    return uq;
  }

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.AbstractHandler#setBaseEnabled(boolean)
   */
  @Override
  protected void setBaseEnabled( final boolean state ) {
    // TODO Auto-generated method stub
    super.setBaseEnabled( state );
  }

  /* (non-Javadoc)
   * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
   */
  @Override
  public void setEnabled( final Object evaluationContext ) {
    // TODO Auto-generated method stub
    super.setEnabled( evaluationContext );
  }
}
