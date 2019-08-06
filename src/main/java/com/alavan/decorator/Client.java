package com.alavan.decorator;

import java.io.*;

public class Client {

    public static void main(String[] args) {
        Component component1 = new ConcreteDecorator2(new ConcreteDecorator1(new ConcreteComponent()));
        component1.doSomething();

        System.out.println("================================================");

        Component component2 = new ConcreteDecorator1(new ConcreteComponent());
        component2.doSomething();
    }
}
