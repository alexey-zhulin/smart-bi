package data_loader_descriptors;

// Класс описывает параметры при загрузке данных
public class LoadParams {
	public boolean loadSequenceFields; // Загружать значение в поле, если поле имеет тип serial
	public String syncFieldName; // Имя поля для синхронизации имеющихся и загружаемых данных
	
	public LoadParams() {
		// Значения по умолчанию
		this.loadSequenceFields = false;
	}
}
