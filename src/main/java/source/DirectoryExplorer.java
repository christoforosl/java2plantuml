package source;

import lombok.extern.java.Log;

import java.io.File;
import java.io.FileFilter;
@Log
public class DirectoryExplorer {

    private FileHandler fileHandler;

    public DirectoryExplorer (FileHandler fileHandler){
        this.fileHandler=fileHandler;
    }

    FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return  !pathname.getAbsolutePath().endsWith(File.separator+"target") &&
                        !pathname.getAbsolutePath().endsWith("src" + File.separator + "test");
            }
            return pathname.toString().endsWith(".java");
        }
    };

    public void explore(File file) {
        if (file.isDirectory()) {
            log.fine(file.getAbsolutePath());
            File[] files = file.listFiles(fileFilter);
            if (files != null) {
                for (File f : files) {
                    explore(f);
                }
            }
        } else {
            fileHandler.handle(file);
        }
    }

}
