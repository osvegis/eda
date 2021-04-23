/*
 * Released under the MIT License.
 * Copyright 2021 Oscar Vega-Gisbert.
 */
package eda;

import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Esta clase implementa un algoritmo para encontrar el par de puntos
 * más cercanos en una nube de puntos usando la técnica de divide
 * y vencerás.
 * 
 * El algoritmo calcula el resultado con complejidad O(n log n).
 * 
 * Bibliografía:
 * 
 * Thomas H. Cormen, Charles E. Leiserson,
 * Ronald L. Rivest, Clifford Stein.
 * INTRODUCTION TO ALGORITHMS (second edition)
 * The MIT Press
 */
public class ClosestPoints
{
private static Result compute(Set<Point> P)
{
    List<Point> X = new ArrayList<>(P),
                Y = new ArrayList<>(P);

    Collections.sort(X, (a,b) -> Integer.compare(a.x, b.x));
    Collections.sort(Y, (a,b) -> Integer.compare(a.y, b.y));
    return compute(X, Y);
}

private static Result compute(List<Point> aX, List<Point> aY)
{
    int size = aX.size();

    if(size <= 3)
    {
        // Con 3 o menos puntos buscamos por "fuerza bruta".
        Result result = null;

        for(int i = 0; i < size; i++)
        {
            Point pi = aX.get(i);

            for(int j = i+1; j < size; j++)
            {
                Point pj = aX.get(j);
                int   d2 = distance2(pi, pj);

                if(result == null || result.d2 > d2)
                    result = new Result(pi, pj, d2);
            }
        }

        return result; //...........................................RETURN
    }

    int middle = size / 2;

    Set<Point> sL = new HashSet<>(), // Puntos a la izquierda.
               sR = new HashSet<>(); // Puntos a la derecha.

    List<Point> xL = new ArrayList<>(), // Ptos. izq. ordenados por 'x'.
                xR = new ArrayList<>(), // Ptos. der. ordenados por 'x'.
                yL = new ArrayList<>(), // Ptos. izq. ordenados por 'y'.
                yR = new ArrayList<>(); // Ptos. der. ordenados por 'y'.

    for(int i = 0; i < middle; i++)
    {
        Point p = aX.get(i);
        sL.add(p);
        xL.add(p);
    }

    for(int i = middle; i < size; i++)
    {
        Point p = aX.get(i);
        sR.add(p);
        xR.add(p);
    }

    for(Point p : aY)
    {
        if(sL.contains(p))
            yL.add(p);
        else if(sR.contains(p))
            yR.add(p);
        else
            throw new AssertionError();
    }

    Result rL = compute(xL, yL),
           rR = compute(xR, yR);

    // Nos quedamos con la mejor solución de las llamadas recursivas.
    Result result = rL.d2 < rR.d2 ? rL : rR;

    // Puede que los dos puntos más cercanos sean uno de los
    // izquierdos y otro de los derechos. Vamos a comprobarlo.

    // Calculamos la coordenada 'x' de la línea vertical
    // que está entre los puntos izquierdos y derechos.
    int m = (xL.get(xL.size()-1).x + xR.get(0).x) / 2;

    // Creamos un array ordenado por coordenada 'y' con los
    // puntos que están a ambos lados de la línea vertical a
    // una distancia de la línea menor al resultado.
    List<Point> yM = new ArrayList<>();

    for(Point p : aY)
    {
        if(distance2(p.x, m) < result.d2)
            yM.add(p);
    }

    int yMSize = yM.size();

    for(int i = 0; i < yMSize; i++)
    {
        Point pi = yM.get(i);
        
        // Buscamos el punto más cercano a 'pi' en un rectángulo de
        // '2*d x d', donde 'd' es la raíz cuadrada de 'result.d2'.
        // En este rectángulo sólo puede haber 8 puntos, por tanto,
        // el siguiente bucle hará 7 iteraciones como mucho.

        for(int j = i + 1; j < yMSize; j++)
        {
            Point pj = yM.get(j);

            if(distance2(pi.y, pj.y) > result.d2)
                break; //............................................BREAK

            int d2 = distance2(pi, pj);

            if(result.d2 > d2)
                result = new Result(pi, pj, d2);
        }
    }

    return result;
}

private static int distance2(Point a, Point b)
{
    int x = a.x - b.x,
        y = a.y - b.y;

    return x * x + y * y; // Distancia euclídea al cuadrado.
}

private static int distance2(int a, int b)
{
    int d = a - b;
    return d * d; // D. al cuadrado entre dos coordenadas del mismo eje.
}

private static class Point
{
    private final int x, y;

