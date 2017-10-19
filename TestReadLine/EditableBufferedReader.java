import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class EditableBufferedReader extends BufferedReader {

	int columna, fila;
	int TOTALCOLS = new ConsoleWidth().getConsoleWidth();
	boolean aux = false;
	CursorPosition cp = new CursorPosition();
	final int UP_ARROW = 300, DOWN_ARROW = 301, RIGHT_ARROW = 302,
			LEFT_ARROW = 303, SPACE = 32, CTRLD = 4, CTRLS = 19,
			SUPRIMIR = 295, ESC = 27, CORXET = 91, DELETE = 127, HOME = 305,
			END = 304;

	public EditableBufferedReader(Reader in) {
		super(in);
	}

	int hola = 0;

	@Override
	public String readLine() throws IOException {
		int cr;
		String str = "";
		char escCode = 0x1B;
		cr = 0;
		System.out.print(String.format("%c[%d;%d%s", escCode, 37, 49, "m")); // colors
		System.out.print(String.format("%c[%d%s", escCode, 4, "h")); // insert
		// System.out.print(String.format("%c[%d%s",escCode,6,"n"));
		// System.out.print(read()+""+read()+""+read()+""+read()+""+read());
		// System.out.print(String.format("%c[%d%s",escCode,7,"h")); //line wrap

		while (cr != CTRLD) {
			cr = read();
			if (96 < cr && cr < 123) {
				str = Character.toString((char) cr);
				System.out.print(str);
			}
			else if (cr == CTRLS) {
				if (aux) {
					String[] cmd = { "bash", "-c", "tput rmul > /dev/tty" };
					executarComanda(cmd);
					aux = false;

				} else {
					String[] cmd = { "bash", "-c", "tput smul > /dev/tty" };
					executarComanda(cmd);
					aux = true;
				}
			}

			else if (cr == SPACE) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "@"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
			}
			else if (cr == UP_ARROW) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
			}
			else if (cr == DOWN_ARROW) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "B"));
			}
			else if (cr == RIGHT_ARROW) {
				/*
				 * String cursorposition = cp.getCurrentPosition();
				 * System.out.print(cursorposition);
				 * 
				 * System.out.print(String.format("%c[%d%s",escCode,6,"n"));
				 * System.out.print(read() +" "+ read() +" "+ read() +" "+
				 * read() + "" + read() +" "+ read() +"   ");
				 * System.out.print(readLine());
				 */

				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
			}
			else if (cr == LEFT_ARROW) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
			}
			else if (cr == DELETE) {

				System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));

			}
			else if (cr == SUPRIMIR) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));
				//System.out.print(String.format("%c[%d%s", escCode, 1, "S"));


			}
			else if ( cr == HOME) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "G"));
			}
			else if (cr == END) {
				System.out.print(String.format("%c[%d%s", escCode, TOTALCOLS, "G"));
			}
			/**
			 * Delete = 127 Home = 27 91 72 End = 27 91 70
			 */

			/**
			 * SEQUENCIA RESPOSTA POSICIÓ CURSOR:
			 * 
			 * ESC [ X;YR
			 * 
			 * ESC --> 27 [ --> 91 X --> X ; --> 59 Y --> Y R --> 82
			 */

			// LLEGIM COLUMNA I FILA ACTUAL CADA COP QUE APRETEM TECLA
			/*
			 * int currentcolumn = read(); System.out.println(currentcolumn);
			 * read(); columna = read()-48; read(); fila = read()-48; read();
			 * System.out.print("fila: "+fila+"columna: "+ columna);
			 */
			// COMPROBAR QUE NO ESTEM A L'ULTIMA COLUMNA
			if (columna == TOTALCOLS) {
				fila = fila + 1;
				columna = 1;
				System.out.print(String.format("%c[%d;%d%s", escCode, fila,
						columna, "f"));
			}

		}
		return str;
	}

	@Override
	public int read() throws IOException {
		int cr = 0;
		int valor_final;
		int aux, aux2, aux3;
		cr = super.read();
		if (cr == ESC) {
			aux = super.read();
			if (aux == CORXET) {
				aux2 = super.read();
				if (aux2 == 65 || aux2 == 66 || aux2 == 67 || aux2 == 68) {
					cr = cr + aux + aux2 + 117;
				}

				else if (aux2 == 51) {
					aux3 = super.read();
					if (aux3 == 126) {
						cr = SUPRIMIR;
					}

				} else if (aux2 == 70) { 
					cr = END;
				} else if (aux2 == 72) {
					cr = HOME;
				}
			}
		}
		valor_final = cr;
		return valor_final;
	}

	public void setRaw() {
		String[] cmd = { "/bin/sh", "-c", "stty -echo raw </dev/tty" };
		executarComanda(cmd);
	}

	public void unsetRaw() {
		String[] cmd = { "/bin/sh", "-c", "stty echo cooked </dev/tty" };
		executarComanda(cmd);
	}

	private void executarComanda(String[] command) {
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}