package data_loaders;

import java.io.File;
import java.sql.ResultSet;

import com.mockrunner.mock.jdbc.MockResultSet;

import data_loader_descriptors.IDictionaryLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryLoaderFromText implements IDictionaryLoader {
	public String fileName;
	public String delimeter;
	public int startRow;
	public int endRow;
	public List<String> headers;

	@Override
	public ResultSet getData() throws Exception {
		MockResultSet mockResultSet = new MockResultSet("textResultSet");
		// Заполним структуру
		for (String string: headers) {
			mockResultSet.addColumn(string);
		}
		// Заполним данные
		Scanner scanner = new Scanner (new File(fileName));
		scanner.useDelimiter(delimeter);
		while (scanner.hasNextLine()) {
			Scanner sentence = new Scanner(scanner.nextLine());
			int i = 1;
			List<Object> rowData = new ArrayList<Object>();
			while (sentence.hasNext()) {
				if (i == headers.size()) break;
				rowData.add(sentence.next());
				i ++;
			}
			mockResultSet.addRow(rowData);
			sentence.close();
		}
		scanner.close();
		return mockResultSet;
	}

}
