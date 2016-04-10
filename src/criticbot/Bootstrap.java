package criticbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Bootstrap {
	
	private DataBase db;
	
	public Bootstrap(DataBase db)
	{
		this.db = db;
		File folder = new File(Config.dataDirectory);
		Scanner keyboard = new Scanner(System.in);
		String[] fileNames = new String[folder.listFiles().length];
		int fileIndex = 0;
		String menuOutput = "Select file to import:\n";
		for(final File fileEntry : folder.listFiles())
		{
			String fileName = fileEntry.getName();
			fileNames[fileIndex] = fileName;
			menuOutput += "Press " + (fileIndex + 1) + " to import " + fileName + "\n";
			fileIndex++;
		}
		menuOutput += "Press 0 to quit.\n";
		String proceed = "";
		String fileName = "";
		do {
			
			System.out.println(menuOutput);
			fileIndex = keyboard.nextInt();
			fileName = fileNames[fileIndex - 1];
			System.out.print("Import " + fileName + "? [y/n] ");
			proceed = keyboard.next();
		} while (!proceed.equals("y"));
		
		try{
			this.writeToMySQL(fileName);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		keyboard.close();
	}
	 
	private String getTableName(String fileName)
	{
		String[] parts = fileName.split("\\.");
		return parts.length > 0 ? parts[0].trim():"";
	}
	
	private void writeToMySQL(String fileName) throws IOException
	{
		String tableName = this.getTableName(fileName);
		System.out.println(fileName);
		FileInputStream in = new FileInputStream(Config.dataDirectory + "/" + fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = "";
		
		String[] columnHeaders = br.readLine().split("\\t");
		int rowsProcessed = 0;
		while((line = br.readLine()) != null)
		{
			String[] rawRowData = line.split("\\t");
			HashMap<String, String> insertData = this.cleanRowData(columnHeaders, rawRowData);
			String sql = this.buildParameterizedSQLInsert(tableName, insertData.keySet());
			this.db.insert(sql, insertData);
			rowsProcessed++;
			System.out.println(rowsProcessed + " Rows Processed.");
		}
		System.out.println("Import done for " + fileName);
		br.close();
	}
	
	private HashMap<String, String> cleanRowData(String[] columnHeaders, String[] rawRowData) 
	{
		HashMap<String, String> result = new HashMap<>();
		
		for (int i = 0; i < columnHeaders.length; i++) {
	
			if (rawRowData[i].trim().equals("\\N")) { 
				continue;
			}
			
			result.put(columnHeaders[i], rawRowData[i]);
		}
		
		return result;
	}
	
	private String buildParameterizedSQLInsert(String tableName, Set<String> keyset)
	{
		int parameterCount = keyset.size();
		String[] placeHolders = new String[parameterCount];
		String[] columnNames = keyset.toArray(new String[parameterCount]);
		
		for (int i = 0; i < parameterCount; i++) {
			placeHolders[i] = "?";
		}
		
		return "REPLACE INTO " + tableName + "(" + Utils.arrayJoin(columnNames, ",") + ")" 
				+ "VALUES(" + Utils.arrayJoin(placeHolders, ",") + ")";
	}
}
