/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;

/**
 * Ejemplos de algoritmos voraces.
 */
public class Voraces
{
private static int[] cambioMonedas(int[] monedas, int valor)
{
    Arrays.sort(monedas);
    int[] cambio = new int[monedas.length];
    int suma = 0;
    int i = monedas.length - 1;

    while(suma != valor)
    {
        while(suma + monedas[i] > valor)
        {
            i--;

            if(i < 0)
                throw new RuntimeException("No hay solución");
        }

        suma += monedas[i];
        cambio[i]++;
    }

    return cambio;
}

private static void printMonedas(
        int[] monedas, int[] cambio, String titulo)
{
    System.out.println("\n"+ titulo);
    System.out.println(Arrays.toString(monedas));

    StringBuilder sb = new StringBuilder();
    int valor = 0;

    for(int i = 0; i < monedas.length; i++)
    {
        int c = cambio[i];

        if(c != 0)
        {
            if(sb.length() > 0)
                sb.append(" + ");

            sb.append(c +"*"+ monedas[i]);
            valor += c * monedas[i];
        }
    }

    sb.append(" = "+ valor);
    System.out.println(sb);
}

private static void cambioMonedas()
{
    int[] monedas = { 1, 5, 10, 25 },
          cambio  = cambioMonedas(monedas, 31);

    printMonedas(monedas, cambio, "Cambio de monedas");
}

private static void cambioMonedasNoCanonico()
{
    int[] monedas = { 1, 3, 4 },
          cambio  = cambioMonedas(monedas, 6);

    printMonedas(monedas, cambio, "Cambio de monedas no canónico");
}

private static double[] mochila(Elemento[] elementos, double pesoObjetivo,
                                boolean fraccionamiento)
{
    Arrays.sort(elementos);
    double resultado[] = new double[elementos.length];
    double peso = 0;

    for(int i = 0; i < elementos.length && peso < pesoObjetivo; i++)
    {
        double pi = elementos[i].peso;

        if(peso + pi <= pesoObjetivo)
        {
            resultado[i] = 1;
            peso += pi;
        }
        else if(fraccionamiento)
        {
            resultado[i] = (pesoObjetivo - peso) / pi;
            peso = pesoObjetivo;
        }
    }

    return resultado;
}

private static void printMochila(Elemento[] elementos,
                                 double[] resultado, String titulo)
{
    System.out.println("\n"+ titulo);
    System.out.println(Arrays.toString(elementos));
    assert elementos.length == resultado.length;
    double beneficio = 0, peso = 0;

    for(int i = 0; i < elementos.length; i++)
    {
        double r = resultado[i];

        if(r > 0)
        {
            double pi = elementos[i].peso,
                   pr = r * pi;

            System.out.println(i +" : "+ r +" * "+ pi +" = "+ pr);
            beneficio += r * elementos[i].valor;
            peso += pr;
        }
    }

    System.out.println("Beneficio: "+ beneficio);
    System.out.println("     Peso: "+ peso);
}

private static void mochilaFraccionamiento()
{
    Elemento[] elementos = {
            new Elemento(20, 10), new Elemento(30, 20),
            new Elemento(66, 30), new Elemento(40, 40),
            new Elemento(60, 50) };

    double[] resultado = mochila(elementos, 100, true);
    printMochila(elementos, resultado, "Mochila con fraccionamiento");
}

private static void mochilaDiscreta()
{
    Elemento[] elementos = {
            new Elemento(50, 5), new Elemento(140, 20),
            new Elemento(60, 10) };

    double[] resultado = mochila(elementos, 30, false);
    printMochila(elementos, resultado, "Mochila discreta");
}

private static class Elemento implements Comparable<Elemento>
{
    private double valor, peso;

    private Elemento(double valor, double peso)
    {
        this.valor = valor;
        this.peso  = peso;
    }

    @Override public int compareTo(Elemento e)
    {
        double beneficio1 = valor / peso,
               beneficio2 = e.valor / e.peso;

        // Compara para ordenar de forma descendente.
        return Double.compare(beneficio2, beneficio1);
    }

    @Override public String toString()
    {
        return "("+ valor +", "+ peso +")";
    }
} // Elemento

public static void main(String[] args)
{
    cambioMonedas();
    cambioMonedasNoCanonico();
    mochilaFraccionamiento();
    mochilaDiscreta();
}

} // Voraces
