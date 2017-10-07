
public class CursorPosition {
	
    public static void main(String[] args) {
    	
    	getCurrentPosition();
    	
    }
    
    public static void getCurrentPosition() {
    	
    	/*String[] str = {" ~$ echo -e "\033[6n""};
    	Runtime.getRuntime().exec(str);*/
    	System.out.println("\033[6n");
    	
    }
}


/*
 * At ANSI compatible terminals, printing the sequence ESC[6n will report the
 * cursor position to the application as (as though typed at the keyboard)
 * ESC[n;mR, where n is the row and m is the column.
 * 
 * Example:
 * 
 * ~$ echo -e "\033[6n"
 * 
 * EDITED:
 * 
 * You should make sure you are reading the keyboard input. The terminal will
 * "type" just the ESC[n;mR sequence (no ENTER key). In bash you can use
 * something like:
 * 
 * 
 * echo -ne "\033[6n" 				# ask the terminal for the position 
 * read -s -d\[ garbage				# discard the first part of the response 
 * read -s -d R foo 				# store the position in bash variable 'foo' 
 * echo -n "Current position: " 
 * echo "$foo" 						# print the position
 * 
 * 
 * Explanation: the -d R (delimiter) argument will make read stop at the char R
 * instead of the default record delimiter (ENTER). This will store ESC[n;m in
 * $foo. The cut is using [ as delimiter and picking the second field, letting
 * n;m (row;column).
 */