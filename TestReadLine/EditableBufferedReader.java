import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.io.InputStream;
import java.io.InputStreamReader;


class EditableBufferedReader extends BufferedReader{
	
	int columna, fila,maxcol,maxfil;
	int colstotals;
	ConsoleWidth c = new ConsoleWidth();
	
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
        maxcol=1;
        maxfil=1;
        colstotals = c.getConsoleWidth();
        System.out.print(String.format("%c[%d;%d%s",escCode,31,47,"m")); //colors
        System.out.print(String.format("%c[%d%s",escCode,4,"h"));	//insert
        
        while(cr!= 4){
            cr = read();
            if (96<cr && cr<123){
                str= Character.toString((char) cr);
                System.out.print(str);

            }
             
            if (cr==32) {
                System.out.print(String.format("%c[%d%s",escCode,1,"@"));
                System.out.print(String.format("%c[%d%s",escCode,1,"C"));
            }
            if (cr ==300){
                System.out.print(String.format("%c[%d%s",escCode,1,"A"));//UP
            }
            if (cr ==301){
            	if(fila <= maxfil) {
            		if(fila < maxfil) {
            			System.out.print(String.format("%c[%d%s",escCode,1,"B"));//DOWN
            		}
            		if(fila == maxfil && columna < maxcol) {
            			System.out.print(String.format("%c[%d%s",escCode,1,"B"));//DOWN
            		}
        		}
                
            }
            if (cr ==302){
            	if((fila == maxfil) && (columna<= maxcol)) {
            		System.out.print(String.format("%c[%d%s",escCode,1,"C"));//RIGHT
            	}
                if (fila< maxfil) {
                	System.out.print(String.format("%c[%d%s",escCode,1,"C"));//RIGHT
                }
            }
            if (cr ==303){
                System.out.print(String.format("%c[%d%s",escCode,1,"D"));//LEFT
            }
            if (cr==295) {
                str="enviar un suprimir";

            }
            
            //LLEGIM COLUMNA I FILA ACTUAL CADA COP QUE APRETEM TECLA
            filacol();
            maxims();
            if(columna == colstotals) {
            	maxcol=1;
            	fila = fila + 1;
            	columna = 1;
            	System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f"));
            }
            
        }
        return str;   
    }
    public void maxims() {
    	if(fila == maxfil) {
    		maxcol = (columna>maxcol) ? columna: maxcol;
    	}
    	maxfil = (fila>maxfil) ? fila:maxfil;
    	try{
    		String[] command = { "bash", "-c", "echo maxfila:" + maxfil+" maxcol: "+ maxcol +" > /dev/pts/17" };
    		InputStream in = Runtime.getRuntime().exec(command).getInputStream();
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

    	String[] command = { "bash", "-c", "echo fila: " + fila+" columna: "+ columna +" > /dev/pts/17" };
    	InputStream in = Runtime.getRuntime().exec(command).getInputStream();
    			
    	
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    }

    @Override
    public int read() throws IOException{
        int cr=0;
        int valor_final;
        int aux,aux2,aux3;
        cr = super.read(); 
        if (cr == 27){
                aux = super.read();
                if (aux == 91){
                    aux2 = super.read();
                    if(aux2 == 65 || aux2 == 66 || aux2 == 67 || aux2 ==68) {
                        cr = cr + aux + aux2 + 117;
                    }
                    
                    if(aux2 == 51) {
                        aux3 = super.read();
                        if(aux3 == 126) {
                            cr = cr + aux + aux2 + aux3;
                        }
                        
                    }        
                }
        }
        valor_final = cr;
        //cr=0;
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