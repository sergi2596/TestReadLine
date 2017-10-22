import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.io.InputStream;
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

	
    public EditableBufferedReader(Reader in){
        super(in);
    }
    @Override
    public String readLine() throws IOException{
        int cr = 0;
        String str = "";
        char escCode = 0x1B;
        System.out.print(String.format("%c[%d;%d%s",escCode,37,49,"m")); //colors
        System.out.print(String.format("%c[%d%s",escCode,4,"h"));	//insert
        rowcol.setFirstRow();
        int firstrow = rowcol.getFirstRow();
        
        while (cr != CTRLD) {
        	currentrow = rowcol.getRow();
        	currentcol = rowcol.getColumn();
			cr = read();			
			if (96 < cr && cr < 123) {
				rowcol.AddColumn();
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
			else if (cr == UP_ARROW) {
				if (currentrow > firstrow ) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					if (currentcol > rowcol.getMaxColumn((currentrow-1))) {
						System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(currentrow-1), "G"));
					}
				}
				
				
			}
			else if (cr == DOWN_ARROW) {
				if (currentrow < rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "B"));
					if (currentcol > rowcol.getMaxColumn((currentrow+1))) {
						System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(currentrow+1), "G"));
					}
				}
				
			}
			else if (cr == RIGHT_ARROW) {
				if (currentcol <= rowcol.getMaxColumn(currentrow)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				}
				else if ((currentcol > rowcol.getMaxColumn(currentrow)) && currentrow < rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
				}
				
				
			}
			else if (cr == LEFT_ARROW) {
				if (currentcol > 1) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				}
				else if ((currentcol == 1) && currentrow > firstrow) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(rowcol.getRow()), "C"));
				}
				
			}
			else if (cr == DELETE) {
				if (currentcol == 1 && currentrow > firstrow) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(currentrow-1)+1, "G"));
				}
				System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));
				rowcol.DownColumn();

			} 
			else if (cr == SUPRIMIR) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));

			} 
			else if (cr == HOME) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "G"));
			} 
			else if (cr == END) {
				System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(currentrow)+1,
						"G"));
			} 
			else if (cr == ENTER) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
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
