import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RowColumn {

	/**
	 * La idea es crear un TreeMap amb [clau/valor] = [fila/columna maxima]
	 * Aixi, tindrem un TreeMap amb totes les files que hem escrit i la columna
	 * maxima de cada fila. Molt util a l'hora de controlar les fletxes, home i
	 * end.
	 */

	Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
	int currentcol, currentrow, maxcol, maxfil, FILAINICIAL;
	final int UP_ARROW = 300, DOWN_ARROW = 301, RIGHT_ARROW = 302,
			LEFT_ARROW = 303, SPACE = 32, CTRLD = 4, CTRLS = 19,
			SUPRIMIR = 295, ESC = 27, CORXET = 91, DELETE = 127, HOME = 305,
			END = 304, PUNTICOMA = 11;

	public void filacol() {
		List<Integer> filacols = new ArrayList<Integer>();
		int aux, i, indice, longitud;
		aux = 0;
		i = 0;
		indice = 0;
		longitud = 0;
		char escCode = 0x1B;
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(System.in));

		try {
			System.out.print(String.format("%c[%d%s", escCode, 6, "n"));
			while (aux != 82) {
				aux = buf.read();
				if ((aux != 27) && (aux != 91) && (aux != 82)) {
					filacols.add(aux);
				}
			}
			while (i < filacols.size()) {
				filacols.set(i, filacols.get(i) - 48);
				i++;
			}
			indice = filacols.indexOf(PUNTICOMA);
			longitud = filacols.size();
			switch (indice) {
			case 1:
				currentrow = filacols.get(0);
				if (longitud == 3) {
					currentcol = filacols.get(2);
				} else {
					currentcol = Integer.parseInt(filacols.get(2) + ""
							+ filacols.get(3));
				}
				break;
			case 2:
				currentrow = Integer.parseInt(filacols.get(0) + ""
						+ filacols.get(1));
				if (longitud == 4) {
					currentcol = filacols.get(3);
				} else if (longitud == 5) {
					currentcol = Integer.parseInt(filacols.get(3) + ""
							+ filacols.get(4));
				}
				break;
			default:
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * He creat bastants mètodes però que fan funcions molt senzilles i clares
	 * (a tope amb el softcode).
	 */

	/**
	 * Executa el mètode filacol() del gran Juanjo i retorna columna actual
	 * 
	 * @return int columa_actual
	 */
	public int getColumn() {
		//filacol();
		return this.currentcol;
	}

	/**
	 * Executa el mètode filacol() del gran Juanjo i retorna fila actual
	 * 
	 * @return int fila_actual
	 */

	public int getRow() {
		//filacol();
		return this.currentrow;
	}

	/**
	 * Fixa la primera fila de l'editor. L'invoquem només un cop a l'inici del
	 * programa per marcar quina és la primera fila.
	 */

	public void setFirstRow() {
		map.put(currentrow, 0);
	}

	/**
	 * Incrementa la columna maxima de la fila actual del TreeMap. Així, cada
	 * cop que escrivim un caràcter, incrementem el número de columnes de la
	 * fila en qüestió. Abans d'això, mirem si la fila existeix dins del TreeMap
	 * (pot ser que estiguem escrivint la primera lletra d'aquella fila, i per
	 * tant hem de crear una nova clau amb la fila)
	 */

	public void AddColumn(int ncols) {
		int maxcol = -1;
		if (map.containsKey(currentrow)) {
			maxcol = getMaxColumn(currentrow);
		}
		map.put(currentrow, maxcol + ncols);
	}

	/**
	 * Idem però eliminant una columna. Es fa servir per quan apretem tecla
	 * DELETE. El primer if de tots és per mirar que no estiguem a l'inici de
	 * l'editor (1a fila, 1a columna) Si estem a la primera columna d'una fila
	 * (que no sigui la primera) i apretem DELETE, es borra aquella fila del
	 * TreeMap. Si no, simplement decrementem el numero de columnes de la fila.
	 */

	public void DownColumn() {
		if (currentrow != getFirstRow() || currentcol != 1) {
			int maxcol = getMaxColumn(currentrow);
			if (maxcol == 0) {
				removeRow();
			} else
				map.put(currentrow, maxcol - 1);
		}
	}

	public void removeRow() {
		map.remove(currentrow);
	}
	
	public void setRowtoZero() {
		map.put(currentrow, 0);
	}
	/**
	 * Retorna el numero maxim de columnes d'una fila
	 * 
	 * @param row
	 *            : fila de la que volem consultar el numero de columnes
	 * @return numero de columnes
	 */

	public int getMaxColumn(int row) {
		return map.get(row);
	}

	/**
	 * Retorna quina es la primera fila del programa. Util per marcar el limit
	 * de la UP_ARROW
	 * 
	 * @return
	 */

	public int getFirstRow() {
		return ((TreeMap<Integer, Integer>) map).firstKey();
	}

	/**
	 * Retorna la ultima fila de programa. Util per marcar el limit de la
	 * DOWN_ARROW
	 * 
	 * @return
	 */

	public int getLastRow() {
		int lastrow = ((TreeMap<Integer, Integer>) map).lastKey();
		return lastrow;
	}
	
	public Map<Integer, Integer> getMap() {
		return map;
	}

	/**
	 * ------------ COMMENTS ----------------
	 */
	/*
	 * public void maxims() { if(fila == maxfil) { maxcol = (columna>maxcol) ?
	 * columna: maxcol; } maxfil = (fila>maxfil) ? fila:maxfil; try{
	 * 
	 * }catch(Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	/*
	 * public boolean dinsText(int direccio) { boolean dins; char escCode =
	 * 0x1B; switch (direccio){ case UP_ARROW: dins = (fila == FILAINICIAL) ?
	 * false:true; break; case DOWN_ARROW: if(columna > maxcol) { dins = (fila
	 * == (maxfil-1)) ? false:true; }else { dins = (fila == (maxfil)) ?
	 * false:true; } break; case RIGHT_ARROW: dins = ((fila==maxfil && columna
	 * ==maxcol) || columna == TOTALCOLS) ? false:true; if (fila != maxfil &&
	 * columna ==TOTALCOLS) { fila = fila + 1; columna = 1;
	 * System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f")); }
	 * break; case LEFT_ARROW: dins = ((fila==FILAINICIAL && columna ==1) ||
	 * columna ==1) ? false:true; if (fila != FILAINICIAL && columna ==1) { fila
	 * = fila - 1; columna = TOTALCOLS;
	 * System.out.print(String.format("%c[%d;%d%s",escCode,fila,columna,"f")); }
	 * 
	 * break; default: dins = false; }
	 * 
	 * return dins; }
	 */

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

	// LLEGIM col I FILA ACTUAL CADA COP QUE APRETEM TECLA
	// filacol();
	// maxims();
	/*
	 * DEBUGGER PROPI: Obrir un altre terminal apart de la que utilitzem per
	 * executar el programa. Mirar amb la comana 'ps' quin es el tty del
	 * terminal i cambiar el /dev/pts/X de les comanes de sota. Aixi podem
	 * observar tots els paràmetres seguents en temps real:
	 * fila,col,maxfil,maxcol,filainicial.
	 */
	/*
	 * try { String[] command = { "bash", "-c", "echo fila: " + fila+" col: "+
	 * col+" filainicial: "+FILAINICIAL +" > /dev/pts/18" }; InputStream in =
	 * Runtime.getRuntime().exec(command).getInputStream(); String[] command1 =
	 * { "bash", "-c", "echo maxfila:" + maxfil+" maxcol: "+ maxcol
	 * +" > /dev/pts/18" }; InputStream in1 =
	 * Runtime.getRuntime().exec(command1).getInputStream(); String[] command2 =
	 * { "bash", "-c", "echo ----------------- > /dev/pts/18" }; InputStream in2
	 * = Runtime.getRuntime().exec(command2).getInputStream(); }catch(Exception
	 * e) { e.printStackTrace(); }
	 */

}