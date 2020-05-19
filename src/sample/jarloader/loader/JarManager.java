package sample.jarloader.loader;
import sample.jarloader.log.Log;
import sample.jarloader.util.Util;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class JarManager {

    private ZipFile jarFile;
    private ClassLoader classLoader;
    private List<Class<?>> jarClass = new ArrayList<>();
    private List<Method> listMethods = new ArrayList<>();

    public JarManager(FileLoader fileLoader) {
        try {
            jarFile = new ZipFile(fileLoader.getFile());
            classLoader = new URLClassLoader(new URL[]{fileLoader.getFile().toURI().toURL()});
        } catch (IOException e) {
            Log.syserr("Error reading from " + fileLoader.getFile().getAbsolutePath());
        }
    }

    public void load() {
        Enumeration<? extends ZipEntry> entries = jarFile.entries();
        ZipEntry zipEntry;
        String entryName;
        Class cls;

        while (entries.hasMoreElements()) {
            zipEntry = entries.nextElement();
            entryName = zipEntry.getName();

            if (Util.hasSufix(entryName, "class")) {
                cls = getClass(entryName);
                if (!cls.getSimpleName().equals("")) {
                    jarClass.add(cls);
                }
            }
        }
    }

    public List<Method> getClassMethods(String className) {
        if (Objects.equals(className, "")) {
            System.out.println("Not entered class name.");
            return null;
        }

        Class cls = hasClass(className);

        if (cls == null) {
            System.out.println("Class not found [" + className + "].");
            return null;
        }

        if (cls.getDeclaredMethods().length > 0) {
            for (Method method : cls.getDeclaredMethods()) {
                System.out.print("\n- " + method.toString());
                listMethods.add(method);
            }
            return listMethods;
        } else {
            System.out.print("Not find methods.\n");
            return null;
        }
    }

    public  Class getClass(String entryName) {
        Class cls = null;

        try {
            cls = classLoader.loadClass(getLoadClassName(entryName));
        } catch (ClassNotFoundException e) {
            Log.syserr("Not found class [" + getLoadClassName(entryName) + "]");
        }
        return cls;
    }

    private String getLoadClassName(String entryName) {
        return entryName.replace('/', '.').replace(".class", "");
    }

    private Class hasClass(String findClassName) {
        for (Class cls : jarClass) {
            if (cls.getSimpleName().contains(findClassName)) {
                return cls;
            }
        }
        return null;
    }

    public String callMethod(Class cls, String methodName, double input1, double input2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (cls.getDeclaredMethods().length > 0) {
            for (Method method : cls.getDeclaredMethods()) {

                System.out.print("\n- " + method.getName());
                if(method.getName().equals(methodName)) {
                    Constructor<?> constructor = cls.getConstructor();
                    Object instance = constructor.newInstance();
                    method.setAccessible(true);
                    Object value = method.invoke(instance, input1, input2);
                    return value.toString();
                }
            }
        } else {
            return "Not found method";
        }
        return "Method is not exist";
    }

    public List<Class<?>> getJarClass() {
        return jarClass;
    }

}
