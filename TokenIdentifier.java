public class TokenIdentifier{
    private int[][] matrizJuego = {
        {1, 6, 6, 6, 6},
        {6, 2, 6, 6, 6},
        {6, 6, 3, 6, 6},
        {6, 6, 6, 4, 6},
        {6, 6, 6, 6, 5}
    };

    private int obtenerColumna(char letra){
        switch(letra){
            case 'j': return 0;
            case 'u': return 1;
            case 'e': return 2;
            case 'g': return 3;
            case 'o': return 4;
            default: return -1;
        }//switch
    }//obtenerColumna

    public boolean esPalabra(String palabra){
        String subcadena = palabra.trim();
        int estado = 0;
        for(int i = 0; i<subcadena.length(); i++){
            char letra = subcadena.charAt(i);

            int columna = obtenerColumna(letra);

            if(columna==-1 || estado>=5){
                return false;
            }

            estado = matrizJuego[estado][columna];

            if(estado== 6){
                return false;
            }
        }
        return estado == 5;
    }//Palabra
}