package BSChecker;

import java.util.ArrayList;

import opennlp.tools.parser.Parse;

public class SentenceTree {
	private SentenceTree parent;
	private ArrayList<SentenceTree> children;
	private String label, tag;
	public SentenceTree(SentenceTree parent, Parse parse){
		label = parse.getLabel();
		this.parent = parent;
		tag = parse.getType();
		children = new ArrayList<SentenceTree>();
		Parse[] childs = parse.getChildren();
		for(Parse child: childs){
			children.add(new SentenceTree(this,child));
		}
	}
	
	public String getLabel(){return label;}
	public String getTag(){return tag;}
	public boolean hasChildren(){return children.size()==0;}
	public ArrayList<SentenceTree> getChildren(){return children;}
	public SentenceTree getParent(){return parent;}
}
