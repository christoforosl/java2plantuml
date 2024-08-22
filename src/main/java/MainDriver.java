import source.DirectoryExplorer;
import source.FileHandler;
import translate.ClassDiagramConfig;
import translate.UmlTranslator;
import visitors.ClassVisitor;
import visitors.EnumVisitor;
import visitors.InterfaceVisitor;

import java.io.File;
import java.io.FileOutputStream;

public class MainDriver {
    public static void main(String[] args) throws Exception {

        if(args.length<1){
            System.out.println("Need to pass path of source as argument");
            System.exit(1);
        }

        for(final String sourcePath : args){
            System.out.println("Processing:" + sourcePath);
            UmlTranslator umlTranslator=new UmlTranslator();
            ClassDiagramConfig config= new ClassDiagramConfig.Builder()
                    .withVisitor(new ClassVisitor(umlTranslator))
                    .withVisitor(new InterfaceVisitor(umlTranslator))
                    .withVisitor(new EnumVisitor(umlTranslator))
                    .setShowMethods(true)
                    .setShowAttributes(true)
                    .setShowColoredAccessSpecifiers(false)
                    .build();
            umlTranslator.setConfig(config);

            FileHandler handler = new FileHandler(umlTranslator);
            File resourceDir = new File(sourcePath);

            if(resourceDir.exists()) {
                new DirectoryExplorer(handler).explore(resourceDir);

                File umlOutputFile = new File(sourcePath+ File.separator + "output.puml");
                FileOutputStream fos = new FileOutputStream(umlOutputFile);
                fos.write(umlTranslator.toUml().getBytes());
                fos.close();
                System.out.println("PlantUml syntax generated to output file:" + umlOutputFile.getAbsolutePath());
            } else {
                System.out.println("File/Folder doesn't exist!");
                System.exit(1);
            }
        }
    }
}

