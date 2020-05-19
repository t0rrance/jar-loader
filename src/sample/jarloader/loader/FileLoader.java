package sample.jarloader.loader;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class FileLoader {

    private File file;

    public FileLoader(File file) throws NoSuchFileException {
        if(!file.exists() || !file.isFile()) {
            throw new NoSuchFileException(file.getAbsolutePath() + " no such file.");
        }
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}
