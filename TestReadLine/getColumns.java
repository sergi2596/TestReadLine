import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class getColumns {

	public static void main(String[] args) {
		String[] command = { "bash", "-c", "tput cols 2> /dev/tty" };
		try {
			InputStream in = Runtime.getRuntime().exec(command).getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader buf = new BufferedReader(isr);
			System.out.println(buf.readLine());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
