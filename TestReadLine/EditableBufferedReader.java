import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.io.InputStreamReader;

class EditableBufferedReader extends BufferedReader {

	int currentcol, currentrow;
	int TOTALCOLS = new ConsoleWidth().getConsoleWidth();
	RowColumn rowcol = new RowColumn();
	boolean aux = false;
	final int UP_ARROW = 300, DOWN_ARROW = 301, RIGHT_ARROW = 302,
			LEFT_ARROW = 303, SPACE = 32, CTRLD = 4, CTRLS = 19,
			SUPRIMIR = 295, ESC = 27, CORXET = 91, DELETE = 127, HOME = 305,
			END = 304, ENTER = 13;

	public EditableBufferedReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		int cr = 0;
		String str = "";
		char escCode = 0x1B;
		System.out.print(String.format("%c[%d;%d%s", escCode, 37, 49, "m")); // colors
		System.out.print(String.format("%c[%d%s", escCode, 4, "h")); // insert

		/**
		 * Quan inciem el programa, marquem amb setFirstRow() quina es la
		 * primera fila de l'editor. A continuació la guardem amb getFirstRow()
		 */
		currentrow = rowcol.getRow();
		currentcol = rowcol.getColumn();
		rowcol.setFirstRow();
		int firstrow = rowcol.getFirstRow();
		//System.out.print("First row: " + firstrow + " Max col: " + rowcol.getMaxColumn(firstrow));

		while (cr != CTRLD) {

			/**
			 * Cada cop que escrivim, guardem la posició del cursor amb getRow()
			 * i getCol()
			 */
			currentrow = rowcol.getRow();
			currentcol = rowcol.getColumn();
			cr = read();
			if (96 < cr && cr < 123) {
				rowcol.AddColumn(); // Si el caracter es una lletra, primer de
									// tot afegim una columna a la fila
				str = Character.toString((char) cr);
				System.out.print(str);
			} else if (cr == CTRLS) {
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
				rowcol.AddColumn();
				System.out.print(String.format("%c[%d%s", escCode, 1, "@"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));

			}

			/**
			 * Per la UP_ARROW, primer mirem que la fila actual no sigui la
			 * primera. En tal cas, si estem a una columna més gran que el
			 * numero màxim de columnes de la fila de dalt, enviem el cursor a
			 * la columna màxima de la fila de dalt (així evitem que es pugui
			 * fer "trampa" i enviar el cursor a una columna de la fila de dalt
			 * que encara no existeix. Ho fem en dos passos, primer movem cursor
			 * a dalt i despres a la dreta (hi ha una comanda per fer-ho de cop
			 * però no m'ha funcionat bé).
			 */

			else if (cr == UP_ARROW) {
				if (currentrow > firstrow) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					if (currentcol > rowcol.getMaxColumn((currentrow - 1))) {
						System.out.print(String.format("%c[%d%s", escCode,
								rowcol.getMaxColumn(currentrow - 1), "G"));
					}
				}

				/**
				 * Igual que UP_ARROW però mirant que no ens passem de la fila
				 * màxima
				 */

			} else if (cr == DOWN_ARROW) {
				if (currentrow < rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "B"));
					if (currentcol > rowcol.getMaxColumn((currentrow + 1))) {
						System.out.print(String.format("%c[%d%s", escCode,
								rowcol.getMaxColumn(currentrow + 1), "G"));
					}
				}

				/**
				 * Si no estem a la columna maxima de la fila, movem cursor a la
				 * dreta i punto. Si estem a la ultima columna (però NO a la
				 * ultima fila), movem cursor a la fila següent, columna 1
				 */

			} else if (cr == RIGHT_ARROW) {
				if (currentcol <= rowcol.getMaxColumn(currentrow) && rowcol.getMaxColumn(currentrow) > 0) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				} else if (currentrow != rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
				}

				/**
				 * Igual que abans, però per canviar de fila ho fem en dos
				 * passos (també es pot fer en un però no funcionava). Primer
				 * movem cursor a la fila de dalt i despres el movem a la dreta
				 * fins la columna màxima.
				 */

			} else if (cr == LEFT_ARROW) {
				if (currentcol > 1) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				} else if (currentrow > firstrow) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					if (rowcol.getMaxColumn(currentrow-1) > 0) {
						System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(rowcol.getRow()), "C"));
					}
				}

				/**
				 * Si estem borrant la primera lletra d'una fila (que no sigui
				 * la primera fila), movem cursor a la fila de dalt fins la
				 * ultima columna i borrem el caracter que quedava. També
				 * decrementem el numero de columnes de la fila (en cas que no
				 * en quedin, el metode DownCol() s'encarrega d'eliminar la fila
				 * del TreeMap)
				 */

			} else if (cr == DELETE) {
				

				if (currentcol == 1 && currentrow > firstrow) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					//System.out.print("ROW " + currentrow + " " + rowcol.printMap());
					System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(currentrow-1)+1, "G"));
				}
				else {
					System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
					System.out.print(String.format("%c[%d%s", escCode, 1, "X"));
				}
				rowcol.DownColumn();

				

				/**
				 * SUPRIMIR encara NO està implementat correctament
				 */

			} else if (cr == SUPRIMIR) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "P"));
				if (rowcol.getMaxColumn(currentrow) > 0 && currentcol <= rowcol.getMaxColumn(currentrow)) {
					rowcol.DownColumn();
				}
				
				/**
				 * HOME i END són iguals que sempre, molt bàsic 
				 */
				
			} else if (cr == HOME) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "G"));
			} else if (cr == END) {
				System.out.print(String.format("%c[%d%s", escCode,
						rowcol.getMaxColumn(currentrow) + 1, "G"));

				/**
				 * ENTER només funciona si en fem un i escrivim algo. Si fem dos
				 * ENTERS (deixant una fila en blanc) NO està implementat
				 */
			} else if (cr == ENTER) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
				rowcol.filacol();
				rowcol.AddColumn();
			}

		}
		System.out.print(rowcol.printMap());
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
