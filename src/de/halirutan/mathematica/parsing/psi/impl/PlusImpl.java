package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/27/13
 * Time: 11:25 PM
 * Purpose:
 */
public class PlusImpl extends ExpressionImpl {
    public PlusImpl(@NotNull ASTNode node) {
        super(node);
    }
}