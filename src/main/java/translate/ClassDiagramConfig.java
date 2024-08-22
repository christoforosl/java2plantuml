package translate;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import java.util.List;

@Data
@Builder
public class ClassDiagramConfig {

    @Singular
    private List<VoidVisitorAdapter<Void>> visitorAdapters;
    private boolean showMethods;
    private boolean showAttributes;
    private boolean showColoredAccessSpecifiers;
    private IUmlProvider umlProvider;

    //default director for builder
    public static class DefaultDirector{
        public ClassDiagramConfig construct(){
            return ClassDiagramConfig.builder().build();
        }

    }

}
