package com.alavan.decorator;

/**
 * 装饰角色要继承 Component
 * 同时要持有一个 component 的引用
 */
public class Decorator implements Component {

    private final Component component;

    Decorator(Component component) {
        this.component = component;
    }

    @Override
    public void doSomething() {
        // 交由引用来实现继承的方法
        component.doSomething();
    }
}
