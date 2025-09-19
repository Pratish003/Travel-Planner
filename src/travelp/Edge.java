
package travelp;
public class Edge {
  int u;
  int v;

  public Edge(int u, int v) {
    this.u = u;
    this.v = v;
  }

  public boolean equals(Object o) {
    return (o instanceof Edge) && u == ((Edge)o).u && v == ((Edge)o).v;
  }
}
