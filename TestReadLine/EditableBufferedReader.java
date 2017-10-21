import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.io.InputStream;
import java.io.InputStreamReader;


class EditableBufferedReader extends BufferedReader{
	
	int columna, fila,maxcol,maxfil,FILAINICIAL;
	int TOTALCOLS = new ConsoleWidth().getConsoleWidth();
	boolean aux = false;
	final int UP_ARROW = 300, DOWN_ARROW = 301, RIGHT_ARROW = 302,
			LEFT_ARROW = 303, SPACE = 32, CTRLD = 4, CTRLS = 19,
			SUPRIMIR = 295, ESC = 27, CORXET = 91, DELETE = 127, HOME = 305,
			END = 304;

	
    public EditableBufferedReader(Reader in){
        super(in);
    }
    int hola =0;
    @Override
    public String readLine() throws IOException{
        int cr;
        String str = "";
        char escCode = 0x1B;
        cr = 0;
    	filacol();
    	FILAINICIAL=fila;
        maxcol=1;
        maxfil=fila;
        System.out.print(String.format("%c[%d;%d%s",escCode,31,47,"m")); //colors
        System.out.print(String.format("%c[%d%s",escCode,4,"h"));	//insert
        System.out.print(String.format("%c[%s%d%s",escCode,"?",4,"h"));	//insert
        
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
				if(dinsText(UP_ARROW)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "A"));
				}
				
			}
			else if (cr == DOWN_ARROW) {
				if(dinsText(DOWN_ARROW)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "B"));
				}
				
			}
			else if (cr == RIGHT_ARROW) {
				if(dinsText(RIGHT_ARROW)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "C"));
				}
				
			}
			else if (cr == LEFT_ARROW) {
				if(dinsText(LEFT_ARROW)) {
					System.out.print(String.format("%c[%d%s", escCode, 1, "D"));
				}
				
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
            
            //LLEGIM COLUMNA I FILA ACTUAL CADA COP QUE APRETEM TECLA
            filacol();
            maxims();
            /*
             * DEBUGGER PROPI:
             * Obrir un altre terminal apart de la que utilitzem per executar el programa.
             * Mirar amb la comana 'ps' quin es el tty del terminal i cambiar el /dev/pts/X de les comanes de sota.
             * Aixi podem observar tots els paràmetres seguents en temps real: fila,columna,maxfil,maxcol,filainicial.
             */
            try { 
                String[] command = { "bash", "-c", "echo fila: " + fila+" columna: "+ columna+" filainicial: "+FILAINICIAL +" > /dev/pts/18" };
            	InputStream in = Runtime.getRuntime().exec(command).getInputStream();
        		String[] command1 = { "bash", "-c", "echo maxfila:" + maxfil+" maxcol: "+ maxcol +" > /dev/pts/18" };
        		InputStream in1 = Runtime.getRuntime().exec(command1).getInputStream();
            	String[] command2 = { "bash", "-c", "echo ----------------- > /dev/pts/18" };
            	InputStream in2 = Runtime.getRuntime().exec(command2).getInputStream();
            }catch(Exception e) {
            	e.printStackTrace();
            }

            
        }
        return str;   
    }
    public boolean dinsText(int direccio) {
    	boolean dins;
        char escCode = 0x1B;
    	switch (direccio){
    	case UP_ARROW:
    		dins = (fila == FILAINICIAL) ? false:true;
    		break;
    	case DOWN_ARROW:
    		if(columna > maxcol) {
    			dins = (fila == (maxfil-1)) ? false:true;
    		}else {
    			dins = (fila == (maxfil)) ? false:true;
    		}
    		break;
    	case RIGHT_ARROW:
    		dins = ((fila==maxfil && columna ==maxcol) || columna == TOTALCOLS) ? false:true;
    		if (fila != maxfil && columna ==TOTALCOLS) {
    			fila = fila + 1;
            	columna = 1;
            	System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f"));
    		}
    		break;
    	case LEFT_ARROW:
    		dins = ((fila==FILAINICIAL && columna ==1) || columna ==1) ? false:true;
    		if (fila != FILAINICIAL && columna ==1) {
    			fila = fila - 1;
            	columna = TOTALCOLS;
            	System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f"));
    		}
    		
    		break;
    	default:
    		dins = false;
    	}
    	
    	return dins;
    }
    public void maxims() {
    	if(fila == maxfil) {
    		maxcol = (columna>maxcol) ? columna: maxcol;
    	}
    	maxfil = (fila>maxfil) ? fila:maxfil;
    	try{

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
    public void filacol() {
    	List<Integer> filacols = new ArrayList<Integer>();
    	int aux,i,indice,longitud;
    	aux=0;i=0;indice=0;longitud=0;
    	char escCode = 0x1B;
    	BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));

    	try {
    		System.out.print(String.format("%c[%d%s",escCode,6,"n"));
    		while(aux!=82) {
    			aux = buf.read();
    			if((aux != 27) && (aux != 91) && (aux != 82)) {
    				filacols.add(aux);
    			}
    		}
        	while(i<filacols.size()) {
        		filacols.set(i,filacols.get(i)-48);
        		i++;
        	}
        	indice = filacols.indexOf(11);
        	longitud = filacols.size();
        	switch(indice) {
    		case 1:
    			fila = filacols.get(0);
    			if(longitud==3) {
    				columna = filacols.get(2);
    			}else {
    				columna = Integer.parseInt(filacols.get(2) + "" + filacols.get(3));
    			}
    			break;
    		case 2:
    			fila = Integer.parseInt(filacols.get(0) + "" + filacols.get(1));
    			if(longitud==4) {
    				columna = filacols.get(3);
    			}else if(longitud ==5) {
    				columna = Integer.parseInt(filacols.get(3) + "" + filacols.get(4));
    			}
    			break;
    		default:
    		}		
    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
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
    

    public void setRaw(){
        String[] cmd = {"/bin/sh","-c","stty -echo raw </dev/tty"};
        executarComanda(cmd);
    }
    public void unsetRaw(){
        String[] cmd = {"/bin/sh","-c","stty echo cooked </dev/tty"};
        executarComanda(cmd);
    }
    private void executarComanda(String[] command){
        try{
            Runtime.getRuntime().exec(command);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}