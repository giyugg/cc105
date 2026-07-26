package DSAPractice_260721;

public class InheritanceExample {
    public static void main(String[] args) {
        InheritDog dog = new InheritDog();

        dog.sound(); // inherited from 'Animal'
        dog.bark(); // its own method
    }
}

class InheritAnimal {
    void sound() {
        System.out.println("Output on sound from Animal: The animal makes a sound.");
    }
}

class InheritDog extends InheritAnimal {
    void bark() {
        System.out.println("Output on bark from Dog: The dog barks.");
    }
}