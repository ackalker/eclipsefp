// Copyright (c) 2006 by Leif Frenzel <himself@leiffrenzel.de>
// All rights reserved.
package net.sf.eclipsefp.haskell.ui.internal.editors.cabal;

import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescription;
import net.sf.eclipsefp.haskell.core.cabalmodel.PackageDescriptionLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

class CabalReconcilingStrategy implements IReconcilingStrategy,
                                                 IReconcilingStrategyExtension {

  private final CabalFormEditor editor;
  private IDocument document;
  private final CabalFoldingStructureProvider foldingStructureProvider;

  CabalReconcilingStrategy( final CabalFormEditor editor ) {
    this.editor = editor;
    foldingStructureProvider = new CabalFoldingStructureProvider( editor );
  }


  // interface methods of IReconcilingStrategy
  ////////////////////////////////////////////

  @Override
  public void reconcile( final IRegion partition ) {
    reconcile();
  }

  @Override
  public void reconcile( final DirtyRegion dirtyRegion, final IRegion subRegion ) {
    reconcile();
  }

  @Override
  public void setDocument( final IDocument document ) {
    this.document = document;
    foldingStructureProvider.setDocument( document );
  }


  // interface methods of IReconcilingStrategyExtension
  /////////////////////////////////////////////////////

  @Override
  public void initialReconcile() {
    reconcile();
  }

  @Override
  public void setProgressMonitor( final IProgressMonitor monitor ) {
    // unused
  }


  // helping methods
  //////////////////

  private void reconcile() {
    String content = document.get();
    final PackageDescription pd = PackageDescriptionLoader.load( content );
    if (editor!=null){
      Display.getDefault().asyncExec( new Runnable() {
        @Override
        public void run() {
          editor.setPackageDescription( pd );
        }
      } );
      foldingStructureProvider.updateFoldingRegions( pd );
    }
  }
}
