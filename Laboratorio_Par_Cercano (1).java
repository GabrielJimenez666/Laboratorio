package laboratorio_par_cercano;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class ComplejidadDeTiempo {

    private static void Crear(String name) // Crea un archivo.
    {
        try {
            // define el nombre del archivo
            String fname = (name);

            File f = new File(fname);

            String msg = "creating file `" + fname + "' ... ";
            // crea el nuevo archivo
            f.createNewFile();

        } catch (IOException err) {

            err.printStackTrace();
        }

        return;
    }

    private static void Escribir(String name, int tm, ArrayList<Integer> is, ArrayList<Integer> comparisons, ArrayList<Integer> runtimes) // escribe al archivo
    {
        try {

            String filename = (name);

            PrintWriter out = new PrintWriter(filename);
            String fmt = ("%10s %10s %10s\n");
            for (int i = 0; i < tm; ++i) {
                out.printf(fmt, is.get(i), comparisons.get(i), runtimes.get(i));
            }

            out.close();
        } catch (FileNotFoundException err) {

            err.printStackTrace();
        }

        return;
    }

    public void Analisis(int ii) { //ii es la cantidad de enteros a alcanzar.
        ArrayList<Integer> runtimes = new ArrayList<Integer>();
        ArrayList<Integer> is = new ArrayList<Integer>();
        ArrayList<Integer> comparisons = new ArrayList<Integer>(); //Inicializamos todas las listas.
        for (int i = 1000; i < ii; i = i * 3 / 2) {
            Laboratorio_Par_Cercano pct = new Laboratorio_Par_Cercano();
            System.out.println(i);
            long total = 0;
            int count;
            ArrayList<Punto> coords = pct.Initialize(i); //enumera las coordenadas y almacena las posiciones x e y ordenadas.
            coords.sort(Comparator.comparing(Point -> Point.x));
            for (int j = 0; j < 256; j++) {
                long startTime = System.nanoTime();
                pct.ParCercano(i, coords, 999999999);
                long endTime = System.nanoTime();
                long totalTime = endTime - startTime; //cálculo del tiempo de ejecución para cada repetición.
                total = total + totalTime;
            }
            count = pct.getComparacion();
            count = count / 256; //encontrar el average de la iteración.
            total = total / 256; //tiempo de ejecución del average.
            runtimes.add((int) total);
            is.add(i); //agrega a la matriz la cantidad total de coordenadas.
            comparisons.add(count);
        }
        Crear("Tiempos.txt");
        Escribir("Tiempos.txt", is.size(), is, comparisons, runtimes);
    }
}

class Punto { //Definición de una clase Punto con posiciones x e y.

    int x;
    int y;
    int pos;

    public Punto(int xx, int yy, int poss) {
        this.x = xx;
        this.y = yy;
        this.pos = poss;
    }
}

public class Laboratorio_Par_Cercano {

    public static int cuenta; //realiza un seguimiento de todo el número de comparaciones.

    public Laboratorio_Par_Cercano() {
        cuenta = 0;
    }

    public int getComparacion() {
        return cuenta;
    }

    public static double[] Fuerza_Bruta(int N, List<Punto> coords, double d_min) { // Encuentra el par más cercano a través del algoritmo de fuerza bruta.
        double dmin = d_min;
        double[] vector = new double[3]; //Matriz para almacenar d_min y el índice de puntos con el par más cercano.
        vector[0] = dmin;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double d = distancia(coords, i, j); //compara la distancia entre cada par.
                if (d < dmin) {
                    cuenta++;
                    dmin = d;
                    vector[0] = d;
                    vector[1] = coords.get(i).pos;
                    vector[2] = coords.get(j).pos;
                } else {
                    cuenta++;
                }
            }
        }
        return vector;
    }

    public static void Printc(List<int[]> coords) { // Muestra un conjunto dado de coordenadas.
        for (int i = 0; i < coords.size(); i++) {
            System.out.println("x: " + coords.get(i)[0] + " y: " + coords.get(i)[1] + " pos: " + coords.get(i)[2]);
        }
    }

    public static ArrayList<Punto> FindCandidates(List<Punto> coords, double min) {
        ArrayList<Punto> cand = new ArrayList<Punto>();
        int i = 0;
        while (i < coords.size() / 2) { //Comparamos la distancia en las posiciones x e y.
            if (Math.abs(coords.get(i).x - coords.get(coords.size() / 2).x) < min && Math.abs(coords.get(i).y - coords.get(coords.size() / 2).y) < min) {
                cuenta++;
                cand.add(coords.get(i)); //se convierten en candidatos cuando la distancia es menor que la distancia mínima.
                i++;
            } else {
                cuenta++;
                i = coords.size() / 2;
            }
        }
        while (i < coords.size()) { //Comparamos los primeros puntos y luego los otros para no repetir los puntos.
            if (Math.abs(coords.get(i).x - coords.get(coords.size() / 2 - 1).x) < min && Math.abs(coords.get(i).y - coords.get(coords.size() / 2 - 1).y) < min) {
                cuenta++;
                cand.add(coords.get(i));
                i++;
            } else {
                cuenta++;
                i = coords.size();
            }
        }
        return cand;
    }

    public static double[] ParCercano(int N, List<Punto> x, double mdis) {
        // Encuentra el par más cercano pero usando recursividad. 
        if (N > 3) { //Si hay más de 3 puntos en una región, la dividimos en 2.
            double[] g1 = new double[3];
            double[] g2 = new double[3];
            int offset = 0;
            cuenta++;
            if (N % 2 == 1) {
                offset = 1;
            }

            g1 = ParCercano(N / 2, x.subList(0, N / 2), mdis);
            g2 = ParCercano(N / 2 + offset, x.subList(N / 2, N), mdis);
            double[] g = new double[3];
            if (g1[0] < g2[0]) {
                g = g1;
                cuenta++;
            } else {
                cuenta++;
                g = g2;
            }
            ArrayList<Punto> candidatos = new ArrayList<Punto>(); //Lista que almacena posibles candidatos.
            candidatos = FindCandidates(x, g[0]);
            //Se aplica el algoritmo de Fuerza Bruta.
            if (candidatos.size() > 1) {
                cuenta++;
                g1 = Fuerza_Bruta(candidatos.size(), candidatos, mdis);
                if (g1[0] < g[0]) {
                    cuenta++;
                    return g1;
                } else {
                    cuenta++;
                    return g;
                }
            } else {
                cuenta++;
                return g;
            }
        } else {
            cuenta++;
            double[] vec = new double[3];
            return Fuerza_Bruta(N, x, mdis); //Aplicar el algoritmo Fuerza_Bruta a 3 o menos coordenadas
        }
    }

    public static double distancia(List<Punto> coords, int i, int j) {
        // calcula la distancia entre los elementos i-ésimo y j-ésimo
        int x1 = coords.get(i).x;
        int x2 = coords.get(j).x;
        int y1 = coords.get(i).y;
        int y2 = coords.get(j).y;
        double d = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return d;
    }

    public static ArrayList<Punto> Initialize(int tam) { //Creación de un conjunto de coordenadas aleatorias.
        ArrayList list = new ArrayList<Punto>();
        Random r = new Random();
        for (int i = 0; i < tam; i++) {
            Punto temp = new Punto(r.nextInt(10000), r.nextInt(10000), i);
            list.add(temp);
        }

        return list;
    }

    public static void main(String[] args) {

        ComplejidadDeTiempo tca = new ComplejidadDeTiempo();
        tca.Analisis(6000000);

    }

}
