package nea;

// This class stores the details for a item row from a receipt table
public class tableRow {

    String[] data = new String[4];

    // Constructor
    public tableRow(String data1, String data2, String data3, String data4) {
        this.data[0] = data1;
        this.data[1] = data2;
        this.data[2] = data3;
        this.data[3] = data4;
    }

    // Setter
    public void set(int index, String input) {
        this.data[index] = input;
    }

    // Getter
    public String get(int index) {
        return this.data[index];
    }

    // Turn the table row into a string
    // Used for debugging
    @Override
    public String toString() {
        return this.get(0) + ", " + this.get(1) + ", " + this.get(2) + ", " + this.get(3);
    }
}
