import java.util.ArrayList;
import java.util.List;

public class Automata {
    private int[][] matriz;
    private String palabra;
    private List<Character> letras;

    public Automata(String palabra){
        this.palabra = palabra.trim();
        letras = new ArrayList<>();
        inicializar();
    }

    private void inicializar(){
        //obtener las letras de la palabra
        for(char c : palabra.toCharArray()){
            if(!letras.contains(c)) letras.add(c);
        }//

        //inicializar la matriz
        matriz = new int[palabra.length()][letras.size()];

        int letraPos = 0;
        for(int i = 0; i<matriz.length; i++){
            for(int j = 0; j<matriz[0].length; j++){
                if(j==letras.indexOf(palabra.charAt(letraPos))){
                    matriz[i][j] = i+1;
                } else {
                    matriz[i][j] = -1;
                }
            }//for j
            letraPos++;
        }//for i

        System.out.println(toString());
    }//inicializar

    @Override
    public String toString(){
        String res = "[";
        for(int i = 0; i<matriz.length; i++){
            res+="{";
            for(int j = 0; j<matriz[0].length; j++){
                if(j==matriz[0].length-1){
                    res+=matriz[i][j]+"}";
                } else{
                    res+=matriz[i][j]+", ";
                }
            }//for j
            res+="\n";
        }//for i
        return res+="]";
    }
}//