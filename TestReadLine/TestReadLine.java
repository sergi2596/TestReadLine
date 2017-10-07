import java.io.*;

class TestReadLine {

	static ConsoleWidth cols = new ConsoleWidth();
	static EditableBufferedReader in = new EditableBufferedReader(new InputStreamReader(System.in));


	public static void main(String[] args) {
				
		in.setRaw();
		String str = null;
		try {
			str = in.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		in.unsetRaw();
	}
}


