import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите путь к файлу: ");
            String path = scanner.nextLine();
            System.out.print("Введите новое имя для класса: ");
            String newClassName = scanner.nextLine();

            String result = Files.readString(Path.of(path));
            File file = new File(path);

            // удаление коммментариев, как однострочных так и многострочных
            result = result.replaceAll("(/\\*([\\S\\s]+?)\\*/)|(//.*)", "");

            // сокращение пробельных символов
            result = result.replaceAll("\\s+", " ");

            // замена имени класса и конструктора
            String oldClassName = file.getName().replace(".java", "");
            result = result.replaceAll("\\b" + oldClassName + "\\b", newClassName);

            // замена идентификаторов в зависимости от их количества
            // регулярное выражение для поиска идентификаторов переменных
            String variableRegex = "\\b(?:int|double|String|boolean|float|char|long|byte|short|void|class)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\b";
            Pattern pattern = Pattern.compile(variableRegex);
            Matcher matcher = pattern.matcher(result);

            // записываем найденные идентификаторы в список уникальных значений
            Set<String> identifiers = new HashSet<>();
            while (matcher.find()) {
                String identifier = matcher.group(1);
                if (!identifier.equals(newClassName) && !identifier.equals("main")) identifiers.add(identifier);
            }

            Map<String, String> identifierMap = generateUniqueNames(identifiers);

            // заменяем идентификаторы на новые имена
            for (Map.Entry<String, String> entry : identifierMap.entrySet()) {
                result = result.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }

            Files.writeString(Path.of(newClassName + ".java"), result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // метод для генерации уникальных имен
    private static Map<String, String> generateUniqueNames(Set<String> identifiers) {
        Map<String, String> map = new HashMap<>();
        Set<String> usedNames = new HashSet<>();
        Random random = new Random();

        // генерируем для каждого идентификатора своё уникальное имя
        for (String identifier : identifiers) {
            String newName;

            do {
                // если идентификаторов меньше 26, то генерируем одну букву, если больше, то две
                if (identifiers.size() <= 26){
                    newName = String.valueOf((char) ('a' + random.nextInt(26)));
                } else{
                    char letter1 = (char) ('a' + random.nextInt(26));
                    char letter2 = (char) ('a' + random.nextInt(26));
                    newName = "" + letter1 + letter2;
                }
            } while (usedNames.contains(newName)); // генерируем до тех пор, пока значение не станет уникальным

            usedNames.add(newName);
            map.put(identifier, newName);
        }

        return map;
    }
}

