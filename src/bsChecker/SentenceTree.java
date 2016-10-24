package bsChecker;

import java.util.ArrayList;

import opennlp.tools.parser.Parse;

public class SentenceTree {
	private SentenceTree parent;
	private ArrayList<SentenceTree> children;
	private String label, tag;
	public SentenceTree(SentenceTree parent, Parse parse){
		label = parse.getLabel();
		System.out.println(parse.getText());
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
		if(children.size() == 1){
			System.out.println("Unary child");
			return children.get(0).fix();
		}
		ArrayList<SentenceTree> arr = new ArrayList<SentenceTree>();
		for(SentenceTree st: children){
			SentenceTree fixed = st.fix();
			if(fixed != null){
				arr.add(fixed);
				System.out.println(fixed.getTag() + ":" + fixed.getLabel());
			}
			else System.out.println("Null tree!");
		}
		SentenceTree verb = null;
		SentenceTree subject = null;
		for(SentenceTree st: arr){
			System.out.println("In sent tree");
			if(st.getTag().charAt(0) == 'V')
				verb = st;
			if(st.getTag().substring(0,2).equals("NN") || st.getTag().substring(0,2).equals("NP"))
				subject = st;
		}
		boolean nounIsSing = false, verbIsSing = false;
		if(verb != null && subject != null){
			System.out.println("in");
			nounIsSing = subject.getTag().charAt(subject.getTag().length()-1) != 'S'; 
			verbIsSing = verb.getTag().equals("VBZ");
		}
		if(nounIsSing != verbIsSing){
				System.out.println("Not the same");
				return this;
		}
		else if(verb == null && subject != null)
			return subject;
		else if(subject == null && verb != null)
			return verb;
		else
			return this;
	}
}
