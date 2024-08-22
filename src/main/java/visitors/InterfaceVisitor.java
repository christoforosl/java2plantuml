package visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import translate.ITranslator;

public class InterfaceVisitor extends VoidVisitorAdapter<Void> {

    private ITranslator translator;

    public InterfaceVisitor(ITranslator translator){
        this.translator=translator;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {

        if(n.isInterface()) {
            this.translator.addInterface(n);
        }
        super.visit(n, arg);

    }
}
