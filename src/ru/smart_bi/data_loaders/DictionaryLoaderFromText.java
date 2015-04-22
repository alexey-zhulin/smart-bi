package ru.smart_bi.data_loaders;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.mockrunner.mock.jdbc.MockResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.smart_bi.data_loader_descriptors.DictionaryLoader;
import ru.smart_bi.data_loader_descriptors.IDictionaryLoader;
import ru.smart_bi.data_loader_descriptors.LoadStructure;

public class DictionaryLoaderFromText extends DictionaryLoader implements IDictionaryLoader {
	public String fileName;
	public String delimeter;
	public Charset encoding;
	public int startRow;
	public int endRow;
	
	final static Charset ENCODING_UTF_8 = StandardCharsets.UTF_8;
	
	@Override
	public ResultSet getData() throws Exception {
		// ѕредварительно отсортируем структуру загрузки (headers) в соответствии с позицией в источнике (positionInSource)
		HeadersSorter();
		MockResultSet mockResultSet = new MockResultSet("textResultSet");
		// «аполним структуру
		int i;
		for (i = 0; i < headers.size(); i++) {
			mockResultSet.addColumn(headers.get(i).linkedField.fieldHandler.fieldName);
		}
		// ќпределим кодировку
		if (encoding == null) {
			encoding = ENCODING_UTF_8;
		}
		// «аполним данные
		Path path = Paths.get(fileName);
		Scanner scanner = new Scanner (path, encoding.name());
		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			Scanner sentence = new Scanner(scanner.nextLine());
			lineNumber ++;
			// Ќачальна¤ строка обработки определена - проверим, нужно ли загружать данную строку
			if (startRow > 0) {
				if (lineNumber < startRow) continue;
			}
			// ≈сли конечна¤ строка определена - проверим, не пора ли выйти
			if (endRow > 0) {
				if (lineNumber > endRow) break;
			}
			sentence.useDelimiter(delimeter);
			int fieldNumberSource = 0;
			int headerNumber = 0;
			List<Object> rowData = new ArrayList<Object>();
			while (sentence.hasNext()) {
				String value = sentence.next();
				if (!NeedToAddValue(fieldNumberSource)) {
					fieldNumberSource ++;
					continue;
				}
				if (value.length() == 0) {
					rowData.add(null);
				}
				else {
					boolean converted = false;
					if (headers.get(headerNumber).field_type == LoadStructure.FieldTypes.Int) {
						rowData.add(Integer.parseInt(value));
						converted = true;
					}
					if (headers.get(headerNumber).field_type == LoadStructure.FieldTypes.Double) {
						rowData.add(Double.parseDouble(value));
						converted = true;
					}
					if (headers.get(headerNumber).field_type == LoadStructure.FieldTypes.Date) {
						DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
						rowData.add(format.parse(value));
						converted = true;
					}
					if (headers.get(headerNumber).field_type == LoadStructure.FieldTypes.Boolean) {
						rowData.add(Boolean.parseBoolean(sentence.next()));
						converted = true;
					}
					// ≈сли не определили типизацию - добавим как строку
					if (!converted) {
						rowData.add(value);
					}
				}
				fieldNumberSource ++;
				headerNumber ++;
			}
			mockResultSet.addRow(rowData);
			sentence.close();
		}
		scanner.close();
		return mockResultSet;
	}
	
	// ‘ункци¤ определ¤ет, нужно ли добавл¤ть значение данного пол¤ в выборку исход¤ из настройки headers
	boolean NeedToAddValue(int position) {
		int i;
		for (i = 0; i < headers.size(); i ++) {
			if (headers.get(i).positionInSource == position) return true;
		}
		return false;
	}

}
