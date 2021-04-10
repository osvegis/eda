/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;

/**
 * Algoritmo de Dijkstra.
 * @param <E> Tipo de los nodos
 */
public class Dijkstra<E>
{
private Map<E,Map<E,Integer>> map = new HashMap<>();

/**
 * Añade un vértice al grafo.
 * @param vertex Vértice.
 */
public void add(E vertex)
{
    if(map.put(vertex, new HashMap<>()) != null)
        throw new RuntimeException("Vértice repetido: "+ vertex);
}

/**
 * Añade una arista ponderada desde un vértice origen a otro destino.
 * @param origin Vértice origen.
 * @param destination Vértice destino.
 * @param weight Peso de la arista.
 */
public void add(E origin, E destination, int weight)
{
    Map<E,Integer> m = map.get(origin);

    if(m == null)
        throw new RuntimeException("No existe el vértice: "+ origin);

    if(!map.containsKey(destination))
        throw new RuntimeException("No existe el vértice: "+ destination);

    if(m.put(destination, weight) != null)
    {
        throw new RuntimeException(
            "Ya existe la arista: "+ origin +" -> "+ destination);
    }
}

/**
 * Añade una arista bidireccional entre dos vértices.
 * @param vertex1 Vértice 1
 * @param vertex2 Vértice 2
 * @param weight Peso de la arista.
 */
public void add2(E vertex1, E vertex2, int weight)
{
    add(vertex1, vertex2, weight);
    add(vertex2, vertex1, weight);
}

/**
 * Busca el camino más corto entre dos vértices.
 * @param origin Vértice origen.
 * @param destination Vértice destino.
 * @return Secuencia de vértices del camino más corto.
 */
public List<E> search(E origin, E destination)
{
    Map<E,Distance<E>> distances = dijkstra(origin);
    ArrayDeque<E> path = new ArrayDeque<>();
    E v = destination;

    while(!v.equals(origin))
    {
        path.addFirst(v);
        v = distances.get(v).parent;
    }

    path.addFirst(origin);
    return new ArrayList<>(path);
}

/**
 * Calcula la distancia de un camino.
 * @param path Secuencia de aristas.
 * @return Distancia del camino.
 */
public int distance(List<E> path)
{
    Iterator<E> it = path.iterator();
    E u = it.next();
    int w = 0;

    while(it.hasNext())
    {
        E v = it.next();
        w += getWeight(u, v);
        u = v;
    }

    return w;
}

private int getWeight(E origin, E destination)
{
    Map<E,Integer> m = map.get(origin);

    if(m == null)
        throw new RuntimeException("No existe el vértice: "+ origin);

    Integer w = m.get(destination);

    if(w == null)
    {
        throw new RuntimeException(
            "No existe la arista: "+ origin +" -> "+ destination);
    }

    return w;
}

private Map<E,Distance<E>> dijkstra(E origin)
{
    // Algoritmo implementado a partir del pseudocódigo de:
    // https://es.wikipedia.org/wiki/Algoritmo_de_Dijkstra

    HashSet<E> visited = new HashSet<>();
    PriorityQueue<Distance<E>> queue = new PriorityQueue<>();
    HashMap<E,Distance<E>> distances = new HashMap<>();

    Distance<E> d = new Distance<>(origin, 0);
    distances.put(origin, d);
    queue.add(d);

    while(!queue.isEmpty())
    {
        E u = queue.remove().vertex;
        visited.add(u);

        for(Map.Entry<E,Integer> n : map.get(u).entrySet())
        {
            E v = n.getKey();

            if(visited.contains(v))
                continue; //......................................CONTINUE

            Distance<E> du = distances.get(u),
                        dv = distances.get(v);

            int duv = du.distance + n.getValue();

            if(dv == null || duv < dv.distance)
            {
                if(dv == null)
                    distances.put(v, dv = new Distance<>(v, duv));
                else
                    dv.distance = duv;

                dv.parent = u;
                queue.add(dv);
            }
        }
    }

    return distances;
}

private static class Distance<E> implements Comparable<Distance<E>>
{
    private E vertex, parent;
    private int distance;

    private Distance(E vertex, int distance)
    {
        this.vertex   = vertex;
        this.distance = distance;
    }

    @Override public int compareTo(Distance d)
    {
        return Integer.compare(distance, d.distance);
    }
} // Distance

public static void main(String[] args)
{
    // Grafo del ejemplo de wikipedia:
    // https://es.wikipedia.org/wiki/Anexo:Ejemplo_de_Algoritmo_de_Dijkstra

    Dijkstra<Character> g = new Dijkstra<>();

    // Vértices
    g.add('A');
    g.add('B');
    g.add('C');
    g.add('D');
    g.add('E');
    g.add('F');
    g.add('G');
    g.add('Z');

    // Aristas
    g.add2('A', 'B', 16);
    g.add2('A', 'C', 10);
    g.add2('A', 'D', 5);
    g.add2('B', 'C', 2);
    g.add2('B', 'F', 4);
    g.add2('B', 'G', 6);
    g.add2('C', 'D', 4);
    g.add2('C', 'E', 10);
    g.add2('C', 'F', 12);
    g.add2('D', 'E', 15);
    g.add2('E', 'F', 3);
    g.add2('E', 'Z', 5);
    g.add2('F', 'G', 8);
    g.add2('F', 'Z', 16);
    g.add2('G', 'Z', 7);

    List<Character> path = g.search('A', 'Z');
    System.out.println("Camino: "+ path);
    System.out.println(" Coste: "+ g.distance(path));
}

} // Dijkstra
