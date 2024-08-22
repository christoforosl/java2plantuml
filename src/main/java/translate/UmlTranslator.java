package translate;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.Data;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
@Data
public class UmlTranslator implements ITranslator {
    private final Set<ClassOrInterfaceDeclaration> classSet;
    private Set<ClassOrInterfaceDeclaration> interfaceSet;
    private Set<EnumDeclaration> enumSet;
    private Boolean error=false;

    private ClassDiagramConfig config=new ClassDiagramConfig.DefaultDirector().construct();

    public UmlTranslator(){
        classSet = new HashSet<>();
        interfaceSet = new HashSet<>();
        enumSet = new HashSet<>();
    }

    @Override
    public void addClass(ClassOrInterfaceDeclaration c) {
        if(!c.isInterface())
            classSet.add(c);
    }

    @Override
    public void addEnum(EnumDeclaration e) {
        enumSet.add(e);
    }

    @Override
    public void addInterface(ClassOrInterfaceDeclaration i) {
        if(i.isInterface())
            interfaceSet.add(i);
    }

    @Override
    public void addField(FieldDeclaration f) {

    }

    @Override
    public void addMethod(MethodDeclaration d) {

    }

    @Override
    public void setError(Boolean b) {
        this.error=b;
    }

    @Override
    public void translateFile(File f)  {

        try {
            ParserConfiguration parserConfiguration = new ParserConfiguration();
            // Set the language level to Java 20
            parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_20);

            ParseResult<CompilationUnit> cu = new JavaParser(parserConfiguration).parse(new File(f.getAbsolutePath()));
            for(VoidVisitorAdapter<Void> visitor:config.getVisitorAdapters()){
                if( cu.isSuccessful() && cu.getResult().isPresent()) {
                    cu.getResult().get().accept(visitor, null);
                } else {
                    throw new IllegalStateException("Un Successful parsing" + cu.getProblems().toString());
                }
            }

        } catch (Exception e) {
            setError(true);
            e.printStackTrace();
        }
    }

    public String toUml(){

        return ((IUmlProvider)config.getUmlProvider()).getUML(this);

    }

}
