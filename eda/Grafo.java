/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;

/**
 * Ejemplo de grafo dirigido implementado con tablas hash.
 * @param <E> Tipo de los nodos.
 */
public class Grafo<E>
{
private Map<E,Set<E>> map = new HashMap<>();

/**
 * Añade un nodo al grafo.
 * @param e Nodo.
 */
public void add(E e)
{
    if(map.put(e, new LinkedHashSet<>()) != null)
        throw new IllegalArgumentException("Nodo repetido: "+ e);
}

/**
 * Añade una arista desde 'a' hasta 'b'.
 * @param a Nodo origen.
 * @param b Nodo destino.
 */
public void add(E a, E b)
{
    Set<E> s = map.get(a);
    
    if(s == null)
        throw new IllegalArgumentException("No existe el nodo: "+ a);
    
    if(!map.containsKey(b))
        throw new IllegalArgumentException("No existe el nodo: "+ b);

    if(!s.add(b))
    {
        throw new IllegalArgumentException(
                "Ya existe la arista: "+ a +" -> "+ b);
    }
}

/**
 * Recorre en profundidad el grafo.
 * @param e Nodo origen del recorrido.
 */
public void recorridoProfundidad(E e)
{
    Set<E> visitados = new LinkedHashSet<>();
    recorridoProfundidad(e, visitados);

    // El siguiente bucle hace falta si se quiere
    // recorrer completamente un grafo no conexo.
    for(E x: map.keySet())
        recorridoProfundidad(x, visitados);

    System.out.print("Profundidad: ");
    
    for(E v : visitados)
        System.out.print(" "+ v); 
    
    System.out.println();
}

private void recorridoProfundidad(E e, Set<E> visitados)
{
    if(visitados.add(e))
    {
        for(E b : map.get(e))
            recorridoProfundidad(b, visitados);
    }
}

/**
 * Recorre en anchura el grafo.
 * @param a Nodo origen del recorrido.
 */
public void recorridoAnchura(E a)
{
    Set<E> visitados = new LinkedHashSet<>();
    Deque<E> cola = new ArrayDeque<>();
    cola.add(a);
    recorridoAnchura(visitados, cola);
    
    // El siguiente bucle hace falta si se quiere
    // recorrer completamente un grafo no conexo.
    for(E x: map.keySet())
    {
        cola.add(x);
        recorridoAnchura(visitados, cola);
    }

    System.out.print("    Anchura: ");

    for(E e : visitados)
        System.out.print(" "+ e); 

    System.out.println();
}

private void recorridoAnchura(Set<E> visitados, Deque<E> cola)
{
    while(!cola.isEmpty())
    {
        E e = cola.poll();

        if(visitados.add(e))
        {        
            for(E b : map.get(e))
                cola.add(b);
        }
    }
}

/**
 * Comprueba si el grafo tiene algún ciclo realizando
 * un recorrido en profundidad.
 * @param e Nodo origen del recorrido.
 * @return {@code true} si el grafo tiene algún ciclo.
 */
public boolean hayCiclos(E e)
{
    Set<E> s = map.get(e);
    
    if(s == null)
        throw new IllegalArgumentException("No existe el nodo: "+ e);

    return hayCiclos(e, new HashSet<>());
}

private boolean hayCiclos(E e, Set<E> set)
{
    // 'set' contiene los nodos visitados hasta llegar a 'e'.

    if(!set.add(e))
        return true; //.............................................RETURN

    for(E x : map.get(e))
    {
        if(hayCiclos(x, set))
            return true; //.........................................RETURN
    }

    // En el "ascenso" de la recursión estamos retrocediento,
    // por tanto, acortamos el camino quitando el nodo 'e'.
    set.remove(e);

    return false;
}

public static void main(String[] args)
{
    Grafo<Integer> g = new Grafo<>();

    for(int v = 1; v <= 7; v++)
        g.add(v);

    g.add(1, 4);
    g.add(1, 2);
    g.add(2, 5);
    g.add(3, 5);
    g.add(4, 6);
    g.add(4, 5);
    g.add(4, 3);
    g.add(4, 2);
    g.add(5, 7);
    g.add(6, 7);
    g.add(6, 3);

    // Esta arista provocaría un ciclo:
    //g.add(7, 4);

    g.recorridoProfundidad(1);
    g.recorridoAnchura(1);
    System.out.println(" Hay ciclos:  "+ g.hayCiclos(1));
}

} // Grafo
