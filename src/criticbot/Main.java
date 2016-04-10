package criticbot;

public class Main {

	public static void main(String[] args) {
		
		DataBase db = new DataBase(Config.dbUser, Config.dbPassword);
		if(Config.bootstrap)
		{
			new Bootstrap(db);
		}
	}
}
