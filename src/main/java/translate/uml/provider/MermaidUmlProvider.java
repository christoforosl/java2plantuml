package translate.uml.provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import lombok.NonNull;
import translate.ITranslator;
import translate.IUmlProvider;

import java.util.HashSet;

public class MermaidUmlProvider implements IUmlProvider {
    private ITranslator umlTranslator;


    @Override
    public String getUML(ITranslator umlTranslator) {

        this.umlTranslator = umlTranslator;

        StringBuilder sb = new StringBuilder();

        sb.append("classDiagram");
        sb.append("\n");
        //this is for removing shapes in attributes/methods visibility

        writeClasses(sb);
        writeInterfaces(sb);
        writeEnumerations(sb);
        writeAssociations(sb);

        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String getExtension() {
        return "mmd";
    }

    private void writeAssociations(StringBuilder sb) {

        for (ClassOrInterfaceDeclaration c : umlTranslator.getInterfaceSet()) {
            if (!c.getExtendedTypes().isEmpty()) sb.append("%% Interface ").append(c.getName()).append(" Extensions\n");
            for (ClassOrInterfaceType e : c.getExtendedTypes()) {
                sb.append(c.getName());
                sb.append(" --|> ");
                sb.append(e.getName());
                sb.append("\n");
            }
            if (!c.getImplementedTypes().isEmpty())
                sb.append("%% Interface ").append(c.getName()).append(" Implemented types\n");
            //implemented types
            for (ClassOrInterfaceType e : c.getImplementedTypes()) {
                sb.append(c.getName());
                sb.append(" ..|> ");
                sb.append(e.getName());
                sb.append("\n");
            }
        }

        for (ClassOrInterfaceDeclaration c : umlTranslator.getClassSet()) {
            if (!c.getExtendedTypes().isEmpty()) sb.append("%% Class ").append(c.getName()).append(" Extended types\n");
            for (ClassOrInterfaceType e : c.getExtendedTypes()) {
                sb.append(c.getName());
                sb.append(" --|> ");
                sb.append(e.getName());
                sb.append("\n");
            }

            if (!c.getImplementedTypes().isEmpty())
                sb.append("%% Class ").append(c.getName()).append(" Implemented types\n");
            //implemented types
            for (ClassOrInterfaceType e : c.getImplementedTypes()) {
                sb.append(c.getName());
                sb.append(" ..|> ");
                sb.append(e.getName());
                sb.append("\n");
            }
        }

        HashSet<String> temp = new HashSet<>();
        for (ClassOrInterfaceDeclaration c : this.umlTranslator.getClassSet()) {
            temp.add(c.getNameAsString());
        }
        for (ClassOrInterfaceDeclaration c : this.umlTranslator.getInterfaceSet()) {
            temp.add(c.getNameAsString());
        }
        for (EnumDeclaration e : this.umlTranslator.getEnumSet()) {
            temp.add(e.getNameAsString());
        }

        for (ClassOrInterfaceDeclaration c : umlTranslator.getClassSet()) {
            for (FieldDeclaration field : c.getFields()) {
                Type fieldType = field.getCommonType();
                if (fieldType.isClassOrInterfaceType()) {

                    ClassOrInterfaceType classType = fieldType.asClassOrInterfaceType();
                    //System.out.println("isClassOrInterfaceType : " + classType);
                    if (classType.getTypeArguments().isPresent()) { // is generic?
                        classType.getTypeArguments().ifPresent(typeArgs -> {
                            final String genericVariableType = extractAfterLastDot(typeArgs.get(0).toString());
                            //System.out.println("Generic Type of: " + genericVariableType);

                            if (temp.contains(genericVariableType)) {
                                sb.append(c.getName().asString());
                                sb.append(" \"1\" --* \"*\" ");
                                sb.append(typeArgs.get(0));
                                sb.append(" : ");
                                sb.append(field.getVariables().get(0));
                                sb.append("\n");
                            }
                        });
                    } else { // not generic
                        if (temp.contains(classType.asString())) {
                            // composition relationships from c ->  field.getVariables().get(0)
                            sb.append(c.getName().asString());
                            if (isEnum(classType.asString())) {
                                sb.append(" --> ");
                            } else {
                                sb.append(" \"1\" --* \"1\" ");
                            }
                            sb.append(classType.asString());
                            sb.append("\n");
                        }
                    }
                } else {
                    final String variableType = field.getVariables().get(0).getType().asString();
                    if (temp.contains(variableType)) {
                        // composition relationships from c ->  field.getVariables().get(0)
                        sb.append(c.getName().asString());
                        if (isEnum(variableType)) {
                            sb.append(" --> ");
                        } else {
                            sb.append(" *-- ");
                        }
                        sb.append(variableType);
                        sb.append("\n");
                    }
                }
            }
        }
        for (ClassOrInterfaceDeclaration c : umlTranslator.getClassSet()) {
            for(MethodDeclaration m: c.getMethods()){
                final String returnType = extractAfterLastDot(m.getType().asString());
                if(! returnType.equals(c.getName())) {
                    if (temp.contains(returnType)) {
                        sb.append(c.getName().asString());
                        sb.append(" ..> ");
                        sb.append(returnType);
                        sb.append(" : ").append(m.getName());
                        sb.append("\n");
                    }
                }
            }
        }
    }

    private boolean isEnum(@NonNull String enumname) {
        for (EnumDeclaration e:this.umlTranslator.getEnumSet()){
            if(enumname.equals(e.getNameAsString())){
                return true;
            }
        }
        return false;
    }

    private void writeClasses(StringBuilder sb){
        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getClassSet()){
            writeClass(c,sb);
        }
    }

