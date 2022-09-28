package Helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.ReportsController;
import play.libs.Json;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    public final static String[] statusListStudent = {"Active", "Suspended", "Decease", "Dropout", "Graduated"};

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<JsonNode> findReportable(String scannedPackage){
        List<Class<?>> classList = find(scannedPackage);
        List<JsonNode> objects = new ArrayList<>();
        String name,className;
        for (Class<?> clazz : classList){
            if( clazz.isAnnotationPresent(EntityProperty.class) && clazz.getAnnotation(EntityProperty.class).hasReport() ) {
                EntityProperty clazzAnnotation = clazz.getAnnotation(EntityProperty.class);
                name = clazzAnnotation.name();
                className = clazz.getSimpleName();
                ObjectNode node = Json.newObject();
                node.put("key",className);
                node.put("href",ReportsController.getReportRef(clazz.getName()));
                node.put("value", name.equals("") ? className : name );
                objects.add(node);
            }
        }

        return objects;
    }

    public static List<Class<?>> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<>();

        File[] files = scannedDir.listFiles();
        if( files != null ) {
            for (File file : files) {
                classes.addAll(find(file, scannedPackage));
            }
        }
        return classes;
    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if ( file.isDirectory()) {
            File[] files = file.listFiles();

            if( files != null ) {
                for (File child : files) {
                    classes.addAll(find(child, resource));
                }
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }

}
