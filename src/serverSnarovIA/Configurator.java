package serverSnarovIA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

//класс для получения конфигураций из конфигурационного файла
public class Configurator {

	//константы
	private static final String FILE_READ_ERR_MSG = "Warning: Файл конфигурации не может быть прочитан."
			+ " Работа будет продолжена с параметрами по умолчанию";
	private static final String PATTERN_STRING = "\\w+\\s*=\\s*\\d+";	//паттерн поля конфигурации в файле
	//поля
	private final File file;											//имя файла конфигурации	
	private final HashMap<String, String> confFields = new HashMap<>(); //Отображение полей конфигурации. ключ - имя поля

	//конструкторы
	public Configurator(String fileName) {
		file = new File(fileName);

		char[] cbuf = new char[(int) file.length()];
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			fr.read(cbuf);
		} catch (IOException ex) {
			System.err.println(FILE_READ_ERR_MSG);
			return;
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException ex) {
			}
		}

		String fileContent = new String(cbuf);					//содержимое файла единой строкой
		String[] fileStrings = fileContent.split("\n");			//разбиение содержимого на отдельные строки

		for (String fileString : fileStrings) {
			if (Pattern.matches(PATTERN_STRING, fileString)) {
				fileString = fileString.replaceAll("\\s", "");
				int assignSymbIndex = fileString.indexOf("=");

				String key = fileString.substring(0, fileString.indexOf("="));
				key = key.toLowerCase();
				String value = fileString.substring(assignSymbIndex + 1);
				confFields.put(key, value);
			}
		}
	}
}
