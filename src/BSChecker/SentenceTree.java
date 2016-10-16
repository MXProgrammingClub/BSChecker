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
	public boolean severTie(){return parent.getChildren().remove(this);}
	public SentenceTree fix(){
		if(children.size() == 0)
			return this;
		if(children.size() == 1)
			return children.get(0).fix();
		ArrayList<SentenceTree> arr = new ArrayList<SentenceTree>();
		for(SentenceTree st: children)
			arr.add(st.fix());
		SentenceTree verb = null;
		SentenceTree subject = null;
		for(SentenceTree st: arr){
			if(verb != null && st.getTag().charAt(1) == 'V')
				verb = st;
			if(subject != null && st.getTag().substring(0,2).equals("NN"))
				subject = st;
		}
		boolean nounIsSing = subject.getTag().charAt(subject.getTag().length()-1) != 'S', verbIsSing = verb.getTag().equals("VBZ");
		if(nounIsSing != verbIsSing){
			if(!severTie()){
				System.out.println("ERROR: COULD NOT REMOVE");
				return null;
			}
			else{
				System.out.println("Not the same");
			}
		}
		return null;
	}
}
