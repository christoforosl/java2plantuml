package translate.uml.provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import translate.ITranslator;
import translate.IUmlProvider;

import java.util.HashSet;

public class PlantUmlProvider implements IUmlProvider {
    private ITranslator umlTranslator;

    @Override
    public String getExtension() {
        return "puml";
    }

    @Override
    public String getUML(ITranslator umlTranslator) {

        this.umlTranslator = umlTranslator;

        StringBuilder sb = new StringBuilder();

        sb.append("@startuml");
        sb.append("\n");

        if(!umlTranslator.getConfig().isShowColoredAccessSpecifiers()) {
            sb.append("skinparam classAttributeIconSize 0\n");
        }
        writeClasses(sb);
        writeAssociations(sb);
        writeInterfaces(sb);
        writeEnumerations(sb);

        sb.append("@enduml");

        return sb.toString();
    }

    private void writeAssociations(StringBuilder sb) {

        HashSet<String> temp = new HashSet<>();
        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getClassSet()){
            temp.add(c.getNameAsString());
        }
        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getInterfaceSet()){
            temp.add(c.getNameAsString());
        }
        for (EnumDeclaration e:this.umlTranslator.getEnumSet()){
            temp.add(e.getNameAsString());
        }

        for(ClassOrInterfaceDeclaration c: umlTranslator.getClassSet()){

            for(FieldDeclaration f: c.getFields()){

                if(temp.contains(f.getVariables().get(0).getType().asString())){

                    sb.append(c.getName().asString());
                    sb.append("--");
                    sb.append("\"");
                    writeModifiers(f.getModifiers(),sb);
                    sb.append(f.getVariables().get(0).getName());
                    sb.append("\" ");
                    sb.append(f.getVariables().get(0).getType().asString());
                    sb.append("\n");
                }

            }

        }

    }


    private void writeClasses(StringBuilder sb){

        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getClassSet()){
            writeClass(c,sb);
        }

    }

    private void writeClass(ClassOrInterfaceDeclaration c,StringBuilder sb) {

        sb.append("class ");
        sb.append(c.getName());
        sb.append("{");
        sb.append("\n");
        //attributes
        if (this.umlTranslator.getConfig().isShowAttributes()){
            writeAttributes(c, sb);
        }

        if(this.umlTranslator.getConfig().isShowMethods()) {
            //methods
            writeConstructors(c,sb);
            writeMethods(c, sb);
        }

        sb.append("}\n");

        //implemented interfaces
        for(ClassOrInterfaceType e: c.getImplementedTypes()){

            sb.append(c.getName());
            sb.append(" ..|> ");
            sb.append(e.getName());
            sb.append("\n");
        }

        //extended classes
        for(ClassOrInterfaceType e: c.getExtendedTypes()){

            sb.append(c.getName());
            sb.append(" --|> ");
            sb.append(e.getName());
            sb.append("\n");
        }

    }

    private void writeAttributes(ClassOrInterfaceDeclaration c, StringBuilder sb) {

        for(FieldDeclaration f : c.getFields()){

            writeField(f,sb);
            sb.append("\n");
        }

    }

    private void writeField(FieldDeclaration f, StringBuilder sb) {

        writeModifiers(f.getModifiers(),sb);
        sb.append(f.getVariables().get(0).getName());
        sb.append(" : ");
        sb.append(f.getVariables().get(0).getType().asString());

    }

    private void writeConstructors(ClassOrInterfaceDeclaration c, StringBuilder sb) {

        for(ConstructorDeclaration m: c.getConstructors()){
            writeConstructor(m,sb);
            sb.append("\n");
        }

    }

    private void writeConstructor(ConstructorDeclaration m, StringBuilder sb) {

        writeModifiers(m.getModifiers(),sb);
        sb.append(m.getName());
        sb.append("(");

        for(Parameter p: m.getParameters()){

            sb.append(p.getName());
            sb.append(" : ");
            sb.append(p.getType().asString());
            sb.append(", ");

        }
        if(!m.getParameters().isEmpty()){
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");

    }


    private void writeMethods(ClassOrInterfaceDeclaration c, StringBuilder sb) {

        for(MethodDeclaration m: c.getMethods()){
            writeMethod(m,sb);
            sb.append("\n");
        }

    }

    private void writeMethod(MethodDeclaration m, StringBuilder sb) {

        writeModifiers(m.getModifiers(),sb);
        sb.append(m.getName());
        sb.append("(");

        for(Parameter p: m.getParameters()){

            sb.append(p.getName());
            sb.append(" : ");
            sb.append(p.getType().asString());
            sb.append(", ");
        }
        if(!m.getParameters().isEmpty()){
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        sb.append(" : ");
        sb.append(m.getType().asString());

    }

    private void writeModifiers(NodeList<Modifier> modifiers, StringBuilder sb) {

        for(Modifier mod:modifiers){
            switch (mod.getKeyword()) {
                case STATIC -> sb.append("{static} ");
                case ABSTRACT -> sb.append("{abstract} ");
                case PUBLIC -> sb.append("+ ");
                case PRIVATE -> sb.append("- ");
                case PROTECTED -> sb.append("# ");

                //TODO:package visibility not shown yet
                case DEFAULT -> sb.append("~ ");
            }
        }

    }

    private void writeEnumerations(StringBuilder sb){

        for(EnumDeclaration e: this.umlTranslator.getEnumSet()){
            sb.append("enum ");
            sb.append(e.getName());
            sb.append("{\n");

            for(EnumConstantDeclaration c: e.getEntries()){

                sb.append(c.getName());
                sb.append("\n");

            }

            sb.append("}\n");
        }

    }

    private void writeInterfaces(StringBuilder sb){

        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getInterfaceSet()){
            writeInterface(c,sb);
        }

    }

    private void writeInterface(ClassOrInterfaceDeclaration c,StringBuilder sb) {

        sb.append("interface ");
        sb.append(c.getName());
        sb.append("{");
        sb.append("\n");

//        for(ClassOrInterfaceDeclaration c1:c.get)

        //attributes
        if (this.umlTranslator.getConfig().isShowAttributes()){
            writeAttributes(c, sb);
        }

        if(this.umlTranslator.getConfig().isShowMethods()) {
            //methods
            writeConstructors(c,sb);
            writeMethods(c, sb);
        }

        sb.append("}\n");

        for(ClassOrInterfaceType e: c.getExtendedTypes()){

            sb.append(c.getName());
            sb.append(" --|> ");
            sb.append(e.getName());
            sb.append("\n");
        }


    }
}
