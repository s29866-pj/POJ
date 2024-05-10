abstract class SchoolResource{
    private int resourceID;
    protected String resourceName;
    public String description;
    public static int totalResources;

    public SchoolResource(int resourceID, String resourceName, String description){
        this.resourceID = resourceID;
        this.resourceName = resourceName;
        this.description = description;
        totalResources = totalResources + 1;
    }

    public String getResourceDetails(){
        return String.format("%d, %d, %s, %s,", resourceID, totalResources, resourceName, description);
    }

    protected abstract void allocateResource();
}

class Employee extends SchoolResource{
    private double salary;
    public String department;

    public Employee(int resourceID, String resourceName, String description, double salary, String department) {
        super(resourceID, resourceName, description);
        this.salary = salary;
        this.department = department;
    }

    @Override
    protected void allocateResource() {}

    public String work(){
        return department + " " + description;
    }

    public String getDetails(){
        return super.getResourceDetails();
    }
}


class Equipment extends SchoolResource{
    protected String location;
    public boolean isFunctional;

    public Equipment(int resourceID, String resourceName, String description, String location, boolean isFunctional) {
        super(resourceID, resourceName, description);
        this.location = location;
        this.isFunctional = isFunctional;
    }

    public void relocate(String newLocation){
        this.location = newLocation;
    }


    @Override
    protected void allocateResource() {}

    public String repair(){
        if (!isFunctional) {
            return "Repaired";
        }
        return "Already Repaired";
    }

    public String getDetails(){
        return super.getResourceDetails() + " " + location + ", " + repair();
    }
}


class TeachingMaterial extends SchoolResource{
    private String subject;
    public boolean isInUse;

    public TeachingMaterial(int resourceID, String resourceName, String description, String subject, boolean isInUse) {
        super(resourceID, resourceName, description);
        this.subject = subject;
        this.isInUse = isInUse;
    }

    public void checkout(){
        if(!isInUse){
            isInUse = true;
        }
    }

    public void checkin(){
        if(isInUse){
            isInUse = false;
        }
    }
    public String getDetails(){
        return super.getResourceDetails() + ", " + subject + ", " + isInUse;
    }

    @Override
    protected void allocateResource() {}
}


public class Main {
    public static void main(String[] args) {
        // Testowanie klasy Employee
        Employee teacher = new Employee(1, "John Doe", "Math Teacher", 3000, "Mathematics");
        teacher.work();
        System.out.println(teacher.getDetails());

        // Testowanie klasy Equipment
        Equipment laptop = new Equipment(3, "Dell Laptop", "Dell Laptop for Teachers", "Room 101", false);
        laptop.relocate("Room 103");
        System.out.println(laptop.getDetails());

        // Testowanie klasy TeachingMaterial
        TeachingMaterial projector = new TeachingMaterial(3, "Projector", "LCD Projector for Presentations", "Mathematics", false);
        projector.checkout();
        System.out.println(projector.getDetails());
        projector.checkout();
        projector.checkin();
    }

}

