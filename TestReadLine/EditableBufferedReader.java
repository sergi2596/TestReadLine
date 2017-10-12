import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

class EditableBufferedReader extends BufferedReader{
	
	int columna, fila;
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
        System.out.print(String.format("%c[%d;%d%s",escCode,31,47,"m")); //colors
        System.out.print(String.format("%c[%d%s",escCode,4,"h"));	//insert
        //System.out.print(String.format("%c[%d%s",escCode,6,"n"));
        //System.out.print(read()+""+read()+""+read()+""+read()+""+read());
        //System.out.print(String.format("%c[%d%s",escCode,7,"h"));	//line wrap
        
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
                System.out.print(String.format("%c[%d%s",escCode,1,"B"));//DOWN
            }
            if (cr ==302){
                System.out.print(String.format("%c[%d%s",escCode,1,"C"));//RIGHT
            }
            if (cr ==303){
                System.out.print(String.format("%c[%d%s",escCode,1,"D"));//LEFT
            }
            if (cr==295) {
                str="enviar un suprimir";

            }
            
            //LLEGIM COLUMNA I FILA ACTUAL CADA COP QUE APRETEM TECLA
            System.out.print(String.format("%c[%d%s",escCode,6,"n"));
            read();
            columna = read()-48;
            read();
            fila = read()-48;
            read();
            System.out.print("fila: "+fila+"columna: "+ columna);
            //COMPROBAR QUE NO ESTEM A L'ULTIMA COLUMNA
            colstotals = c.getConsoleWidth();
            if(columna == colstotals) {
            	fila = fila + 1;
            	columna = 1;
            	System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f"));
            }
            
        }
        return str;   
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