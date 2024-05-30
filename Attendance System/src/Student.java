public class Student {
    private int id;
    private String name;
    private boolean present;

    public Student(int id, String name, boolean present) {
        this.id = id;
        this.name = name;
        this.present = present;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
