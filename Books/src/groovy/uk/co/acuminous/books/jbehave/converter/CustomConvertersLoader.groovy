package uk.co.acuminous.books.jbehave.converter

import org.jbehave.core.steps.ParameterConverters.ParameterConverter
import org.jbehave.core.steps.ParameterConverters
import java.lang.reflect.Modifier

class CustomConvertersLoader {

    public static ParameterConverters getConverters() {
        ParameterConverters parameterConverters = new ParameterConverters()
        String packageName = CustomConvertersLoader.class.getPackage().getName()
        getClasses(packageName).each { Class clazz ->
            if (!Modifier.isAbstract(clazz.modifiers) && ParameterConverter.isAssignableFrom(clazz)) {
                parameterConverters.addConverters(clazz.newInstance())
            }
        }
        return parameterConverters    
    }

    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        ClassLoader classLoader = Thread.currentThread().contextClassLoader
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6)
                classes.add(Class.forName(className, true, classLoader));
            }
        }
        return classes;
    }    

}