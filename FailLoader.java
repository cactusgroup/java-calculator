import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class FailLoader extends ClassLoader {
    public FailLoader(ClassLoader parent) {
        super(parent);
    }
    public Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
        if (name.equals("Instructions")) {
            Class<?> clazz = null;
            try {
                final InputStream inputStream = 
                    new FileInputStream(new File(name + ".class"));
                byte [] data = new byte[inputStream.available()];
                inputStream.read(data);
                clazz = defineClass(name, data, 0, data.length);
                resolveClass(clazz);
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                return clazz;
            }
        }
        return super.loadClass(name, resolve);
    }
}
