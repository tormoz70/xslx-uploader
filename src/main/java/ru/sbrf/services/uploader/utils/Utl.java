package ru.sbrf.services.uploader.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@UtilityClass
public class Utl {

    /**
     * Вытаскивает имя файла из полного пути
     * @param filePath
     * @return fileName
     */
    public static String fileName(String filePath) {
        if (!Strings.isEmpty(filePath)) {
            int p = filePath.lastIndexOf(File.separator);
            if (p >= 0)
                return filePath.substring(p + 1);
            return filePath;
        }
        return filePath;
    }

    /**
     * Вытаскивает расширение файла из полного пути
     * @param fileName
     * @return fileExt
     */
    public static String fileNameExt(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = fileName.lastIndexOf(File.separator);

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String fileNameWithoutExt(String fileName) {
        if (Strings.isEmpty(fileName))
            return fileName;
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Приводит LongToInt, если это возможно
     * @param l long
     * @return int
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Читает из потока заданное кол-во байтов и возвращает их в виде массива
     * @param in stream
     * @param length length og stream
     * @return bytes
     */
    public static byte[] readStream(InputStream in, int length) {
        try {
            if (length > (Integer.MAX_VALUE - 5))
                throw new IllegalArgumentException("Parameter \"length\" too big!");
            byte[] buff = new byte[Utl.safeLongToInt(length)];
            in.read(buff);
            return buff;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    /**
     * Читает из потока заданное кол-во байтов и возвращает их в виде массива
     * @param in stream
     * @param encoding encoding
     * @param addLineSeparator adds separator to end line
     * @return string
     */
    public static String readStream(InputStream in, String encoding, boolean addLineSeparator) {
        try {
            InputStreamReader is = new InputStreamReader(in, encoding);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                if (addLineSeparator)
                    sb.append(System.lineSeparator());
                read = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static String readStream(InputStream in, String encoding) {
        return readStream(in, encoding, true);
    }

    public static String readStream(InputStream in) {
        return readStream(in, "UTF-8");
    }

    public static String dictionaryInfo(Dictionary dict, String beanName, String tab) {
        if (tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        out.append(String.format(tab + "%s {\n", beanName));
        for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object val = dict.get(key);
            out.append(String.format(attrFmt, key, val));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static String propertyInfo(Hashtable hashtable, String beanName, String tab) {
        return dictionaryInfo(hashtable, beanName, tab);
    }


    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }

    public static String preparePath(String path, char pathSeparator) {
        if (Strings.isEmpty(path))
            return null;
        String rslt = path;
        if (pathSeparator == (char) 0)
            pathSeparator = File.separatorChar;
        rslt = rslt.replace('\\', pathSeparator);
        rslt = rslt.replace('/', pathSeparator);
        return rslt;
    }

    public static String normalizePath(String path, char pathSeparator) {
        if (Strings.isEmpty(path))
            return null;
        if (pathSeparator == (char) 0)
            pathSeparator = File.separatorChar;
        String rslt = preparePath(path, pathSeparator);
        rslt = rslt.endsWith("" + pathSeparator) ? rslt : rslt + pathSeparator;
        return rslt;
    }

    public static String normalizePath(String path) {
        return normalizePath(path, (char) 0);
    }

    public static String generateTmpFileName(final String tmpPath, final String fileName) {
        String randomUUIDString = UUID.randomUUID().toString().replaceAll("-", "");
        return String.format("%s%s-$(%s).%s", Utl.normalizePath(tmpPath), Utl.fileNameWithoutExt(fileName), randomUUIDString, Utl.fileNameExt(fileName));
    }

    public static Boolean confIsEmpty(Dictionary conf) {
        if (conf == null || conf.isEmpty())
            return true;
        int count = 0;
        String componentKey = "component";
        for (Enumeration e = conf.keys(); e.hasMoreElements(); ) {
            e.nextElement();
            count++;
        }
        return (count == 1 && conf.get(componentKey) != null);
    }


    public static String md5(String fileName) {
        String md5 = null;
        try {
            try (FileInputStream fis = new FileInputStream(new File(fileName))) {
                md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
        return md5;
    }

    public static int storeInputStream(InputStream inputStream, Path path) {
        try {
            int len = 0;
            Files.createDirectories(path.getParent());
            try (OutputStream out = new FileOutputStream(new File(path.toString()))) {
                int read = 0;
                final byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                    len += read;
                }
            }
            return len;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Path storeBlob(byte[] blob, Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = new FileOutputStream(new File(path.toString()))) {
                out.write(blob);
            }
            return path;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Path storeBlob(byte[] blob, String path) {
        return storeBlob(blob, Paths.get(path));
    }

    public static int storeInputStream(InputStream inputStream, String path) {
        return storeInputStream(inputStream, Paths.get(path));
    }


    public static void storeString(String text, String path, String encoding) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(path), true, encoding)) {
                out.print(text);
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void storeString(String text, String path) {
        storeString(text, path, "utf-8");
    }

    private static URL _findResource(String filePath) {
        try {
            return new ClassPathResource(filePath).getURL();
        } catch(IOException e) {
            return null;
        }
    }

    static InputStream _openFile(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            return new FileInputStream(file);
        } catch (IOException e) {
            try {
                URL url = _findResource(filePath);
                if (url != null) {
                    return url.openStream();
                } else {
                    InputStream result = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                    if(result != null)
                        return result;
                    else
                        throw new IOException();
                }
            } catch(IOException e1) {
                throw new FileNotFoundException(String.format("File \"%s\" not found!", filePath));
            }
        }
    }

    public static InputStream openFile(String filePath) throws IOException {
        return _openFile(filePath);
    }

    public static InputStream openFile(Path filePath) throws IOException {
        return openFile(filePath.toString());
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static String readFile(String filePath, String encoding) {
        try {
            String rslt;
            try (InputStream is = _openFile(filePath)) {
                rslt = Utl.readStream(is, encoding);
            }
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static String readFile(String filePath) {
        return readFile(filePath, "utf-8");
    }

    public static List<String> readFileAsList(String filePath, String encoding) {
        try {
            List<String> rslt = new ArrayList<>();
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Charset charset = Charset.forName(encoding);
                try (BufferedReader br = Files.newBufferedReader(path, charset)) {
                    String line = br.readLine();
                    while (line != null) {
                        rslt.add(line);
                        line = br.readLine();
                    }
                }
            }
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static List<String> readFileAsList(String filePath) {
        return readFileAsList(filePath, StandardCharsets.UTF_8.displayName());
    }

    public static void storeListToFile(List<String> list, String filePath, String encoding) {
        try {
            Path path = Paths.get(filePath);
            Charset charset = Charset.forName(encoding);
            try (BufferedWriter bw = Files.newBufferedWriter(path, charset)) {
                for (String line : list)
                    bw.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void storeListToFile(List<String> list, String filePath) {
        storeListToFile(list, filePath, StandardCharsets.UTF_8.displayName());
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        String hex = uuid.toString().replace("-", "").toLowerCase();
        return hex;
    }

    public static Object getFieldValue(java.lang.reflect.Field fld, Object bean) {
        try {
            return fld.get(bean);
        } catch (IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void setFieldValue(java.lang.reflect.Field fld, Object bean, Object value) {
        try {
            fld.set(bean, value);
        } catch (IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Document loadXmlDocument(InputStream inputStream) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Document loadXmlDocument(String fileName) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            try(InputStream inputStream = openFile(fileName)) {
                return builder.parse(inputStream);
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void writeInputToOutput(InputStream input, OutputStream output, boolean closeOutput) {
        try(BufferedInputStream buf = new BufferedInputStream(input)) {
            int readBytes = 0;
            while ((readBytes = buf.read()) != -1)
                output.write(readBytes);
            if(closeOutput) {
                output.flush();
                output.close();
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void writeInputToOutput(InputStream input, OutputStream output) {
        writeInputToOutput(input, output, true);
    }

    public static void writeFileToOutput(File file, OutputStream output) {
        try(InputStream io = new FileInputStream(file)) {
            writeInputToOutput(io, output);
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void deleteFile(Path filePath, Boolean silent) {
        try {
            Files.delete(filePath);
        } catch (Exception e) {
            if (!silent)
                throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void deleteFile(String filePath, Boolean silent) {
        deleteFile(Paths.get(filePath), silent);
    }

    public static <T extends Enum<T>> T enumValueOf(
            Class<T> enumeration, String name, T defaultVal) {

        for (T enumValue : enumeration.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }

        if (defaultVal != null)
            return defaultVal;
        throw new IllegalArgumentException(String.format(
                "There is no value with name '%s' in Enum %s",
                name, enumeration.getName()
        ));
    }

    public static Properties loadProperties(InputStream inputStream) {
        try {
            Properties prop = new Properties();
            prop.load(inputStream);
            return prop;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Properties loadProperties(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            return loadProperties(input);
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static long fileSize(String filePath) {
        try {
            long rslt = 0;
            Path p = Paths.get(filePath);
            if (Files.exists(p))
                rslt = p.toFile().length();
            else
                throw new FileNotFoundException(String.format("Файл %s не наден!", filePath));
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static int generateIndxByProb(double[] prob) {
        double indxD = Math.random();
        int indxI = 0;
        double curLow = 0;
        for (int i = 0; i < prob.length; i++) {
            curLow += prob[i];
            if (indxD >= curLow - prob[i] && indxD < curLow) {
                indxI = i;
                break;
            }
        }
        return indxI;
    }

    public static RuntimeException wrapErrorAsRuntimeException(String msg, Exception e) {
        if (e != null && e instanceof RuntimeException)
            return (RuntimeException) e;
        else {
            if (Strings.isEmpty(msg))
                return new RuntimeException(e);
            else if (e == null)
                return new RuntimeException(msg);
            else
                return new RuntimeException(msg, e);
        }
    }

    public static RuntimeException wrapErrorAsRuntimeException(Exception e) {
        return wrapErrorAsRuntimeException(null, e);
    }

    public static RuntimeException wrapErrorAsRuntimeException(String msg) {
        return wrapErrorAsRuntimeException(msg, null);
    }

    public static String append(String line, String str, String delimiter) {
        if (StringUtils.isEmpty(line))
            line = ((str == null) ? "" : str);
        else
            line += delimiter + ((str == null) ? "" : str);
        return line;
    }

    public static void append(StringBuilder stringBuilder, String str, String delimiter) {
        if (stringBuilder.length() == 0)
            stringBuilder.append((str == null) ? "" : str);
        else
            stringBuilder.append(delimiter + ((str == null) ? "" : str));
    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;

    }

    public static byte[] toPrimitives(Byte[] oBytes) {

        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

//    public static String[] split(String str, String ... delimiters) {
//        if (StringUtils.isNoneEmpty(str)) {
//            if ((delimiters != null) && (delimiters.length > 0)) {
//                String line = str;
//                String dlmtr = null;
//                if (delimiters.length > 1) {
//                    final String csDlmtrPG = "#inner_pg_delimeter_str#";
//                    for (String delimeter : delimiters)
//                        line = line.replace(delimeter, csDlmtrPG);
//                    dlmtr = csDlmtrPG;
//                } else
//                    dlmtr = delimiters[0];
//                List<String> lst = new ArrayList<String>();
//                int item_bgn = 0;
//                while (item_bgn <= line.length()) {
//                    String line2Add = "";
//                    int dlmtr_pos = line.indexOf(dlmtr, item_bgn);
//                    if (dlmtr_pos == -1)
//                        dlmtr_pos = line.length();
//                    line2Add = line.substring(item_bgn, dlmtr_pos);
//                    lst.add(line2Add);
//                    item_bgn += line2Add.length() + dlmtr.length();
//                }
//                return lst.toArray(new String[lst.size()]);
//            } else
//                return new String[] { str };
//        } else
//            return new String[] {};
//    }

//    public static String[] split(String str, char ... delimiters) {
//        String[] d = new String[delimiters.length];
//        for(int i=0; i<delimiters.length; i++){
//            d[i] = ""+delimiters[i];
//        }
//        return split(str, d);
//    }

    public static <T> String combineArray(T[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (T item : array)
            sb.append(sb.length() == 0 ? item.toString() : delimiter+item.toString());
        return sb.toString();
    }

    public static String combineArray(int[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (int item : array)
            sb.append(sb.length() == 0 ? item : delimiter+item);
        return sb.toString();
    }
    public static String combineArray(byte[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (byte item : array)
            sb.append(sb.length() == 0 ? item : delimiter+item);
        return sb.toString();
    }

}
