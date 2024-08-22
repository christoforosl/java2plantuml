package visitors;

import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import translate.ITranslator;

public class EnumVisitor extends VoidVisitorAdapter<Void> {

    private ITranslator translator;

    public EnumVisitor(ITranslator translator){
        this.translator=translator;
    }

    @Override
    public void visit(EnumDeclaration n, Void arg) {

        this.translator.addEnum(n);
        super.visit(n, arg);

    }
}
