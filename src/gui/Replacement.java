package gui;

import java.util.regex.Pattern;

public class Replacement {
	public String name;
	public Pattern pattern;
	public Replacement(Pattern compile, String string) {
		this.pattern = compile;
		this.name = string;
	}
	public String toString(){return name;}
}
