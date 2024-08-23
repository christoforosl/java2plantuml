import source.DirectoryExplorer;
import source.FileHandler;
import translate.ClassDiagramConfig;
import translate.IUmlProvider;
import translate.UmlTranslator;
import translate.uml.provider.EnumProvider;
import translate.uml.provider.UMLProviderFactory;
import visitors.ClassVisitor;
import visitors.EnumVisitor;
import visitors.InterfaceVisitor;

import java.io.File;
import java.io.FileOutputStream;

public class MainDriver {
    public static void main(String[] args) throws Exception {

        System.out.println("args:" + args.length);

        if (args.length < 2) {
            System.out.println("Need to pass at least 2 arguments: PLANT|MERMAID and at least one path");
            System.exit(1);
        }

        final String mode = args[0].toUpperCase();
        final EnumProvider enumProvider = EnumProvider.fromString(mode);

        if (enumProvider == null) {
            System.out.println("First argument must be either PLANT or MERMAID");
            System.exit(1);
        }

        for(int i=1; i < args.length; i++){
            final String sourcePath = args[i];
            System.out.println("Processing:" + sourcePath);
            final UmlTranslator umlTranslator=new UmlTranslator();
            final IUmlProvider umlProvider = UMLProviderFactory.getUmlProvider(enumProvider);
            final ClassDiagramConfig config= ClassDiagramConfig.builder()
                    .umlProvider(umlProvider)
                    .visitorAdapter(new ClassVisitor(umlTranslator))
                    .visitorAdapter(new InterfaceVisitor(umlTranslator))
                    .visitorAdapter(new EnumVisitor(umlTranslator))
                    .showMethods(true)
                    .showAttributes(true)
                    .showColoredAccessSpecifiers(false)
                    .build();

            umlTranslator.setConfig(config);

            final FileHandler handler = new FileHandler(umlTranslator);
            final File resourceDir = new File(sourcePath);

            if(resourceDir.exists()) {
                new DirectoryExplorer(handler).explore(resourceDir);

                final File umlOutputFile = new File(sourcePath+ File.separator + "output." + umlProvider.getExtension());
                try (FileOutputStream fos = new FileOutputStream(umlOutputFile)) {
                    fos.write(umlTranslator.toUml().getBytes());
                }
                System.out.println( enumProvider.name() + " syntax generated to output file:" + umlOutputFile.getAbsolutePath());
            } else {
                System.out.println("Error: File/Folder doesn't exist:" + resourceDir);
                System.exit(1);
            }
        }
    }
}

