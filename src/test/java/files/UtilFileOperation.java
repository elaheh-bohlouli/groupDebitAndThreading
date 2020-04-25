package files;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UtilFileOperation {
    private UtilFileOperation() {
        throw new UnsupportedOperationException();
    }

    public static void createFile(String filePath) {
        Path filePathObj = Paths.get(filePath);
        boolean fileNotExists = Files.notExists(filePathObj);
        if (fileNotExists) {
            try {
                Path createFile = Files.createFile(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File Not Present! Please Check!");
        }
    }


    public static void writeToFile(List list, Path path) {
        try {
            Files.write(path, list, Charset.forName("UTF-8"), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> readFromFile(Path path) {
        try {
            List<String> list = new ArrayList<>();
            Files.lines(path).forEach(x -> list.add(x));
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static void AppendToFile(String filePath, String contentToAppend) {
        Path filePathObj = Paths.get(filePath);
        boolean fileExists = Files.exists(filePathObj);
        if (fileExists) {
            try {
                Files.write(filePathObj, contentToAppend.getBytes(), StandardOpenOption.APPEND);
                System.out.println("! Data Successfully Appended !");
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem Occured While Writing To The File= " + ioExceptionObj.getMessage());
            }
        } else {
            System.out.println("File Not Present! Please Check!");
        }

    }

    public static void replaceInFile(String filePath, String depositNumber, String newLine) {
        try {
            Path path = Paths.get(filePath);
            Stream<String> lines = Files.lines(path);
            List<String> replaced = lines.map(line -> depositNumber.contains(depositNumber) ? newLine : line)
                    .collect(Collectors.toList());
            Files.write(path, replaced);
            lines.close();
            System.out.println("Find and Replace done!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String[] splitLine(String line) {
        String[] str = line.split("   ");
        return str;
    }


    public static String join(List<String> lists) {

        String tab = "   ";

        String res = lists.stream()
                .map(Object::toString)
                .collect(Collectors.joining(tab));
        return res;
    }
}
