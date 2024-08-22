package visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import translate.ITranslator;

public class ClassVisitor extends VoidVisitorAdapter<Void> {

    private ITranslator translator;

    public ClassVisitor(ITranslator translator){
        this.translator=translator;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {

        if(!n.isInterface()) {
            this.translator.addClass(n);
//            System.out.println(n.getName());
        }
        super.visit(n, arg);

    }
}
