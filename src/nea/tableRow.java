package nea;

// This class stores a single row inside the invoice table
public class tableRow {
    
        // Array which stores each of the 4 cells inside the row
	String[] data = new String[4];

        // Constructor for the tableRow
	public tableRow(String data1, String data2, String data3, String data4) {
		this.data[0] = data1;
		this.data[1] = data2;
		this.data[2] = data3;
		this.data[3] = data4;
	}

        // Setter method
	public void set(int index, String input) {
		this.data[index] = input;
	}

        // Getter method
	public String get(int index) {
		return this.data[index];
	}

        // Method to turn the table row into a string - useful for debugging
	@Override
	public String toString() {
		return this.get(0) + ", " + this.get(1) + ", " + this.get(2) + ", " + this.get(3);

	}

}
