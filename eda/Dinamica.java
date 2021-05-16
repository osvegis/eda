/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;
import java.math.*;

/**
 * Ejemplos de algoritmos de programación dinámica.
 */
public class Dinamica
{
// Complejidad O(2^n)
private static long fibR(int n)
{
    return n < 2 ? 1 : fibR(n-1) + fibR(n-2);
}

// Complejidad O(n)
// Enfoque de abajo hacia arriba (Bottom-Up).
private static long fibBU(int n)
{
    long[] table = new long[n+2];
    table[0] = table[1] = 1;
    
    for(int i = 2; i <= n; i++)
        table[i] = table[i-1] + table[i-2];
    
    return table[n];
}

// Complejidad O(n)
// Enfoque de arriba hacia abajo (Top-Down).
private static long fibTD(int n)
{
    return fibTD(new long[n+1], n);
}

private static long fibTD(long[] table, int n)
{
    if(n <= 1)
        return 1;

    if(table[n] == 0)
        table[n] = fibTD(table, n-1) + fibTD(table, n-2);

    return table[n];
}

// Complejidad O(1) si los valores ya han sido calculados.
private static class Fibonacci
{
    // Usamos BigInteger porque fib(92) > Long.MAX_VALUE
    ArrayList<BigInteger> table = new ArrayList<>();
    
    private Fibonacci()
    {
        table.add(BigInteger.ONE);
        table.add(BigInteger.ONE);
    }

    // Enfoque de abajo hacia arriba (Bottom-Up).
    private BigInteger get(int n)
    {
        for(int i = table.size(); i <= n; i++)
            table.add(table.get(i-1).add(table.get(i-2)));

        return table.get(n);
    }
} // Fibonacci

private static void mainFibonacci()
{
    for(int i = 0; i < 90; i++)
        System.out.println(i +" : "+ fibR(i));

    /*
    Fibonacci fib = new Fibonacci();

    for(int i = 0; i < 1000; i++)
        System.out.println(i +" : "+ fib.get(i));
    */
}

private static int[] cambioMonedas(int[] d, int W)
{
    Arrays.sort(d); // Las monedas deben estar ordenadas.
    System.out.println("\nMonedas: "+ Arrays.toString(d));
    int[][] C = new int[d.length][W + 1];

    for(int i = 0; i < d.length; i++)
    {
        for(int j = 1; j <= W; j++)
        {
            if(i == 0 && j < d[i])
                C[i][j] = Integer.MAX_VALUE;
            else if(i == 0)
                C[i][j] = 1 + C[i][j - d[i]];
            else if(j < d[i])
                C[i][j] = C[i-1][j];
            else
                C[i][j] = Math.min(C[i-1][j], 1 + C[i][j - d[i]]);
        }
    }

    // Algoritmo voraz para calcular las monedas del cambio.
    int[] x = new int[d.length];
    
    for(int i = d.length - 1, j = W; i >= 0 && j > 0; i--)
    {
        if(i == 0 && C[i][j] != 0 ||
            i > 0 && C[i][j] != C[i-1][j])
        {
            if(j - d[i] < 0)
                throw new RuntimeException("No hay moneda unidad.");

            if(C[i][j] == 1 + C[i][j - d[i]])
            {
                if(C[i][j] == Integer.MAX_VALUE)
                    throw new RuntimeException("No hay moneda unidad.");

                while(j - d[i] >= 0)
                {
                    x[i]++;
                    j -= d[i];
                }
            }
        }
    }
    
    return x;
}

private static void printCambioMonedas(int[] d, int[] x)
{
    assert d.length == x.length;
    StringBuilder sb = new StringBuilder();
    sb.append(" Cambio: ");
    int W = 0;
    
    for(int i = 0; i < x.length; i++)
    {
        if(x[i] != 0)
        {
            sb.append(d[i] +"*"+ x[i] +" + ");
            W += d[i] * x[i];
        }
    }
    
    sb.setLength(sb.length() - 3);
    sb.append(" = "+ W);
    System.out.println(sb);
}

private static void cambioMonedas()
{
    int[]d = { 1, 3, 4 };
    int[]x = cambioMonedas(new int[]{ 1, 3, 4 }, 6);
    printCambioMonedas(d, x);

    d = new int[]{ 1, 4, 6 };
    x = cambioMonedas(d, 8);
    printCambioMonedas(d, x);

    d = new int[]{ 1, 7, 10, 11 };
    x = cambioMonedas(d, 14);
    printCambioMonedas(d, x);
}

private static boolean[] mochila(int[] w, int[] v, int W)
{
    System.out.println("\nPeso máximo: "+ W);
    assert w.length == v.length;
    int[][] V = new int[v.length][W + 1];
    
    for(int i = 0; i < v.length; i++)
    {
        for(int j = 1; j <= W; j++)
        {
            if(i == 0)
                V[i][j] = w[0] <= j ? v[0] : 0;
            else if(w[i] > j)
                V[i][j] = V[i-1][j];
            else
                V[i][j] = Math.max(V[i-1][j], V[i-1][j - w[i]] + v[i]);
        }
    }

    // Algoritmo voraz para calcular los elementos.
    boolean[] x = new boolean[v.length];
    int j = W;
    
    for(int i = v.length - 1; i >= 0; i--)
    {
        if(i == 0 && V[i][j] != 0 && V[i][j] == v[i] ||
            i > 0 && V[i][j] != V[i-1][j] &&
                V[i][j] == V[i-1][j - w[i]] + v[i])
        {
            assert !x[i];
            x[i] = true;
            j -= w[i];
        }
    }

    return x;
}

private static void printMochila(int[] w, int[] v, boolean[] x)
{
    assert w.length == v.length && v.length == x.length;
    int W = 0, V = 0;
    System.out.println("Objeto : Peso : Valor");
    
    for(int i = 0; i < x.length; i++)
    {
        System.out.printf("%6d : %4d : %2d", i, w[i], v[i]);

        if(x[i])
        {
            System.out.print(" x 1");
            V += v[i];
            W += w[i];
        }

        System.out.println();
    }
    
    System.out.printf("  Total: %4d : %2d\n", W, V);
}

private static void mochila()
{
    int[] w, v;
    boolean[] x;

    w = new int[]{ 6, 5, 5 };
    v = new int[]{ 8, 6, 5 };
    x = mochila(w, v, 10);
    printMochila(w, v, x);

    w = new int[]{ 5, 6, 5 };
    v = new int[]{ 5, 8, 6 };
    x = mochila(w, v, 10);
    printMochila(w, v, x);
    
    w = new int[]{ 1, 2, 5, 6, 7 };
    v = new int[]{ 1, 6, 18, 22, 28 };
    x = mochila(w, v, 11);
    printMochila(w, v, x);

    w = new int[]{ 4, 3, 5, 2 };
    v = new int[]{ 10, 40, 30, 20 };
    x = mochila(w, v, 9);
    printMochila(w, v, x);
}

public static void main(String[] args)
{
    //mainFibonacci();
    cambioMonedas();
    mochila();
}

} // Dinamica
