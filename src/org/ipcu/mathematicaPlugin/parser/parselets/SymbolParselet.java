/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ipcu.mathematicaPlugin.parser.parselets;

import com.intellij.lang.PsiBuilder;
import org.ipcu.mathematicaPlugin.parser.MathematicaParser;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;

/**
 * @author patrick (3/27/13)
 *
 */
public class SymbolParselet implements PrefixParselet {

    private final int precedence;

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) {
        final PsiBuilder.Marker symbolMark = parser.getBuilder().mark();
        parser.advanceLexer();
        symbolMark.done(SYMBOL_EXPRESSION);
        return parser.result(symbolMark, SYMBOL_EXPRESSION, true);
    }

    public SymbolParselet(int precedence) {
        this.precedence = precedence;
    }
}