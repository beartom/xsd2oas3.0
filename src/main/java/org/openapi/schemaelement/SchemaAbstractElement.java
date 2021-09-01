package org.openapi.schemaelement;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class SchemaAbstractElement {

    List<SchemaAbstractElement> subElements = new ArrayList<>();


    //the target namespace abbreviation
    private final String prefix;

    private int minOccurs=0;

    private int MaxOccurs=1;

    private boolean unbounds = false;

    abstract public StringBuilder convertSchema();

    /**
     *
     * @return The name to ref
     */
    public String getFullName() {
        return "";
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return MaxOccurs;
    }

    public void setMaxOccurs(int MaxOccurs) {
        this.MaxOccurs = MaxOccurs;
    }

    public Boolean getUnbounds() {
        return unbounds;
    }

    public void setUnbounds(Boolean unbounds) {
        this.unbounds = unbounds;
    }

    public SchemaAbstractElement(String prefix){
        this.prefix = prefix;
    }

    public boolean isRequired(){
        return this.minOccurs>0;
    }

    public boolean isArray(){
        return this.MaxOccurs>1 || this.unbounds;
    }

    public void addSubElement(SchemaAbstractElement element){
        this.subElements.add(element);
    }

    public List<SchemaAbstractElement> getSubElements() {
        return subElements;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isContainer(){
        return false;
    }

    private final static  String retractSpace = "  ";

    public static StringBuilder retract(StringBuilder jsonInstance,int spaceCount){
        if(jsonInstance.length()<=0){
            return jsonInstance;
        }
        StringBuilder spaces = new StringBuilder();
        for(int i=0;i<spaceCount;i++){
            spaces.append(retractSpace);
        }
        int tmpIndex = -1;
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);

        while(tmpIndex<(jsonInstance.length()-1) &&(tmpIndex=jsonInstance.indexOf("\n",tmpIndex+1))!=-1){
            stack.push(tmpIndex);
        }
        while (stack.size() > 0) {
            int index = stack.pop();
            if (index == jsonInstance.length() - 1 && index > 2) {
                //last character is line breaker.
                if (jsonInstance.charAt(index) == '\n' && jsonInstance.charAt(index - 1) == ',' && (jsonInstance.charAt(index - 2) == '|'||jsonInstance.charAt(index - 2) == '}')) {
                    jsonInstance.deleteCharAt(index - 1);
                    continue;
                }
            }
            jsonInstance.insert(index + 1, spaces);
        }
        return jsonInstance;
    }
}
