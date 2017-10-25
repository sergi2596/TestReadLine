import java.io.*;

class TestReadLine {
    
    ConsoleWidth cols = new ConsoleWidth();
    static EditableBufferedReader in = new EditableBufferedReader(new InputStreamReader(System.in));

    
    public static void main(String[] args) {
        in.setRaw();
        String str= null;
        //int str = 0;
        try {
                str = in.readLine();
               // str = in.read();
            } catch(IOException e) {
                e.printStackTrace();
            }
        System.out.print(str);
        in.unsetRaw();        
    }
}

