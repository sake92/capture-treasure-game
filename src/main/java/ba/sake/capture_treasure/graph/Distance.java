package ba.sake.capture_treasure.graph;

public class Distance {

	private Vertex next;
	private int weight;

	public Distance(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Vertex getNext() {
		return next;
	}

	public void setNext(Vertex next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "Distance [next=" + next + ", weight=" + weight + "]";
	}

}
