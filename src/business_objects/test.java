package business_objects;

public class test {
	
	public static void main(String[] args) {
		try {
			MetabaseHandler metabaseHandler = new MetabaseHandler();
			boolean recreateMetabase = true;
			metabaseHandler.CreateMetabase(recreateMetabase, "10.0.3.50", "5432", "testdb", "postgres", "postgres");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation completed...");
	}

}
