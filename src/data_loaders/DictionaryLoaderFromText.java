package data_loaders;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.mockrunner.mock.jdbc.MockResultSet;

import data_loader_descriptors.IDictionaryLoader;
import data_loader_descriptors.LoadStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryLoaderFromText implements IDictionaryLoader {
	public String fileName;
	public String delimeter;
	public Charset encoding;
	public int startRow;
	public int endRow;
	public List<LoadStructure> headers;
	
	final static Charset ENCODING_UTF_8 = StandardCharsets.UTF_8;
	
	@Override
	public ResultSet getData() throws Exception {
		MockResultSet mockResultSet = new MockResultSet("textResultSet");
		// �������� ���������
		int i;
		for (i = 0; i < headers.size(); i++) {
			mockResultSet.addColumn(headers.get(i).linkedField.fieldHandler.fieldName);
		}
		// ��������� ���������
		if (encoding == null) {
			encoding = ENCODING_UTF_8;
		}
		// �������� ������
		Path path = Paths.get(fileName);
		Scanner scanner = new Scanner (path, encoding.name());
		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			Scanner sentence = new Scanner(scanner.nextLine());
			lineNumber ++;
			// ��������� ������ ��������� ���������� - ��������, ����� �� ��������� ������ ������
			if (startRow > 0) {
				if (lineNumber < startRow) continue;
			}
			// ���� �������� ������ ���������� - ��������, �� ���� �� �����
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
					// ���� �� ���������� ��������� - ������� ��� ������
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
	
	// ������� ����������, ����� �� ��������� �������� ������� ���� � ������� ������ �� ��������� headers
	boolean NeedToAddValue(int position) {
		int i;
		for (i = 0; i < headers.size(); i ++) {
			if (headers.get(i).positionInSource == position) return true;
		}
		return false;
	}

	@Override
	public String getFieldName(int position) throws Exception {
		if (headers.size() > position) return headers.get(position).linkedField.fieldHandler.fieldName;
		return null;
	}

}