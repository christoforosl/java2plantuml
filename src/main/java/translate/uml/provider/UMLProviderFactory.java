package translate.uml.provider;

import lombok.NonNull;
import translate.IUmlProvider;

public class UMLProviderFactory {

    public static IUmlProvider getUmlProvider(@NonNull EnumProvider provider) {

        if (provider == EnumProvider.MERMAID) return new MermaidUmlProvider();
        if (provider == EnumProvider.PLANT ) return new PlantUmlProvider();

        throw new IllegalStateException("Uknown provider");
    }
}
