import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.io.InputStream;
import java.io.InputStreamReader;


class EditableBufferedReader extends BufferedReader {

	int columna, fila;
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
        int cr;
        String str = "";
        char escCode = 0x1B;
        cr = 0;
        System.out.print(String.format("%c[%d;%d%s",escCode,37,49,"m")); //colors
        System.out.print(String.format("%c[%d%s",escCode,4,"h"));	//insert
        rowcol.setFirstRow();
        
        while (cr != CTRLD) {
        	fila = rowcol.getRow();
        	columna = rowcol.getColumn();
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
				if (fila > 2 ) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					if (columna > rowcol.getMaxColumn((fila-1))) {
						System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(fila-1), "G"));
					}
				}
				
				
			}
			else if (cr == DOWN_ARROW) {
				if (fila < rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "B"));
					if (columna > rowcol.getMaxColumn((fila+1))) {
						System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(fila+1), "G"));
					}
				}
				
			}
			else if (cr == RIGHT_ARROW) {
				if (columna < rowcol.getMaxColumn(fila)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				}
				else if ((columna >= rowcol.getMaxColumn(fila)) && fila < rowcol.getLastRow()) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
				}
				
				
			}
			else if (cr == LEFT_ARROW) {
				if (columna > 1) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				}
				else if ((columna == 1) && fila > 2) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
					System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(rowcol.getRow()), "C"));
				}
				
			}
			else if (cr == DELETE) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));

			} else if (cr == SUPRIMIR) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				System.out.print(String.format("%c[%d%s", escCode, 1, "X"));

			} else if (cr == HOME) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "G"));
			} else if (cr == END) {
				System.out.print(String.format("%c[%d%s", escCode, rowcol.getMaxColumn(fila),
						"G"));
			} else if (cr == ENTER) {
				System.out.print(String.format("%c[%d%s", escCode, 1, "E"));
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
            
            //LLEGIM COLUMNA I FILA ACTUAL CADA COP QUE APRETEM TECLA
            //filacol();
            //maxims();
            /*
             * DEBUGGER PROPI:
             * Obrir un altre terminal apart de la que utilitzem per executar el programa.
             * Mirar amb la comana 'ps' quin es el tty del terminal i cambiar el /dev/pts/X de les comanes de sota.
             * Aixi podem observar tots els paràmetres seguents en temps real: fila,columna,maxfil,maxcol,filainicial.
             */
            /*try { 
                String[] command = { "bash", "-c", "echo fila: " + fila+" columna: "+ columna+" filainicial: "+FILAINICIAL +" > /dev/pts/18" };
            	InputStream in = Runtime.getRuntime().exec(command).getInputStream();
        		String[] command1 = { "bash", "-c", "echo maxfila:" + maxfil+" maxcol: "+ maxcol +" > /dev/pts/18" };
        		InputStream in1 = Runtime.getRuntime().exec(command1).getInputStream();
            	String[] command2 = { "bash", "-c", "echo ----------------- > /dev/pts/18" };
            	InputStream in2 = Runtime.getRuntime().exec(command2).getInputStream();
            }catch(Exception e) {
            	e.printStackTrace();
            }
*/
            
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
