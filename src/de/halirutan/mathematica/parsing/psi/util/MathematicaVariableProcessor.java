/*
 * Copyright (c) 2013 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.parsing.psi.util;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSetDelayed;
import de.halirutan.mathematica.parsing.psi.api.rules.RuleDelayed;
import de.halirutan.mathematica.parsing.psi.impl.SymbolPsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provides the functionality of resolving references. This means, this class takes care to find out where a local
 * variable was defined and it can be used to find all references of a variable inside a scope.
 *
 * @author patrick (5/22/13)
 */
public class MathematicaVariableProcessor extends BaseScopeProcessor {

  private final Symbol myStartElement;
  private PsiElement myReferringSymbol;
  private LocalizationConstruct.ConstructType myLocalization;
  private PsiElement myLocalizationSymbol;

  public MathematicaVariableProcessor(Symbol myStartElement) {
    this.myStartElement = myStartElement;
    this.myReferringSymbol = null;
    this.myLocalization = LocalizationConstruct.ConstructType.NULL;

  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof FunctionCall) {
      final FunctionCall functionCall = (FunctionCall) element;
      if (functionCall.isScopingConstruct()) {
        List<Symbol> vars = Lists.newArrayList();
        final LocalizationConstruct.ConstructType scopingConstruct = functionCall.getScopingConstruct();

        if (LocalizationConstruct.isFunctionLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalFunctionVariables(functionCall);
        }

        if (LocalizationConstruct.isModuleLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalModuleLikeVariables(functionCall);
        }

        if (LocalizationConstruct.isTableLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalTableLikeVariables(functionCall);
        }

        if (LocalizationConstruct.isManipulateLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalManipulateLikeVariables(functionCall);
        }

        if (LocalizationConstruct.isCompileLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalCompileLikeVariables(functionCall);
        }

        if (LocalizationConstruct.isLimitLike(scopingConstruct)) {
          vars = MathematicaPsiUtililities.getLocalLimitVariables(functionCall);
        }

        for (Symbol v : vars) {
          if (v.getSymbolName().equals(myStartElement.getSymbolName())) {
            myReferringSymbol = v;
            myLocalizationSymbol = element.getFirstChild();
            myLocalization = scopingConstruct;
            return false;
          }
        }
      }
    } else if (element instanceof SetDelayed || element instanceof TagSetDelayed) {

      MathematicaPatternVisitor patternVisitor = new MathematicaPatternVisitor();
      element.accept(patternVisitor);
      for (Symbol p : patternVisitor.getMyPatternSymbols()) {
        if (p.getSymbolName().equals(myStartElement.getSymbolName())) {
          myReferringSymbol = p;
          myLocalization = LocalizationConstruct.ConstructType.SETDELAYEDPATTERN;
          return false;
        }
      }
    } else if (element instanceof RuleDelayed) {
      PsiElement lhs = element.getFirstChild();
      MathematicaPatternVisitor patternVisitor = new MathematicaPatternVisitor();
      lhs.accept(patternVisitor);

      for (Symbol symbol : patternVisitor.getMyPatternSymbols()) {
        if (symbol.getSymbolName().equals(myStartElement.getSymbolName())) {
          myReferringSymbol = symbol;
          myLocalization = LocalizationConstruct.ConstructType.RULEDELAYED;
          return false;
        }
      }


    }
    return true;
  }

  /**
   * Returns the list of all symbols collected during a {@link SymbolPsiReference#getVariants()} run. Before returning
   * the list, it removes duplicates, so that no entry appears more than once in the autocompletion window.
   *
   * @return Sorted and cleaned list of collected symbols.
   */
  @Nullable
  public PsiElement getMyReferringSymbol() {
    return myReferringSymbol;
  }

  public LocalizationConstruct.ConstructType getMyLocalization() {
    return myLocalization;
  }

  public PsiElement getMyLocalizationSymbol() {
    return myLocalizationSymbol;
  }
}
