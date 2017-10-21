import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;

public class ConsoleWidth {

	public int getConsoleWidth() {
		
		String[] command = { "bash", "-c", "tput cols 2> /dev/tty" };
		try {
			InputStream in = Runtime.getRuntime().exec(command).getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buf = new BufferedReader(isr);
			String cols = buf.readLine();
			int numcols = Integer.parseInt(cols);
			return numcols;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

}
