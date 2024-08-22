package source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import translate.ITranslator;

import java.io.File;

@RequiredArgsConstructor
public class FileHandler {

    @Getter
    private final ITranslator translator;

    void handle(File f){

        System.out.println("File Found: " +f.getName());

        try {
            translator.translateFile(new File(f.getAbsolutePath()));

        } catch (Exception e){
            translator.setError(true);
        }
    }

}
