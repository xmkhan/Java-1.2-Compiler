package algorithm.base;

/**
 * A basic implementation of a Pair object
 */
public class Pair<A, B> {
  private A first;
  private B second;

  public Pair() {
  }

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A getFirst() {
    return first;
  }

  public Pair setFirst(A first) {
    this.first = first;
    return this;
  }

  public B getSecond() {
    return second;
  }

  public Pair setSecond(B second) {
    this.second = second;
    return this;
  }
}
