/**
 * (c) 2011, Alejandro Serrano
 * Released under the terms of the EPL.
 */
package net.sf.eclipsefp.haskell.ui.internal.editors.cabal.forms.overview;

import net.sf.eclipsefp.haskell.ui.internal.editors.cabal.forms.Choice;

/**
 * Information for showing the available licenses for a Cabal package.
 * @author Alejandro Serrano
 *
 */
public class LicenseChoice extends Choice<License> {

  @Override
  public License[] getValues() {
    return License.values();
  }

  @Override
  public boolean allowOther() {
    return true;
  }

  @Override
  public License fromCabalString( final String s ) {
    for( License l: getValues() ) {
      if( l.getCabalName().equals( s ) ) {
        return l;
      }
    }
    return null;
  }

  @Override
  public String toCabalString( final License o ) {
    return o.getCabalName();
  }

  @Override
  public License fromShownString( final String s ) {
    for( License l: getValues() ) {
      if( l.getShownName().equals( s ) ) {
        return l;
      }
    }
    return null;
  }

  @Override
  public String toShownString( final License o ) {
    return o.getShownName();
  }

}
