package ca.nicholaspaun.chemlogic.bindings;

import ca.nicholaspaun.chemlogic.prolog.*;
import jp.ac.kobe_u.cs.prolog.lang.*;

public class StupidFromJava {
    public static String getCapital(String country) {
	PrologControl prolog = new PrologControl();
	Predicate capital = new PRED_capital_2();
	Term t_country = SymbolTerm.makeSymbol(country);
	Term t_capital = new VariableTerm();
	Term[] args = { t_country, t_capital };
	//prolog.setPredicate(capital, args);
/*
	for (boolean r = prolog.call(); r; r = prolog.redo()) {
	    System.out.println(t_capital.toString());
	}
*/

	if (prolog.execute(capital,args))
	 return(t_capital.toString());
	else
	 return("no.");
    }

    public static void main(String[] args) {
	System.out.println(getCapital(args[0]));
    }
}
