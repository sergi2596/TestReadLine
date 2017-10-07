import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleWidth {

	public int getConsoleWidth() {
		
		String[] command = { "bash", "-c", "tput cols 2> /dev/tty" };
		try {
			InputStream in = Runtime.getRuntime().exec(command).getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buf = new BufferedReader(isr);
			int numcols = Integer.parseInt(buf.readLine());
			return numcols;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
