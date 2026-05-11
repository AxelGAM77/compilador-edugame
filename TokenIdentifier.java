public class TokenIdentifier{
    // Estados: 0=inicio, 1=j, 2=ju, 3=jue, 4=jueg, 5=juego, 6=error
    // Columnas: j=0, u=1, e=2, g=3, o=4 (resto son error)
    private static int matriz[][] = {
        {1, 6, 6, 6, 6},    // Estado 0: espera 'j'
        {6, 2, 6, 6, 6},    // Estado 1: espera 'u'
        {6, 6, 3, 6, 6},    // Estado 2: espera 'e'
        {6, 6, 6, 4, 6},    // Estado 3: espera 'g'
        {6, 6, 6, 6, 5}     // Estado 4: espera 'o'
    };
    
    private static int obtenerColumna(char letra){
        switch(letra){
            case 'j': return 0;
            case 'u': return 1;
            case 'e': return 2;
            case 'g': return 3;
            case 'o': return 4;
            default: return -1;
        }
    }

    public static boolean esValida(String cadena){
        String subcadena = cadena.trim();
        int estado = 0;
        
        for(int i = 0; i < subcadena.length(); i++){
            char letra = subcadena.charAt(i);
            int col = obtenerColumna(letra);
            
            if(col == -1 || estado >= 5){
                return false; // Carácter inválido o ya completó la palabra
            }
            
            estado = matriz[estado][col];
            
            if(estado == 6){ // Estado de error
                return false;
            }
        }
        
        return estado == 5; // Solo válido si terminó en estado 5 (juego completo)
    }
}//class//class