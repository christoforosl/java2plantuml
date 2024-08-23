package source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import translate.ITranslator;

import java.io.File;

@Getter
@RequiredArgsConstructor
public class FileHandler {

    private final ITranslator translator;

    void handle(File f){

        try {
            translator.translateFile(new File(f.getAbsolutePath()));

        } catch (Exception e){
            translator.setError(true);
        }
    }

}