    private void writeClass(ClassOrInterfaceDeclaration c,StringBuilder sb) {

        sb.append("\tclass ");
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

        sb.append("\t}\n\n");


    }

    private void writeAttributes(ClassOrInterfaceDeclaration c, StringBuilder sb) {

        for(FieldDeclaration f : c.getFields()){
            writeField(f,sb);
            sb.append("\n");
        }

    }

    private void writeField(FieldDeclaration f, StringBuilder sb) {
        sb.append("\t\t");
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

        sb.append("(\t\t");
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

        sb.append("\t\t");
        writeModifiers(m.getModifiers(),sb);
        sb.append(m.getName());
        sb.append("(");

        for(Parameter p: m.getParameters()){
            sb.append(p.getName());
            sb.append(" : ");
            sb.append(extractAfterLastDot(p.getType().asString()));
            sb.append(", ");
        }
        if(!m.getParameters().isEmpty()){
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        sb.append(" : ");
        sb.append(extractAfterLastDot(m.getType().asString()));

    }

    private void writeModifiers(NodeList<Modifier> modifiers, StringBuilder sb) {

        for(Modifier mod:modifiers){
            switch (mod.getKeyword()) {
                case PUBLIC -> sb.append("+");
                case PRIVATE -> sb.append("-");
                case PROTECTED -> sb.append("#");
            }
        }

    }

    private void writeEnumerations(StringBuilder sb){

        for(EnumDeclaration e: this.umlTranslator.getEnumSet()){
            sb.append("\tclass ");
            sb.append(e.getName());
            sb.append("{\n\t\t<<enumeration>>\n");

            for(EnumConstantDeclaration c: e.getEntries()){
                sb.append("\t\t");
                sb.append(c.getName());
                sb.append("\n");
            }

            sb.append("\t}\n\n");
        }

    }

    private void writeInterfaces(StringBuilder sb){

        for(ClassOrInterfaceDeclaration c: this.umlTranslator.getInterfaceSet()){
            writeInterface(c,sb);
        }
    }

    private void writeInterface(ClassOrInterfaceDeclaration c,StringBuilder sb) {

        sb.append("\tclass ");
        sb.append(c.getName());
        sb.append("{");
        sb.append("\n");

        sb.append("\t\t<<interface>>");
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

        sb.append("\t}\n\n");
    }
    public static String extractAfterLastDot(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int lastDotIndex = input.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == input.length() - 1) {
            return input;
        }

        return input.substring(lastDotIndex + 1);
    }
}
