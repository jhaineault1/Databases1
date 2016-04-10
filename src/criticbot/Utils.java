package criticbot;

public class Utils {

	public static String arrayJoin(String[] arr, String delimiter)
	{
		String result = "";
		if(arr.length < 2)
		{
			return arr[0];
		}
		int arrayLen = arr.length;
		for(int i = 0; i < arr.length; i++)
		{
			if(i == arrayLen - 1)
			{
				result += arr[i];
			}else
			{
				result += arr[i] + delimiter + " ";
			}
			
		}
		return result;
	}
	
	public static void debug_log(String value) 
	{
		if(Config.debug) {
			System.out.println(value);
		}
	}
}