    private Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + x;
        hash = 89 * hash + y;
        return hash;
    }

    @Override public boolean equals(Object obj)
    {
        return obj instanceof Point
               && x == ((Point)obj).x
               && y == ((Point)obj).y;
    }

    @Override public String toString()
    {
        return "("+ x +","+ y +")";
    }
}

private static class Result
{
    private final Point a, b;
    private final int d2;

    private Result(Point a, Point b)
    {
        this(a, b, distance2(a, b));
    }

    private Result(Point a, Point b, int d2)
    {
        this.a  = a;
        this.b  = b;
        this.d2 = d2;
    }

    @Override public String toString()
    {
        return a +"-"+ b +": "+ Math.sqrt(d2);
    }
}

private static class Canvas extends JPanel
{
    private Set<Point> points;
    private Result result;

    @Override protected void paintComponent(Graphics g)
    {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        if(points == null)
        {
            g.setFont(g.getFont().deriveFont(30f));
            g.setColor(Color.green);

            g.drawString("Haz click en esta ventana para "+
                         "indicar el número de puntos.", 40, 60);
        }
        else
        {
            if(result != null)
            {
                int x = (result.a.x + result.b.x) / 2,
                    y = (result.a.y + result.b.y) / 2,
                    d = (int)Math.sqrt(result.d2);

                g.setColor(Color.red);
                g.drawLine(0, y, getWidth(), y);
                g.drawLine(x, 0, x, getHeight());

                g.drawOval(x - d/2, y - d/2, d, d);
            }

            g.setColor(Color.white);

            for(Point p : points)
                g.fillOval(p.x - 2, p.y - 2, 4, 4);
        }
    }
}

private static Set<Point> createPoints(int width, int height, int number)
{
    // Evitamos añadir puntos repetidos.
    // Si hay que crear muchos puntos en poco espacio, habrá muchas
    // repeticiones, y por tanto, habrá riesgo de bucle infinito.
    Set<Point> points = new HashSet<>();
    Random r = new Random();

    // Para evitar un bucle infinito iteramos el triple como mucho.
    int count3 = number * 3;

    for(int i = 0; i < count3 && points.size() < number; i++)
    {
        // Creamos los puntos con un margen de 4 píxeles.
        points.add(new Point(4 + r.nextInt(width - 8),
                             4 + r.nextInt(height - 8)));
    }

    return points;
}

private static Integer getNumberOfPoints(Canvas canvas)
{
    int number = canvas.points == null ? 100 : canvas.points.size();

    for(;;)
    {
        String input = JOptionPane.showInputDialog(canvas,
                       "Número de puntos", Integer.toString(number));

        if(input == null)
            return null; //.........................................RETURN

        try
        {
            number = Integer.parseInt(input);

            if(number >= 3)
                return number; //...................................RETURN

            JOptionPane.showMessageDialog(canvas,
                    "Hay que indicar al menos 3 puntos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(canvas,
                    "Hay que introducir el número de puntos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private static void canvasClick(Canvas canvas)
{
    Integer number = getNumberOfPoints(canvas);

    if(number == null)
        return; //..................................................RETURN

    int width  = canvas.getWidth(),
        height = canvas.getHeight();

    Set<Point> points = createPoints(width, height, number);
    Result     result = compute(points);
    System.out.println(result);

    canvas.points = points;
    canvas.result = result;
    canvas.repaint();
}

public static void main(String[] args)
{
    EventQueue.invokeLater(() ->
    {
        Canvas canvas = new Canvas();
        JFrame frame = new JFrame("Par de puntos más cercanos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);

        canvas.addMouseListener(new MouseAdapter()
        {@Override public void mouseClicked(MouseEvent e)
        {
            canvasClick(canvas);
        }});
    });
}

} // ClosestPoints
