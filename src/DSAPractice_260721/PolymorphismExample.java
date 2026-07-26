package DSAPractice_260721;

public class PolymorphismExample {
    public static void main(String[] args) {
        PolyAnimal a1 = new PolyDog();
        PolyAnimal a2 = new PolyCat();

        a1.sound();
        a2.sound();
    }
}

class PolyAnimal {
    void sound() {
        System.out.println("An animal makes a sound.");
    }
}

class PolyDog extends PolyAnimal {
    @Override
    void sound() {
        System.out.println("Dog barks.");
    }
}

class PolyCat extends PolyAnimal {
    @Override
    void sound() {
        System.out.println("Cat meows.");
    }
}